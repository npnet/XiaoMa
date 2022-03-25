package com.xiaoma.bluetooth.phone.common.sdk;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.aidl.model.State;
import com.xiaoma.bluetooth.phone.BlueToothPhone;
import com.xiaoma.bluetooth.phone.common.factory.BlueToothPhoneManagerFactory;
import com.xiaoma.bluetooth.phone.common.listener.PhoneStateChangeListener;
import com.xiaoma.bluetooth.phone.common.listener.PullPhoneBookResultCallback;
import com.xiaoma.bluetooth.phone.common.manager.IBlueToothPhoneManager;
import com.xiaoma.bluetooth.phone.common.manager.PhoneStateManager;
import com.xiaoma.bluetooth.phone.common.model.BluePhoneState;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.remote.Client;
import com.xiaoma.center.logic.remote.ClientCallback;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author ZiXu Huang
 * @Data 2019/2/13
 */
public class BluetoothPhoneClient extends Client implements PhoneStateChangeListener {

    private IBlueToothPhoneManager manager;
    private List<ClientCallback> stateChangeListenerList = new CopyOnWriteArrayList<>();
    private Context context;

    private static BluetoothPhoneClient mInstance;

    private BluetoothPhoneClient() {
        super(BlueToothPhone.getContext(), CenterConstants.BLUETOOTH_PHONE_PORT);
        context = BlueToothPhone.getContext();
        manager = BlueToothPhoneManagerFactory.getInstance();
        PhoneStateManager.getInstance(context).addPhoneListener(this);
    }

    public static BluetoothPhoneClient getInstance() {
        if (mInstance == null) {
            synchronized (BluetoothPhoneClient.class) {
                if (mInstance == null) {
                    mInstance = new BluetoothPhoneClient();
                }
            }
        }
        return mInstance;
    }

    @Override
    protected void onReceive(int action, Bundle data) {
    }

    @Override
    protected void onConnect(int action, Bundle data, ClientCallback callback) {

    }

    @Override
    protected void onRequest(int action, Bundle data, ClientCallback callback) {
        switch (action) {
            case CenterConstants.BluetoothPhoneThirdAction.DIAL:
                dial(data, callback);
                break;
            case CenterConstants.BluetoothPhoneThirdAction.SEND_DTMF:
                sendDTMF(data, callback);
                break;
            case CenterConstants.BluetoothPhoneThirdAction.HOLD_CALL:
                holdCall(data, callback);
                break;
            case CenterConstants.BluetoothPhoneThirdAction.ACCEPT_CALL:
                acceptCall(data, callback);
                break;
            case CenterConstants.BluetoothPhoneThirdAction.REJECT_CALL:
                rejectCall(data, callback);
                break;
            case CenterConstants.BluetoothPhoneThirdAction.TERMINAL_CALL:
                terminalCall(data, callback);
                break;
            case CenterConstants.BluetoothPhoneThirdAction.GET_AUDIO_STATE:
                getAudioState(data, callback);
                break;
            case CenterConstants.BluetoothPhoneThirdAction.CONNECT_AUDIO:
                connectAudio(data, callback);
                break;
            case CenterConstants.BluetoothPhoneThirdAction.DISCONNECT_AUDIO:
                disConnectAudio(data, callback);
                break;
            case CenterConstants.BluetoothPhoneThirdAction.GET_ALL_CONTACT:
                getAllContact(data, callback);
                break;
            case CenterConstants.BluetoothPhoneThirdAction.DIAL_BACK:
                dialBack(data, callback);
                break;
            case CenterConstants.BluetoothPhoneThirdAction.REDIAL:
                redial(data, callback);
                break;
            case CenterConstants.BluetoothPhoneThirdAction.SYNCHRONIZE_CONTACT_BOOK:
                synchronizeContactBook(callback);
                break;
            case CenterConstants.BluetoothPhoneThirdAction.IS_CONTACT_BOOK_SYNCHRONIZED:
                isContactBookSynchronized(callback);
                break;
            case CenterConstants.BluetoothPhoneThirdAction.GET_DIAL_BACK_NUMBER:
                getDialBackNumber(callback);
                break;
            case CenterConstants.BluetoothPhoneThirdAction.GET_REDIAL_NUMBER:
                getRedialNumber(callback);
                break;
            case CenterConstants.BluetoothPhoneThirdAction.REGISTER_PHONE_STATE_CALLBACK:
                registerPhoneStateCallback(callback);
                break;
            case CenterConstants.BluetoothPhoneThirdAction.GET_CALL_HISTORY:
                getCallHistory(data, callback);
                break;
            case CenterConstants.BluetoothPhoneThirdAction.IS_BLUETOOTH_CONNECTED:
                getConnectState(callback);
                break;
            case CenterConstants.BluetoothPhoneThirdAction.CALL_HISTORY:
                registerCallHistory(callback);
                break;
        }
    }

    private void registerCallHistory(ClientCallback callback) {
        addOnPhoneStateChangedListener(callback);
    }

    private void getConnectState(ClientCallback callback) {
        boolean isConnected = manager.isBluetoothConnected();
        createBundleAndCallBack(callback, isConnected, CenterConstants.BluetoothPhoneThirdBundleKey.RESULT);
    }

    private void registerPhoneStateCallback(ClientCallback callback) {
        addOnPhoneStateChangedListener(callback);
    }

    private void getRedialNumber(ClientCallback callback) {
        String result = manager.getRedialNumber();
        createBundleAndCallBack(callback, result, CenterConstants.BluetoothPhoneThirdBundleKey.RESULT);
    }

    private void getDialBackNumber(ClientCallback callback) {
        String result = manager.getDialBackNumber();
        createBundleAndCallBack(callback, result, CenterConstants.BluetoothPhoneThirdBundleKey.RESULT);
    }

    private void isContactBookSynchronized(ClientCallback callback) {
        List<ContactBean> allContact = PhoneStateManager.getInstance(context).getAllContact();
        boolean result = allContact != null && allContact.size() != 0;
        createBundleAndCallBack(callback, result, CenterConstants.BluetoothPhoneThirdBundleKey.RESULT);
    }

    private void synchronizeContactBook(final ClientCallback callback) {
        manager.synchronizeContactBook(new PullPhoneBookResultCallback() {
            @Override
            public void onResult(boolean isSuccess) {
                createBundleAndCallBack(callback, isSuccess, CenterConstants.BluetoothPhoneThirdBundleKey.RESULT);
            }
        });

    }

    private void redial(Bundle data, ClientCallback callback) {
        boolean redial = manager.redial();
        createBundleAndCallBack(callback, redial, CenterConstants.BluetoothPhoneThirdBundleKey.REDIAL_RESULT);
    }

    private void dialBack(Bundle data, ClientCallback callback) {
        boolean b = manager.dialBack();
        createBundleAndCallBack(callback, b, CenterConstants.BluetoothPhoneThirdBundleKey.DIAL_CALL_BACK_RESULT);
    }

    private void getAllContact(Bundle data, ClientCallback callback) {
        List<ContactBean> allContact = manager.getAllContact();
        createBundleAndCallBack(callback, allContact, CenterConstants.BluetoothPhoneThirdBundleKey.GET_CALL_CONTACTS_RESULT);
    }

    private void getCallHistory(Bundle data, ClientCallback callback) {
        List<ContactBean> allHistory = manager.getCallHistory();
        createBundleAndCallBack(callback, allHistory, CenterConstants.BluetoothPhoneThirdBundleKey.GET_CALL_HISTORY);
    }

    private void disConnectAudio(Bundle data, ClientCallback callback) {
        boolean b = manager.disconnectAudio();
        createBundleAndCallBack(callback, b, CenterConstants.BluetoothPhoneThirdBundleKey.DISCONNECT_AUDIO_RESULT);
    }

    private void connectAudio(Bundle data, ClientCallback callback) {
        boolean b = manager.connectAudio();
        createBundleAndCallBack(callback, b, CenterConstants.BluetoothPhoneThirdBundleKey.CONNECT_AUDIO_RESULT);
    }

    private void getAudioState(Bundle data, ClientCallback callback) {
        int audioState = manager.getAudioState();
        createBundleAndCallBack(callback, audioState, CenterConstants.BluetoothPhoneThirdBundleKey.GET_AUDIO_STATE_RESULT);
    }

    private void terminalCall(Bundle data, ClientCallback callback) {
        boolean b = manager.terminateCall();
        createBundleAndCallBack(callback, b, CenterConstants.BluetoothPhoneThirdBundleKey.TERMINAL_CALL_RESULT);

    }

    private void rejectCall(Bundle data, ClientCallback callback) {
        boolean b = manager.rejectCall();
        createBundleAndCallBack(callback, b, CenterConstants.BluetoothPhoneThirdBundleKey.REJECT_CALL_RESULT);
    }

    private void acceptCall(Bundle data, ClientCallback callback) {
        boolean b = manager.acceptCall();
        createBundleAndCallBack(callback, b, CenterConstants.BluetoothPhoneThirdBundleKey.ACCEPT_CALL_RESULT);
    }

    private void holdCall(Bundle data, ClientCallback callback) {
        boolean b = manager.holdCall();
        createBundleAndCallBack(callback, b, CenterConstants.BluetoothPhoneThirdBundleKey.HOLD_CALL_RESULT);
    }

    private void sendDTMF(Bundle data, ClientCallback callback) {
        if (data == null) {
            setNullCallBack(callback);
            return;
        }
        char code = data.getChar(CenterConstants.BluetoothPhoneThirdBundleKey.SEND_DTMF, '0');
        if (code == -1) {
            setNullCallBack(callback);
            return;
        }
        boolean b = manager.sendDTMF(code);
        createBundleAndCallBack(callback, b, CenterConstants.BluetoothPhoneThirdBundleKey.SEND_DTMF_RESULT);

    }

    private void dial(Bundle data, ClientCallback callback) {
        if (data == null) {
            setNullCallBack(callback);
            return;
        }
        String num = data.getString(CenterConstants.BluetoothPhoneThirdBundleKey.DIAL_NUM);
        if (TextUtils.isEmpty(num)) {
            setNullCallBack(callback);
            return;
        }
        boolean dial = manager.dial(num);
        createBundleAndCallBack(callback, dial, CenterConstants.BluetoothPhoneThirdBundleKey.DIAL_NUM_RESULT);
    }

    private void createBundleAndCallBack(ClientCallback callback, boolean b, String bundleKey) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(bundleKey, b);
        callback.setData(bundle);
        callback.callback();
    }

    private void createBundleAndCallBack(ClientCallback callback, int b, String bundleKey) {
        Bundle bundle = new Bundle();
        bundle.putInt(bundleKey, b);
        callback.setData(bundle);
        callback.callback();
    }

    private void createBundleAndCallBack(ClientCallback callback, String b, String bundleKey) {
        Bundle bundle = new Bundle();
        bundle.putString(bundleKey, b);
        callback.setData(bundle);
        callback.callback();
    }

    private void createBundleAndCallBack(ClientCallback callback, List<ContactBean> contacts, String bundleKey) {
        Bundle bundle = new Bundle();
        bundle.putParcelableList(bundleKey, contacts);
        callback.setData(bundle);
        callback.callback();
    }

    private void setNullCallBack(ClientCallback callBack) {
        callBack.setData(null);
        callBack.callback();
    }

    public void addOnPhoneStateChangedListener(ClientCallback callback) {
        if (!stateChangeListenerList.contains(callback)) {
            stateChangeListenerList.add(callback);
        }
    }

    public void notifyStateChanged(List<ContactBean> beanList, int[] states) {
        for (ClientCallback callback : stateChangeListenerList) {
            Bundle bundle = new Bundle();
            bundle.putParcelableList(CenterConstants.BluetoothPhoneThirdBundleKey.CONTACT_BEAN_LIST, beanList);
            bundle.putIntArray(CenterConstants.BluetoothPhoneThirdBundleKey.STATE_ARRAY, states);
            callback.setData(bundle);
            callback.callback();
        }
    }

    public void notifySyncSuccess(List<ContactBean> contacts) {
        for (ClientCallback callback : stateChangeListenerList) {
            Bundle bundle = new Bundle();
            bundle.putInt(CenterConstants.BluetoothPhoneThirdAction.PHONE_ACTION,
                    CenterConstants.BluetoothPhoneThirdAction.CALL_HISTORY);
            bundle.putParcelableList(CenterConstants.BluetoothPhoneThirdBundleKey.CONTACT_BEAN_LIST, contacts);
            callback.setData(bundle);
            callback.callback();
        }
    }

    @Override
    public void onPhoneStateChange(ContactBean bean, State state) {
        BluePhoneState phoneStates = PhoneStateManager.getInstance(BlueToothPhone.getContext()).getPhoneStates();
        notifyStateChanged(phoneStates.getBeanList(), phoneStates.getStates());
    }

    public interface Callback {
        void onResult(boolean isSuccess);
    }

}
