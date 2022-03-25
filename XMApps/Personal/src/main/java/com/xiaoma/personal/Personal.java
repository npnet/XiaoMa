package com.xiaoma.personal;

import android.app.ActivityManager;
import android.app.Application;
import android.app.Service;
import android.content.Intent;
import android.text.TextUtils;

import com.mapbar.xiaoma.manager.XmMapNaviManager;
import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.center.logic.local.Center;
import com.xiaoma.component.base.BaseApp;
import com.xiaoma.db.DBManager;
import com.xiaoma.guide.utils.GuideToast;
import com.xiaoma.hotfix.HotfixConstants;
import com.xiaoma.hotfix.model.PatchConfig;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.model.User;
import com.xiaoma.network.XmHttp;
import com.xiaoma.personal.account.ui.PersonalCenterActivity;
import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.skin.utils.SkinUtils;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.voiceprint.VoicePrintManager;

/**
 * @author youthyJ
 * @date 2018/11/5
 */
@SuppressWarnings("unused")
@DefaultLifeCycle(application = HotfixConstants.App.Personal, flags = ShareConstants.TINKER_ENABLE_ALL)
public class Personal extends BaseApp {
    public Personal(Application app, int flags, boolean verify, long appStartElapsedTime, long appStartMillisTime, Intent result) {
        super(app, flags, verify, appStartElapsedTime, appStartMillisTime, result);
    }

    @Override
    public void initLibs() {
        super.initLibs();
        XmHttp.getDefault().init(getApplication());
        initDB();
        LoginManager.getInstance().init(getApplication());
        UserManager.getInstance().init(getApplication());
        listenUserLoginStatus();
        VoicePrintManager.getInstance().init(getApplication());
        initAutoTracker();
        Center.getInstance().init(getApplication());
        XmSkinManager.getInstance().initSkin(getApplication());
        SkinUtils.loadSkin(getApplication());
        XmMapNaviManager.getInstance().init(getApplication());
    }

    private void initDB() {
        DBManager.getInstance().with(getApplication());
        DBManager.getInstance().initGlobalDB();
        DBManager.getInstance().initUserDB(LoginManager.getInstance().getLoginUserId());
    }

    private void initAutoTracker() {
        if (!TextUtils.isEmpty(LoginManager.getInstance().getLoginUserId())) {
            XmAutoTracker.getInstance().init(getApplication(), LoginManager.getInstance().getLoginUserId());
        }
    }

    @Override
    protected String[] allNeedPermissions() {
        return new String[]{};
    }

    @Override
    public Class<?> firstActivity() {
        return PersonalCenterActivity.class;
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
        GuideToast.cancelToast();
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
                // 用户信息变化之后重启App,避免残留上一个用户的脏数据
                try {
                    ((ActivityManager) getApplication().getSystemService(Service.ACTIVITY_SERVICE)).restartPackage(getApplication().getPackageName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLogout() {
                XmHttp.getDefault().addCommonParams("uid", "");
            }
        });
    }

    @Override
    protected boolean needKillSelfOnLowMemory() {
        return true;
    }
}
