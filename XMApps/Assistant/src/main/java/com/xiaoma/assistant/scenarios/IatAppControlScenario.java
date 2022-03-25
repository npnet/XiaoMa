package com.xiaoma.assistant.scenarios;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;

import com.mapbar.xiaoma.manager.XmMapNaviManager;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.callback.WrapperSynthesizerListener;
import com.xiaoma.assistant.constants.AssistantConstants;
import com.xiaoma.assistant.manager.AssistantManager;
import com.xiaoma.assistant.manager.api.AudioManager;
import com.xiaoma.assistant.manager.api.MusicApiManager;
import com.xiaoma.assistant.manager.api.RadioApiManager;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.model.parser.ParserLocationParam;
import com.xiaoma.assistant.utils.Constants;
import com.xiaoma.assistant.utils.OpenAppUtils;
import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.XmCarConfigManager;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.center.IClientCallback;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.model.Response;
import com.xiaoma.component.AppHolder;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.component.nodejump.NodeUtils;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.login.common.LoginConstants;
import com.xiaoma.model.User;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.LaunchUtils;
import com.xiaoma.utils.constant.VrConstants;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.logintype.callback.OnBlockCallback;
import com.xiaoma.utils.logintype.constant.LoginCfgConstant;
import com.xiaoma.utils.logintype.manager.LoginType;
import com.xiaoma.utils.logintype.manager.LoginTypeManager;
import com.xiaoma.vr.tts.OnTtsListener;
import com.xiaoma.wechat.manager.WeChatManager;
import com.xiaoma.wechat.manager.WeChatManagerFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.KEYGUARD_SERVICE;

/**
 * @author: iSun
 * @date: 2019/2/18 0018
 * App控制
 */
class IatAppControlScenario extends IatScenario {
    private String[] okAnswers = {"打开了", "好嘞", "诺"};
    private String[] opRobotSuccess = {"我来了~", "别急别急，马上出来", "这么久才想起来我，蓝瘦", "还是对我念念不忘"};
    private String[] opRobotFail = {"我好像没有这个装备哎", "当我是土地爷啊，会从地底下钻出来？", "仪表板上抠个洞，我立马出来见你", "见我之前先背诵一遍《蜀道难》"};
    private String[] okHomeAnswers = {"好嘞", "嗯"};

    private String[] appNames;
    private String[] appPackages;
    private HashMap<String, String> appInfoMap;
    private static final int SKY_WINDOW_OFF = 0;

    public IatAppControlScenario(Context context) {
        super(context);
    }

    @Override
    public void init() {
        appNames = context.getResources().getStringArray(R.array.app_name);
        appPackages = context.getResources().getStringArray(R.array.app_package);
        appInfoMap = new HashMap<>();
        for (int i = 0; i < appNames.length; i++) {
            appInfoMap.put(appNames[i], appPackages[i]);
        }
        getAllAppInfo();
    }

    private void getAllAppInfo() {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(intent, 0);
        for (int i = 0; i < resolveInfos.size(); i++) {
            ResolveInfo resolveInfo = resolveInfos.get(i);
            if (resolveInfo.activityInfo.packageName.startsWith("com.xiaoma")
                    || resolveInfo.activityInfo.packageName.startsWith("com.xylink.mc.leopaard")) {//过滤小马包名
                String name = resolveInfo.activityInfo.loadLabel(context.getPackageManager()).toString();
                if (!appInfoMap.containsKey(name)) {
                    appInfoMap.put(name, resolveInfo.activityInfo.packageName);
                }
                KLog.d("hzx", "app name: " + name + ", packageName: " + resolveInfo.activityInfo.packageName);
            }
        }
    }

    @Override
    public void onParser(String voiceJson, LxParseResult parseResult, ParserLocationParam location, long session) {
        String operation = parseResult.getOperation();
        try {
            JSONObject slots = new JSONObject(parseResult.getSlots());
            boolean isOpen = false;
            if (slots.has("name")) {
                if ("LAUNCH".equals(operation)) {
                    isOpen = true;
                }
                String name = slots.getString("name");
                controlApp(name, isOpen, parseResult);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void controlApp(String name, boolean isOpen, LxParseResult parseResult) {
        // TODO: 2019/2/18 0018 控制接口

        if ("全景影像".equals(name)) {
            if ("LAUNCH".equals(parseResult.getOperation())) {
                XmCarVendorExtensionManager.getInstance().onAvsSwitch();
                speakContent(context.getString(R.string.ok), new WrapperSynthesizerListener() {
                    @Override
                    public void onCompleted() {
                        closeVoicePopup();
                    }
                });
                return;
            } else if ("关闭全景影像".equals(parseResult.getText())) {
                XmCarVendorExtensionManager.getInstance().closeAvs();
                speakContent(context.getString(R.string.ok), new WrapperSynthesizerListener() {
                    @Override
                    public void onCompleted() {
                        closeVoicePopup();
                    }
                });
                return;
            }
        }

        if (("系统设置").equals(name)) {
            name = "设置";
        }
        if (appInfoMap.containsKey(name)) {
            openApp(appInfoMap.get(name), isOpen);
            return;
        }
        String speakText = "";
        if (!LoginTypeManager.getInstance().canUse(name, new OnBlockCallback() {
            @Override
            public void handle(LoginType loginType) {
                closeAfterSpeak(LoginTypeManager.getPrompt(context));
            }
        })) return;
        switch (name) {
            case Constants.ParseKey.OPEN_ROBOTMODE:
                //TODO
             /*   if (XmCarConfigManager.hasHologram()){
                    setRobAction(AssistantConstants.RobActionKey.OPEN_HOLOGRAPHIC_IMAGE);
                    XmCarFactory.getCarVendorExtensionManager().setRobSwitcher(SDKConstants.SWITCH_ROBOT_ON);
                    speakText = opRobotSuccess[new Random().nextInt(4)];
                }else {
                    speakText = opRobotFail[new Random().nextInt(4)];
                }*/
                break;
            case Constants.ParseKey.DATA_MANAGER:
            case Constants.ParseKey.CALENDAR:  //打开日历
                //TODO
                setRobAction(AssistantConstants.RobActionKey.OPEN_CALENDAR);
                openCalendar("");
                return;
            case Constants.ParseKey.OFF_ROBOTMODE:
                //TODO
              /*  if (XmCarConfigManager.hasHologram()){
                    setRobAction(AssistantConstants.RobActionKey.CLOSE_HOLOGRAPHIC_IMAGE);
                    XmCarFactory.getCarVendorExtensionManager().setRobSwitcher(SDKConstants.SWITCH_ROBOT_OFF);
                    speakText = context.getString(R.string.off_robotmode_success);
                }else {
                    speakText = "我好像没有这个装备哎";
                }
             */
                break;
            case Constants.ParseKey.WIFI:
                speakText = enableWifi(isOpen);
                break;
            case Constants.ParseKey.MARK:
                if (XmCarConfigManager.hasJourneyRecord()) {
                    speakContent(getString(R.string.ok), new WrapperSynthesizerListener() {
                        @Override
                        public void onCompleted() {
                            closeVoicePopup();
                            LaunchUtils.launchAppOnlyNewTask(context, CenterConstants.LAUNCHER
                                    , "com.xiaoma.launcher.mark.ui.activity.MarkMainActivity", null);

                        }
                    });
                }else {
                    speakMultiToneListening(getString(R.string.no_device_speak),getString(R.string.no_device));
                }

                return;
            case Constants.ParseKey.WIFI_SETTING:
                if (!isOpen) return;
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_OPEN_WIFI_SETTING);
                boolean openWifi = NodeUtils.jumpTo(context, Constants.SystemSetting.PACKAGE_NAME_SETTING,
                        "com.xiaoma.setting.main.ui.MainActivity",
                        NodeConst.Setting.ASSISTANT_ACTIVITY + "/" + NodeConst.Setting.WIFI_SETTINGS);
                if (openWifi) {
                    closeAfterSpeak(getString(R.string.ok));
                } else {
                    closeAfterSpeak(getString(R.string.please_install_settings_wifi));
                }
                return;
            case Constants.ParseKey.BLUETOOTH:
                speakText = enableBluetooth(isOpen);
                break;
            case Constants.ParseKey.ELECTRONIC_DOG:
            case Constants.ParseKey.ELECTRONIC_EYE:
                // 开启/关闭电子眼
                speakText = controlRoadCamera(isOpen);
                break;
            case Constants.ParseKey.SHARED_HOT_SPOT://共享热点
            case Constants.ParseKey.HOT_SPOT://热点
            case Constants.ParseKey.WIFI_HOT_SPOT://wifi热点
            case Constants.ParseKey.NET_HOT_SPOT://网络共享
            case Constants.ParseKey.NET_HOT://网络热点

                // 开启/关闭网络热点
                XmCarFactory.getSystemManager().setWorkPattern(isOpen ? SDKConstants.WifiMode.AP : SDKConstants.WifiMode.OFF);
                speakText = isOpen ? context.getString(R.string.turn_on_network_hot_spot) : context.getString(R.string.turn_off_network_hot_spot);
                setRobAction(isOpen ? AssistantConstants.RobActionKey.ROB_ACTION_OPEN_WIFI : AssistantConstants.RobActionKey.ROB_ACTION_CLOSE_WIFI);
                break;
            case Constants.ParseKey.SUNSHADE:
                setRobAction(isOpen ? AssistantConstants.RobActionKey.CAR_CONTROL_OPEN : AssistantConstants.RobActionKey.CAR_CONTROL_CLOSE);
                if (!isOpen) {
                    XmCarFactory.getCarCabinManager().setTopWindowPos(SKY_WINDOW_OFF);
                }
                XmCarFactory.getCarCabinManager()
                        .setUmbrellaPos(isOpen ? IatCarControlScenario.SUNSHADE_ON : IatCarControlScenario.SUNSHADE_OFF);
                speakText = getString(R.string.ok);
                break;
            case Constants.ParseKey.BOARDING_LIGHTING:
                // 开启/关闭登车照明    需求已删除

                break;
            case Constants.ParseKey.OUT_OF_CAR_LIGHTING:
                // 开启/关闭离车照明    需求已删除

                break;
            case Constants.ParseKey.BLUETOOTH_SETTINGS:
                if (!isOpen) return;
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_OPEN_BLUETOOTH);
                if (OpenAppUtils.openApp(context.getApplicationContext(), CenterConstants.SETTING)) {
                    speakContent(getString(R.string.ok), new WrapperSynthesizerListener() {
                        @Override
                        public void onCompleted() {
                            closeVoicePopup();
                            NodeUtils.jumpTo(context
                                    , Constants.SystemSetting.PACKAGE_NAME_SETTING
                                    , "com.xiaoma.setting.main.ui.MainActivity"
                                    , NodeConst.Setting.ASSISTANT_ACTIVITY + "/" + NodeConst.Setting.BLUETOOTH_SETTINGS);
                        }
                    });
                } else {
                    closeAfterSpeak(getString(R.string.please_install_settings_first));
                }
                return;
            case Constants.ParseKey.LOCAL_STATION:
            case Constants.ParseKey.RADIO:
                if (isOpen) {
                    RadioApiManager.getInstance().playLocalRadioStation();
                } else {
                    RadioApiManager.getInstance().pauseRadioStation(context.getString(R.string.close_this_song));
                }
                return;
            case Constants.ParseKey.NAVI:
                if (isOpen) {
                    if (XmMapNaviManager.getInstance().isPathPlanSuccess()) {
                        setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_SELECT_LINE);
                        XmMapNaviManager.getInstance().startNavi();
                    } else {
                        speakText = context.getString(R.string.not_route_planning_state);
                    }
                } else {
                    if (XmMapNaviManager.getInstance().isNaviing()) {
                        setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_STOP_NAVI);
                        XmMapNaviManager.getInstance().cancelNavi();
                        speakText = context.getString(R.string.navigation_is_over);
                    } else {
                        speakText = context.getString(R.string.not_navi_state);
                    }
                }
                break;
            case Constants.ParseKey.LOCAL_VIDEO:
            case Constants.ParseKey.LOCAL_PICTURE:
            case Constants.ParseKey.USB_PICTURE:
                String node = NodeConst.XKAN.ASSISTANT_ACTIVITY
                        + "/" + NodeConst.XKAN.OPEN_PICTURE;
                if (Constants.ParseKey.LOCAL_VIDEO.equals(name)) {
                    node = NodeConst.XKAN.ASSISTANT_ACTIVITY
                            + "/" + NodeConst.XKAN.OPEN_VIDEO;
                }
                setRobAction(AssistantConstants.RobActionKey.MUSIC_PLAY_CONTROL);
                if (AudioManager.getInstance().isUsbConnected()) {
                    if (!OpenAppUtils.checkPackInfo(context, CenterConstants.XKAN)) {
                        closeAfterSpeak(getString(R.string.please_install_xkan));
                        return;
                    } else {
                        NodeUtils.jumpTo(context
                                , CenterConstants.XKAN
                                , "com.xiaoma.xkan.main.ui.MainActivity"
                                , node);
                        closeAfterSpeak(R.string.ok);
                        return;
                    }
                } else {
                    addFeedbackAndSpeakMultiTone(context.getString(R.string.no_usb), context.getString(R.string.no_usb_speak), new WrapperSynthesizerListener() {
                        @Override
                        public void onCompleted() {
                            AssistantManager.getInstance().closeAssistant();
                        }
                    });
                    return;
                }
            case Constants.ParseKey.RECOGNIZE_SONG:
                setRobAction(AssistantConstants.RobActionKey.PLAY_MUSIC);
                recognizeSong();
                return;
            //打开语音训练
            case Constants.ParseKey.VR_PRACTICE:
                openVrPractice(isOpen);
                return;
            case Constants.ParseKey.USB_MUSIC:
                setRobAction(AssistantConstants.RobActionKey.PLAY_MUSIC_SOURCE);
                if (isOpen) {
                    final OpenAppUtils.PendingHandle handleUsb = OpenAppUtils.playUsbMusic();
                    if ((handleUsb == null)) {
                        addFeedbackAndSpeakMultiTone(context.getString(R.string.no_usb), context.getString(R.string.no_usb_speak), new WrapperSynthesizerListener() {
                            @Override
                            public void onCompleted() {
                                AssistantManager.getInstance().closeAssistant();
                            }
                        });
                        return;
                    } else {
                        speakContent(context.getString(R.string.ok), new WrapperSynthesizerListener() {
                            @Override
                            public void onCompleted() {
                                AssistantManager.getInstance().closeAssistant();
                                handleUsb.jumpUsbMusic(context);
                                MusicApiManager.getInstance().play(AudioConstants.AudioTypes.MUSIC_LOCAL_USB);
                            }
                        });
                    }
                } else {
                    if (AudioManager.getInstance().isUsbMusicPlaying()) {
                        speakContent(context.getString(R.string.ok), new WrapperSynthesizerListener() {
                            @Override
                            public void onCompleted() {
                                MusicApiManager.getInstance().pause();
                                Intent intent = new Intent();
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setAction(Constants.CLOSE_APP + "com.xiaoma.music");
                                context.sendBroadcast(intent);
                                assistantManager.speakThenClose(okCloseAnswers[new Random().nextInt(3)]);
                            }
                        });
                    } else {
                        closeAfterSpeak(context.getString(R.string.no_usb_music_playing));
                    }
                }
                return;
            case Constants.ParseKey.BLUETOOTH_MUSIC:
                setRobAction(AssistantConstants.RobActionKey.PLAY_MUSIC_SOURCE);
                if (isOpen) {
                    final OpenAppUtils.PendingHandle handleBlutooth = OpenAppUtils.playBlutoothMusic();
                    if ((handleBlutooth == null)) {
                        addFeedbackAndSpeakMultiTone(context.getString(R.string.no_bluetooth), context.getString(R.string.no_bluetooth_speak), new WrapperSynthesizerListener() {
                            @Override
                            public void onCompleted() {
                                AssistantManager.getInstance().closeAssistant();
                            }
                        });
                        return;
                    } else {
                        speakContent(context.getString(R.string.ok), new WrapperSynthesizerListener() {
                            @Override
                            public void onCompleted() {
                                AssistantManager.getInstance().closeAssistant();
                                handleBlutooth.jumpBlutoothMusic(context);
                                MusicApiManager.getInstance().play(AudioConstants.AudioTypes.MUSIC_LOCAL_BT);
                            }
                        });
                    }
                } else {
                    if (AudioManager.getInstance().isBluetoothMusicPlaying()) {
                        speakContent(context.getString(R.string.ok), new WrapperSynthesizerListener() {
                            @Override
                            public void onCompleted() {
                                MusicApiManager.getInstance().pause();
                                Intent intent = new Intent();
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setAction(Constants.CLOSE_APP + "com.xiaoma.music");
                                context.sendBroadcast(intent);
                                assistantManager.speakThenClose(okCloseAnswers[new Random().nextInt(3)]);
                            }
                        });
                    } else {
                        closeAfterSpeak(context.getString(R.string.no_bluetooth_music_playing));
                    }
                }
                return;
            case Constants.ParseKey.VOICE_ASSISTANT:
                AssistantManager.getInstance().startListening(false);
                return;
            case Constants.ParseKey.CAR_HOME_NET://车家互联
                if (!LoginTypeManager.getInstance().canUse(LoginCfgConstant.XIAOMA_SMART_HOME, new OnBlockCallback() {
                    @Override
                    public void handle(LoginType loginType) {
                        addFeedbackAndSpeak(LoginTypeManager.getPrompt(context), new WrapperSynthesizerListener() {
                            @Override
                            public void onCompleted() {
                                AssistantManager.getInstance().closeAssistant();
                            }
                        });
                    }
                })) return;
                openSmartHome();
                return;
            default:
                break;
        }
        if (TextUtils.isEmpty(speakText)) {
            closeVoicePopup();
        } else {
            closeAfterSpeak(speakText);
        }
    }

    private void openSmartHome() {
        Intent intent = new Intent();
        ComponentName name = new ComponentName("com.xiaoma.launcher", "com.xiaoma.smarthome.login.ui.MainActivity");
        intent.setComponent(name);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
            context.startActivity(intent);
            speakContent(okAnswers[new Random().nextInt(okAnswers.length)], new WrapperSynthesizerListener() {
                @Override
                public void onCompleted() {
                    AssistantManager.getInstance().closeAssistant();
                }
            });
            return;
        }
        speakContent(getString(R.string.app_not_install), new WrapperSynthesizerListener() {
            @Override
            public void onCompleted() {
                AssistantManager.getInstance().closeAssistant();
            }
        });
    }

    private String enableBluetooth(boolean isOpen) {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        ThreadDispatcher.getDispatcher().post(new Runnable() {
            @Override
            public void run() {
                if (isOpen) {
                    btAdapter.enable();
                } else {
                    btAdapter.disable();
                }
            }
        });
        setRobAction(isOpen ? AssistantConstants.RobActionKey.ROB_ACTION_OPEN_BLUETOOTH : AssistantConstants.RobActionKey.ROB_ACTION_CLOSE_BLUETOOTH);
        return isOpen ? context.getString(R.string.tts_enable_bluetooth) : context.getString(R.string.tts_disable_bluetooth);
    }

    private String enableWifi(boolean isOpen) {
        XmCarFactory.getSystemManager().setWorkPattern(isOpen ? SDKConstants.WifiMode.STA : SDKConstants.WifiMode.OFF);
        setRobAction(isOpen ? AssistantConstants.RobActionKey.ROB_ACTION_OPEN_WIFI : AssistantConstants.RobActionKey.ROB_ACTION_CLOSE_WIFI);
        return isOpen ? context.getString(R.string.tts_enable_wifi) : context.getString(R.string.tts_disable_wifi);
    }

    private void openApp(final String packageName, boolean isOpen) {
        // 账号权限处理
        if (isOpen && !canUse(packageName)) return;
        if (isOpen) {
            boolean isSuccess;
            if (TextUtils.equals(packageName, Constants.Launcher.PACKAGE_LAUNCHER)) {
                //                isSuccess = LaunchUtils.launchApp(context, packageName, Constants.Launcher.ACTIVITY_MAIN);
                returnToWindow();
                resultForOpen(okHomeAnswers[new Random().nextInt(2)], packageName);
                return;
            } else if (TextUtils.equals(packageName, Constants.Launcher.WECHAT)) {
                openWechat();
                return;
            } else if (TextUtils.equals(packageName, Constants.Launcher.PACKAGE_ASSISTANT)) {
                openAssistant();
                return;
            } else {
                isSuccess = OpenAppUtils.openApp(context.getApplicationContext(), packageName);
                if (!isSuccess) {
                    closeAfterSpeak(getString(R.string.app_not_install));
                }
            }
            if (isSuccess) {
                resultForOpen(okAnswers[new Random().nextInt(3)], packageName);
            }
        } else {
            if (TextUtils.equals(packageName, "com.xiaoma.music")) {
                if (AudioManager.getInstance().isMusicPlaying()){
                    MusicApiManager.getInstance().pause();
                }
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(Constants.CLOSE_APP + packageName);
                context.sendBroadcast(intent);
            } else if (TextUtils.equals(packageName, "com.xiaoma.xting")) {
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(Constants.CLOSE_APP + packageName);
                context.sendBroadcast(intent);
            } else if (TextUtils.equals(packageName, "com.xylink.mc.faw.bab2019")) {
                try {
                    ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                    activityManager.forceStopPackage("com.xylink.mc.faw.bab2019");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (!TextUtils.equals(packageName, "com.xiaoma.assistant")) {
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction(Constants.CLOSE_APP + packageName);
                    context.sendBroadcast(intent);
                }
            }
            assistantManager.speakThenClose(okCloseAnswers[new Random().nextInt(3)]);
            setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_CLOSE_APP);


        }
    }

    private void openAssistant() {
        Intent intent = new Intent();
        intent.setAction(VrConstants.Actions.SHOW_ASSSISTANT_DIALOG);
        AppHolder.getInstance().getAppContext().sendBroadcast(intent);
    }

    private void openWechat() {
        if (!WeChatManager.getInstance().checkWxExist()) {
            addFeedbackAndSpeak(context.getString(R.string.please_install_wechat_first), new WrapperSynthesizerListener() {
                @Override
                public void onCompleted() {
                    closeVoicePopup();
                }
            });
            return;
        }
        setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI_SET_HOME);
       /* boolean logined = WeChatManagerFactory.getManager().isLogined();
        if (!logined) {
            closeVoicePopup();
           *//* addFeedbackAndSpeak(context.getString(R.string.please_login_weixin_first), new WrapperSynthesizerListener() {
                @Override
                public void onCompleted() {
                    closeVoicePopup();
                }

                @Override
                public void onError(int code) {
                    closeVoicePopup();
                }
            });*//*
        } else {
            closeVoicePopup();
        }*/
        addFeedbackAndSpeak(context.getString(R.string.ok), new WrapperSynthesizerListener() {
            @Override
            public void onCompleted() {
                WeChatManagerFactory.getManager().startWechat();
                closeVoicePopup();
            }
        });
    }

    private void resultForOpen(String text, final String packageName) {
        setRobAction(14);
        assistantManager.speakContent(text, new WrapperSynthesizerListener() {
            @Override
            public void onCompleted() {
                assistantManager.closeAssistant();
                if (TextUtils.equals(packageName, "com.xiaoma.music")) {
                    MusicApiManager.getInstance().playLastAudioSource();
                } else if (TextUtils.equals(packageName, "com.xiaoma.xting")) {
                    if (AudioManager.getInstance().isOnlineFMType()) {
                        RadioApiManager.getInstance().continuePlayProgram();
                    } else if (AudioManager.getInstance().isLocalFMType()) {
                        RadioApiManager.getInstance().playLocalRadioStationNoSpeak();
                    }else {
                        MusicApiManager.getInstance().playLastAudioSource();
                    }
                }
            }
        });
    }

    public void returnToWindow() {
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.addCategory(Intent.CATEGORY_HOME);
        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(home);
        setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_CLOSE_APP);
    }

    //某些账号无法使用某些功能
    private boolean canUse(final String packageName) {
        if (TextUtils.isEmpty(packageName)) return true;
        if (!LaunchUtils.isAppInstalled(context, packageName)) return true;
        return LoginTypeManager.getInstance().judgeUse(packageName, new OnBlockCallback() {

            @Override
            public boolean onKeyVerification(LoginType loginType) {
                User user = UserManager.getInstance().getUser(LoginManager.getInstance().getLoginUserId());
                if (user == null) return true;
                if (TextUtils.isEmpty(packageName)) return true;
                Bundle bundle = new Bundle();
                bundle.putParcelable(LoginConstants.KeyBind.BundleKey.USER, user);
                bundle.putBoolean(LoginConstants.KeyBind.BundleKey.APP_NEED_MODIFY_THEME, true);
                LoginTypeManager.getInstance().keyVerificationAndStartApp(context, bundle, packageName);
                return true;
            }

            @Override
            public boolean onChooseAccount(LoginType loginType) {
                Bundle bundle = new Bundle();
                bundle.putBoolean(LoginConstants.KeyBind.BundleKey.APP_NEED_MODIFY_THEME, true);
                try {
                    LoginTypeManager.getInstance().chooseAccount(context, bundle, LoginCfgConstant.PERSONAL);
                    resultForOpen(okAnswers[new Random().nextInt(3)], packageName);
                } catch (Exception e) {
                    closeAfterSpeak(getString(R.string.open_error));
                }
                return true;
            }

            @Override
            public boolean onShowToast(LoginType loginType) {
                closeAfterSpeak(getString(com.xiaoma.utils.R.string.account_permission_prompt));
                return true;
            }

        });
    }


    private String controlRoadCamera(boolean isOpen) {
        setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
        if (isOpen) {
            XmMapNaviManager.getInstance().setCameraBroadcastType(1);
            XmMapNaviManager.getInstance().setCameraBroadcastType(2);
            XmMapNaviManager.getInstance().setCameraBroadcastType(3);
            return context.getString(R.string.open_road_camera);
        } else {
            XmMapNaviManager.getInstance().setCameraBroadcastType(0);
            return context.getString(R.string.close_road_camera);
        }
    }

    @Override
    public void onChoose(String voiceText) {

    }

    @Override
    public boolean isIntercept() {
        return false;
    }

    @Override
    public void onEnd() {

    }

    private boolean isBackgroundRunning() {
        String processName = "com.ybs.demo_ybs";
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(KEYGUARD_SERVICE);
        if (activityManager == null)
            return false;
        // get running application processes
        List<ActivityManager.RunningAppProcessInfo> processList = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo process : processList) {
             /*
            BACKGROUND=400 EMPTY=500 FOREGROUND=100
            GONE=1000 PERCEPTIBLE=130 SERVICE=300 ISIBLE=200
             */
            if (process.processName.startsWith(processName)) {
                boolean isBackground = process.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                        && process.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;
                boolean isLockedState = keyguardManager
                        .inKeyguardRestrictedInputMode();
                return isBackground || isLockedState;
            }
        }
        return false;
    }
}
