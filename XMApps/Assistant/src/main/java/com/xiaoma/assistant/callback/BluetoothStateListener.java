package com.xiaoma.assistant.callback;

/**
 * @author: iSun
 * @date: 2018/11/28 0028
 */
public interface BluetoothStateListener {

    void onBlueToothConnected();

    void onHfpConnected();

    void onBlueToothDisConnected();

    void onBlueToothDisabled();

    void onHfpDisConnected();

    void onPbapConnected();

    void onPbapDisconnected();
}
