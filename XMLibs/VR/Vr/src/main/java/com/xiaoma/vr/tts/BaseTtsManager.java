package com.xiaoma.vr.tts;

import android.content.Context;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/6/11
 */

public abstract class BaseTtsManager {
    protected Context mContext;

    abstract public void init(Context context);

    abstract public boolean startSpeaking(String text);

    abstract public boolean startSpeaking(String text, OnTtsListener listener);

    abstract public boolean startSpeaking(String text, int speed, OnTtsListener listener);

    abstract public boolean startSpeaking(String text, TtsPriority priority);

    abstract public boolean startSpeaking(String text, TtsPriority priority, OnTtsListener listener);

    abstract public boolean startSpeaking(String text, int speed, TtsPriority priority, OnTtsListener listener);

    abstract public void stopSpeaking();

    abstract public boolean isTtsSpeaking();

    abstract public void removeListeners();

    abstract public void auditionVoiceType(String voiceParam, String speakContent, OnTtsListener listener);

    abstract public String setVoiceType(int voiceType);

    abstract public boolean setVoiceType(String voiceParam, String voiceName);

    abstract public void destroy();

}
