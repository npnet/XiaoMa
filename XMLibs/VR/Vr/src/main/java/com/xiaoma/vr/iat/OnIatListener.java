package com.xiaoma.vr.iat;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/6/11
 */
public interface OnIatListener {
    void onComplete(String voiceText, String parseText);

    void onVolumeChanged(int volume);

    void onNoSpeaking();

    void onError(int errorCode);

    void onResult(String recognizerText, boolean isLast, String currentText);

    void onWavFileComplete();

    void onRecordComplete();
}
