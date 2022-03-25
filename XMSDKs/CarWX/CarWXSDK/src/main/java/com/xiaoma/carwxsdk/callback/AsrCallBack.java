package com.xiaoma.carwxsdk.callback;

public interface AsrCallBack extends BaseCallBack{
    void onVolumeChanged(int volume);

    void onSRstatus(int i);

    void onError(int errorCode);

    void showSrText(String voiceFilePath, String recordTxt, boolean isResolving);
}
