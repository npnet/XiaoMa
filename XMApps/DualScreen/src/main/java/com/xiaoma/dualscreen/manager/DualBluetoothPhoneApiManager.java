package com.xiaoma.dualscreen.manager;

import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;

import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.aidl.model.State;
import com.xiaoma.center.IClientCallback;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.model.Response;
import com.xiaoma.dualscreen.listener.ICallHistorySyncListener;
import com.xiaoma.process.listener.IPhoneStateChangeListener;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.List;

/**
 * 蓝牙电话接口
 * Created by Lai on 2019/5/16 17:33
 * Desc:
 */
public class DualBluetoothPhoneApiManager extends DualApiManager {

    private List<IPhoneStateChangeListener> phoneStateChangeListeners = new ArrayList<>();
    private List<ICallHistorySyncListener> callHistorySyncListeners = new ArrayList<>();

    @Override
    public void initRemote() {
        super.initRemote(CenterConstants.BLUETOOTH_PHONE, CenterConstants.BLUETOOTH_PHONE_PORT);
    }

    private static DualBluetoothPhoneApiManager mInstance;

    private DualBluetoothPhoneApiManager() {
    }

    public static DualBluetoothPhoneApiManager getInstance() {
        if (mInstance == null) {
            synchronized (DualBluetoothPhoneApiManager.class) {
                if (mInstance == null) {
                    mInstance = new DualBluetoothPhoneApiManager();
                }
            }
        }
        return mInstance;
    }

    @Override
    public void init() {
        registerPhoneStateCallback();
    }

    //拨打电话
    public void dial(String phoneNum) {
        Bundle bundle = new Bundle();
        bundle.putString(CenterConstants.BluetoothPhoneThirdBundleKey.DIAL_NUM, phoneNum);
        request(CenterConstants.BluetoothPhoneThirdAction.DIAL, bundle, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.BluetoothPhoneThirdBundleKey.DIAL_NUM_RESULT);
                KLog.d("acceptCall=" + success);
                if (!success) {

                }
            }
        });
    }

    //接听电话
    public void acceptCall() {
        request(CenterConstants.BluetoothPhoneThirdAction.ACCEPT_CALL, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.BluetoothPhoneThirdBundleKey.ACCEPT_CALL_RESULT);
                KLog.d("acceptCall=" + success);
                if (!success) {

                }
            }
        });
    }

    //拒接电话
    public void rejectCall() {
        request(CenterConstants.BluetoothPhoneThirdAction.REJECT_CALL, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.BluetoothPhoneThirdBundleKey.REJECT_CALL_RESULT);
                KLog.d("rejectCall=" + success);
                if (!success) {

                }
            }
        });
    }

    //挂断电话
    public void terminateCall() {
        request(CenterConstants.BluetoothPhoneThirdAction.TERMINAL_CALL, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.BluetoothPhoneThirdBundleKey.TERMINAL_CALL_RESULT);
                KLog.d("terminateCall=" + success);
                if (!success) {

                }
            }
        });
    }

    //回拨
    public void dialBack() {
        request(CenterConstants.BluetoothPhoneThirdAction.DIAL_BACK, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.BluetoothPhoneThirdBundleKey.DIAL_CALL_BACK_RESULT);
                if (success) {
                    //todo
                }
            }
        });
    }

    //再次拨打
    public void redial() {
        request(CenterConstants.BluetoothPhoneThirdAction.REDIAL, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.BluetoothPhoneThirdBundleKey.REDIAL_RESULT);
                if (success) {
                    //todo
                }
            }
        });
    }

    //第三方通话切换
    public void holdCall() {
        KLog.d("holdCall");
        request(CenterConstants.BluetoothPhoneThirdAction.HOLD_CALL, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                KLog.d("callback");
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.BluetoothPhoneThirdBundleKey.HOLD_CALL_RESULT);
                if (success) {
                    //todo
                }
            }
        });
    }

    public void getDialBackNumber(final onGetStringResultListener listener) {
        request(CenterConstants.BluetoothPhoneThirdAction.GET_DIAL_BACK_NUMBER, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                String number = extra.getString(CenterConstants.BluetoothPhoneThirdBundleKey.RESULT);
                if (!TextUtils.isEmpty(number)) {
                    listener.onSuccess(number);
                } else {
                    listener.onFailed();
                }
            }
        });
    }

    public void getRedialNumber(final onGetStringResultListener listener) {
        request(CenterConstants.BluetoothPhoneThirdAction.GET_REDIAL_NUMBER, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                String number = extra.getString(CenterConstants.BluetoothPhoneThirdBundleKey.RESULT);
                if (!TextUtils.isEmpty(number)) {
                    listener.onSuccess(number);
                } else {
                    listener.onFailed();
                }
            }
        });
    }

    public void getAllContact(IClientCallback callback) {
        request(CenterConstants.BluetoothPhoneThirdAction.GET_ALL_CONTACT, null, callback);
    }

    public void synchronizeContactBook(final OnTrueListener listener) {
        request(CenterConstants.BluetoothPhoneThirdAction.SYNCHRONIZE_CONTACT_BOOK, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.BluetoothPhoneThirdBundleKey.RESULT);
                if (success) {
                    //todo
                } else {
                    //todo
                }
            }
        });
    }

    public void getCallHistory(IClientCallback callback) {
        request(CenterConstants.BluetoothPhoneThirdAction.GET_CALL_HISTORY, null, callback);
    }

    public void isContactBookSynchronized(final OnTrueListener listener) {
        request(CenterConstants.BluetoothPhoneThirdAction.IS_CONTACT_BOOK_SYNCHRONIZED, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean success = extra.getBoolean(CenterConstants.BluetoothPhoneThirdBundleKey.RESULT);
                if (success) {
                    listener.onTrue();
                } else {
                    //交互二
                    //synchronizeContactBook(listener);
                    listener.onFalse();
                }
            }
        });
    }


    public int registerPhoneStateCallback() {
        KLog.d("registerPhoneStateCallback");
        int resullt =  requestWithoutTTS(CenterConstants.BluetoothPhoneThirdAction.REGISTER_PHONE_STATE_CALLBACK, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                KLog.d("registerPhoneStateCallback callback");
                Bundle extra = response.getExtra();
                List<ContactBean> beanList = extra.getParcelableArrayList(CenterConstants.BluetoothPhoneThirdBundleKey.CONTACT_BEAN_LIST);
                int[] states = extra.getIntArray(CenterConstants.BluetoothPhoneThirdBundleKey.STATE_ARRAY);
                State[] stateArray = new State[states.length];
                for (int i = 0; i < states.length; i++) {
                    stateArray[i] = State.getState(states[i]);
                }
                notifyPhoneStateChanged(beanList, stateArray);
            }
        });
        return resullt;
    }

    public int registerPhoneHistoryCallback() {
        KLog.d("registerPhoneStateCallback");
        return request(CenterConstants.BluetoothPhoneThirdAction.CALL_HISTORY, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                KLog.d("registerPhoneStateCallback callback");
                Bundle extra = response.getExtra();
                int action = extra.getInt(CenterConstants.BluetoothPhoneThirdAction.PHONE_ACTION);
                if (action == CenterConstants.BluetoothPhoneThirdAction.CALL_HISTORY){
                    List<ContactBean> beanList = extra.getParcelableArrayList(CenterConstants.BluetoothPhoneThirdBundleKey.CONTACT_BEAN_LIST);
                    for (ICallHistorySyncListener callHistorySyncListener : callHistorySyncListeners) {
                        callHistorySyncListener.onHistorySync(beanList);
                    }
                }
            }
        });
    }

    public void addOnPhoneStateChangeListener(IPhoneStateChangeListener listener) {
        if (!phoneStateChangeListeners.contains(listener)) {
            phoneStateChangeListeners.add(listener);
        }
    }

    public void removeOnPhoneStateChangeListener(IPhoneStateChangeListener listener) {
        if (phoneStateChangeListeners.contains(listener)) {
            phoneStateChangeListeners.remove(listener);
        }
    }

    public void addOnCallHistoryChangeListener(ICallHistorySyncListener listener) {
        if (!callHistorySyncListeners.contains(listener)) {
            callHistorySyncListeners.add(listener);
        }
    }

    public void removeOnCallHistoryChangeListener(ICallHistorySyncListener listener) {
        if (callHistorySyncListeners.contains(listener)) {
            callHistorySyncListeners.remove(listener);
        }
    }


    private void notifyPhoneStateChanged(List<ContactBean> beanList, State[] states) {
        for (IPhoneStateChangeListener listener : phoneStateChangeListeners) {
            listener.onPhoneStateChanged(beanList, states);
        }
    }

    @Override
    String getAppName() {
        return "蓝牙电话";
    }

    public void isBluetoothConnected(final onGetBooleanResultListener listener) {
        KLog.d("isBluetoothConnected");
        request(CenterConstants.BluetoothPhoneThirdAction.IS_BLUETOOTH_CONNECTED, null, new IClientCallback.Stub() {
            @Override
            public void callback(Response response) throws RemoteException {
                Bundle extra = response.getExtra();
                boolean isBluetoothConnected = extra.getBoolean(CenterConstants.BluetoothPhoneThirdBundleKey.RESULT);
                KLog.d("isBluetoothConnected:" + isBluetoothConnected);
                if (isBluetoothConnected) {
                    listener.onTrue();
                }else{
                    listener.onFalse();
                }
            }
        });
    }


}
