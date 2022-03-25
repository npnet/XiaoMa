package com.qiming.fawcard.synthesize.base.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.qiming.fawcard.synthesize.BuildConfig;
import com.qiming.fawcard.synthesize.base.constant.QMConstant;
import com.qiming.fawcard.synthesize.base.system.callback.MyLifecycleHandler;
import com.qiming.fawcard.synthesize.core.drivescore.DriveScoreHomeActivity;
import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.xiaoma.component.base.BaseApp;
import com.xiaoma.hotfix.HotfixConstants;
import com.xiaoma.hotfix.model.PatchConfig;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.network.XmHttp;
import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.skin.utils.SkinUtils;

@SuppressWarnings("unused")
@DefaultLifeCycle(application = HotfixConstants.App.DrivingScoreQM, flags = ShareConstants.TINKER_ENABLE_ALL)
public class QmApplication extends BaseApp {
    private static int engineStatus = QMConstant.DEFAULT_ENGINE_STATUS; // // 默认（刚开机就是此默认状态 点火后为1 熄火后为0）

    private static Context context;

    public QmApplication(Application app, int flags, boolean verify, long appStartElapsedTime, long appStartMillisTime, Intent result) {
        super(app, flags, verify, appStartElapsedTime, appStartMillisTime, result);
    }

    public static Context getContext() {
        return context;
    }

    @Override
    public void initLibs() {
        super.initLibs();
        XmHttp.getDefault().init(getApplication());
        XmSkinManager.getInstance().initSkin(getApplication());
        SkinUtils.loadSkin(getApplication());
        LoginManager.getInstance().init(getApplication());
        UserManager.getInstance().init(getApplication());
    }

    @Override
    public void onCreate(boolean isMainProcess) {
        super.onCreate(isMainProcess);
        context = getApplication().getApplicationContext();
        getApplication().registerActivityLifecycleCallbacks(new MyLifecycleHandler());
    }

    @Override
    protected String[] allNeedPermissions() {
        return new String[0];
    }

    @Override
    public Class<?> firstActivity() {
        return DriveScoreHomeActivity.class;
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

    public static void setEngineStatus(int status){
        engineStatus = status;
    }

    public static int getEngineStatus(){
        return engineStatus;
    }
}
