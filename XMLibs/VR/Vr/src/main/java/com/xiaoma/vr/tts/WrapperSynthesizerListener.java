package com.xiaoma.vr.tts;


/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2016/8/3
 */
public class WrapperSynthesizerListener implements OnTtsListener {

    @Override
    public void onCompleted() {

    }

    @Override
    public void onBegin() {

    }

    @Override
    public void onError(int code) {
        //此Listener 不需要关注onError的情况，直接回调完成即可，否则影响播报后续的业务流程
        //如果需接收onError 则重写此方法 不调用super即可
        onCompleted();
    }
}
