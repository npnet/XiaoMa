package com.xiaoma.setting.bluetooth.btinterface;

import android.bluetooth.BluetoothDevice;

/**
 * @Author ZiXu Huang
 * @Data 2018/12/24
 */
public interface BluetoothConnectStateInterface {
    void onBtConnected(BluetoothDevice device,boolean isShowDialog);

    void onBtDisconnected(BluetoothDevice device,boolean isShowDialog);

    void onDeleteBondedDevice();

    void onPairingFailed();

    void onHeadSetClientConnected(BluetoothDevice device);
}
