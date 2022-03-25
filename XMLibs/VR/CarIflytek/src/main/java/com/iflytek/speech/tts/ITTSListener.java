package com.iflytek.speech.tts;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/12/27
 * Desc：
 */
public interface ITTSListener {
    void onTTSPlayBegin(String ttsID);

    void onTTSPlayCompleted(String ttsID);

    void onTTSPlayInterrupted(String ttsID);

    void onTTSProgressReturn(int var1, int var2);
}
