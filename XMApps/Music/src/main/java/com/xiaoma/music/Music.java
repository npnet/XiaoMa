package com.xiaoma.music;

import android.Manifest;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioFocusInfo;
import android.media.AudioManager;
import android.media.audiopolicy.AudioPolicy;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.carlib.wheelcontrol.XmWheelManager;
import com.xiaoma.component.base.BaseApp;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.db.DBManager;
import com.xiaoma.guide.utils.GuideToast;
import com.xiaoma.hotfix.HotfixConstants;
import com.xiaoma.hotfix.model.PatchConfig;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.login.common.LoginConstants;
import com.xiaoma.login.common.LoginMethod;
import com.xiaoma.model.User;
import com.xiaoma.music.callback.OnBTConnectStateChangeListener;
import com.xiaoma.music.callback.OnUsbMusicChangedListener;
import com.xiaoma.music.callback.UsbFileSearchListener;
import com.xiaoma.music.common.audiosource.AudioSource;
import com.xiaoma.music.common.audiosource.AudioSourceListener;
import com.xiaoma.music.common.audiosource.AudioSourceManager;
import com.xiaoma.music.common.constant.MusicConstants;
import com.xiaoma.music.common.manager.AssistantShowManager;
import com.xiaoma.music.common.manager.MusicDbManager;
import com.xiaoma.music.common.manager.VoiceAssistantListener;
import com.xiaoma.music.common.util.UsbMusicRecordManager;
import com.xiaoma.music.export.manager.AudioShareManager;
import com.xiaoma.music.manager.BTControlManager;
import com.xiaoma.music.manager.IUsbPlayTipsListener;
import com.xiaoma.music.model.UsbMusic;
import com.xiaoma.music.practice.MusicSkillManager;
import com.xiaoma.music.search.model.SearchBean;
import com.xiaoma.music.utils.UsbScanManager;
import com.xiaoma.network.XmHttp;
import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.skin.utils.SkinUtils;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.apptool.AppObserver;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.logintype.callback.AbsClearDataListener;
import com.xiaoma.utils.logintype.constant.LoginTypeConstant;
import com.xiaoma.utils.logintype.manager.LoginTypeManager;
import com.xiaoma.utils.logintype.receiver.CleanDataBroadcastReceiver;
import com.xiaoma.utils.receiver.UsbDetector;
import com.xiaoma.utils.tputils.TPUtils;
import com.xiaoma.vr.dispatch.DispatchManager;
import com.xiaoma.vr.iat.RemoteIatManager;
import com.xiaoma.vr.model.AppType;

import java.util.ArrayList;
import java.util.List;

import cn.kuwo.application.App;
import cn.kuwo.open.log.LogUtils;

import static com.xiaoma.music.common.audiosource.AudioSource.BLUETOOTH_MUSIC;
import static com.xiaoma.music.common.audiosource.AudioSource.ONLINE_MUSIC;
import static com.xiaoma.music.common.audiosource.AudioSource.USB_MUSIC;

/**
 * Created by ZYao.
 * Date ：2018/10/9 0009
 */
@SuppressWarnings("unused")
@DefaultLifeCycle(application = HotfixConstants.App.Music, flags = ShareConstants.TINKER_ENABLE_ALL)
public class Music extends BaseApp {
    private static final int KW_NOTIFICATION_ID = 1073741823;
    private final App kwApp;


    public Music(final Application app, int flags, boolean verify, long appStartElapsedTime, long appStartMillisTime, Intent result) {
        super(app, flags, verify, appStartElapsedTime, appStartMillisTime, result);
        kwApp = new App(getApplication()) {

            @Override
            public boolean isDebugServer() {
                //true 测试环境   false 正式环境
                //                return ConfigManager.EnvConfig.getEnvType() == EnvType.TEST;
                // TODO: 2019/7/10 0010 后台还没有酷我的正式接口，目前先都使用测试sdk
                return MusicConstants.KwConstants.KW_SDK_ENV_FLAG;
            }

            @Override
            public boolean isCatchException() {
                return false;
            }

            @Override
            public Notification getNotification() {
                Notification n = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    final NotificationManager mgr = app.getSystemService(NotificationManager.class);
                    if (mgr != null) {
                        final String channelId = app.getPackageName() + "_kuwo";
                        final NotificationChannel channel = new NotificationChannel(channelId, "kuwo", NotificationManager.IMPORTANCE_DEFAULT);
                        mgr.createNotificationChannel(channel);
                        // 通知只设置小icon,不设置任何内容,小马SystemUI将隐藏该通知的显示
                        mgr.notify(KW_NOTIFICATION_ID,
                                n = new Notification.Builder(app, channelId)
                                        .setSmallIcon(R.drawable.icon_default_icon)
                                        .build());
                    }
                }
                return n;
            }
        };
    }

    @Override
    public void onCreate(boolean isMainProcess) {
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {
                kwApp.onCreate();
                return false;
            }
        });
        super.onCreate(isMainProcess);
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
        LoginManager.getInstance().init(getApplication(), false);
        UserManager.getInstance().init(getApplication());
        DispatchManager.getInstance().init(getApplication());
        XmSkinManager.getInstance().initSkin(getApplication());
        SkinUtils.loadSkin(getApplication());
        UsbMusicRecordManager.getInstance().initApplicationAttribute(getApplication());
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {
                listenUserLoginStatus();
                AudioSourceManager.getInstance().init(getApplication());
                DBManager.getInstance().with(getApplication());
                DBManager.getInstance().initGlobalDBCascade();
                DBManager.getInstance().initUserDBCascade(LoginManager.getInstance().getLoginUserId());
                XmWheelManager.getInstance().init(getApplication());
                initOnlineMusic();
                initBtMusic();
                initUsbMusic();
                initAutoTracker();
                initAudioShare();
                registerAudioPolicy();
                XmCarFactory.init(getApplication());
                registerClearDataListener();
                MusicSkillManager.getInstance().init(getApplication());
                AssistantShowManager.getInstance().registerVrDialogReceiver(getApplication());
                return false;
            }
        });
        RemoteIatManager.getInstance().init(getApplication());
        AppObserver.getInstance().addAppStateChangedListener(new AppObserver.AppStateChangedListener() {
            @Override
            public void onForegroundChanged(boolean isForeground) {
                RemoteIatManager.getInstance().uploadAppState(isForeground, AppType.MUSIC);
            }
        });
        VoiceAssistantListener.getInstance().init(getApplication());
    }


    private void registerClearDataListener() {
        CleanDataBroadcastReceiver cleanDataBroadcastReceiver = new CleanDataBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LoginTypeConstant.BROADCAST_ACTION_SWITCH_USER_CLEAR);
        getBaseContext(getApplication()).registerReceiver(cleanDataBroadcastReceiver, intentFilter);
        LoginTypeManager.getInstance().registerClearDataListener(new AbsClearDataListener() {
            @Override
            public void onSwitchUserClear(long currentUserId, String loginMethod) {
                if (LoginConstants.TOURIST_USER_ID == currentUserId && LoginMethod.TOURISTS.name().equals(loginMethod)) {
                    Music.clearAllData(getApplication(), String.valueOf(currentUserId));
                }
            }
        });
    }

    public void finishMusic() {
        KLog.i("filOut| " + "[finishMusic]");
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public void clearDataAfterCloseProcess() {
        KLog.i("filOut| " + "[clearDataAfterCloseProcess]");
        AudioSourceManager.getInstance().removeCurrAudioSource();
    }


    public static void clearExcludeUserId(Context context) {
        KLog.i("filOut| " + "[onSwitchUserClear]");
        try {
            TPUtils.put(context, AudioSourceManager.KEY_AUDIO_SOURCE, "");//播放记录清理
            DBManager.getInstance().getDBManager().deleteAll(SearchBean.class);//搜索记录清理
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void clearAllData(Context context, String userId) {
        KLog.i("filOut| " + "[clearAllData]->" + userId);
        try {
            MusicDbManager.getInstance().clearAllHistoryMusic(userId);//清理历史记录
        } catch (Exception ignored) {
        }

        try {
            DBManager.getInstance().getDBManager().deleteAll(SearchBean.class);//搜索记录清理
        } catch (Exception ignored) {
        }

        try {
            MusicDbManager.getInstance().clearCollectionMusic();//清理收藏数据
        } catch (Exception ignored) {
        }
    }

    private void initAudioShare() {
        AudioShareManager.getInstance().init(getApplication());
        AudioSourceManager.getInstance().addAudioSourceListener(new AudioSourceListener() {
            @Override
            public void onAudioSourceSwitch(@AudioSource int preAudioSource, @AudioSource int currAudioSource) {
                if (AudioSource.NONE != preAudioSource) {
                    switch (currAudioSource) {
                        case ONLINE_MUSIC:
                            XMToast.showToast(getApplication(), getApplication().getString(R.string.changed_to_online_music));
                            break;
                        case BLUETOOTH_MUSIC:
                            XMToast.showToast(getApplication(), getApplication().getString(R.string.changed_to_bt_music));
                            break;
                        case USB_MUSIC:
                            XMToast.showToast(getApplication(), getApplication().getString(R.string.changed_to_usb_music));
                            break;
                        case AudioSource.NONE:
                            break;
                    }
                }
            }
        });
        UsbMusicFactory.getUsbPlayerListProxy().addUsbPlayTipsListener(new IUsbPlayTipsListener() {
            @Override
            public void onFirstTips() {
                XMToast.showToast(getApplication(), R.string.already_first);
            }

            @Override
            public void onLastTips() {
                XMToast.showToast(getApplication(), R.string.already_last);
            }

        });
    }

    private void initAutoTracker() {
        if (!TextUtils.isEmpty(LoginManager.getInstance().getLoginUserId())) {
            XmAutoTracker.getInstance().init(getApplication(), LoginManager.getInstance().getLoginUserId());
        }
    }

    private void initUsbMusic() {
        UsbMusicFactory.init(getApplication());
        UsbDetector.getInstance().addUsbDetectListener(new UsbDetector.UsbDetectListener() {
            @Override
            public void noUsbMounted() {
                removeUsbDetector();
            }

            @Override
            public void inserted() {

            }

            @Override
            public void mounted(List<String> mountPaths) {
                addUsbDetector();
                if (!ListUtils.isEmpty(mountPaths)) {
                    UsbScanManager.getInstance().scanUsbMediaData(mountPaths.get(0));
                }
            }

            @Override
            public void mountError() {
                removeUsbDetector();
            }

            @Override
            public void removed() {
                removeUsbDetector();
                XMToast.showToast(getApplication(), R.string.usb_device_disconnected);
                MusicDbManager.getInstance().removeUsbMusicList();
            }
        });
        // 后台监听USB挂载状态
        UsbScanManager.getInstance().addUsbScanListener(new UsbFileSearchListener() {
            @Override
            public void onUsbMusicScanFinished(ArrayList<UsbMusic> musicList) {
                XMToast.showToast(getApplication(), R.string.complete_fetch_usb_media_data);
            }

            @Override
            public void onUsbMusicAnalyticFinished(final ArrayList<UsbMusic> musicList) {
                // USB音乐扫描完毕
                KLog.d("XM_LOG_" + "onUsbMusicAnalyticFinished: " + musicList.size());
                saveUsbMusic(musicList);
            }
        });

        UsbMusicFactory.getUsbPlayerProxy().addMusicChangeListener(new OnUsbMusicChangedListener() {
            @Override
            public void onBuffering(UsbMusic usbMusic) {

            }

            @Override
            public void onPlay(UsbMusic usbMusic) {
                UsbMusicRecordManager.getInstance().startUsbMemory();
            }

            @Override
            public void onPause() {

            }

            @Override
            public void onProgressChange(long progressInMs, long totalInMs) {

            }

            @Override
            public void onPlayFailed(int errorCode) {

            }

            @Override
            public void onPlayStop() {
                UsbMusicRecordManager.getInstance().stopUsbMemory();
            }

            @Override
            public void onCompletion() {

            }
        });


        //ACC OFF/ON 注册  防止usb播放时 acc off后usb依旧在播放
        XmCarVendorExtensionManager.getInstance().addValueChangeListener((id, value) -> {
            if (id == SDKConstants.ID_WORK_MODE_STATUS) {
                int rawValue = (int) value;

                if (AudioSourceManager.getInstance().getCurrAudioSource() != USB_MUSIC) {
                    return;
                }

                if (SDKConstants.WorkMode.STANDBY == rawValue) {
                    UsbMusicFactory.getUsbPlayerProxy().switchPlay(false);
                } else if (SDKConstants.WorkMode.NORMAL == rawValue) {
                    UsbMusicFactory.getUsbPlayerProxy().continuePlayOrPlayFirst();
                }
            }
        });
    }

    private void saveUsbMusic(ArrayList<UsbMusic> musicList) {
        ThreadDispatcher.getDispatcher().postDelayed(new Runnable() {
            @Override
            public void run() {
                MusicDbManager.getInstance().saveUsbMusicList(musicList);
            }
        }, 1000);
    }

    private void initOnlineMusic() {
        OnlineMusicFactory.getKWPlayer().init(getApplication());
        OnlineMusicFactory.getKWMessage().registerKuwoStateListener();
        OnlineMusicFactory.getKWLogin().autoLogin();
        OnlineMusicFactory.getKWLogin().fetchVipInfo();
        if (ConfigManager.ApkConfig.isDebug()) LogUtils.setShowLog(true);
    }

    private void registerAudioPolicy() {
        AudioManager mAudioManager = (AudioManager) getApplication().getSystemService(Context.AUDIO_SERVICE);
        AudioPolicy.Builder builder = new AudioPolicy.Builder(getApplication());
        builder.setAudioPolicyFocusListener(new AudioPolicy.AudioPolicyFocusListener() {
            @Override
            public void onAudioFocusGrant(AudioFocusInfo afi, int requestResult) {
                super.onAudioFocusGrant(afi, requestResult);
                if (afi != null && afi.getPackageName() != null) {
                    String packageName = afi.getPackageName();
                    Log.d("MediaFocusControl", "reqFocus package: " + packageName);
                    if (packageName.equals("com.xiaoma.music")) {
                        if (afi.getAttributes() != null) {
                            Bundle bundle = afi.getAttributes().getBundle();
                            if (bundle == null) {
                                return;
                            }
                            int anInt = bundle.getInt(MusicConstants.AUDIO_FOCUS_SOURCE, AudioSource.ONLINE_MUSIC);
                            switch (anInt) {
                                case AudioSource.ONLINE_MUSIC:
                                    AudioShareManager.getInstance().shareKwAudioTypeChanged();
                                    break;
                                case AudioSource.USB_MUSIC:
                                    AudioShareManager.getInstance().shareUsbAudioTypeChanged();
                                    break;
                                case AudioSource.BLUETOOTH_MUSIC:
                                    AudioShareManager.getInstance().shareBtAudioTypeChanged();
                                    break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onAudioFocusLoss(AudioFocusInfo afi, boolean wasNotified) {
                super.onAudioFocusLoss(afi, wasNotified);
                Log.d("MediaFocusControl", "reqFocus package: " + afi.getPackageName());
            }

        });
        if (mAudioManager != null) {
            mAudioManager.registerAudioPolicy(builder.build());
        }
    }

    @Override
    protected String[] allNeedPermissions() {
        return new String[]{
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE
        };
    }

    @Override
    public Class<?> firstActivity() {
        return MainActivity.class;
    }

    private void initBtMusic() {
        if (!ConfigManager.ApkConfig.isCarPlatform()) {
            return;
        }
        BTMusicFactory.getBTMusicControl().init(getApplication());
        BTMusicFactory.getBTMusicControl().addBtStateChangeListener(new OnBTConnectStateChangeListener() {
            public void onBtRemove() {
                @AudioSource int currAudioSource = AudioSourceManager.getInstance().getCurrAudioSource();
                if (currAudioSource == AudioSource.BLUETOOTH_MUSIC) {
                    AudioSourceManager.getInstance().removeCurrAudioSource();
                }
            }

            @Override
            public void onBTConnect() {

            }

            @Override
            public void onBTDisconnect() {
                onBtRemove();
            }

            @Override
            public void onBTSinkConnected() {

            }

            @Override
            public void onBTSinkDisconnected() {
                onBtRemove();
            }
        });
        BTControlManager.getInstance().pauseRender();
    }

    private void addUsbDetector() {
        UsbMusicFactory.getUsbPlayerProxy().init(getApplication());
    }

    private void removeUsbDetector() {
        @AudioSource int currAudioSource = AudioSourceManager.getInstance().getCurrAudioSource();
        if (currAudioSource == AudioSource.USB_MUSIC) {
            AudioSourceManager.getInstance().removeCurrAudioSource();
        }
        UsbMusicFactory.getUsbPlayerProxy().destroy();
    }

    private void listenUserLoginStatus() {
        if (LoginManager.getInstance().isUserLogin()) {
            User user = UserManager.getInstance().getUser(LoginManager.getInstance().getLoginUserId());
            if (user != null) {
                XmHttp.getDefault().addCommonParams("uid", String.valueOf(user.getId()));
            }
        }
        LoginManager.getInstance().addLoginEventListener(new LoginManager.LoginEventListener() {
            @Override
            public void onLogin(User data) {
                if (data != null) {
                    XmHttp.getDefault().addCommonParams("uid", String.valueOf(data.getId()));
                    DBManager.getInstance().initUserDB(data.getId());
                }
                initAutoTracker();
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
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().startActivity(intent);
    }
}
