package com.xiaoma.bluetooth.phone.common.factory;

import com.xiaoma.bluetooth.phone.common.manager.IBlueToothPhoneManager;
import com.xiaoma.bluetooth.phone.common.sdk.BluetoothPhoneNforeAndOriginHybridSdk;
import com.xiaoma.bluetooth.phone.common.sdk.BluetoothPhoneOriginSdk;

/**
 * Created by qiuboxiang on 2018/12/24 11:47
 */
public class BlueToothPhoneManagerFactory {
    public enum PhoneType{
        CONTACT,
        RECEIVER,
        MISS,
        DIAL,
        History,
        Logs
    }

    public static final String TAG = "BlueToothPhoneManagerFactory";

    private static IBlueToothPhoneManager mInstance;

    private BlueToothPhoneManagerFactory() {}

    /*public static IBlueToothPhoneManager getInstance() {
        if (mInstance == null) {
            synchronized (BlueToothPhoneManagerFactory.class) {
                if (mInstance == null) {
                    mInstance = new BlueToothPhoneManager();
                }
            }
        }
        return mInstance;
    }*/

    public static final boolean isUseNforeSdk = true;//是否使用安富蓝牙接口

    public static IBlueToothPhoneManager getInstance() {
        if (isUseNforeSdk) {
            return BluetoothPhoneNforeAndOriginHybridSdk.getInstance();
        } else {
            return BluetoothPhoneOriginSdk.getInstance();
        }
    }
}