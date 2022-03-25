package com.xiaoma.systemuilib;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Icon;
import android.util.Log;

import static com.xiaoma.systemuilib.SystemUIConstant.ACTION_STATUS_BAR_REMOVE_ICON;
import static com.xiaoma.systemuilib.SystemUIConstant.ACTION_STATUS_BAR_SET_ICON;
import static com.xiaoma.systemuilib.SystemUIConstant.EXTRA_ICON;
import static com.xiaoma.systemuilib.SystemUIConstant.EXTRA_SLOT;

/**
 * Created by LKF on 2019-3-20 0020.
 */
public class StatusBarControl {
    private static final String TAG = "StatusBarControl";

    public static int getStatusBarHeight(Context context) {
        final Resources res = context.getResources();
        final int fromXM = res.getDimensionPixelSize(R.dimen.status_bar_height);
        final int fromAndroid = getAndroidStatusBarHeight(res);
        Log.i(TAG, String.format("getStatusBarHeight() { fromXM:%s, fromAndroid: %s }", fromXM, fromAndroid));
        return fromXM;
    }

    public static void setIcon(Context context, Icon icon) {
        setIcon(context, null, icon);
    }

    /**
     * @param slot 图标的唯一标识
     */
    public static void setIcon(Context context, String slot, Icon icon) {

        Intent intent = new Intent(ACTION_STATUS_BAR_SET_ICON)
                .setPackage(SystemUIConstant.SYSTEM_UI_PACKAGE_NAME)
                .putExtra(EXTRA_SLOT, makeSlot(context, slot))
                .putExtra(EXTRA_ICON, icon);
        context.sendBroadcast(intent);
    }

    public static void removeIcon(Context context) {
        removeIcon(context, null);
    }

    /**
     * @param slot 图标的唯一标识
     */
    public static void removeIcon(Context context, String slot) {
        context.sendBroadcast(new Intent(ACTION_STATUS_BAR_REMOVE_ICON)
                .setPackage(SystemUIConstant.SYSTEM_UI_PACKAGE_NAME)
                .putExtra(EXTRA_SLOT, makeSlot(context, slot)));
    }

    private static String makeSlot(Context context, String slot) {
        // 不用包名标识,避免多个应用发送同一个Slot,显示重复的Icon
        /*String realSlot = context.getPackageName();
        if (!TextUtils.isEmpty(slot)) {
            realSlot += "_" + slot;
        }
        return realSlot;*/

        return slot;
    }

    private static int getAndroidStatusBarHeight(Resources res) {
        final int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
        return res.getDimensionPixelSize(resourceId);
    }
}
