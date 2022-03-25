package com.xiaoma.component.base;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import com.tencent.bugly.crashreport.CrashReport;
import com.xiaoma.alive.AliveClient;
import com.xiaoma.component.AppHolder;
import com.xiaoma.component.dispatch.FrontDispatch;
import com.xiaoma.component.nodejump.NodeUtils;
import com.xiaoma.component.permission.PermissionHelper;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.hotfix.TinkerAppProxy;
import com.xiaoma.skin.constant.SkinConstants;
import com.xiaoma.skin.receiver.SkinReceiver;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.ProcessUtils;
import com.xiaoma.utils.apptool.AppObserver;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.dispatch.DispatchManager;

import static com.xiaoma.component.ComponentConstants.BUGLY_APP_ID;

/**
 * Created by youthyj on 2018/9/6
 */
public abstract class BaseApp extends TinkerAppProxy {

    public BaseApp(Application app, int flags, boolean verify, long appStartElapsedTime, long appStartMillisTime, Intent result) {
        super(app, flags, verify, appStartElapsedTime, appStartMillisTime, result);
    }

    /**
     * 由于涉及到多进程的时候,onCreate会在每个进程初始化的时候被调用,可能需要对不同进程做一些处理,
     * 比如:有的模块只需在主进程中执行初始化,而无需在其他子进程中调用.
     * 第三方进程的初始化请写在第三方进程里面
     * 请重写{@link #onCreate(boolean)}
     */
    @Override
    final public void onCreate() {
        super.onCreate();
        AppHolder.getInstance().init(getApplication());
        // 观察App前后台状态的模块
        AppObserver.getInstance().init(getApplication());
        asyncInitKLog();
        // RSA加解密目前没有使用,不初始化,减少启动时间
        //RSAUtils.RSAPublicKeyHolder.getInstance().init(getApplication());
        boolean isMainProcess = ProcessUtils.isMainProcess(getApplication());
        if (isMainProcess) {
            initBugly();
        }
        onCreate(isMainProcess);

//        if(ConfigManager.ApkConfig.isDebug()){
//            if (LeakCanary.isInAnalyzerProcess(getApplication())) {
//                // This process is dedicated to LeakCanary for heap analysis.
//                // You should not init your app in this process.
//                return;
//            }
//            LeakCanary.install(getApplication());
//        }
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
        CrashReport.initCrashReport(getApplication(), BUGLY_APP_ID, ConfigManager.ApkConfig.isDebug());
        if (ConfigManager.ApkConfig.isCarPlatform()) {
            CrashReport.setAppChannel(getApplication(), ConfigManager.ApkConfig.getChannelID() + "_CAR");
        } else {
            CrashReport.setAppChannel(getApplication(), ConfigManager.ApkConfig.getChannelID() + "_PAD");
        }
    }

    /**
     * onCreate回调进程信息
     *
     * @param isMainProcess 是否为当前应用的主进程
     */
    public void onCreate(boolean isMainProcess) {
        if (isMainProcess) {
            // 主进程相关操作
            NodeUtils.ActNodeManager.getInstance().init(getApplication());
            receiverExitBroadcast();
            FrontDispatch.getInstance().init(getApplication())
                    .addOnFrontListener(new FrontDispatch.OnFrontEvent() {
                        @Override
                        public void intoFront() {
                            onAppIntoFront();
                        }

                        @Override
                        public void intoBackground() {
                            onAppIntoBackground();
                        }
                    });
        }
        //必须多进程初始化 否则其他进程获取用户文件夹会出错
        ConfigManager.getInstance().setDeviceInfo(ConfigManager.DeviceConfig.getIMEI(getApplication()));
        if ((ConfigManager.ApkConfig.isCarPlatform() || isAllPermissionAgree())
                && isMainProcess) {
            initLibs();
            startTinkerUpgradeService();
        }
    }

    protected void onAppIntoFront() {
        // empty
    }

    protected void onAppIntoBackground() {
        // empty
    }

    private boolean isAllPermissionAgree() {
        if (getApplication().getApplicationInfo().targetSdkVersion < Build.VERSION_CODES.M) {
            return true;
        }
        return PermissionHelper.isAllPermissionAgree(getApplication(), PermissionHelper.MUST_PERMISSION, allNeedPermissions());
    }

    protected abstract String[] allNeedPermissions();

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

    public abstract Class<?> firstActivity();

    private void receiverExitBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("close_app_"+getApplication().getPackageName());
        intentFilter.addAction("forcestop_process");
        getApplication().registerReceiver(receiver, intentFilter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(("close_app_"+getApplication().getPackageName()).equals(intent.getAction())){
                //exitApp其实杀掉进程后会被重新拉起
                exitApp();
            }else if("forcestop_process".equals(intent.getAction())){
                //killSelfOnLowMemory杀死进程后是不会重新被拉起的
                killSelfOnLowMemory();
            }
        }
    };

    protected void exitApp() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    protected void killSelfOnLowMemory() {
        if (needKillSelfOnLowMemory()){
            if (!AppObserver.getInstance().isForeground()) {
                Log.e(getClass().getName(), "onLowMemory, this application is on low memory and not a foreground process, kill self now");
                AppObserver.getInstance().closeAllActivitiesAndExit();
            } else {
                Log.e(getClass().getName(), "onLowMemory, this application is on low memory and is a foreground process");
            }
        }else{
            Log.e(getClass().getName(), "onLowMemory, this application is need not kill self now");
        }
    }

    protected boolean needKillSelfOnLowMemory() {
        return false;
    }

    protected void doKillSelfOnLowMemoryBefore() {
    }

    protected void doKillSelfOnTrimMemoryBefore(int level) {
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        doKillSelfOnLowMemoryBefore();
        killSelfOnLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        doKillSelfOnTrimMemoryBefore(level);
        if(level == ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL){
            Intent intent = new Intent();
            intent.setAction("forcestop_process");
            getApplication().sendBroadcast(intent);
            Log.e(getClass().getName(), "onTrimMemory TRIM_MEMORY_RUNNING_CRITICAL");
        }else if (level == ComponentCallbacks2.TRIM_MEMORY_BACKGROUND
                //表示手机目前内存已经很低了，系统准备开始根据LRU缓存来清理进程。
                // 这个时候我们的程序在LRU缓存列表的最近位置，是不太可能被清理掉的，
                // 但这时去释放掉一些比较容易恢复的资源能够让手机的内存变得比较充足，
                // 从而让我们的程序更长时间地保留在缓存当中，这样当用户返回我们的程序时会感觉非常顺畅，
                // 而不是经历了一次重新启动的过程。
                || level == ComponentCallbacks2.TRIM_MEMORY_MODERATE
                //表示手机目前内存已经很低了，并且我们的程序处于LRU缓存列表的中间位置，
                // 如果手机内存还得不到进一步释放的话，那么我们的程序就有被系统杀掉的风险了。
                || level == ComponentCallbacks2.TRIM_MEMORY_COMPLETE) {
            //表示手机目前内存已经很低了，并且我们的程序处于LRU缓存列表的最边缘位置，
            // 系统会最优先考虑杀掉我们的应用程序，在这个时候应当尽可能地把一切可以释放的东西都进行释放。
            Log.e(getClass().getName(), "onTrimMemory TRIM_MEMORY_BACKGROUND or TRIM_MEMORY_COMPLETE");
            killSelfOnLowMemory();
        }
    }
}
