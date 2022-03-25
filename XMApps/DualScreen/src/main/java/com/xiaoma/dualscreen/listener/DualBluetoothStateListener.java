package com.xiaoma.dualscreen.listener;

/**
 * @author: iSun
 * @date: 2018/11/28 0028
 */
public interface DualBluetoothStateListener {

    void onBlueToothConnected();

    void onHfpConnected();

    void onBlueToothDisConnected();

    void onBlueToothDisabled();

    void onHfpDisConnected();

    void onPbapConnected();

    void onPbapDisconnected();
}
