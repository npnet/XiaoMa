package com.xiaoma.audio.processor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.xiaoma.aidl.model.MusicInfo;
import com.xiaoma.aidl.model.MusicPlayStatus;
import com.xiaoma.audio.constants.XMMusicConstants;
import com.xiaoma.audio.listener.IMusicHandle;
import com.xiaoma.audio.listener.IMusicSearchListener;
import com.xiaoma.audio.listener.IMusicStatusListener;
import com.xiaoma.audio.model.MusicPlayMode;

class SystemLocalMusicHandle implements IMusicHandle<IMusicStatusListener<MusicInfo>, IMusicSearchListener<MusicInfo>> {

    private IMusicStatusListener<MusicInfo> mMusicIMusicStatusListener;
    private Context context;
    private MusicInfo musicInfo = new MusicInfo();

    SystemLocalMusicHandle(Context context) {
        this.context = context;
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(XMMusicConstants.SYSTEM_LOCAL_MUSIC_PLAY_STATUS);
        context.registerReceiver(receiver, filter);
    }

    private void unregisterReceiver() {
        context.unregisterReceiver(receiver);
    }

    private MusicInfo parse(Intent intent) {
        if (intent == null) {
            return null;
        }
        String musicName = intent.hasExtra(XMMusicConstants.MUSIC_NAME) ? intent.getStringExtra(XMMusicConstants.MUSIC_NAME) : "";
        String musicArtist = intent.hasExtra(XMMusicConstants.MUSIC_ARTIST) ? intent.getStringExtra(XMMusicConstants.MUSIC_ARTIST) : "";
        String musicAlbum = intent.hasExtra(XMMusicConstants.MUSIC_ALBUM) ? intent.getStringExtra(XMMusicConstants.MUSIC_ALBUM) : "";
        int musicPlayStatus = intent.hasExtra(XMMusicConstants.MUSIC_PLAY_STATUS) ? intent.getIntExtra(XMMusicConstants.MUSIC_PLAY_STATUS, -1) : -1;
        boolean musicHasPre = intent.hasExtra(XMMusicConstants.MUSIC_HAS_PRE) && intent.getBooleanExtra(XMMusicConstants.MUSIC_HAS_PRE, false);
        boolean musicHasNext = intent.hasExtra(XMMusicConstants.MUSIC_HAS_NEXT) && intent.getBooleanExtra(XMMusicConstants.MUSIC_HAS_NEXT, false);
        musicInfo.setMusicName(musicName);
        musicInfo.setSinger(musicArtist);
        musicInfo.setAlbumName(musicAlbum);
        musicInfo.setStatus(musicInfo.parseInt(musicPlayStatus));
        musicInfo.setHasPreMusic(musicHasPre);
        musicInfo.setHasNextMusic(musicHasNext);
        return musicInfo;
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            MusicInfo music = parse(intent);
            @MusicPlayStatus.Status int status = music.getStatus();
            switch (status) {
                case MusicPlayStatus.INIT:
                    mMusicIMusicStatusListener.onInit();
                    break;
                case MusicPlayStatus.PLAYING:
                    mMusicIMusicStatusListener.onPlaying(music);
                    break;
                case MusicPlayStatus.BUFFERING:
                    mMusicIMusicStatusListener.onBuffering(music);
                    break;
                case MusicPlayStatus.PAUSE:
                    mMusicIMusicStatusListener.onPause(music);
                    break;
                case MusicPlayStatus.STOP:
                    mMusicIMusicStatusListener.onStop(music);
                    break;
                case MusicPlayStatus.EXIT:
                    mMusicIMusicStatusListener.onExit();
                    release();
                    break;
            }
        }
    };

    @Override
    public void init() {
        registerReceiver();
    }

    @Override
    public void startApp() {
        //...
    }

    @Override
    public void exitApp() {
        //...
    }

    @Override
    public void startWithKeyword(String keyword) {
        //...
    }

    @Override
    public void playNext() {
        //...
    }

    @Override
    public void playPre() {
        //...
    }

    @Override
    public void pause() {
        //...
    }

    @Override
    public void play() {
        //...
    }

    @Override
    public void release() {
        removeListener(null);
        unregisterReceiver();
    }

    @Override
    public void setPlayMode(MusicPlayMode musicPlayMode) {
        //...
    }

    @Override
    public void setPlayModeRandom() {
        //...
    }

    @Override
    public void subscribeProgram() {
        //...
    }

    @Override
    public void unSubscribeProgram() {
        //...
    }

    @Override
    public void registerListener(IMusicStatusListener<MusicInfo> listener) {
        if (listener != null) {
            mMusicIMusicStatusListener = listener;
        }
    }

    @Override
    public void removeListener(IMusicStatusListener<MusicInfo> musicInfoIMusicStatusListener) {
        mMusicIMusicStatusListener = null;
    }

    @Override
    public void searchMusicInfo(String singer, String song, String album, IMusicSearchListener<MusicInfo> listener) {
        //...
    }

}
