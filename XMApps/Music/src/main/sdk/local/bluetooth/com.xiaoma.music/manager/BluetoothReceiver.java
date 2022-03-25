package com.xiaoma.music.manager;

import android.bluetooth.BluetoothA2dpSink;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothHeadsetClient;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xiaoma.utils.log.KLog;

import java.util.Objects;

/**
 * @Author ZiXu Huang
 * @Data 2018/10/29
 */
public class BluetoothReceiver extends BroadcastReceiver {
    private BluetoothStateCallback callback;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            KLog.d("BluetoothReceiver ACTION_ACL_CONNECTED");
            if (callback != null) {
                callback.onBTConnected();
            }
        } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            KLog.d("BluetoothReceiver ACTION_ACL_DISCONNECTED");
            if (callback != null) {
                callback.onBTDisconnected();
            }
        } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
            KLog.d("BluetoothReceiver "+blueState);
            switch (blueState) {
                case BluetoothAdapter.STATE_OFF:
                    KLog.d("BluetoothReceiver ACTION_STATE_CHANGED_STATE_OFF");
                    if (callback != null) {
                        callback.onBTDisconnected();
                    }
                    break;
                case BluetoothAdapter.STATE_ON:
                    KLog.d("BluetoothReceiver ACTION_STATE_CHANGED_STATE_ON");
                    if (callback != null) {
                        callback.onBTConnected();
                    }
                    break;
            }
        } else if (Objects.equals(action, BluetoothA2dpSink.ACTION_CONNECTION_STATE_CHANGED)) {
            int state = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, -1);
            int preState = intent.getIntExtra(BluetoothProfile.EXTRA_PREVIOUS_STATE, -1);
            KLog.d("BluetoothReceiver Bluetooth status change: " + action);
            if (state == BluetoothA2dpSink.STATE_CONNECTED) {
                if (preState == BluetoothA2dpSink.STATE_CONNECTING || preState == BluetoothA2dpSink.STATE_DISCONNECTED) {
                    KLog.d("BluetoothReceiver a2dpSink connected");
                    if (callback != null) {
                        callback.onBTSinkConnected();
                    }
                }
            } else if (state == BluetoothProfile.STATE_DISCONNECTED) {
                if (preState == BluetoothA2dpSink.STATE_CONNECTED || preState == BluetoothA2dpSink.STATE_DISCONNECTING) {
                    KLog.d("BluetoothReceiver a2dpSink disconnected");
                    if (callback != null) {
                        callback.onBTSinkDisconnected();
                    }
                }
            } else {
                KLog.d("BluetoothReceiver a2dp state : " + state);
            }
        }
    }

    public void setBluetoothStateCallback(BluetoothStateCallback callback) {
        this.callback = callback;
    }

    public interface BluetoothStateCallback {
        void onBTSinkDisconnected();

        void onBTSinkConnected();

        void onBTConnected();

        void onBTDisconnected();
    }
}
