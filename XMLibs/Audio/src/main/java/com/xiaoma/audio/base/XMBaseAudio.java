package com.xiaoma.audio.base;

import android.content.Context;
import android.text.TextUtils;

import com.xiaoma.aidl.model.MusicInfo;
import com.xiaoma.aidl.model.MusicPlayStatus;
import com.xiaoma.audio.audio.XMAudioCenter;
import com.xiaoma.audio.listener.IMusicHandle;
import com.xiaoma.audio.model.MusicPlayMode;
import com.xiaoma.audio.model.QQMusicData;
import com.xiaoma.audio.processor.XMMusicCenter;

import cn.kuwo.base.bean.Music;

public abstract class XMBaseAudio {

    @MusicPlayStatus.Status
    private int playStatus = MusicPlayStatus.STOP;
    protected XMAudioCenter mXmAudioCenter;
    protected Context context;
    protected IMusicHandle mIMusicHandle;

    public XMBaseAudio(Context context, XMAudioCenter xmAudioCenter) {
        this.mXmAudioCenter = xmAudioCenter;
        this.context = context;
        this.mIMusicHandle = getIMusicHandle();
    }

    public @MusicPlayStatus.Status
    int getPlayStatus() {
        return playStatus;
    }

    public void setPlayStatus(@MusicPlayStatus.Status int playStatus) {
        this.playStatus = playStatus;
    }

    protected MusicInfo parseToMusicInfo(Music music, @MusicPlayStatus.Status int status) {
        MusicInfo musicInfo = new MusicInfo();
        if (!TextUtils.isEmpty(music.album)) {
            musicInfo.setAlbumName(music.album.replace("&nbsp;", " "));
        } else {
            musicInfo.setAlbumName("");
        }
        if (!TextUtils.isEmpty(music.name)) {
            musicInfo.setMusicName(music.name.replace("&nbsp;", " "));
        } else {
            musicInfo.setMusicName("");
        }

        if (!TextUtils.isEmpty(music.artist)) {
            musicInfo.setSinger(music.artist.replace("&nbsp;", " "));
        } else {
            musicInfo.setSinger("");
        }

//        musicInfo.setSinger(music.artist);
        musicInfo.setHasNextMusic(true);
        musicInfo.setHasPreMusic(true);
        musicInfo.setStatus(status);
        return musicInfo;
    }

    protected MusicInfo parseToMusicInfo(QQMusicData music) {
        MusicInfo musicInfo = new MusicInfo();
        return musicInfo;
    }

    protected XMMusicCenter getXMMusicControlCenter() {
        return mXmAudioCenter.getXMMusicCenter();
    }

    protected IMusicHandle getSystemLocalMusicHandle() {
        return getXMMusicControlCenter().getSystemLocalMusicHandle();
    }

    protected IMusicHandle getKWMusicHandle() {
        return getXMMusicControlCenter().getKWMusicHandle();
    }

    protected IMusicHandle getQQMusicHandle() {
        return getXMMusicControlCenter().getQQMusicHandle();
    }

    protected IMusicHandle getXTingMusicHandle() {
        return getXMMusicControlCenter().getXTingMusicHandle();
    }

    public abstract void init();

    public abstract IMusicHandle getIMusicHandle();

    public void release() {
        mIMusicHandle.release();
    }

    public void setPlayMode(MusicPlayMode musicPlayMode) {
        mIMusicHandle.setPlayMode(musicPlayMode);
    }

    public void setPlayModeRandom() {
        mIMusicHandle.setPlayModeRandom();
    }

    public void subscribeProgram() {
        mIMusicHandle.subscribeProgram();
    }

    public void unSubscribeProgram() {
        mIMusicHandle.unSubscribeProgram();
    }

    public void playNext() {
        mIMusicHandle.playNext();
    }

    public void playPre() {
        mIMusicHandle.playPre();
    }

    public void pause() {
        mIMusicHandle.pause();
    }

    public void play() {
        mIMusicHandle.play();
    }
}
