package com.xiaoma.instruction;

import android.app.Application;
import android.content.Intent;
import android.text.TextUtils;

import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.component.base.BaseApp;
import com.xiaoma.db.DBManager;
import com.xiaoma.guide.utils.GuideToast;
import com.xiaoma.hotfix.HotfixConstants;
import com.xiaoma.hotfix.model.PatchConfig;
import com.xiaoma.instruction.ui.activity.MainActivity;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.model.User;
import com.xiaoma.network.XmHttp;
import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.skin.utils.SkinUtils;
import com.xiaoma.ui.toast.XMToast;

/**
 * Created by Thomas on 2019/6/1 0001
 * 使用手册
 */

@DefaultLifeCycle(application = HotfixConstants.App.Instruction, flags = ShareConstants.TINKER_ENABLE_ALL)
public class Instruction extends BaseApp {

    public Instruction(Application app, int flags, boolean verify, long appStartElapsedTime, long appStartMillisTime, Intent result) {
        super(app, flags, verify, appStartElapsedTime, appStartMillisTime, result);
    }

    @Override
    public void initLibs() {
        super.initLibs();
        XmHttp.getDefault().init(getApplication());
        LoginManager.getInstance().init(getApplication(), false);
        UserManager.getInstance().init(getApplication());
        listenUserLoginStatus();
        initDB();
        initAutoTracker();
        XmSkinManager.getInstance().initSkin(getApplication());
        SkinUtils.loadSkin(getApplication());
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

    private void listenUserLoginStatus() {
        if (LoginManager.getInstance().isUserLogin()) {
            User user = UserManager.getInstance().getUser(LoginManager.getInstance().getLoginUserId());
            XmHttp.getDefault().addCommonParams("uid", String.valueOf(user.getId()));
        }
        LoginManager.getInstance().addLoginEventListener(new LoginManager.LoginEventListener() {
            @Override
            public void onLogin(User user) {
                if (user != null) {
                    XmHttp.getDefault().addCommonParams("uid", String.valueOf(user.getId()));
                    DBManager.getInstance().initUserDB(user.getId());
                    initAutoTracker();
                }
            }

            @Override
            public void onLogout() {
                XmHttp.getDefault().addCommonParams("uid", "");
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

    @Override
    protected boolean needKillSelfOnLowMemory() {
        return true;
    }
}
