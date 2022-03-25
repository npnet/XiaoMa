package com.xiaoma.mqtt.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.xiaoma.utils.log.KLog;

/**
 * Created by Thomas on 2019/6/4 0004
 * xm mqtt push service
 */

public class XmPushService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        KLog.d("XmPushService onCreate");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        KLog.d("XmPushService onBind");
        return XmPushServiceHandle.getInstance().init(this.getApplicationContext());
    }

}
