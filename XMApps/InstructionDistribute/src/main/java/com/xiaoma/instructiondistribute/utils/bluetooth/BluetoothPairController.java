package com.xiaoma.instructiondistribute.utils.bluetooth;

import android.bluetooth.BluetoothDevice;

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
