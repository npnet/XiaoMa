package com.xiaoma.setting.common.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;

import com.xiaoma.setting.Setting;

/**
 * @Author ZiXu Huang
 * @Data 2018/10/12
 */
public class BluetoothAdapterUtils {
    public static BluetoothAdapter getBluetoothAdapter() {
        BluetoothAdapter adapter;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            adapter = BluetoothAdapter.getDefaultAdapter();
        } else {
            final BluetoothManager bluetoothManager = (BluetoothManager) Setting.getContext().getSystemService(Context.BLUETOOTH_SERVICE);
            adapter = bluetoothManager.getAdapter();
        }
        return adapter;
    }
}
