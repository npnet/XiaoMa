package com.xiaoma.assistant.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.assistant.Interceptor.interceptor.AnswerPhoneInterceptor;
import com.xiaoma.assistant.Interceptor.interceptor.BaseInterceptor;
import com.xiaoma.assistant.Interceptor.interceptor.NavigationInterceptor;
import com.xiaoma.assistant.Interceptor.manager.InterceptorManager;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.callback.IAssistantViewListener;
import com.xiaoma.assistant.callback.WakeUpListener;
import com.xiaoma.assistant.callback.WrapperSynthesizerListener;
import com.xiaoma.assistant.constants.AssistantConstants;
import com.xiaoma.assistant.constants.EventConstants;
import com.xiaoma.assistant.model.parser.LxParseResult;
import com.xiaoma.assistant.processor.AssistantProcessorChain;
import com.xiaoma.assistant.scenarios.IatScenario;
import com.xiaoma.assistant.scenarios.ScenarioDispatcher;
import com.xiaoma.assistant.ui.AssistantDialog;
import com.xiaoma.assistant.ui.adapter.BaseMultiPageAdapter;
import com.xiaoma.assistant.utils.AssistantUtils;
import com.xiaoma.assistant.utils.Constants;
import com.xiaoma.assistant.utils.HandleSpecialWordUtils;
import com.xiaoma.assistant.utils.UpdateByTimeUtils;
import com.xiaoma.assistant.view.ContactView;
import com.xiaoma.assistant.view.MultiPageView;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.cariflytek.iat.VrAidlManager;
import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.login.LoginManager;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LatLng;
import com.xiaoma.model.User;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.UIUtils;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.SoundPoolUtils;
import com.xiaoma.utils.XmProperties;
import com.xiaoma.utils.constant.VrConstants;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;
import com.xiaoma.vr.VoiceConfigManager;
import com.xiaoma.vr.VrConfig;
import com.xiaoma.vr.iat.IAssistantView;
import com.xiaoma.vr.iat.OnIatListener;
import com.xiaoma.vr.iat.OnShortSrListener;
import com.xiaoma.vr.ivw.OnWakeUpListener;
import com.xiaoma.vr.model.ConversationItem;
import com.xiaoma.vr.model.SeoptType;
import com.xiaoma.vr.recorder.RecorderManager;
import com.xiaoma.vr.recorder.RecorderType;
import com.xiaoma.vr.tts.OnTtsListener;
import com.xiaoma.vrfactory.AssistantMicFocusManager;
import com.xiaoma.vrfactory.iat.XmIatManager;
import com.xiaoma.vrfactory.ivw.XmIvwManager;
import com.xiaoma.vrfactory.tts.XmTtsManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.xiaoma.assistant.constants.AssistantConstants.WakeUpMethod.CLICK_METHOD;
import static com.xiaoma.assistant.constants.AssistantConstants.WakeUpMethod.HARDWARE_METHOD;
import static com.xiaoma.assistant.constants.AssistantConstants.WakeUpMethod.SPEECH_METHOD;
import static com.xiaoma.vr.VoiceConfigManager.IS_VOICE_WAKEUP_ON;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/9/26
 * Desc：语音助手管理类
 */
public class AssistantManager implements IAssistantManager, IAssistantViewListener, IAssistantView, XmCarVendorExtensionManager.ValueChangeListener {
    //语音助手不操作最大关闭等待时长
    private static final int MAX_NOT_WORK_TIME = 20000;
    //语音助手不操作最小关闭等待时长
    private static final int MIN_NOT_WORK_TIME = 40000;
    public static final String DISCLAIMER_STATUS = "disclaimer status";//免责说明
    //对象
    private static volatile AssistantManager instance;
    //助手界面的view
    private AssistantDialog assistantView;
    //上下文对象
    private Context mContext;
    //语音引擎初始化状态
    private boolean initIatSuccess = false;
    //handler
    private Handler uiHandler = new Handler(Looper.getMainLooper());
    //AudioManager对象
    private AudioManager mAudioManager;
    //无法识别计数
    private int startListenCount = 0;
    //没有输入音频的计数
    private int alertCount = 0;
    //当前是否有音频在播放
    private boolean isPlaying = false;
    //语音输入结束标志
    private boolean mIsIatListenerComplete;
    //当前的唤醒词
    private String currentWakeUpWord;
    //当前的可见即可说的内容
    private String currSrSceneStksCmd;
    //会话列表
    private List<ConversationItem> conversationItemList;
    //当前的会话
    private ConversationItem currentConversationItem;
    //当前的场景
    private IatScenario currentScenario;
    // 新手引导view
    private View guideContainer;
    SeoptType localSeoptType;

    private WakeUpListener mWakeUpListener;
    private OnTtsListener mOnTtsListener;
    public static int[] mEndSpkeak = new int[]{R.string.not_speak_anything_and_restart_speak_two, R.string.not_speak_anything_and_restart_speak_three};  //识别错误提示语


    //焦点变化监听
    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager
            .OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            KLog.d("focusChange :" + focusChange);
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                closeAssistant();
            }
        }
    };
    //关闭语音助手的线程
    private Runnable closeWindowRunnable = new Runnable() {
        @Override
        public void run() {
            mIsIatListenerComplete = false;
            if (assistantView != null) {
                boolean isMultiple = assistantView.inChooseMode();
                if (isMultiple) {
                    // 超时关闭
                    hideMultiPageView();
                    hideContactView();
                    //帮助提示？？？
                    rePostCloseWindowRunnable();
                    //重新聆听
                    startListening(false);
                } else if (isShowing()) {
                    closeAssistant();
                }
            } else {
                closeAssistant();
            }

        }
    };

    //最短等待关闭线程
    private Runnable minUnWorkRunnable = new Runnable() {
        @Override
        public void run() {
            ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                @Override
                public void run() {
                    if (assistantView != null && !mIsIatListenerComplete
                            && !assistantView.inChooseMode()) {
                        //帮助提示？？？
                    }
                }
            });
        }
    };

    private void rePostCloseWindowRunnable() {
        closeWindowRunnable(MAX_NOT_WORK_TIME);
    }

    private void closeWindowRunnable(int unWork_time) {
        removeCloseWindowRunnable();
        ThreadDispatcher.getDispatcher().postOnMainDelayed(closeWindowRunnable,
                unWork_time);
    }

    public void rePostCloseWindowRunnable(long delayed) {
        removeCloseWindowRunnable();
        ThreadDispatcher.getDispatcher().postOnMainDelayed(closeWindowRunnable, delayed);
    }

    public void removeCloseWindowRunnable() {
        ThreadDispatcher.getDispatcher().removeOnMain(closeWindowRunnable);
    }

    private void removeMinTimeCountDownRunnable() {
        ThreadDispatcher.getDispatcher().removeOnMain(minUnWorkRunnable);
    }


    private void postMinTimeCountDownRunnable() {
        removeMinTimeCountDownRunnable();
        ThreadDispatcher.getDispatcher().postOnMainDelayed(minUnWorkRunnable,
                MIN_NOT_WORK_TIME);
    }


    public static AssistantManager getInstance() {
        if (instance == null) {
            synchronized (AssistantManager.class) {
                if (instance == null) {
                    instance = new AssistantManager();
                }
            }
        }
        return instance;
    }

    private AssistantManager() {
        if (LoginManager.getInstance().isUserLogin()) {
            setUid();
        } else {
            LoginManager.getInstance().addLoginEventListener(new LoginManager.LoginEventListener() {
                @Override
                public void onLogin(User data) {
                    //主进程
                    setUid();
                }

                @Override
                public void onLogout() {

                }
            });
        }

    }

    private void setUid() {
        //主进程
        VoiceConfigManager.getInstance().setUid(LoginManager.getInstance().getLoginUserId());
        //VR进程
        VrAidlManager.getInstance().setUid(LoginManager.getInstance().getLoginUserId());
    }


    @Override
    public void init(Context context) {
        mContext = context;
        localSeoptType = VoiceConfigManager.getInstance().getLocalSeoptType();
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        AssistantProcessorChain.getInstance().init(context, this);
        XmCarVendorExtensionManager.getInstance().addValueChangeListener(this);
        XmIvwManager.getInstance().init(context);
        XmIvwManager.getInstance().setOnWakeUpListener(new OnWakeUpListener() {
            @Override
            public void onWakeUp(int index, String keyWord, boolean isMainDrive) {
                KLog.d("hzx", "wakeup word: " + keyWord);
                //先判断登录状态,未登录，不可使用语音助手
                boolean wakeupSwitch = XmProperties.build(LoginManager.getInstance().getLoginUserId()).get(IS_VOICE_WAKEUP_ON, true);
                if (!checkPermission() || !wakeupSwitch) {
                    Log.e("AssistantManager", "wake interception");
                    return;
                }
                if (XmCarFactory.getCarVendorExtensionManager().getCameraStatus()) {
                    Log.e("AssistantManager", "panoramic image fg interception");
                    return;
                }
                currentWakeUpWord = keyWord;
                updateSeoptType(isMainDrive);
                if (keyWord.startsWith("你好友幂")) {
                    setRobAction(4);
                }
                if (index == 128) {
                    if (keyWord.startsWith("打电话给")) {
                        setRobAction(25);
                    } else if (keyWord.startsWith("导航到")) {
                        setRobAction(15);
                    } else if (keyWord.startsWith("我要听") || keyWord.startsWith("我想听")) {
                        setRobAction(16);
                    }
                    if (!GlobalWakeupProcess.getInstance(mContext).isCloseNeedWakeup()) {
                        showAssistantView(true);
                    }
                    return;
                } else if (index == 64) {
                    //处理全局唤醒词及自定义唤醒词
                    if (!GlobalWakeupProcess.getInstance(mContext).dispatchGlobalKeyWork(keyWord)) {
                        //自定义唤醒词
                        showAssistantView(false);
                    }
                } else {
                    showAssistantView(false);
                }
                uploadWakeUp(AssistantConstants.WakeUpMethod.SPEECH_METHOD);
                if (mWakeUpListener != null) {
                    mWakeUpListener.onWakeUp();
                }
                //语音唤醒打断回调TTS onError
                if (mOnTtsListener != null) {
                    mOnTtsListener.onError(OnTtsListener.ErrorCode.SPEAK_INTERRUPT);
                }
            }

            @Override
            public void onWakeUpCmd(String cmdText) {
                AssistantProcessorChain.getInstance().processWakeUpCmd(cmdText);
            }
        });
        XmIvwManager.getInstance().addAssistantView(this);
        XmIatManager.getInstance().init(context);
        initAssistantDialogControl();
        //Setting.setLocationEnable(true);
    }

    private void setRobAction(int action) {
        ScenarioDispatcher.getInstance().getIatNewsScenario(mContext).setRobAction(action);
    }

    private boolean updateSeoptType(boolean isMainDrive) {
        localSeoptType = VoiceConfigManager.getInstance().getLocalSeoptType();
        if (localSeoptType != SeoptType.CLOSE) {
            if (isMainDrive) {
                localSeoptType = SeoptType.LEFT;
            } else {
                localSeoptType = SeoptType.RIGHT;
            }
        }
        return true;
    }

    public SeoptType getLocalSeoptType() {
        return localSeoptType;
    }

    public void initAssistantDialogControl() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(VrConstants.Actions.ASSISTANT_DIALOG_CLOSE);
        filter.addAction(CenterConstants.IN_A_CALL);
        filter.addAction(CenterConstants.INCOMING_CALL);
        if (mContext != null) {
            mContext.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    closeAssistant();
                }
            }, filter);
        }
    }

    @Override
    public boolean isVrShowing() {
        return assistantView != null && assistantView.isViewShowing();
    }

    private void showAssistantView(boolean isOneShot) {
        if (isShowing() && !isOneShot) {
            if (initIatSuccess || XmIatManager.getInstance().getInitState()) {
                //判断语音初始化状态
                startListening(false);
            }
        } else {
            mIsIatListenerComplete = false;
            show(isOneShot);
        }
    }


    @Override
    public void show(boolean isOneshot) {
        //防止方控及广播
        if (!checkPermission()) {
            return;
        }
        //如果全景影像在前台，则禁用语音助手
        if (XmCarFactory.getCarVendorExtensionManager().getCameraStatus()) {
            return;
        }
        if (!AssistantMicFocusManager.getInstance().requestMicFocus()) {
            XMToast.showToast(mContext, mContext.getString(R.string.get_mic_failed));
            return;
        }
        mContext.sendBroadcast(new Intent(CenterConstants.SHOW_VOICE_ASSISTANT_DIALOG));
        if (assistantView == null) {
            assistantView = new AssistantDialog();
        }
//        updateInitState();
        alertCount = 0;
        startListenCount = 0;
        assistantView.createView(mContext);
        if (conversationItemList == null) {
            conversationItemList = new ArrayList<>();
            assistantView.setAdapter(conversationItemList);
        } else {
            conversationItemList.clear();
        }
        assistantView.setAssistantListener(this);
        assistantView.show(localSeoptType);
        // 展示新手引导
        showGuideWindow();
        OnIatListener iatListener = new OnIatListener() {
            @Override
            public void onComplete(String voiceText, String parseText) {
                KLog.d("onComplete");
                onIatComplete(parseText);
            }

            @Override
            public void onVolumeChanged(int volume) {
                Log.d("onVolumeChanged", "onVolumeChanged: " + volume);
                onIatVolumeChanged(volume);
            }

            @Override
            public void onNoSpeaking() {
                KLog.d("onNoSpeaking");
                mIsIatListenerComplete = false;
                onIatNoSpeaking();
            }

            @Override
            public void onError(int errorCode) {
                KLog.d("onError");
                mIsIatListenerComplete = false;
                onIatError(errorCode);
            }

            @Override
            public void onResult(String recognizerText, boolean isLast, String currentText) {
                KLog.d("onResult");
                onIatResult(recognizerText, currentText);
                dismissGuide();
            }

            @Override
            public void onWavFileComplete() {
                KLog.d("onWavFileComplete");
            }

            @Override
            public void onRecordComplete() {
                KLog.d("onRecordComplete");
                mIsIatListenerComplete = true;
                onIatRecordComplete();
            }
        };
        XmIatManager.getInstance().setOnIatListener(iatListener);
        OnShortSrListener onShortSrListener = new OnShortSrListener() {
            @Override
            public void onShortSrChange(boolean isShortSr) {
                if (isShowing()) {
                    XmIvwManager.getInstance().startWakeup();
                } else {
                    XmIatManager.getInstance().setOnIatListener(null);
                    XmIatManager.getInstance().stopListening();
                    XmIvwManager.getInstance().startWakeup();
                }
            }
        };
        XmIatManager.getInstance().setOnShortSrListener(onShortSrListener);
        if (initIatSuccess || XmIatManager.getInstance().getInitState()) {
            // 开启聆听
            startListening(!isOneshot);
        } else {
            addWelcomeConversation(getString(R.string.is_in_initing));
            rePostCloseWindowRunnable();
            updateInitState();
        }

        LatLng location = LocationManager.getInstance().getCurrentPosition();
        if (location != null) {
            XmIatManager.getInstance().setLocalInfo(location.latitude, location.longitude);
        }
    }

    private boolean checkPermission() {
        boolean result = true;
        boolean disclaimer = XmProperties.build().get(DISCLAIMER_STATUS, false);
        if (!LoginManager.getInstance().isUserLogin() || !disclaimer) {
            Log.e("AssistantManager", "当前用户没有登录或已经设置唤醒不可用");
            result = false;
        }
        if (result) {
            //主进程
            VoiceConfigManager.getInstance().setUid(LoginManager.getInstance().getLoginUserId());
            //VR进程
            VrAidlManager.getInstance().setUid(LoginManager.getInstance().getLoginUserId());
        }
        if (result) {
            result = !IBCallAndPhoneStateManager.getInstance().isBusyState();
        }
        return result;
    }

    public void uploadWakeUp(String wakeUpMethodKey) {
        if (mContext == null)
            return;
        int wakeUpCount = TPUtils.get(mContext, wakeUpMethodKey, 0);
        String wakeUpMethod = "";
        switch (wakeUpMethodKey) {
            case HARDWARE_METHOD:
                wakeUpCount++;
                wakeUpMethod = mContext.getString(R.string.wake_up_hardware);
                break;
            case CLICK_METHOD:
                wakeUpCount++;
                wakeUpMethod = mContext.getString(R.string.wake_up_click);
                break;
            case SPEECH_METHOD:
                wakeUpCount++;
                wakeUpMethod = mContext.getString(R.string.wake_up_speech);
                break;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(mContext.getString(R.string.wake_up_method), wakeUpMethod);
            jsonObject.put(mContext.getString(R.string.wake_up_count), wakeUpCount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        XmAutoTracker.getInstance().onEvent(EventConstants.speechAssistant, jsonObject.toString(),
                "AssistantService", EventConstants.speechService);
        TPUtils.put(mContext, wakeUpMethodKey, wakeUpCount);
    }

    private void updateInitState() {
        final UpdateByTimeUtils updateByTimeUtils = new UpdateByTimeUtils();
        updateByTimeUtils.startUpdateByTime(1000, new UpdateByTimeUtils.CallBack() {
            @Override
            public void onCall() {
                if (initIatSuccess || XmIatManager.getInstance().getInitState()) {
                    addWelcomeConversation(AssistantUtils.getWelcomeWord(mContext));
                    startListening(false);
                    updateByTimeUtils.stopUpdate();
                }
            }
        });
    }


    private void onIatVolumeChanged(int volume) {
        initIatSuccess = true;
        if (assistantView == null) {
            return;
        }
        if (!assistantView.inContactChooseMode()) {
            assistantView.setVoiceAnim(volume);
        } else {
            //联系人界面的动画设置
            assistantView.getContactView().setVoiceAnim(volume);
        }
        if (isShowing()) {
            rePostCloseWindowRunnable();
        }
        if (!assistantView.inChooseMode() && isIatListening()) {
            if (volume > 0) {
                removeMinTimeCountDownRunnable();
            } else if (volume == 0) {
                postMinTimeCountDownRunnable();
            }
        }
    }

    private void onIatRecordComplete() {
        if (assistantView == null) {
            return;
        }
        if (isShowing()) {
            assistantView.stopInputAnim();
            rePostCloseWindowRunnable();
        }
    }

    private void onIatResult(String voiceText, String currentText) {
        if (TextUtils.isEmpty(voiceText)) {
            return;
        }
        if (VrConfig.useShortTimeSr && !isShowing() && !TextUtils.isEmpty(voiceText)) {
            assistantView.showSrDialog();
            handleForUpdated(voiceText);
            return;
        }
        rePostCloseWindowRunnable();
        handleForUpdated(voiceText);
        handleForChoose(currentText);
        XmAutoTracker.getInstance().onEventSpeechRec(EventConstants.recognizeResult, "成功",
                voiceText, currentText, "AssistantService", EventConstants.speechService);
    }


    private void handleForUpdated(String voiceText) {
        if (assistantView == null) {
            return;
        }
        if (inMultipleForChooseMode()) {
            return;
        }
        KLog.d("set pgs text");
        assistantView.getShowTextView().setText(voiceText);
    }


    private void handleForChoose(String text) {
        if (!inMultipleForChooseMode()) {
            return;
        }
        LxParseResult lxParseResult = LxParseResult.fromJson(text);
        if (lxParseResult != null && (lxParseResult.isPhoneCallAction() || lxParseResult.isRoutAction() ||
                lxParseResult.isMusicAction() || lxParseResult.isRadioAction())) {
            hideMultiPageView();
            onIatComplete(text);
        }
        AssistantProcessorChain.getInstance().handleWaitingForChoose(text);
    }


    private String vj;
    private String vt;
    private LxParseResult pr;

    private void onIatComplete(String voiceJson) {
        KLog.d("zhs", "onIatComplete---" + System.currentTimeMillis());
        if (assistantView != null) {
            assistantView.stopInputAnim();
        }
        String voiceText;
        try {
            JSONObject jsonObject = new JSONObject(voiceJson);
            if (jsonObject.has("intent")) {
                voiceJson = jsonObject.getString("intent");
                jsonObject = new JSONObject(voiceJson);
            }
            String text = jsonObject.getString("text");
            voiceText = HandleSpecialWordUtils.replaceFilter(text);
        } catch (Exception e) {
            voiceText = "";
            e.printStackTrace();
        }
        voiceText = nextPicWrapper(voiceText);
        if (assistantView == null) {
            return;
        }
        conversationItemList.clear();
        rePostCloseWindowRunnable();
        voiceJson = voiceJson.trim();
        if (TextUtils.isEmpty(voiceJson)) {
            speakUnderstand();
            return;
        }
        handleForUpdated(voiceText);
        KLog.json(voiceJson);
        LxParseResult parserResult = LxParseResult.fromJson(voiceJson);
        //if (parserResult != null && parserResult.getRc() != 4) {
        //科大离线版解析成功，把解析结果上报给后台
        //UploadTextParserAgent.getInstance().uploadTextParserLogInThread(voiceText, voiceJson);
        //}

        //开启一个新的线程去做本地拦截的判断处理，下面的这个场景分发同步进行，在进入场景前获取判断的值，同时做个超时的判断
        /*vj = voiceJson;
        vt = voiceText;
        pr = parserResult;
        ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
            @Override
            public void run() {
                boolean handle = AssistantProcessorChain.getInstance().processRegisterKeyWord(vt, vj, pr);
                if (handle) {
                    closeAssistant();
                }
            }
        });*/
        AssistantProcessorChain.getInstance().processByScenario(voiceText, voiceJson, parserResult);
        currentConversationItem = null;
    }


    private String nextPicWrapper(String voiceText) {
        //由于讯飞识别总是将“下一张”识别成“下一章”，且我们的指令中并没有“下一章”这样的指令，
        //所以这里舍弃“下一章”，强转成“下一张”
        if ("下一章".equals(voiceText)) {
            voiceText = "下一张";
        }
        return voiceText;
    }


    private void onIatNoSpeaking() {
        if (assistantView == null || !isShowing() || assistantView.isSrDialogShowing()) {
            return;
        }
        if (!NetworkUtils.isConnected(mContext)) {
            changeMicStatus();
            addNetworkUnavailableConversation();
            return;
        }

//        if (assistantView.isHelpViewShowing()) {
//            assistantView.getMicroPhoneButton().setImageResource(R.drawable.robot_anim);
//            removeMinTimeCountDownRunnable();
//            return;
//        }
        BaseInterceptor interceptor = InterceptorManager.getInstance().getCurrentInterceptor();
        if (interceptor instanceof NavigationInterceptor) {
            ((NavigationInterceptor) interceptor).cancelOperation();
            clearInterceptor();
            return;
        }
        changeMicStatus();
        relisten("");
    }

    private void relisten(String text) {
        if (alertCount >= 1) {
            startListenEndSpeak(getString(mEndSpkeak[getRandomIndex()]));
        } else {
            startListenEndSpeak(!TextUtils.isEmpty(text) ? text : getString(R.string.not_speak_anything_and_restart_speak));
        }
    }

    /**
     * 随机为说话语音
     */
    public static int getRandomIndex() {
        Random random = new Random();
        int i = random.nextInt(mEndSpkeak.length);
        return i;
    }

    private void startListenEndSpeak(String speak) {
        addFeedBackConversation(speak);
        speakContent(speak, new WrapperSynthesizerListener() {
            @Override
            public void onCompleted() {
                startListening(false);
                alertCount++;
                if (alertCount >= 2) {
                    //closeAfterSpeak(mContext.getString(R.string.stand_down_first));
                    rePostCloseWindowRunnable(0);
                    alertCount = 0;
                }
            }

        });
    }


    private void onIatError(int errorCode) {
        if (assistantView == null || !isShowing() || assistantView.isSrDialogShowing()) {
            return;
        }
        assistantView.stopInputAnim();
        if (!NetworkUtils.isConnected(mContext)) {
            changeMicStatus();
            addNetworkUnavailableConversation();
            return;
        }
        KLog.d("errorCode：" + errorCode);
        String speakText;
        if (10015 == errorCode || 10007 == errorCode || 0 == errorCode) {
            //代表引擎未返回识别结果
            //isFirstAlertUnWork = true;
            speakText = mContext.getString(R.string.too_loud_to_recognize);
        } else {
            speakText = mContext.getString(R.string.voice_engine_error);
        }
//        if (assistantView.isHelpViewShowing()) {
//            assistantView.getMicroPhoneButton().setImageResource(R.drawable.robot_anim);
//            removeMinTimeCountDownRunnable();
//            return;
//        }
//        if (System.currentTimeMillis() - firstReportWelcome >= MIN_NOT_WORK_TIME)
//            isFirstAlertUnWork = true;

        changeMicStatus();
        addFeedBackConversation(speakText);
        relisten(speakText);
    }

    private boolean isIatListening() {
        return RecorderType.IAT == RecorderManager.getInstance().getCurRecorderType();
    }

    @Override
    public void onDialogDismiss(DialogInterface dialogInterface) {
        XmTtsManager.getInstance().stopSpeaking();
        releaseAudioFocus();
        onVoicePopupDismiss();
        showLoadingView(false);
        clearState();
        sendVrStatusToDualScreen(false);
//        ThirdVoiceKeyManager.getInstance().notifyVoiceIsShowing(false);
//        EventBus.getDefault().unregister(AssistantManager.this);
//        VoiceDialogAnimationManager.getInstance().startWakeup();
//        if (mListener != null) {
//            mListener.onDismiss(mVoiceDialog);
//            mListener = null;
//        }
    }

    private void clearState() {
        clearInterceptor();
        ScenarioDispatcher.getInstance().IatWeChatScenario(mContext).uploadWeChatState(false);
        ScenarioDispatcher.getInstance().getIatNewsScenario(mContext).executeSkillSucceeded();
    }

    private void clearInterceptor() {
        if (InterceptorManager.getInstance().getCurrentInterceptor() instanceof AnswerPhoneInterceptor) {
            InterceptorManager.getInstance().clearSavedInterceptor();
        } else {
            InterceptorManager.getInstance().clearInterceptor();
        }
    }

    private void onVoicePopupDismiss() {
        startListenCount = 0;
        removeCloseWindowRunnable();
        removeMinTimeCountDownRunnable();
        XmTtsManager.getInstance().stopSpeaking();
        XmTtsManager.getInstance().removeListeners();
        if (VrConfig.useShortTimeSr) {
            XmIatManager.getInstance().cancelListening();
            XmIatManager.getInstance().startListeningNormal();
        } else {
            XmIatManager.getInstance().stopListening();
            XmIatManager.getInstance().setOnIatListener(null);
        }
    }

    @Override
    public boolean onDialogKeyEvent(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent
                .ACTION_DOWN && keyEvent.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onSpeak(String content) {
        speakContent(content);
    }

    @Override
    public void onViewClose() {
        closeAssistant();
    }

    @Override
    public void onViewTouch(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            removeCloseWindowRunnable();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            rePostCloseWindowRunnable();
        }
    }

    @Override
    public void onViewClick(int viewId) {
        switch (viewId) {
            case R.id.btn_mic:
                if (NetworkUtils.isConnected(mContext)) {
                    if (isIatListening()) {
                        if (!inMultipleForChooseMode()) {
//                            stopListening();
                            removeMinTimeCountDownRunnable();
                            startListening(false);
                        }
                    } else {
                        if (inMultipleForChooseMode()) {
                            startListeningForChoose(currSrSceneStksCmd);
                        } else {
                            startListening(false);
                        }
                    }
                } else {
                    addNetworkUnavailableConversation();
                }
                rePostCloseWindowRunnable();
                break;
            case R.id.assistant_iv_back:
                onBackPressed();
                break;
            case R.id.rl_example:
                stopListening();
                showHelper();
                break;
            default:
                break;
        }
    }

    @Override
    public void speakContent(String text) {
        speakContent(text, null);
    }

    @Override
    public void speakContent(String text, final OnTtsListener listener) {
        if (TextUtils.isEmpty(text) && listener != null) {
            listener.onCompleted();
            return;
        }

        Log.d("QBX", "speakContent: " + text);
        XmTtsManager.getInstance().stopSpeaking();
        XmTtsManager.getInstance().startSpeakingByAssistant(text, this.mOnTtsListener = new OnTtsListener() {
            @Override
            public void onCompleted() {
                KLog.d("assistant tts speak complete");
                Log.d("QBX", "speakContent onCompleted");
                rePostCloseWindowRunnable();
                if (listener != null) {
                    listener.onCompleted();
                }
                mOnTtsListener = null;
            }

            @Override
            public void onBegin() {
                KLog.d("assistant tts speak start");
                if (listener != null) {
                    listener.onBegin();
                }
                removeCloseWindowRunnable();
                removeMinTimeCountDownRunnable();
            }

            @Override
            public void onError(int code) {
                KLog.d("assistant tts speak onError");
                if (listener != null) {
                    listener.onError(code);
                }
                mOnTtsListener = null;
            }
        });
    }

    @Override
    public void speakThenListening(String word) {
        XmTtsManager.getInstance().startSpeakingByAssistant(word, new WrapperSynthesizerListener() {
            @Override
            public void onCompleted() {
                startListening(false);
                rePostCloseWindowRunnable();
            }

            @Override
            public void onBegin() {
                removeCloseWindowRunnable();
                removeMinTimeCountDownRunnable();
            }
        });
    }

    @Override
    public void speakThenClose(String word) {
        XmTtsManager.getInstance().startSpeakingByAssistant(word, new OnTtsListener() {
            @Override
            public void onCompleted() {
                closeAssistant();
            }

            @Override
            public void onBegin() {

            }

            @Override
            public void onError(int code) {

            }
        });
    }

    @Override
    public void startListeningForChoose(final String stkCmd) {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                if (!isShowing()) {
                    return;
                }
                if (!inMultipleForChooseMode() || TextUtils.isEmpty(stkCmd)) {
                    currSrSceneStksCmd = "";
                    return;
                }
                currSrSceneStksCmd = stkCmd;
                prepareStartListening();
                if (!isShowing()) {
                    return;
                }
                XmIatManager.getInstance().startListeningForChoose(stkCmd);
            }
        });
    }

    @Override
    public void speakUnderstand() {
        XmTtsManager.getInstance().stopSpeaking();
//        if (isShowing()) {
//            return;
//        }
        int speakRandom = AssistantUtils.getUnStandWord();
        String unStandSpeak = getString(AssistantUtils.mUnStands_Speak[speakRandom]);
        String unStand = getString(AssistantUtils.mUnStands[speakRandom]);
        speakContent(unStandSpeak, new WrapperSynthesizerListener() {
            @Override
            public void onCompleted() {
                startListenCount++;
                if (startListenCount < 2) {
                    startListening(false);
                }
            }
        });
        addFeedBackConversation(unStand);
    }

    @Override
    public void updateShowText(String statusText) {
        if (assistantView != null) {
            assistantView.updateShowText(statusText);
        }
    }


    @Override
    public void startListening(boolean first) {
        assistantView.playWaitStateAnim();
        prepareStartListening();
        if (NetworkUtils.isConnected(mContext))
            postMinTimeCountDownRunnable();
        //欢迎语逻辑
        String welcomeWord = AssistantUtils.getWelcomeWord(mContext);
        addWelcomeConversation(welcomeWord);
        boolean hasFocus = requestAudioFocus();
        if (first && hasFocus) {
            boolean welcomeMedia = AssistantUtils.isWelcomeMedia(mContext);
            if (welcomeMedia) {
                ThreadDispatcher.getDispatcher().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SoundPoolUtils.getInstance(mContext).play(R.raw.sound_assistant_start, true);
                        ThreadDispatcher.getDispatcher().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                SoundPoolUtils.getInstance(mContext).releaseAudioFocus();
                            }
                        }, 500);
                    }
                }, 100);
                XmIatManager.getInstance().startListeningNormal();
            } else if (!TextUtils.isEmpty(welcomeWord)) {
                speakContent(welcomeWord, new WrapperSynthesizerListener() {
                    @Override
                    public void onCompleted() {
                        XmIatManager.getInstance().startListeningNormal();
                    }
                });
            }
        } else {
            XmIatManager.getInstance().startListeningNormal();
        }
        if (assistantView != null) {
            assistantView.clearContent();
        }
        sendVrStatusToDualScreen(true);
    }

    private void sendVrStatusToDualScreen(boolean b) {
        if (mContext != null) {
            Intent intent = new Intent(VrConstants.Actions.VOICE_RECORDING);
            intent.putExtra(VrConstants.ActionExtras.VOICE_RECORDING_STATUS, b);
            mContext.sendBroadcast(intent);
        }
    }

    @Override
    public void stopListening() {
        XmTtsManager.getInstance().stopSpeaking();
        XmIatManager.getInstance().stopListening();
    }

    private void prepareStartListening() {
        if (!isShowing()) {
            return;
        }
        mIsIatListenerComplete = false;
        if (!inMultipleForChooseMode() && !assistantView.isHelpViewShowing()) {
            assistantView.showFullScreenDialog();
            hideMultiPageView();
        }
        if (!inContactChooseMode() && !assistantView.isHelpViewShowing()) {
            assistantView.showFullScreenDialog();
            hideContactView();
        }
//        conversationItemList.clear();
//        assistantView.notifyDataSetChanged();
//        currentConversationItem = null;
        // 先停掉tts
        XmTtsManager.getInstance().removeListeners();
        XmTtsManager.getInstance().stopSpeaking();
        assistantView.updateFeedbackView(false, "");
        String showText = AssistantUtils.getWelcomeWord(mContext);
        if (inMultipleForChooseMode()) {
            showText = getString(R.string.i_am_listening);
        }
        assistantView.updateShowText(showText);
        if (conversationItemList.isEmpty() && !inMultipleForChooseMode() && !assistantView.isHelpViewShowing()) {
            assistantView.getRlExample().setVisibility(View.VISIBLE);
        }
        //设置讯飞套件位置
//        XmIatManager.getInstance().setLocalInfo();
    }


    @Override
    public void onResume() {
        XmIvwManager.getInstance().startWakeup();
    }

    @Override
    public void onHomePressed() {
        XmTtsManager.getInstance().stopSpeaking();
        closeAssistant();
    }

    @Override
    public void showProgressDialog(final String loadingText) {
//        uiHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                if (progressDialog == null) {
//                    progressDialog = new CustomProgressDialog(mContext);
//                    Window window = progressDialog.getWindow();
//                    if (window == null) {
//                        closeAssistant();
//                        return;
//                    }
//                    window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//                    progressDialog.setCanceledOnTouchOutside(false);
//                }
//                if (progressDialog.isShowing()) {
//                    progressDialog.setMessage(loadingText);
//                } else {
//                    progressDialog.setMessage(loadingText);
//                    progressDialog.show();
//                }
//            }
//        });
    }

    @Override
    public void dismissProgressDialog() {
//        uiHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    if (progressDialog == null || !progressDialog.isShowing()) {
//                        return;
//                    }
//                    progressDialog.dismiss();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }


    @Override
    public void addFeedBackConversation(String content) {
        if (assistantView != null) {
            if (!TextUtils.isEmpty(content)) {
                UIUtils.runOnMain(new Runnable() {
                    @Override
                    public void run() {
                        assistantView.updateFeedbackView(true, content);
                    }
                });
            }
        }
    }

    @Override
    public void addItemToConversationList(ConversationItem item) {
        if (conversationItemList == null || assistantView == null) {
            return;
        }
        if (inMultipleForChooseMode()) {
            return;
        }
        if (assistantView.isHelpViewShowing()) {
            assistantView.hideHelpView();
        }
        conversationItemList.clear();
        conversationItemList.add(item);
        assistantView.getRlExample().setVisibility(View.GONE);
        assistantView.showFullScreenDialog();
        assistantView.notifyDataSetChanged();
    }

    @Override
    public void addItemAndMoveToBottom(ConversationItem item) {
        if (conversationItemList != null) {
            this.conversationItemList.add(item);
        }
        if (assistantView != null) {
            assistantView.notifyDataSetChanged();
            assistantView.getRlExample().setVisibility(View.GONE);
        }
    }

    @Override
    public boolean showMultiPageView(BaseMultiPageAdapter adapter) {
        return assistantView != null && assistantView.setMultiPageData(adapter);
    }

    @Override
    public void showDetailView() {
        if (assistantView != null) {
            assistantView.showDetailView();
        }
    }

    @Override
    public void hideMultiPageView() {
        if (assistantView != null) {
            assistantView.hideMultiPageView();
        }
    }

    @Override
    public boolean showContactView(BaseMultiPageAdapter adapter) {
        return assistantView != null && assistantView.setContactData(adapter);
    }

    @Override
    public void displayMusicRecognitionView(boolean show) {
        assistantView.getMusicRecognitionView().setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void hideContactView() {
        if (assistantView != null) {
            assistantView.hideContactView();
        }
    }

    @Override
    public MultiPageView getMultiPageView() {
        if (assistantView != null) {
            return assistantView.getMultiPageView();
        }
        return null;
    }

    @Override
    public ContactView getContactView() {
        if (assistantView != null) {
            return assistantView.getContactView();
        }
        return null;
    }

    @Override
    public TextView getSearchResultOperate() {
        if (assistantView != null) {
            return assistantView.getSearchResultOperate();
        }
        return null;
    }

    @Override
    public long getDialogSession() {
        if (assistantView != null) {
            return assistantView.getDialogSession();
        }
        return -1;
    }

    @Override
    public void closeAssistant() {
        if (assistantView != null) {
            assistantView.closeView();
        }
        dismissGuide();
    }

    @Override
    public void closeAssistant(DialogInterface.OnDismissListener listener) {

    }

    private boolean requestAudioFocus() {
        boolean isFocus = false;
        if (mAudioManager == null)
            mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        if (mAudioManager != null) {
            int i = mAudioManager.requestAudioFocus(audioFocusChangeListener, VrConfig.IAT_STREAM_TYPE,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE);
            isFocus = i == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        }
        return isFocus;
    }


    private void releaseAudioFocus() {
        if (mAudioManager != null) {
            mAudioManager.abandonAudioFocus(audioFocusChangeListener);
        }
    }


    private void changeMicStatus() {
        rePostCloseWindowRunnable();
        removeMinTimeCountDownRunnable();
    }


    private void addWelcomeConversation(String welcome) {
        if (assistantView == null) {
            return;
        }
        assistantView.updateFeedbackView(false, "");
        assistantView.getShowTextView().setText(welcome);
        rePostCloseWindowRunnable();
    }


    private void addNetworkUnavailableConversation() {
        if (NetworkUtils.isConnected(mContext)) {
            return;
        }
        String tips = mContext.getString(R.string.is_network_unavailable);
        assistantView.updateShowText(tips);
        if (inMultipleForChooseMode()) {
            XMToast.showToast(mContext, tips);
        }
    }

    @Override
    public boolean isShowing() {
        return assistantView != null && assistantView.isViewShowing();
    }

    @Override
    public boolean inMultipleForChooseMode() {
        return assistantView != null && assistantView.inChooseMode();
    }

    public boolean inContactChooseMode() {
        return assistantView != null && assistantView.inContactChooseMode();
    }

    private String getString(int id) {
        if (mContext != null && id > 0) {
            return mContext.getApplicationContext().getString(id);
        }

        return "";
    }


    private void onBackPressed() {
        clearState();
        if (assistantView.getMultiPageView().isDetailPage()) {
            assistantView.getMultiPageView().hideDetailPage();
            if (ListUtils.isEmpty(assistantView.getMultiPageView().getAdapter().getAllList())) {
                backFromMultiPageView();
            }
            setSearchResultOperate();
        } else if (assistantView.inMultipleForChooseMode()) {
            backFromMultiPageView();
        } else if (assistantView.inContactChooseMode()) {
            XmTtsManager.getInstance().stopSpeaking();
            hideContactView();
            startListening(false);
        } else if (assistantView.isHelpViewShowing()) {
            if (assistantView.getHelpViewDetail()) {
                XmTtsManager.getInstance().stopSpeaking();
                XmIatManager.getInstance().stopListening();
                assistantView.showHelpView();
                rePostCloseWindowRunnable();
                assistantView.setHelpViewDetail();
            } else {
                XmTtsManager.getInstance().stopSpeaking();
                XmIatManager.getInstance().stopListening();
                assistantView.hideHelpView();
                rePostCloseWindowRunnable();
            }
            //startListening();
        } else {
            closeAssistant();
        }
    }

    private void setSearchResultOperate() {
        TextView searchResultOperate = getSearchResultOperate();
        String prefix = mContext.getString(R.string.please_choose_or_cancel);
        String content = mContext.getString(R.string.search_result_more_page);
        SpannableString spannableString = new SpannableString(prefix + content);
        spannableString.setSpan(new ForegroundColorSpan(mContext.getColor(R.color.white)), 0, prefix.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(mContext.getColor(R.color.gray)), prefix.length() + 1, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        searchResultOperate.setText(spannableString);
    }

    private void backFromMultiPageView() {
        XmTtsManager.getInstance().stopSpeaking();
        boolean handle = false;
        currentScenario = AssistantProcessorChain.getInstance().getSemanticByNetworkProcessor().getCurrentScenario();
        if (currentScenario != null) {
            handle = currentScenario.handleBack();
        }
        if (!handle) {
            //二级场景返回一级场景
            hideMultiPageView();
            startListening(false);
        }
    }

    @Override
    public void showLoadingView(boolean show) {
        if (assistantView != null) {
            assistantView.showLoadingView(show);
        }
    }

    @Override
    public void showMultiPageView(boolean show) {
        if (assistantView != null) {
            assistantView.showMultiPageView(show);
        }
    }

    @Override
    public void stopSpeak() {
        XmTtsManager.getInstance().stopSpeaking();
    }

    @Override
    public void closeAfterSpeak(String content) {
        addFeedBackConversation(content);
        speakContent(content, new WrapperSynthesizerListener() {
            @Override
            public void onCompleted() {
                AssistantManager.getInstance().closeAssistant();
            }
        });
    }

    public void showHelper() {
        if (assistantView != null) {
            assistantView.showHelpView();
            rePostCloseWindowRunnable();
        }
    }

    private void showGuideWindow() {
        if (!GuideDataHelper.shouldShowGuide(GuideConstants.LAUNCHER_SHOWED, false)) return;
        ViewGroup contentView = assistantView.getContentView();
        guideContainer = LayoutInflater.from(mContext).inflate(R.layout.guide_view_assistant, null);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        contentView.addView(guideContainer, lp);
        TextView tvGuideDesc = guideContainer.findViewById(R.id.tv_guide_desc);
        Spanned guideDesc = Html.fromHtml(getString(R.string.guide_try_assistant_description));
        tvGuideDesc.setText(guideDesc);
        GuideDataHelper.finishGuideData(GuideConstants.LAUNCHER_SHOWED);
        mContext.sendBroadcast(new Intent(Constants.ASSISTANT_GUIDE_VIEW_SHOW));
    }

    /**
     * 当语音识别结束 || 长时间未说话语音关闭 关闭新手引导
     */
    private void dismissGuide() {
        if (guideContainer != null) {
            assistantView.getContentView().removeView(guideContainer);
            guideContainer = null;
            XmProperties.build().put("LAUNCHER_HAS_GUIDED", true);
        }
    }

    public void setWakeUpListener(WakeUpListener wakeUpListener) {
        this.mWakeUpListener = wakeUpListener;
    }

    void showWithoutListening() {
        show(false);
        stopListening();
        if (assistantView != null) {
            assistantView.stopInputAnim();
            assistantView.setStopListening(true);
            assistantView.playNormalStateAnim();
            assistantView.getShowTextView().setText(mContext.getString(R.string.searching));
        }
    }

    @Override
    public void onChange(int id, Object value) {
        if (id == SDKConstants.ID_WORK_MODE_STATUS) {
            KLog.d("wzw", "ACC OFF/ON");
            XmTtsManager.getInstance().stopSpeaking();
            XmIatManager.getInstance().stopListening();
            closeAssistant();
        } else if (id == SDKConstants.ID_CAMERA_STATUS) {
            if (XmCarVendorExtensionManager.getInstance().getCameraStatus()) {
                KLog.d("wzw", "CAMERA_STATUS");
                XmTtsManager.getInstance().stopSpeaking();
                XmIatManager.getInstance().stopListening();
                closeAssistant();
            }
        }
    }
}
