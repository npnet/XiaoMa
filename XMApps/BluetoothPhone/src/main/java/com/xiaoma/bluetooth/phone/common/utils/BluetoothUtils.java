package com.xiaoma.bluetooth.phone.common.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.AppOpsManagerCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import java.util.Set;

/**
 * Created by qiuboxiang on 2018/12/11
 */
public class BluetoothUtils {

    public static boolean isBluetoothEnabled() {
        BluetoothAdapter adapter = getBluetoothAdapter();
        if (adapter == null) {
            return false;
        }
        return adapter.isEnabled();
    }

    public static boolean isPBAPConnected() {
        BluetoothAdapter adapter = getBluetoothAdapter();
        if (adapter == null) {
            return false;
        }
        int pbapState = adapter.getProfileConnectionState(BluetoothProfile.PBAP_CLIENT);
        Log.i("BluetoothPhoneService", "isPBAPConnected: " + adapter + ", state = " + pbapState);
        return pbapState == BluetoothProfile.STATE_CONNECTED;
    }

    private static BluetoothAdapter getBluetoothAdapter() {
        return BluetoothAdapter.getDefaultAdapter();
    }

    public static boolean hasPermission(Context context, String[] permissions) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        if (context == null || permissions == null || permissions.length == 0) return false;
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(context, permission);
            if (result == PackageManager.PERMISSION_DENIED) {
                return false;
            }
            String op = AppOpsManagerCompat.permissionToOp(permission);
            if (TextUtils.isEmpty(op)) continue;
            result = AppOpsManagerCompat.noteProxyOp(context, op, context.getPackageName());
            if (result != AppOpsManagerCompat.MODE_ALLOWED) return false;
        }
        return true;
    }

    public static void requestAppointPermissions(Activity activity, String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(activity, permissions, requestCode);
        }
    }

    public static boolean isBTConnectDevice() {
        BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
        int a2DPConnectionState = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP_SINK);
        int hfpConnectionState = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.HEADSET_CLIENT);
        return a2DPConnectionState == BluetoothProfile.STATE_CONNECTED && hfpConnectionState == BluetoothProfile.STATE_CONNECTED;
    }



    public static BluetoothDevice getConnectedDevice() {
        if (isBluetoothEnabled()) {
            BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
            Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
            for (BluetoothDevice device : bondedDevices) {
                if (device.isConnected()) {
                    return device;
                }
            }
        }
        return null;
    }

}
