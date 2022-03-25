package com.xiaoma.launcher.common.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import com.xiaoma.aidl.model.ScheduleInfo;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.common.views.NaviBarWindow;
import com.xiaoma.launcher.schedule.manager.ScheduleDataManager;
import com.xiaoma.launcher.schedule.manager.ScheduleRemindManager;
import com.xiaoma.login.LoginManager;
import com.xiaoma.thread.Priority;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.logintype.manager.LoginTypeManager;
import com.xiaoma.utils.logintype.manager.TravellerLoginType;

/**
 * Created by Thomas on 2019/4/26 0026
 * 桌面开启前台通知服务 提高进程优先级
 */

public class LauncherService extends Service {

    public static final int NOTIFY_ID = 1688888888;
    private final IBinder mBinder = new ForegroundServiceProxyBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        KLog.e("LauncherService onCreate");
        startForeground(NOTIFY_ID, getNotification());
        if (LauncherConstants.IS_INIT_NAVIBAR_WINDOW) {
            NaviBarWindow.getNaviBarWindow().init(this);
        }
        //日程提醒服务
        if (LoginManager.getInstance().isUserLogin()) {
            ThreadDispatcher.getDispatcher().postSerial(new Runnable() {
                @Override
                public void run() {
                    if (LoginTypeManager.getInstance().getLoginType() instanceof TravellerLoginType) {
                        //如果是游客或者离线模式，先清空上次游客离线 日程数据
                        ScheduleDataManager.getDBManager().deleteAll(ScheduleInfo.class);
                    }
                    ScheduleRemindManager.getInstance().open(LauncherService.this);
                }
            }, Priority.NORMAL);
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    public class ForegroundServiceProxyBinder extends Binder {

        public LauncherService getService() {
            return LauncherService.this;
        }

    }

    public Notification getNotification() {
        Notification n = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final NotificationManager mgr = getSystemService(NotificationManager.class);
            if (mgr != null) {
                final String channelId = getPackageName() + "_ForegroundService";
                final NotificationChannel channel = new NotificationChannel(channelId, "Launcher", NotificationManager.IMPORTANCE_DEFAULT);
                mgr.createNotificationChannel(channel);
                // 通知只设置小icon,不设置任何内容,小马SystemUI将隐藏该通知的显示
                mgr.notify(NOTIFY_ID,
                        n = new Notification.Builder(this, channelId)
                                .setSmallIcon(R.drawable.icon_default_icon)
                                .build());
            }
        } else {
            n = new Notification();
        }
        return n;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ThreadDispatcher.getDispatcher().postSerial(new Runnable() {
            @Override
            public void run() {
                ScheduleRemindManager.getInstance().release(LauncherService.this);
            }
        }, Priority.NORMAL);
    }

}
