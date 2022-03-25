package com.xiaoma.cariflytek.tts;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.iflytek.speech.ISSErrors;
import com.iflytek.tts.ESpeaker;
import com.iflytek.tts.ITtsInitListener;
import com.iflytek.tts.ITtsListener;
import com.iflytek.tts.TtsSession;
import com.xiaoma.cariflytek.R;
import com.xiaoma.cariflytek.iat.VrAidlServiceManager;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.XmProperties;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.VrConfig;
import com.xiaoma.vr.tts.BaseTtsManager;
import com.xiaoma.vr.tts.OnTtsListener;
import com.xiaoma.vr.tts.TtsPriority;
import com.xiaoma.vr.tts.TtsPriorityHelper;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/8/9
 * Desc:离线版tts管理类
 */

public class LxTtsManager extends BaseTtsManager {
    private static final String TAG = "LxXfTtsManager";
    private int defRole = ESpeaker.ivTTS_ROLE_XIAOTENG;
    private int voiceRole;
    private String resDir = VrConfig.IFLY_TEK_RES + "/tts/";
    private static final String VOICE_SPEED_VALUE = "[s5]";  //语速从s0-s10 共10级
    private String VOICE_SPEAKER_KEY = "speaker_role";
    //    private String speakingText;
    private Context mContext;
    private boolean isSuccess;
    private TtsSession mTtsSession;
    private AudioManager mAudioManager;
    private boolean isTtsSpeaking = false;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private TtsPriorityHelper mTtsPriority;

    private ConcurrentHashMap<String, OnTtsListener> synthesizerListenerMap = new ConcurrentHashMap<>();

    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new
            AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    KLog.d(TAG, "focusChange :" + focusChange);
                }
            };

    /**
     * 合成回调监听。
     */
    private ITtsListener ttsListener = new ITtsListener() {
        @Override
        public void onPlayCompleted(final String ttsID) {
            if (mTtsPriority != null) {
                mTtsPriority.recovery();
            }
            Log.d(TAG, "play complete");
            isTtsSpeaking = false;
            abandonAudioFocus();
            if (synthesizerListenerMap.size() <= 0) {
                return;
            }
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        Iterator iterator = synthesizerListenerMap.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry entry = (Map.Entry) iterator.next();
                            String id = (String) entry.getKey();
                            if (id.equals(ttsID)) {
                                OnTtsListener synthesizerListener = (OnTtsListener) entry.getValue();
                                synthesizerListener.onCompleted();
                                synthesizerListenerMap.remove(ttsID);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void onPlayBegin(String ttsID) {
            Log.d(TAG, "play begin");
            if (requestAudioFocus()) {
                onBegin(ttsID);
            } else {
                onError(ttsID);
            }
        }

        @Override
        public void onPlayInterrupted(String ttsID) {
            isTtsSpeaking = false;
            Log.d(TAG, "play onPlayInterrupted");
            if (synthesizerListenerMap.size() <= 0) {
                return;
            }
            try {
                Iterator iterator = synthesizerListenerMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry entry = (Map.Entry) iterator.next();
                    String id = (String) entry.getKey();
                    if (id.equals(ttsID)) {
                        OnTtsListener synthesizerListener = (OnTtsListener) entry.getValue();
                        synthesizerListener.onError(OnTtsListener.ErrorCode.SPEAK_INTERRUPT);
                        synthesizerListenerMap.remove(ttsID);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                abandonAudioFocus();
            }
        }

        @Override
        public void onProgressReturn(int textIndex, int textLen) {

        }
    };

    private void onError(final String ttsID) {
        isTtsSpeaking = false;
        abandonAudioFocus();
        if (synthesizerListenerMap.size() <= 0) {
            return;
        }
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Iterator iterator = synthesizerListenerMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry entry = (Map.Entry) iterator.next();
                        String id = (String) entry.getKey();
                        if (id.equals(ttsID)) {
                            OnTtsListener synthesizerListener = (OnTtsListener) entry.getValue();
                            synthesizerListener.onError(OnTtsListener.ErrorCode.SPEAK_FOCUS);
                            synthesizerListenerMap.remove(ttsID);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void onBegin(final String ttsID) {
        isTtsSpeaking = true;
        if (synthesizerListenerMap.size() <= 0) {
            return;
        }
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Iterator iterator = synthesizerListenerMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry entry = (Map.Entry) iterator.next();
                        String id = (String) entry.getKey();
                        if (id.equals(ttsID)) {
                            OnTtsListener synthesizerListener = (OnTtsListener) entry.getValue();
                            synthesizerListener.onBegin();
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 合成初始化监听
     */
    private ITtsInitListener mITtsInitListener = new ITtsInitListener() {
        @Override
        public void onTtsInited(boolean state, int errId) {
            if (state) {
                Log.d(TAG, "onTtsInitialized true");
                mTtsSession.sessionStart(ttsListener, VrConfig.TTS_STREAM_TYPE);
            } else {
                if (errId == ISSErrors.REMOTE_EXCEPTION) {
                    mTtsSession.initService();
                }
            }
        }
    };


    @Override
    public void init(Context context) {
        KLog.d("lx tts init");
        if (mTtsSession != null) {
            return;
        }
        mContext = context.getApplicationContext();
        mTtsSession = new TtsSession(mContext, mITtsInitListener, resDir);
        String uid = VrAidlServiceManager.getInstance().getUid();
        voiceRole = XmProperties.build(uid).get(VOICE_SPEAKER_KEY, defRole);
        Log.d(TAG, "tts role ---" + voiceRole);
        setParam();
        mTtsPriority = TtsPriorityHelper.getInstance();
    }

    @Override
    public boolean startSpeaking(final String text) {
        Log.d("XmTtsManager", String.format("startSpeaking: %s ", text));
        if (text == null) {
            return false;
        }
        isSuccess = false;
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                mTtsSession.setParam(ESpeaker.ISS_TTS_PARAM_SPEAKER, voiceRole);
                String id = text + System.currentTimeMillis();
                isSuccess = mTtsSession.startSpeak(text, id) == ISSErrors.ISS_SUCCESS;
               /* if (!isSuccess) {
                    if (synthesizerListenerMap.size() <= 0) {
                        return;
                    }
                    try {
                        Iterator iterator = synthesizerListenerMap.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry entry = (Map.Entry) iterator.next();
                            OnTtsListener synthesizerListener = (OnTtsListener) entry.getValue();
                            synthesizerListener.onError(OnTtsListener.ErrorCode.SPEAK_ERROR);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }*/
            }
        });

        return isSuccess;
    }

    @Override
    public boolean startSpeaking(final String text, final OnTtsListener listener) {
        Log.d("XmTtsManager", String.format("startSpeaking: %s ", text));
        if (text == null) {
            return false;
        }
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "mTtsSession.startSpeak--->" + text);
                String id = text + System.currentTimeMillis();
                synthesizerListenerMap.put(id, listener);
                mTtsSession.setParam(ESpeaker.ISS_TTS_PARAM_SPEAKER, voiceRole);
                isSuccess = mTtsSession.startSpeak(VOICE_SPEED_VALUE + text, id) == ISSErrors.ISS_SUCCESS;

            }
        });
        return isSuccess;
    }

    public boolean isSpeaking() {
        boolean isSpeaking = false;
        if (mTtsSession != null) {
            isSpeaking = mTtsSession.isSpeaking();
        }
        return isSpeaking;
    }

    @Override
    public boolean startSpeaking(final String text, final int speed, final OnTtsListener listener) {
        Log.d("XmTtsManager", String.format("startSpeaking: %s ", text));
        if (text == null) {
            return false;
        }
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                String id = text + System.currentTimeMillis();
                synthesizerListenerMap.put(id, listener);
                mTtsSession.setParam(ESpeaker.ISS_TTS_PARAM_SPEAKER, voiceRole);
                isSuccess = mTtsSession.startSpeak(mContext.getString(R.string.voice_speed, speed, text), id) == ISSErrors.ISS_SUCCESS;

            }
        });
        return isSuccess;
    }

    @Override
    public void removeListeners() {
        if (synthesizerListenerMap != null) {
            synthesizerListenerMap.clear();
        }
    }

    private void getVoiceRole() {
        //todo voice role
        //voiceRole = XmVoiceTypeManager.getInstance().getVoiceId();
    }


    @Override
    public void auditionVoiceType(String voiceParam, String speakContent, OnTtsListener listener) {
        if (NumberUtils.isDigits(voiceParam)) {
            auditionVoiceType(NumberUtils.toInt(voiceParam), speakContent, listener);
        }
    }


    private void auditionVoiceType(int voiceParam, String speakContent, final OnTtsListener listener) {
        if (mTtsSession == null || voiceParam < 0) return;
        stopSpeaking();
        mTtsSession.setParam(ESpeaker.ISS_TTS_PARAM_SPEAKER, voiceParam);
        if (TextUtils.isEmpty(speakContent)) {
            speakContent = mContext.getString(R.string.audition_voice_content);
        }
        //因播报完成需要重设，所以此处添加一个listener来处理
        final OnTtsListener lxTtsListener = new OnTtsListener() {
            @Override
            public void onCompleted() {
                mTtsSession.setParam(ESpeaker.ISS_TTS_PARAM_SPEAKER, voiceRole);
                if (listener != null) {
                    listener.onCompleted();
                }
            }

            @Override
            public void onBegin() {
                if (listener != null) {
                    listener.onBegin();
                }
            }

            @Override
            public void onError(int code) {

            }
        };
        final String finalSpeakContent = speakContent;
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                String id = finalSpeakContent + System.currentTimeMillis();
                synthesizerListenerMap.put(id, lxTtsListener);
                isSuccess = mTtsSession.startSpeak(finalSpeakContent, id) == ISSErrors.ISS_SUCCESS;

            }
        });
    }

    private void abandonAudioFocus() {
        if (mAudioManager != null) {
            mAudioManager.abandonAudioFocus(audioFocusChangeListener);
        }
    }

    private boolean requestAudioFocus() {
        boolean isFocus = false;
        if (mAudioManager == null)
            mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        if (mAudioManager != null) {
            int i = mAudioManager.requestAudioFocus(audioFocusChangeListener,
                    VrConfig.TTS_STREAM_TYPE, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
            KLog.d(TAG, " requestAudioFocus : " + i);
            isFocus = i == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        }
        return isFocus;
    }

    @Override
    public String setVoiceType(int type) {
        if (mTtsSession == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(mContext.getString(R.string.switch_voice_hint));
        //XmVoiceTypeManager.getInstance().setVoiceType(type);
        mTtsSession.setParam(ESpeaker.ISS_TTS_PARAM_SPEAKER, type);
        //todo
//        String voiceName = XmVoiceTypeManager.getInstance().voiceToName(type);
//        builder.append(voiceName);
//        this.voiceRole = type;
//        return builder.toString();
        return mContext.getString(R.string.voice_woman);
    }

    @Override
    public boolean setVoiceType(String voiceParam, String voiceName) {
        if (NumberUtils.isDigits(voiceParam)) {
            return setVoiceType(NumberUtils.toInt(voiceParam), voiceName);
        }
        return false;
    }

    public boolean setVoiceType(int voiceParam, String voiceName) {
        Log.d(TAG, " setVoiceType : " + voiceParam);
        if (mTtsSession == null || voiceParam < 0)
            return false;
        //todo
        //XmVoiceTypeManager.getInstance().setVoiceType(voiceParam);
        mTtsSession.setParam(ESpeaker.ISS_TTS_PARAM_SPEAKER, voiceParam);
        voiceRole = voiceParam;
        String uid = VrAidlServiceManager.getInstance().getUid();
        XmProperties.build(uid).put(VOICE_SPEAKER_KEY, voiceRole);
        return true;
    }


    @Override
    public boolean startSpeaking(final String text, TtsPriority priority) {
        Log.d("XmTtsManager", String.format("startSpeaking: %s  priority:%s", text, priority));
        if (text == null) {
            return false;
        }
        isSuccess = false;
        if (mTtsPriority != null && !mTtsPriority.interruptPriority(priority)) {
            return false;
        }
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "mTtsSession.startSpeak---" + text);
                mTtsSession.setParam(ESpeaker.ISS_TTS_PARAM_SPEAKER, voiceRole);
                String id = text + System.currentTimeMillis();
                isSuccess = mTtsSession.startSpeak(text, id) == ISSErrors.ISS_SUCCESS;
               /* if (!isSuccess) {
                    if (synthesizerListenerMap.size() <= 0) {
                        return;
                    }
                    try {
                        Iterator iterator = synthesizerListenerMap.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry entry = (Map.Entry) iterator.next();
                            OnTtsListener synthesizerListener = (OnTtsListener) entry.getValue();
                            synthesizerListener.onError(OnTtsListener.ErrorCode.SPEAK_ERROR);
                        }
                        mTtsPriority.recovery();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }*/
            }
        });

        return isSuccess;
    }

    @Override
    public boolean startSpeaking(final String text, TtsPriority priority, final OnTtsListener listener) {
        Log.d("XmTtsManager", String.format("startSpeaking: %s  priority:%s", text, priority));
        if (text == null) {
            return false;
        }
        if (mTtsPriority != null && !mTtsPriority.interruptPriority(priority)) {
            return false;
        }
        if (!ConfigManager.ApkConfig.isCarPlatform() && ConfigManager.ApkConfig.isDebug()) {
            //方便开发测试，当语音为PAD版本及Debug包时 直接回调播报完成，不影响后继逻辑
            listener.onCompleted();
            return true;
        }
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                String id = text + System.currentTimeMillis();
                synthesizerListenerMap.put(id, listener);
                mTtsSession.setParam(ESpeaker.ISS_TTS_PARAM_SPEAKER, voiceRole);
                isSuccess = mTtsSession.startSpeak(VOICE_SPEED_VALUE + text, id) == ISSErrors.ISS_SUCCESS;
                if (!isSuccess) {
                    mTtsPriority.recovery();
                }
            }
        });
        return isSuccess;
    }

    @Override
    public boolean startSpeaking(final String text, final int speed, TtsPriority priority, final OnTtsListener listener) {
        Log.d("XmTtsManager", String.format("startSpeaking: %s  priority:%s", text, priority));
        if (text == null) {
            return false;
        }
        if (mTtsPriority != null && !mTtsPriority.interruptPriority(priority)) {
            return false;
        }

        if (!ConfigManager.ApkConfig.isCarPlatform() && ConfigManager.ApkConfig.isDebug()) {
            //方便开发测试，当语音为PAD版本及Debug包时 直接回调播报完成，不影响后继逻辑
            listener.onCompleted();
            return true;
        }

        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                String id = text + System.currentTimeMillis();
                synthesizerListenerMap.put(id, listener);
                mTtsSession.setParam(ESpeaker.ISS_TTS_PARAM_SPEAKER, voiceRole);
                isSuccess = mTtsSession.startSpeak(mContext.getString(R.string.voice_speed, speed, text), id) == ISSErrors.ISS_SUCCESS;
                if (!isSuccess) {
                    mTtsPriority.recovery();
                }

            }
        });
        return isSuccess;
    }

    @Override
    public void stopSpeaking() {
        mTtsSession.stopSpeak();
        mTtsPriority.recovery();
    }

    @Override
    public boolean isTtsSpeaking() {
        return isTtsSpeaking;
    }

    @Override
    public void destroy() {

    }

    private void setParam() {
        getVoiceRole();
        if (mTtsSession == null) {
            KLog.d("setParam speechSynthesizer is empty");
            return;
        }
        mTtsSession.setParam(ESpeaker.ISS_TTS_PARAM_SPEAKER, voiceRole);
        mTtsSession.setParam(ESpeaker.ISS_TTS_PARAM_VOICE_SPEED, ESpeaker.ISS_TTS_SPEED_NORMAL_DEFAULT);
        mTtsSession.setParam(ESpeaker.ISS_TTS_PARAM_VOLUME, ESpeaker.ISS_TTS_VOLUME_MAX_DEFAULT);
        //mTtsSession.setParam(ESpeaker.ISS_TTS_PARAM_VOICE_PITCH, ESpeaker.ISS_TTS_PITCH_MAX);
    }

}
