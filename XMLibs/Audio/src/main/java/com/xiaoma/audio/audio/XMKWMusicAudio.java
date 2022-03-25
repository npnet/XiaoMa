package com.xiaoma.audio.audio;

import android.content.Context;
import com.xiaoma.aidl.model.MusicPlayStatus;
import com.xiaoma.audio.base.XMBaseAudio;
import com.xiaoma.audio.listener.IMusicHandle;
import com.xiaoma.audio.listener.IMusicSearchListener;
import com.xiaoma.audio.listener.IMusicStatusListener;
import com.xiaoma.audio.model.ForegroundAudioApp;
import java.util.List;
import cn.kuwo.base.bean.Music;

class XMKWMusicAudio extends XMBaseAudio {

    public XMKWMusicAudio(Context context, XMAudioCenter xmAudioCenter) {
        super(context, xmAudioCenter);
    }

    @Override
    public void init() {
        initKWListener();
    }

    @Override
    public IMusicHandle getIMusicHandle() {
        return getKWMusicHandle();
    }

    private void initKWListener() {
        mIMusicHandle.registerListener(new IMusicStatusListener<Music>() {
            @Override
            public void onBuffering(Music musicData) {
                setPlayStatus(MusicPlayStatus.BUFFERING);
                mXmAudioCenter.updateCurrentAudio(XMKWMusicAudio.this);
                mXmAudioCenter.onPlayStatusChanged(ForegroundAudioApp.KWMusic, parseToMusicInfo(musicData, MusicPlayStatus.BUFFERING));
            }

            @Override
            public void onPlaying(Music musicData) {
                setPlayStatus(MusicPlayStatus.PLAYING);
                mXmAudioCenter.updateCurrentAudio(XMKWMusicAudio.this);
                mXmAudioCenter.onPlayStatusChanged(ForegroundAudioApp.KWMusic, parseToMusicInfo(musicData, MusicPlayStatus.PLAYING));
            }

            @Override
            public void onPause(Music musicData) {
                setPlayStatus(MusicPlayStatus.PAUSE);
                mXmAudioCenter.onPlayStatusChanged(ForegroundAudioApp.KWMusic, parseToMusicInfo(musicData, MusicPlayStatus.PAUSE));
            }

            @Override
            public void onStop(Music musicData) {
                setPlayStatus(MusicPlayStatus.STOP);
                mXmAudioCenter.onPlayStatusChanged(ForegroundAudioApp.KWMusic, parseToMusicInfo(musicData, MusicPlayStatus.STOP));
            }

            @Override
            public void onInit() {
                setPlayStatus(MusicPlayStatus.INIT);
                mXmAudioCenter.updateCurrentAudio(XMKWMusicAudio.this);
            }

            @Override
            public void onExit() {
                setPlayStatus(MusicPlayStatus.EXIT);
            }
        });
    }

    public void searchMusicInfo(String singer, String song, String album) {
        mIMusicHandle.searchMusicInfo(singer, song, album, new IMusicSearchListener<Music>() {
            @Override
            public void musicSearchFinished(boolean searchStatus, List<Music> list) {
                //...
            }
        });
    }

}
