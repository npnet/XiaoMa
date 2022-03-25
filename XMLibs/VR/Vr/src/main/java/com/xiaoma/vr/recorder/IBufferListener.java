package com.xiaoma.vr.recorder;

/**
 *User:Created by Terence
 *IDE :Android Studio
 *Date:2019/3/20
 *Desc:语音buffer传输
 */
public interface IBufferListener {

    void onBuffer(byte[] buffer);
}
