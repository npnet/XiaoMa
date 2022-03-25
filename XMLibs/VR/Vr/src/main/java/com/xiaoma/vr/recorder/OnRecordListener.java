package com.xiaoma.vr.recorder;

/**
 * Created by LKF
 * 2017/9/19 0019.
 */

public interface OnRecordListener {
    void onStart(BaseRecorder recorder);

    void onBuffer(BaseRecorder recorder, byte[] buffer, int start, int byteCount);

    void onBuffer(BaseRecorder recorder, byte[] buffer, byte[] leftBuffer, byte[] rightBuffer, int start, int byteCount);

    void onStop(BaseRecorder recorder);

    void onRecordFailed(BaseRecorder recorder, Exception e);
}
