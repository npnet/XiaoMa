package com.iflytek.mvw;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/12/27
 * Desc：
 */
public interface IMvwListener {
    void onVwInited(boolean var1, int var2);

    void onVwWakeup(int var1, int var2, int var3, String var4);
}
