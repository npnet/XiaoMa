package com.xiaoma.vr.recorder;

/**
 * Created by LKF
 * 2018/3/15 0015.
 * 录音器生命周期回调
 */

public interface RecorderLifecycle {
    void onStart();

    void onStop();

    void onBuffer(byte[] buffer, int start, int byteCount);

    void onBuffer(byte[] buffer, byte[] leftBuffer, byte[] rightBuffer);

    void onFailed(Exception e);
}
