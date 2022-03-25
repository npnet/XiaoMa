package com.xiaoma.cariflytek;
import com.xiaoma.cariflytek.WakeUpInfo;

interface IVrNotifyCallBack {

    //下面是识别相关回调
    oneway void initIatSuccess();

    oneway void onIatVolumeChanged(int volume);

    oneway void onRecordState(boolean isComplete);

    oneway void onWavFileComplete();

    oneway void onIatResult(String content);

    oneway void onIatComplete(String voiceText,String parseText);

    oneway void onNoSpeaking();

    oneway void onIatError(int errorCode);

    oneway void onListeningForChoose();

    String getAllContacts();

    oneway void onHardwareIatTypeChange(int value);

    oneway void onOneShotStateChange(boolean isOneShot);

    oneway void onShortTimeSrChange(boolean isShortSR);

    //下面是唤醒相关回调

    oneway void onWakeUp(in WakeUpInfo info);

    oneway void onWakeUpCmd(String cmdText);

    oneway void startWakeup();

    oneway void startVwRecord();

    oneway void stopThread();

    oneway void onUpIvw();

    boolean isVrShowing();

}
