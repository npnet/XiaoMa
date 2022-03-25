package com.xiaoma.dualscreen.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.DisplayInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.xiaoma.dualscreen.R;
import com.xiaoma.dualscreen.presentation.XmPresentation;
import com.xiaoma.dualscreen.views.ViewCache;
import com.xiaoma.utils.log.KLog;


/**
 * @author: iSun
 * @date: 2018/12/17 0017
 */
public class PresentationService extends Service {

    private static final String TAG = PresentationService.class.getSimpleName();
    private static final int DUALSCREEN_NOTIFICATION_ID = 123435781;

    private DisplayManager mDisplayManager;//屏幕管理类
    private Display mDisplay[];//屏幕数组
    private LinearLayout mFloatLayout;
    private WindowManager.LayoutParams wmParams;
    private WindowManager mWindowManager;
    private XmPresentation mXmPresentation;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    BroadcastReceiver localeChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED)) {
                ViewCache.getInstance().clear();
                mXmPresentation.refreshByLauncherChange();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        showNotification();
        createFloatView();
        registerReceiver(localeChangeReceiver, new IntentFilter(Intent.ACTION_LOCALE_CHANGED));
        KLog.e("PresentationService onCreate");
    }


    public void showNotification() {
        Notification n = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final NotificationManager mgr = getSystemService(NotificationManager.class);
            if (mgr != null) {
                final String channelId = getPackageName();
                final NotificationChannel channel = new NotificationChannel(channelId, "dualscreen", NotificationManager.IMPORTANCE_DEFAULT);
                mgr.createNotificationChannel(channel);
                // 通知只设置小icon,不设置任何内容,小马SystemUI将隐藏该通知的显示
                mgr.notify(DUALSCREEN_NOTIFICATION_ID,
                        n = new Notification.Builder(getApplicationContext(), channelId)
                                .setSmallIcon(R.drawable.icon_default_icon)
                                .build());
            }
        } else {
            n = new Notification();
        }
        startForeground(DUALSCREEN_NOTIFICATION_ID, n);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        KLog.e("PresentationService onStartCommand");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showPersentation();
            }
        }, 100);
        return super.onStartCommand(intent, flags, startId);

    }

    private void initDisplayData() {
        mDisplayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        mDisplay = mDisplayManager.getDisplays();
    }

    private void showPersentation() {
        initDisplayData();
        if (mDisplay.length < 2) {
            return;
        }
        for (int i = 0; i < mDisplay.length; i++) {
            Display display = mDisplay[i];
            DisplayInfo displayInfo = new DisplayInfo();
            display.getDisplayInfo(displayInfo);
            KLog.e("获取屏幕 displayName=" + display.getName());
        }
        Display sencondDisplay = mDisplay[1];
        for (int i = 0; i < mDisplay.length; i++) {
            Display display = mDisplay[i];
            DisplayInfo displayInfo = new DisplayInfo();
            display.getDisplayInfo(displayInfo);
            if (displayInfo.name.contains("HDMI")) {
                sencondDisplay = display;
                break;
            }
        }
        if (mXmPresentation == null) {
            mXmPresentation = new XmPresentation(this, sencondDisplay);
        }
        mXmPresentation.show();
    }


    private void createFloatView() {
        wmParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager)getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.x = 1;
        wmParams.y = 10;
        wmParams.width = 1;
        wmParams.height = 1;
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        mFloatLayout = (LinearLayout) inflater.inflate(R.layout.float_layout, null);
        mWindowManager.addView(mFloatLayout, wmParams);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(localeChangeReceiver);
        KLog.e("PresentationService onDestroy");
    }

}
