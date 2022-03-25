package com.xiaoma.vr.recorder;

/**
 * Created by LKF
 * 2017/9/19 0019.
 */

public class SimpleOnRecordListener implements OnRecordListener {
    @Override
    public void onStart(BaseRecorder recorder) {

    }

    @Override
    public void onBuffer(BaseRecorder recorder, byte[] buffer, int start, int byteCount) {

    }

    @Override
    public void onBuffer(BaseRecorder recorder, byte[] buffer, byte[] leftBuffer, byte[] rightBuffer, int start, int byteCount) {

    }

    @Override
    public void onStop(BaseRecorder recorder) {

    }

    @Override
    public void onRecordFailed(BaseRecorder recorder, Exception e) {

    }
}
