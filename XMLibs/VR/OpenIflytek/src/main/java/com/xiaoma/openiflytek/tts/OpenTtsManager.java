package com.xiaoma.openiflytek.tts;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.xiaoma.openiflytek.OpenVoiceManager;
import com.xiaoma.openiflytek.R;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.media.IMediaPlayer;
import com.xiaoma.utils.media.OnCompletionListener;
import com.xiaoma.utils.media.XMMediaPlayer;
import com.xiaoma.vr.VoiceTypeManager;
import com.xiaoma.vr.VrConfig;
import com.xiaoma.vr.tts.BaseTtsManager;
import com.xiaoma.vr.tts.OnTtsListener;
import com.xiaoma.vr.tts.TtsPriority;

import java.io.FileInputStream;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/6/11
 * 开发版讯飞TTS语音播报管理
 */

public class OpenTtsManager extends BaseTtsManager {
    private static final int MAX_CACHE_CONTENT_LEN = 20;
    private static final String TAG = "OpenTtsManager";
    // 默认发音人
    private String voiceRole = "xiaoqi";
    private String speakingText;
    private StringBuilder mStringBuilder;
    private boolean mStopCalled = false;
    private SpeechSynthesizer speechSynthesizer;
    private WeakHashMap<String, OnTtsListener> synthesizerListenerMap = new WeakHashMap<>();
    private XMMediaPlayer mLastMediaPlayer;
    private OpenTtsCacheHandler mTtsCache;

    private XMMediaPlayer createPlayer() {
        XMMediaPlayer player = new XMMediaPlayer();
        player.setAudioStreamType(VrConfig.TTS_STREAM_TYPE);
        return player;
    }

    private void setParam() {
        getVoiceRole();
        // 清空参数
        if (speechSynthesizer == null) {
            KLog.d("setParam speechSynthesizer is empty");
            return;
        }
        speechSynthesizer.setParameter(SpeechConstant.PARAMS, null);
        speechSynthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        //设置在线合成发音人
        speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, voiceRole);
        //设置合成语速
        speechSynthesizer.setParameter(SpeechConstant.SPEED, "60");
        //设置合成音调
        speechSynthesizer.setParameter(SpeechConstant.PITCH, "50");
        //设置合成音量
        speechSynthesizer.setParameter(SpeechConstant.VOLUME, "80");
        //设置播放器音频流类型
        speechSynthesizer.setParameter(SpeechConstant.STREAM_TYPE, VrConfig.TTS_STREAM_TYPE + "");
        //设置播放合成音频打断音乐播放，默认为true
        speechSynthesizer.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
        //设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        //注：AUDIO_FORMAT参数语记需要更新版本才能生效
        speechSynthesizer.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        speechSynthesizer.setParameter(SpeechConstant.TTS_AUDIO_PATH,
                Environment.getExternalStorageDirectory() + "/msc/tts.wav");
    }

    /**
     * 合成回调监听。
     */
    private SynthesizerListener synthesizerListener = new SimpleSynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            super.onSpeakBegin();
            if (isMapEmpty(synthesizerListenerMap)) {
                return;
            }
            Map<String, OnTtsListener> callbacks = new ArrayMap<>();
            callbacks.putAll(synthesizerListenerMap);
            for (Map.Entry<String, OnTtsListener> entry : callbacks.entrySet()) {
                try {
                    entry.getValue().onBegin();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (isMapEmpty(synthesizerListenerMap)) {
                return;
            }
            Map<String, OnTtsListener> listenerMap = new ArrayMap<>();
            listenerMap.putAll(synthesizerListenerMap);
            for (Map.Entry<String, OnTtsListener> entry : listenerMap.entrySet()) {
                try {
                    entry.getValue().onCompleted();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                synthesizerListenerMap.remove(speakingText);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    public void init(Context context) {
        if (speechSynthesizer != null) {
            return;
        }
        mContext = context.getApplicationContext();
        OpenVoiceManager.getInstance().initXf(mContext);
        speechSynthesizer = SpeechSynthesizer.createSynthesizer(context.getApplicationContext(), new InitListener() {
            @Override
            public void onInit(int code) {
                Log.d(TAG, "onInitCode===>" + code);
                if (code != ErrorCode.SUCCESS) {
                    Log.d(TAG, "init error：" + code);
                } else {
                    setParam();
                }
            }
        });
        KLog.d("Init Xf tts " + speechSynthesizer);
        mTtsCache = new OpenTtsCacheHandler(speechSynthesizer);
    }

    @Override
    public boolean startSpeaking(String text) {
        return startSpeaking(text, new OnTtsListener() {
            @Override
            public void onCompleted() {

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
    public boolean startSpeaking(String text, OnTtsListener listener) {
        return startSpeaking(text, System.currentTimeMillis(), listener);
    }

    @Override
    public boolean startSpeaking(String text, int speed, OnTtsListener listener) {
        return startSpeaking(text, System.currentTimeMillis(), listener);
    }

    @Override
    public boolean startSpeaking(String text, TtsPriority priority) {
        return false;
    }

    @Override
    public boolean startSpeaking(String text, TtsPriority priority, OnTtsListener listener) {
        return false;
    }

    @Override
    public boolean startSpeaking(String text, int speed, TtsPriority priority, OnTtsListener listener) {
        return false;
    }


    private boolean startSpeaking(final String text, final long time, final OnTtsListener listener) {
        KLog.d("start speaking " + text);
        mStopCalled = false;
        if (speechSynthesizer == null)
            return false;
        if (mStringBuilder == null) {
            mStringBuilder = new StringBuilder();
        }
        mStringBuilder.delete(0, mStringBuilder.length());
        mStringBuilder.append(TextUtils.isEmpty(text) ? "" : text).append(time);
        speakingText = mStringBuilder.toString();
        if (listener != null)
            synthesizerListenerMap.put(speakingText, listener);
        //长度超过MAX_CACHE_CONTENT_LEN的语音不进行缓存, 缓存对象为null时直接在线合成
        if (text.length() > MAX_CACHE_CONTENT_LEN || mTtsCache == null) {
            KLog.i(StringUtil.format("speak online, text:%s", text));
            return speechSynthesizer.startSpeaking(text, synthesizerListener) == ErrorCode.SUCCESS;
        }
        boolean rlt = true;
        //先从缓存中读取
        FileInputStream cacheInput = mTtsCache.get(text, voiceRole);
        if (cacheInput != null) {
            releaseLastMediaPlayer();
            XMMediaPlayer player = createPlayer();
            try {
                player.setOnCompletionListener(new OnCompletionListener() {
                    @Override
                    public void onCompletion(IMediaPlayer mp) {
                        KLog.d(StringUtil.format("onCompletion, text:%s", text));
                        synthesizerListener.onCompleted(null);
                    }
                });
                player.setDataSource(cacheInput.getFD());
                player.prepare();
                player.start();
                synthesizerListener.onSpeakBegin();
                KLog.i(StringUtil.format("speak from cache, text:%s", text));
            } catch (Exception e) {
                e.printStackTrace();
                //本地合成错误,尝试用在线合成
                rlt = speechSynthesizer.startSpeaking(text, synthesizerListener) == ErrorCode.SUCCESS;
            }
            mLastMediaPlayer = player;
        } else {
            rlt = mTtsCache.put(text, voiceRole, new SimpleSynthesizerListener() {
                @Override
                public void onCompleted(SpeechError speechError) {
                    //因为语音合成有延迟,假如在这之前执行了stop,则这个时候不再进行播放
                    if (mStopCalled) {
                        KLog.i(StringUtil.format("synthesizeToUri complete, but #stop has called"));
                        return;
                    }
                    if (speechError != null) {
                        //本地合成错误,尝试用在线合成
                        speechSynthesizer.startSpeaking(text, synthesizerListener);
                    } else {
                        startSpeaking(text, time, listener);
                    }
                }
            }) == ErrorCode.SUCCESS;
            KLog.i(StringUtil.format("speak cache not exist, synthesizeToUri. text:%s", text));
        }
        return rlt;
    }

    public void stopSpeaking() {
        releaseLastMediaPlayer();
        if (speechSynthesizer != null) {
            speechSynthesizer.stopSpeaking();
        }
        mStopCalled = true;
    }

    @Override
    public boolean isTtsSpeaking() {
        return false;
    }

    public void removeListeners() {
        if (synthesizerListenerMap != null) {
            synthesizerListenerMap.clear();
        }
    }

    private void releaseLastMediaPlayer() {
        if (mLastMediaPlayer != null) {
            try {
                mLastMediaPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getVoiceRole() {
        voiceRole = VoiceTypeManager.getVoice(mContext);
    }


    @Override
    public void auditionVoiceType(String voiceParam, String speakContent, OnTtsListener listener) {
        if (speechSynthesizer == null || TextUtils.isEmpty(voiceParam)) return;
        stopSpeaking();
        speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, voiceParam);
        if (TextUtils.isEmpty(speakContent))
            speakContent = mContext.getString(R.string.audition_voice_content);
        speechSynthesizer.startSpeaking(speakContent, synthesizerListener);
        speakingText = speakContent + System.currentTimeMillis();
        synthesizerListenerMap.put(speakingText, new OnTtsListener() {
            @Override
            public void onCompleted() {
                speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, voiceRole);
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
    public String setVoiceType(int type) {
        KLog.i("setVoiceType type=" + type);
        if (speechSynthesizer == null) {
            return "";
        }
        if (mStringBuilder == null) {
            mStringBuilder = new StringBuilder();
        }
        mStringBuilder.delete(0, mStringBuilder.length());
        mStringBuilder.append(mContext.getString(R.string.switch_voice_hint));
        String voice = VoiceTypeManager.setVoiceType(mContext, type);
        speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, voice);
        String voiceName = VoiceTypeManager.voiceToName(mContext, voice);
        mStringBuilder.append(voiceName);
        this.voiceRole = voice;
        return mStringBuilder.toString();
    }


    @Override
    public boolean setVoiceType(String voiceParam, String voiceName) {
        if (speechSynthesizer == null || TextUtils.isEmpty(voiceParam))
            return false;
        speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, voiceParam);
        VoiceTypeManager.setVoiceType(mContext, voiceParam, voiceName);
        this.voiceRole = voiceParam;
        return true;
    }


    @Override
    public void destroy() {

    }

    private boolean isMapEmpty(Map map) {
        return map == null || map.size() <= 0;
    }
}
