package com.xiaoma.systemui.bussiness.barstatus;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.RemoteException;

import com.android.internal.statusbar.IStatusBar;
import com.android.internal.statusbar.StatusBarIcon;
import com.xiaoma.systemui.R;
import com.xiaoma.systemui.bussiness.BarUtil;
import com.xiaoma.systemui.common.util.LogUtil;
import com.xiaoma.systemui.topbar.controller.TopBarController;

import static android.bluetooth.BluetoothAdapter.STATE_CONNECTED;
import static android.bluetooth.BluetoothAdapter.STATE_OFF;
import static android.bluetooth.BluetoothAdapter.STATE_ON;
import static android.bluetooth.BluetoothAdapter.STATE_TURNING_OFF;
import static android.bluetooth.BluetoothAdapter.STATE_TURNING_ON;
import static android.bluetooth.BluetoothProfile.STATE_CONNECTING;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTING;
import static com.xiaoma.systemui.common.util.BluetoothUtil.getBluetoothConnectionState;

/**
 * Created by LKF on 2019-3-6 0006.
 */
public class BluetoothBarStatus implements BarStatus {
    private static final String TAG = "BluetoothBarStatus";

    private IStatusBar mStatusBar;

    public BluetoothBarStatus() {
        mStatusBar = TopBarController.getInstance().getStatusBar();
    }

    @Override
    public void startup(Context context, final int iconLevel) {
        update(context, iconLevel);
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LogUtil.logI(TAG, "onReceive( intent: %s ) [ iconLevel: %s ]", intent, iconLevel);
                update(context, iconLevel);
            }
        }, intentFilter);
    }

    private void update(Context context, int iconLevel) {
        final BluetoothManager mgr = (BluetoothManager) context.getSystemService(Service.BLUETOOTH_SERVICE);
        if (mgr == null)
            return;
        final int bluetoothState = mgr.getAdapter().getState();
        LogUtil.logI(TAG, "update( iconLevel: %s ) [ bluetoothState: %s ]", iconLevel, bluetoothState);
        switch (bluetoothState) {
            case STATE_TURNING_ON:
            case STATE_ON:
                updateWithBluetoothOpen(context, mgr, iconLevel);
                break;
            case STATE_OFF:
            case STATE_TURNING_OFF:
                try {
                    mStatusBar.removeIcon(TAG);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void updateWithBluetoothOpen(Context context, BluetoothManager mgr, int iconLevel) {
        StatusBarIcon icon = null;
        final int connState = getBluetoothConnectionState(mgr);
        LogUtil.logI(TAG, "updateWithBluetoothOpen( iconLevel: %s ) [ connState: %s ]", iconLevel, connState);
        switch (connState) {
            case STATE_CONNECTED:
                icon = BarUtil.makeIcon(context, R.drawable.status_icon_bluetooth_connect, iconLevel);
                break;
            case STATE_CONNECTING:
            case STATE_DISCONNECTING:
            case STATE_DISCONNECTED:
                icon = BarUtil.makeIcon(context, R.drawable.status_icon_bluetooth_disconnect, iconLevel);
                break;
        }
        if (icon != null) {
            try {
                mStatusBar.setIcon(TAG, icon);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

}
