package com.xiaoma.xting.launcher;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * Created by LKF on 2018/11/1 0001.
 */
public class BootReceiver extends BroadcastReceiver {
    public static final String SERVICE_CLASS = "com.xiaoma.component.base.LauncherAppService";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentService = new Intent();
        intentService.setComponent(new ComponentName(context.getPackageName(), SERVICE_CLASS));
        context.startService(intentService);
    }
}