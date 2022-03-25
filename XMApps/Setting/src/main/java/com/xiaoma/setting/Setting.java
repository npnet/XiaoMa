package com.xiaoma.setting;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.manager.CarServiceConnManager;
import com.xiaoma.carlib.manager.CarServiceListener;
import com.xiaoma.center.logic.local.Center;
import com.xiaoma.component.base.BaseApp;
import com.xiaoma.db.DBManager;
import com.xiaoma.hotfix.HotfixConstants;
import com.xiaoma.hotfix.model.PatchConfig;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.model.User;
import com.xiaoma.network.XmHttp;
import com.xiaoma.setting.bluetooth.service.BluetoothServiceManager;
import com.xiaoma.setting.client.SettingClient;
import com.xiaoma.setting.main.ui.MainActivity;
import com.xiaoma.setting.practice.SettingSkillManager;
import com.xiaoma.setting.service.SettingService;
import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.skin.utils.SkinUtils;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.tts.EventTtsManager;

import static com.xiaoma.setting.common.constant.ClientConstants.SETTING_PHONE_PORT;

@SuppressWarnings("unused")
@DefaultLifeCycle(application = HotfixConstants.App.Setting, flags = ShareConstants.TINKER_ENABLE_ALL)
public class Setting extends BaseApp {
    private static Setting instance;

    public Setting(Application app, int flags, boolean verify, long appStartElapsedTime, long appStartMillisTime, Intent result) {
        super(app, flags, verify, appStartElapsedTime, appStartMillisTime, result);
    }

    @Override
    public void onCreate(boolean isMainProcess) {
        super.onCreate(isMainProcess);
        instance = this;
        initCenter();
    }

    @Override
    public void initLibs() {
        super.initLibs();
        XmHttp.getDefault().init(getApplication());
        LoginManager.getInstance().init(getApplication(), false);
        UserManager.getInstance().init(getApplication());
        listenUserLoginStatus();
        XmCarFactory.init(getApplication());
        initDB();
        initAutoTracker();
        EventTtsManager.getInstance().init(getApplication());
        XmSkinManager.getInstance().initSkin(getApplication());
        SkinUtils.loadSkin(getApplication());
        SettingSkillManager.getInstance().init(getApplication());
        BluetoothServiceManager.getInstance().init(getApplication());
        CarServiceConnManager.getInstance(getApplication()).addCallBack(new CarServiceListener() {
            @Override
            public void onCarServiceConnected(IBinder binder) {
                KLog.e("car", " onCarServiceConnected  ");
                Intent startIntent = new Intent(getApplication(), SettingService.class);
                getApplication().startService(startIntent);
            }

            @Override
            public void onCarServiceDisconnected() {

            }
        });
    }


    private void initDB() {
        DBManager.getInstance().with(getApplication());
        DBManager.getInstance().initGlobalDB();
        DBManager.getInstance().initUserDB(LoginManager.getInstance().getLoginUserId());
    }

    @Override
    protected String[] allNeedPermissions() {
        return new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
    }

    @Override
    public Class<?> firstActivity() {
        return MainActivity.class;
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

    public static Context getContext() {
        return instance.getApplication();
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

    private void initCenter() {
        KLog.d("ljb", "initCenter");
        if (!Center.getInstance().isConnected()) {
            boolean init = Center.getInstance().init(getApplication());
            KLog.d("ljb", "init:" + init);
        }
        Center.getInstance().runAfterConnected(new Runnable() {
            @Override
            public void run() {
                SettingClient settingClient = new SettingClient(getContext(), SETTING_PHONE_PORT);
                boolean register = Center.getInstance().register(settingClient);
                KLog.d("register state: " + register);
            }
        });
    }
}
