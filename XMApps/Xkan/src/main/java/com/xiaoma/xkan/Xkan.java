package com.xiaoma.xkan;

import android.app.Application;
import android.content.Intent;
import android.os.Environment;
import android.os.Looper;
import android.os.MessageQueue;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.manager.XmCarEventDispatcher;
import com.xiaoma.carlib.wheelcontrol.XmWheelManager;
import com.xiaoma.component.AppHolder;
import com.xiaoma.component.base.BaseApp;
import com.xiaoma.db.DBManager;
import com.xiaoma.hotfix.HotfixConstants;
import com.xiaoma.hotfix.model.PatchConfig;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.model.User;
import com.xiaoma.network.XmHttp;
import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.skin.utils.SkinUtils;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.xkan.common.manager.UsbMediaDataManager;
import com.xiaoma.xkan.common.manager.XkanCarEvent;
import com.xiaoma.xkan.main.ui.MainActivity;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;

import static android.content.ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN;

/**
 * Created by Thomas on 2018/11/5 0005
 */

@SuppressWarnings("unused")
@DefaultLifeCycle(application = HotfixConstants.App.XKan, flags = ShareConstants.TINKER_ENABLE_ALL)
public class Xkan extends BaseApp {

    public Xkan(Application app, int flags, boolean verify, long appStartElapsedTime, long appStartMillisTime, Intent result) {
        super(app, flags, verify, appStartElapsedTime, appStartMillisTime, result);
    }

    @Override
    public void initLibs() {
        super.initLibs();
        XmHttp.getDefault().init(getApplication());
        LoginManager.getInstance().init(getApplication(), false);
        UserManager.getInstance().init(getApplication());
        initUsbManager();
        XmSkinManager.getInstance().initSkin(getApplication());
        SkinUtils.loadSkin(getApplication());
        XmWheelManager.getInstance().init(getApplication());
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {
                listenUserLoginStatus();
                initDB();
                initAutoTracker();
                XmCarFactory.init(getApplication());
                XmCarEventDispatcher.getInstance().registerEvent(XkanCarEvent.getInstance());
                return false;
            }
        });
        caughtExceptionPrintLocal();
    }

    private void caughtExceptionPrintLocal() {
        final Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 异常Crash打印到本地文件
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                try {
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                    File f = new File(Environment.getExternalStorageDirectory(),
                            "crash_" + AppHolder.getInstance().getAppContext().getPackageName() + "_" + df.format(System.currentTimeMillis()) + ".log");
                    if (f.exists()) {
                        f.delete();
                    }
                    f.createNewFile();
                    e.printStackTrace(new PrintStream(f));
                } catch (IOException ioEx) {
                    ioEx.printStackTrace();
                }
                defaultHandler.uncaughtException(t, e);
            }
        });
    }

    @Override
    protected String[] allNeedPermissions() {
        return new String[]{};
    }

    @Override
    public Class<?> firstActivity() {
        return MainActivity.class;
    }

    private void listenUserLoginStatus() {
        if (LoginManager.getInstance().isUserLogin()) {
            User user = UserManager.getInstance().getUser(LoginManager.getInstance().getLoginUserId());
            XmHttp.getDefault().addCommonParams("uid", String.valueOf(user.getId()));
        }
        LoginManager.getInstance().addLoginEventListener(new LoginManager.LoginEventListener() {
            @Override
            public void onLogin(User data) {
                if (data != null) {
                    XmHttp.getDefault().addCommonParams("uid", String.valueOf(data.getId()));
                    DBManager.getInstance().initUserDB(data.getId());
                }
                initAutoTracker();
            }

            @Override
            public void onLogout() {
                XmHttp.getDefault().addCommonParams("uid", "");
            }
        });
    }

    private void initUsbManager() {
        UsbMediaDataManager.getInstance().init(getApplication());
    }

    private void initAutoTracker() {
        if (!TextUtils.isEmpty(LoginManager.getInstance().getLoginUserId())) {
            XmAutoTracker.getInstance().init(getApplication(), LoginManager.getInstance().getLoginUserId());
        }
    }

    @Override
    protected PatchConfig obtainPatchConfig() {
        String baseVersion = BuildConfig.TINKER_BASE_VERSION;
        int patchVersion = BuildConfig.TINKER_PATCH_VERSION;
        PatchConfig patchConfig = new PatchConfig();
        patchConfig.setBasePkgVersion(baseVersion);
        patchConfig.setPatchVersion(patchVersion);
        return patchConfig;
    }

    @Override
    protected void onAppIntoBackground() {
        XMToast.cancelToast();
    }

    private void initDB() {
        DBManager.getInstance().with(getApplication());
        DBManager.getInstance().initGlobalDB();
        DBManager.getInstance().initUserDB(LoginManager.getInstance().getLoginUserId());
    }

    @Override
    protected void doKillSelfOnTrimMemoryBefore(int level) {
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            //当app置换到后台,清楚所有缓存
            Glide.get(getApplication()).clearMemory();
        }
        Glide.get(getApplication()).trimMemory(level);
    }

    @Override
    protected void doKillSelfOnLowMemoryBefore() {
        Glide.get(getApplication()).clearMemory();
    }

    @Override
    protected boolean needKillSelfOnLowMemory() {
        return true;
    }
}
