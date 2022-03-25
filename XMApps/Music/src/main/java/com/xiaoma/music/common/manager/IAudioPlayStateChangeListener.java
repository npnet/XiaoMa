package com.xiaoma.music.common.manager;

/**
 * Created by ZYao.
 * Date ：2018/10/31 0031
 */
public interface IAudioPlayStateChangeListener<T> {

    void onPreStart(T music);

    void onReadyPlay(T music);

    void onBufferStart();

    void onBufferFinish();

    void onPlay(T music);

    void onPause();

    void onProgressChange(long progressInMs, long totalInMs);

    void onPlayFailed(int errorCode, String errorMsg);

    void onPlayStop();

    void onPlayModeChanged(int playMode);

    void onDisconnect();

    void onBTSinkDisconnected();

    /**
     * 音乐播放完成
     */
    void onComplete();
}
