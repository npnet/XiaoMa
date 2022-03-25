package com.xiaoma.carlib.wheelcontrol;

import android.content.Context;
import android.content.Intent;

/**
 * Created by LKF on 2019-5-6 0006.
 */
public class XmWheelServiceManager {
    /**
     * 开启方控监听服务
     */
    public static void startService(Context context) {
        Context app = context.getApplicationContext();
        app.startService(new Intent(app, WheelControlProxyService.class));
        app.startService(new Intent(app, WheelControlService.class));
    }
}