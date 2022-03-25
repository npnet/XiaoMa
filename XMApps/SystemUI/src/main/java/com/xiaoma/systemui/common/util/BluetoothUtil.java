package com.xiaoma.systemui.common.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;

import java.util.List;

import static android.bluetooth.BluetoothAdapter.STATE_CONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTED;

public class BluetoothUtil {
    public static int getBluetoothConnectionState(BluetoothManager mgr) {
        BluetoothAdapter adapter = mgr.getAdapter();
        List<Integer> supportedProfiles = adapter.getSupportedProfiles();
        if (supportedProfiles == null || supportedProfiles.isEmpty()) {
            LogUtil.logE("BluetoothUtil", "getBluetoothConnectionState -> No Profile supported !!!");
            return STATE_DISCONNECTED;
        }
        int state = STATE_DISCONNECTED;
        int connectingProfile = -1;
        for (Integer profile : supportedProfiles) {
            if (STATE_CONNECTED != adapter.getProfileConnectionState(profile))
                continue;
            state = STATE_CONNECTED;
            connectingProfile = profile;
            break;
        }
        LogUtil.logI("BluetoothUtil", "getBluetoothConnectionState -> connectingProfile: %s, state: %s",
                connectingProfile, state);
        return state;
    }
}
