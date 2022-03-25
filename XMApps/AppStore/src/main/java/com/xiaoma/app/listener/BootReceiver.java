package com.xiaoma.app.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xiaoma.utils.log.KLog;

/**
 * Created by LKF on 2018/11/1 0001.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            KLog.d("Boot completed !!!");
        }
    }
}