package com.iflytek.sr;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/12/27
 * Desc：
 */
public interface IIsrListener {
    void onSrMsgProc(long uMsg, long wParam, String lParam);

    void onSrInited(boolean var1, int var2);
}
