package com.xiaoma.assistant;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.mapbar.xiaoma.manager.XmMapNaviManager;
import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.xiaoma.assistant.manager.IBCallAndPhoneStateManager;
import com.xiaoma.assistant.manager.NaviServiceManager;
import com.xiaoma.assistant.manager.OpenSemanticManager;
import com.xiaoma.assistant.manager.QueryHomeAndCompanyManager;
import com.xiaoma.assistant.manager.VrPracticeManager;
import com.xiaoma.assistant.practice.manager.AssistantSkillManager;
import com.xiaoma.assistant.receiver.ScreenReceiver;
import com.xiaoma.assistant.service.AssistantService;
import com.xiaoma.autotracker.XmAutoTracker;
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
import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.skin.utils.SkinUtils;
import com.xiaoma.smarthome.common.manager.SmartHomeManager;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.constant.VrConstants;
import com.xiaoma.vr.skill.SkillManager;
import com.xiaoma.vr.tts.EventTtsManager;
import com.xiaoma.vrfactory.RecorderReceiver;
import com.xiaoma.wechat.manager.WeChatManagerFactory;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;

import cn.kuwo.application.App;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/12/5
 * Desc:语音助手assistant
 */
@SuppressWarnings("unused")
@DefaultLifeCycle(application = HotfixConstants.App.Assistant, flags = ShareConstants.TINKER_ENABLE_ALL)
public class Assistant extends BaseApp {

    private RecorderReceiver recorderReceiver;
    private static final String APP_SECRET = "4749ba4fd2931e96744688aa1714b0cd";
    private final App kwApp;
    private static Assistant instance;

    public Assistant(Application app, int flags, boolean verify, long appStartElapsedTime, long appStartMillisTime, Intent result) {
        super(app, flags, verify, appStartElapsedTime, appStartMillisTime, result);
        kwApp = new App(getApplication()) {
            @Override
            public boolean isCatchException() {
                return false;
            }

        };
    }

    @Override
    public void onCreate(boolean isMainProcess) {
        super.onCreate(isMainProcess);
        instance = this;
        if (!isMainProcess) {
            return;
        }
       /* initVoiceEngine();
        LoginManager.getInstance().init(getApplication());
        XmHttp.getDefault().init(getApplication());
        listenUserLoginStatus();
        UserManager.getInstance().init(getApplication());
        DBManager.getInstance().with(getApplication());
        DBManager.getInstance().initGlobalDB();
        DBManager.getInstance().initUserDB(LoginManager.getInstance().getLoginUserId());
        ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
            @Override
            public void run() {
                initXmlySdk(getApplication());
            }
        });*/
    }

    public static Context getContext() {
        return instance.getApplication();
    }

    @Override
    public void initLibs() {
        super.initLibs();
        XmHttp.getDefault().init(getApplication());
        LoginManager.getInstance().init(getApplication(), false);
        UserManager.getInstance().init(getApplication());
        listenUserLoginStatus();
        initVoiceEngine();
        DBManager.getInstance().with(getApplication());
        DBManager.getInstance().initGlobalDBCascade();
        DBManager.getInstance().initUserDBCascade(LoginManager.getInstance().getLoginUserId());
        initXmMapManager();
        ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
            @Override
            public void run() {
                initXmlySdk(getApplication());
            }
        });
        initScreenReceiver();
        initSmartHome();
        kwApp.onCreate();
        XmMapNaviManager.getInstance().init(getApplication());
        WeChatManagerFactory.getManager().init(getApplication());
        initAutoTracker();
        XmSkinManager.getInstance().initSkin(getApplication());
        SkinUtils.loadSkin(getApplication());
        VrPracticeManager.getInstance().init(getApplication());
        SkillManager.getInstance().init(getApplication());
        AssistantSkillManager.getInstance().init(getApplication());
        QueryHomeAndCompanyManager.getInstance().init(getApplication());
        OpenSemanticManager.getInstance().init();
        IBCallAndPhoneStateManager.getInstance().init(getApplication());
        NaviServiceManager.getInstance().init(getApplication());
    }

    private void initSmartHome() {
        //云米智能家居初始化
        //CMDeviceManager.getInstance().initCM(getApplication());
        //公子小白智能家居初始化
        SmartHomeManager.getInstance().init(getApplication(), SmartHomeManager.TYPE_GOLID);
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
    protected String[] allNeedPermissions() {
        return new String[]{
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA
        };
    }

    @Override
    public Class<?> firstActivity() {
        return MainActivity.class;
    }


    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        MultiDex.install(base.getApplicationContext());
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
            XmHttp.getDefault().addCommonParams("uid", LoginManager.getInstance().getLoginUserId());
        }
        LoginManager.getInstance().addLoginEventListener(new LoginManager.LoginEventListener() {
            @Override
            public void onLogin(User data) {
                if (data != null) {
                    XmHttp.getDefault().addCommonParams("uid", String.valueOf(data.getId()));
                    DBManager.getInstance().initUserDBCascade(data.getId());
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

    private void initVoiceEngine() {
        startAssistantService();
        initRecorderBroadcastReceiver();
        EventTtsManager.getInstance().init(getApplication());
    }

    private void startAssistantService() {
        Intent intent = new Intent(getApplication(), AssistantService.class);
        getApplication().startService(intent);
    }


    public void initRecorderBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(VrConstants.Actions.INIT_IVW);
        filter.addAction(VrConstants.Actions.INIT_IAT);
        filter.addAction(VrConstants.Actions.START_IVW);
        filter.addAction(VrConstants.Actions.STOP_IVW);
        filter.addAction(VrConstants.Actions.START_IAT);
        filter.addAction(VrConstants.Actions.START_IAT_RECORD);
        filter.addAction(VrConstants.Actions.START_IAT_FOR_CHOOSE);
        filter.addAction(VrConstants.Actions.STOP_IAT);
        filter.addAction(VrConstants.Actions.START_RECORDER);
        filter.addAction(VrConstants.Actions.STOP_RECORDER);
        filter.addAction(VrConstants.Actions.UPLOAD_CONTACT_IAT);
        filter.addAction(VrConstants.Actions.UPLOAD_APP_STATE);
        filter.addAction(VrConstants.Actions.UPLOAD_PLAY_STATE);
        filter.addAction(VrConstants.Actions.UPLOAD_NAVI_STATE);
        filter.addAction(VrConstants.Actions.SET_IVW_THRESHOLD);
        filter.addAction(VrConstants.Actions.START_SPEAKING);
        filter.addAction(VrConstants.Actions.START_SPEAKING_BY_THIRD);
        filter.addAction(VrConstants.Actions.STOP_SPEAKING);
        filter.addAction(VrConstants.Actions.REMOVE_TTS_SPEAKING_LISTENER);
        filter.addAction(VrConstants.Actions.AUDITION_VOICE_TYPE);
        filter.addAction(VrConstants.Actions.SET_VOICE_TYPE);

        recorderReceiver = new RecorderReceiver();
        getApplication().registerReceiver(recorderReceiver, filter);
    }

    public void initScreenReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(VrConstants.ActionScreen.TURN_ON_SCREEN_ACTION);
        filter.addAction(VrConstants.ActionScreen.TURN_OFF_SCREEN_ACTION);
        filter.addAction(VrConstants.ActionScreen.SHOW_VOICE_ASSISTANT_DIALOG);
        getApplication().registerReceiver(new ScreenReceiver(), filter);
    }


    public static void initXmlySdk(Context context) {
        if (context == null)
            throw new NullPointerException("context cannot be null !");
        context = context.getApplicationContext();
        //Sdk初始化
        CommonRequest commonRequest = CommonRequest.getInstanse();
        commonRequest.init(context, APP_SECRET);
    }
}
