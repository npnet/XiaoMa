package com.xiaoma.app;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.xiaoma.app.common.constant.AppStoreConstants;
import com.xiaoma.app.listener.NetworkChangedReceiver;
import com.xiaoma.app.model.AppStateEvent;
import com.xiaoma.app.ui.activity.MainActivity;
import com.xiaoma.app.usb.UsbConnectStateListenerImp;
import com.xiaoma.app.usb.UsbMediaDataManager;
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
import com.xiaoma.network.db.DownloadManager;
import com.xiaoma.network.model.Progress;
import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.skin.utils.SkinUtils;
import com.xiaoma.ui.toast.XMToast;

import org.simple.eventbus.EventBus;

import java.util.List;

import static android.content.Intent.ACTION_PACKAGE_ADDED;

/**
 * Created by LKF on 2018/9/25 0025
 */

@DefaultLifeCycle(application = HotfixConstants.App.AppStore, flags = ShareConstants.TINKER_ENABLE_ALL)
public class AppStore extends BaseApp {

    public AppStore(Application app, int flags, boolean verify, long appStartElapsedTime, long appStartMillisTime, Intent result) {
        super(app, flags, verify, appStartElapsedTime, appStartMillisTime, result);
    }

    @Override
    public void initLibs() {
        super.initLibs();
        XmHttp.getDefault().init(getApplication());
        LoginManager.getInstance().init(getApplication(), false);
        UserManager.getInstance().init(getApplication());
        listenUserLoginStatus();
        initSilenceService();
        initDB();
        registerNetworkChangedReceiver();
        registerAppInstallReceiver();
        initAutoTracker();
        XmSkinManager.getInstance().initSkin(getApplication());
        SkinUtils.loadSkin(getApplication());
        initUsbManager();
    }

    @Override
    public Class<?> firstActivity() {
        return MainActivity.class;
    }

    @Override
    protected String[] allNeedPermissions() {
        return new String[]{
        };
    }

    @Override
    protected void onAppIntoBackground() {
        XMToast.cancelToast();
        GuideToast.cancelToast();
    }

    private void initAutoTracker() {
        if (!TextUtils.isEmpty(LoginManager.getInstance().getLoginUserId())) {
            XmAutoTracker.getInstance().init(getApplication(), LoginManager.getInstance().getLoginUserId());
        }
    }

    /**
     * 网络改变监听
     */
    private void registerNetworkChangedReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        NetworkChangedReceiver networkChangeReceiver = new NetworkChangedReceiver();
        getApplication().registerReceiver(networkChangeReceiver, intentFilter);
    }

    private void initSilenceService() {
        SilentInstallManager.getInstance().initService(getApplication());
    }

    private void registerAppInstallReceiver() {
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addDataScheme("package");
        mIntentFilter.addAction(ACTION_PACKAGE_ADDED);
        mIntentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        mIntentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        getApplication().registerReceiver(appInstallReceiver, mIntentFilter);
    }

    private BroadcastReceiver appInstallReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String packageName = intent.getData().getSchemeSpecificPart();
            String action = intent.getAction();
            AppStateEvent stateEvent = new AppStateEvent();
            stateEvent.setResult(true);
            stateEvent.setPackageName(packageName);
            //安装
            if (TextUtils.equals(action, Intent.ACTION_PACKAGE_ADDED)) {
                stateEvent.setAppState(AppStateEvent.STATE_PACKAGE_ADDED);

                //替换
            } else if (TextUtils.equals(action, Intent.ACTION_PACKAGE_REPLACED)) {
                stateEvent.setAppState(AppStateEvent.STATE_PACKAGE_REPLACED);

                //卸载
            } else if (TextUtils.equals(action, Intent.ACTION_PACKAGE_REMOVED)) {
                stateEvent.setAppState(AppStateEvent.STATE_PACKAGE_REMOVED);
            }
            //避免车机上安装卸载时发送这个消息
            if ("PAD".equals(BuildConfig.BUILD_PLATFORM)) {
                EventBus.getDefault().post(stateEvent, AppStoreConstants.APP_INSTALL_RECEIVER);
            }
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

    private void initDB() {
        DBManager.getInstance().with(getApplication());
        DBManager.getInstance().initGlobalDB();
        DBManager.getInstance().initUserDB(LoginManager.getInstance().getLoginUserId());
    }

    private void initUsbManager() {
        UsbMediaDataManager.getInstance().init(getApplication());
        UsbMediaDataManager.getInstance().syncUsbConnectState(getApplication(), new UsbConnectStateListenerImp(getApplication()));
    }

    @Override
    protected boolean needKillSelfOnLowMemory() {
        List<Progress> downloadingProgress = DownloadManager.getInstance().getDownloading();
        if(downloadingProgress == null ||downloadingProgress.size() == 0){
            return true;
        }
        return false;
    }
}
