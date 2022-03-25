package com.xiaoma.setting.sdk.controller;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.xiaoma.setting.bluetooth.btinterface.BluetoothControllerAgentInterface;

/**
 * @Author ZiXu Huang
 * @Data 2019/1/7
 */
public class BluetoothController implements BluetoothControllerAgentInterface {
    public BluetoothController(Context context) {
    }

    @Override
    public void initProfile(Context context) {

    }

    @Override
    public void onPair(int type, BluetoothDevice device, String pairingKey) {

    }

    @Override
    public void canBeDiscovered() {

    }

    @Override
    public void unableDiscovered() {

    }

    @Override
    public boolean removeBond(BluetoothDevice device) {
        return false;
    }

    @Override
    public void connect(BluetoothDevice device) {

    }

    @Override
    public void disconnect(BluetoothDevice device) {

    }

    @Override
    public boolean disconnectOtherDevice(BluetoothDevice device) {
        return false;
    }


    @Override
    public void onDestroy() {

    }

    @Override
    public boolean filterConnection(BluetoothDevice device) {
        return false;
    }


    @Override
    public void filterConnection(BluetoothDevice device, int profile) {

    }

    @Override
    public boolean isBluetoothConnected(BluetoothDevice device) {
        return false;
    }

    @Override
    public boolean isDeviceDisconnected(BluetoothDevice device) {
        return false;
    }

    @Override
    public boolean isFirstProfileConnected(BluetoothDevice device) {
        return false;
    }

}
