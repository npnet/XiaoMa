package com.xiaoma.motorcade;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.carlib.manager.XmMicManager;
import com.xiaoma.carlib.wheelcontrol.XmWheelManager;
import com.xiaoma.component.base.BaseApp;
import com.xiaoma.db.DBManager;
import com.xiaoma.hotfix.HotfixConstants;
import com.xiaoma.hotfix.model.PatchConfig;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.model.User;
import com.xiaoma.motorcade.common.im.IMUtils;
import com.xiaoma.motorcade.common.manager.MotorcadeRepo;
import com.xiaoma.motorcade.common.utils.MotorcadeSetting;
import com.xiaoma.motorcade.common.utils.UserUtil;
import com.xiaoma.network.XmHttp;
import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.skin.utils.SkinUtils;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.AppUtils;

/**
 * Created by ZYao.
 * Date ：2019/1/10 0010
 */
@SuppressWarnings("unused")
@DefaultLifeCycle(application = HotfixConstants.App.Motorcade, flags = ShareConstants.TINKER_ENABLE_ALL)
public class Motorcade extends BaseApp {
    public Motorcade(Application app, int flags, boolean verify, long appStartElapsedTime, long appStartMillisTime, Intent result) {
        super(app, flags, verify, appStartElapsedTime, appStartMillisTime, result);
    }

    @Override
    public void initLibs() {
        super.initLibs();
        XmHttp.getDefault().init(getApplication());
        LoginManager.getInstance().init(getApplication(), false);
        UserManager.getInstance().init(getApplication());
        MotorcadeSetting.init(getApplication());
        XmWheelManager.getInstance().init(getApplication());
        XmMicManager.getInstance().init(getApplication());
        listenUserLoginStatus();
        initDB();
        initHx();
        initAutoTracker();
        XmSkinManager.getInstance().initSkin(getApplication());
        SkinUtils.loadSkin(getApplication());
    }

    @Override
    protected String[] allNeedPermissions() {
        return new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.RECORD_AUDIO
        };
    }

    @Override
    public Class<?> firstActivity() {
        return MainActivity.class;
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

    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        MultiDex.install(base.getApplicationContext());
    }

    private void initAutoTracker() {
        if (!TextUtils.isEmpty(LoginManager.getInstance().getLoginUserId())) {
            XmAutoTracker.getInstance().init(getApplication(), LoginManager.getInstance().getLoginUserId());
        }
    }

    private void initDB() {
        DBManager.getInstance().with(getApplication());
        DBManager.getInstance().initGlobalDBCascade();
        DBManager.getInstance().initUserDBCascade(LoginManager.getInstance().getLoginUserId());
        MotorcadeRepo.init(getApplication());
    }

    private void initHx() {
        IMUtils.hxImInit(getApplication(), false);
        if (LoginManager.getInstance().isUserLogin()) {
            final User user = UserUtil.getCurrentUser();
            if (user != null) {
                IMUtils.loginHx(user.getHxAccountService(), user.getHxPasswordService(), null);
            }
        }
    }

    private void listenUserLoginStatus() {
        if (LoginManager.getInstance().isUserLogin()) {
            User user = UserManager.getInstance().getUser(LoginManager.getInstance().getLoginUserId());
            XmHttp.getDefault().addCommonParams("uid", String.valueOf(user.getId()));
        }
        LoginManager.getInstance().addLoginEventListener(new LoginManager.LoginEventListener() {
            @Override
            public void onLogin(User user) {
                // 用户信息变化之后重启App,避免残留上一个用户的脏数据
                ActivityManager am = (ActivityManager) getApplication().getSystemService(Service.ACTIVITY_SERVICE);
                am.restartPackage(getApplication().getPackageName());
            }

            @Override
            public void onLogout() {
                XmHttp.getDefault().addCommonParams("uid", "");
                IMUtils.loginOutHx();
                DBManager.getInstance().onUserLogout();
                if (AppUtils.isAppForeground()) {
                    // 前台退出登录重启App
                    final Intent launchIntent = getApplication().getPackageManager().getLaunchIntentForPackage(getApplication().getPackageName());
                    if (launchIntent != null) {
                        getApplication().startActivity(launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                    } else {
                        try {
                            ((ActivityManager) getApplication().getSystemService(Service.ACTIVITY_SERVICE)).restartPackage(getApplication().getPackageName());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    // 后台退出登录直接杀进程
                    System.exit(0);
                }
            }
        });
    }

    @Override
    protected boolean needKillSelfOnLowMemory() {
        return true;
    }
}
