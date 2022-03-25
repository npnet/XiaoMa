// TtsListener.aidl
package com.xiaoma.carwxsdk;

// Declare any non-default types here with import statements

interface TtsListener {

    void onFinish(String id);

    void onStart(String id);

    void onError(String id, int code);

    void onProgress(String id, int voice, int process);
}
