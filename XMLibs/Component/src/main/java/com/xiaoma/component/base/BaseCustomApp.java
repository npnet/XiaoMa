package com.xiaoma.component.base;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.support.multidex.MultiDex;

import com.tencent.bugly.crashreport.CrashReport;
import com.xiaoma.alive.AliveClient;
import com.xiaoma.component.AppHolder;
import com.xiaoma.component.nodejump.NodeUtils;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.skin.constant.SkinConstants;
import com.xiaoma.skin.receiver.SkinReceiver;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.ProcessUtils;
import com.xiaoma.utils.apptool.AppObserver;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.dispatch.DispatchManager;

import static com.xiaoma.component.ComponentConstants.BUGLY_APP_ID;

public class BaseCustomApp extends Application {
    @Override
    final public void onCreate() {
        super.onCreate();
        AppHolder.getInstance().init(this);
        // 观察App前后台状态的模块
        AppObserver.getInstance().init(this);
        asyncInitKLog();
        boolean isMainProcess = ProcessUtils.isMainProcess(this);
        if (isMainProcess) {
            initBugly();
        }
        onCreate(isMainProcess);
        onCreatePost();

//        if(ConfigManager.ApkConfig.isDebug()){
//            if (LeakCanary.isInAnalyzerProcess(this)) {
//                // This process is dedicated to LeakCanary for heap analysis.
//                // You should not init your app in this process.
//                return;
//            }
//            LeakCanary.install(this);
//        }
    }

    /**
     * onCreate回调进程信息
     *
     * @param isMainProcess 是否为当前应用的主进程
     */
    public void onCreate(boolean isMainProcess) {
        if (isMainProcess) {
            // 主进程相关操作
            NodeUtils.ActNodeManager.getInstance().init(this);
        }
        //必须多进程初始化 否则其他进程获取用户文件夹会出错
        ConfigManager.getInstance().setDeviceInfo(ConfigManager.DeviceConfig.getIMEI(getApplication()));
        if (isMainProcess) {
            initLibs();
        }
    }

    /**
     * onCreate执行完之后
     */
    protected void onCreatePost() {

    }

    @Override
    public void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * 第三方进程的初始化请写在第三方进程里面
     * 参考AppUpdateService
     */
    public void initLibs() {
        DispatchManager.getInstance().init(getApplication());
        AliveClient.register(getApplication());
//        XmSkinManager.getInstance().initSkin(getApplication());
        initSkinReciver();
        KLog.i("---------BaseApp initLibs-------------");
    }

    private void initSkinReciver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SkinConstants.SKIN_ACTION);
        intentFilter.addAction(SkinConstants.SKIN_ACTION_DAOMENG);
        intentFilter.addAction(SkinConstants.SKIN_ACTION_QINGSHE);
        intentFilter.addAction(SkinConstants.SKIN_ACTION_XM);
        AppHolder.getInstance().getAppContext().registerReceiver(new SkinReceiver(), intentFilter);
    }

    private void asyncInitKLog() {
        ThreadDispatcher.getDispatcher().postHighPriority(new Runnable() {
            @Override
            public void run() {
                KLog.init(getApplication());
            }
        });
    }

    private void initBugly() {
        CrashReport.initCrashReport(this, BUGLY_APP_ID, ConfigManager.ApkConfig.isDebug());
        if (ConfigManager.ApkConfig.isCarPlatform()) {
            CrashReport.setAppChannel(this, ConfigManager.ApkConfig.getChannelID() + "_CAR");
        } else {
            CrashReport.setAppChannel(this, ConfigManager.ApkConfig.getChannelID() + "_PAD");
        }
    }

    protected Application getApplication() {
        return this;
    }
}
