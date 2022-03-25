package com.xiaoma.music.model;

import android.bluetooth.BluetoothDevice;

import com.xiaoma.adapter.base.XMBean;

/**
 * Created by ZYao.
 * Date ：2018/10/23 0023
 */
public class XMBluetoothDevice extends XMBean<BluetoothDevice> {
    public XMBluetoothDevice(BluetoothDevice bluetoothDevice) {
        super(bluetoothDevice);
    }
}
