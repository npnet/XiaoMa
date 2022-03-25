package com.xiaoma.systemui.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.UserHandle;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.android.internal.statusbar.StatusBarIcon;
import com.xiaoma.systemui.common.util.LogUtil;
import com.xiaoma.systemui.navigationbar.NavigationBarController;
import com.xiaoma.systemui.topbar.controller.TopBarController;
import com.xiaoma.systemuilib.StatusBarSlot;
import com.xiaoma.systemuilib.SystemUIConstant;

import static com.xiaoma.systemuilib.SystemUIConstant.ACTION_NAVIGATION_BAR_SET_VIEW;
import static com.xiaoma.systemuilib.SystemUIConstant.ACTION_STATUS_BAR_REMOVE_ICON;
import static com.xiaoma.systemuilib.SystemUIConstant.ACTION_STATUS_BAR_SET_ICON;
import static com.xiaoma.systemuilib.SystemUIConstant.EXTRA_ICON;
import static com.xiaoma.systemuilib.SystemUIConstant.EXTRA_REMOTE_VIEW;
import static com.xiaoma.systemuilib.SystemUIConstant.EXTRA_SLOT;

/**
 * Created by LKF on 2019-2-28 0028.
 */
public class XMSystemUIReceiver extends BroadcastReceiver {
    private static final String TAG = XMSystemUIReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        LogUtil.logI(TAG, "onReceive( intent: %s ) extras: %s", intent, intent.getExtras());
        if (ACTION_NAVIGATION_BAR_SET_VIEW.equals(action)) {
            // 设置导航栏View
            final RemoteViews remoteViews = intent.getParcelableExtra(EXTRA_REMOTE_VIEW);
            NavigationBarController.getInstance().setRemoteViews(remoteViews);
        } else if (ACTION_STATUS_BAR_SET_ICON.equals(action)) {
            // 设置状态栏图标
            String slot = intent.getStringExtra(EXTRA_SLOT);
            Icon icon = intent.getParcelableExtra(EXTRA_ICON);
            if (!TextUtils.isEmpty(slot) && icon != null) {
                StatusBarIcon sbi = new StatusBarIcon(
                        UserHandle.ALL, context.getPackageName(), icon,
                        getStatusIconLevel(slot), 0, "");
                try {
                    TopBarController.getInstance()
                            .getStatusBar()
                            .setIcon(slot, sbi);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        } else if (ACTION_STATUS_BAR_REMOVE_ICON.equals(action)) {
            // 移除状态栏图标
            String slot = intent.getStringExtra(EXTRA_SLOT);
            if (!TextUtils.isEmpty(slot)) {
                try {
                    TopBarController.getInstance()
                            .getStatusBar()
                            .removeIcon(slot);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        } else if (SystemUIConstant.ACTION_NOTIFICATION_PANEL_EXPAND.equals(action)) {
            try {
                TopBarController.getInstance().getStatusBar().animateExpandNotificationsPanel();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else if (SystemUIConstant.ACTION_NOTIFICATION_PANEL_COLLAPSE.equals(action)) {
            try {
                TopBarController.getInstance().getStatusBar().animateCollapsePanels();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private static int getStatusIconLevel(String slot) {
        // 特殊处理:用户信息在最左边
        if (StatusBarSlot.SLOT_USER_INFO.equals(slot))
            return -3;
        // 特殊处理:Launcher发过来的天气
        if (StatusBarSlot.SLOT_WEATHER.equals(slot))
            return -2;
        return -1;
    }
}
