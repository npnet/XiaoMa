package com.xiaoma.bluetooth.phone.common.manager;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.aidl.model.MacAddressBean;
import com.xiaoma.aidl.model.State;
import com.xiaoma.bluetooth.phone.BlueToothPhone;
import com.xiaoma.bluetooth.phone.R;
import com.xiaoma.bluetooth.phone.common.factory.BlueToothPhoneManagerFactory;
import com.xiaoma.bluetooth.phone.common.listener.PhoneStateChangeListener;
import com.xiaoma.bluetooth.phone.common.listener.PullPhoneBookResultCallback;
import com.xiaoma.bluetooth.phone.common.model.BluePhoneState;
import com.xiaoma.bluetooth.phone.common.utils.RouteUtils;
import com.xiaoma.bluetooth.phone.main.ui.MainActivity;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.tts.EventTtsManager;
import com.xiaoma.vr.tts.WrapperSynthesizerListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 来电状态管理器
 *
 * @author: iSun
 * @date: 2018/11/19 0019
 */
public class PhoneStateManager implements WheelOperatePhoneManager.OnWheelOperatePhoneListener {
    private static final String TAG = PhoneStateManager.class.getSimpleName();
    private static PhoneStateManager instance;
    private final Context mContext;
    private List<PhoneStateChangeListener> listeners = new CopyOnWriteArrayList<>();
    private BluePhoneState phoneStates = new BluePhoneState();
    private boolean isWindowMode = false;
    private List<ContactBean> items;
    private List<ContactBean> operators;//运营商号码
    private List<ContactBean> callHistory;
    private List<OnMacAddressChangedListener> macAddressListeners = new CopyOnWriteArrayList<>();
    private List<OnCallStateListener> onCallStateListener = new CopyOnWriteArrayList<>();
    private boolean isCallState;
    private boolean isActiveState;
    private boolean isPullPhoneSuccess;
    private PullPhoneBookRequest request;
    private PageState pageState;
    private String incomingPhoneNumber;
    private State beforeState;
    public boolean isCallPullActivity = false;

    private PhoneStateManager(Context context) {
        this.mContext = context;

        operators = new ArrayList<>();
        operators.add(new ContactBean(mContext.getString(R.string.china_mobile), mContext.getString(R.string.china_mobile_number)));
        operators.add(new ContactBean(mContext.getString(R.string.china_unicom), mContext.getString(R.string.china_unicom_number)));
        operators.add(new ContactBean(mContext.getString(R.string.china_telecom), mContext.getString(R.string.china_telecom_number)));

        WheelOperatePhoneManager.getInstance().setOnWheelOperatePhoneListener(this);
    }

    public static PhoneStateManager getInstance(Context context) {
        if (instance == null) {
            synchronized (PhoneStateManager.class) {
                if (instance == null) {
                    instance = new PhoneStateManager(context);
                }
            }
        }
        return instance;
    }

    public void resetCallPullActivity() {
        isCallPullActivity = false;
    }

    public void dispatchState(int phoneState, ContactBean bean) {
        KLog.d("Phone State", "phone state:" + phoneState);
        if (bean == null || TextUtils.isEmpty(bean.getPhoneNum())) return;
        ContactBean contactByNum = findContactByNum(bean.getPhoneNum());
        if (contactByNum != null) {
            bean.setName(contactByNum.getName());
            bean.setIcon(contactByNum.getIcon());
        } else {
            bean.setName(mContext.getString(R.string.unknown_contact));
        }
        Log.d(TAG, "dispatchState: " + bean.getPhoneNum());
        State state = State.getState(phoneState);
        if (!phoneStates.updateState(bean, state)) return;
        notifyCallState();
        handleIncomingPhone(bean, state);
        notifyStateChange(bean, state);
        wheelOperateListenerRegisterOrUnRegister(bean.getPhoneNum(), state); // 在此处注册和反注册方控的监听
        showMainActivity();
    }

    public void showMainActivity() {
        boolean condition1 = !MainActivity.isInited || (MainActivity.isPause() && !PhoneStateManager.getInstance(BlueToothPhone.getContext()).isWindowMode());
        boolean condition2 = !IBCallStateManager.getInstance().isBusyState();
        Log.d(TAG,"condition1: " + condition1 +", condition2: " + condition2);
        if (condition1 && condition2) {
            //临时处理第一次Activity没有启动时不显示摘取电话通讯录弹窗
            MainActivity.isFirst = true;
            RouteUtils.showMainActivity(mContext);
            isCallPullActivity = true;
        }
    }

    private void handleIncomingPhone(ContactBean bean, State state) {
        String phoneNum = bean.getPhoneNum();
        if (state == State.INCOMING && !phoneNum.equals(incomingPhoneNumber)) {
            incomingPhoneNumber = phoneNum;
            if (IBCallStateManager.getInstance().isBusyState() ) {
                return;
            }
            String target = bean.getName().equals(mContext.getString(R.string.unknown_contact)) ? "[n1]" + phoneNum + "[n0]" : bean.getName();
            String text = StringUtil.format(mContext.getString(R.string.incoming_phon_by_someone), target);
            EventTtsManager.getInstance().stopSpeaking();
            EventTtsManager.getInstance().startSpeaking(text, new WrapperSynthesizerListener() {
                @Override
                public void onCompleted() {
                    mContext.sendBroadcast(new Intent(CenterConstants.INCOMING_CALL_TTS_COMPLETED));
                    CommonAudioFocusManager.getInstance().requestAudioFocus();
                }

                @Override
                public void onError(int code) {
                    onCompleted();
                }
            });
        } else if (phoneNum.equals(incomingPhoneNumber) && (state == State.ACTIVE || state == State.IDLE)) {
            incomingPhoneNumber = null;
            EventTtsManager.getInstance().stopSpeaking();
        }
    }

    /**
     * @param phoneNum
     * @param state
     * @desc 方控监听的注册和反注册
     */
    private void wheelOperateListenerRegisterOrUnRegister(String phoneNum, State state) {
        switch (state) {
            case INCOMING:
            case ACTIVE:
            case CALL:
                WheelOperatePhoneManager.getInstance().registerCarLibListener(phoneNum);
                break;
            case IDLE:
                WheelOperatePhoneManager.getInstance().unregisterCarLibListener(phoneNum);
                break;
        }
    }

    private ContactBean findContactByNum(String phoneNum) {
        if (TextUtils.isEmpty(phoneNum)) return null;
        if (items == null || items.size() == 0) return null;
        ContactBean bean = searchContact(phoneNum, items);
        if (bean != null) return bean;
        return searchContact(phoneNum, operators);
    }

    private ContactBean searchContact(String phoneNum, List<ContactBean> list) {
        phoneNum = phoneNum.replace("-", "").replace(" ", "");
        for (ContactBean bean : list) {
            String t = bean.getPhoneNum().replace("-", "").replace(" ", "");
            if (TextUtils.equals(t, phoneNum)) {
                return bean;
            }
        }
        return null;
    }


    public BluePhoneState getPhoneStates() {
        return phoneStates;
    }

    public void setPhoneStates(BluePhoneState phoneStates) {
        this.phoneStates = phoneStates;
    }

    public boolean isWindowMode() {
        return isWindowMode;
    }

    //浮窗模式
    public void setWindowMode(boolean isWindow) {
        this.isWindowMode = isWindow;
    }

    public State getBeforeState() {
        return beforeState;
    }

    public void setBeforeState(State beforeState) {
        this.beforeState = beforeState;
    }

    //是否处于通话状态(来电，拨号，通话中，挂断中）
    public boolean isCallState() {
        return phoneStates.isCallState();
    }

    public boolean isActiveState() {
        return phoneStates.isActiveState();
    }

    public boolean isBothCallBusy() {
        return phoneStates.isBothCallBusy();
    }

    public boolean isHangUp() {
        return phoneStates.isHangUp();
    }

    public void setIsHangUp(boolean isHangUp) {
        phoneStates.setIsHangUp(isHangUp);
    }

    public void clearState() {
        phoneStates.clearState();
    }

    public void setContactList(List<ContactBean> items) {
        this.items = items;
    }

    public List<ContactBean> getAllContact() {
        return items;
    }

    public void setMacAddress(String macAddress) {
        Log.d("QBX", "setMacAddress:" + macAddress);
        MacAddressBean macAddressBean = null;
        if (!TextUtils.isEmpty(macAddress)) {
            macAddressBean = BluetoothPhoneDbManager.getInstance().queryMacAddress(macAddress);
            if (macAddressBean == null) {
                macAddressBean = new MacAddressBean(macAddress);
                BluetoothPhoneDbManager.getInstance().saveMacAddress(macAddressBean);
            }
        }
        BluetoothPhoneDbManager.getInstance().setMacAddressBean(macAddressBean);
        notifyMacAddressChanged(macAddress);
    }

    public List<ContactBean> getCallHistory() {
        return callHistory;
    }

    public void setCallHistory(List<ContactBean> callHistory) {
        this.callHistory = callHistory;
    }

    public ContactBean getCurrentActiveBean() {
        return phoneStates.getCurrentActiveBean();
    }

    public void addPhoneListener(PhoneStateChangeListener callListener) {
        if (!listeners.contains(callListener)) {
            listeners.add(callListener);
        }
    }

    public void removePhoneListener(PhoneStateChangeListener callListener) {
        listeners.remove(callListener);
    }

    public void notifyStateChange(ContactBean bean, State state) {
        if (IBCallStateManager.getInstance().isBusyState()) return;
        updatePageState();
        for (PhoneStateChangeListener listener : listeners) {
            listener.onPhoneStateChange(bean == null ? null : (ContactBean) bean.clone(), state);
        }
    }

    private void updatePageState() {
        if (isCallState) {
            switch (phoneStates.getState(0)) {
                case INCOMING:
                case CALL:
                    pageState = PageState.DIALING;//DialingFragment
                    break;
                case ACTIVE:
                case KEEP:
                    pageState = PageState.ACTIVE;//PhoneFragment
                    break;
            }
            switch (phoneStates.getState(1)) {
                case INCOMING:
                    pageState = PageState.DIALING;//DialingFragment
                    break;
                case ACTIVE:
                case KEEP:
                    pageState = PageState.ACTIVE;//PhoneFragment
                    break;
            }
            if (phoneStates.getState(0).equals(State.INCOMING) && phoneStates.getState(1).equals(State.ACTIVE)) {
                pageState = PageState.DIALING;
            }
        } else {
            pageState = PageState.IDLE;
        }
    }

    public void addOnCallStateListener(OnCallStateListener listener) {
        if (!onCallStateListener.contains(listener)) {
            onCallStateListener.add(listener);
        }
    }

    public void removeOnCallStateListener(OnCallStateListener listener) {
        onCallStateListener.remove(listener);
    }

    public void notifyCallState() {
        if (isCallState()) {
            if (!isCallState) {
                isCallState = true;
                ContactBean activeBean = phoneStates.getCurrentActiveBean();
                if (activeBean.getBeforeState() != 0) {
                    Log.d("QBX", "onCall: ");
                    for (OnCallStateListener listener : onCallStateListener) {
                        try {
                            listener.onCall();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            if (isActiveState() && !isActiveState) {
                Log.d("QBX", "onActive: ");
                isActiveState = true;
                for (OnCallStateListener listener : onCallStateListener) {
                    try {
                        listener.onActive();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            if (isCallState) {
                Log.d("QBX", "onIdle: ");
                isCallState = false;
                isActiveState = false;
                for (OnCallStateListener listener : onCallStateListener) {
                    try {
                        listener.onIdle();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void addOnMacAddressChangedListener(OnMacAddressChangedListener listener) {
        if (!macAddressListeners.contains(listener)) {
            macAddressListeners.add(listener);
            listener.onMacAddressChanged(BluetoothPhoneDbManager.getInstance().getMacAddress());
        }
    }

    public void removeOnMacAddressChangedListener(OnMacAddressChangedListener listener) {
        macAddressListeners.remove(listener);
    }

    private void notifyMacAddressChanged(String macAddress) {
        for (OnMacAddressChangedListener listener : macAddressListeners) {
            listener.onMacAddressChanged(macAddress);
        }
    }

    public boolean isKeep() {
        return phoneStates.isKeep();
    }

    public void setPullPhoneBookState(boolean isSuccess) {
        isPullPhoneSuccess = isSuccess;
    }

    public boolean isPullPhoneBookSuccess() {
        return isPullPhoneSuccess;
    }

    public void pullPhoneBook(PullPhoneBookResultCallback callback) {
        if (request != null) {
            request.pullPhoneBook(callback);
        }
    }

    public void setPullPhoneBookRequest(PullPhoneBookRequest request) {
        this.request = request;
    }

    @Override
    public void answerPhone(String phoneNum) {
        /*State firstTargetState = PhoneStateManager.getInstance(mContext).getPhoneStates().getState(0);
        if (firstTargetState == State.INCOMING) {
            answer();
        } else {
            keepAndListen();
        }*/
    }

    @Override
    public void hangupPhone(String phoneNum) {
        /*BluePhoneState states = PhoneStateManager.getInstance(mContext).getPhoneStates();
        State firstTargetState = states.getState(0);
        State secondTargetState = states.getState(1);
        List<ContactBean> beanList = PhoneStateManager.getInstance(mContext).getPhoneStates().getBeanList();
        if (secondTargetState != State.IDLE) {  //第三方来电
            if (TextUtils.equals(phoneNum, beanList.get(1).getPhoneNum())) {
                OperateUtils.sendBlueToothPhoneBroadcast(State.IDLE.getValue(), beanList.get(1));
                if (secondTargetState == State.ACTIVE) {
                    BlueToothPhoneManagerFactory.getInstance().terminateCall();
                } else if (secondTargetState == State.INCOMING) {
                    BlueToothPhoneManagerFactory.getInstance().rejectCall();
                }
            }

        } else {
            if (TextUtils.equals(phoneNum, beanList.get(0).getPhoneNum())) {
                OperateUtils.sendBlueToothPhoneBroadcast(State.IDLE.getValue(), beanList.get(0));
                switch (firstTargetState) {
                    case CALL:
                    case ACTIVE:
                        BlueToothPhoneManagerFactory.getInstance().terminateCall();
                        break;

                    case INCOMING:
                        BlueToothPhoneManagerFactory.getInstance().rejectCall();
                        break;
                }
            }
        }*/
    }

    private void answer() {
        BlueToothPhoneManagerFactory.getInstance().acceptCall();
    }

    private void keepAndListen() {
        BlueToothPhoneManagerFactory.getInstance().acceptCall();
    }

    public interface OnMacAddressChangedListener {
        void onMacAddressChanged(String macAddress);
    }

    public interface OnCallStateListener {
        void onCall();

        void onIdle();

        void onActive();
    }

    public interface PullPhoneBookRequest {
        void pullPhoneBook(PullPhoneBookResultCallback callback);
    }

    enum PageState {
        IDLE,
        DIALING,
        ACTIVE
    }

    public boolean isDialingPageState() {
        return pageState == PageState.DIALING;
    }

    public boolean isActivePageState() {
        return pageState == PageState.ACTIVE;
    }

}
