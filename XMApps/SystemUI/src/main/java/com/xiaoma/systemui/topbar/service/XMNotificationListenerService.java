package com.xiaoma.systemui.topbar.service;

import android.Manifest;
import android.car.Car;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.CarVendorExtensionManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.os.UserHandle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import android.support.v4.content.PermissionChecker;

import com.xiaoma.systemui.BuildConfig;
import com.xiaoma.systemui.common.constant.BroadcastAction;
import com.xiaoma.systemui.common.controller.CarConnector;
import com.xiaoma.systemui.common.controller.OnConnectStateListener;
import com.xiaoma.systemui.common.util.LogUtil;
import com.xiaoma.systemui.topbar.controller.PanelController;
import com.xiaoma.systemui.topbar.controller.StatusBarController;
import com.xiaoma.systemui.topbar.controller.TopBarController;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Set;

/**
 * Created by LKF on 2018/11/21 0021.
 */
public class XMNotificationListenerService extends NotificationListenerService implements OnConnectStateListener<Car>, CarVendorExtensionManager.CarVendorExtensionCallback {
    private final String TAG = getClass().getSimpleName();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private BroadcastReceiver mReceiver;

    private void logI(String format, Object... args) {
        LogUtil.logI(getClass().getSimpleName(), format, args);
    }

    private void logE(String format, Object... args) {
        LogUtil.logE(getClass().getSimpleName(), format, args);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        logI("onCreate");
        checkNotificationAccessSetting();
        // 将当前服务注册为系统服务
        try {
            registerAsSystemService(this, new ComponentName(this, getClass()), UserHandle.USER_ALL);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        PanelController.getInstance().setNotificationMgrDelegate(new PanelController.NotificationMgrDelegate() {
            @Override
            public void cancelNotification(String key) {
                XMNotificationListenerService.this.cancelNotification(key);
            }

            @Override
            public void cancelNotifications(Set<String> keys) {
                if (keys == null || keys.isEmpty())
                    return;
                final String[] keysArr = new String[keys.size()];
                keys.toArray(keysArr);
                XMNotificationListenerService.this.cancelNotifications(keysArr);
            }


            @Override
            public void cancelAllNotifications() {
                XMNotificationListenerService.this.cancelAllNotifications();
            }

            @Override
            public StatusBarNotification[] getNotifications() {
                final StatusBarNotification[] notifications = XMNotificationListenerService.this.getActiveNotifications();
                LogUtil.dump(TAG, notifications);
                return notifications;
            }
        });

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_TIME_CHANGED);
        intentFilter.addAction(BroadcastAction.ACTION_VR_SHOW);
        intentFilter.addAction(BroadcastAction.ACTION_VR_DIMISS);
        if (BuildConfig.DEBUG) {
            intentFilter.addAction("com.xiaoma.TEST_AVS");
        }
        registerReceiver(mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {
                String action = intent.getAction();
                if (Intent.ACTION_TIME_CHANGED.equals(action)) {
                    PanelController.getInstance().updateNotifications();
                } else if (BroadcastAction.ACTION_VR_SHOW.equals(action)) {
                    // 语音助手起来时,隐藏状态栏和通知栏
                    try {
                        TopBarController.getInstance().getStatusBar().animateCollapsePanels();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    StatusBarController.getInstance().dismiss();
                } else if (BroadcastAction.ACTION_VR_DIMISS.equals(action)) {
                    // 语音助手关闭后,重新添加状态栏
                    StatusBarController.getInstance().show();
                }

                if (BuildConfig.DEBUG) {
                    if ("com.xiaoma.TEST_AVS".equals(intent.getAction())) {
                        // 倒车进入时隐藏状态栏,避免滑动事件透传
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (intent.getBooleanExtra("isOn", false)) {
                                    try {
                                        TopBarController.getInstance().getStatusBar().animateCollapsePanels();
                                    } catch (Throwable e) {
                                        e.printStackTrace();
                                    }
                                    StatusBarController.getInstance().dismiss();
                                } else {
                                    StatusBarController.getInstance().show();
                                }
                            }
                        });
                    }
                }
            }
        }, intentFilter);
        PanelController.getInstance().updateNotifications();
        CarConnector.getInstance().registerConnectListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        try {
            unregisterAsSystemService();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        CarConnector.getInstance().unregisterConnectListener(this);
    }

    private void checkNotificationAccessSetting() {
        final int rlt = PermissionChecker.checkSelfPermission(this, Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE);
        logE("checkNotificationAccessSetting -> Check permission rlt: %d", rlt);
        /*if (PackageManager.PERMISSION_GRANTED != rlt) {
            try {
                startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }

    private void updatePanelNotifications() {
        PanelController.getInstance().updateNotifications();
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        logE("onListenerConnected");
        updatePanelNotifications();
    }

    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        logE("onListenerDisconnected");
        updatePanelNotifications();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        logI("onNotificationPosted: [ sbn: %s ]", sbn);
        updatePanelNotifications();
        PanelController.getInstance().showHeadsUpView(sbn);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn, RankingMap rankingMap) {
        super.onNotificationPosted(sbn, rankingMap);
        long when = -1;
        if (sbn != null && sbn.getNotification() != null) {
            when = sbn.getNotification().when;
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
        logI("onNotificationPosted: [ sbn: %s, rankingMap: %s ] when: %s, curr: %s",
                sbn, rankingMap, df.format(when), df.format(System.currentTimeMillis()));
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        logI("onNotificationRemoved: " + sbn);
        updatePanelNotifications();
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap) {
        super.onNotificationRemoved(sbn, rankingMap);
        logI(String.format("onNotificationRemoved: [ sbn: %s, rankingMap: %s ]", sbn, rankingMap));
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap, int reason) {
        super.onNotificationRemoved(sbn, rankingMap, reason);
        logI(String.format("onNotificationRemoved: [ sbn: %s, rankingMap: %s, reason: %s ]", sbn, rankingMap, reason));
    }

    @Override
    public void onNotificationRankingUpdate(RankingMap rankingMap) {
        super.onNotificationRankingUpdate(rankingMap);
        logI(String.format("onNotificationRankingUpdate: [ rankingMap: %s ]", rankingMap));
        updatePanelNotifications();
    }

    private CarVendorExtensionManager mCarVendorExtensionManager;

    @Override
    public void onConnected(@NonNull Car car) {
        try {
            mCarVendorExtensionManager = (CarVendorExtensionManager) car.getCarManager(Car.VENDOR_EXTENSION_SERVICE);
            mCarVendorExtensionManager.registerCallback(this);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisconnected() {
        try {
            mCarVendorExtensionManager.unregisterCallback(this);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onChangeEvent(CarPropertyValue carPropertyValue) {
        try {
            if (CarVendorExtensionManager.ID_CAMERA_STATUS == carPropertyValue.getPropertyId()) {
                final Boolean isOpen = mCarVendorExtensionManager.getGlobalProperty(
                        Boolean.class, CarVendorExtensionManager.ID_CAMERA_STATUS);
                // 倒车进入时隐藏状态栏,避免滑动事件透传
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isOpen != null && isOpen) {
                            try {
                                TopBarController.getInstance().getStatusBar().animateCollapsePanels();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                            StatusBarController.getInstance().dismiss();
                        } else {
                            StatusBarController.getInstance().show();
                        }
                    }
                });
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorEvent(int propertyId, int area) {

    }
}