package com.xiaoma.club.msg.chat.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatDialog;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestBuilder;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.ICarEvent;
import com.xiaoma.carlib.manager.XmCarEventDispatcher;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.carlib.model.CarEvent;
import com.xiaoma.club.R;
import com.xiaoma.club.common.util.LogUtil;
import com.xiaoma.club.msg.chat.constant.MessageConfig;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.thread.Priority;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.constant.IatError;
import com.xiaoma.utils.constant.VrConstants;
import com.xiaoma.utils.media.PcmUtil;
import com.xiaoma.vr.iat.OnIatListener;
import com.xiaoma.vr.iat.RemoteIatManager;
import com.xiaoma.vr.recorder.RecordConstants;

import java.io.File;

import static com.xiaoma.club.common.util.LogUtil.logI;

/**
 * Created by LKF on 2019-3-26 0026.
 * 语音发送对话框
 */
public class VrRecordDialog extends AppCompatDialog implements View.OnClickListener {
    final String TAG = getClass().getSimpleName();
    private static final int MIN_SECONDS = MessageConfig.MIN_VOICE_SECONDS;// 最短时间
    private static final int MAX_SECONDS = MessageConfig.MAX_VOICE_SECONDS;// 最长时间
    private static final int COUNTDOWN_SECONDS = 10;// 开始倒计时剩余时间
    private static final String PATH_VOICE_RECORD = "ClubIat/";

    private final RemoteIatManager mIatManager = RemoteIatManager.getInstance();
    private final Handler mHandler = new Handler();
    private long mRecordStartTime;
    private boolean mHasStopListening;
    private File mVoiceFile;
    private long mVoiceDuration;
    private ICarEvent mCarEvent;

    private TextView mTvTitle;
    private ImageView mIvWaveStart;
    private ImageView mIvWaveEnd;
    private TextView mBtnSend;
    private TextView mBtnCancel;

    public VrRecordDialog(Context context, Callback callback) {
        super(context, R.style.custom_dialog2);
        mCallback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
        setContentView(R.layout.fmt_voice_msg_speak);
        mTvTitle = findViewById(R.id.tv_title);
        mIvWaveStart = findViewById(R.id.iv_wave_start);
        mIvWaveEnd = findViewById(R.id.iv_wave_end);
        mBtnSend = findViewById(R.id.btn_send);
        mBtnCancel = findViewById(R.id.btn_cancel);

        mBtnSend.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
        // 左侧动画翻转180度,使得左右对称
        mIvWaveStart.setRotationY(180);
        findViewById(R.id.bg_dim).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mRecordStartTime = System.currentTimeMillis();

        WindowManager.LayoutParams lp;
        if (getWindow() != null && (lp = getWindow().getAttributes()) != null) {
            lp.gravity = Gravity.START;
            lp.width = lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        }

        RequestBuilder<Drawable> reqBuilder = ImageLoader.with(getContext()).load(R.drawable.voice_record_anim);
        reqBuilder.into(mIvWaveStart);
        reqBuilder.into(mIvWaveEnd);

        mIatManager.init(getContext());
        mIatManager.setOnIatListener(new RecordIatListener());
        mIatManager.startListeningRecord();
        final int realRecordSeconds = MAX_SECONDS + 1;
        mHandler.postDelayed(new Runnable() {
            private long mSeconds = COUNTDOWN_SECONDS;

            @Override
            public void run() {
                if (!isShowing())
                    return;
                if (mSeconds >= 0) {
                    if (mTvTitle != null) {
                        mTvTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        mTvTitle.setText(String.valueOf(mSeconds--));
                        mTvTitle.setTextColor(getContext().getColor(R.color.chat_send_voice_recorder_countdown));
                        mTvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimensionPixelSize(R.dimen.chat_send_voice_recorder_countdown));
                    }
                    mHandler.postDelayed(this, 1000);
                } else {
                    mBtnSend.performClick();
                }
            }
        }, (realRecordSeconds - COUNTDOWN_SECONDS) * 1000);

        // 监听360全景
        XmCarEventDispatcher.getInstance().registerEvent(mCarEvent = new ICarEvent() {
            @Override
            public void onCarEvent(CarEvent event) {
                if (event == null)
                    return;
                if (SDKConstants.ID_CAMERA_STATUS == event.id) {
                    // 如果打开了倒车摄像头,则主动关闭录音对话框
                    if (XmCarVendorExtensionManager.getInstance().getCameraStatus()) {
                        cancel();
                    }
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacksAndMessages(null);

        stopListening();
        mIatManager.setOnIatListener(null);
        mIatManager.release();

        XmCarEventDispatcher.getInstance().unregisterEvent(mCarEvent);
    }

    private boolean stopListening() {
        if (mHasStopListening)
            return false;
        mHasStopListening = true;
        mIatManager.stopListening();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bg_dim:
                cancel();
                break;
            case R.id.btn_send:
                if ((System.currentTimeMillis() - mRecordStartTime) <= (MIN_SECONDS * 1000)) {
                    mTvTitle.setText(R.string.speak_time_too_short);
                    mTvTitle.setTextColor(getContext().getColor(R.color.chat_send_voice_recorder_duration_too_short));
                    mTvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimensionPixelSize(R.dimen.chat_send_voice_recorder_duration_too_short));
                    mTvTitle.setCompoundDrawablesWithIntrinsicBounds(R.drawable.chat_send_voice_recorder_alert, 0, 0, 0);
                    return;
                }
                if (stopListening()) {
                    // 网络不佳的时候,允许消息发出去,但是可能没有ASR内容,消息可以在消息列表里转圈圈
                    if (!NetworkUtils.isConnected(getContext())) {
                        XMToast.toastException(getContext(), R.string.net_work_error);
                    }
                }
                break;
            case R.id.btn_cancel:
                cancel();
                break;
        }
    }

    void sendVoice() {
        if (mBtnSend != null)
            mBtnSend.performClick();
    }

    private Callback mCallback;

    public interface Callback {
        void onSend(VrRecordDialog dlg, File wavFile, long duration, String translation);

        void onError(VrRecordDialog dlg, int errorCode);
    }

    private class RecordIatListener implements OnIatListener {
        @Override
        public void onComplete(String voiceText, String parseText) {
            logI(TAG, "onComplete( voiceContent: %s )", voiceText);
            onAsrEnd(voiceText, 0);
        }

        @Override
        public void onError(int errorCode) {
            if (IatError.ERROR_MEDIA_FOCUS_LOSS == errorCode
                    || IatError.ERROR_MIC_FOCUS_LOSS == errorCode) {
                // 焦点失去或者麦克风焦点失去,要退出录音,并提示用户
                XMToast.toastException(getContext(), R.string.tips_audio_focus_occupied);
                cancel();
                LogUtil.logE(TAG, "onError( errorCode: %s ) AudioFocus loss, cancel recording", errorCode);
            } else {
                LogUtil.logE(TAG, "onError( errorCode: %s )", errorCode);
            }
        }

        private void onAsrEnd(final String voiceContent, final int errorCode) {
            logI(TAG, "onAsrEnd( voiceContent: %s, errorCode: %s )", voiceContent, errorCode);
            ThreadDispatcher.getDispatcher().postSerial(new Runnable() {
                @Override
                public void run() {
                    boolean showing = isShowing();
                    logI(TAG, "onAsrEnd( voiceContent: %s, errorCode: %s ) run(){ showing: %s }", voiceContent, errorCode, showing);
                    if (!showing)
                        return;
                    if (mCallback != null) {
                        final File voiceFile = mVoiceFile;
                        if (voiceFile != null && voiceFile.exists()) {
                            mCallback.onSend(VrRecordDialog.this, voiceFile, mVoiceDuration, voiceContent);
                        } else {
                            mCallback.onError(VrRecordDialog.this, errorCode);
                        }
                    }
                    cancel();
                }
            }, Priority.HIGH);
        }

        @Override
        public void onWavFileComplete() {
            logI(TAG, "onWavFileComplete()");
            ThreadDispatcher.getDispatcher().postSerial(new Runnable() {
                @Override
                public void run() {
                    boolean showing = isShowing();
                    logI(TAG, "onWavFileComplete() run(){ showing: %s }", showing);
                    if (!showing)
                        return;
                    File sourceFile = new File(VrConstants.PCM_RECORD_FILE_PATH);// 语音助手保存的音频路径
                    File cacheDir = new File(Environment.getExternalStorageDirectory(), PATH_VOICE_RECORD);
                    if (!cacheDir.exists()) {
                        cacheDir.mkdirs();
                    }
                    final String dstFileName = "voice_" + System.currentTimeMillis();
                    File dstFile = new File(cacheDir, dstFileName);// 转换后的格式的录音文件
                    logI(TAG, "onWavFileComplete() run() Translate format begin ...");
                    try {
                        PcmUtil.pcm2Aac(sourceFile, dstFile, RecordConstants.DEFAULT_SAMPLE_RATE, 1);// 转换格式
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    logI(TAG, "onWavFileComplete() run() Translate format end !!!");
                    mVoiceFile = dstFile;
                    // 获取音频长度
                    logI(TAG, "onWavFileComplete() run() get duration begin...");
                    try {
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        retriever.setDataSource(dstFile.getPath());
                        mVoiceDuration = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                    } catch (Exception e) {
                        e.printStackTrace();
                        mVoiceDuration = 0;
                    }
                    logI(TAG, "onWavFileComplete() run() get duration end...");
                    logI(TAG, "onWavFileComplete(){ pcmLen: %s, dstLen: %s, duration: %s }", sourceFile.length(), dstFile.length(), mVoiceDuration);
                    // Debug不删录音文件,方便拿到位录音文件定位问题
                    if (!ConfigManager.ApkConfig.isDebug()) {
                        sourceFile.delete();
                    }
                }
            }, Priority.HIGH);
            // 等待ASR结束
            ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
                @Override
                public void run() {
                    mBtnSend.setText(R.string.chat_voice_sending);
                }
            });
        }

        @Override
        public void onVolumeChanged(int volume) {
            //logI(TAG, "onVolumeChanged( volume: %s )", volume);
        }

        @Override
        public void onNoSpeaking() {
            logI(TAG, "onNoSpeaking()");
        }

        @Override
        public void onResult(String recognizerText, boolean isLast, String currentText) {
            logI(TAG, "onResult( recognizerText: %s, isLast: %s , currentText: %s )", recognizerText, isLast, currentText);
        }

        @Override
        public void onRecordComplete() {
            logI(TAG, "onRecordComplete()");
        }
    }
}
