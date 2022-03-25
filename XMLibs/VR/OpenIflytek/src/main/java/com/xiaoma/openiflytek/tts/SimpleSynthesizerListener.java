package com.xiaoma.openiflytek.tts;

import android.os.Bundle;

import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/6/11
 */

public class SimpleSynthesizerListener implements SynthesizerListener {

    @Override
    public void onSpeakBegin() {
        KLog.i("#onSpeakBegin");
    }

    @Override
    public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
        KLog.i(StringUtil.format("#onBufferProgress -> percent:%d, beginPos:%d, endPos:%d, info:%s",
                percent, beginPos, endPos, info));
    }

    @Override
    public void onSpeakPaused() {
        KLog.i("#onSpeakPaused");
    }

    @Override
    public void onSpeakResumed() {
        KLog.i("#onSpeakResumed");
    }

    @Override
    public void onSpeakProgress(int percent, int beginPos, int endPos) {
//        KLog.i(StringUtil.format("#onSpeakProgress -> percent:%d, beginPos:%d, endPos:%d",
//                percent, beginPos, endPos));
    }

    @Override
    public void onCompleted(SpeechError speechError) {
        KLog.i("#onCompleted -> speechError:%s", String.valueOf(speechError));
    }

    @Override
    public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        KLog.i(StringUtil.format("#onEvent -> eventType:%d, arg1:%d, arg2:%d, obj:%s",
                eventType, arg1, arg2, String.valueOf(obj)));
    }
}
