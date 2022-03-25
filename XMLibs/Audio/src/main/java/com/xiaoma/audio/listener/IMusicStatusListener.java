package com.xiaoma.audio.listener;

public interface IMusicStatusListener<MUSIC_INFO> {

    void onInit();

    void onExit();

    void onPlaying(MUSIC_INFO musicInfo);

    void onBuffering(MUSIC_INFO musicInfo);

    void onPause(MUSIC_INFO musicInfo);

    void onStop(MUSIC_INFO musicInfo);

}
