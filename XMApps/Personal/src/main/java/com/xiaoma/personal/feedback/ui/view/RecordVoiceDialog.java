package com.xiaoma.personal.feedback.ui.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.personal.R;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.AppUtils;
import com.xiaoma.utils.constant.IatError;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.iat.OnIatListener;
import com.xiaoma.vr.iat.RemoteIatManager;
import com.xiaoma.vr.tts.EventTtsManager;

/**
 * @author Gillben
 * date: 2018/12/04
 * <p>
 * 反馈主页 录音窗口
 */
public class RecordVoiceDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = RecordVoiceDialog.class.getSimpleName();
    private Context mContext;
    private TextView mTitleText;
    private TextView mConfirmText;
    private TextView mCancelText;
    private ImageView mRecordVoiceImg;
    private ImageView rightVoiceImg;
    private ImageView leftVoiceImg;

    private static final int TOTAL_TIME = 5000;
    private OnConfirmCallback onConfirmCallback;
    private boolean isRecord = false;
    private TimerRunnable timerRunnable;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private AnimationDrawable rightAnimation;
    private AnimationDrawable leftAnimation;
    private static final String ASSISTANT_PKG = "com.xiaoma.assistant";
    private RecordProgressCallback recordProgressCallback;

    public static RecordVoiceDialog newInstance() {
        return new RecordVoiceDialog();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setStyle(0, R.style.custom_dialog2);
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        recordProgressCallback = (RecordProgressCallback) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.dialog_record_voice, container, false);
        initView(contentView);
        registerFocusLossReceiver();
        return contentView;
    }

    private void initView(View contentView) {
        mRecordVoiceImg = contentView.findViewById(R.id.iv_record_voice_image);
        rightVoiceImg = contentView.findViewById(R.id.iv_voice_line_right);
        leftVoiceImg = contentView.findViewById(R.id.iv_voice_line_left);

        mTitleText = contentView.findViewById(R.id.tv_record_voice_title);
        mConfirmText = contentView.findViewById(R.id.tv_confirm_text);
        mCancelText = contentView.findViewById(R.id.tv_cancel_text);
        mConfirmText.setOnClickListener(this);
        mCancelText.setOnClickListener(this);
    }

    private BroadcastReceiver focusLossReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (CenterConstants.IN_A_CALL.equals(intent.getAction())) {
                KLog.d(TAG, "焦点丢失，取消当前收音.");
                RecordVoiceDialog.this.dismiss();
            }
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().setCanceledOnTouchOutside(true);
        Window window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            DisplayMetrics dm = new DisplayMetrics();
            window.getWindowManager().getDefaultDisplay().getMetrics(dm);
            layoutParams.width = 654;
            layoutParams.height = 420;
            window.setAttributes(layoutParams);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        boolean isRunning = checkAssistantServiceIsRun();
        if (!isRunning) {
            startAliveTimer();
            return;
        }

        ThreadDispatcher.getDispatcher().postOnMainDelayed(this::initRecordVoice, 1000);
    }


    private boolean checkAssistantServiceIsRun() {
        return AppUtils.isAppInstalled(mContext, ASSISTANT_PKG);
    }


    private void initRecordVoice() {
        RemoteIatManager.getInstance().init(mContext);
        RemoteIatManager.getInstance().setOnIatListener(new OnIatListener() {
            @Override
            public void onComplete(String voiceText, String parseText) {
                if (recordProgressCallback != null) {
                    recordProgressCallback.dismissRecordProgress();
                }

                KLog.w("voiceContent: " + voiceText);
                if (isRecord && onConfirmCallback != null) {
                    onConfirmCallback.confirm(voiceText);
                }
                RecordVoiceDialog.this.dismiss();
                EventTtsManager.getInstance().destroy();
                RemoteIatManager.getInstance().release();
            }

            @Override
            public void onVolumeChanged(int volume) {
                KLog.d("Adjust voice: " + volume);
                if (volume >= 4) {
                    isRecord = true;
                    stopAliveTimer();
                }
            }

            @Override
            public void onNoSpeaking() {
                KLog.d("Not speaking.");
            }

            @Override
            public void onError(int errorCode) {
                KLog.e("Record error: code=" + errorCode);
                if (recordProgressCallback != null) {
                    recordProgressCallback.dismissRecordProgress();
                }

                int resId;
                if (IatError.ERROR_MIC_FOCUS_LOSS == errorCode) {
                    resId = R.string.feedback_mic_dismiss;
                } else {
                    resId = R.string.hint_fail_receive_voice;
                }
                XMToast.toastException(mContext, resId);
                RecordVoiceDialog.this.dismiss();
                RemoteIatManager.getInstance().release();
            }

            @Override
            public void onResult(String recognizerText, boolean isLast, String currentText) {
                KLog.d("recognizerText: " + recognizerText +
                        " --- isLast: " + isLast + " --- currentText: " + currentText);
            }

            @Override
            public void onWavFileComplete() {
                KLog.d("onWavFileComplete.");
            }

            @Override
            public void onRecordComplete() {
                KLog.d("onRecordComplete.");
            }
        });

        startVoiceDance();
        startAliveTimer();
        RemoteIatManager.getInstance().startListeningRecord();
    }


    private void startVoiceDance() {
        mTitleText.setText(R.string.hint_receiving_voice);
        leftVoiceImg.setVisibility(View.VISIBLE);
        rightVoiceImg.setVisibility(View.VISIBLE);
        if (leftAnimation == null) {
            leftAnimation = (AnimationDrawable) leftVoiceImg.getBackground();
        }
        if (rightAnimation == null) {
            rightAnimation = (AnimationDrawable) rightVoiceImg.getBackground();
        }
        leftAnimation.start();
        rightAnimation.start();
    }


    private void stopVoiceDance() {
        if (leftAnimation != null && leftAnimation.isRunning()) {
            leftAnimation.stop();
            leftAnimation = null;
        }
        if (rightAnimation != null && rightAnimation.isRunning()) {
            rightAnimation.stop();
            rightAnimation = null;
        }
    }


    private void startAliveTimer() {
        if (timerRunnable == null) {
            timerRunnable = new TimerRunnable();
        }
        mHandler.postDelayed(timerRunnable, TOTAL_TIME);
    }

    private void stopAliveTimer() {
        if (timerRunnable != null) {
            mHandler.removeCallbacks(timerRunnable);
            timerRunnable = null;
        }
    }


    public void show(FragmentManager fragmentManager, String tag, OnConfirmCallback callback) {
        this.show(fragmentManager, tag);
        this.onConfirmCallback = callback;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }

    private void registerFocusLossReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CenterConstants.IN_A_CALL);
        mContext.registerReceiver(focusLossReceiver, intentFilter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_confirm_text:
                if (recordProgressCallback != null) {
                    recordProgressCallback.showRecordProgress();
                }
                dismiss();
                break;

            case R.id.tv_cancel_text:
                isRecord = false;
                dismiss();
                break;
        }
    }


    public interface OnConfirmCallback {
        void confirm(String content);

    }


    class TimerRunnable implements Runnable {

        @Override
        public void run() {
            RemoteIatManager.getInstance().cancelListening();
            stopAliveTimer();
            RecordVoiceDialog.this.dismiss();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (focusLossReceiver != null) {
            mContext.unregisterReceiver(focusLossReceiver);
            focusLossReceiver = null;
        }


        stopVoiceDance();
        stopAliveTimer();

        if (checkAssistantServiceIsRun()) {
            RemoteIatManager.getInstance().cancelListening();
        }

        if (!isRecord) {
            RemoteIatManager.getInstance().release();

            if (recordProgressCallback != null) {
                recordProgressCallback.dismissRecordProgress();
            }
        }
    }


    public interface RecordProgressCallback {
        void showRecordProgress();

        void dismissRecordProgress();
    }

}
