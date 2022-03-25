package com.xiaoma.setting.bluetooth.btinterface;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

/**
 * @Author ZiXu Huang
 * @Data 2019/1/7
 */
public interface BluetoothControllerAgentInterface {
    void initProfile(Context context);

    void onPair(int type, BluetoothDevice device, String pairingKey);

    void canBeDiscovered();

    void unableDiscovered();

    boolean removeBond(BluetoothDevice device);

    void connect(BluetoothDevice device);

    void disconnect(BluetoothDevice device);

    boolean disconnectOtherDevice(BluetoothDevice device);

    void onDestroy();

    boolean filterConnection(BluetoothDevice device);

    void filterConnection(BluetoothDevice device, int profile);

    boolean isBluetoothConnected(BluetoothDevice device);

    boolean isDeviceDisconnected(BluetoothDevice device);

    boolean isFirstProfileConnected(BluetoothDevice device);

    int disconnectAll();

    BluetoothDevice getConnectedDevice();
}
