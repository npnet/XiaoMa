package com.xiaoma.audio.audio;

import android.content.Context;
import com.xiaoma.aidl.model.MusicInfo;
import com.xiaoma.aidl.model.MusicPlayStatus;
import com.xiaoma.audio.base.XMBaseAudio;
import com.xiaoma.audio.listener.IMusicHandle;
import com.xiaoma.audio.listener.IMusicSearchListener;
import com.xiaoma.audio.listener.IMusicStatusListener;
import com.xiaoma.audio.model.ForegroundAudioApp;
import java.util.List;

class SystemLocalMusicAudio extends XMBaseAudio {

    SystemLocalMusicAudio(Context context, XMAudioCenter xmAudioCenter) {
        super(context, xmAudioCenter);
    }

    @Override
    public void init() {
        initSystemLocalMusicListener();
    }

    @Override
    public IMusicHandle getIMusicHandle() {
        return getSystemLocalMusicHandle();
    }

    private void initSystemLocalMusicListener() {
        mIMusicHandle.registerListener(new IMusicStatusListener<MusicInfo>() {
            @Override
            public void onInit() {
                setPlayStatus(MusicPlayStatus.INIT);
                mXmAudioCenter.updateCurrentAudio(SystemLocalMusicAudio.this);
            }

            @Override
            public void onExit() {
                setPlayStatus(MusicPlayStatus.EXIT);
            }

            @Override
            public void onPlaying(MusicInfo musicInfo) {
                setPlayStatus(MusicPlayStatus.PLAYING);
                mXmAudioCenter.updateCurrentAudio(SystemLocalMusicAudio.this);
                mXmAudioCenter.onPlayStatusChanged(ForegroundAudioApp.SystemLocalMusic, musicInfo);
            }

            @Override
            public void onBuffering(MusicInfo musicInfo) {
                setPlayStatus(MusicPlayStatus.BUFFERING);
                mXmAudioCenter.updateCurrentAudio(SystemLocalMusicAudio.this);
                mXmAudioCenter.onPlayStatusChanged(ForegroundAudioApp.SystemLocalMusic, musicInfo);
            }

            @Override
            public void onPause(MusicInfo musicInfo) {
                setPlayStatus(MusicPlayStatus.PAUSE);
                mXmAudioCenter.onPlayStatusChanged(ForegroundAudioApp.SystemLocalMusic, musicInfo);
            }

            @Override
            public void onStop(MusicInfo musicInfo) {
                setPlayStatus(MusicPlayStatus.STOP);
                mXmAudioCenter.onPlayStatusChanged(ForegroundAudioApp.SystemLocalMusic, musicInfo);
            }
        });
    }

    public void searchMusicInfo(String singer, String song, String album) {
        mIMusicHandle.searchMusicInfo(singer, song, album, new IMusicSearchListener<MusicInfo>() {
            @Override
            public void musicSearchFinished(boolean searchStatus, List<MusicInfo> list) {
                //...
            }
        });
    }

}
