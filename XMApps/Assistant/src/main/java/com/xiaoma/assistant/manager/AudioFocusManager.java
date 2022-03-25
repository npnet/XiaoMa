package com.xiaoma.assistant.manager;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.VrConfig;
import com.xiaoma.vr.tts.OnTtsListener;
import com.xiaoma.vrfactory.tts.XmTtsManager;

/**
 * Created by qiuboxiang on 2019/4/22 20:52
 * Desc:
 */
public class AudioFocusManager {

    private static AudioFocusManager mInstance;
    private AudioManager audioManager;
    private boolean hasAudioFocus;
    private Context context;
    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            KLog.d("AudioFocusManager onAudioFocusChange : " + focusChange);
        }
    };

    private AudioFocusManager(Context context) {
        this.context = context;
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public static AudioFocusManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (AudioFocusManager.class) {
                if (mInstance == null) {
                    mInstance = new AudioFocusManager(context);
                }
            }
        }
        return mInstance;
    }

    public boolean requestAudioFocus() {
        int i = audioManager.requestAudioFocus(onAudioFocusChangeListener, VrConfig.TTS_STREAM_TYPE, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
        hasAudioFocus = i == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        return hasAudioFocus;
    }

    public void abandonAudioFocus() {
        if (hasAudioFocus) {
            hasAudioFocus = false;
            audioManager.abandonAudioFocus(onAudioFocusChangeListener);
        }
    }

}
