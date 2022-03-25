package com.xiaoma.carpark;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.text.TextUtils;

import com.qihoo360.replugin.RePlugin;
import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.carpark.main.ui.MainActivity;
import com.xiaoma.carpark.webview.manager.WebviewManager;
import com.xiaoma.component.base.BaseApp;
import com.xiaoma.db.DBManager;
import com.xiaoma.guide.utils.GuideToast;
import com.xiaoma.hotfix.HotfixConstants;
import com.xiaoma.hotfix.model.PatchConfig;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.model.User;
import com.xiaoma.network.XmHttp;
import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.skin.utils.SkinUtils;
import com.xiaoma.ui.toast.XMToast;


/**
 * Created by Thomas on 2018/11/5 0005
 */
@SuppressWarnings("unused")
@DefaultLifeCycle(application = HotfixConstants.App.CarPark, flags = ShareConstants.TINKER_ENABLE_ALL)
public class CarPark extends BaseApp {

    public CarPark(Application app, int flags, boolean verify, long appStartElapsedTime, long appStartMillisTime, Intent result) {
        super(app, flags, verify, appStartElapsedTime, appStartMillisTime, result);
    }


    @Override
    public void initLibs() {
        super.initLibs();
        if ("CAR".equals(BuildConfig.BUILD_PLATFORM)) {
            WebviewManager.hookWebView();
        }
        XmHttp.getDefault().init(getApplication());
        LoginManager.getInstance().init(getApplication(), false);
        UserManager.getInstance().init(getApplication());
        listenUserLoginStatus();
        DBManager.getInstance().with(getApplication());
        DBManager.getInstance().initGlobalDB();
        DBManager.getInstance().initUserDB(LoginManager.getInstance().getLoginUserId());
        initRePlugin();
        initAutoTracker();
        XmSkinManager.getInstance().init(getApplication());
        SkinUtils.loadSkin(getApplication());
//        initWebView();

    }

    private void initAutoTracker() {
        if (!TextUtils.isEmpty(LoginManager.getInstance().getLoginUserId())) {
            XmAutoTracker.getInstance().init(getApplication(), LoginManager.getInstance().getLoginUserId());
        }
    }

    @Override
    protected String[] allNeedPermissions() {
        return new String[0];
    }

    @Override
    public Class<?> firstActivity() {
        return MainActivity.class;
    }

    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        RePlugin.App.attachBaseContext(getApplication());
    }

    @Override
    protected void doKillSelfOnLowMemoryBefore() {
        RePlugin.App.onLowMemory();
    }

    @Override
    protected void doKillSelfOnTrimMemoryBefore(int level) {
        RePlugin.App.onTrimMemory(level);
    }

    @Override
    protected boolean needKillSelfOnLowMemory() {
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        RePlugin.App.onConfigurationChanged(newConfig);
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
                DBManager.getInstance().onUserLogout();
            }
        });
    }

    private void initRePlugin() {
        RePlugin.App.onCreate();
    }

//    private void initWebView(){
//        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
//
//        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {
//
//            @Override
//            public void onViewInitFinished(boolean arg0) {
//                // TODO Auto-generated method stub
//                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
//                Log.d("app", " onViewInitFinished is " + arg0);
//            }
//
//            @Override
//            public void onCoreInitFinished() {
//                // TODO Auto-generated method stub
//            }
//        };
//        //x5内核初始化接口
//        QbSdk.initX5Environment(getApplication(),  cb);
//    }
}
