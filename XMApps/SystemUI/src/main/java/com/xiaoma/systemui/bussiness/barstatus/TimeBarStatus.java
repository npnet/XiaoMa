package com.xiaoma.systemui.bussiness.barstatus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.RemoteException;

import com.android.internal.statusbar.StatusBarIcon;
import com.xiaoma.systemui.bussiness.BarUtil;
import com.xiaoma.systemui.common.util.LogUtil;
import com.xiaoma.systemui.topbar.controller.TopBarController;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by LKF on 2019-3-6 0006.
 * 原生的时间获取
 */
public class TimeBarStatus implements BarStatus {
    private static final String TAG = "TimeBarStatus";

    @Override
    public void startup(Context context, final int iconLevel) {
        updateTime(context, iconLevel);
        // 监听时间变化
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                LogUtil.logI(TAG, "onReceive( intent: %s )", intent);
                updateTime(context, iconLevel);
            }
        }, intentFilter);
    }

    private void updateTime(Context context, int iconLevel) {
        /*final String timeFormat;
        final long currTime = System.currentTimeMillis();
        if (DateFormat.is24HourFormat(context)) {
            timeFormat = "HH:mm";
        } else {
            final Calendar c = Calendar.getInstance();
            c.setTimeInMillis(currTime);
            int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
            if (hourOfDay <= 12) {
                timeFormat = context.getString(R.string.status_bar_time_format_morning);
            } else {
                timeFormat = context.getString(R.string.status_bar_time_format_afternoon);
            }
        }
        final String timeText = new SimpleDateFormat(timeFormat, Locale.CHINA).format(currTime);*/

        SimpleDateFormat df = new SimpleDateFormat("HH:mm", Locale.getDefault());
        final String timeText = df.format(System.currentTimeMillis());
        LogUtil.logI(TAG, "updateTime( timeText: %s )", timeText);
        final StatusBarIcon icon = BarUtil.makeIcon(context, timeText, iconLevel);
        try {
            TopBarController.getInstance().getStatusBar().setIcon(TAG, icon);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
