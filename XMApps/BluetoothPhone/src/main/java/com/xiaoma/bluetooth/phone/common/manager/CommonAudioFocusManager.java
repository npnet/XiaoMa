package com.xiaoma.bluetooth.phone.common.manager;

import android.content.Context;
import android.media.AudioManager;
import com.xiaoma.vr.VrConfig;

/**
 * Created by qiuboxiang on 2019/8/23 16:55
 * Desc:
 */
public class CommonAudioFocusManager {

    private static CommonAudioFocusManager instance;
    private AudioManager audioManager;
    private boolean hasAudioFocus;

    public static CommonAudioFocusManager getInstance() {
        if (instance == null) {
            synchronized (CommonAudioFocusManager.class) {
                if (instance == null) {
                    instance = new CommonAudioFocusManager();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public void requestAudioFocus() {
        if (hasAudioFocus) {
            return;
        }
        int i = audioManager.requestAudioFocus(onAudioFocusChangeListener, VrConfig.IAT_STREAM_TYPE,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE);
        hasAudioFocus = i == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    public void abandonAudioFocus() {
        if (!hasAudioFocus) {
            return;
        }
        int i = audioManager.abandonAudioFocus(onAudioFocusChangeListener);
        hasAudioFocus = !(i == AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
    }

    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE:
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
                    hasAudioFocus = true;
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    hasAudioFocus = false;
                    break;
            }
        }
    };

}
