package com.iflytek.speech.tts;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/12/27
 * Desc：
 */
public interface TtsPlayerInst {
    int sessionBegin(int var1);

    int setParam(int var1, int var2);

    int setParamEx(int var1, String var2);

    int startSpeak(String var1, ITTSListener var2, String id);

    int pauseSpeak();

    int resumeSpeak();

    int stopSpeak();

    int sessionStop();

    int sessionInitState();
}
