package com.xiaoma.systemuilib;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Created by LKF on 2019-2-28 0028.
 */
public class NavigationBarControl {
    /**
     * 设置导航栏的View
     *
     * @param remoteViews 如果传null,则表示清除当前导航栏的View,并还原默认的导航栏View
     */
    public static void setNavigationBarView(Context context, RemoteViews remoteViews) {
        final Intent intent = new Intent(SystemUIConstant.ACTION_NAVIGATION_BAR_SET_VIEW);
        if (remoteViews != null) {
            intent.putExtra(SystemUIConstant.EXTRA_REMOTE_VIEW, remoteViews);
        }
        context.sendBroadcast(intent);
    }
}
