package com.xiaoma.music.kuwo.listener;

import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.kuwo.observer.KuwoPlayControlObserver;

/**
 * Created by ZYao.
 * Date ：2018/10/16 0016
 */
public interface OnPlayControlListener {

    void onPreStart(XMMusic music);

    void onReadyPlay(XMMusic music);

    void onBufferStart();

    void onBufferFinish();

    void onPlay(XMMusic music);

    void onSeekSuccess(int position);

    void onPause();

    void onProgressChange(long progressInMs, long totalInMs);

    void onPlayFailed(@KuwoPlayControlObserver.ErrorCode int errorCode, String errorMsg);

    void onPlayStop();

    void onPlayModeChanged(int playMode);

    void onCurrentPlayListChanged();
}
