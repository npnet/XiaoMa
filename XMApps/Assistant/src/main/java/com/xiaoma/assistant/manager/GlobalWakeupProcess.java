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
    private List<String> allowKeyWord = new ArrayList();//???????????????????????????
    private int[] acceptCallWord = new int[]{R.string.accept_call, R.string.ok, R.string.here};
    private int[] rejectCallWord = new int[]{R.string.reject_call, R.string.ok, R.string.here};
    //????????????
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
        //??????LxXfWakeup???LxXfWakeupMultiple???????????????
        addKeyWord("GlobalKeyWord.json", "GlobalControlKeyWord.json");
        allowKeyWord.add("?????????");
        allowKeyWord.add("?????????");
        allowKeyWord.add("?????????");
        allowKeyWord.add("?????????");
        allowKeyWord.add("?????????");
        allowKeyWord.add("?????????");
        allowKeyWord.add("?????????");
        allowKeyWord.add("?????????");
        allowKeyWord.add("?????????");
        allowKeyWord.add("??????");
        allowKeyWord.add("??????");
        allowKeyWord.add("????????????");
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
            // TODO: 2019/4/26 0026 ???????????????????????????
            String speakContent;
            switch (keyWord) {
                case "????????????":
                case "????????????":
                case "????????????":
                case "????????????":
                    setRobAction(36);
                    ScreenControlUtil.sendTurnOffScreenBroadCast(mContext);
                    break;
                case "????????????":
                case "????????????":
                    setRobAction(14);
                    ScreenControlUtil.sendTurnOnScreenBroadCast(mContext);
                    break;
                case "????????????":
                case "????????????":
                case "????????????":
                case "????????????":
                    setRobAction(16);
                    ScreenControlUtil.sendTurnOnScreenBroadCast(mContext);
                    iatInstructionScenario.returnToWindow();
                    break;
                case "????????????":
                    setRobAction(18);
                    XmTtsManager.getInstance().startSpeakingByAssistant("??????");
                    XmMapNaviManager.getInstance().switchToLauncher();
                    break;
                case "????????????":
                case "????????????":
                case "????????????":
                case "????????????":
                case "?????????":
                    setRobAction(34);
                    setVolume(iatInstructionScenario, true);
                    break;
                case "????????????":
                case "????????????":
                case "????????????":
                case "?????????":
                    setRobAction(34);
                    setVolume(iatInstructionScenario, false);
                    break;
                case "????????????":
                case "??????":
                case "????????????":
                    setRobAction(35);
                    iatInstructionScenario.seMute(true);
                    break;
                case "????????????":
                case "????????????":
                    setRobAction(36);
                    iatInstructionScenario.seMute(false);
                    break;
                case "??????":
                case "????????????":
                    setRobAction(26);
                    if (AudioManager.getInstance().isOnlineFMPlaying()) {
                        RadioApiManager.getInstance().pauseProgram("");
                    } else if (AudioManager.getInstance().isLocalFMPlaying()) {
                        RadioApiManager.getInstance().pauseRadioStation("");
                    } else if (AudioManager.getInstance().isMusicPlaying()) {
                        MusicApiManager.getInstance().pause();
                    }
                    break;
                case "????????????":
                case "????????????":
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
                case "?????????":
                case "?????????":
                case "?????????":
                    setRobAction(getAction());
                    if (inPictureViewPage()) {
                        dispatchVoiceInstruction(AssistantConstants.RobActionKey.PLAY_CONTROL, "?????????");
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
                case "?????????":
                case "?????????":
                case "?????????":
                    setRobAction(getAction());
                    if (inPictureViewPage()) {
                        dispatchVoiceInstruction(AssistantConstants.RobActionKey.PLAY_CONTROL, "?????????");
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
                case "??????":
                case "??????":
                case "?????????":
                case "?????????":
                case "??????":
                case "??????":
                case "??????":
                    if (AssistantManager.getInstance().isShowing()) {
                        XmCarFactory.getCarVendorExtensionManager().setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_CLOSE_ASSISTANT);
                        ScenarioDispatcher.getInstance().getIatInstructionScenario(mContext).closeAssistant();
                    }
                    break;
                case "??????":
                    Log.d("QBX", "dispatchGlobalKeyWork: ??????");
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
                case "??????":
                case "??????":
                    Log.d("QBX", "dispatchGlobalKeyWork: ??????");
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

                case "????????????":
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
                case "????????????":
                    startNavi();
                    break;
                case "????????????":
                case "????????????":
                case "????????????":
                case "????????????":
                case "????????????":
                    stopNavi();
                    break;
                case "????????????":
                    setRoutAvoidType(AssistantConstants.RoutAvoidType.HIGH_FIRST);
                    break;
                case "????????????":
                    setRoutAvoidType(AssistantConstants.RoutAvoidType.NOT_HIGH_FIRST);
                    break;
                case "????????????":
                    setRoutAvoidType(AssistantConstants.RoutAvoidType.AVOID_ROUND);
                    break;
                case "????????????":
                case "????????????":
                    setRoutAvoidType(AssistantConstants.RoutAvoidType.CHEAPEST);
                    break;
                case "2D??????":
                case "2D??????":
                    if (XmMapNaviManager.getInstance().isNaviForeground()) {
                        setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
                        XmMapNaviManager.getInstance().setNaviShowState(AssistantConstants.NaviShowState.VIEW_TRANS_2D_NORTH_UP);
                    }
                    break;
                case "3D??????":
                case "3D??????":
                    if (XmMapNaviManager.getInstance().isNaviForeground()) {
                        setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
                        XmMapNaviManager.getInstance().setNaviShowState(AssistantConstants.NaviShowState.VIEW_TRANS_3D_HEAD_UP);
                    }
                    break;
                case "????????????":
                case "????????????":
                case "????????????":
                case "????????????":
                    if (XmMapNaviManager.getInstance().isNaviForeground()) {
                        setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
                        XmMapNaviManager.getInstance().setNaviShowState(AssistantConstants.NaviShowState.VIEW_TRANS_2D_HEAD_UP);
                    }
                    break;
                case "????????????":
                case "????????????":
                case "????????????":
                case "????????????":
                    if (XmMapNaviManager.getInstance().isNaviForeground()) {
                        setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
                        XmMapNaviManager.getInstance().setNaviShowState(AssistantConstants.NaviShowState.VIEW_TRANS_2D_NORTH_UP);
                    }
                    break;
                case "????????????":
                    if (XmMapNaviManager.getInstance().isNaviForeground()) {
                        setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
                        XmMapNaviManager.getInstance().setMapZoomIn();
                    }
                    break;
                case "????????????":
                    if (XmMapNaviManager.getInstance().isNaviForeground()) {
                        setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
                        XmMapNaviManager.getInstance().setMapZoomOut();
                    }
                    break;
                case "????????????":
                case "????????????":
                case "????????????":
                case "????????????":
                    if (XmMapNaviManager.getInstance().isNaviForeground() && XmMapNaviManager.getInstance().isNaviing()) {
                        setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
                        boolean isOverView = XmMapNaviManager.getInstance().isRouteOverview();
                        if (isOverView) {
                            break;
                        }
                        XmMapNaviManager.getInstance().switchRouteOverview();
                    }
                    break;
                case "????????????":
                case "????????????":
                    if (XmMapNaviManager.getInstance().isNaviForeground() && XmMapNaviManager.getInstance().isNaviing()) {
                        setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_CANCEL_INFO);
                        boolean isOverView = XmMapNaviManager.getInstance().isRouteOverview();
                        if (!isOverView) {
                            break;
                        }
                        XmMapNaviManager.getInstance().switchRouteOverview();
                    }
                    break;
                case "????????????":
                    setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
                    XmMapNaviManager.getInstance().showTmc(true);
                    XmTtsManager.getInstance().startSpeakingByAssistant("?????????????????????");
                    break;
                case "????????????":
                    setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
                    XmMapNaviManager.getInstance().showTmc(false);
                    XmTtsManager.getInstance().startSpeakingByAssistant("?????????????????????");
                    break;
                case "???????????????":
                    setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
                    if (!XmMapNaviManager.getInstance().isNaviing()) return isWakeup;
                    XmMapNaviManager.getInstance().setCameraBroadcastType(1);
                    XmMapNaviManager.getInstance().setCameraBroadcastType(2);
                    XmMapNaviManager.getInstance().setCameraBroadcastType(3);
                    XmTtsManager.getInstance().startSpeakingByAssistant("????????????????????????");
                    break;
                case "???????????????":
                    setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_NAVI);
                    if (!XmMapNaviManager.getInstance().isNaviing()) return isWakeup;
                    XmMapNaviManager.getInstance().setCameraBroadcastType(0);
                    XmTtsManager.getInstance().startSpeakingByAssistant("????????????????????????");
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
            //??????????????????????????????????????????????????????????????????
            Log.d(TAG, "?????????????????????");
        }
        return !isOpen;
    }

    void setRobAction(int action) {
        XmCarFactory.getCarVendorExtensionManager().setRobAction(action);
    }

    private void dispatchGlobalControlKeyWork(String keyWord) {
        String speakText = null;
        switch (keyWord) {
            case "????????????":
            case "????????????":
                speakText = mContext.getString(R.string.ok);
                dispatchVoiceInstruction(AssistantConstants.RobActionKey.PLAY_RADIO_STATION, keyWord);
                break;
            case "?????????":
            case "?????????":
                if (inPictureViewPage()) {
                    dispatchVoiceInstruction(AssistantConstants.RobActionKey.PLAY_CONTROL, keyWord);
                }
                break;
            case "????????????":
                dispatchVoiceInstruction(AssistantConstants.RobActionKey.ROB_ACTION_OPEN_APP, keyWord);
                break;
            case "????????????":
                dispatchVoiceInstruction(AssistantConstants.RobActionKey.MUSIC_PLAY_CONTROL, keyWord);
                break;
//            case "????????????":
//            case "????????????":
            case "???????????????":
            case "???????????????":
                dispatchVoiceInstruction(AssistantConstants.RobActionKey.MUSIC_PLAY_CONTROL, keyWord);
                break;
            case "????????????":
                queryFlow();
                setRobAction(AssistantConstants.RobActionKey.QUERY_FLOW);

                return;
            case "????????????":
                //TODO ????????????????????????
                speakText = startGetFlowApp();
                setRobAction(AssistantConstants.RobActionKey.PLAY_RADIO_STATION);

                break;
            case "????????????":
                //TODO ????????????????????????
                speakText = startGetFlowApp();
                setRobAction(AssistantConstants.RobActionKey.PLAY_RADIO_STATION);

                break;
            case "??????????????????":
                //TODO
                setRobAction(AssistantConstants.RobActionKey.OPEN_HOLOGRAPHIC_IMAGE);
//                XmCarFactory.getCarVendorExtensionManager().setRobSwitcher(SDKConstants.SWITCH_ROBOT_ON);
                break;
            case "??????????????????":
                //TODO
                setRobAction(AssistantConstants.RobActionKey.CLOSE_HOLOGRAPHIC_IMAGE);
//                XmCarFactory.getCarVendorExtensionManager().setRobSwitcher(SDKConstants.SWITCH_ROBOT_OFF);
                break;
            case "????????????":
                //TODO

                break;
            case "?????????":
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
           /* case "????????????":

                break;*/
            case "????????????":
                setRobAction(26);
                MusicApiManager.getInstance().switchPlayMode(AudioConstants.KwAudioPlayMode.MODE_SINGLE_CIRCLE);
                break;
            case "????????????":
                setRobAction(26);
                MusicApiManager.getInstance().switchPlayMode(AudioConstants.KwAudioPlayMode.MODE_ALL_RANDOM);
                break;
            case "????????????":
            case "????????????":
                setRobAction(26);
                MusicApiManager.getInstance().switchPlayMode(AudioConstants.KwAudioPlayMode.MODE_ALL_ORDER);
                break;
            case "????????????":
                MusicApiManager.getInstance().switchPlayMode(AudioConstants.KwAudioPlayMode.MODE_ALL_CIRCLE);
                break;
            case "?????????":
            case "?????????":
            case "?????????":
                ScenarioDispatcher.getInstance().getIatNaviScenario(mContext).chooseRoute(0);
                break;
            case "?????????":
            case "?????????":
            case "?????????":
                ScenarioDispatcher.getInstance().getIatNaviScenario(mContext).chooseRoute(1);
                break;
            case "?????????":
            case "?????????":
            case "?????????":
                ScenarioDispatcher.getInstance().getIatNaviScenario(mContext).chooseRoute(2);
                break;
            case "????????????":
            case "????????????":
            case "????????????":
            case "????????????":
                if (WeChatManager.getInstance().isWeMain()) {
                    setRobAction(25);
                    ScenarioDispatcher.getInstance().IatWeChatScenario(mContext).playMsg();
                }
                break;
            case "???????????????":
            case "????????????????????????":
            case "?????????????????????":
            case "???????????????":
            case "?????????????????????":
            case "???????????????":
            case "???????????????":
            case "??????????????????":
                setRobAction(38);
                ScenarioDispatcher.getInstance().getIatNewsScenario(mContext).recognizeSong();
                break;
            case "???????????????":
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

    // ?????????????????????
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
                //1060:??????????????????1059:?????????????????????
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
     * ????????????????????????
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
            XmTtsManager.getInstance().startSpeakingByAssistant("??????????????????");
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
     * ????????????
     */
    public static int getAction() {
        Random random = new Random();
        int i = random.nextInt(mAction.length);
        return mAction[i];
    }

}
