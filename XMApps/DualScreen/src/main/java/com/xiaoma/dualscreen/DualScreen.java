package com.xiaoma.dualscreen;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;

import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.manager.CarServiceListener;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.carlib.wheelcontrol.XmWheelManager;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.local.Center;
import com.xiaoma.center.logic.local.StateManager;
import com.xiaoma.center.logic.model.SourceInfo;
import com.xiaoma.component.base.BaseApp;
import com.xiaoma.dualscreen.eol.EOLScreenClient;
import com.xiaoma.dualscreen.manager.DualBluetoothPhoneApiManager;
import com.xiaoma.dualscreen.manager.DualBluetoothPhoneManager;
import com.xiaoma.dualscreen.manager.PlayerAudioManager;
import com.xiaoma.dualscreen.service.PresentationService;
import com.xiaoma.dualscreen.ui.MainActivity;
import com.xiaoma.hotfix.HotfixConstants;
import com.xiaoma.hotfix.model.PatchConfig;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.model.User;
import com.xiaoma.network.XmHttp;
import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.log.KLog;

/**
 * @author: iSun
 * @date: 2018/12/19 0019
 */
@SuppressWarnings("unused")
@DefaultLifeCycle(application = HotfixConstants.App.DualScreen, flags = ShareConstants.TINKER_ENABLE_ALL)
public class DualScreen extends BaseApp {

    private static final String TAG = DualScreen.class.getSimpleName();

    public DualScreen(Application app, int flags, boolean verify, long appStartElapsedTime, long appStartMillisTime, Intent result) {
        super(app, flags, verify, appStartElapsedTime, appStartMillisTime, result);
    }

    @Override
    public void onCreate(boolean isMainProcess) {
        super.onCreate(isMainProcess);
        new Handler().postDelayed(startServiceRunnable, 3000);
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
    public void initLibs() {
        super.initLibs();
        XmCarVendorExtensionManager.getInstance().addCarServiceListener(new CarServiceListener() {
            @Override
            public void onCarServiceConnected(IBinder binder) {
                KLog.d("CarService connect now");
                // 仪表皮肤同步
                ThreadDispatcher.getDispatcher().removeOnMain(startServiceRunnable);
                ThreadDispatcher.getDispatcher().postOnMain(startServiceRunnable);
                XmCarVendorExtensionManager.getInstance().removeCarServiceListener(this);
            }

            @Override
            public void onCarServiceDisconnected() {
            }
        });
        XmHttp.getDefault().init(getApplication());
        LoginManager.getInstance().init(getApplication(), false);
        UserManager.getInstance().init(getApplication());
        listenUserLoginStatus();
        initAutoTracker();
        XmSkinManager.getInstance().initSkin(getApplication());
        XmCarFactory.init(getApplication());
        XmWheelManager.getInstance().init(getApplication());
        initCenter();
//        initMap();
    }

    private void initCenter() {
        KLog.e("Center.getInstance().isConnected() : " + Center.getInstance().isConnected());
        if (!Center.getInstance().isConnected()) {
            boolean initStatus = Center.getInstance().init(getApplication());
            KLog.e("Center.getInstance().init(getApplication() : " + initStatus);
        }
        DualBluetoothPhoneApiManager.getInstance().init(getApplication());  //先设置context避免崩
        Center.getInstance().runAfterConnected(new Runnable() {
            @Override
            public void run() {
                //注册音乐
                KLog.e("注册音乐,注册蓝牙电话");
                PlayerAudioManager.getInstance().connectSourceInfos(getApplication());
                PlayerAudioManager.getInstance().addStateCallBack(getApplication());
                //注册蓝牙电话
                DualBluetoothPhoneApiManager.getInstance().init(getApplication(), CenterConstants.DUALSCREEN_PORT);
                DualBluetoothPhoneApiManager.getInstance().init();
                DualBluetoothPhoneManager.getInstance().init(getApplication());
                //注册EOLClient
                Center.getInstance().register(EOLScreenClient.newSingleton());
            }
        });

        StateManager.getInstance().addStateCallback(new StateManager.StateListener() {
            @Override
            public void onClientIn(SourceInfo source) {
                StateManager.getInstance().removeCallback(this);
                DualBluetoothPhoneApiManager.getInstance().init(getApplication(),CenterConstants.DUALSCREEN_PORT);
                DualBluetoothPhoneApiManager.getInstance().init();
                DualBluetoothPhoneManager.getInstance().init(getApplication());
            }

            @Override
            public void onBindService(boolean bindState) {
                super.onBindService(bindState);
            }
        });
    }


    private void initAutoTracker() {
        if (!TextUtils.isEmpty(LoginManager.getInstance().getLoginUserId())) {
            XmAutoTracker.getInstance().init(getApplication(), LoginManager.getInstance().getLoginUserId());
        }
    }

    Runnable startServiceRunnable = new Runnable() {
        @Override
        public void run() {
            Intent startIntent = new Intent(getApplication(), PresentationService.class);
            getApplication().startService(startIntent);
        }
    };


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
