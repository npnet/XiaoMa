package com.xiaoma.carlib.wheelcontrol;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by LKF on 2019-5-6 0006.
 */
@SuppressLint("LogNotTimber")
public class WheelControlService extends Service {
    private static final String TAG = "WheelControlService";
    private ScheduledExecutorService mExecutorService;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate()");
        // TODO: 测试,每隔一段时间自动模拟一个方控事件
        mExecutorService = Executors.newSingleThreadScheduledExecutor();
        mExecutorService.scheduleWithFixedDelay(new Runnable() {
            private final Random mRandom = new Random();

            @Override
            public void run() {
                // 删除测试代码
                /*final int keyAction = 11 + mRandom.nextInt(14 - 11 + 1);
                final int keyCode = 1 + mRandom.nextInt(10 - 1 + 1);
                LocalBroadcastManager.getInstance(WheelControlService.this).sendBroadcast(new Intent(WheelConstant.ACTION_WHEEL_KEY_EVENT)
                        .putExtra(WheelConstant.EXTRA_KEY_ACTION, keyAction)
                        .putExtra(WheelConstant.EXTRA_KEY_CODE, keyCode));*/
            }
        }, 3, 3, TimeUnit.SECONDS);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
        mExecutorService.shutdown();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, String.format("onBind( intent: %s )", intent));
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, String.format("onUnbind( intent: %s )", intent));
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, String.format("onStartCommand( intent: %s, flags: %s, startId: %s )", intent, flags, startId));
        return super.onStartCommand(intent, flags, startId);
    }
}
