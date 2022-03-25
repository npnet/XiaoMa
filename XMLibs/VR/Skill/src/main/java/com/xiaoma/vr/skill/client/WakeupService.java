package com.xiaoma.vr.skill.client;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.xiaoma.vr.skill.SkillManager;

/**
 * @author youthyJ
 * @date 2019/6/13
 */
public class WakeupService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SkillManager.getInstance().init(getApplicationContext());
        stopSelf();
    }
}
