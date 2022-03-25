package com.xiaoma.launcher.player.utils;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import java.util.List;

import static android.bluetooth.BluetoothAdapter.STATE_CONNECTED;
import static android.bluetooth.BluetoothAdapter.STATE_OFF;
import static android.bluetooth.BluetoothAdapter.STATE_ON;
import static android.bluetooth.BluetoothAdapter.STATE_TURNING_OFF;
import static android.bluetooth.BluetoothAdapter.STATE_TURNING_ON;
import static android.bluetooth.BluetoothProfile.STATE_CONNECTING;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTING;

public class BluetoothUtil {

    public static boolean getBlueToothConnectState(Context context) {
        boolean connect;
        final BluetoothManager mgr = (BluetoothManager) context.getSystemService(Service.BLUETOOTH_SERVICE);
        if (mgr == null)
            return false;
        final int bluetoothState = mgr.getAdapter().getState();
        switch (bluetoothState) {
            case STATE_TURNING_ON:
            case STATE_ON:
                final int connState = getBluetoothConnectionState(mgr);
                switch (connState) {
                    case STATE_CONNECTED:
                        connect = true;
                        break;
                    case STATE_CONNECTING:
                    case STATE_DISCONNECTING:
                    case STATE_DISCONNECTED:
                    default:
                        connect = false;
                        break;
                }
                break;
            case STATE_OFF:
            case STATE_TURNING_OFF:
            default:
                connect = false;
                break;
        }
        return connect;
    }

    private static int getBluetoothConnectionState(BluetoothManager mgr) {
        BluetoothAdapter adapter = mgr.getAdapter();
        List<Integer> supportedProfiles = adapter.getSupportedProfiles();
        if (supportedProfiles == null || supportedProfiles.isEmpty()) {
            return STATE_DISCONNECTED;
        }
        int state = STATE_DISCONNECTED;
        for (Integer profile : supportedProfiles) {
            if (STATE_CONNECTED != adapter.getProfileConnectionState(profile))
                continue;
            state = STATE_CONNECTED;
            break;
        }
        return state;
    }

}
