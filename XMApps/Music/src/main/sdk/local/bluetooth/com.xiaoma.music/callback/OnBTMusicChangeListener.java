package com.xiaoma.music.callback;

import com.xiaoma.music.model.BTMusic;

/**
 * Created by ZYao.
 * Date ：2018/10/23 0023
 */
public interface OnBTMusicChangeListener {
    void currentBtMusic(BTMusic btMusic);

    void onPlay();

    void onPause();

    void onProgressChange(long progressInMs, long totalInMs);

    void onPlayFailed(int errorCode);

    void onPlayStop();
}
