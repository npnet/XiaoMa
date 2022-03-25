package com.xiaoma.service;

import android.Manifest;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.mapbar.xiaoma.manager.XmMapNaviManager;
import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.wheelcontrol.XmWheelManager;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.local.Center;
import com.xiaoma.component.AppHolder;
import com.xiaoma.component.base.BaseApp;
import com.xiaoma.db.DBManager;
import com.xiaoma.gdmap.XmMapManager;
import com.xiaoma.guide.utils.GuideToast;
import com.xiaoma.hotfix.HotfixConstants;
import com.xiaoma.hotfix.model.PatchConfig;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.model.User;
import com.xiaoma.network.XmHttp;
import com.xiaoma.service.common.constant.ServiceConstants;
import com.xiaoma.service.common.manager.CarDataManager;
import com.xiaoma.service.common.manager.IBCallManager;
import com.xiaoma.service.main.ui.MainActivity;
import com.xiaoma.skin.constant.SkinConstants;
import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.skin.receiver.SkinReceiver;
import com.xiaoma.skin.utils.SkinUtils;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.vr.dispatch.DispatchManager;

import org.simple.eventbus.EventBus;

/**
 * Created by Thomas on 2018/11/12 0012
 */

@SuppressWarnings("unused")
@DefaultLifeCycle(application = HotfixConstants.App.Service, flags = ShareConstants.TINKER_ENABLE_ALL)
public class Service extends BaseApp {

    public Service(Application app, int flags, boolean verify, long appStartElapsedTime, long appStartMillisTime, Intent result) {
        super(app, flags, verify, appStartElapsedTime, appStartMillisTime, result);
    }

    @Override
    public void initLibs() {
        super.initLibs();
        registerCallReceiver();
        XmHttp.getDefault().init(getApplication());
        LoginManager.getInstance().init(getApplication(), false);
        UserManager.getInstance().init(getApplication());
        listenUserLoginStatus();
        XmCarFactory.init(getApplication());
        DBManager.getInstance().with(getApplication());
        DBManager.getInstance().initGlobalDB();
        DBManager.getInstance().initUserDB(LoginManager.getInstance().getLoginUserId());
        initXmMapManager();
        initAutoTracker();
        XmMapNaviManager.getInstance().init(getApplication());
        XmSkinManager.getInstance().initSkin(getApplication());
        DispatchManager.getInstance().init(getApplication());
        Center.getInstance().init(getApplication());
        XmWheelManager.getInstance().init(getApplication());
        initSkinReciver();
        SkinUtils.loadSkin(getApplication());
    }


    private void initAutoTracker() {
        if (!TextUtils.isEmpty(LoginManager.getInstance().getLoginUserId())) {
            XmAutoTracker.getInstance().init(getApplication(), LoginManager.getInstance().getLoginUserId());
        }
    }

    @Override
    protected String[] allNeedPermissions() {
        return new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.USE_SIP
        };
    }

    @Override
    public Class<?> firstActivity() {
        return MainActivity.class;
    }

    private void initXmMapManager() {
        if (!TextUtils.isEmpty(LoginManager.getInstance().getLoginUserId())) {
            XmMapManager.getInstance().init(getApplication(), LoginManager.getInstance().getLoginUserId());
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
                    initXmMapManager();
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

    private void initSkinReciver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SkinConstants.SKIN_ACTION);
        intentFilter.addAction(SkinConstants.SKIN_ACTION_DAOMENG);
        intentFilter.addAction(SkinConstants.SKIN_ACTION_QINGSHE);
        AppHolder.getInstance().getAppContext().registerReceiver(new SkinReceiver(), intentFilter);
    }

    @Override
    protected boolean needKillSelfOnLowMemory() {
        if (IBCallManager.getInstance().isIBCall()) {
            return false;
        }
        return true;
    }

    private void registerCallReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CenterConstants.IN_A_CALL);
        intentFilter.addAction(CenterConstants.END_OF_CALL);
        intentFilter.addAction(CenterConstants.INCOMING_CALL);
        AppHolder.getInstance().getAppContext().registerReceiver(callReceiver, intentFilter);
    }


    private BroadcastReceiver callReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (CenterConstants.IN_A_CALL.equals(intent.getAction())) {
                CarDataManager.getInstance().setBluetoothCall(true);
                if (IBCallManager.getInstance().isIBCall()) {
                    EventBus.getDefault().post(true, ServiceConstants.BLUETOOTH_CALL);
                }
            } else if (CenterConstants.END_OF_CALL.equals(intent.getAction())) {
                CarDataManager.getInstance().setBluetoothCall(false);
                if (IBCallManager.getInstance().isIBCall()) {
                    EventBus.getDefault().post(false, ServiceConstants.BLUETOOTH_CALL);
                }
            } else if (CenterConstants.INCOMING_CALL.equals(intent.getAction())) {
                CarDataManager.getInstance().setBluetoothCall(true);
                if (IBCallManager.getInstance().isIBCall()) {
                    EventBus.getDefault().post("", ServiceConstants.INCOMING_CALL);
                }
            } else if (CenterConstants.WHEEL_HANGUP_IBCALL.equals(intent.getAction())) {
                //方控按下挂断
                EventBus.getDefault().post("", ServiceConstants.WHEEL_HANGUP_IBCALL);
            }
        }
    };
}
