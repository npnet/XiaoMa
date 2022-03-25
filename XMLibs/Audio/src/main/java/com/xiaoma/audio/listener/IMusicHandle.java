package com.xiaoma.audio.listener;

import com.xiaoma.audio.model.MusicPlayMode;

public interface IMusicHandle<LISTENER extends IMusicStatusListener, MUSIC_SEARCH_LISTENER extends IMusicSearchListener> {

    void init();

    void release();

    void startApp();

    void exitApp();

    void playNext();

    void playPre();

    void pause();

    void play();

    void startWithKeyword(String keyword);

    void setPlayMode(MusicPlayMode musicPlayMode);

    void setPlayModeRandom();

    void subscribeProgram();

    void unSubscribeProgram();

    void registerListener(LISTENER listener);

    void removeListener(LISTENER listener);

    void searchMusicInfo(String singer, String song, String album, MUSIC_SEARCH_LISTENER listener);
}
