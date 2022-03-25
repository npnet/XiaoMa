package com.xiaoma.setting.bluetooth.model;

import android.bluetooth.BluetoothDevice;

/**
 * @Author ZiXu Huang
 * @Data 2018/10/13
 */
public class BluetoothDeviceStatusModule {
    private BluetoothDevice device;

    private int deviceStatus ;

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public int getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(int deviceStatus) {
        this.deviceStatus = deviceStatus;
    }
}
