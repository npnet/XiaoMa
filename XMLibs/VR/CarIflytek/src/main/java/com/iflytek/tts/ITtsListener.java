package com.iflytek.tts;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/12/27
 * Desc：
 */
public interface ITtsListener {
    void onPlayBegin(String ttsID);

    void onPlayCompleted(String ttsID);

    void onPlayInterrupted(String ttsID);

    void onProgressReturn(int var1, int var2);
}
