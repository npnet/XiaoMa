package com.xiaoma.vr.iat;

import android.content.Context;

import com.xiaoma.vr.model.AppType;
import com.xiaoma.vr.model.NaviState;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/8/31
 * Desc:识别相关基础接口接口
 */

public interface IIatManager {

    void init(Context context);

    void setOnIatListener(OnIatListener onIatListener);

    void stopListening();

    void startListeningNormal();

    /**
     * 开放平台固定时长录音，如果超时后会继续重新开启，直到调用stopListener
     * 需要主动结束，可用于固定时长录音
     */
    void startListeningRecord();


    /**
     * 前后端点超时录音，时间不固定，超时后直接回调结束
     * 不需要主动结束，达到结束条件即自动结束
     *
     * @param startTimeOut 讯飞支持1000-10000
     * @param endTimeOut
     */
    void startListeningRecord(int startTimeOut, int endTimeOut);

    void startListeningForChoose();

    void startListeningForChoose(String srSceneStkCmd);

    void upLoadContact(boolean isPhoneContact, String contacts);

    void uploadAppState(boolean isForeground, @AppType String appType);

    void uploadPlayState(boolean isPlaying, @AppType String appType);

    void uploadNaviState(@NaviState String naviState);

    void cancelListening();

    void release();

    void destroy();

    boolean getInitState();
}
