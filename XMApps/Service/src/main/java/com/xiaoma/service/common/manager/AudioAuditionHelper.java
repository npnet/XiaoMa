package com.xiaoma.service.common.manager;

import android.app.Service;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.log.KLog;

import java.io.IOException;

/**
 * Created by LKF on 2019-6-25 0025.
 * 音频试听
 */
public class AudioAuditionHelper implements AudioManager.OnAudioFocusChangeListener {
    private static final String TAG = "AudioAuditionHelper";
    private MediaPlayer mPlayer;
    private AudioManager mAudioManager;
    private String mPlayUrl;
    private PlayCallback mCallback;
    private static final int AUDIO_FOCUS_GAIN_FLAG = AudioManager.AUDIOFOCUS_GAIN_TRANSIENT;

    private int currentStreamType;

    private AudioAuditionHelper() {

    }


    private static class InstanceHolder {
        static final AudioAuditionHelper sInstance = new AudioAuditionHelper();
    }

    public static AudioAuditionHelper getInstance() {
        return AudioAuditionHelper.InstanceHolder.sInstance;
    }

    public void init(Context context, String url, final PlayCallback callback, final int streamType) {
        if (mPlayer != null) {
            release();
        }

        currentStreamType = streamType;

//        MediaPlayer mp = MediaPlayer.create(context, Uri.parse(url)); // 同步快速点击造成主线程阻塞
        MediaPlayer mp = new MediaPlayer();
        if (mp == null) {//车机测试出现 mp为null的情况
            callback.onError();
            return;
        }
        mp.setAudioAttributes(new AudioAttributes.Builder()
                .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build());
        try {
            mp.setDataSource(context, Uri.parse(url));
            mp.prepareAsync();
        } catch (IOException e) {
            callback.onError();
        }
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (callback != null)
                    callback.onStop();
            }
        });
        mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (callback != null) {
                    callback.onError();
                }
                return false;
            }
        });
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                start();
            }
        });

        mPlayer = mp;
        mAudioManager = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
        mPlayUrl = url;
        mCallback = callback;
    }

    public void release() {
        stop();
        if (mAudioManager != null) {
            mAudioManager.abandonAudioFocusForCall();
        }
        mCallback = null;
        MediaPlayer mp = mPlayer;
        if (mp != null) {
            try {
                mp.release();
                KLog.e(TAG, "release");
            } catch (Exception e) {
                e.printStackTrace();
                KLog.e(TAG, "release" + e.getMessage());
            }
        }
        mPlayer = null;
    }

    public void start() {
        final MediaPlayer mp = mPlayer;
        if (mp != null) {
            mAudioManager.requestAudioFocus(this, AudioManager.STREAM_NOTIFICATION, AUDIO_FOCUS_GAIN_FLAG);
            KLog.i(TAG, "requestAudioFocus");
            ThreadDispatcher.getDispatcher().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        mp.start();
                        mp.setLooping(true);
                        KLog.e(TAG, "start");
                        if (mCallback != null)
                            mCallback.onStart();
                    } catch (Exception e) {
                        e.printStackTrace();
                        KLog.e(TAG, e.getMessage());
                        if (mCallback != null)
                            mCallback.onError();
                    }
                }
            }, 100);


        }
    }

    public void stop() {
        MediaPlayer mp = mPlayer;
        if (mp != null) {
            try {
                mp.stop();
                KLog.e(TAG, "stop");
                mAudioManager.abandonAudioFocus(this);
            } catch (Exception e) {
                e.printStackTrace();
                KLog.e(TAG, "stop" + e.getMessage());
            }
        }
        if (mCallback != null) {
            mCallback.onStop();
        }
    }

    public void requestAudioForCall() {
        mAudioManager.requestAudioFocusForCall(currentStreamType, AUDIO_FOCUS_GAIN_FLAG);
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
                mAudioManager.requestAudioFocus(this, AudioManager.STREAM_NOTIFICATION, AUDIO_FOCUS_GAIN_FLAG);
                break;
        }
    }

    public interface PlayCallback {
        void onStart();

        void onStop();

        void onError();
    }
}
