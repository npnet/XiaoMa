package com.xiaoma.vr.recorder;

import android.os.Process;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by LKF
 * 2017/9/19 0019.
 * 录音器
 */

public class BaseRecorder implements RecorderLifecycle {
    private Set<OnRecordListener> mOnRecordListeners = Collections.synchronizedSet(new HashSet<OnRecordListener>());

    BaseRecorder() {
    }

    public boolean addOnRecordListener(OnRecordListener onRecordListener) {
        return onRecordListener != null
                && mOnRecordListeners.add(onRecordListener);
    }

    public boolean removeOnRecordListener(OnRecordListener onRecordListener) {
        return onRecordListener != null
                && mOnRecordListeners.remove(onRecordListener);
    }

    public int getRecordThreadPriority() {
        return Process.THREAD_PRIORITY_BACKGROUND;
    }

    @Override
    public void onStart() {
        dispatchOnStart();
    }

    @Override
    public void onStop() {
        dispatchOnStop();
    }

    @Override
    public void onBuffer(byte[] buffer, int start, int byteCount) {
        dispatchOnBuffer(buffer, start, byteCount);
    }

    @Override
    public void onBuffer(byte[] buffer, byte[] leftBuffer, byte[] rightBuffer) {
        dispatchOnBufferV2(buffer, leftBuffer, rightBuffer);
    }

    @Override
    public void onFailed(Exception e) {
        dispatchOnFailed(e);
    }

    private void dispatchOnStart() {
        for (OnRecordListener l : mOnRecordListeners) {
            l.onStart(this);
        }
    }

    private void dispatchOnBuffer(byte[] buffer, int start, int byteCount) {
        for (OnRecordListener l : mOnRecordListeners) {
            l.onBuffer(this, buffer, start, byteCount);
        }
    }

    private void dispatchOnBufferV2(byte[] buffer, byte[] leftBuffer, byte[] rightBuffer) {
        for (OnRecordListener l : mOnRecordListeners) {
            l.onBuffer(this, buffer, leftBuffer, rightBuffer, 0, 0);
        }
    }

    private void dispatchOnStop() {
        for (OnRecordListener l : mOnRecordListeners) {
            l.onStop(this);
        }
    }

    private void dispatchOnFailed(Exception e) {
        for (OnRecordListener l : mOnRecordListeners) {
            l.onRecordFailed(this, e);
        }
    }

}
