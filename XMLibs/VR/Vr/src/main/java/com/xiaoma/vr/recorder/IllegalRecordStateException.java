package com.xiaoma.vr.recorder;

/**
 * Created by LKF
 * 2018/1/4 0004.
 * 录音状态异常
 */

public class IllegalRecordStateException extends Exception {
    private int recordingState;

    public IllegalRecordStateException(int recordingState) {
        this.recordingState = recordingState;
    }

    @Override
    public String getMessage() {
        return "record state exception, state code : " + recordingState;
    }

    public int getRecordingState() {
        return recordingState;
    }
}
