package com.xiaoma.shop;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.component.base.BaseApp;
import com.xiaoma.db.DBManager;
import com.xiaoma.guide.utils.GuideToast;
import com.xiaoma.hotfix.HotfixConstants;
import com.xiaoma.hotfix.model.PatchConfig;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.model.User;
import com.xiaoma.network.XmHttp;
import com.xiaoma.shop.business.ui.main.MainActivity;
import com.xiaoma.shop.common.manager.update.UpdateOtaManager;
import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.skin.utils.SkinUtils;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.log.KLog;

/**
 * @author wutao
 * @date 2019/1/7
 */
@SuppressWarnings("unused")
@DefaultLifeCycle(application = HotfixConstants.App.Shop, flags = ShareConstants.TINKER_ENABLE_ALL)
public class Shop extends BaseApp {
    public Shop(Application app, int flags, boolean verify, long appStartElapsedTime, long appStartMillisTime, Intent result) {
        super(app, flags, verify, appStartElapsedTime, appStartMillisTime, result);
    }

    @Override
    public void initLibs() {
        super.initLibs();
        XmHttp.getDefault().init(getApplication());
        LoginManager.getInstance().init(getApplication());
        UserManager.getInstance().init(getApplication());
        listenUserLoginStatus();
        initDB();
        //        FaultFactory.getSDK().init(getApplication());
        //        FaultFactory.getSDK().registerFaultListener(new FaultListener() {
        //            @Override
        //            public void onFaultOccur(List<CarFault> carFault) {
        ////                MainActivity.NewFault(getApplication(), new ArrayList<>(carFault));
        //            }
        //        });
        //        initAppUpdate();
        initAutoTracker();
        XmSkinManager.getInstance().initSkin(getApplication());
        SkinUtils.loadSkin(getApplication());
        initUpdateOtaService();
        KLog.i("filOut| "+"[initLibs]->shop process start");
    }

    private void initUpdateOtaService() {
        UpdateOtaManager.getInstance().initService(getApplication());
    }

    private void initAutoTracker() {
        if (!TextUtils.isEmpty(LoginManager.getInstance().getLoginUserId())) {
            XmAutoTracker.getInstance().init(getApplication(), LoginManager.getInstance().getLoginUserId());
        }
    }

    //    private void initAppUpdate() {
    //        Intent intent = new Intent(getApplication(), AppUpdateService.class);
    //        intent.putExtra(AppUpdateService.ARGS_PACKAGE_NAME, getApplication().getPackageName());
    //        getApplication().startService(intent);
    //    }
    private void initDB() {
        DBManager.getInstance().with(getApplication());
        DBManager.getInstance().initGlobalDB();
        DBManager.getInstance().initUserDB(LoginManager.getInstance().getLoginUserId());
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
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        MultiDex.install(base.getApplicationContext());
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
                }
                initAutoTracker();
            }

            @Override
            public void onLogout() {
                XmHttp.getDefault().addCommonParams("uid", "");
            }
        });
    }

    @Override
    protected boolean needKillSelfOnLowMemory() {
        return false;
    }
}
