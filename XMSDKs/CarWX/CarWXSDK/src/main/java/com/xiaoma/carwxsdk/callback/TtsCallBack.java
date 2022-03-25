package com.xiaoma.carwxsdk.callback;

public interface TtsCallBack extends BaseCallBack{
    void onFinish(String id);

    void onStart(String id);

    void onError(String id, int code);

    void onProgress(String id, int voice, int process);
}
