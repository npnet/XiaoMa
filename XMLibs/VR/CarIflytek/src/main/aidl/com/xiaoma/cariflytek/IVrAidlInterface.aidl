package com.xiaoma.cariflytek;

import com.xiaoma.cariflytek.IVrNotifyCallBack;

interface IVrAidlInterface {

    //下面的内容是识别相关
    oneway void registerCallBack(IVrNotifyCallBack callBack);

    oneway void unregisterCallBack(IVrNotifyCallBack callBack);

    oneway void initIat();

    oneway void reInitIat();

    boolean startListening();

    oneway void startListeningWithTime(int timeOut);

    oneway void startListeningRecord();

    oneway void startListeningForChoose(String srSceneStksCmd);

    oneway void cancelListening();

    oneway void stopListening();

    oneway void abandonSrResult();

    void appendIatAudioData(in Bundle buffer);

    boolean isInitSuccess();

    boolean getInitIatState();

    oneway void uploadContacts(String contactType);

    oneway void uploadAppState(boolean isForeground, String appType);

    oneway void uploadPlayState(boolean isPlaying, String appType);

    oneway void uploadNaviState(String naviState);

    //下面是内容是唤醒相关
    oneway void initIvw();

    oneway void startWakeUp();

    oneway void stopWakeUp();

    oneway void stopIvwRecorder();

    void appendIvwAudioData(in Bundle buffer);

    boolean setWakeUpWord(String word);

    List<String> getWakeUpWords();

    boolean registerOneShotWakeupWord(in List<String> wakeupWord);

    boolean unregisterOneShotWakeupWord(in List<String> wakeupWord);

    //通用
    boolean destroy();

    boolean upIvw(boolean isOpenSeopt);

    void setLocationInfo(double lat, double lon);

    void setUid(String uid);
}
