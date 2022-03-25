package com.xiaoma.bluetooth.phone.common.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.bluetooth.phone.common.factory.BlueToothPhoneManagerFactory;

/**
 * Created by qiuboxiang on 2018/12/11
 */
public class BluetoothPhoneService extends Service {

    private final String TAG = "BluetoothPhoneService";

    public BluetoothPhoneService() {
        KLog.i(TAG, "BluetoothPhoneService()");
        initBlueToothPhoneManager();
    }

    private void initBlueToothPhoneManager() {
        BlueToothPhoneManagerFactory.getInstance();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        KLog.i(TAG, "BluetoothPhoneService onCreate()");
    }

    @Override
    public IBinder onBind(Intent intent) {
        KLog.d(TAG, "BluetoothPhoneService onBind");
        return new BluetoothPhoneBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        KLog.d(TAG, "BluetoothPhoneService onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        KLog.d(TAG, "BluetoothPhoneService onDestroy");
        super.onDestroy();
    }

}