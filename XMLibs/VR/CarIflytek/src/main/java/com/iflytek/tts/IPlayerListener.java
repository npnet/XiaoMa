package com.iflytek.tts;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/12/27
 * Desc：
 */
public interface IPlayerListener {
    void onPlayBegin();

    void onProgress(int var1, int var2);

    void onPlayInterrupted();

    void onFocusGain();

    void onPlayCompleted();

    void onError(int var1);
}
