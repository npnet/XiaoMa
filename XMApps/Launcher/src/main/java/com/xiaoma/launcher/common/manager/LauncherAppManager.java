package com.xiaoma.launcher.common.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.XmSystemManager;
import com.xiaoma.component.AppHolder;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.main.manager.CarSettingManager;
import com.xiaoma.utils.AppUtils;
import com.xiaoma.utils.ProcessUtils;
import com.xiaoma.utils.log.KLog;

import static com.xiaoma.center.logic.CenterConstants.BLUETOOTH_PHONE_DIED;

/**
 * Created by Thomas on 2019/4/1 0001
 * 桌面拉取app初始化
 */

public class LauncherAppManager {

    private static final String TAG = LauncherAppManager.class.getSimpleName();

    public static void launcherAppOutStart(Context context, String packageName) {
        String settingPkg = LauncherConstants.LauncherApp.LAUNCHER_SETTING_PACKAGE;
        if (settingPkg.equals(packageName)) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(settingPkg, LauncherConstants.LauncherApp.LAUNCHER_ACTIVITY_SERVICE_CLASS));
            context.startService(intent);
        }

        String dualscreenPkg = LauncherConstants.LauncherApp.LAUNCHER_DUALSCREEN_PACKAGE;
        if (dualscreenPkg.equals(packageName)) {
            XmCarFactory.getCarVendorExtensionManager().setInteractMode(SDKConstants.VALUE.HuInteractReq_INACTIVE_REQ);
            XmCarFactory.getCarVendorExtensionManager().setSimpleMenuDisplay(SDKConstants.VALUE.CanCommon_OFF);
            XmCarFactory.getCarVendorExtensionManager().setNaviDisplay(SDKConstants.VALUE.CanCommon_OFF);
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(dualscreenPkg, LauncherConstants.LauncherApp.LAUNCHER_ACTIVITY_SERVICE_CLASS));
            context.startService(intent);
        }

        String phonePkg = LauncherConstants.LauncherApp.LAUNCHER_BLUETOOTH_PHONE_PACKAGE;
        if (phonePkg.equals(packageName)) {
            context.sendBroadcast(new Intent(BLUETOOTH_PHONE_DIED));
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(phonePkg, LauncherConstants.LauncherApp.LAUNCHER_ACTIVITY_SERVICE_CLASS));
            context.startService(intent);
        }


        String musicPkg = LauncherConstants.LauncherApp.LAUNCHER_MUSIC_PACKAGE;
        if (musicPkg.equals(packageName)) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(musicPkg, LauncherConstants.LauncherApp.LAUNCHER_ACTIVITY_SERVICE_CLASS));
            context.startService(intent);
        }

        String xtingPkg = LauncherConstants.LauncherApp.LAUNCHER_XTING_PACKAGE;
        if (xtingPkg.equals(packageName)) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(xtingPkg, LauncherConstants.LauncherApp.LAUNCHER_ACTIVITY_SERVICE_CLASS));
            context.startService(intent);
        }

        String assistantPkg = LauncherConstants.LauncherApp.LAUNCHER_ASSISTANT_PACKAGE;
        if (assistantPkg.equals(packageName)) {
            launcherVrService(context);
        }
    }

    public static void launcherAudioService(Context context) {
        if (context == null) {
            return;
        }
        //拉取电台
        if (AppUtils.isAppInstalled(context, LauncherConstants.LauncherApp.LAUNCHER_XTING_PACKAGE)) {
            try {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setComponent(new ComponentName(LauncherConstants.LauncherApp.LAUNCHER_XTING_PACKAGE,
                        LauncherConstants.LauncherApp.LAUNCHER_XTING_CLASS));
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //拉取音乐
        if (AppUtils.isAppInstalled(context, LauncherConstants.LauncherApp.LAUNCHER_MUSIC_PACKAGE)) {
            try {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setComponent(new ComponentName(LauncherConstants.LauncherApp.LAUNCHER_MUSIC_PACKAGE,
                        LauncherConstants.LauncherApp.LAUNCHER_MUSIC_CLASS));
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void launcherAppStart(Context context) {
        if (context == null) {
            return;
        }
        //拉取应用中心通知服务
        if (AppUtils.isAppInstalled(context, LauncherConstants.LauncherApp.LAUNCHER_APPSTORE_PACKAGE)) {
            if (!ProcessUtils.isProcessExists(context, LauncherConstants.LauncherApp.LAUNCHER_APPSTORE_PACKAGE)) {
                try {
                    Intent serviceIntent = new Intent();
                    serviceIntent.setComponent(new ComponentName(LauncherConstants.LauncherApp.LAUNCHER_APPSTORE_PACKAGE, LauncherConstants.LauncherApp.LAUNCHER_APPSTORE_SERVICE));
                    context.startService(serviceIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        //拉取车服务通知服务
        if (AppUtils.isAppInstalled(context, LauncherConstants.LauncherApp.LAUNCHER_SERVICE_PACKAGE)) {
            if (!ProcessUtils.isProcessExists(context, LauncherConstants.LauncherApp.LAUNCHER_SERVICE_PACKAGE)) {
                try {
                    Intent serviceIntent = new Intent();
                    serviceIntent.setComponent(new ComponentName(LauncherConstants.LauncherApp.LAUNCHER_SERVICE_PACKAGE, LauncherConstants.LauncherApp.LAUNCHER_SERVICE_CARSERVICE));
                    context.startService(serviceIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        //拉取启明驾驶评分服务
        if (AppUtils.isAppInstalled(context, LauncherConstants.LauncherApp.LAUNCHER_QIMING_PACKAGE)) {
            if (!ProcessUtils.isProcessExists(context, LauncherConstants.LauncherApp.LAUNCHER_QIMING_PACKAGE)) {
                try {
                    ServiceConnection connection = new ServiceConnection() {
                        @Override
                        public void onServiceConnected(ComponentName name, IBinder service) {

                        }

                        @Override
                        public void onServiceDisconnected(ComponentName name) {

                        }
                    };
                    Intent serviceIntent = new Intent();
                    serviceIntent.setComponent(new ComponentName(LauncherConstants.LauncherApp.LAUNCHER_QIMING_PACKAGE, LauncherConstants.LauncherApp.LAUNCHER_QIMING_DRIVERSERVICE));
                    String vinInfo = CarSettingManager.getInstance().getVinInfo();
                    int engineStatus = CarSettingManager.getInstance().getEngineStatus();
                    serviceIntent.putExtra(LauncherConstants.LauncherApp.LAUNCHER_QIMING_VIN_KEY, vinInfo);
                    serviceIntent.putExtra(LauncherConstants.LauncherApp.LAUNCHER_QIMING_ENGINE_KEY, engineStatus);
                    KLog.d(TAG, "vin:" + vinInfo + "/" + "engineStatus:" + engineStatus);
                    context.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void launcherVrService(Context context) {
        if (context == null) {
            return;
        }
        //拉取语音
        if (XmSystemManager.getInstance().noUpdate()) {
            launcherVr(context);
        } else {
            XmSystemManager.getInstance().setUpdateListener(new XmSystemManager.IupDateListener() {
                @Override
                public void onUpdateChange(boolean noUpdate) {
                    if (noUpdate) {
                        launcherVr(context);
                    }
                }
            });
        }

        launcherSystemUI(context);
    }

    private static void launcherVr(Context context) {
        String launcherAssistantPackage = LauncherConstants.LauncherApp.LAUNCHER_ASSISTANT_PACKAGE;
        String launcherAssistantService = LauncherConstants.LauncherApp.LAUNCHER_ASSISTANT_SERVICE;
        if (AppUtils.isAppInstalled(context, launcherAssistantPackage)) {
            if (!ProcessUtils.isProcessExists(context, launcherAssistantPackage)) {
                try {
                    Intent serviceIntent = new Intent();
                    serviceIntent.setComponent(new ComponentName(launcherAssistantPackage, launcherAssistantService));
                    context.startService(serviceIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void launcherSystemUI(Context context) {
        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            intent.setAction("com.xiaoma.systemui.START");
            context.sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void launcherOtherApp(Context context) {
        if (context == null) {
            return;
        }
        //拉取其它配置App
        String[] apps = AppHolder.getInstance().getAppContext().getResources().getStringArray(R.array.launcher_apps);
        if (apps.length <= 0) {
            return;
        }
        for (int i = 0; i < apps.length; i++) {
            if (!ProcessUtils.isProcessExists(context, apps[i])) {
                try {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(apps[i], LauncherConstants.LauncherApp.LAUNCHER_ACTIVITY_SERVICE_CLASS));
                    context.startService(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void launcherForPowerOn(Context context) {
        if (context == null) {
            return;
        }
        //拉取开机启动app
        String[] apps = AppHolder.getInstance().getAppContext().getResources().getStringArray(R.array.power_on_apps);
        if (apps.length <= 0) {
            return;
        }
        for (int i = 0; i < apps.length; i++) {
            if (!ProcessUtils.isProcessExists(context, apps[i])) {
                try {
                    KLog.d(TAG, " launcherForPowerOn : ");
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(apps[i], LauncherConstants.LauncherApp.LAUNCHER_ACTIVITY_SERVICE_CLASS));
                    context.startService(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
