package com.xiaoma.systemui.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.xiaoma.systemui.common.util.LogUtil.logE;

/**
 * Created by LKF on 2018/11/1 0001.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            logE("Boot completed !!!");
        }
    }
}