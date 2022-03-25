package com.xiaoma.bluetooth.phone;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.bluetooth.phone.common.factory.BlueToothPhoneManagerFactory;
import com.xiaoma.bluetooth.phone.common.manager.AudioFocusManager;
import com.xiaoma.bluetooth.phone.common.manager.FocusManager;
import com.xiaoma.bluetooth.phone.common.manager.IBCallStateManager;
import com.xiaoma.bluetooth.phone.common.manager.CommonAudioFocusManager;
import com.xiaoma.bluetooth.phone.common.sdk.BluetoothPhoneClient;
import com.xiaoma.bluetooth.phone.common.utils.OperateUtils;
import com.xiaoma.bluetooth.phone.main.ui.MainActivity;
import com.xiaoma.carlib.wheelcontrol.XmWheelManager;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.local.Center;
import com.xiaoma.center.logic.local.StateManager;
import com.xiaoma.center.logic.model.SourceInfo;
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
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.iat.RemoteIatManager;
import com.xiaoma.vr.tts.EventTtsManager;

/**
 * @author: iSun
 * @date: 2018/11/16 0016
 */

@SuppressWarnings("unused")
@DefaultLifeCycle(application = HotfixConstants.App.BlueToothPhone, flags = ShareConstants.TINKER_ENABLE_ALL)
public class BlueToothPhone extends BaseApp {
    private static BlueToothPhone instance;
    private static Handler mHandler = new Handler();

    public BlueToothPhone(Application app, int flags, boolean verify, long appStartElapsedTime, long appStartMillisTime, Intent result) {
        super(app, flags, verify, appStartElapsedTime, appStartMillisTime, result);
    }

    public static Context getContext() {
        return instance.getApplication();
    }

    public static Handler getHandler() {
        return mHandler;
    }

    @Override
    public void onCreate(boolean isMainProcess) {
        super.onCreate(isMainProcess);
        instance = this;
        initCenter();
        BlueToothPhoneManagerFactory.getInstance();
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
        RemoteIatManager.getInstance().init(getApplication());
        XmWheelManager.getInstance().init(getApplication());
        EventTtsManager.getInstance().init(getApplication());
        XmSkinManager.getInstance().initSkin(getApplication());
        FocusManager.getInstance().init(getApplication());
        AudioFocusManager.getInstance().init(getApplication());
        CommonAudioFocusManager.getInstance().init(getApplication());
        IBCallStateManager.getInstance().init(getApplication());
        SkinUtils.loadSkin(getApplication());
    }

    private void initCenter() {
        if (!Center.getInstance().isConnected()) {
            initCenterService();
        } else {
            registerClient();
        }
        StateManager.getInstance().addStateCallback(new StateManager.StateListener() {
            @Override
            public void onPrepare(String depend) {
                Log.d("QBX", "onPrepare");
                initCenterService();
            }

            @Override
            public void onConnected() {
                Log.d("QBX", "registerClient");
                registerClient();
            }

            @Override
            public void onClientIn(SourceInfo source) {
                if (CenterConstants.ASSISTANT.equals(source.getLocation())
                        && BlueToothPhoneManagerFactory.getInstance().isContactBookSynchronized()) {
                    OperateUtils.upLoadContact(BlueToothPhoneManagerFactory.getInstance().getAllContact());
                }
            }
        });
    }

    private void initCenterService() {
        boolean init = Center.getInstance().init(getContext());
        KLog.d("init state: " + init);
    }

    private void registerClient() {
        boolean register = Center.getInstance().register(BluetoothPhoneClient.getInstance());
        KLog.d("register state: " + register);
    }

    private void initDB() {
        DBManager.getInstance().with(getApplication());
        DBManager.getInstance().initGlobalDBCascade();
    }

    private void initAutoTracker() {
        if (!TextUtils.isEmpty(LoginManager.getInstance().getLoginUserId())) {
            XmAutoTracker.getInstance().init(getApplication(), LoginManager.getInstance().getLoginUserId());
        }
    }

    @Override
    public Class<?> firstActivity() {
        return MainActivity.class;
    }

    @Override
    protected String[] allNeedPermissions() {
        return new String[]{};
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

    /*private CommonParameter genCommonParameter() {
        String channelID = ConfigManager.ApkConfig.getChannelID();
        String versionCode = String.valueOf(ConfigManager.ApkConfig.getBuildVersionCode());
        String userId = LoginManager.getInstance().isUserLogin()
                ? LoginManager.getInstance().getLoginUserId()
                : "";
        String iccid = ConfigManager.DeviceConfig.getICCID(getApplication());
        String imei = ConfigManager.DeviceConfig.getIMEI(getApplication());
        String osVersion = ConfigManager.DeviceConfig.getOSVersion();
        String deviceModel = ConfigManager.DeviceConfig.getDeviceModel();
        return new CommonParameter(
                channelID,
                versionCode,
                userId,
                iccid,
                imei,
                osVersion,
                deviceModel);
    }*/


    @Override
    protected void exitApp() {
        Intent startActivityIntent = new Intent(Intent.ACTION_MAIN);
        startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityIntent.addCategory(Intent.CATEGORY_HOME);
        getContext().startActivity(startActivityIntent);
    }
}

