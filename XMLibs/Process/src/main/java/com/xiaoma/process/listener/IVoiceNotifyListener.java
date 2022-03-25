package com.xiaoma.process.listener;

public interface IVoiceNotifyListener {

    boolean notifyVoiceCmd(String cmdId);

    boolean notifyVoiceCmdWithValue(String cmdId, String content);

    void notifyVoiceViewShowing(boolean isShowing);

}
