package com.xiaoma.setting.bluetooth.btinterface;

import android.bluetooth.BluetoothDevice;

/**
 * @Author ZiXu Huang
 * @Data 2018/12/24
 */
public interface BluetoothStateInterface  {
    void onBtTurnOnSuccess();

    void onBtClose();

    void onBtTurnOnFailed();

    void onBtBonded(BluetoothDevice device);

    void onPairingConfirm(int type, BluetoothDevice device, String pairingKey);

}
