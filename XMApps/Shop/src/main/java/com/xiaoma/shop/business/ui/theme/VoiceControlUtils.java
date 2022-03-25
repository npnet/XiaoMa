package com.xiaoma.shop.business.ui.theme;

import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.XmProperties;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.tts.EventTtsManager;
import com.xiaoma.vr.tts.OnTtsListener;

public final class VoiceControlUtils {

    private static final String TAG = VoiceControlUtils.class.getSimpleName();
    private static final String VOICE_SPEAKER_KEY = "speaker_role";

    private VoiceControlUtils() {
        throw new RuntimeException("Not create instance.");
    }


    public static void sendVoiceToneSetup(String voiceParam, String voiceName) {
        KLog.d(TAG, "send voice tone setup.");
        EventTtsManager.getInstance().setVoiceType(voiceParam, voiceName);
    }

    static OnTtsListener mOnTtsListener;

    public static void tryPlayVoiceTone(String voiceParam, String speakContent, final OnTtsListener onTtsListener) {
        KLog.d(TAG, "Try play voice tone.");
        EventTtsManager.getInstance().auditionVoiceType(voiceParam, speakContent, onTtsListener);
    }

    public static void stopSpeaking() {
        EventTtsManager.getInstance().stopSpeaking();
    }


    public static String getCurrentVoiceToneId() {
        return XmProperties.build().get(VOICE_SPEAKER_KEY, null);
    }


}