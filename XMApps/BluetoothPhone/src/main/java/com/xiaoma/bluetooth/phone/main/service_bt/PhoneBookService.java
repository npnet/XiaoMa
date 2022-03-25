package com.xiaoma.bluetooth.phone.main.service_bt;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoma.aidl.model.ContactBean;
import com.xiaoma.bluetooth.phone.common.CommonInterface.PullContactbookCallback;
import com.xiaoma.bluetooth.phone.common.factory.BlueToothPhoneManagerFactory;
import com.xiaoma.bluetooth.phone.common.listener.BluetoothStateListener;
import com.xiaoma.bluetooth.phone.common.listener.PullPhoneBookResultCallback;
import com.xiaoma.bluetooth.phone.common.manager.BluetoothStateManager;
import com.xiaoma.bluetooth.phone.common.manager.PhoneStateManager;
import com.xiaoma.bluetooth.phone.common.utils.BluetoothAdapterUtils;
import com.xiaoma.bluetooth.phone.common.utils.BluetoothUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author: ZiXu Huang
 * @Data: 2019/8/5
 * @Desc:
 */
public class PhoneBookService extends Service implements PullContactbookCallback, BluetoothStateListener, PhoneStateManager.PullPhoneBookRequest {
    private static final String TAG = "PhoneBookService";
    private PullContactbookCallback callback;
    private final List<ContactBean> contactBeans = new CopyOnWriteArrayList<>();
    private final List<ContactBean> callHistoryBeans = new CopyOnWriteArrayList<>();
    BluetoothAdapter bluetoothAdapter = BluetoothAdapterUtils.getBluetoothAdapter();
    private InPullPhoneBookCallback inPullPhoneBookCallback;
    PullPhoneBookResultCallback pullPhoneBookResultCallback;
    private boolean isInPulling = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new PhoneBookServiceBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"PhoneBookService is Created");
        BlueToothPhoneManagerFactory.getInstance().setPullResultCallback(this);
        BluetoothStateManager.getInstance(this).addListener(this);
        pullIfAllow();
        PhoneStateManager.getInstance(this).setPullPhoneBookRequest(this);
    }

    private void pullIfAllow() {
        if (bluetoothAdapter.isEnabled() &&  isBothProfileConnected()) {
            Log.d("PhoneBook","first pull ");
            pullPhoneBookLocal(null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BluetoothStateManager.getInstance(this).removeListener(this);
        BlueToothPhoneManagerFactory.getInstance().onDestroy();
    }

    @Override
    public void onBlueToothConnected() {

    }

    @Override
    public void onHfpConnected(BluetoothDevice device) {
        Log.d(TAG,"phonebook service, onHfpConnected: " + device.getAddress() );
        if (isDeviceConnected(device)) {
            pullPhoneBookLocal(null);
            Log.d("PhoneBook", "onHfpConnected ,start pull phone book");
        }
    }

    @Override
    public void onBlueToothDisConnected(BluetoothDevice device) {

    }

    @Override
    public void onBlueToothDisabled() {

    }

    @Override
    public void onHfpDisConnected(BluetoothDevice device) {
        cleanData();
        isInPulling = false;
    }

    @Override
    public void onPbapConnected() {

    }

    @Override
    public void onPbapDisconnected() {
    }

    @Override
    public void onA2dpConnected(BluetoothDevice device) {
        boolean pull = isBothProfileConnected();
        Log.d(TAG, String.format("onA2dpConnected -> macAddress: %s, pull: %s", device.getAddress(), pull));
//        if (isDeviceConnected(device)) {
//            pullPhoneBookLocal(null);
//            Log.d("PhoneBook", "onA2dpConnected ,start pull phone book");

//        }
    }

    private boolean isBothProfileConnected() {
        int a2dpState = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP_SINK);
        int hfpState = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET_CLIENT);
        return a2dpState == BluetoothProfile.STATE_CONNECTED && hfpState == BluetoothProfile.STATE_CONNECTED;
    }

    @Override
    public void onA2dpDisconnected(BluetoothDevice device) {
        cleanData();
    }

    public void setPullResultCallback(PullContactbookCallback callback) {
        this.callback = callback;
    }

    public void pullPhoneBookLocal(BlueToothPhoneManagerFactory.PhoneType type) {
        if (canPull()) {
            if (type != null) {
                isInPulling = true;
                BlueToothPhoneManagerFactory.getInstance().downloadByType(type);
            } else {
                isInPulling = true;
                BlueToothPhoneManagerFactory.getInstance().downloadAll();
            }
            if (inPullPhoneBookCallback != null) {
                inPullPhoneBookCallback.isInPulling();
            }
        }
    }

    private boolean canPull() {
        if (!BluetoothUtils.isBluetoothEnabled()) return false;
        BluetoothDevice connectedDevice = BluetoothUtils.getConnectedDevice();
        if (connectedDevice == null) return false;
        return true;
    }

    @Override
    public void onPullContactFinished(List<ContactBean> list) {
        isInPulling = false;
        contactBeans.clear();
        contactBeans.addAll(list);
        if (callback != null) {
            callback.onPullContactFinished(list);
        }
        thirdPullCallback(true);
        PhoneStateManager.getInstance(this).setContactList(list);
    }

    private void thirdPullCallback(boolean result) {
        if (pullPhoneBookResultCallback != null) {
            pullPhoneBookResultCallback.onResult(result);
            pullPhoneBookResultCallback = null;
        }
    }

    @Override
    public void onPullMissCallLog(List<ContactBean> list) {
        if (callback != null) {
            callback.onPullMissCallLog(list);
        }
    }

    @Override
    public void onPullReceivedCallLog(List<ContactBean> list) {
        if (callback != null) {
            callback.onPullReceivedCallLog(list);
        }
    }

    @Override
    public void onPullDialedCallLog(List<ContactBean> list) {
        if (callback != null) {
            callback.onPullDialedCallLog(list);
        }
    }

    @Override
    public void onPullCalllog(List<ContactBean> list) {
        if (callback != null) {
            callback.onPullCalllog(list);
        }
    }

    @Override
    public void onPullCallHistory(List<ContactBean> list) {
        isInPulling = false;
        callHistoryBeans.clear();
        callHistoryBeans.addAll(list);
        if (callback != null) {
            callback.onPullCallHistory(list);
        }
        PhoneStateManager.getInstance(this).setCallHistory(list);
    }

    @Override
    public void onPullFailed() {
        isInPulling = false;
        cleanData();
        if (callback != null) {
            callback.onPullFailed();
        }
        thirdPullCallback(false);
    }

    private void cleanData() {
        callHistoryBeans.clear();
        contactBeans.clear();
    }

    @Override
    public void onUnauthoried() {
        isInPulling = false;
        cleanData();
        if (callback != null) {
            callback.onUnauthoried();
        }
        thirdPullCallback(false);
    }

    @Override
    public void onTimeOut() {
        isInPulling = false;
        cleanData();
        if (callback != null) {
            callback.onTimeOut();
        }
        thirdPullCallback(false);
    }

    @Override
    public void unInitialized() {
        isInPulling = false;
        cleanData();
        if (callback != null) {
            callback.unInitialized();
        }
        thirdPullCallback(false);
    }

    public void stopDownload(String macAddress){
        if (!TextUtils.isEmpty(macAddress)) {
            BlueToothPhoneManagerFactory.getInstance().stopDownload(macAddress);
        }
    }

    public boolean isInDownloading(){
        return isInPulling;
    }

    public List<ContactBean> getContactBean(){
        return contactBeans;
    }

    public List<ContactBean> getCallLogs(){
        return callHistoryBeans;
    }

    @Override
    public void pullPhoneBook(PullPhoneBookResultCallback callback) {
        this.pullPhoneBookResultCallback = callback;
        boolean isRequestPullPhoneBook = checkIfPullContact();
        if (isRequestPullPhoneBook) {
           pullPhoneBookLocal(null);
            Log.d("PhoneBook","pullPhoneBook pull");
        }
    }

    private boolean checkIfPullContact() {
        if (!BluetoothUtils.isBluetoothEnabled()) return false;
        BluetoothDevice connectedDevice = BluetoothUtils.getConnectedDevice();
        if (connectedDevice == null) return false;
        return true;
    }

    private boolean isDeviceConnected(BluetoothDevice device){
        return BlueToothPhoneManagerFactory.getInstance().isDeviceConnected(device);
    }

    private boolean isDeviceDisconnected(BluetoothDevice device){
        return BlueToothPhoneManagerFactory.getInstance().isDeviceDisconnected(device);
    }

    public class PhoneBookServiceBinder extends Binder {
        public PhoneBookService getService() {
            return PhoneBookService.this;
        }
    }

    public void setInPullPhoneBookCallback(InPullPhoneBookCallback callback){
        inPullPhoneBookCallback = callback;
    }

    public interface InPullPhoneBookCallback{
        void isInPulling();
    }
}
