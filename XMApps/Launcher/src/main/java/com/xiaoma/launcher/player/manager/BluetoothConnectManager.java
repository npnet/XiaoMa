package com.xiaoma.launcher.player.manager;

import android.bluetooth.BluetoothA2dpSink;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.log.KLog;

import java.util.List;

/**
 * Created by ZYao.
 * Date ：2019/8/15 0015
 */
public class BluetoothConnectManager {
    private static final String TAG = BluetoothConnectManager.class.getSimpleName();
    private boolean blueConnect = false;
    private BluetoothA2dpSink mA2dpSinkService;

    public static BluetoothConnectManager getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static final BluetoothConnectManager instance = new BluetoothConnectManager();
    }

    public void init(Context context) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.getProfileProxy(context, mA2dpSinkServiceListener, BluetoothProfile.A2DP_SINK);
    }

    public boolean isBlueConnect() {
        if (mA2dpSinkService == null) {
            return false;
        }
        List<BluetoothDevice> devices = mA2dpSinkService.getConnectedDevices();
        return !ListUtils.isEmpty(devices);
    }

    private BluetoothProfile.ServiceListener mA2dpSinkServiceListener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            if (profile == BluetoothProfile.A2DP_SINK) {
                mA2dpSinkService = (BluetoothA2dpSink) proxy;
                KLog.d(TAG, "mA2dpSinkService connected" + mA2dpSinkService);
            }
        }

        @Override
        public void onServiceDisconnected(int profile) {
            if (profile == BluetoothProfile.A2DP_SINK) {
                KLog.d(TAG, "mA2dpSinkService disconnected");
                mA2dpSinkService = null;
            }
        }
    };
}
