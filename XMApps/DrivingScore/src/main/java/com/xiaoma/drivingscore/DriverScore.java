package com.xiaoma.drivingscore;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;

import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.component.base.BaseApp;
import com.xiaoma.drivingscore.main.ui.MainActivity;
import com.xiaoma.hotfix.HotfixConstants;
import com.xiaoma.hotfix.model.PatchConfig;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.model.User;
import com.xiaoma.network.XmHttp;
//import com.xiaoma.update.service.AppUpdateService;

/**
 * @author wutao
 * @date 2019/1/7
 */
@SuppressWarnings("unused")
@DefaultLifeCycle(application = HotfixConstants.App.DriverScore, flags = ShareConstants.TINKER_ENABLE_ALL)
public class DriverScore extends BaseApp {
    public DriverScore(Application app, int flags, boolean verify, long appStartElapsedTime, long appStartMillisTime, Intent result) {
        super(app, flags, verify, appStartElapsedTime, appStartMillisTime, result);
    }

    @Override
    public void initLibs() {
        super.initLibs();
        XmHttp.getDefault().init(getApplication());
        LoginManager.getInstance().init(getApplication());
        UserManager.getInstance().init(getApplication());
        listenUserLoginStatus();
//        FaultFactory.getSDK().init(getApplication());
//        FaultFactory.getSDK().registerFaultListener(new FaultListener() {
//            @Override
//            public void onFaultOccur(List<CarFault> carFault) {
////                MainActivity.NewFault(getApplication(), new ArrayList<>(carFault));
//            }
//        });
//        initAppUpdate();
        initAutoTracker();
    }

    private void initAutoTracker() {
        XmAutoTracker.getInstance().init(getApplication());
    }

//    private void initAppUpdate() {
//        Intent intent = new Intent(getApplication(), AppUpdateService.class);
//        intent.putExtra(AppUpdateService.ARGS_PACKAGE_NAME, getApplication().getPackageName());
//        getApplication().startService(intent);
//    }

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
            }

            @Override
            public void onLogout() {
                XmHttp.getDefault().addCommonParams("uid", "");
            }
        });
    }
}
