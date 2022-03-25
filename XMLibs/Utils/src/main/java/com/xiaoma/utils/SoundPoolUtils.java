package com.xiaoma.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.HashMap;

public class SoundPoolUtils implements SoundPool.OnLoadCompleteListener {
    private AudioManager mAudioManager;
    /**
     * SoundPool left volume
     */
    private static final float LEFT_VOLUME = 0.3f;

    /**
     * SoundPool right volume
     */
    private static final float RIGHT_VOLUME = 0.3f;

    /**
     * All sounds will have equal priority
     */
    private static final int STREAM_PRIORITY = 0;

    /**
     * Potential LOOP_MODE
     */
    private static final int MODE_NO_LOOP = 0;

    /**
     * Potential LOOP_MODE
     */
    @SuppressWarnings("unused")
    private static final int MODE_LOOP_FOREVER = -1;

    /**
     * Whether sounds should loop
     */
    private static final int LOOP_MODE = MODE_NO_LOOP;

    /**
     * SoundPool playback rate
     */
    private static final float PLAYBACK_RATE = 1.0f;

    private static final String TAG = "SoundManager";

    /**
     * Inner SoundManager instance
     */
    private static SoundPoolUtils sInstance = null;

    /**
     * Mapping of resource ids to sound ids returned by load()
     */
    private HashMap<Integer, Integer> mSoundMap = new HashMap<Integer, Integer>();

    /**
     * SoundPool instance
     */
    private SoundPool mSoundPool;

    /**
     * Application Context
     */
    private Context mContext;

    /**
     * Maximum concurrent streams that can play
     */
    private static final int MAX_STREAMS = 2;
    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int i) {

        }
    };

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable releaseTask = new Runnable() {
        @Override
        public void run() {
            releaseAudioFocus();
        }
    };

    /**
     * Private constructor for singleton
     */
    private SoundPoolUtils(Context context) {
        mContext = context.getApplicationContext();
        mSoundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_NOTIFICATION, 0);
        mSoundPool.setOnLoadCompleteListener(this);
    }

    /**
     * Static access to internal instance
     */
    public static SoundPoolUtils getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SoundPoolUtils(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Loads a sound. Called automatically by play() if not already loaded
     */
    public void load(int id) {
        mSoundMap.put(id, mSoundPool.load(mContext, id, 1));
    }

    /**
     * Test if sound is loaded, call with id from R.raw
     *
     * @param resourceId
     * @return true|false
     */
    public boolean isSoundLoaded(int resourceId) {
        return mSoundMap.containsKey(resourceId);
    }

    /**
     * Unload sound, prints warning if sound is not loaded
     */
    public void unload(int id) {
        if (mSoundMap.containsKey(id)) {
            int soundId = mSoundMap.remove(id);
            mSoundPool.unload(soundId);
        } else {
            Log.w(TAG, "sound: " + id + " is not loaded!");
        }
    }

    public void play(int resourceId) {
        if (isSoundLoaded(resourceId)) {
            mSoundPool.play(mSoundMap.get(resourceId), LEFT_VOLUME, RIGHT_VOLUME, STREAM_PRIORITY, LOOP_MODE, PLAYBACK_RATE);
        } else {
            load(resourceId);
        }
    }

    public void play(int resourceId, boolean isRequestFocus) {
        if (isRequestFocus) {
            requestAudioFocus();
        }
        play(resourceId);
    }

    public void playDelay(final int resourceId) {
        boolean b = requestAudioFocus();
        if (b) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    play(resourceId);
                    handler.postDelayed(releaseTask, 500);
                }
            }, 100);
        }
    }


    private boolean requestAudioFocus() {
        boolean isFocus = false;
        if (mAudioManager == null)
            mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        if (mAudioManager != null) {
            int i = mAudioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_NOTIFICATION,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
            isFocus = i == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        }
        return isFocus;
    }

    public void releaseAudioFocus() {
        if (mAudioManager != null) {
            mAudioManager.abandonAudioFocus(audioFocusChangeListener);
        }
    }


    /**
     * If the sound is being loaded for the first time, we should wait until it
     * is completely loaded to play it.
     */
    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
//        requestAudioFocus();
        mSoundPool.play(sampleId, LEFT_VOLUME, RIGHT_VOLUME, STREAM_PRIORITY, LOOP_MODE, PLAYBACK_RATE);
    }
}