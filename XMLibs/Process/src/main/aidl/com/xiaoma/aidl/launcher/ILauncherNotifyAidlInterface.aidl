package com.xiaoma.aidl.launcher;

interface ILauncherNotifyAidlInterface {

    boolean notifyVoiceCmd(String cmdId);

    boolean notifyVoiceCmdWithValue(String cmdId, String content);

    void notifyVoiceViewShowing(boolean isShowing);

}
