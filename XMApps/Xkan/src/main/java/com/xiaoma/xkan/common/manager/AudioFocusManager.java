package com.xiaoma.xkan.common.manager;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;

import com.xiaoma.component.AppHolder;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.xkan.ijkplayer.NiceVideoPlayerManager;

/**
 * 兼容8.0以及以下的版本音频焦点控制
 */
public class AudioFocusManager {

    private static final String TAG = AudioFocusManager.class.getSimpleName();
    private static boolean IS_OVER_ANDROID_O = Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O;
    private static final int AUDIO_FOCUS_GAIN_FLAG = AudioManager.AUDIOFOCUS_GAIN;
    private static final int AUDIO_STREAM_TYPE = AudioManager.STREAM_MUSIC;
    private AudioManager mAudioManager;
    private AudioFocusRequest mAudioFocusRequest;
    private boolean mHasAudioFocus;
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
    }

    public boolean hasAudioFocus() {
        return mHasAudioFocus;
    }

    public void requestAudioFocus() {
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) AppHolder.getInstance().getAppContext().getSystemService(Context.AUDIO_SERVICE);
        }
        if (mHasAudioFocus) {
            return;
        }
        int rlt;
        if (IS_OVER_ANDROID_O) {
            if (mAudioFocusRequest == null) {
                AudioAttributes attr = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setLegacyStreamType(AUDIO_STREAM_TYPE)
                        .build();
                mAudioFocusRequest = new AudioFocusRequest.Builder(AUDIO_FOCUS_GAIN_FLAG)
                        .setAudioAttributes(attr)
                        .setOnAudioFocusChangeListener(mFocusChangeListener)
                        .build();
            }
            rlt = mAudioManager.requestAudioFocus(mAudioFocusRequest);
        } else {
            rlt = mAudioManager.requestAudioFocus(mFocusChangeListener, AUDIO_STREAM_TYPE, AUDIO_FOCUS_GAIN_FLAG);
        }
        mHasAudioFocus = AudioManager.AUDIOFOCUS_REQUEST_GRANTED == rlt;
        KLog.i(TAG, String.format("requestAudioFocus() rlt: %s", rlt));
    }

    public void abandonAudioFocus() {
        if (!mHasAudioFocus) {
            return;
        }
        int rlt = Integer.MIN_VALUE;
        try {
            if (IS_OVER_ANDROID_O) {
                if (mAudioFocusRequest != null) {
                    rlt = mAudioManager.abandonAudioFocusRequest(mAudioFocusRequest);
                }
            } else {
                if (mFocusChangeListener != null) {
                    rlt = mAudioManager.abandonAudioFocus(mFocusChangeListener);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mAudioFocusRequest = null;
        }
        mHasAudioFocus = false;
        KLog.i(TAG, String.format("abandonAudioFewq ocus() rlt: %s", rlt));
    }

    private AudioManager.OnAudioFocusChangeListener mFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {

        @Override
        public void onAudioFocusChange(int focusChange) {
            KLog.i(TAG, String.format("onAudioFocusChange( focusChange: %s )", focusChange));
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    mHasAudioFocus = true;
                    NiceVideoPlayerManager.instance().resumeNiceVideoPlayer();
                    break;
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                    mHasAudioFocus = true;
                    break;
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE:
                    mHasAudioFocus = true;
                    break;
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                    mHasAudioFocus = true;
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    mHasAudioFocus = false;
                    NiceVideoPlayerManager.instance().pauseNiceVideoPlayer();
                    abandonAudioFocus();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    mHasAudioFocus = false;
                    NiceVideoPlayerManager.instance().pauseNiceVideoPlayer();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    mHasAudioFocus = false;
                    break;
            }
        }
    };

}