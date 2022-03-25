package com.xiaoma.launcher.main.ui;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mapbar.android.NaviSupportBefore;
import com.mapbar.android.launchersupport.GlobalEngine;
import com.mapbar.android.launchersupport.NaviSupportFragment;
import com.mapbar.android.mapbarmap.util.listener.Listener;
import com.mapbar.xiaoma.Receiver.XmNaviReceiver;
import com.mapbar.xiaoma.manager.XmMapNaviManager;
import com.xiaoma.ad.AdManager;
import com.xiaoma.ad.dialog.AdDialog;
import com.xiaoma.ad.models.Ad;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.carlib.manager.WifiObserver;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.guide.listener.GuideFinishCallBack;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.guide.utils.NewGuide;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.app.ui.AppFragment;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.common.manager.LauncherAppManager;
import com.xiaoma.launcher.common.manager.RequestManager;
import com.xiaoma.launcher.common.manager.WeatherManager;
import com.xiaoma.launcher.common.service.LauncherService;
import com.xiaoma.launcher.common.views.CenterRadioButton;
import com.xiaoma.launcher.common.views.LauncherWebActivity;
import com.xiaoma.launcher.common.views.MenuButton;
import com.xiaoma.launcher.common.views.SelectModelDialog;
import com.xiaoma.launcher.favorites.FavoritesDBManager;
import com.xiaoma.launcher.favorites.PoiFavorite;
import com.xiaoma.launcher.map.manager.MapManager;
import com.xiaoma.launcher.player.manager.PlayerConnectHelper;
import com.xiaoma.launcher.player.ui.AudioMainFragment;
import com.xiaoma.launcher.player.ui.FlickerActivity;
import com.xiaoma.launcher.recommend.manager.RecommendDataParserManager;
import com.xiaoma.launcher.recommend.view.RecommendDialog;
import com.xiaoma.launcher.service.ui.ServiceFragment;
import com.xiaoma.launcher.splash.manager.SplashVideoManager;
import com.xiaoma.launcher.travel.car.ui.NearByOilParkActivity;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.login.business.bean.LoginStatus;
import com.xiaoma.login.common.LoginConstants;
import com.xiaoma.login.common.LoginMethod;
import com.xiaoma.login.common.UserUtil;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.User;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.mqtt.model.MqttInfo;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.progress.loading.CustomProgressDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.update.constant.SoundUpdate;
import com.xiaoma.update.manager.AppUpdateCheck;
import com.xiaoma.utils.KeyEventUtils;
import com.xiaoma.utils.LaunchUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.XmProperties;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.logintype.callback.AbsClearDataListener;
import com.xiaoma.utils.logintype.constant.LoginTypeConstant;
import com.xiaoma.utils.logintype.manager.LoginTypeManager;
import com.xiaoma.utils.logintype.receiver.CleanDataBroadcastReceiver;
import com.xiaoma.utils.tputils.TPUtils;

import org.json.JSONObject;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import skin.support.widget.SkinCompatSupportable;

/**
 * Created by Thomas on 2019/1/10 0010
 * 桌面 main enter
 */
@SuppressLint("LogNotTimber")
@PageDescComponent(EventConstants.PageDescribe.mainActivityPagePathDesc)
public class MainActivity extends BaseActivity implements SkinCompatSupportable, View.OnSystemUiVisibilityChangeListener {
    public static final String TAG = "[MainActivity]";
    private static final Class[] FRAGMENT_CLZ = new Class[]{
            NaviSupportFragment.class, AudioMainFragment.class, ServiceFragment.class, AppFragment.class
    };
    private static final int SELECT_MODEL_DIALOG = 5000;
    private static final int FIVE_MINUTE = 300000;
    private static final int MENU_BTN_ENABLE = 1000;
    private static final int RESTART_SELF = 1000;
    private static final int SWITCH_BUTTON_ANIMAL = 200;
    private static final int MAIN_LEFT_TAB_WIDTH = 175;
    private MenuButton menuButton;
    private CenterRadioButton naviCenterRadioButton, musicCenterRadioButton, serviceCenterRadioButton, appCenterRadioButton;
    /*private ServiceFragment serviceFragment;
    private AppFragment appFragment;
    private NaviSupportFragment mapFragment;
    private AudioMainFragment audioFragment;*/
    private Ad mAdBean;
    private NewGuide newGuide;
    private BroadcastReceiver guideDismidssReceiver;
    private int currentFragmentResId;
    //private Fragment currentFragment;
    private CleanDataBroadcastReceiver mCleanDataBroadcastReceiver;
    private AbsClearDataListener mAbsClearDataListener;
    private BroadcastReceiver naviByPoiReceiver;
    private BroadcastReceiver naviByKeyReceiver;
    private BroadcastReceiver queryCollectReceiver;
    private SelectModelDialog mSelectModelDialog;
    //地图初始化完成前显示的dialog
    private CustomProgressDialog loadingDialog = null;
    private boolean mapCompelete = false;
    private FragmentManager.FragmentLifecycleCallbacks mFragmentLifecycleCallbacks;

    private static final int SystemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_IMMERSIVE;

    @Override
    protected void onCreate(final @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(this);
        getWindow().getDecorView().setSystemUiVisibility(SystemUiVisibility);

        GlobalEngine.getInstance().setIsFirst(true);
        setContentView(R.layout.layout_main_activity);
        KLog.e("XM Launcher MainActivity onCreate");
        naviCenterRadioButton = findViewById(R.id.radio_button_navi);
        musicCenterRadioButton = findViewById(R.id.radio_button_music);
        serviceCenterRadioButton = findViewById(R.id.radio_button_service);
        appCenterRadioButton = findViewById(R.id.radio_button_app);
        menuButton = findViewById(R.id.menu_button);
        if (savedInstanceState != null) {
            int tabResId = savedInstanceState.getInt("tab_res_id");
            if (tabResId > 0) {
                currentFragmentResId = tabResId;
            } else {
                currentFragmentResId = R.id.radio_button_navi;
            }
        }
        if (mFragmentLifecycleCallbacks == null) {
            getSupportFragmentManager().registerFragmentLifecycleCallbacks(
                    mFragmentLifecycleCallbacks = new NavLifecycleCallback(),
                    false);
        }
        initDisclaimerState();
        EventBus.getDefault().register(this);
        addWifiListener();
    }

    private void addWifiListener() {
        if (ConfigManager.FileConfig.isUserValid()) return;
        WifiObserver.getInstance().init();
        WifiObserver.getInstance().setForUserListener(new WifiObserver.WifiListener() {
            @Override
            public void onWifiConnect() {
                Log.d(UserUtil.TAG, "onCarEvent: wifi connect callback invoke");
                UserUtil.fetchUserValid(null);
            }

            @Override
            public void onWifiDisConnect() {
                if (ConfigManager.FileConfig.isUserValid()) {
                    WifiObserver.getInstance().removeForUserListener();
                }
            }
        });
    }

    private void restartSelf() {
        XMToast.showToast(this, "Launcher will soon restart, wait for a while...");
        getUIHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                PendingIntent restartIntent = PendingIntent.getActivity(
                        MainActivity.this,
                        0,
                        intent,
                        PendingIntent.FLAG_ONE_SHOT);
                AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 500, restartIntent);
                System.exit(0);
            }
        }, RESTART_SELF);
    }

    //初始化用户免责同意状态
    private void initDisclaimerState() {
        ThreadDispatcher.getDispatcher().postHighPriority(new Runnable() {
            @Override
            public void run() {
                if (XmProperties.build().get(LauncherConstants.DISCLAIMER_STATUS, false)) {
                    intAdManager();
                } else {
                    //免责声明弹窗
                    showDisclaimerDialog();
                }
            }
        });
    }


    //显示广告
    private void intAdManager() {
        try {
            if (AdManager.getCachedAd().size() > 0) {
                mAdBean = AdManager.showAd(MainActivity.this, new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        //新手引导
                        shouldShowGuideWindow();
                    }
                }, new AdDialog.AdDialogClickListener() {
                    @Override
                    public void onDialogClick(String link) {
                        LauncherWebActivity.start(MainActivity.this, link);
                    }
                });
                init();
            } else {
                //新手引导
                shouldShowGuideWindow();
                init();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 如果出了异常直接进入初始化,避免页面卡着不动
            init();
        }
    }

    private void showDisclaimerDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View view = View.inflate(MainActivity.this, R.layout.disclaimer_dialog, null);
                TextView disclaimerText = view.findViewById(R.id.disclaimer_text);
                TextView disclaimerBut = view.findViewById(R.id.disclaimer_but);
                AlertDialog.Builder builderDialog = new AlertDialog.Builder(MainActivity.this, R.style.custom_dialog2);
                builderDialog.setView(view);
                builderDialog.setCancelable(false);
                builderDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        return false;
                    }
                });
                final AlertDialog dialog = builderDialog.create();
                dialog.getWindow().setBackgroundDrawable(XmSkinManager.getInstance().getDrawable(R.drawable.bg_dialog_big));
                disclaimerText.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
                    @Override
                    public ItemEvent returnPositionEventMsg(View view) {
                        return new ItemEvent(EventConstants.NormalClick.DISCLAIMERTEXT, "0");
                    }

                    @Override
                    @BusinessOnClick
                    public void onClick(View v) {
                        //免责详情
                        startActivity(new Intent(MainActivity.this, SisclaimerActivity.class));
                    }
                });
                disclaimerBut.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
                    @Override
                    public ItemEvent returnPositionEventMsg(View view) {
                        return new ItemEvent(EventConstants.NormalClick.DISCLAIMERBUT, "0");
                    }

                    @Override
                    @BusinessOnClick
                    public void onClick(View v) {
                        XmProperties.build().put(LauncherConstants.DISCLAIMER_STATUS, true);
                        dialog.dismiss();
                        intAdManager();
                    }
                });
                dialog.show();
            }
        });
    }

    //初始化主功能
    private void init() {
        //初始化四维地图
        NaviSupportBefore.initAtActivity(this, 1920, 720);
        //四维margin菜单边距175px
        NaviSupportBefore.initSidebarWidth(MAIN_LEFT_TAB_WIDTH);
        initView();
        initData();

        initModeDialog();
    }

    private void initModeDialog() {
        //如果是星期六星期一以外就不显示模式弹窗
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        if (day == Calendar.MONDAY || day == Calendar.SATURDAY) {
            boolean haveShown = TPUtils.get(MainActivity.this, LauncherConstants.IS_HAVE_SHOWN, false);
            if (!haveShown){
                if (NewGuide.isGuideShowNow()) {
                    NewGuide.setGuideFinishCallBack(new GuideFinishCallBack() {
                        @Override
                        public void onGuideFinish() {
                            showModelDialog();
                        }
                    });
                } else {
                    showModelDialog();
                }
            }
        } else {
            TPUtils.put(MainActivity.this, LauncherConstants.IS_HAVE_SHOWN, false);
        }
    }

    private void showModelDialog() {
        int delayTime = SELECT_MODEL_DIALOG;
        if (mAdBean != null) {
            delayTime = mAdBean.getShowTime() * 1000 + SELECT_MODEL_DIALOG;
        }
        ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
            @Override
            public void run() {
                mSelectModelDialog = new SelectModelDialog(MainActivity.this);
                mSelectModelDialog.show();
                TPUtils.put(MainActivity.this, LauncherConstants.IS_HAVE_SHOWN, true);
            }
        }, delayTime);
    }

    private void registerClearDataListener() {
        mCleanDataBroadcastReceiver = new CleanDataBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LoginTypeConstant.BROADCAST_ACTION_SWITCH_USER_CLEAR);
        registerReceiver(mCleanDataBroadcastReceiver, intentFilter);
        LoginTypeManager.getInstance().registerClearDataListener(mAbsClearDataListener = new AbsClearDataListener() {

            @Override
            public void onSwitchUserClear(long currentUserId, String loginMethod) {
                if (LoginConstants.TOURIST_USER_ID == currentUserId
                        && LoginMethod.TOURISTS.name().equals(loginMethod)) {
//                    TPUtils.putObject(MainActivity.this, AudioMainFragment.LAST_AUDIO_INFO, null);
//                    TPUtils.put(MainActivity.this, AudioMainFragment.CATEGORY_ID, -1);
//                    KLog.i("filOut| " + "[clearAllData]");
                }
            }
        });
    }

    private void initSplashVideo() {
        SplashVideoManager.init(this);
        SplashVideoManager.syncVideo();
    }

    private void initForegroundService() {
        this.startService(new Intent(this, LauncherService.class));
    }

    private void initAppAliveWatcher() {
        LauncherAppManager.launcherAppStart(this);
        keepAudioService();
    }

    private void keepAudioService() {
        PlayerConnectHelper connectHelper = PlayerConnectHelper.getInstance();
        if (connectHelper.getSourceInfoByAudioType(AudioConstants.AudioTypes.XTING) == null) {
            Log.d("zs", "LauncherAppManager:" + LauncherConstants.LauncherApp.LAUNCHER_XTING_PACKAGE);
            LauncherAppManager.launcherAppOutStart(getApplication(), LauncherConstants.LauncherApp.LAUNCHER_XTING_PACKAGE);
        }
        if (connectHelper.getSourceInfoByAudioType(AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO) == null ||
                connectHelper.getSourceInfoByAudioType(AudioConstants.AudioTypes.MUSIC_LOCAL_BT) == null ||
                connectHelper.getSourceInfoByAudioType(AudioConstants.AudioTypes.MUSIC_LOCAL_USB) == null) {
            Log.d("zs", "LauncherAppManager:" + LauncherConstants.LauncherApp.LAUNCHER_MUSIC_PACKAGE);
            LauncherAppManager.launcherAppOutStart(getApplication(), LauncherConstants.LauncherApp.LAUNCHER_MUSIC_PACKAGE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        KLog.d("MainActivity onNewIntent");
        Bundle bundle = intent.getExtras();
        boolean map = (bundle != null && bundle.getBoolean(KeyEventUtils.GO_HOME_MAP, false));
        if (map && currentFragmentResId != R.id.radio_button_navi) {
            naviCenterRadioButton.setChecked(true);
        }
        boolean service = (bundle != null && bundle.getBoolean(KeyEventUtils.GO_HOME_SERVICE, false));
        if (service && currentFragmentResId != R.id.radio_button_service) {
            serviceCenterRadioButton.setChecked(true);
        }
        if (XmProperties.build().get(LauncherConstants.DISCLAIMER_STATUS, false)) {
            shouldShowGuideWindow();
        } else {
            //免责声明弹窗
            showDisclaimerDialog();
        }
    }
//
//    private void showModelDialog() {
//        int delayTime = SELECT_MODEL_DIALOG;
//        if (mAdBean != null) {
//            delayTime = mAdBean.getShowTime() * 1000 + SELECT_MODEL_DIALOG;
//        }
//        ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mSelectModelDialog = new SelectModelDialog(MainActivity.this);
//                mSelectModelDialog.show();
//            }
//        }, delayTime);
//    }

    private void initView() {
        KLog.e(TAG, "initView()");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getNaviBar().hideNavi();
//                EventBus.getDefault().register(this);
                initFragment();
                menuButton.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int resId) {
                        controlPage(resId, false);
                    }
                });
                changeMenuBtnEnable(false);
                //开启前台通知服务
                initForegroundService();
            }
        });
        // 产品确认屏蔽宠物入口
        /*mBtnPet.setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick({EventConstants.NormalClick.MAINACTIVITY_BETTON_PET})
            public void onClick(View v) {
                if (!LoginTypeManager.getInstance().judgeUse(LauncherConstants.LauncherApp.LAUNCHER_PET_PACKAGE
                        , new OnBlockCallback() {
                            @Override
                            public boolean onShowToast(LoginType loginType) {
                                XMToast.showToast(MainActivity.this, LoginTypeManager.getPrompt(MainActivity.this));
                                return true;
                            }
                        })) return;
                startPetApp();
            }
        });*/
    }

    private void initMusicFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        String fmtTag = AudioMainFragment.class.getName();
        Fragment exist = fragmentManager.findFragmentByTag(fmtTag);
        // 已存在的话,不要重新添加,避免内存泄漏
        if (exist instanceof AudioMainFragment) {
            return;
        }
        AudioMainFragment audioMainFragment = new AudioMainFragment();
        fragmentManager.beginTransaction()
                .add(R.id.fl_common, audioMainFragment, fmtTag)
                .hide(audioMainFragment)
                .commitNowAllowingStateLoss();
    }

    private void initFragment() {
        if (currentFragmentResId > 0) {
            controlPage(currentFragmentResId, true);
        } else {
            initMusicFragment();
            controlPage(R.id.radio_button_navi, true);
        }
    }

    private void loadingDialogDismiss() {
        mapCompelete = true;
        ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
            @Override
            public void run() {
                if (null != loadingDialog) {
                    loadingDialog.dismiss();
                    loadingDialog = null;
                }
            }
        }, MENU_BTN_ENABLE);
    }

    private void initData() {
        //获取天气
        WeatherManager.getInstance().getCurrCityWeather();
        //执行3D动作
        WeatherManager.getInstance().getCurrRobAction();
        //应用拉活
        initAppAliveWatcher();
        //获取消息列表
        notifyMsgList();
        //获取开机广告
        initSplashVideo();
        // 注册车载微信广播接收者
        registCarWXReceiver();
        // 清理数据监听
        registerClearDataListener();
        // 注册查询收藏监听
        registQueryCollect();
        notifyClearAppData();
        //通知检查是否有未完成的音效更新
        notifyCheckUndoneAudioUpdate();
    }

    private void notifyClearAppData() {
        User currentUser = UserManager.getInstance().getCurrentUser();
        LoginStatus loginStatus = LoginManager.getInstance().getLoginStatus();
        if (currentUser != null && loginStatus != null) {
            LoginTypeManager.getInstance().sendBroadcastForClearData(this,
                    currentUser.getId(), loginStatus.getLoginMethod());
        }
    }

    private void notifyCheckUndoneAudioUpdate() {
        Intent intent = new Intent(SoundUpdate.ACTION_CHECK_SOUND_UPDATE);
        sendStickyBroadcast(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        KLog.e("XM Launcher MainActivity onResume");
        AppUpdateCheck.getInstance().checkAppUpdate(getPackageName(), getApplication());
        //检查是否有推荐数据未显示
        checkRecommendData();
    }

    private void checkRecommendData() {
        ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
            @Override
            public void run() {
                HashMap<Long, String> recommendDataMap = RecommendDataParserManager.getInstance().getRecommendDataMap();
                for (Iterator<HashMap.Entry<Long, String>> it = recommendDataMap.entrySet().iterator(); it.hasNext(); ) {
                    HashMap.Entry<Long, String> entry = it.next();
                    long time = entry.getKey();
                    String message = entry.getValue();
                    //如果是时效五分钟内就弹窗
                    if (System.currentTimeMillis() - time <= FIVE_MINUTE) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                RecommendDataParserManager.getInstance().parserRecommendData(MainActivity.this, message);
                            }
                        });
                    }
                }
                //清空map
                RecommendDataParserManager.getInstance().resetRecommendDataMap();
            }
        });
    }

    private void controlPage(int resId, boolean isFirstInit) {
        //1 地图 2 音源 3 服务 4 应用
        switch (resId) {
            case R.id.radio_button_navi:
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.MAINACTIVITY_RADIO_BUTTON_NAVI,
                        "0",
                        EventConstants.PagePath.MainActivityPath,
                        EventConstants.PageDescribe.mainActivityPagePathDesc);
                switchFragment(NaviSupportFragment.class);
                break;
            case R.id.radio_button_music:
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.MAINACTIVITY_RADIO_BUTTON_MUSIC,
                        "0",
                        EventConstants.PagePath.MainActivityPath,
                        EventConstants.PageDescribe.mainActivityPagePathDesc);
                switchFragment(AudioMainFragment.class);
                break;
            case R.id.radio_button_service:
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.MAINACTIVITY_RADIO_BUTTON_SERVICE,
                        "0",
                        EventConstants.PagePath.MainActivityPath,
                        EventConstants.PageDescribe.mainActivityPagePathDesc);
                switchFragment(ServiceFragment.class);
                break;
            case R.id.radio_button_app:
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.MAINACTIVITY_RADIO_BUTTON_APP,
                        "0",
                        EventConstants.PagePath.MainActivityPath,
                        EventConstants.PageDescribe.mainActivityPagePathDesc);
                switchFragment(AppFragment.class);
                break;
        }
        switchButtonAnimal(resId, isFirstInit);
        if (R.id.radio_button_navi != resId && XmMapNaviManager.getInstance().isNaviForeground()) {
            XmNaviReceiver.notifyNaviForegroundState(this.getApplicationContext(), false);
        }
        if (R.id.radio_button_navi == resId) {
            XmNaviReceiver.notifyNaviForegroundState(this.getApplicationContext(), true);
        }
    }

    private void switchButtonAnimal(int resId, boolean isFirstInit) {
        ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
            @Override
            public void run() {
                switchButtonInverted(isFirstInit);
                currentFragmentResId = resId;
                switchButtonNormal(isFirstInit);
            }
        }, SWITCH_BUTTON_ANIMAL);
    }

    private void switchButtonNormal(boolean isFirstInit) {
        switch (currentFragmentResId) {
            case R.id.radio_button_navi:
                if (isFirstInit) {
//                                        naviCenterRadioButton.setBackground(getResources().getDrawable(R.drawable.recommend_15, null));
                    naviCenterRadioButton.setBackgroundResource(R.drawable.recommend_15);
                } else {
//                    naviCenterRadioButton.setBackground(XmSkinManager.getInstance().getDrawable(R.drawable.recommend_anim, null));
                    naviCenterRadioButton.setBackgroundResource(R.drawable.recommend_anim);
                    AnimationDrawable mNaviAnimation = (AnimationDrawable) naviCenterRadioButton.getBackground();
                    mNaviAnimation.start();
                }
                break;
            case R.id.radio_button_music:
                if (isFirstInit) {
                    musicCenterRadioButton.setBackgroundResource(R.drawable.music_15);
                } else {
//                    musicCenterRadioButton.setBackground(XmSkinManager.getInstance().getDrawable(R.drawable.music_anim, null));
                    musicCenterRadioButton.setBackgroundResource(R.drawable.music_anim);
                    AnimationDrawable mAudioAnimation = (AnimationDrawable) musicCenterRadioButton.getBackground();
                    mAudioAnimation.start();
                }
                break;
            case R.id.radio_button_service:
                if (isFirstInit) {
                    serviceCenterRadioButton.setBackgroundResource(R.drawable.service_15);
                } else {
//                    serviceCenterRadioButton.setBackground(XmSkinManager.getInstance().getDrawable(R.drawable.service_anim, null));
                    serviceCenterRadioButton.setBackgroundResource(R.drawable.service_anim);
                    AnimationDrawable mServiceAnimation = (AnimationDrawable) serviceCenterRadioButton.getBackground();
                    mServiceAnimation.start();
                }
                break;
            case R.id.radio_button_app:
                if (isFirstInit) {
                    appCenterRadioButton.setBackgroundResource(R.drawable.app_15);
                } else {
//                    appCenterRadioButton.setBackground(XmSkinManager.getInstance().getDrawable(R.drawable.app_anim, null));
                    appCenterRadioButton.setBackgroundResource(R.drawable.app_anim);
                    AnimationDrawable mAppAnimation = (AnimationDrawable) appCenterRadioButton.getBackground();
                    mAppAnimation.start();
                }
                break;
            default:
                break;
        }
    }

    private void switchButtonInverted(boolean isFirstInit) {
        switch (currentFragmentResId) {
            case R.id.radio_button_navi:
                if (isFirstInit) {
                    //                    naviCenterRadioButton.setBackground(getResources().getDrawable(R.drawable.recommend_15, null));
                    naviCenterRadioButton.setBackgroundResource(R.drawable.recommend_15);
                } else {
                    //                    naviCenterRadioButton.setBackground(getResources().getDrawable(R.drawable.recommend_anim_inverted, null));
//                    naviCenterRadioButton.setBackground(XmSkinManager.getInstance().getDrawable(R.drawable.recommend_anim_inverted, null));
                    naviCenterRadioButton.setBackgroundResource(R.drawable.recommend_anim_inverted);
                    AnimationDrawable mNaviAnimation = (AnimationDrawable) naviCenterRadioButton.getBackground();
                    mNaviAnimation.start();
                }
                break;
            case R.id.radio_button_music:
                if (isFirstInit) {
                    //                    musicCenterRadioButton.setBackground(getResources().getDrawable(R.drawable.music_15, null));
                    musicCenterRadioButton.setBackgroundResource(R.drawable.music_15);
                } else {
//                    musicCenterRadioButton.setBackground(XmSkinManager.getInstance().getDrawable(R.drawable.music_anim_inverted, null));
                    musicCenterRadioButton.setBackgroundResource(R.drawable.music_anim_inverted);
                    AnimationDrawable mAudioAnimation = (AnimationDrawable) musicCenterRadioButton.getBackground();
                    mAudioAnimation.start();
                }
                break;
            case R.id.radio_button_service:
                if (isFirstInit) {
                    //                    serviceCenterRadioButton.setBackground(getResources().getDrawable(R.drawable.service_15, null));
                    serviceCenterRadioButton.setBackgroundResource(R.drawable.service_15);
                } else {
//                    serviceCenterRadioButton.setBackground(XmSkinManager.getInstance().getDrawable(R.drawable.service_anim_inverted, null));
                    serviceCenterRadioButton.setBackgroundResource(R.drawable.service_anim_inverted);
                    AnimationDrawable mServiceAnimation = (AnimationDrawable) serviceCenterRadioButton.getBackground();
                    mServiceAnimation.start();
                }
                break;
            case R.id.radio_button_app:
                if (isFirstInit) {
                    //                    appCenterRadioButton.setBackground(getResources().getDrawable(R.drawable.app_15, null));
                    appCenterRadioButton.setBackgroundResource(R.drawable.app_15);
                } else {
//                    appCenterRadioButton.setBackground(XmSkinManager.getInstance().getDrawable(R.drawable.app_anim_inverted, null));
                    appCenterRadioButton.setBackgroundResource(R.drawable.app_anim_inverted);
                    AnimationDrawable mAppAnimation = (AnimationDrawable) appCenterRadioButton.getBackground();
                    mAppAnimation.start();
                }
                break;
            default:
                break;
        }
    }

    private void switchFragment(Class<? extends Fragment> clz) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        String newFmtTag = clz.getName();
        Fragment existFmt = fragmentManager.findFragmentByTag(newFmtTag);
        if (existFmt != null) {
            transaction.setCustomAnimations(
                    R.animator.slide_scale_in_nomal, R.animator.slide_scale_out_normal,
                    R.animator.slide_scale_in_nomal, R.animator.slide_scale_out_normal);
        } else {
            transaction.setCustomAnimations(
                    R.animator.slide_scale_other_in_init, R.animator.slide_scale_other_out_init,
                    R.animator.slide_scale_other_in_init, R.animator.slide_scale_other_out_init);
        }
//        boolean isNav = (clz == NaviSupportFragment.class);
        if (existFmt != null) {
//            if (isNav && existFmt != NavFragmentHolder.getInstance()) {
//                transaction.remove(existFmt)
//                        .add(R.id.fl_common, NavFragmentHolder.getInstance(), NaviSupportFragment.class.getName());
//            } else {
            if (existFmt.isHidden()) {
                transaction.show(existFmt);
            }
//            }
        } else {
            Fragment newFmt;
            newFmt = Fragment.instantiate(this, clz.getName());
//            if (isNav) {
//                newFmt = NavFragmentHolder.getInstance();
            if (newFmt instanceof NaviSupportFragment) {
                if (!mapCompelete
                        && (newGuide == null || !newGuide.isShowing())) {
                    // 存在新手引导时,不显示Loading
                    showLoadingDialog();
                }
                ((NaviSupportFragment) newFmt).setMapRenderInitedListener(new Listener.SuccinctListener() {
                    @Override
                    public void onEvent() {
                        loadingDialogDismiss();
                    }
                });
            }
//            } else {

//            }
            transaction.add(R.id.fl_common, newFmt, newFmtTag);
        }
        for (Class fmtClz : FRAGMENT_CLZ) {
            if (fmtClz != clz) {
                Fragment fmt = fragmentManager.findFragmentByTag(fmtClz.getName());
                if (fmt != null && !fmt.isHidden()) {
                    transaction.hide(fmt);
                }
            }
        }
        transaction.commitNowAllowingStateLoss();
        changeMenuBtnState();
        // 快速切换Content Tab时,概率性导致此Fragment不显示
        // 具体原因不知,调试时发现此Fragment的生命周期,以及当前Activity的View状态均正常,猜测是系统bug
        // 只要让当前Activity走一次onResume,就会正常显示,因此这里强行开启一个不可见并迅速finish掉的Activity
        startActivity(new Intent(this, FlickerActivity.class));
    }

    private void changeMenuBtnState() {
        changeMenuBtnEnable(false);
        ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
            @Override
            public void run() {
                changeMenuBtnEnable(true);
            }
        }, MENU_BTN_ENABLE);
    }

    private void changeMenuBtnEnable(boolean enabled) {
        if (menuButton == null) {
            return;
        }
        for (int i = 0; i < menuButton.getChildCount(); i++) {
            menuButton.getChildAt(i).setEnabled(enabled);
        }
    }

    @Subscriber(tag = LauncherConstants.RECOMMEND_MESSAGE)
    public void onRecommendMessageShow(String message) {
        try {
            RecommendDataParserManager.getInstance().parserRecommendData(this, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void notifyMsgList() {
        RequestManager.getInstance().getMsgList(getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        // 桌面屏蔽Back按键
    }

    @Override
    protected boolean isAppNeedShowNaviBar() {
        return false;
    }

    private void startPetApp() {
        LaunchUtils.launchApp(this, LauncherConstants.LauncherApp.LAUNCHER_PET_PACKAGE, LauncherConstants.LauncherApp.LAUNCHER_PET_CLASS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (guideDismidssReceiver != null) {
            unregisterReceiver(guideDismidssReceiver);
        }
        if (naviByKeyReceiver != null) {
            unregisterReceiver(naviByKeyReceiver);
        }
        if (naviByPoiReceiver != null) {
            unregisterReceiver(naviByPoiReceiver);
        }
        if (queryCollectReceiver != null) {
            unregisterReceiver(queryCollectReceiver);
        }
        unRegisterClearDataListener();
        if (mSelectModelDialog != null) {
            mSelectModelDialog.dismiss();
        }
        KLog.e("Launcher MainActivity onDestroy");
        EventBus.getDefault().unregister(this);
        if (mFragmentLifecycleCallbacks != null) {
            getSupportFragmentManager().unregisterFragmentLifecycleCallbacks(mFragmentLifecycleCallbacks);
        }
    }

    private void shouldShowGuideWindow() {
        runOnUiThread(() -> {
            try {
                if (!GuideDataHelper.shouldShowGuide(GuideConstants.LAUNCHER_SHOWED, true)) return;
                registerReceiver(guideDismidssReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (LauncherConstants.ASSISTANT_GUIDE_VIEW_SHOW.equals(intent.getAction())) {
                            dismissGuideWindow();
                            KLog.d("MainActivity onReceive: LauncherConstants.ASSISTANT_GUIDE_VIEW_SHOW");
                        }
                    }
                }, new IntentFilter(LauncherConstants.ASSISTANT_GUIDE_VIEW_SHOW));
                newGuide = NewGuide.with(this)
                        .setLebal(GuideConstants.LAUNCHER_SHOWED)
                        .setGuideLayoutId(R.layout.guide_view_pics)
                        .setNextStepViewId(R.id.view_next_step)
                        .setGuidePicViewId(R.id.iv_guide_pic)
                        .setViewSkipId(R.id.view_skip)
                        .setGuidePics(new int[]{R.drawable.guide_pic_one,/* R.drawable.guide_pic_two,*/ R.drawable.guide_pic_three, R.drawable.guide_pic_four})
                        .build();
                newGuide.showGuideWithPics();
                GuideDataHelper.setFirstGuideFalse(GuideConstants.LAUNCHER_SHOWED);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void dismissGuideWindow() {
        try {
            if (newGuide != null) {
                newGuide.dismissGuideWindow();
                newGuide = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registCarWXReceiver() {
        // poi导航
        registerReceiver(naviByPoiReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null) return;
                String name = intent.getStringExtra("name");
                if (TextUtils.isEmpty(name)) return;
                double lat = intent.getDoubleExtra("lat", 0);
                double lon = intent.getDoubleExtra("lon", 0);
                MapManager.getInstance().startNaviToPoi(name, "", lon, lat);
            }
        }, new IntentFilter(LauncherConstants.START_NAVI_BY_POI));

        // 关键字导航
        registerReceiver(naviByKeyReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null) return;
                String keyWords = intent.getStringExtra("key_words");
                if (TextUtils.isEmpty(keyWords)) return;
                MapManager.getInstance().searchAndShowResult(keyWords);
            }
        }, new IntentFilter(LauncherConstants.START_NAVI_BY_KEY_WORDS));
    }

    private void registQueryCollect() {
        Log.d("QBX", "registQueryCollectReceiver: ");
        registerReceiver(queryCollectReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String keyWords = intent.getStringExtra(CenterConstants.QueryCollectConstants.QUERY_TYPE);
                List<PoiFavorite> list = null;
                if (CenterConstants.QueryCollectConstants.QUERY_TYPE_HOME.equals(keyWords)) {
                    list = FavoritesDBManager.getInstance().getShortCutByIndex("home");
                } else if (CenterConstants.QueryCollectConstants.QUERY_TYPE_COMPANY.equals(keyWords)) {
                    list = FavoritesDBManager.getInstance().getShortCutByIndex("company");
                }
                Intent intent1 = new Intent(CenterConstants.QueryCollectConstants.RESPOND_COLLECT_DATA);
                if (list != null && list.size() > 0) {
                    intent1.putExtra(CenterConstants.QueryCollectConstants.RESPOND_LONGITUDE, list.get(0).getPosX());
                    intent1.putExtra(CenterConstants.QueryCollectConstants.RESPOND_LATITUDE, list.get(0).getPosY());
                    intent1.putExtra(CenterConstants.QueryCollectConstants.RESPOND_ADDRESS, list.get(0).getAddress());
                } else {
                    intent1.putExtra(CenterConstants.QueryCollectConstants.RESPOND_LONGITUDE, "");
                    intent1.putExtra(CenterConstants.QueryCollectConstants.RESPOND_LATITUDE, "");
                    intent1.putExtra(CenterConstants.QueryCollectConstants.RESPOND_ADDRESS, "");
                }
                sendBroadcast(intent1);
            }
        }, new IntentFilter(CenterConstants.QueryCollectConstants.REQUEST_COLLECT_DATA));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tab_res_id", currentFragmentResId);
        KLog.e("Launcher MainActivity onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        KLog.e("Launcher MainActivity onRestoreInstanceState");
    }

    @Override
    protected void onPause() {
        super.onPause();
        KLog.e("XM Launcher MainActivity onPause");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        KLog.e("XM Launcher MainActivity onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        KLog.e("XM Launcher MainActivity onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        KLog.e("XM Launcher MainActivity onStop");
    }

    private void unRegisterClearDataListener() {
        LoginTypeManager.getInstance().unRegisterClearDataListener(mAbsClearDataListener);
        unregisterReceiver(mCleanDataBroadcastReceiver);
    }

    private void showLoadingDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingDialog = new CustomProgressDialog(MainActivity.this);
                loadingDialog.setMessage(getString(R.string.base_loading));
                loadingDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        postDelayDialogDismiss();
                    }
                });
                loadingDialog.show();
            }
        });
    }

    private void postDelayDialogDismiss() {
        if (!mapCompelete) {
            mapCompelete = true;
            ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
                @Override
                public void run() {
                    if (loadingDialog != null && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                        loadingDialog = null;
                    }
                }
            }, SELECT_MODEL_DIALOG);
        }
    }

    @Override
    public void onSystemUiVisibilityChange(int visibility) {
        getWindow().getDecorView().setSystemUiVisibility(SystemUiVisibility);
    }

    private static class NavLifecycleCallback extends FragmentManager.FragmentLifecycleCallbacks {
        static final String NavTag = "NavFragment";

        @Override
        public void onFragmentPreCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
            if (isNavFmt(f)) {
                Log.i(NavTag, "NaviSupportFragment # onPreCreated");
            }
        }

        @Override
        public void onFragmentPreAttached(FragmentManager fm, Fragment f, Context context) {
            if (isNavFmt(f)) {
                Log.i(NavTag, "NaviSupportFragment # onPreAttached");
            }
        }

        @Override
        public void onFragmentActivityCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
            if (isNavFmt(f)) {
                Log.i(NavTag, "NaviSupportFragment # onActivityCreated");
            }
        }

        @Override
        public void onFragmentSaveInstanceState(FragmentManager fm, Fragment f, Bundle outState) {
            if (isNavFmt(f)) {
                Log.i(NavTag, "NaviSupportFragment # onSaveInstanceState");
            }
        }

        @Override
        public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {
            if (isNavFmt(f)) {
                Log.i(NavTag, "NaviSupportFragment # onAttached");
            }
        }

        @Override
        public void onFragmentCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
            if (isNavFmt(f)) {
                Log.i(NavTag, "NaviSupportFragment # onCreated");
            }
        }

        @Override
        public void onFragmentViewCreated(FragmentManager fm, Fragment f, View v, Bundle savedInstanceState) {
            if (isNavFmt(f)) {
                Log.i(NavTag, "NaviSupportFragment # onViewCreated");
            }
        }

        @Override
        public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
            if (isNavFmt(f)) {
                Log.i(NavTag, "NaviSupportFragment # onViewDestroyed");
            }
        }

        @Override
        public void onFragmentDetached(FragmentManager fm, Fragment f) {
            if (isNavFmt(f)) {
                Log.i(NavTag, "NaviSupportFragment # onDetached");
            }
        }

        @Override
        public void onFragmentDestroyed(FragmentManager fm, Fragment f) {
            if (isNavFmt(f)) {
                Log.i(NavTag, "NaviSupportFragment # onDestroyed");
            }
        }

        @Override
        public void onFragmentStarted(FragmentManager fm, Fragment f) {
            if (isNavFmt(f)) {
                Log.i(NavTag, "NaviSupportFragment # onStarted");
            }
        }

        @Override
        public void onFragmentResumed(FragmentManager fm, Fragment f) {
            if (isNavFmt(f)) {
                Log.i(NavTag, "NaviSupportFragment # onResumed");
            }
        }

        @Override
        public void onFragmentPaused(FragmentManager fm, Fragment f) {
            if (isNavFmt(f)) {
                Log.i(NavTag, "NaviSupportFragment # onPaused");
            }
        }

        @Override
        public void onFragmentStopped(FragmentManager fm, Fragment f) {
            if (isNavFmt(f)) {
                Log.i(NavTag, "NaviSupportFragment # onStopped");
            }
        }

        private boolean isNavFmt(Fragment f) {
            return f != null && f.getClass() == NaviSupportFragment.class;
        }
    }


    @Override
    public void applySkin() {
        if (naviCenterRadioButton != null && musicCenterRadioButton != null &&
                serviceCenterRadioButton != null && appCenterRadioButton != null) {
            naviCenterRadioButton.setBackground(XmSkinManager.getInstance().getDrawable(R.drawable.recommend_00, null));
            musicCenterRadioButton.setBackground(XmSkinManager.getInstance().getDrawable(R.drawable.music_00, null));
            serviceCenterRadioButton.setBackground(XmSkinManager.getInstance().getDrawable(R.drawable.service_00, null));
            appCenterRadioButton.setBackground(XmSkinManager.getInstance().getDrawable(R.drawable.app_00, null));
            switch (currentFragmentResId) {
                case R.id.radio_button_navi:
                    naviCenterRadioButton.setBackground(XmSkinManager.getInstance().getDrawable(R.drawable.recommend_15));
                    break;
                case R.id.radio_button_music:
                    musicCenterRadioButton.setBackground(XmSkinManager.getInstance().getDrawable(R.drawable.music_15));
                    break;
                case R.id.radio_button_service:
                    serviceCenterRadioButton.setBackground(XmSkinManager.getInstance().getDrawable(R.drawable.service_15));
                    break;
                case R.id.radio_button_app:
                    appCenterRadioButton.setBackground(XmSkinManager.getInstance().getDrawable(R.drawable.app_15));
                    break;
            }
        }

    }

    @Subscriber(tag = LauncherConstants.SHOW_FUEL_WARNING)
    public void showFuelWaring(String message) {
        KLog.e(TAG, "showFuelWaring() message=" + message);
        if (StringUtil.isEmpty(message)) {
            return;
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(message);
            String title = jsonObject.optString("title");
            String image = jsonObject.optString("image");
            if (StringUtil.isEmpty(title)) {
                return;
            }
            RecommendDialog recommendDialog = new RecommendDialog(this);
            recommendDialog.setOnRecommendClick(new RecommendDialog.OnRecommendClick() {
                @Override
                public void sureClick() {
                    Intent gasIntent = new Intent(MainActivity.this, NearByOilParkActivity.class);
                    gasIntent.putExtra("type", "NearbyGasStation");
                    gasIntent.putExtra("poi_type", "加油站");
                    startActivity(gasIntent);
                }
            });
            recommendDialog.show();
            recommendDialog.setDialogType("gas");
            recommendDialog.setDialogMessage(title);
            recommendDialog.setIvRecommend(image);
            recommendDialog.setMsgColor("#FFFFFF");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscriber(tag = LauncherConstants.IS_FUEL_WARNING)
    public void isFuelWaring(int active) {
        KLog.e(TAG, "isFuelWaring() active=" + active);
        if (active == 0) {
            ThreadDispatcher.getDispatcher().post(new Runnable() {
                @Override
                public void run() {
                    RequestManager.getInstance().getMqttOil(new ResultCallback<XMResult<MqttInfo>>() {

                        @Override
                        public void onSuccess(XMResult<MqttInfo> result) {

                        }

                        @Override
                        public void onFailure(int code, String msg) {
                            KLog.e(TAG, "MqttManager getMqttInfo onFailure() code = " + code + " msg = " + msg);
                        }
                    });
                }
            });
        }
    }
}
