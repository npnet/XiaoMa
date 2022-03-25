package com.xiaoma.vr.ivw;

import android.content.Context;

import com.xiaoma.vr.iat.IAssistantView;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/9/3
 * Desc:唤醒相关的基础接口
 */

public interface IIvwManager {
    void init(Context context);

    void setOnWakeUpListener(OnWakeUpListener listener);

    void addAssistantView(IAssistantView view);

    void release();

    void startWakeup();

    void stopWakeup();

    void startRecorder();

    void stopRecorder();

    //设置唤醒的阈值
    void setIvwThreshold(int curThresh);

    //停止唤醒
    void setWakeupInterception(boolean interception);
}
