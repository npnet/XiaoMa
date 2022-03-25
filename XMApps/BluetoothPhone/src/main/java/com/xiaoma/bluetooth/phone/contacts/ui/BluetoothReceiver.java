package com.xiaoma.bluetooth.phone.contacts.ui;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @Author ZiXu Huang
 * @Data 2018/12/10
 */
public class BluetoothReceiver extends BroadcastReceiver {

    private Callback callback;

    public BluetoothReceiver(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                callback.onBluetoothConnected(device);
            }
        }
    }

    public interface Callback {
        void onBluetoothConnected(BluetoothDevice device);
    }
}
