package com.xiaoma.songname.common.manager;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/06/20
 * @Describe: 音频焦点管理类
 */

public class AudioFocusManager {

    private AudioManager mAudioManager;
    private List<IAudioFocusListener> mIAudioFocusListeners;
    private AudioFocusRequest mFocusRequest;
    private AudioAttributes mAudioAttributes;
    private static AudioFocusManager instance;

    public static AudioFocusManager getInstance() {
        if (instance == null) {
            synchronized (AudioFocusManager.class) {
                if (instance == null) {
                    instance = new AudioFocusManager();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mIAudioFocusListeners = new ArrayList<>();
    }

    public void registerListener(IAudioFocusListener listener) {
        if (listener == null || mIAudioFocusListeners.contains(listener)) return;
        mIAudioFocusListeners.add(listener);
    }

    public void unRegisterListener(IAudioFocusListener listener) {
        if (listener == null || !mIAudioFocusListeners.contains(listener)) return;
        mIAudioFocusListeners.remove(listener);
    }

    public boolean requestFocus() {
        int result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mFocusRequest == null) {
                if (mAudioAttributes == null) {
                    mAudioAttributes = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build();
                }
                mFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                        .setAudioAttributes(mAudioAttributes)
                        .setWillPauseWhenDucked(true)
                        .setOnAudioFocusChangeListener(mOnAudioFocusChangeListener)
                        .build();
            }
            result = mAudioManager.requestAudioFocus(mFocusRequest);
        } else {
            result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        }

        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == result;
    }

    public boolean abandonFocus() {
        int result = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (mFocusRequest != null) {
                result = mAudioManager.abandonAudioFocusRequest(mFocusRequest);
            }
        } else {
            result = mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == result;
    }

    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS://此状态表示，焦点被其他应用获取 AUDIOFOCUS_GAIN 时，触发此回调，需要暂停播放。
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT://短暂性丢失焦点，如播放视频，打电话等，需要暂停播放
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK://短暂性丢失焦点并作降音处理，看需求处理而定。
                    for (IAudioFocusListener iAudioFocusListener : mIAudioFocusListeners) {
                        iAudioFocusListener.stop();
                    }
                    break;
                default:
            }
        }
    };

    public interface IAudioFocusListener {
        void stop();
    }
}
