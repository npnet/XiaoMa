package com.xiaoma.facerecognize.ui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.facerecognize.R;
import com.xiaoma.facerecognize.common.RecognizeManager;
import com.xiaoma.utils.CountDownTimer;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.iat.OnIatListener;
import com.xiaoma.vr.iat.RemoteIatManager;
import com.xiaoma.vr.tts.EventTtsManager;
import com.xiaoma.vr.tts.OnTtsListener;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by kaka
 * on 19-4-12 上午10:17
 * <p>
 * desc: #a
 * </p>
 */
public abstract class AbsRecognizeDialog extends BaseActivity {
    private static final String TAG = AbsRecognizeDialog.class.getSimpleName();
    protected CountDown mCountDownTask;
    protected Random mRandom = new Random();
    private List<String> okStrList;
    private List<String> cancelList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        statusBarDividerGone();
        getRootLayout().setBackground(new ColorDrawable(Color.TRANSPARENT));
        okStrList = Arrays.asList(getResources().getStringArray(R.array.ok));
        cancelList = Arrays.asList(getResources().getStringArray(R.array.cancel));
        mCountDownTask = new CountDown(getSecondsInFuture() * 1000, getInterval());
        EventTtsManager.getInstance().init(this);
        RemoteIatManager.getInstance().init(this);
        RemoteIatManager.getInstance().setOnIatListener(new OnIatListener() {
            @Override
            public void onComplete(String voiceText, String parseText) {
                KLog.d(TAG, "onComplete：" + voiceText);
            }

            @Override
            public void onVolumeChanged(int volume) {
                KLog.d(TAG, "onVolumeChanged：" + volume);
            }

            @Override
            public void onNoSpeaking() {
                KLog.d(TAG, "oonNoSpeaking");
            }

            @Override
            public void onError(int errorCode) {
                KLog.d(TAG, "onErrorL:" + errorCode);

            }

            @Override
            public void onResult(String recognizerText, boolean isLast, String currentText) {
                KLog.d(TAG, "voice onResult  recognizerText:" + recognizerText + " ---isLast: " + isLast + " currentText: " + currentText);
                if (!TextUtils.isEmpty(recognizerText)) {
                    if (okStrList.contains(recognizerText)) {
                        onIatConfirm();
                    } else if (cancelList.contains(recognizerText)) {
                        onIatCancel();
                    } else {
                        onChose(recognizerText);
                    }
                }
            }

            @Override
            public void onWavFileComplete() {
                KLog.d(TAG, "onWavFileComplete");
            }

            @Override
            public void onRecordComplete() {
                KLog.d(TAG, "onRecordComplete");
            }
        });
    }

    /**
     * 倒计时读秒回调
     *
     * @param seconds 剩余倒计时时间
     */
    abstract void onTick(int seconds);

    /**
     * 倒计时读秒完成回调
     */
    protected void onFinish() {
        finish();
    }

    /**
     * 识别“取消”的语音命令
     */
    protected void onIatCancel() {
        finish();
    }

    /**
     * 识别“确定”的语音命令
     */
    protected void onIatConfirm() {
        //super class do nothing, lay on child class
    }

    /**
     * 识别“导航地点”的语音命令
     */
    protected void onChose(String recognizerText) {

    }

    /**
     * 获取倒计时时长
     *
     * @return seconds 总倒计时时长，单位秒
     */
    protected int getSecondsInFuture() {
        return 10;
    }

    /**
     * 获取倒计时回调间隔
     *
     * @return millis 倒计时回调间隔，单位毫秒
     */
    protected long getInterval() {
        return 1000;
    }

    protected void playTTs(int tipRes) {
        String content = getString(tipRes);
        playTTs(content);
    }

    protected void playTTs(String content) {
        EventTtsManager.getInstance().startSpeaking(content, new OnTtsListener() {
            @Override
            public void onCompleted() {
                startCountDown();
                RemoteIatManager.getInstance().startListeningNormal();
            }

            @Override
            public void onBegin() {

            }

            @Override
            public void onError(int code) {
                startCountDown();
            }
        });
    }

    protected void startCountDown() {
        if (mCountDownTask == null) {
            mCountDownTask = new CountDown(getSecondsInFuture(), getInterval());
        } else {
            mCountDownTask.cancel();
        }
        mCountDownTask.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCountDownTask != null) {
            mCountDownTask.cancel();
        }
        getWindow().setTransitionBackgroundFadeDuration(200);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        EventTtsManager.getInstance().destroy();
        RemoteIatManager.getInstance().cancelListening();
        RemoteIatManager.getInstance().release();
    }

    class CountDown extends CountDownTimer {

        CountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            AbsRecognizeDialog.this.onTick(Math.round(millisUntilFinished / 1000f));
        }

        @Override
        public void onFinish() {
            AbsRecognizeDialog.this.onFinish();
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (mCountDownTask != null) {
            mCountDownTask.cancel();
        }
        if (this instanceof TipsDialogActivity
                || this instanceof NavDialogActivity) {
            EventTtsManager.getInstance().stopSpeaking();
            RecognizeManager.getInstance().handleRecognize(this);
        }
    }
}
