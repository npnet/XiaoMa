package com.xiaoma.vr.tts;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/6/11
 */
public interface OnTtsListener {
    void onCompleted();

    void onBegin();

    void onError(@ErrorCode int code);

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ErrorCode.SPEAK_ERROR, ErrorCode.SPEAK_INTERRUPT,ErrorCode.SPEAK_FOCUS})
    @interface ErrorCode {
        int SPEAK_ERROR = -1;//
        int SPEAK_INTERRUPT = -2;//打断
        int SPEAK_FOCUS = -3;//焦点抢占失败
    }
}
