package com.xiaoma.vr.tts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.text.TextUtils;

import com.xiaoma.utils.constant.VrConstants;
import com.xiaoma.utils.log.KLog;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * User:Created by Terence
 * IDE : Android Studio
 * Date:2018/8/21
 * Desc:对外提供TTS播报的能力
 */

public class EventTtsManager extends BaseTtsManager {

    private static EventTtsManager instance = null;
    private boolean isRegistered;
    private EventTtsReceiver mReceiver;
    private Map<String, OnTtsListener> ttsMap = new HashMap<>();
    //    private AudioManager audioManager;
    private boolean keepAudioFocus;
    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    KLog.d("EventTtsManager onAudioFocusChange, " + "focusChange: " + focusChange);
                }
            };
    private boolean isSpeaking = false;

    public static EventTtsManager getInstance() {
        if (null == instance) {
            instance = new EventTtsManager();
        }

        return instance;
    }


    @Override
    public void init(Context context) {
        mContext = context.getApplicationContext();
        if (isRegistered) {
            return;
        }
        mReceiver = new EventTtsReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(VrConstants.Actions.TTS_SPEAKING_BEGIN);
        intentFilter.addAction(VrConstants.Actions.TTS_SPEAKING_COMPLETED);
        intentFilter.addAction(VrConstants.Actions.TTS_SPEAKING_ERROR);
        intentFilter.addAction(VrConstants.Actions.TTS_SPEAKING_STATUS);
        mContext.registerReceiver(mReceiver, intentFilter);
//        audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        isRegistered = true;
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

    @Override
    public boolean startSpeaking(String text) {
        return !TextUtils.isEmpty(text) && startSpeaking(text, new OnTtsListener() {
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
        if (TextUtils.isEmpty(text)) return false;
//        audioManager.requestAudioFocus(onAudioFocusChangeListener, VrConfig.TTS_STREAM_TYPE, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        String id = text + System.currentTimeMillis();
        ttsMap.put(id, listener);
        if (mContext != null) {
            Intent intent = new Intent(VrConstants.Actions.START_SPEAKING);
            intent.putExtra(VrConstants.ActionExtras.TTS_SPEAKING_CONTENT, text);
            intent.putExtra(VrConstants.ActionExtras.TTS_ID, id);
            intent.putExtra(VrConstants.ActionExtras.PACKAGE_NAME, mContext.getPackageName());
            mContext.sendBroadcast(intent);
            return true;
        }
        return false;
    }

    @Override
    public boolean startSpeaking(String text, int speed, OnTtsListener listener) {
        if (TextUtils.isEmpty(text)) return false;
//        audioManager.requestAudioFocus(onAudioFocusChangeListener, VrConfig.TTS_STREAM_TYPE, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        String id = text + System.currentTimeMillis();
        ttsMap.put(id, listener);
        if (mContext != null) {
            Intent intent = new Intent(VrConstants.Actions.START_SPEAKING);
            intent.putExtra(VrConstants.ActionExtras.TTS_SPEAKING_CONTENT, text);
            intent.putExtra(VrConstants.ActionExtras.TTS_SPEAKING_SPEED, speed);
            intent.putExtra(VrConstants.ActionExtras.TTS_ID, id);
            intent.putExtra(VrConstants.ActionExtras.PACKAGE_NAME, mContext.getPackageName());
            mContext.sendBroadcast(intent);
            return true;
        }
        return false;
    }

    /**
     * 提供给第三方非小马应用使用
     *
     * @param text
     * @return
     */
    public boolean startSpeakingByThird(String text) {
        return !TextUtils.isEmpty(text) && startSpeakingByThird(text, new OnTtsListener() {
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

    /**
     * TTS播报状态
     *
     * @return
     */
    public boolean isSpeaking() {
        return isSpeaking;
    }

    /**
     * 提供给第三方非小马应用使用
     *
     * @param text
     * @param listener
     * @return
     */
    public boolean startSpeakingByThird(String text, OnTtsListener listener) {
        if (TextUtils.isEmpty(text)) return false;
        String id = text + System.currentTimeMillis();
        ttsMap.put(id, listener);
        if (mContext != null) {
            Intent intent = new Intent(VrConstants.Actions.START_SPEAKING_BY_THIRD);
            intent.putExtra(VrConstants.ActionExtras.TTS_SPEAKING_CONTENT, text);
            intent.putExtra(VrConstants.ActionExtras.TTS_ID, id);
            intent.putExtra(VrConstants.ActionExtras.PACKAGE_NAME, mContext.getPackageName());
            mContext.sendBroadcast(intent);
            return true;
        }
        return false;
    }

    @Override
    public void stopSpeaking() {
        if (mContext != null) {
            Intent intent = new Intent(VrConstants.Actions.STOP_SPEAKING);
            mContext.sendBroadcast(intent);
//            if (!keepAudioFocus) audioManager.abandonAudioFocus(onAudioFocusChangeListener);
        }
    }

    @Override
    public boolean isTtsSpeaking() {
        return false;
    }

    @Override
    public void removeListeners() {
        if (mContext != null) {
            Intent intent = new Intent(VrConstants.Actions.REMOVE_TTS_SPEAKING_LISTENER);
            mContext.sendBroadcast(intent);
        }
    }

    @Override
    public void auditionVoiceType(String voiceParam, String speakContent, OnTtsListener listener) {
        if (TextUtils.isEmpty(voiceParam) || TextUtils.isEmpty(speakContent)) return;
        if (mContext != null) {
            String id = speakContent + System.currentTimeMillis();
            ttsMap.put(id, listener);
            Intent intent = new Intent(VrConstants.Actions.AUDITION_VOICE_TYPE);
            intent.putExtra(VrConstants.ActionExtras.TTS_ID, id);
            intent.putExtra(VrConstants.ActionExtras.VOICE_PARAM, voiceParam);
            intent.putExtra(VrConstants.ActionExtras.SPEAK_CONTENT, speakContent);
            intent.putExtra(VrConstants.ActionExtras.PACKAGE_NAME, mContext.getPackageName());
            mContext.sendBroadcast(intent);
        }
    }

    @Override
    public String setVoiceType(int voiceType) {
        return null;
    }

    @Override
    public boolean setVoiceType(String voiceParam, String voiceName) {
        if (TextUtils.isEmpty(voiceName) || TextUtils.isEmpty(voiceParam)) return false;
        if (mContext != null) {
            Intent intent = new Intent(VrConstants.Actions.SET_VOICE_TYPE);
            intent.putExtra(VrConstants.ActionExtras.VOICE_PARAM, voiceParam);
            intent.putExtra(VrConstants.ActionExtras.VOICE_NAME, voiceName);
            mContext.sendBroadcast(intent);
            return true;
        }
        return false;
    }

    @Override
    public void destroy() {
        try {
            ttsMap = null;
            if (isRegistered && mReceiver != null) {
                mContext.unregisterReceiver(mReceiver);
                isRegistered = false;
            }
        } catch (Exception e) {
            KLog.e(e.toString());
        }
    }


    private class EventTtsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action) || ttsMap.isEmpty()) {
                return;
            }
            String id = intent.getStringExtra(VrConstants.ActionExtras.TTS_ID);
            if (action.equals(VrConstants.Actions.TTS_SPEAKING_BEGIN)) {
                if (intent.getStringExtra(VrConstants.ActionExtras.PACKAGE_NAME).equals(context.getPackageName())) {
                    Iterator iter = ttsMap.keySet().iterator();
                    while (iter.hasNext()) {
                        String ttsId = (String) iter.next();
                        if (ttsId.equals(id)) {
                            OnTtsListener listener = ttsMap.get(ttsId);
                            listener.onBegin();
                            break;
                        }
                    }
                }
            } else if (action.equals(VrConstants.Actions.TTS_SPEAKING_COMPLETED)) {
                if (intent.getStringExtra(VrConstants.ActionExtras.PACKAGE_NAME).equals(context.getPackageName())) {
                    Iterator iter = ttsMap.keySet().iterator();
                    while (iter.hasNext()) {
                        String ttsId = (String) iter.next();
                        if (ttsId.equals(id)) {
                            OnTtsListener listener = ttsMap.get(ttsId);
                            listener.onCompleted();
                            ttsMap.remove(ttsId);
                            break;
                        }
                    }
                }
            } else if (action.equals(VrConstants.Actions.TTS_SPEAKING_ERROR)) {
                if (intent.getStringExtra(VrConstants.ActionExtras.PACKAGE_NAME).equals(context.getPackageName())) {
                    Iterator iter = ttsMap.keySet().iterator();
                    while (iter.hasNext()) {
                        String ttsId = (String) iter.next();
                        if (ttsId.equals(id)) {
                            OnTtsListener listener = ttsMap.get(ttsId);
                            listener.onError(intent.getIntExtra(VrConstants.ActionExtras.ERROR_CODE, -1));
                            ttsMap.remove(ttsId);
                            break;
                        }
                    }
                }
            } else if (action.equals(VrConstants.Actions.TTS_SPEAKING_STATUS)) {
                isSpeaking = intent.getBooleanExtra(VrConstants.ActionExtras.TTS_STATUS, false);
            }
        }
    }

    public void keepAudioFocus() {
        keepAudioFocus = true;
    }

//    public void abandonAudioFocus() {
//        keepAudioFocus = false;
//        audioManager.abandonAudioFocus(onAudioFocusChangeListener);
//    }

}
