package com.xiaoma.setting.sdk.controller;

import android.bluetooth.BluetoothDevice;

import com.xiaoma.setting.bluetooth.btinterface.BluetoothServiceInterface;

/**
 * @Author ZiXu Huang
 * @Data 2019/1/8
 */
public class BluetoothPairController implements BluetoothServiceInterface {
    @Override
    public void cancelPairingUserInput(BluetoothDevice device) {
        device.cancelPairingUserInput();
    }
}
