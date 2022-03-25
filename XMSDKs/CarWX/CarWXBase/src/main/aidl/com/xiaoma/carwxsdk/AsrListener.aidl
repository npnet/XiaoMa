// AsrListener.aidl
package com.xiaoma.carwxsdk;

// Declare any non-default types here with import statements

interface AsrListener {

    void onVolumeChanged(int volume);

    void onSRstatus(int i);

    void onError(int errorCode);

    void showSrText(String voiceFilePath, String recordTxt, boolean arg1);
}
