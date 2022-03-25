package com.xiaoma.assistant.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.mapbar.android.mapbarnavi.PoiBean;
import com.mapbar.xiaoma.manager.XmMapNaviManager;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.callback.WrapperSynthesizerListener;
import com.xiaoma.assistant.constants.AssistantConstants;
import com.xiaoma.assistant.manager.api.ApiManager;
import com.xiaoma.assistant.manager.api.AudioManager;
import com.xiaoma.assistant.manager.api.BluetoothPhoneApiManager;
import com.xiaoma.assistant.manager.api.MusicApiManager;
import com.xiaoma.assistant.manager.api.RadioApiManager;
import com.xiaoma.assistant.model.FlowBean;
import com.xiaoma.assistant.model.FlowMarginBean;
import com.xiaoma.assistant.processor.AssistantProcessorChain;
import com.xiaoma.assistant.scenarios.IatInstructionScenario;
import com.xiaoma.assistant.scenarios.ScenarioDispatcher;
import com.xiaoma.assistant.utils.Constants;
import com.xiaoma.assistant.utils.UnitConverUtils;
import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.manager.XmCarAudioManager;
import com.xiaoma.carlib.manager.XmCarConfigManager;
import com.xiaoma.center.IClientCallback;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.model.Response;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.component.nodejump.NodeUtils;
import com.xiaoma.login.LoginManager;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.player.AudioConstants;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.AppUtils;
import com.xiaoma.utils.AssetUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.LaunchUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.XmProperties;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.logintype.constant.LoginCfgConstant;
import com.xiaoma.utils.logintype.manager.LoginTypeManager;
import com.xiaoma.utils.screentool.ScreenControlUtil;
import com.xiaoma.vrfactory.tts.XmTtsManager;
import com.xiaoma.wechat.manager.WeChatManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.xiaoma.vr.VoiceConfigManager.IS_VOICE_WITHOUT_WAKE;

/**
 * @author: iSun
 * @date: 2019/4/26 0026
 */
public class GlobalWakeupProcess {
    private static GlobalWakeupProcess instance;
    private final Context mContext;
    private List<String> keyWords = new ArrayList<>();
    private String TAG = GlobalWakeupProcess.class.getSimpleName();
    private static final String PICTURE_VIEW_PAGE_CLASS_NAME = "com.xiaoma.xkan.picture.ui.XmPhotoActivity";
    private List<String> allowKeyWord = new ArrayList();//不受免唤醒开关影响
    private int[] acceptCallWord = new int[]{R.string.accept_call, R.string.ok, R.string.here};
    private int[] rejectCallWord = new int[]{R.string.reject_call, R.string.ok, R.string.here};
    //随机动作
    public static int[] mAction = new int[]{AssistantConstants.RobActionKey.PLAY_CONTROL_RANDOM1
            , AssistantConstants.RobActionKey.PLAY_CONTROL_RANDOM2
            , AssistantConstants.RobActionKey.PLAY_CONTROL_RANDOM3};


    private int[] finishWord = new int[]{R.string.ok, R.string.here, R.string.now_take_photo};

    public static GlobalWakeupProcess getInstance(Context context) {
        if (instance == null) {
            synchronized (GlobalWakeupProcess.class) {
                if (instance == null) {
                    instance = new GlobalWakeupProcess(context);
                }
            }
        }
        return instance;
    }


    private GlobalWakeupProcess(Context context) {
        this.mContext = context;
        initKeyWord();
    }


    private void initKeyWord() {
        //需与LxXfWakeup及LxXfWakeupMultiple唤醒词一样
        addKeyWord("GlobalKeyWord.json", "GlobalControlKeyWord.json");
        allowKeyWord.add("第一条");
        allowKeyWord.add("第一个");
        allowKeyWord.add("方案一");
        allowKeyWord.add("第二条");
        allowKeyWord.add("第二个");
        allowKeyWord.add("方案二");
        allowKeyWord.add("第三条");
        allowKeyWord.add("第三个");
        allowKeyWord.add("方案三");
        allowKeyWord.add("接听");
        allowKeyWord.add("挂断");
        allowKeyWord.add("开始导航");
    }

    private void addKeyWord(String... fileNames) {
        keyWords.clear();
        for (String fileName : fileNames) {
            String textFromAsset = AssetUtils.getTextFromAsset(mContext, fileName);
            List<String> strings = GsonHelper.fromJsonToList(textFromAsset, String[].class);
            if (strings != null && !strings.isEmpty()) {
                keyWords.addAll(strings);
            }
        }
    }


    private boolean isIntercept(String key) {
        boolean result = false;
        if (keyWords.contains(key) && isCloseNeedWakeup() && !allowKeyWord.contains(key)) {
            result = true;
        }
        return result;
    }


    public boolean dispatchGlobalKeyWork(String keyWord) {
        if (isIntercept(keyWord)) {
            return true;
        }
        boolean isWakeup = keyWords.contains(keyWord);
        if (isWakeup) {
            IatInstructionScenario iatInstructionScenario = ScenarioDispatcher.getInstance().getIatInstructionScenario(mContext);
            // TODO: 2019/4/26 0026 全局唤醒词处理逻辑
            String speakContent;
            switch (keyWord) {
                case "关闭屏幕":
                case "屏幕关闭":
                case "屏幕关掉":
                case "关掉屏幕":
                    setRobAction(36);
                    ScreenControlUtil.sendTurnOffScreenBroadCast(mContext);
                    break;
                case "打开屏幕":
                case "屏幕打开":
                    setRobAction(14);
                    ScreenControlUtil.sendTurnOnScreenBroadCast(mContext);
                    break;
                case "回到桌面":
                case "返回桌面":
                case "回到主页":
                case "返回主页":
                    setRobAction(16);
                    ScreenControlUtil.sendTurnOnScreenBroadCast(mContext);
                    iatInstructionScenario.returnToWindow();
                    break;
                case "返回导航":
                    setRobAction(18);
                    XmTtsManager.getInstance().startSpeakingByAssistant("好的");
                    XmMapNaviManager.getInstance().switchToLauncher();
                    break;
                case "增大音量":
                case "音量增大":
                case "放大音量":
                case "音量放大":
                case "大点声":
                    setRobAction(34);
                    setVolume(iatInstructionScenario, true);
                    break;
                case "减小音量":
                case "音量减小":
                case "降低音量":
                case "小点声":
                    setRobAction(34);
                    setVolume(iatInstructionScenario, false);
                    break;
                case "关闭声音":
                case "静音":
                case "静音模式":
                    setRobAction(35);
                    iatInstructionScenario.seMute(true);
                    break;
                case "打开声音":
                case "取消静音":
                    setRobAction(36);
                    iatInstructionScenario.seMute(false);
                    break;
                case "暂停":
                case "暂停播放":
                    setRobAction(26);
                    if (AudioManager.getInstance().isOnlineFMPlaying()) {
                        RadioApiManager.getInstance().pauseProgram("");
                    } else if (AudioManager.getInstance().isLocalFMPlaying()) {
                        RadioApiManager.getInstance().pauseRadioStation("");
                    } else if (AudioManager.getInstance().isMusicPlaying()) {
                        MusicApiManager.getInstance().pause();
                    }
                    break;
                case "继续播放":
                case "开始播放":
                    setRobAction(26);
                    if (!AudioManager.getInstance().isMediaPlaying()) {
                        if (AudioManager.getInstance().haveMediaPlayRecord()) {
                            if (AudioManager.getInstance().isOnlineFMType()) {
                                RadioApiManager.getInstance().continuePlayProgram();
                            } else if (AudioManager.getInstance().isLocalFMType()) {
                                RadioApiManager.getInstance().continueToPlayRadioStation();
                            } else {
                                if (!AudioManager.getInstance().isDeviceDisconnected()) {
                                    MusicApiManager.getInstance().play("", AudioManager.getInstance().getAudioType());
                                }
                            }
                        } else {
                            RadioApiManager.getInstance().playProgram(new IClientCallback.Stub() {
                                @Override
                                public void callback(Response response) {
                                    Bundle extra = response.getExtra();
                                    boolean haveRecord = extra.getBoolean(AudioConstants.BundleKey.AUDIO_DATA_SOURCE);
                                    if (!haveRecord) {
                                        MusicApiManager.getInstance().haveOnlineMusicPlayRecord(new ApiManager.onGetBooleanResultListener() {
                                            @Override
                                            public void onTrue() {
                                                MusicApiManager.getInstance().play("", AudioConstants.AudioTypes.MUSIC_ONLINE_KUWO);
                                            }

                                            @Override
                                            public void onFalse() {
                                                RadioApiManager.getInstance().haveLocalRadioPlayRecord(new ApiManager.onGetBooleanResultListener() {
                                                    @Override
                                                    public void onTrue() {
                                                        RadioApiManager.getInstance().playLocalRadioStation();
                                                    }

                                                    @Override
                                                    public void onFalse() {

                                                    }
                                                });
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                    break;
                case "上一首":
                case "上一曲":
                case "上一个":
                    setRobAction(getAction());
                    if (inPictureViewPage()) {
                        dispatchVoiceInstruction(AssistantConstants.RobActionKey.PLAY_CONTROL, "上一张");
                    } else {
                        if (AudioManager.getInstance().isOnlineFMPlaying()) {
                            RadioApiManager.getInstance().preProgram();
                        } else if (AudioManager.getInstance().isLocalFMPlaying()) {
                            RadioApiManager.getInstance().preRadioStation();
                        } else if (AudioManager.getInstance().isMusicPlaying()) {
                            MusicApiManager.getInstance().past();
                        } else {
                            AudioManager.getInstance().playerPauseMediaPre();
                        }
                    }
                    break;
                case "下一首":
                case "下一曲":
                case "下一个":
                    setRobAction(getAction());
                    if (inPictureViewPage()) {
                        dispatchVoiceInstruction(AssistantConstants.RobActionKey.PLAY_CONTROL, "下一张");
                    } else {
                        if (AudioManager.getInstance().isOnlineFMPlaying()) {
                            RadioApiManager.getInstance().nextProgram();
                        } else if (AudioManager.getInstance().isLocalFMPlaying()) {
                            RadioApiManager.getInstance().nextRadioStation("");
                        } else if (AudioManager.getInstance().isMusicPlaying()) {
                            MusicApiManager.getInstance().next(null);
                        } else {
                            AudioManager.getInstance().playerPauseMediaNext();
                        }
                    }
                    break;
                case "住口":
                case "关闭":
                case "退下吧":
                case "关了吧":
                case "退下":
                case "退出":
                case "取消":
                    if (AssistantManager.getInstance().isShowing()) {
                        XmCarFactory.getCarVendorExtensionManager().setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_CLOSE_ASSISTANT);
                        ScenarioDispatcher.getInstance().getIatInstructionScenario(mContext).closeAssistant();
                    }
                    break;
                case "接听":
                    Log.d("QBX", "dispatchGlobalKeyWork: 接听");
                    if (BluetoothPhoneManager.getInstance().isIncomingState()) {
                        setRobAction(AssistantConstants.RobActionKey.CALL);
                        XmTtsManager.getInstance().stopSpeaking();
                        XmTtsManager.getInstance().startSpeakingByAssistant(mContext.getString(getAcceptCallWord()), new WrapperSynthesizerListener() {
                            @Override
                            public void onCompleted() {
                                BluetoothPhoneApiManager.getInstance().acceptCall();
                            }

                            @Override
                            public void onError(int code) {
                                onCompleted();
                            }
                        });
                    }
                    break;
                case "挂断":
                case "不接":
                    Log.d("QBX", "dispatchGlobalKeyWork: 挂断");
                    if (BluetoothPhoneManager.getInstance().isIncomingState()) {
                        setRobAction(AssistantConstants.RobActionKey.HANG_UP);
                        XmTtsManager.getInstance().stopSpeaking();
                        XmTtsManager.getInstance().startSpeakingByAssistant(mContext.getString(getRejectCallWord()), new WrapperSynthesizerListener() {
                            @Override
                            public void onCompleted() {
                                BluetoothPhoneApiManager.getInstance().rejectCall();
                            }

                            @Override
                            public void onError(int code) {
                                onCompleted();
                            }
                        });
                    }
                    break;

                case "当前位置":
                    if (XmMapNaviManager.getInstance().isNaviForeground()) {
                        XmMapNaviManager.getInstance().showCarPosition();
                        PoiBean carPosition = XmMapNaviManager.getInstance().getCarPosition();
                        if (carPosition != null) {
                            setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI_LOCATION);
                            XmTtsManager.getInstance().startSpeakingByAssistant(carPosition.getName());
                        } else {
                            ScenarioDispatcher.getInstance().getIatNaviScenario(mContext).speakNoLocation();
                        }
                    }
                    break;
                case "开始导航":
                    startNavi();
                    break;
                case "停止导航":
                case "退出导航":
                case "取消导航":
                case "关闭导航":
                case "结束导航":
                    stopNavi();
                    break;
                case "高速优先":
                    setRoutAvoidType(AssistantConstants.RoutAvoidType.HIGH_FIRST);
                    break;
                case "不走高速":
                    setRoutAvoidType(AssistantConstants.RoutAvoidType.NOT_HIGH_FIRST);
                    break;
                case "躲避拥堵":
                    setRoutAvoidType(AssistantConstants.RoutAvoidType.AVOID_ROUND);
                    break;
                case "避免收费":
                case "躲避收费":
                    setRoutAvoidType(AssistantConstants.RoutAvoidType.CHEAPEST);
                    break;
                case "2D视图":
                case "2D模式":
                    if (XmMapNaviManager.getInstance().isNaviForeground()) {
                        setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
                        XmMapNaviManager.getInstance().setNaviShowState(AssistantConstants.NaviShowState.VIEW_TRANS_2D_NORTH_UP);
                    }
                    break;
                case "3D视图":
                case "3D模式":
                    if (XmMapNaviManager.getInstance().isNaviForeground()) {
                        setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
                        XmMapNaviManager.getInstance().setNaviShowState(AssistantConstants.NaviShowState.VIEW_TRANS_3D_HEAD_UP);
                    }
                    break;
                case "车头向前":
                case "车头向上":
                case "车头朝前":
                case "车头朝上":
                    if (XmMapNaviManager.getInstance().isNaviForeground()) {
                        setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
                        XmMapNaviManager.getInstance().setNaviShowState(AssistantConstants.NaviShowState.VIEW_TRANS_2D_HEAD_UP);
                    }
                    break;
                case "正北向前":
                case "正北向上":
                case "正北朝前":
                case "正北朝上":
                    if (XmMapNaviManager.getInstance().isNaviForeground()) {
                        setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
                        XmMapNaviManager.getInstance().setNaviShowState(AssistantConstants.NaviShowState.VIEW_TRANS_2D_NORTH_UP);
                    }
                    break;
                case "放大地图":
                    if (XmMapNaviManager.getInstance().isNaviForeground()) {
                        setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
                        XmMapNaviManager.getInstance().setMapZoomIn();
                    }
                    break;
                case "缩小地图":
                    if (XmMapNaviManager.getInstance().isNaviForeground()) {
                        setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
                        XmMapNaviManager.getInstance().setMapZoomOut();
                    }
                    break;
                case "全览模式":
                case "查看全览":
                case "全局模式":
                case "查看全局":
                    if (XmMapNaviManager.getInstance().isNaviForeground() && XmMapNaviManager.getInstance().isNaviing()) {
                        setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
                        boolean isOverView = XmMapNaviManager.getInstance().isRouteOverview();
                        if (isOverView) {
                            break;
                        }
                        XmMapNaviManager.getInstance().switchRouteOverview();
                    }
                    break;
                case "退出全览":
                case "退出全局":
                    if (XmMapNaviManager.getInstance().isNaviForeground() && XmMapNaviManager.getInstance().isNaviing()) {
                        setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_CANCEL_INFO);
                        boolean isOverView = XmMapNaviManager.getInstance().isRouteOverview();
                        if (!isOverView) {
                            break;
                        }
                        XmMapNaviManager.getInstance().switchRouteOverview();
                    }
                    break;
                case "打开路况":
                    setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
                    XmMapNaviManager.getInstance().showTmc(true);
                    XmTtsManager.getInstance().startSpeakingByAssistant("实时路况已打开");
                    break;
                case "关闭路况":
                    setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
                    XmMapNaviManager.getInstance().showTmc(false);
                    XmTtsManager.getInstance().startSpeakingByAssistant("实时路况已关闭");
                    break;
                case "打开电子眼":
                    setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
                    if (!XmMapNaviManager.getInstance().isNaviing()) return isWakeup;
                    XmMapNaviManager.getInstance().setCameraBroadcastType(1);
                    XmMapNaviManager.getInstance().setCameraBroadcastType(2);
                    XmMapNaviManager.getInstance().setCameraBroadcastType(3);
                    XmTtsManager.getInstance().startSpeakingByAssistant("电子眼播报已打开");
                    break;
                case "关闭电子眼":
                    setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
                    if (!XmMapNaviManager.getInstance().isNaviing()) return isWakeup;
                    XmMapNaviManager.getInstance().setCameraBroadcastType(0);
                    XmTtsManager.getInstance().startSpeakingByAssistant("电子眼播报已关闭");
                    break;
                default:
                    dispatchGlobalControlKeyWork(keyWord);
            }
        }
        return isWakeup;
    }

    private void setVolume(IatInstructionScenario iatInstructionScenario, boolean up) {
        int streamID = XmCarAudioManager.getInstance().getCarVolumeGroupId();
        String speakContent = iatInstructionScenario.dispatchVolume(up ? Constants.SystemSetting.VOLUME_UP : Constants.SystemSetting.VOLUME_DOWN, -1, iatInstructionScenario.getStreamText(streamID));
//        XmTtsManager.getInstance().startSpeakingByAssistant(speakContent);
    }

    public boolean isCloseNeedWakeup() {
        boolean isOpen = XmProperties.build(LoginManager.getInstance().getLoginUserId()).get(IS_VOICE_WITHOUT_WAKE, true);
        if (!isOpen) {
            //匹配到了免唤醒词且免唤醒开关被关闭则直接消费
            Log.d(TAG, "免唤醒开关关闭");
        }
        return !isOpen;
    }

    void setRobAction(int action) {
        XmCarFactory.getCarVendorExtensionManager().setRobAction(action);
    }

    private void dispatchGlobalControlKeyWork(String keyWord) {
        String speakText = null;
        switch (keyWord) {
            case "日程管理":
            case "节目分类":
                speakText = mContext.getString(R.string.ok);
                dispatchVoiceInstruction(AssistantConstants.RobActionKey.PLAY_RADIO_STATION, keyWord);
                break;
            case "上一张":
            case "下一张":
                if (inPictureViewPage()) {
                    dispatchVoiceInstruction(AssistantConstants.RobActionKey.PLAY_CONTROL, keyWord);
                }
                break;
            case "放大图片":
                dispatchVoiceInstruction(AssistantConstants.RobActionKey.ROB_ACTION_OPEN_APP, keyWord);
                break;
            case "缩小图片":
                dispatchVoiceInstruction(AssistantConstants.RobActionKey.MUSIC_PLAY_CONTROL, keyWord);
                break;
//            case "全屏显示":
//            case "取消全屏":
            case "顺时针旋转":
            case "逆时针旋转":
                dispatchVoiceInstruction(AssistantConstants.RobActionKey.MUSIC_PLAY_CONTROL, keyWord);
                break;
            case "查询流量":
                queryFlow();
                setRobAction(AssistantConstants.RobActionKey.QUERY_FLOW);

                return;
            case "兑换流量":
                //TODO 进入兑换流量界面
                speakText = startGetFlowApp();
                setRobAction(AssistantConstants.RobActionKey.PLAY_RADIO_STATION);

                break;
            case "购买流量":
                //TODO 进入购买流量界面
                speakText = startGetFlowApp();
                setRobAction(AssistantConstants.RobActionKey.PLAY_RADIO_STATION);

                break;
            case "打开全息影像":
                //TODO
                setRobAction(AssistantConstants.RobActionKey.OPEN_HOLOGRAPHIC_IMAGE);
//                XmCarFactory.getCarVendorExtensionManager().setRobSwitcher(SDKConstants.SWITCH_ROBOT_ON);
                break;
            case "关闭全息影像":
                //TODO
                setRobAction(AssistantConstants.RobActionKey.CLOSE_HOLOGRAPHIC_IMAGE);
//                XmCarFactory.getCarVendorExtensionManager().setRobSwitcher(SDKConstants.SWITCH_ROBOT_OFF);
                break;
            case "人物选择":
                //TODO

                break;
            case "拍照片":
                setRobAction(14);
                if (XmCarConfigManager.hasJourneyRecord()) {
                    Bundle bundle = new Bundle();
                    bundle.putString(CenterConstants.DATE, CenterConstants.ASSISTANT_MARK);
                    LaunchUtils.launchAppOnlyNewTask(mContext, CenterConstants.LAUNCHER,
                            "com.xiaoma.launcher.mark.ui.activity.MarkMainActivity",
                            bundle);
                    speakText = mContext.getString(getFinishWord());
                } else {
                    speakText = mContext.getString(R.string.no_device_speak);
                }

                break;
           /* case "服装选择":

                break;*/
            case "单曲循环":
                setRobAction(26);
                MusicApiManager.getInstance().switchPlayMode(AudioConstants.KwAudioPlayMode.MODE_SINGLE_CIRCLE);
                break;
            case "随机播放":
                setRobAction(26);
                MusicApiManager.getInstance().switchPlayMode(AudioConstants.KwAudioPlayMode.MODE_ALL_RANDOM);
                break;
            case "顺序循环":
            case "顺序播放":
                setRobAction(26);
                MusicApiManager.getInstance().switchPlayMode(AudioConstants.KwAudioPlayMode.MODE_ALL_ORDER);
                break;
            case "循环播放":
                MusicApiManager.getInstance().switchPlayMode(AudioConstants.KwAudioPlayMode.MODE_ALL_CIRCLE);
                break;
            case "第一条":
            case "第一个":
            case "方案一":
                ScenarioDispatcher.getInstance().getIatNaviScenario(mContext).chooseRoute(0);
                break;
            case "第二条":
            case "第二个":
            case "方案二":
                ScenarioDispatcher.getInstance().getIatNaviScenario(mContext).chooseRoute(1);
                break;
            case "第三条":
            case "第三个":
            case "方案三":
                ScenarioDispatcher.getInstance().getIatNaviScenario(mContext).chooseRoute(2);
                break;
            case "播报消息":
            case "朗读消息":
            case "消息播报":
            case "消息朗读":
                if (WeChatManager.getInstance().isWeMain()) {
                    setRobAction(25);
                    ScenarioDispatcher.getInstance().IatWeChatScenario(mContext).playMsg();
                }
                break;
            case "这是什么歌":
            case "现在放的是什么歌":
            case "正在播放什么歌":
            case "这歌叫什么":
            case "这歌叫什么名字":
            case "这是谁唱的":
            case "这是谁的歌":
            case "打开听歌识曲":
                setRobAction(38);
                ScenarioDispatcher.getInstance().getIatNewsScenario(mContext).recognizeSong();
                break;
            case "打开负一屏":
                setRobAction(16);
                mContext.sendBroadcast(new Intent(CenterConstants.OPEN_NEGATIVE_SCREEN));
                break;
        }
        if (!TextUtils.isEmpty(speakText)) {
            XmTtsManager.getInstance().startSpeakingByAssistant(speakText);
        }
    }

    private boolean inPictureViewPage() {
        return AppUtils.isActivityForeground(mContext, PICTURE_VIEW_PAGE_CLASS_NAME);
    }

    // 购买，兑换流量
    private String startGetFlowApp() {
        if (!AppUtils.isAppInstalled(mContext, CenterConstants.SHOP)) {
            return mContext.getString(R.string.please_install_shop);
        }
        if (!LoginTypeManager.getInstance().judgeUse(LoginCfgConstant.SHOP)) {
            return LoginTypeManager.getInstance().getLoginType().getPrompt(mContext);
        }

        //        Intent intent = new Intent();
        //        intent.setAction(Intent.ACTION_VIEW);
        //        intent.addCategory(Intent.CATEGORY_DEFAULT);
        //        intent.setAction(AssistantConstants.ASSIATANT_JUP_SHOP_ACTION);
        //        intent.setData(Uri.parse(AssistantConstants.ASSIATANT_JUP_SHOP_URI));
        //        context.startActivity(intent);

        NodeUtils.jumpTo(mContext, CenterConstants.SHOP, "com.xiaoma.shop.business.ui.main.MainActivity", NodeConst.SHOP.ASSISTANT_ACTIVITY + "/" + NodeConst.SHOP.BUY_FLOW);

        return mContext.getString(R.string.result_ok);
    }

    public void queryFlow() {
        setRobAction(AssistantConstants.FlowType.QUERY_FLOW);
        RequestManager.newSingleton().queryFlow(new StringCallback() {
            @Override
            public void onSuccess(com.xiaoma.network.model.Response<String> response) {
                String result = response.body();
                //1060:服务不可用，1059:暂不支持该语言
                FlowMarginBean flowMarginBean = GsonHelper.fromJson(result, FlowMarginBean.class);
                if (flowMarginBean == null) {
                    return;
                }
                FlowBean flowBean = handleFlowMarginData(flowMarginBean);

                if (StringUtil.isNotEmpty(flowBean.getBalance()) && StringUtil.isNotEmpty(flowBean.getTotal())) {
                    XmTtsManager.getInstance().startSpeakingByAssistant(String.format(mContext.getString(R.string.total_flow_this_month), flowBean.getTotal(), flowBean.getBalance()));
                }

            }

            @Override
            public void onError(com.xiaoma.network.model.Response<String> response) {
                super.onError(response);
                XmTtsManager.getInstance().startSpeakingByAssistant(mContext.getString(R.string.no_traffic_information_obtained));
            }
        });
    }

    /**
     * 处理流量余量数据
     */
    private FlowBean handleFlowMarginData(FlowMarginBean flowMarginBean) {
        String total = "0.00M", balance = "0.00M", usage = "0.00M";
        FlowBean flowBean = new FlowBean();
        List<FlowMarginBean.LeftInfoBean> leftInfo = flowMarginBean.getLeftInfo();
        if (leftInfo != null && leftInfo.size() > 0) {
            FlowMarginBean.LeftInfoBean leftInfoBean = leftInfo.get(0);
            String unit = TextUtils.isEmpty(leftInfoBean.getUnit()) ? "" : leftInfoBean.getUnit();
            UnitConverUtils.Unit u = null;
            switch (unit) {
                case "Kb":
                    u = UnitConverUtils.Unit.KB;
                    break;
                case "G":
                    u = UnitConverUtils.Unit.G;
                    break;
                case "M":
                    u = UnitConverUtils.Unit.M;
                    break;
            }
            if (u != null) {
                total = UnitConverUtils.toNear(u, leftInfoBean.getQuota());
                balance = UnitConverUtils.toNear(u, leftInfoBean.getQuotaBalance());
                usage = UnitConverUtils.toNear(u, leftInfoBean.getQuotaUsage());

            }
        }
        flowBean.setTotal(total);
        flowBean.setBalance(balance);
        flowBean.setUsage(usage);
        return flowBean;
    }

    private int getFinishWord() {
        Random random = new Random();
        int index = random.nextInt(finishWord.length);
        return finishWord[index];
    }


    private void dispatchVoiceInstruction(int action, String keyWord) {
        setRobAction(action);
        boolean isHandled = AssistantProcessorChain.getInstance().processRegisterKeyWord(keyWord, null, null);
        Log.d(TAG, mContext.getString(R.string.global_wakeup_word_disptach) + isHandled);
    }

    private void setRoutAvoidType(int type) {
        final int i = XmMapNaviManager.getInstance().setRouteAvoidType(type);
        if (i == 0) {
            setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
            XmTtsManager.getInstance().startSpeakingByAssistant("路线规划成功");
        }
    }

    private void startNavi() {
        if (XmMapNaviManager.getInstance().isPathPlanSuccess()
                && XmMapNaviManager.getInstance().isNaviForeground()) {
            setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_SELECT_LINE);
            XmMapNaviManager.getInstance().startNavi();
        }
    }

    private void stopNavi() {
        if (XmMapNaviManager.getInstance().isNaviForeground()) {
            final int i = XmMapNaviManager.getInstance().cancelNavi();
            if (i == 0) {
                setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_STOP_NAVI);
                XmTtsManager.getInstance().startSpeakingByAssistant(mContext.getString(R.string.navigation_is_over));
            }
        }
    }

    private int getAcceptCallWord() {
        int index = new Random().nextInt(acceptCallWord.length);
        return acceptCallWord[index];
    }

    private int getRejectCallWord() {
        int index = new Random().nextInt(rejectCallWord.length);
        return rejectCallWord[index];
    }


    /**
     * 随机动作
     */
    public static int getAction() {
        Random random = new Random();
        int i = random.nextInt(mAction.length);
        return mAction[i];
    }

}
