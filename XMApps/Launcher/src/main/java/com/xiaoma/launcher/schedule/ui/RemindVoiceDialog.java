package com.xiaoma.launcher.schedule.ui;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.schedule.ui
 *  @file_name:      RemindVoiceDialog
 *  @author:         Rookie
 *  @create_time:    2019/4/15 16:32
 *  @description：   TODO             */

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.views.BaseRecommendDialog;
import com.xiaoma.launcher.schedule.view.VoiceTextView;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.iat.OnIatListener;
import com.xiaoma.vr.iat.RemoteIatManager;
import com.xiaoma.vr.tts.EventTtsManager;
import com.xiaoma.vr.tts.OnTtsListener;

public class RemindVoiceDialog extends BaseRecommendDialog {

    private ImageView ivVoice;
    private TextView tvTitle;
    private TextView tvTip;
    private VoiceTextView tvMessage;
    private OnVoiceContentListener mContentCompleteListener;
    private AnimationDrawable mAnimationDrawable;

    public interface OnVoiceContentListener {
        void onIatComplete(String content);

        void onIatFail();
    }


    public void setOnVoiceContentComplete(OnVoiceContentListener onCompleteListener) {
        mContentCompleteListener = onCompleteListener;
    }

    public RemindVoiceDialog(@NonNull Context context) {
        super(context);
        EventTtsManager.getInstance().init(mContext);
        RemoteIatManager.getInstance().init(mContext);
        RemoteIatManager.getInstance().setOnIatListener(new OnIatListener() {
            @Override
            public void onComplete(String voiceText, String parseText) {
                KLog.d("voice onComplete voiceText: " + voiceText + "parseText: " + parseText);
                stopAnim();
                if (mContentCompleteListener != null) {
                    mContentCompleteListener.onIatComplete(voiceText);
                }
            }

            @Override
            public void onVolumeChanged(int volume) {
            }

            @Override
            public void onNoSpeaking() {
                KLog.d("voice onNoSpeaking ");
                stopAnim();
                if (mContentCompleteListener != null) {
                    mContentCompleteListener.onIatFail();
                }
            }

            @Override
            public void onError(int errorCode) {
                KLog.d("voice onError " + errorCode);
                stopAnim();
                if (mContentCompleteListener != null) {
                    mContentCompleteListener.onIatFail();
                }
            }

            @Override
            public void onResult(String recognizerText, boolean isLast, String currentText) {
                KLog.d("voice onResult  " + recognizerText + " --- " + isLast + "  " + currentText);
                tvMessage.setText(recognizerText);
            }

            @Override
            public void onWavFileComplete() {
                KLog.d("voice onWavFileComplete");
            }

            @Override
            public void onRecordComplete() {
                KLog.d("voice onRecordComplete");
            }
        });
    }

    @Override
    public int getContentViewId() {
        return R.layout.view_remind_voice_dialog;
    }

    @Override
    protected void initWindow() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.gravity = Gravity.START | Gravity.TOP;
        lp.width = 600;
        lp.height = 160;
        lp.x = 211;
        lp.y = 475;
        getWindow().setAttributes(lp);
    }

    @Override
    public void initView() {
        ivVoice = findViewById(R.id.iv_voice);
        tvTitle = findViewById(R.id.tv_title);
        tvTip = findViewById(R.id.tv_tip);
        tvMessage = findViewById(R.id.tv_voice_msg);
    }

    @Override
    public void show() {
        super.show();
        EventTtsManager.getInstance().startSpeaking(mContext.getString(R.string.need_record_something), new OnTtsListener() {
            @Override
            public void onCompleted() {
                startListen();
            }

            @Override
            public void onBegin() {
                KLog.d("onBegin");
            }

            @Override
            public void onError(int code) {
                KLog.d("onerror....." + code);
            }
        });
    }

    private void startListen() {
        tvTitle.setVisibility(View.GONE);
        tvTip.setVisibility(View.GONE);
        tvMessage.setVisibility(View.VISIBLE);
        tvMessage.startEllipsisAnimation();
        ivVoice.setImageResource(R.drawable.speak_rhythm);
        ivVoice.setPadding(25, 25, 25, 25);
        mAnimationDrawable = (AnimationDrawable) ivVoice.getDrawable();
        mAnimationDrawable.start();
        RemoteIatManager.getInstance().stopListening();
        RemoteIatManager.getInstance().startListeningNormal(true);
    }

    private void stopAnim() {
        if (mAnimationDrawable != null) {
            mAnimationDrawable.stop();
        }
        ivVoice.setImageResource(R.drawable.icon_circle_voice);
        ivVoice.setPadding(0, 0, 0, 0);
        tvMessage.stopEllipsisAnimation();
    }


}
