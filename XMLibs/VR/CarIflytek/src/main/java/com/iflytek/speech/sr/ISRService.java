package com.iflytek.speech.sr;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/12/27
 * Desc：
 */
public interface ISRService {
    int setMachineCode(String var1);

    int setSerialNumber(String var1);

    int getActiveKey(String var1);

    String create(String var1, ISRListener var2);

    String createEx(int var1, String var2, ISRListener var3);

    int sessionStart(String var1, String var2, int var3, String var4);

    int uploadDict(String var1, String var2, int var3);

    int uploadData(String sid, String szData, int iUpLoadMode);

    int setParam(String var1, String var2, String var3);

    int appendAudioData(String var1, byte[] var2, int var3);

    int endAudioData(String var1);

    int sessionStop(String var1);

    int destroy(String var1);

    int setMvwKeyWords(String var1, int var2, String var3);

    String mspSearch(String var1, String var2, String var3);

    String localNli(String var1, String var2, String var3);
}
