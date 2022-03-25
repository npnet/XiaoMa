package com.xiaoma.vr.ivw;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/6/11
 */
public interface OnWakeUpListener {
    void onWakeUp(int index, String keyWord, boolean isMainDrive);

    void onWakeUpCmd(String cmdText);
}
