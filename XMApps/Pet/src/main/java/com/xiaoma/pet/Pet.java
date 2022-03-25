package com.xiaoma.pet;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.component.base.BaseApp;
import com.xiaoma.guide.utils.GuideToast;
import com.xiaoma.hotfix.HotfixConstants;
import com.xiaoma.hotfix.model.PatchConfig;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.model.User;
import com.xiaoma.network.XmHttp;
import com.xiaoma.pet.common.manager.PetAssetManager;
import com.xiaoma.pet.ui.PetSplashActivity;
import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.skin.utils.SkinUtils;
import com.xiaoma.ui.toast.XMToast;

//import com.xiaoma.update.service.AppUpdateService;

/**
 * @author wutao
 * @date 2019/1/7
 */
@SuppressWarnings("unused")
@DefaultLifeCycle(application = HotfixConstants.App.Pet, flags = ShareConstants.TINKER_ENABLE_ALL)
public class Pet extends BaseApp {
    public Pet(Application app, int flags, boolean verify, long appStartElapsedTime, long appStartMillisTime, Intent result) {
        super(app, flags, verify, appStartElapsedTime, appStartMillisTime, result);
    }

    @Override
    public void initLibs() {
        super.initLibs();
        XmHttp.getDefault().init(getApplication());
        LoginManager.getInstance().init(getApplication(), false);
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
        PetAssetManager.init(getApplication());
        XmSkinManager.getInstance().initSkin(getApplication());
        SkinUtils.loadSkin(getApplication());
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

    @Override
    protected String[] allNeedPermissions() {
        return new String[]{};
    }

    @Override
    public Class<?> firstActivity() {
        return PetSplashActivity.class;
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

}
