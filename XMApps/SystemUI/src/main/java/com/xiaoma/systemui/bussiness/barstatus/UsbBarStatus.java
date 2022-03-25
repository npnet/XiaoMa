package com.xiaoma.systemui.bussiness.barstatus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.RemoteException;

import com.android.internal.statusbar.StatusBarIcon;
import com.xiaoma.systemui.R;
import com.xiaoma.systemui.bussiness.BarUtil;
import com.xiaoma.systemui.common.util.LogUtil;
import com.xiaoma.systemui.topbar.controller.TopBarController;

import java.util.HashMap;

/**
 * Created by LKF on 2019-3-6 0006.
 */
public class UsbBarStatus implements BarStatus {
    public static final String TAG = "UsbBarStatus";

    @Override
    public void startup(final Context context, final int iconLevel) {
        update(context, iconLevel, false);
        final IntentFilter intentFilter = new IntentFilter(UsbManager.ACTION_USB_STATE);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        intentFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LogUtil.logI(TAG, "onReceive( intent: %s )", intent);
                String action = intent.getAction();
                if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
                    update(context, iconLevel, true);
                } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                    update(context, iconLevel, false);
                }

            }
        }, intentFilter);
        UsbManager mgr = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceMap = mgr.getDeviceList();
        update(context, iconLevel, deviceMap != null && !deviceMap.isEmpty());
        LogUtil.logI(TAG, String.format("startup -> devices: %s", deviceMap));
    }

    private void update(Context context, int iconLevel, boolean insert) {
        if (insert) {
            final StatusBarIcon icon = BarUtil.makeIcon(context, R.drawable.status_icon_usb, iconLevel);
            try {
                TopBarController.getInstance().getStatusBar().setIcon(TAG, icon);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            try {
                TopBarController.getInstance().getStatusBar().removeIcon(TAG);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
