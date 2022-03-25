package com.xiaoma.bluetooth.phone.common.service;

import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.xiaoma.aidl.bluetoothphone.IBluetoothPhoneAidlInterface;
import com.xiaoma.aidl.bluetoothphone.IBluetoothPhoneNotifyAidlInterface;
import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.aidl.model.State;
import com.xiaoma.bluetooth.phone.BlueToothPhone;
import com.xiaoma.bluetooth.phone.common.factory.BlueToothPhoneManagerFactory;
import com.xiaoma.bluetooth.phone.common.listener.PhoneStateChangeListener;
import com.xiaoma.bluetooth.phone.common.manager.PhoneStateManager;
import com.xiaoma.bluetooth.phone.common.model.BluePhoneState;
import com.xiaoma.utils.log.KLog;

import java.util.List;

/**
 * Created by qiuboxiang on 2018/12/25 12:20
 */
public class BluetoothPhoneBinder extends IBluetoothPhoneAidlInterface.Stub implements PhoneStateChangeListener {

    private final String TAG = BluetoothPhoneBinder.class.getSimpleName();
    private RemoteCallbackList<IBluetoothPhoneNotifyAidlInterface> mCallbacks = new RemoteCallbackList<>();

    BluetoothPhoneBinder() {
        PhoneStateManager.getInstance(BlueToothPhone.getContext()).addPhoneListener(this);
    }

    @Override
    public boolean dial(String phoneNum) {
        KLog.d(TAG, "dial: " + phoneNum);
        return BlueToothPhoneManagerFactory.getInstance().dial(phoneNum);
    }

    @Override
    public boolean sendDTMF(char code) {
        KLog.d(TAG, "sendDTMF: " + code);
        return BlueToothPhoneManagerFactory.getInstance().sendDTMF(code);
    }

    @Override
    public boolean acceptCall() {
        KLog.d(TAG, "acceptCall");
        return BlueToothPhoneManagerFactory.getInstance().acceptCall();
    }

    @Override
    public boolean rejectCall() {
        KLog.d(TAG, "rejectCall");
        return BlueToothPhoneManagerFactory.getInstance().rejectCall();
    }

    @Override
    public boolean terminateCall() {
        KLog.d(TAG, "terminateCall");
        return BlueToothPhoneManagerFactory.getInstance().terminateCall();
    }

    @Override
    public boolean holdCall() {
        KLog.d(TAG, "holdCall");
        return BlueToothPhoneManagerFactory.getInstance().holdCall();
    }

    @Override
    public boolean connectAudio() {
        KLog.d(TAG, "connectAudio");
        return BlueToothPhoneManagerFactory.getInstance().connectAudio();
    }

    @Override
    public boolean disconnectAudio() {
        KLog.d(TAG, "disconnectAudio");
        return BlueToothPhoneManagerFactory.getInstance().disconnectAudio();
    }

    @Override
    public List<ContactBean> getAllContact() {
        KLog.d(TAG, "getAllContact");
        return BlueToothPhoneManagerFactory.getInstance().getAllContact();
    }

    @Override
    public boolean dialBack() {
        KLog.d(TAG, "dialBack");
        return BlueToothPhoneManagerFactory.getInstance().dialBack();
    }

    @Override
    public boolean redial() {
        KLog.d(TAG, "redial");
        return BlueToothPhoneManagerFactory.getInstance().redial();
    }

    @Override
    public void registerPhoneStateCallback(IBluetoothPhoneNotifyAidlInterface callback) {
        KLog.d(TAG, "registerPhoneStateCallback");
        mCallbacks.register(callback);
    }

    @Override
    public void unregisterPhoneStateCallback(IBluetoothPhoneNotifyAidlInterface callback) {
        KLog.d(TAG, "unregisterPhoneStateCallback");
        mCallbacks.unregister(callback);
    }

    @Override
    public List<ContactBean> getHistoryCall() throws RemoteException {
        return BlueToothPhoneManagerFactory.getInstance().getCallHistory();
    }

    @Override
    public boolean isBluetoothConnected(){
        KLog.d(TAG, "isBluetoothConnected");
        return BlueToothPhoneManagerFactory.getInstance().isBluetoothConnected();
    }

    @Override
    public void onPhoneStateChange(ContactBean bean, State state) {
        notifyStateChanged();
    }

    @Override
    public int missCallNum(){
        return 0;
    }

    private void notifyStateChanged() {
        BluePhoneState phoneStates = PhoneStateManager.getInstance(BlueToothPhone.getContext()).getPhoneStates();
        final int len = mCallbacks.beginBroadcast();
        for (int i = 0; i < len; i++) {
            try {
                mCallbacks.getBroadcastItem(i).onPhoneStateChanged(phoneStates.getBeanList(), phoneStates.getStates());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mCallbacks.finishBroadcast();
    }
}