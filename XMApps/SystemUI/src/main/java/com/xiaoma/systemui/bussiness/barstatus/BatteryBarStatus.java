package com.xiaoma.systemui.bussiness.barstatus;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.DrawableRes;

import com.android.internal.statusbar.StatusBarIcon;
import com.xiaoma.systemui.R;
import com.xiaoma.systemui.bussiness.BarUtil;
import com.xiaoma.systemui.common.util.LogUtil;
import com.xiaoma.systemui.topbar.controller.TopBarController;
import com.xiaoma.systemuilib.SystemUIConstant;

import java.util.List;

import static android.bluetooth.BluetoothAdapter.STATE_CONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTED;

/**
 * Created by LKF on 2019-3-6 0006.
 */
public class BatteryBarStatus implements BarStatus {
    private static final String TAG = "BatteryBarStatus";
    private int mBatteryPercent = -1;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mUpdatePollTask;

    @Override
    public void startup(final Context context, final int iconLevel) {
        if (mUpdatePollTask == null) {
            mUpdatePollTask = new Runnable() {
                @Override
                public void run() {
                    update(context, iconLevel);
                    mHandler.postDelayed(this, 3000);
                }
            };
        }
        mHandler.removeCallbacks(mUpdatePollTask);
        mHandler.post(mUpdatePollTask);

        update(context, iconLevel);
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LogUtil.logI(TAG, "onReceive( intent: %s )", intent);
                if (SystemUIConstant.BT_BATTERY_ACTION.equals(intent.getAction())) {
                    // 获取电量百分比
                    mBatteryPercent = intent.getIntExtra(SystemUIConstant.BT_BATTERY_KEY, -1);
                    update(context, iconLevel);
                }
            }
        }, new IntentFilter(SystemUIConstant.BT_BATTERY_ACTION));
    }

    private void update(Context context, int iconLevel) {
        BluetoothManager mgr = (BluetoothManager) context.getSystemService(Service.BLUETOOTH_SERVICE);
        if (BluetoothAdapter.STATE_CONNECTED != getBluetoothConnectionState(mgr)) {
            LogUtil.logI(TAG, "update( iconLevel: %s ) Bt not connect", iconLevel);
            removeIcon();
            return;
        }
        if (mBatteryPercent < 0) {
            removeIcon();
            return;
        }
        @DrawableRes int iconRes;
        if (mBatteryPercent == 0) {
            iconRes = R.drawable.status_icon_battery_0;
        } else if (mBatteryPercent <= 25) {
            iconRes = R.drawable.status_icon_battery_25;
        } else if (mBatteryPercent <= 50) {
            iconRes = R.drawable.status_icon_battery_50;
        } else if (mBatteryPercent <= 75) {
            iconRes = R.drawable.status_icon_battery_75;
        } else {
            iconRes = R.drawable.status_icon_battery_100;
        }
        try {
            StatusBarIcon icon = BarUtil.makeIcon(context, iconRes, iconLevel);
            TopBarController.getInstance().getStatusBar().setIcon(TAG, icon);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void removeIcon() {
        try {
            TopBarController.getInstance().getStatusBar().removeIcon(TAG);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private int getBluetoothConnectionState(BluetoothManager mgr) {
        BluetoothAdapter adapter = mgr.getAdapter();
        List<Integer> supportedProfiles = adapter.getSupportedProfiles();
        if (supportedProfiles == null || supportedProfiles.isEmpty()) {
            LogUtil.logE(TAG, "getBluetoothConnectionState -> No Profile supported !!!");
            return STATE_DISCONNECTED;
        } else {
            LogUtil.logE(TAG, "getBluetoothConnectionState -> supportedProfiles: %s", supportedProfiles);
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
        /*String deviceName = null;
        if (connectingProfile != -1) {
            Set<BluetoothDevice> devices = adapter.getBondedDevices();
            if (devices != null && !devices.isEmpty()) {
                for (BluetoothDevice device : devices) {
                    if (device.getBatteryLevel() >= 0) {
                        deviceName = device.getName();
                        mBatteryPercent = device.getBatteryLevel();
                        break;
                    }
                }
            }
        }*/
        LogUtil.logI(TAG, "getBluetoothConnectionState -> connectingProfile: %s, state: %s, battery: %s",
                connectingProfile, state, mBatteryPercent);

        return state;
    }
}
