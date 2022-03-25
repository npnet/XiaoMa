package com.xiaoma.vr.ivw;

import android.content.Context;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: blusehuang
 * Date: 2016-7-21
 * Desc：base wake up
 */
public abstract class BaseWakeup {

    protected Context context;
    protected OnWakeUpListener onWakeUpListener;

    abstract public void init(Context context);

    abstract public void setOnWakeUpListener(OnWakeUpListener onWakeUpListener);

    abstract public void startWakeup();

    abstract public void stopWakeup();

    abstract public void appendAudioData(byte[] buffer, int start, int byteCount);

    abstract public void appendAudioData(byte[] buffer, byte[] bufferLeft, byte[] bufferRight);

    abstract public void setIvwThreshold(int curThresh);

    public abstract boolean setWakeupWord(String word);

    public abstract boolean resetWakeupWord();

    public abstract List<String> getWakeupWord();

    public abstract boolean registerOneShotWakeupWord(List<String> wakeupWord);

    public abstract boolean unregisterOneShotWakeupWord(List<String> wakeupWord);

    public abstract void destroy();

    public abstract boolean destroyIvw();
}
