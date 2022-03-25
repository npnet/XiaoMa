package com.xiaoma.bluetooth.phone.common.listener;

import android.bluetooth.BluetoothDevice;

/**
 * @author: iSun
 * @date: 2018/11/28 0028
 */
public interface BluetoothStateListener {

    void onBlueToothConnected();

    void onHfpConnected(BluetoothDevice device);

    void onBlueToothDisConnected(BluetoothDevice device);

    void onBlueToothDisabled();

    void onHfpDisConnected(BluetoothDevice device);

    void onPbapConnected();

    void onPbapDisconnected();

    void onA2dpConnected(BluetoothDevice device);

    void onA2dpDisconnected(BluetoothDevice device);
}
