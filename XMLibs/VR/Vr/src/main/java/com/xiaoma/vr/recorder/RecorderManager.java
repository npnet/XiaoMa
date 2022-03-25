package com.xiaoma.vr.recorder;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Process;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;

import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.VoiceConfigManager;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by LKF
 * 2017/9/19 0019.
 * 录音器管理器
 */

public class RecorderManager {
    private static final String TAG = RecorderManager.class.getSimpleName();
    private static RecorderManager sInstance;
    private AudioParam audioParam;

    public static RecorderManager getInstance() {
        if (sInstance == null) {
            synchronized (RecorderManager.class) {
                if (sInstance == null) {
                    sInstance = new RecorderManager();
                }
            }
        }
        return sInstance;
    }

    private ExecutorService mRecordExecutor = Executors.newSingleThreadExecutor();
    private ExecutorService mWriteExecutor = Executors.newSingleThreadExecutor();
    private AudioRecord mAudioRecord = null;
    private byte[] mBuffer;
    private boolean mRecordThreadStarted = false;
    private BaseRecorder mCurRecorder;
    private RecorderType mCurRecorderType;

    private RecorderManager() {
    }

    public RecorderType getCurRecorderType() {
        return mCurRecorderType;
    }

    public synchronized BaseRecorder startRecord(RecorderType type) {
        if (mCurRecorderType == type)
            return mCurRecorder;
        BaseRecorder r = RecorderFactory.createRecorder(type);
        if (r == null)
            return null;
        mCurRecorderType = type;
        mCurRecorder = r;
        startAudioRecorderInternal();
        return r;
    }

    public synchronized void stopRecord(RecorderType type) {
        if (mCurRecorderType == type) {
            stopRecord();
        }
    }

    public synchronized void stopRecord() {
        mCurRecorder = null;
        mCurRecorderType = null;
    }

    /**
     * 开启录音线程
     */
    public void startAudioRecorder() {
        startAudioRecorderInternal();
    }

    /**
     * 停止录音线程
     */
    public void stopAudioRecorder() {
        stopAudioRecorderInternal();
    }

    private void startAudioRecorderInternal() {
        synchronized (this) {
            if (!mRecordThreadStarted) {
                mRecordExecutor.submit(new AudioRecordRunnable());
                mRecordThreadStarted = true;
            }
        }
    }

    private void stopAudioRecorderInternal() {
        synchronized (this) {
            mRecordThreadStarted = false;
        }
    }

    public boolean isAudioRecorderStarted() {
        return mRecordThreadStarted;
    }

    private class AudioRecordRunnable implements Runnable {
        @Override
        public void run() {
            boolean doRestart = false;
            BaseRecorder currRecorder = mCurRecorder;
            try {
                if (mAudioRecord == null) {
                    audioParam = new AudioParam();
                    final int minBufferSize = AudioRecord.getMinBufferSize(audioParam.sampleRate, audioParam.channel, audioParam.audioFormat);
                    final int bufferSize = minBufferSize * audioParam.bufferMultiple;
                    KLog.d(StringUtil.format("minBufferSize : %d, bufferSize : %d", minBufferSize, bufferSize));
                    mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, audioParam.sampleRate, audioParam.channel, audioParam.audioFormat, bufferSize);
                    mAudioRecord.startRecording();
                    mBuffer = new byte[bufferSize];
                }
                final int recordingState = mAudioRecord.getRecordingState();
                if (AudioRecord.RECORDSTATE_RECORDING != recordingState && currRecorder != null) {
                    currRecorder.onFailed(new IllegalRecordStateException(recordingState));
                    mRecordThreadStarted = false;
                    return;
                }
                BaseRecorder lastRecorder = null;
                Log.e(TAG, "AudioRecord start");
                while (mRecordThreadStarted) {
                    currRecorder = mCurRecorder;
                    //录音对象发生变化,执行回调,并更新线程优先级
                    if (currRecorder != lastRecorder) {
                        if (lastRecorder != null) {
                            lastRecorder.onStop();
                            KLog.d(StringUtil.format("< %s > stop", lastRecorder.getClass().getSimpleName()));
                        }
                        if (currRecorder != null) {
                            Process.setThreadPriority(currRecorder.getRecordThreadPriority());
                            KLog.d(StringUtil.format("< %s > start", currRecorder.getClass().getSimpleName()));
                            currRecorder.onStart();
                        }
                    }
                    if (currRecorder != null) {
                        int readBytes = mAudioRecord.read(mBuffer, 0, mBuffer.length);
                        //KLog.d(StringUtil.format("<%s> readBytes : %d", currRecorder.getClass().getSimpleName(), readBytes));
                        if (readBytes > 0) {
                            // 采用录/写分离,避免写操作耗时影响录音线程,导致录音丢帧
                            mWriteExecutor.submit(new WriteRunnable(currRecorder, Arrays.copyOf(mBuffer, readBytes)));
                        } else {
                            stopAudioRecorderInternal();
                            doRestart = true;
                        }
                    }
                    lastRecorder = currRecorder;
                    SystemClock.sleep(mBuffer.length / audioParam.track);
                }
                Log.e(TAG, "AudioRecord stop");
                if (currRecorder != null) {
                    currRecorder.onStop();
                    KLog.d(StringUtil.format("<%s> stop", currRecorder.getClass().getSimpleName()));
                }
            } catch (Exception e) {
                e.printStackTrace();
                mRecordThreadStarted = false;
                if (currRecorder != null) {
                    KLog.d(StringUtil.format("<%s> failed, msg : %s", currRecorder.getClass().getSimpleName(), String.valueOf(e)));
                    currRecorder.onFailed(e);
                }
            } finally {
                if (mAudioRecord != null) {
                    try {
                        mAudioRecord.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        mAudioRecord = null;
                    }
                }
                Log.e("xm", "MicManager AudioRecord release");
            }
            if (doRestart) {
                startAudioRecorderInternal();
            }
        }
    }

    private class WriteRunnable implements Runnable {
        private final BaseRecorder recorder;
        private final byte[] buffer;

        WriteRunnable(@NonNull BaseRecorder recorder, @NonNull byte[] buffer) {
            this.recorder = recorder;
            this.buffer = buffer;
        }

        @Override
        public void run() {
            if (recorder != mCurRecorder) {
//                KLog.i("current recorder has changed, ignore this written");
                return;
            }
            Process.setThreadPriority(recorder.getRecordThreadPriority());
            byte[][] stereo = splitChannel(buffer);
            byte[] leftChannel = stereo[0];
            byte[] rightChannel = stereo[1];
            recorder.onBuffer(buffer, leftChannel, rightChannel);
            //KLog.d(StringUtil.format("record thread running, writing buffer len : %d", buffer.length));
        }

        private byte[][] splitChannel(byte[] oriBytes) {
            if (oriBytes == null) {
                return null;
            }
            int outLength = oriBytes.length / 2;
            byte[][] output = new byte[2][outLength];
            byte[] left = output[0];
            byte[] right = output[1];
            int oriIndex = 0;
            int leftIndex = 0;
            int rightIndex = 0;
            while (true) {
                try {
                    left[leftIndex++] = oriBytes[oriIndex++];
                    left[leftIndex++] = oriBytes[oriIndex++];
                    right[rightIndex++] = oriBytes[oriIndex++];
                    right[rightIndex++] = oriBytes[oriIndex++];
                } catch (Exception e) {
                    break;
                }
            }
            return output;
        }

    }


    private static class AudioParam {

        public AudioParam() {
            channel = RecordConstants.STEREO_CHANNEL;
            track = RecordConstants.STEREO_TRACK;
        }

        int sampleRate = RecordConstants.DEFAULT_SAMPLE_RATE;
        int audioFormat = RecordConstants.DEFAULT_AUDIO_FORMAT;
        int bufferMultiple = RecordConstants.DEFAULT_BUFFER_MULTIPLE;
        int channel = 0;
        int track = 0;

    }
}
