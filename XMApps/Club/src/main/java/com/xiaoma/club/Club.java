package com.xiaoma.club;

import android.app.ActivityManager;
import android.app.Application;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.chat.EMClient;
import com.mapbar.xiaoma.manager.XmMapNaviManager;
import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.carlib.wheelcontrol.XmWheelManager;
import com.xiaoma.club.common.constant.ClubConstants;
import com.xiaoma.club.common.hyphenate.IMUtils;
import com.xiaoma.club.common.repo.ClubRepo;
import com.xiaoma.club.common.service.ClubNotificationService;
import com.xiaoma.club.common.service.ClubService;
import com.xiaoma.club.common.ui.MainActivity;
import com.xiaoma.club.common.util.UserUtil;
import com.xiaoma.component.base.BaseApp;
import com.xiaoma.db.DBManager;
import com.xiaoma.guide.utils.GuideToast;
import com.xiaoma.hotfix.HotfixConstants;
import com.xiaoma.hotfix.model.PatchConfig;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.login.common.LoginConstants;
import com.xiaoma.model.User;
import com.xiaoma.network.XmHttp;
import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.skin.utils.SkinUtils;
import com.xiaoma.ui.toast.XMToast;

import org.simple.eventbus.EventBus;

import java.util.Objects;

import static com.xiaoma.club.common.util.LogUtil.logE;

/**
 * Created by LKF on 2018/9/25 0025.
 */
@SuppressWarnings("unused")
@DefaultLifeCycle(application = HotfixConstants.App.Club, flags = ShareConstants.TINKER_ENABLE_ALL)
public class Club extends BaseApp {
    private static final String TAG = "Club";

    public Club(Application app, int flags, boolean verify, long appStartElapsedTime, long appStartMillisTime, Intent result) {
        super(app, flags, verify, appStartElapsedTime, appStartMillisTime, result);
    }

    @Override
    public void onCreate(boolean isMainProcess) {
        super.onCreate(isMainProcess);
        ClubRepo.init(getApplication());
    }

    @Override
    public void initLibs() {
        super.initLibs();
        final Application app = getApplication();
        XmHttp.getDefault().init(app);
        LoginManager.getInstance().init(app);
        UserManager.getInstance().init(app);
        listenUserLoginStatus(app);
        initDB(app);
        initHx(app);
        initAutoTracker();
        XmWheelManager.getInstance().init(app);
        XmSkinManager.getInstance().initSkin(app);
        SkinUtils.loadSkin(getApplication());
        XmMapNaviManager.getInstance().init(app);
    }

    private void initAutoTracker() {
        if (!TextUtils.isEmpty(LoginManager.getInstance().getLoginUserId())) {
            XmAutoTracker.getInstance().init(getApplication(), LoginManager.getInstance().getLoginUserId());
        }
    }

    private void initDB(Context context) {
        DBManager.getInstance().with(context);
        DBManager.getInstance().initGlobalDBCascade();
        DBManager.getInstance().initUserDBCascade(LoginManager.getInstance().getLoginUserId());
    }

    private void startServices(Context context) {
        context.startService(new Intent(context, ClubService.class));
        context.startService(new Intent(context, ClubNotificationService.class));
    }

    private void stopServices(Context context) {
        context.stopService(new Intent(context, ClubService.class));
        context.stopService(new Intent(context, ClubNotificationService.class));
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
    protected PatchConfig obtainPatchConfig() {
        String baseVersion = BuildConfig.TINKER_BASE_VERSION;
        int patchVersion = BuildConfig.TINKER_PATCH_VERSION;
        PatchConfig patchConfig = new PatchConfig();
        patchConfig.setBasePkgVersion(baseVersion);
        patchConfig.setPatchVersion(patchVersion);
        return patchConfig;
    }

    private void initHx(final Context context) {
        IMUtils.hxImInit(context, false);
        // 环信登录状态变化时,控制服务的启动和关闭逻辑,避免账号切换后不重新推送消息
        EMClient.getInstance().addConnectionListener(new EMConnectionListener() {
            @Override
            public void onConnected() {
                logE(TAG, "onConnected()");
                startServices(context);
            }

            @Override
            public void onDisconnected(int error) {
                logE(TAG, "onDisconnected( error: %s )", error);
                stopServices(context);
                // 如果当前处于已登录状态,断线后尝试重新登录
                handleLoginOnHx();
            }
        });
        handleLoginOnHx();
    }

    private void handleLoginOnHx() {
        if (LoginManager.getInstance().isUserLogin()) {
            final User user = UserUtil.getCurrentUser();
            if (user != null) {
                IMUtils.loginHx(user.getHxAccount(), user.getHxPassword(), null);
                logE(TAG, "handleLoginOnHx -> loginHx");
            } else {
                logE(TAG, "handleLoginOnHx -> XM has login, but User is null !");
            }
        } else {
            logE(TAG, "handleLoginOnHx -> XM didn't login !");
        }
    }

    private void listenUserLoginStatus(final Context context) {
        if (LoginManager.getInstance().isUserLogin()) {
            XmHttp.getDefault().addCommonParams("uid", LoginManager.getInstance().getLoginUserId());
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LoginConstants.ACTION_ON_LOGIN);
        intentFilter.addAction(LoginConstants.ACTION_ON_LOGOUT);
        intentFilter.addAction(LoginConstants.ACTION_ON_USER_UPDATE);
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // 登录状态变化之后重启App,避免残留上一个用户的脏数据
                String action = intent.getAction();
                if (LoginConstants.ACTION_ON_LOGIN.equals(action)) {
                    logE(TAG, "ACTION_ON_LOGIN");
                    logoutAndRestart();
                } else if (LoginConstants.ACTION_ON_LOGOUT.equals(action)) {
                    logE(TAG, "ACTION_ON_LOGOUT");
                    logoutAndRestart();
                } else if (LoginConstants.ACTION_ON_USER_UPDATE.equals(action)) {
                    logE(TAG, "ACTION_ON_USER_UPDATE");
                    try {
                        User u = intent.getParcelableExtra("userId");
                        String newHxAccount = u.getHxAccount();
                        String curHxAccount = EMClient.getInstance().getCurrentUser();
                        logE(TAG, "ACTION_ON_USER_UPDATE [ newHxAccount: %s, curHxAccount: %s ]", newHxAccount, curHxAccount);
                        // 如果发现当前用户的环信id和环信侧的不一致,侧重启应用,避免登录状态错乱
                        if (!Objects.equals(curHxAccount, newHxAccount)) {
                            logoutAndRestart();
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }

            private void logoutAndRestart() {
                IMUtils.loginOutHx(new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        logE(TAG, "ACTION_ON_LOGOUT : Success");
                    }

                    @Override
                    public void onError(int i, String s) {
                        logE(TAG, "ACTION_ON_LOGOUT : Error( %s, %s )", i, s);
                    }

                    @Override
                    public void onProgress(int i, String s) {
                        logE(TAG, "ACTION_ON_LOGOUT : onProgress( %s, %s )", i, s);
                    }
                });
                try {
                    ((ActivityManager) context.getSystemService(Service.ACTIVITY_SERVICE))
                            .restartPackage(context.getPackageName());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }, intentFilter);
    }

    @Override
    protected void exitApp() {
        //用Process.killProcess无法立即杀死车友圈app，用moveTaskToBack代替
        EventBus.getDefault().post("", ClubConstants.MOVE_TASK_TO_BACK);
    }

    @Override
    protected boolean needKillSelfOnLowMemory() {
        return true;
    }

    @Override
    protected void onAppIntoBackground() {
        XMToast.cancelToast();
        GuideToast.cancelToast();
    }
}
