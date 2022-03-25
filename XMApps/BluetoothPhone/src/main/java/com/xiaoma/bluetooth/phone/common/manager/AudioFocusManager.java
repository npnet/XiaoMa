package com.xiaoma.bluetooth.phone.common.manager;

import android.content.Context;
import android.media.AudioManager;

import com.xiaoma.utils.log.KLog;

/**
 * Created by qiuboxiang on 2019/6/11 15:24
 * Desc:
 */
public class AudioFocusManager {

    private static final String TAG = AudioFocusManager.class.getSimpleName();
    private static final int AUDIO_FOCUS_GAIN_FLAG = AudioManager.AUDIOFOCUS_GAIN_TRANSIENT;
    private static final int AUDIO_STREAM_TYPE = AudioManager.STREAM_BLUETOOTH_SCO;
    private AudioManager mAudioManager;
    private static AudioFocusManager instance;
    private boolean hasAudioFocus;

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

    public void requestAudioFocus() {
        if (hasAudioFocus) {
            return;
        }
        hasAudioFocus = true;
        try {
            KLog.d("BluetoothReceiverManager","请求音频焦点");
            mAudioManager.requestAudioFocusForCall(AUDIO_STREAM_TYPE, AUDIO_FOCUS_GAIN_FLAG);
        } catch (Exception e) {
            e.printStackTrace();
        }
        KLog.i(TAG, "requestAudioFocusForCall");
    }

    public void abandonAudioFocus() {
        if (!hasAudioFocus) {
            return;
        }
        hasAudioFocus = false;
        try {
            KLog.d("BluetoothReceiverManager","放弃音频焦点");
            mAudioManager.abandonAudioFocusForCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
        KLog.i(TAG, "abandonAudioFocusForCall");
    }
}
