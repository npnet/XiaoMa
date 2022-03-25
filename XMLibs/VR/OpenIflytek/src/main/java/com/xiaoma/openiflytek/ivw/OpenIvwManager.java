package com.xiaoma.openiflytek.ivw;

import android.content.Context;

import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.iat.IAssistantView;
import com.xiaoma.vr.ivw.IIvwManager;
import com.xiaoma.vr.ivw.OnWakeUpListener;
import com.xiaoma.vr.recorder.BaseRecorder;
import com.xiaoma.vr.recorder.OnRecordListener;
import com.xiaoma.vr.recorder.RecorderManager;
import com.xiaoma.vr.recorder.RecorderType;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/6/11
 * Desc：Ivw manager by open IFlyTek speech
 */
public class OpenIvwManager implements IIvwManager,OnRecordListener {
    private static OpenIvwManager instance = null;
    private OpenXfWakeup wakeUpInstance = null;
    private OnWakeUpListener onOtherWakeUpListener;
    private OnWakeUpListener onWakeUpListener = new OnWakeUpListener() {
        @Override
        public void onWakeUp(int index, String keyWord, boolean isLeft) {
            if (onOtherWakeUpListener != null) {
                onOtherWakeUpListener.onWakeUp(index,keyWord,isLeft);
            }
        }

        @Override
        public void onWakeUpCmd(String cmdText) {
            if (onOtherWakeUpListener != null) {
                onOtherWakeUpListener.onWakeUpCmd(cmdText);
            }
        }
    };

    public static IIvwManager getInstance() {
        if (instance == null) {
            synchronized (OpenIvwManager.class) {
                if (instance == null) {
                    instance = new OpenIvwManager();
                }
            }
        }
        return instance;
    }


    private OpenIvwManager() {
        if (wakeUpInstance == null) {
            wakeUpInstance = new OpenXfWakeup();
        }
    }

    @Override
    public void init(Context context) {
        if (wakeUpInstance != null) {
            wakeUpInstance.init(context);
        }
    }


    @Override
    public void setOnWakeUpListener(OnWakeUpListener otherListener) {
        this.onOtherWakeUpListener = otherListener;
        if (wakeUpInstance != null) {
            wakeUpInstance.setOnWakeUpListener(otherListener);
        }
    }

    @Override
    public void addAssistantView(IAssistantView view) {

    }


    @Override
    public void startWakeup() {
        if (wakeUpInstance != null) {
            wakeUpInstance.startWakeup();
        }
        startRecorder();
    }

    /**
     * 开启语音唤醒前，需要确保录音线程被开启
     */
    public void setWakeupInterception(boolean interception) {
        if (interception) {
            stopAudioRecorder();
        } else {
            RecorderManager.getInstance().startAudioRecorder();
            this.startWakeup();
        }
    }


    @Override
    public void stopWakeup() {
        if (wakeUpInstance != null) {
            wakeUpInstance.stopWakeup();
        }
        stopRecorderInternal();
    }


    private void appendAudioData(byte[] buffer, int start, int byteCount) {
        if (wakeUpInstance != null) {
            wakeUpInstance.appendAudioData(buffer,start,byteCount);
        }
    }

    @Override
    public void startRecorder() {
        BaseRecorder r = RecorderManager.getInstance().startRecord(RecorderType.IVW);
        if (r != null) {
            r.addOnRecordListener(this);
        } else {
            KLog.e("ivw recorder start failed !");
        }
    }

    @Override
    public void stopRecorder() {
        if (wakeUpInstance != null) {
            wakeUpInstance.stopWakeup();
        }
        stopRecorderInternal();
    }


    private void stopRecorderInternal() {
        RecorderManager.getInstance().stopRecord(RecorderType.IVW);
    }

    /**
     * 听歌识曲需要停止语音助手的录音线程,才能开始识别歌曲。而不仅仅是停止录音唤醒
     */
    private void stopAudioRecorder() {
        RecorderManager.getInstance().stopAudioRecorder();
    }


    @Override
    public void setIvwThreshold(int curThresh) {
        if (wakeUpInstance != null) {
            wakeUpInstance.setIvwThreshold(curThresh);
        }
    }


    @Override
    public void release() {

    }


    @Override
    public void onStart(BaseRecorder recorder) {
        //do nothing
    }

    @Override
    public void onBuffer(BaseRecorder recorder, byte[] buffer, int start, int byteCount) {
        if (buffer != null && buffer.length > 0)
            appendAudioData(buffer, start, byteCount);
    }

    @Override
    public void onBuffer(BaseRecorder recorder, byte[] buffer, byte[] leftBuffer, byte[] rightBuffer, int start, int byteCount) {

    }

    @Override
    public void onStop(BaseRecorder recorder) {
        //do nothing
    }

    @Override
    public void onRecordFailed(BaseRecorder recorder, Exception e) {
        KLog.e(StringUtil.format("onRecordFailed : %s", String.valueOf(e)));
    }
}
