/**
 * Copyright (C) 2018 The Android Open Source Project
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xiaoma.xting.sdk;

import android.annotation.NonNull;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;

import java.util.Objects;

/**
 * Manages radio's audio stream.
 */
public class AudioStreamController {
    private static final String TAG = "BcRadioApp.AudioSCntrl";

    private final Object mLock = new Object();
    private final AudioManager mAudioManager;
    private final CarAudioHandler mCarAudioHandler;
    private final AudioFocusRequest mGainFocusReq;
    private RadioAudioFocusChangeListener mFocusChangeListener = null;
    private boolean mHasSomeFocus = false;
    // private boolean mIsTuning = false;
    // private int mCurrentPlaybackState = PlaybackStateCompat.STATE_NONE;

    public AudioStreamController(
            @NonNull Context context,
            @NonNull CarAudioHandler carAudioHandler) {

        if (mFocusChangeListener == null) {
            mFocusChangeListener = new RadioAudioFocusChangeListener();
        }
        // 获取系统的音频管理器
        mAudioManager = Objects.requireNonNull((AudioManager) context.getSystemService(Context.AUDIO_SERVICE));
        // 获取车辆的音频管理器
        mCarAudioHandler = Objects.requireNonNull(carAudioHandler);

        // 申请音频焦点的参数
        AudioAttributes playbackAttr = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setLegacyStreamType(AudioManager.STREAM_RADIO)
                .build();

        // 申请音频焦点对象
        mGainFocusReq = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(playbackAttr)
                .setAcceptsDelayedFocusGain(true)
                .setWillPauseWhenDucked(true)
                .setOnAudioFocusChangeListener(mFocusChangeListener)
                .build();
    }

    // public void notifyProgramInfoChanged() {
    //     synchronized (mLock) {
    //         if (!mIsTuning) {
    //             return;
    //         }
    //         mIsTuning = false;
    //         // notifyPlaybackStateChangedLocked(PlaybackStateCompat.STATE_PLAYING);
    //     }
    // }


    // public boolean preparePlayback(Optional<Boolean> skipDirectionNext) {
    //     synchronized (mLock) {
    //         if (!requestAudioFocusLocked()) {
    //             // 没有获取到焦点
    //             return false;
    //         }
    //
    //         // 保存一下播放状态
    //         int state = PlaybackStateCompat.STATE_CONNECTING;
    //
    //         if (skipDirectionNext.isPresent()) {
    //             // 如果存在值
    //             state = skipDirectionNext.get()
    //                     ? PlaybackStateCompat.STATE_SKIPPING_TO_NEXT
    //                     : PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS;
    //         }
    //         // 回调当前的状态
    //         // notifyPlaybackStateChangedLocked(state);
    //         // mIsTuning = true;
    //         return true;
    //     }
    // }

    public boolean requestMuted(boolean muted) {
        synchronized (mLock) {
            if (muted) {
                // 请求静音
                // notifyPlaybackStateChangedLocked(PlaybackStateCompat.STATE_STOPPED);
                return abandonAudioFocusLocked();
            } else {
                // 请求恢复播放
                return requestAudioFocusLocked();
            }
        }
    }

    public boolean isMuted() {
        return !mHasSomeFocus;
    }


    private void onAudioFocusChange(int focusChange) {
        synchronized (mLock) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    mHasSomeFocus = true;
                    mCarAudioHandler.setMuted(false);
                    YqSDK.getInstance().openRadio();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    mHasSomeFocus = false;
                    YqSDK.getInstance().closeRadio();
                    // notifyPlaybackStateChangedLocked(PlaybackStateCompat.STATE_STOPPED);
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    mHasSomeFocus = false;
                    mCarAudioHandler.setMuted(true);
//                    YqSDK.getInstance().closeRadio();
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    mCarAudioHandler.setMuted(true);
                    break;
                default:
            }
        }
    }

//    private void notifyPlaybackStateChangedLocked(@PlaybackStateCompat.State int state) {
//        if (mCurrentPlaybackState == state) {
//            return;
//        }
//        mCurrentPlaybackState = state;
//    }

    private boolean requestAudioFocusLocked() {
        if (mHasSomeFocus) {
            return true;
        }
        int res = mAudioManager.requestAudioFocus(mGainFocusReq);
        if (res == AudioManager.AUDIOFOCUS_REQUEST_DELAYED) {
            mHasSomeFocus = true;
            return true;
        }
        if (res != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return false;
        }
        mHasSomeFocus = true;
        return mCarAudioHandler.setMuted(false);
    }

    private boolean abandonAudioFocusLocked() {
        if (!mCarAudioHandler.setMuted(true)) {
            return false;
        }

        int res = mAudioManager.abandonAudioFocusRequest(mGainFocusReq);
        if (res != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return false;
        }
        mHasSomeFocus = false;
        return true;
    }

    private class RadioAudioFocusChangeListener implements AudioManager.OnAudioFocusChangeListener {
        @Override
        public void onAudioFocusChange(int focusChange) {
            AudioStreamController.this.onAudioFocusChange(focusChange);
        }
    }
}
