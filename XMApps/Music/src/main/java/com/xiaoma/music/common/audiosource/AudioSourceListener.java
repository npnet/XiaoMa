package com.xiaoma.music.common.audiosource;

/**
 * Created by LKF on 2018-12-21 0021.
 */
public interface AudioSourceListener {
    /**
     * 音源切换回调
     *
     * @param preAudioSource  上一个音源
     * @param currAudioSource 当前音源
     */
    void onAudioSourceSwitch(@AudioSource int preAudioSource, @AudioSource int currAudioSource);
}