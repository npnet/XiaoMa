package com.xiaoma.aidl.wechat;

interface IWeChatNotifyAidlInterface {

    void notifyTTSEnd(String content);

    void notifyVolumChanged(int volume);

    void notifyVoiceRecordFinish(String filePath, String recordTxt);

    void notifyVoiceRecordError();

}
