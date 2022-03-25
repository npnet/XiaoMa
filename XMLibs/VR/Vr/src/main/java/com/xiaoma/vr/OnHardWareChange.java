package com.xiaoma.vr;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/9/19
 * Desc:模式切换
 */
public interface OnHardWareChange {
    //降噪
    void onNoiseClean();

    //回声消除
    void onEchoCancel();
}
