package com.qiming.fawcard.synthesize.base.system.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.qiming.fawcard.synthesize.base.application.QmApplication;
import com.qiming.fawcard.synthesize.base.constant.QMConstant;
import com.qiming.fawcard.synthesize.base.system.service.DriverService;
import com.xiaoma.utils.log.KLog;

import static com.qiming.fawcard.synthesize.base.constant.QMConstant.ACTION_DRIVE_START;
import static com.qiming.fawcard.synthesize.base.constant.QMConstant.ACTION_DRIVE_STOP;
import static com.qiming.fawcard.synthesize.base.constant.QMConstant.TAG;


public class DriveScoreBroadcastReceiver extends BroadcastReceiver {
//    private static int CURRENT_STATE = 0; // 1.点火 2.熄火

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Intent tempIntent = new Intent(context, DriverService.class);
        if (ACTION_DRIVE_START.equals(action)) {
            if (QmApplication.getEngineStatus() == QMConstant.STATUS_LAUNCHED) return;
            KLog.d(TAG, "onReceive: 收到点火广播");
            //引擎点火时，通知Service进行操作
            tempIntent.putExtra(QMConstant.DRIVER_SERVICE_KEY, QMConstant.DRIVER_SERVICE_START);
            QmApplication.setEngineStatus(QMConstant.STATUS_LAUNCHED);
        } else if (ACTION_DRIVE_STOP.equals(action)) {
            //引擎熄火时，通知Service进行评分弹窗
            if (QmApplication.getEngineStatus() == QMConstant.STATUS_SHUT_DOWN) return;
            KLog.d(TAG, "onReceive:收到熄火广播");
            tempIntent.putExtra(QMConstant.DRIVER_SERVICE_KEY, QMConstant.DRIVER_SERVICE_END);
            QmApplication.setEngineStatus(QMConstant.STATUS_SHUT_DOWN);
        } else {

        }
        context.startService(tempIntent);
    }
}
