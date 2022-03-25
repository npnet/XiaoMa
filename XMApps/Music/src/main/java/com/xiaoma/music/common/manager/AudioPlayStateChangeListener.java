package com.xiaoma.music.common.manager;

/**
 * @author zs
 * @date 2018/11/12 0012.
 */
public abstract class  AudioPlayStateChangeListener<T> implements IAudioPlayStateChangeListener<T>{

    @Override
    public void onPreStart(T music) {

    }

    @Override
    public void onReadyPlay(T music) {

    }

    @Override
    public void onBufferStart() {

    }

    @Override
    public void onBufferFinish() {

    }

    @Override
    public void onPlay(T music) {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onProgressChange(long progressInMs, long totalInMs) {

    }

    @Override
    public void onPlayFailed(int errorCode, String errorMsg) {

    }

    @Override
    public void onPlayStop() {

    }

    @Override
    public void onPlayModeChanged(int playMode) {

    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onBTSinkDisconnected() {

    }

    @Override
    public void onComplete() {

    }
}
