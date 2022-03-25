package com.xiaoma.audio.audio;

import android.content.Context;
import com.xiaoma.aidl.model.MusicPlayStatus;
import com.xiaoma.audio.base.XMBaseAudio;
import com.xiaoma.audio.listener.IMusicHandle;
import com.xiaoma.audio.listener.IMusicSearchListener;
import com.xiaoma.audio.listener.IMusicStatusListener;
import com.xiaoma.audio.model.ForegroundAudioApp;
import com.xiaoma.audio.model.QQMusicData;
import java.util.List;

class XMQQMusicAudio extends XMBaseAudio {

    XMQQMusicAudio(Context context, XMAudioCenter xmAudioCenter) {
        super(context, xmAudioCenter);
    }

    @Override
    public void init() {
        initQQMusicListener();
    }

    @Override
    public IMusicHandle getIMusicHandle() {
        return getQQMusicHandle();
    }

    private void initQQMusicListener() {
        mIMusicHandle.registerListener(new IMusicStatusListener<QQMusicData>() {
            @Override
            public void onBuffering(QQMusicData musicData) {
                setPlayStatus(MusicPlayStatus.BUFFERING);
                mXmAudioCenter.updateCurrentAudio(XMQQMusicAudio.this);
                mXmAudioCenter.onPlayStatusChanged(ForegroundAudioApp.QQMusic, parseToMusicInfo(musicData));
            }

            @Override
            public void onPlaying(QQMusicData musicData) {
                setPlayStatus(MusicPlayStatus.PLAYING);
                mXmAudioCenter.updateCurrentAudio(XMQQMusicAudio.this);
                mXmAudioCenter.onPlayStatusChanged(ForegroundAudioApp.QQMusic, parseToMusicInfo(musicData));
            }

            @Override
            public void onPause(QQMusicData musicData) {
                setPlayStatus(MusicPlayStatus.PAUSE);
                mXmAudioCenter.onPlayStatusChanged(ForegroundAudioApp.QQMusic, parseToMusicInfo(musicData));
            }

            @Override
            public void onStop(QQMusicData musicData) {
                setPlayStatus(MusicPlayStatus.STOP);
                mXmAudioCenter.onPlayStatusChanged(ForegroundAudioApp.QQMusic, parseToMusicInfo(musicData));
            }

            @Override
            public void onInit() {
                setPlayStatus(MusicPlayStatus.INIT);
                mXmAudioCenter.updateCurrentAudio(XMQQMusicAudio.this);
            }

            @Override
            public void onExit() {
                setPlayStatus(MusicPlayStatus.EXIT);
            }
        });
    }

    public void searchMusicInfo(String singer, String song, String album) {
        mIMusicHandle.searchMusicInfo(singer, song, album, new IMusicSearchListener<QQMusicData>() {
            @Override
            public void musicSearchFinished(boolean searchStatus, List<QQMusicData> list) {
                //...
            }
        });
    }

}
