package com.xiaoma.shop.common.util;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * Created by LKF on 2019-6-25 0025.
 * 音频试听
 */
public class AudioAuditionHelper implements AudioManager.OnAudioFocusChangeListener {
    private static final String TAG = "AudioAuditionHelper";
    private static final int PLAY_DELAY = 800;
    private MediaPlayer mPlayer;
    private AudioManager mAudioManager;
    private String mPlayUrl;
    private PlayCallback mCallback;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public void init(Context context, String url, final PlayCallback callback) {
        mAudioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
        mPlayUrl = url;
        mCallback = callback;

        release();

        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stop();
            }
        });
        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (callback != null) {
                    callback.onError();
                }
                stop();
                return false;
            }
        });
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                start();
            }
        });
        try {
            mPlayer.setDataSource(context, Uri.parse(url));
            mPlayer.prepareAsync();
        } catch (Exception e) {
            callback.onError();
        }
    }

    public void release() {
        if (mPlayer == null) {
            return;
        }
        mHandler.removeCallbacksAndMessages(null);
        if (mPlayer.isPlaying()) {
            stop();
        }
        mCallback = null;
        try {
            mPlayer.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mPlayer = null;
    }

    private void start() {
        if (mPlayer != null) {
            int rlt = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == rlt) {
                try {
                    // 注意:主机那边有个坑,申请到焦点后必须延迟播放,不然声音出不来!!!
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mPlayer != null) {
                                mPlayer.start();
                                if (mCallback != null) {
                                    mCallback.onStart();
                                }
                            }

                        }
                    }, PLAY_DELAY);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (mCallback != null)
                        mCallback.onError();
                }
            } else {
                Log.e(TAG, String.format("start: { rlt: %s } Cannot gain audio focus !!!", rlt));
            }
        }
    }

    private void stop() {
        MediaPlayer mp = mPlayer;
        if (mp == null)
            return;
        mHandler.removeCallbacksAndMessages(null);
        if (mAudioManager != null) {
            mAudioManager.abandonAudioFocus(this);
        }
        try {
            mp.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mCallback != null) {
            mCallback.onStop();
        }
    }

    public boolean isPlaying() {
        MediaPlayer mp = mPlayer;
        return mp != null && mp.isPlaying();
    }

    public String getPlayUrl() {
        return mPlayUrl;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        Log.i(TAG, String.format("onAudioFocusChange: { focusChange: %s }", focusChange));
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                stop();
                release();
                break;
        }
    }

    public interface PlayCallback {
        void onStart();

        void onStop();

        void onError();
    }
}
