package com.mapbar.xiaoma.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.mapbar.xiaoma.constant.NaviConstants;
import com.mapbar.xiaoma.manager.XmMapNaviManager;

/**
 * Created by Thomas on 2019/5/8 0008
 * 桌面导航状态透传出去
 */

public class XmNaviReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }
        if (NaviConstants.XM_NAVI_RECEIVER.equals(intent.getAction())) {
            XmMapNaviManager.getInstance().setNaviForeground(intent.getBooleanExtra(NaviConstants.IS_NAVI_FOREGROUND, false));
        }
    }

    public static void initXmNaviReceiver(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NaviConstants.XM_NAVI_RECEIVER);
        context.registerReceiver(new XmNaviReceiver(), intentFilter);
    }

    public static void notifyNaviForegroundState(Context context, boolean naviForegroundState) {
        Intent intent = new Intent();
        intent.setAction(NaviConstants.XM_NAVI_RECEIVER);
        intent.putExtra(NaviConstants.IS_NAVI_FOREGROUND, naviForegroundState);
        context.sendBroadcast(intent);
    }

}
