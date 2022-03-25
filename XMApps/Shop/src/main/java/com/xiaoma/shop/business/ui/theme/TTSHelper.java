package com.xiaoma.shop.business.ui.theme;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.xiaoma.vr.tts.OnTtsListener;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/07/26
 * @Describe:
 */
public class TTSHelper implements OnTtsListener, AudioManager.OnAudioFocusChangeListener {
    private int mCurrentPlayId = -1;
    private AudioManager mAudioManager;
    private int tempPlayId = -1;
    private PlayCallback mCallback;

    public void init(Context context, PlayCallback playCallback) {
        mCallback = playCallback;
        mAudioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
    }

    public void start(String voiceParam, String speakContent, int id) {
        int rlt = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == rlt) {
            tempPlayId = id;
            VoiceControlUtils.tryPlayVoiceTone(voiceParam, speakContent, this);
        }
    }

    public void stop() {
        VoiceControlUtils.stopSpeaking();
        release();
        if (mCallback != null) {
            mCallback.onStop();
        }
    }

    private void release() {
        mCurrentPlayId = -1;
        tempPlayId = -1;
        mAudioManager.abandonAudioFocusForCall();
    }

    public int getCurrentPlayId() {
        return mCurrentPlayId;
    }

    @Override
    public void onCompleted() {
        stop();
    }

    @Override
    public void onBegin() {
        mCurrentPlayId = tempPlayId;
        if (mCallback != null) {
            mCallback.onStart();
        }
    }

    @Override
    public void onError(int code) {
        if (mCallback != null) {
            mCallback.onError();
        }
        release();
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                stop();
                break;
        }
    }

    public interface PlayCallback {
        void onStart();

        void onStop();

        void onError();
    }
}
