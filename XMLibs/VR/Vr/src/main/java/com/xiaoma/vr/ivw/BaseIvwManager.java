package com.xiaoma.vr.ivw;

import android.content.Context;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/8/6
 */

public abstract class BaseIvwManager {
    protected Context context;
    protected OnWakeUpListener onWakeUpListener;

    public abstract void init(Context context);

    public abstract void init(Context context, OnWakeUpListener onWakeUpListener);

    public abstract void setOnWakeUpListener(OnWakeUpListener listener);

    public abstract void release();

    public abstract void startWakeup();

    public abstract void stopWakeup();

    public abstract void startRecorder();

    public abstract void stopRecorder();

    //设置唤醒的阈值
    public abstract void setIvwThreshold(int curThresh);

    public abstract void setWakeupInterception(boolean interception);
}
