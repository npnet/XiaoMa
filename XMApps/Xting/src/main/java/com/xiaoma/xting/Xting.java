package com.xiaoma.xting;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioFocusInfo;
import android.media.AudioManager;
import android.media.audiopolicy.AudioPolicy;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.wheelcontrol.XmWheelManager;
import com.xiaoma.component.AppHolder;
import com.xiaoma.component.base.BaseApp;
import com.xiaoma.db.DBManager;
import com.xiaoma.gdmap.XmMapManager;
import com.xiaoma.guide.utils.GuideToast;
import com.xiaoma.hotfix.HotfixConstants;
import com.xiaoma.hotfix.model.PatchConfig;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.login.common.LoginConstants;
import com.xiaoma.login.common.LoginMethod;
import com.xiaoma.model.User;
import com.xiaoma.network.XmHttp;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.skin.utils.SkinUtils;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.KeyEventUtils;
import com.xiaoma.utils.apptool.AppObserver;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.logintype.callback.AbsClearDataListener;
import com.xiaoma.utils.logintype.constant.LoginTypeConstant;
import com.xiaoma.utils.logintype.manager.LoginTypeManager;
import com.xiaoma.utils.logintype.receiver.CleanDataBroadcastReceiver;
import com.xiaoma.vr.iat.RemoteIatManager;
import com.xiaoma.vr.model.AppType;
import com.xiaoma.vr.tts.EventTtsManager;
import com.xiaoma.xting.common.XtingConstants;
import com.xiaoma.xting.common.XtingUtils;
import com.xiaoma.xting.common.playerSource.control.AudioFocusManager;
import com.xiaoma.xting.common.playerSource.info.callback.YQRadioPlayerStatusListenerImpl;
import com.xiaoma.xting.common.playerSource.info.sharedPref.SharedPrefUtils;
import com.xiaoma.xting.common.playerSource.utils.PrintInfo;
import com.xiaoma.xting.koala.KoalaFactory;
import com.xiaoma.xting.koala.KoalaPlayer;
import com.xiaoma.xting.launcher.AudioShareManager;
import com.xiaoma.xting.practice.manager.XtingSkillManager;
import com.xiaoma.xting.sdk.AcrFactory;
import com.xiaoma.xting.sdk.LocalFMFactory;
import com.xiaoma.xting.sdk.OnlineFMFactory;
import com.xiaoma.xting.sdk.OnlineFMPlayerFactory;
import com.xiaoma.xting.search.model.SearchBean;
import com.xiaoma.xting.wheelControl.WheelCarControlHelper;

/**
 * @author youthyJ
 * @date 2018/9/29
 */
@SuppressWarnings("unused")
@DefaultLifeCycle(application = HotfixConstants.App.Xting, flags = ShareConstants.TINKER_ENABLE_ALL)
public class Xting extends BaseApp {

    public Xting(Application app, int flags, boolean verify, long appStartElapsedTime, long appStartMillisTime, Intent result) {
        super(app, flags, verify, appStartElapsedTime, appStartMillisTime, result);
    }

    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        MultiDex.install(base.getApplicationContext());
    }

    @Override
    public void initLibs() {
        super.initLibs();
        XmHttp.getDefault().init(getApplication());
        initDB();
        LoginManager.getInstance().init(getApplication(), false);
        UserManager.getInstance().init(getApplication());
        listenUserLoginStatus();
        KoalaFactory.getSDK().init(getApplication());
        LocalFMFactory.getSDK().init(getApplication());
        OnlineFMFactory.getInstance().getSDK().init(getApplication());
        OnlineFMPlayerFactory.getPlayer().init(getApplication());
//        LauncherAudioShareWrapper.getInstance().init(getApplication());
        AudioShareManager.newSingleton().init(getApplication());
        if (XtingConstants.LOCAL_FM_TO_XMLY_TOGGLE) {
            AcrFactory.getInstance().getSDK().init(getApplication());
        }
        initXmMapManager();
        initAutoTracker();
        XmWheelManager.getInstance().init(getApplication());
        WheelCarControlHelper.newSingleton().init(getApplication());
        registerAudioPolicy();
        XmCarFactory.init(getApplication());
        XmSkinManager.getInstance().initSkin(getApplication());
        SkinUtils.loadSkin(getApplication());
        registerClearDataListener();
        XtingSkillManager.getInstance().init(getApplication());
        RemoteIatManager.getInstance().init(getApplication());
        EventTtsManager.getInstance().init(getApplication());
        AppObserver.getInstance().addAppStateChangedListener(new AppObserver.AppStateChangedListener() {
            @Override
            public void onForegroundChanged(boolean isForeground) {
                RemoteIatManager.getInstance().uploadAppState(isForeground, AppType.RADIO);

            }
        });
    }

    public static void clearExcludeUserId(Context context) {
        KLog.i("filOut| " + "[onSwitchUserClear]");
        SharedPrefUtils.clearCachedYQRadioLast();
        SharedPrefUtils.clearCachedPlayerInfo();
        OnlineFMPlayerFactory.getPlayer().clearPlayCache();
    }

    public static void clearAllData(Context context, String userId) {
        KLog.i("filOut| " + "[clearAllData]->" + userId);
        SearchBean.clearAllHistory(userId);//清理搜索记录
        PrintInfo.print("PlayerInfo", "Clear Record 2x");
        XtingUtils.getUserRoomDB(userId).getRecordDao().clear();//清理历史记录
        XtingUtils.getUserRoomDB(userId).getSubscribeDao().clear();// 清理收藏记录
    }

    private void registerClearDataListener() {
        CleanDataBroadcastReceiver cleanDataBroadcastReceiver = new CleanDataBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LoginTypeConstant.BROADCAST_ACTION_SWITCH_USER_CLEAR);
        getBaseContext(getApplication()).registerReceiver(cleanDataBroadcastReceiver, intentFilter);
        LoginTypeManager.getInstance().registerClearDataListener(new AbsClearDataListener() {
            @Override
            public void onSwitchUserClear(long currentUserId, String loginMethod) {
                if (LoginConstants.TOURIST_USER_ID == currentUserId
                        && LoginMethod.TOURISTS.name().equals(loginMethod)) {
                    Xting.clearAllData(getApplication(), String.valueOf(currentUserId));
                }
            }
        });
    }

    public void clearDataBeforeCloseProcess() {
        KLog.i("filOut| " + "[clearDataBeforeCloseProcess]");
        SharedPrefUtils.clearCachedPlayerInfo(AppHolder.getInstance().getAppContext());
        OnlineFMPlayerFactory.getPlayer().pause();
        OnlineFMPlayerFactory.getPlayer().resetPlayList();
        LocalFMFactory.getSDK().closeRadio();
        KoalaPlayer.newSingleton().pause();
        KoalaPlayer.newSingleton().clearPlayerList();
    }

    public void finishXting() {
        KLog.i("filOut| " + "[finishXting]");
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void registerAudioPolicy() {
        AudioManager audioManager = (AudioManager) getApplication().getSystemService(Context.AUDIO_SERVICE);
        AudioPolicy.Builder builder = new AudioPolicy.Builder(getApplication());
        builder.setAudioPolicyFocusListener(new AudioPolicy.AudioPolicyFocusListener() {
            @Override
            public void onAudioFocusGrant(AudioFocusInfo afi, int requestResult) {
                super.onAudioFocusGrant(afi, requestResult);
                if (afi != null && afi.getPackageName() != null) {
                    String packageName = afi.getPackageName();
                    Log.e("AUDIO_FOCUS", "onAudioFocusGrant: " + packageName);
                    if (packageName.equals("com.xiaoma.xting")) {
                        AudioFocusManager.getInstance().setLossByOther(false);
                        YQRadioPlayerStatusListenerImpl.newSingleton().setSwitchByExternal(false);
                        WheelCarControlHelper.newSingleton().register();
                        Bundle bundle = afi.getAttributes().getBundle();
                        if (bundle != null) {
                            String audioSource = bundle.getString(AudioFocusManager.ARG_AUDIO_SOURCE);
                            Log.d("AUDIO_FOCUS", "onAudioFocusGrant: " + audioSource);
                            switch (audioSource) {
                                case AudioFocusManager.ATTR_AUDIO_HIMALAYAN:
                                    AudioShareManager.newSingleton().shareAudioTypeChanged(AudioConstants.AudioTypes.XTING_NET_FM);
                                    break;
                                case AudioFocusManager.ATTR_AUDIO_KOALA:
                                    AudioShareManager.newSingleton().shareAudioTypeChanged(AudioConstants.AudioTypes.XTING_NET_FM);
                                    break;
                                case AudioFocusManager.ATTR_AUDIO_YQ:
                                    AudioShareManager.newSingleton().shareAudioTypeChanged(AudioConstants.AudioTypes.XTING_LOCAL_FM);
                                    break;
                                default:

                                    break;
                            }
                        }
                    } else {
                        YQRadioPlayerStatusListenerImpl.newSingleton().setSwitchByExternal(true);
                        AudioFocusManager.getInstance().setLossByOther(true);
                        WheelCarControlHelper.newSingleton().unRegister();
                    }
                }
            }

            @Override
            public void onAudioFocusLoss(AudioFocusInfo afi, boolean wasNotified) {
                super.onAudioFocusLoss(afi, wasNotified);
//                if (mOnWheelKeyListener != null) {
//                    XmWheelManager.getInstance().unregister(mOnWheelKeyListener);
//                    mOnWheelKeyListener = null;
//                }
                Log.e("AUDIO_FOCUS", "onAudioFocusLoss: " + afi.getPackageName());
            }

        });
        if (audioManager != null) {
            audioManager.registerAudioPolicy(builder.build());
        }
    }

    private void initDB() {
        DBManager.getInstance().with(getApplication());
        DBManager.getInstance().initGlobalDB();
        DBManager.getInstance().initUserDB(LoginManager.getInstance().getLoginUserId());
    }

    private void initAutoTracker() {
        if (!TextUtils.isEmpty(LoginManager.getInstance().getLoginUserId())) {
            XmAutoTracker.getInstance().init(getApplication(), LoginManager.getInstance().getLoginUserId());
        }
    }

    private void initXmMapManager() {
        if (!TextUtils.isEmpty(LoginManager.getInstance().getLoginUserId())) {
            XmMapManager.getInstance().init(getApplication(), LoginManager.getInstance().getLoginUserId());
        }
    }

    @Override
    public Class<?> firstActivity() {
        return MainActivity.class;
    }

    @Override
    protected String[] allNeedPermissions() {
        return new String[]{
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
    }

    @Override
    public void onCreate(boolean isMainProcess) {
        super.onCreate(isMainProcess);
    }

    private void listenUserLoginStatus() {
        if (LoginManager.getInstance().isUserLogin()) {
            XmHttp.getDefault().addCommonParams("uid", LoginManager.getInstance().getLoginUserId());
        }
        LoginManager.getInstance().addLoginEventListener(new LoginManager.LoginEventListener() {
            @Override
            public void onLogin(User data) {
                if (data != null) {
                    XmHttp.getDefault().addCommonParams("uid", String.valueOf(data.getId()));
                    DBManager.getInstance().initUserDB(data.getId());
                    initXmMapManager();
                    initAutoTracker();
                    XtingUtils.resetDataBase();
                }
            }

            @Override
            public void onLogout() {
                XmHttp.getDefault().addCommonParams("uid", "");
                DBManager.getInstance().onUserLogout();
            }
        });
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
    protected void exitApp() {
        KeyEventUtils.sendKeyEvent(getApplication(), KeyEvent.KEYCODE_HOME);
    }
}
