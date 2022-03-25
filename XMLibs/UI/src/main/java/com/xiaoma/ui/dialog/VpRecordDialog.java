package com.xiaoma.ui.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xiaoma.thread.Priority;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.R;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.constant.IatError;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.iat.OnIatListener;
import com.xiaoma.vr.iat.RemoteIatManager;


/**
 * @author taojin
 * @date 2019/6/11
 */
public class VpRecordDialog extends AppCompatDialog implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();
    private static final int MIN_SECONDS = 4 * 1000;// 最短时间
    private static final int NO_SPEAK_CODE = -1;
    private Callback mCallBack;
    private boolean mHasStopListening;

    private final RemoteIatManager mIatManager = RemoteIatManager.getInstance();

    private ImageView mIvWaveStart;
    private Button mBtnSure;
    private RelativeLayout rlVp;
    private AnimationDrawable mAnimationDrawable;

    private boolean isSpeak = false;
    private Runnable speakRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isSpeak) {
                mBtnSure.performClick();
            }
        }
    };

    public VpRecordDialog(Context context, Callback callback) {
        super(context, R.style.vp_record_dialog);
        mCallBack = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_vp_record2);
        mIvWaveStart = findViewById(R.id.iv_dialog_anim2);
        mBtnSure = findViewById(R.id.btn_sure);
        rlVp = findViewById(R.id.rl_vp);

        if (mBtnSure != null) {
            mBtnSure.setOnClickListener(this);
        }

        // 左侧动画翻转180度,使得左右对称
        mIvWaveStart.setRotationY(180);
        mAnimationDrawable = (AnimationDrawable) mIvWaveStart.getBackground();
    }

    @Override
    protected void onStart() {
        super.onStart();

        WindowManager.LayoutParams lp;
        if (getWindow() != null && (lp = getWindow().getAttributes()) != null) {
            lp.gravity = Gravity.START;
            lp.width = lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        }
        mAnimationDrawable.start();

        mIatManager.init(getContext());
        mIatManager.setOnIatListener(new RecordIatListener());
        mIatManager.startListeningRecord(3000, 3000);
        ThreadDispatcher.getDispatcher().postOnMainDelayed(speakRunnable, MIN_SECONDS);

    }

    @Override
    protected void onStop() {
        super.onStop();
        stopListening();
        mIatManager.setOnIatListener(null);
        mIatManager.release();
    }

    private boolean stopListening() {
        ThreadDispatcher.getDispatcher().removeOnMain(speakRunnable);
        if (mHasStopListening)
            return false;
        mHasStopListening = true;
        mIatManager.stopListening();
        mAnimationDrawable.stop();
        return true;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_sure) {
//            if ((System.currentTimeMillis() - mRecordStartTime) <= (MIN_SECONDS)) {
//                XMToast.showToast(getContext(), R.string.you_no_speaking);
//                return;
//            }
            if (stopListening()) {
                // 网络不佳的时候,允许消息发出去,但是可能没有ASR内容,消息可以在消息列表里转圈圈
                if (!NetworkUtils.isConnected(getContext())) {
                    XMToast.toastException(getContext(), R.string.network_error);
                }
            }
        }
    }


    private class RecordIatListener implements OnIatListener {
        @Override
        public void onComplete(String voiceText, String parseText) {
            KLog.d(TAG, "onComplete voiceContent: " + voiceText);
            onAsrEnd(voiceText, 0);
        }

        @Override
        public void onError(int errorCode) {
            KLog.d(TAG, "onError errorCode: " + errorCode);
            // 如果用户没有说话,语音助手那边会回调onError,此时要去取录音文件
            if (NO_SPEAK_CODE == errorCode) {
                XMToast.showToast(getContext(), R.string.you_no_speaking);
            } else if (errorCode == IatError.ERROR_MEDIA_FOCUS_LOSS || errorCode == IatError.ERROR_MIC_FOCUS_LOSS) {
                XMToast.toastException(getContext(), R.string.get_mic_failed);
            }
            cancel();
        }

        private void onAsrEnd(final String voiceContent, final int errorCode) {
            KLog.d(TAG, "onAsrEnd voiceContent: " + voiceContent + "errorCode " + errorCode);
            ThreadDispatcher.getDispatcher().postSerial(new Runnable() {
                @Override
                public void run() {
                    boolean showing = isShowing();
                    KLog.d(TAG, String.format("onAsrEnd( voiceContent: %s, errorCode: %s ) run(){ showing: %s }", voiceContent, errorCode, showing));
                    if (!showing)
                        return;
                    if (mCallBack != null) {
                        if (!TextUtils.isEmpty(voiceContent)) {
                            mCallBack.onSend(VpRecordDialog.this, voiceContent);
                        } else {
                            mCallBack.onError(VpRecordDialog.this, errorCode);
                        }
                    }
                    if (mHasStopListening || (!TextUtils.isEmpty(voiceContent) && voiceContent.length() >= 30)) {
                        cancel();
                    }
                }
            }, Priority.HIGH);
        }

        @Override
        public void onWavFileComplete() {

        }

        @Override
        public void onVolumeChanged(int volume) {
            //logI(TAG, "onVolumeChanged( volume: %s )", volume);
            if (volume > 4) {
                isSpeak = true;
                ThreadDispatcher.getDispatcher().removeOnMain(speakRunnable);
            }
        }

        @Override
        public void onNoSpeaking() {
            KLog.d(TAG, "onNoSpeaking()");
        }

        @Override
        public void onResult(String recognizerText, boolean isLast, String currentText) {
            KLog.d(TAG, String.format("onResult( recognizerText: %s, isLast: %s , currentText: %s )", recognizerText, isLast, currentText));
            onAsrEnd(recognizerText, 0);
        }

        @Override
        public void onRecordComplete() {
            KLog.d(TAG, "onRecordComplete()");
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!(event.getX() >= -10 && event.getY() >= -10)
                    || event.getX() >= rlVp.getWidth() + 10
                    || event.getY() >= rlVp.getHeight() + 20) {//如果点击位置在当前View外部则销毁当前视图,其中10与20为微调距离
                ThreadDispatcher.getDispatcher().removeOnMain(speakRunnable);
                mBtnSure.performClick();
            }
        }
        return true;
    }


    @Override
    public void dismiss() {
        KLog.d(TAG, "dismiss");
        super.dismiss();
    }

    public interface Callback {
        void onSend(VpRecordDialog dlg, String translation);

        void onError(VpRecordDialog dlg, int errorCode);
    }
}
