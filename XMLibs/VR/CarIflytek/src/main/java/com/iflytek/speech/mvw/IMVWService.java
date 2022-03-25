package com.iflytek.speech.mvw;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/12/27
 * Desc：
 */
public interface IMVWService {
    int initMvw(String var1, IMVWListener var2);

    int startMvw(int var1);

    int addStartMvwScene(int var1);

    int appendAudioData(byte[] var1, int var2);

    int setThreshold(int var1, int var2, int var3);

    int setParam(String var1, String var2);

    int stopMvw();

    int stopMvwScene(int var1);

    int releaseMvw();

    int setMvwKeyWords(int var1, String var2);

    int setMvwDefaultKeyWords(int var1);
}
