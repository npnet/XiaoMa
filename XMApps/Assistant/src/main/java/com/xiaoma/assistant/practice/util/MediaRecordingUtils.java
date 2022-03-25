package com.xiaoma.assistant.practice.util;

import android.content.Context;
import android.os.Environment;

import com.xiaoma.thread.Priority;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.constant.VrConstants;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.media.PcmUtil;
import com.xiaoma.vr.iat.OnIatListener;
import com.xiaoma.vr.iat.RemoteIatManager;
import com.xiaoma.vr.recorder.RecordConstants;

import java.io.File;

/**
 * 录音功能
 */

public class MediaRecordingUtils {

    private static final String TAG = "MediaRecordingUtils";

    private static final String RECORD_DIR = "assistantRecord/";

    private static final int MAX_LENGTH = 1000 * 60;// 最大录音时长，单位毫秒，1000*60;

    private RemoteIatManager mIatManager;

    private static class SingletonHolder {
        private static final MediaRecordingUtils INSTANCE = new MediaRecordingUtils();
    }

    public static MediaRecordingUtils getInstance() {
        return MediaRecordingUtils.SingletonHolder.INSTANCE;
    }


    public interface OnRecordComplete {
        void recordComplete(String path);
    }

    private OnRecordComplete mOnRecordComplete;

    public void startRecord(Context context, OnRecordComplete onRecordComplete) {

        mIatManager = RemoteIatManager.getInstance();
        mIatManager.init(context);
        mIatManager.setOnIatListener(new RecordIatListener());
        mIatManager.startListeningRecord();
        mOnRecordComplete = onRecordComplete;

    }

    private class RecordIatListener implements OnIatListener {
        @Override
        public void onComplete(String voiceText, String parseText) {
            KLog.d(TAG, "onComplete : " + voiceText + " parseText: " + parseText);
        }

        @Override
        public void onError(int errorCode) {
            KLog.d(TAG, "onError: " + errorCode);
        }

        private void onAsrEnd(final String voiceContent, final int errorCode) {
            KLog.d(TAG, "onAsrEnd : " + voiceContent + " errorCode: " + errorCode);
        }

        @Override
        public void onWavFileComplete() {
            KLog.d(TAG, "onWavFileComplete()");
            ThreadDispatcher.getDispatcher().postSerial(new Runnable() {
                @Override
                public void run() {
                    File sourceFile = new File(VrConstants.PCM_RECORD_FILE_PATH);// 语音助手保存的音频路径
                    File cacheDir = new File(Environment.getExternalStorageDirectory(), RECORD_DIR);
                    if (!cacheDir.exists()) {
                        cacheDir.mkdirs();
                    }
                    final String dstFileName = "voice_" + System.currentTimeMillis() + ".wav";
                    File dstFile = new File(cacheDir, dstFileName);// 转换后的格式的录音文件
                    KLog.d(TAG, "onWavFileComplete() run() Translate format begin ...");
                    try {
                        PcmUtil.convertPcm2Wav(sourceFile, dstFile, RecordConstants.DEFAULT_SAMPLE_RATE, 1, 16);// 转换格式
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (mOnRecordComplete != null) {
                        mOnRecordComplete.recordComplete(dstFile.getAbsolutePath());
                    }
                    KLog.d(TAG, "onWavFileComplete(){ pcmLen: " + sourceFile.length() + ", dstLen: " + dstFile.length());
                    sourceFile.delete();// 转换完成后删除原来的音频
                }
            }, Priority.HIGH);
        }

        @Override
        public void onVolumeChanged(int volume) {
        }

        @Override
        public void onNoSpeaking() {
            KLog.d(TAG, "onNoSpeaking  ");
        }

        @Override
        public void onResult(String recognizerText, boolean isLast, String currentText) {
            KLog.d(TAG, "onResult  ");
        }

        @Override
        public void onRecordComplete() {
            KLog.d(TAG, "onRecordComplete");
        }
    }


    /**
     * 停止录音
     */
    public void stopRecord() {
        if (mIatManager == null) {
            return;
        }
        mIatManager.stopListening();
    }

    public void releaseRecord() {
        if (mIatManager == null) {
            return;
        }
        mIatManager.release();
        mIatManager.setOnIatListener(null);
        mOnRecordComplete = null;
    }


}