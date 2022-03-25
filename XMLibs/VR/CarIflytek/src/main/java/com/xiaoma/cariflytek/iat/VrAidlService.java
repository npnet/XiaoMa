package com.xiaoma.cariflytek.iat;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/8/21
 * Desc:Vr aidl 服务
 */

public class VrAidlService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return VrAidlServiceManager.getInstance().init(this);
    }

    @Override
    public void onDestroy() {
        VrAidlServiceManager.getInstance().kill();
        super.onDestroy();
    }
}
