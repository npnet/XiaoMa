package com.xiaoma.audio.processor;

import android.content.Context;
import android.content.Intent;

import com.xiaoma.aidl.model.MusicInfo;
import com.xiaoma.aidl.model.MusicPlayStatus;
import com.xiaoma.audio.listener.IMusicHandle;
import com.xiaoma.audio.listener.IMusicSearchListener;
import com.xiaoma.audio.listener.IMusicStatusListener;
import com.xiaoma.audio.model.MusicPlayMode;
import com.xiaoma.process.listener.IPlayInfoChangeListener;
import com.xiaoma.process.manager.XMApi;

class XTingMusicHandle implements IMusicHandle<IMusicStatusListener<MusicInfo>, IMusicSearchListener<MusicInfo>> {

    private Context context;
    private IMusicStatusListener<MusicInfo> mIMusicStatusListener;

    public XTingMusicHandle(Context context) {
        this.context = context;
    }

    @Override
    public void init() {
        addPlayInfoChangeListen();
        connectXTingService();
    }

    @Override
    public void release() {
        removePlayInfoChangeListen();
        removeListener(null);
        unbindXTingService();
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
    public void setPlayMode(MusicPlayMode musicPlayMode) {
        switch (musicPlayMode) {
            case MEDIA_CIRCLE :
                //XMApi.getInstance().getXMXTingApiManager().setPlayMode(mode);
                break;
            case MEDIA_ONE :
                //XMApi.getInstance().getXMXTingApiManager().setPlayMode(mode);
                break;
            case MEDIA_ORDER :
                //XMApi.getInstance().getXMXTingApiManager().setPlayMode(mode);
                break;
            case MEDIA_RANDOM :
                //XMApi.getInstance().getXMXTingApiManager().setPlayMode(mode);
                break;
        }
    }

    @Override
    public void setPlayModeRandom(){
        XMApi.getInstance().getXMXTingApiManager().setPlayModeRandom();
    }

    public void playNext() {
        XMApi.getInstance().getXMXTingApiManager().playNextMusic();
    }

    public void playPre() {
        XMApi.getInstance().getXMXTingApiManager().playPreMusic();
    }

    public void play() {
        XMApi.getInstance().getXMXTingApiManager().switchPlay(true);
    }

    public void pause() {
        XMApi.getInstance().getXMXTingApiManager().switchPlay(false);
    }

    @Override
    public void registerListener(IMusicStatusListener<MusicInfo> listener) {
        if (listener != null) {
            mIMusicStatusListener = listener;
        }
    }

    @Override
    public void removeListener(IMusicStatusListener<MusicInfo> listener) {
        mIMusicStatusListener = null;
    }

    @Override
    public void searchMusicInfo(String singer, String song, String album, IMusicSearchListener<MusicInfo> listener) {
        //...
    }

    @Override
    public void subscribeProgram(){
        XMApi.getInstance().getXMXTingApiManager().subscribeProgram();
    }

    @Override
    public void unSubscribeProgram(){
        XMApi.getInstance().getXMXTingApiManager().unSubscribeProgram();
    }

    //发送广播 通知考拉这一次恢复焦点的时候，不需要自动恢复播放状态
    private void sendBroadcastToKlNotResumePlay() {
        Intent intent = new Intent("com.kaolafm.mediaplayer.resumePlay");
        intent.putExtra("resume_play", false);
        context.sendBroadcast(intent);
    }

    public void getCurrPlayingMusicName() {
        XMApi.getInstance().getXMXTingApiManager().getCurrPlayingMusicName();
    }

    public void unbindXTingService() {
        XMApi.getInstance().getXMXTingApiManager().unBindService();
    }

    public boolean connectXTingService() {
        return XMApi.getInstance().getXMXTingApiManager().bindService();
    }

    private IPlayInfoChangeListener mIPlayInfoChangeListener = new IPlayInfoChangeListener() {

        @Override
        public void onPlayStatusChange(MusicInfo musicInfo) {
            if (musicInfo == null || mIMusicStatusListener == null) {
                return;
            }
            @MusicPlayStatus.Status int status = musicInfo.getStatus();
            switch (status) {
                case MusicPlayStatus.PLAYING:
                    mIMusicStatusListener.onPlaying(musicInfo);
                    break;
                case MusicPlayStatus.BUFFERING:
                    mIMusicStatusListener.onBuffering(musicInfo);
                    break;
                case MusicPlayStatus.EXIT:
                    mIMusicStatusListener.onExit();
                    break;
                case MusicPlayStatus.INIT:
                    mIMusicStatusListener.onInit();
                    break;
                case MusicPlayStatus.PAUSE:
                    mIMusicStatusListener.onPause(musicInfo);
                    break;
                case MusicPlayStatus.STOP:
                    mIMusicStatusListener.onStop(musicInfo);
                    break;
            }
        }
    };

    private void addPlayInfoChangeListen() {
        XMApi.getInstance().getXMXTingApiManager().addPlayInfoChangeListen(mIPlayInfoChangeListener);
    }

    private void removePlayInfoChangeListen() {
        XMApi.getInstance().getXMXTingApiManager().removePlayInfoChangeListen(mIPlayInfoChangeListener);
    }

}
