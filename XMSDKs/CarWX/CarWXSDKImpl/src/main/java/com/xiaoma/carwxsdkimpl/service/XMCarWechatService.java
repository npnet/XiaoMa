package com.xiaoma.carwxsdkimpl.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.xiaoma.carwxbase.utils.CarWXConstants;
import com.xiaoma.skin.constant.SkinConstants;

@SuppressLint("LogNotTimber")
public class XMCarWechatService extends Service {
    private static final String TAG = "XMCarWechatService";
    private CarWXBinder carWXBinder;
    private BroadcastReceiver mThemeReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SkinConstants.SKIN_SUCCESS);// 主题切换的广播
        registerReceiver(mThemeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int curTheme = XMCarManager.getInstance().getCurrentTheme();
                context.sendBroadcast(new Intent(CarWXConstants.ACTION_THEME_CHANGED)
                        .putExtra(CarWXConstants.EXTRA_THEME_ID_INT, curTheme));
                Log.d(TAG, String.format("onReceive -> Theme changed, themeId: %s", curTheme));

            }
        }, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mThemeReceiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        carWXBinder = new CarWXBinder(this);
        carWXBinder.onServiceBind(this);
        return carWXBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        carWXBinder.onServiceUnbind(this);
        return super.onUnbind(intent);
    }
}
