package com.xiaoma.aidl.launcher;

import com.xiaoma.aidl.launcher.ILauncherNotifyAidlInterface;

interface ILauncherAidlInterface {

    boolean registerNotifyListener(ILauncherNotifyAidlInterface notifyAidlInterface);

    boolean unRegisterNotifyListener(ILauncherNotifyAidlInterface notifyAidlInterface);

     //注册关键词
    boolean registerVoiceCmd(String cmdContent, String cmdId);

     //注销关键词
    boolean unRegisterVoiceCmd(String cmdId);

    //唤起语音助手界面
    void showXiaoMaVoice();

    //关闭语音助手界面
    void closeXiaoMaVoice();

}
