package com.xiaoma.audio.processor;

import android.content.Context;

import com.xiaoma.audio.listener.IMusicHandle;
import com.xiaoma.audio.listener.IMusicSearchListener;
import com.xiaoma.audio.listener.IMusicStatusListener;
import com.xiaoma.audio.model.MusicPlayMode;

import java.util.List;
import java.util.Random;

import cn.kuwo.autosdk.api.KWAPI;
import cn.kuwo.autosdk.api.OnPlayerStatusListener;
import cn.kuwo.autosdk.api.OnSearchListener;
import cn.kuwo.autosdk.api.PlayMode;
import cn.kuwo.autosdk.api.PlayState;
import cn.kuwo.autosdk.api.PlayerStatus;
import cn.kuwo.autosdk.api.SearchStatus;
import cn.kuwo.base.bean.Music;

public class KWMusicCarHandle implements IMusicHandle<IMusicStatusListener<Music>, IMusicSearchListener<Music>> {

    private static final String KUWO_KEY = "xiaomalixing_YiqibentengX40";
    private KWAPI kwapi;
    private IMusicStatusListener<Music> mMusicIMusicStatusListener;
    private Context context;

    KWMusicCarHandle(Context context) {
        this.context = context;
    }

    private OnPlayerStatusListener mOnPlayerStatusListener = new OnPlayerStatusListener() {
        @Override
        public void onPlayerStatus(PlayerStatus playerStatus, Music music) {
            if (music == null || mMusicIMusicStatusListener == null) {
                return;
            }
            if (playerStatus == PlayerStatus.BUFFERING) {
                mMusicIMusicStatusListener.onBuffering(music);
            } else if (playerStatus == PlayerStatus.PLAYING) {
                mMusicIMusicStatusListener.onPlaying(music);
            } else if (playerStatus == PlayerStatus.PAUSE) {
                mMusicIMusicStatusListener.onPause(music);
            } else if (playerStatus == PlayerStatus.STOP) {
                mMusicIMusicStatusListener.onStop(music);
            } else if (playerStatus == PlayerStatus.INIT) {
                mMusicIMusicStatusListener.onInit();
            }
        }
    };

    @Override
    public void init() {
        getKWAPI().registerPlayerStatusListener(mOnPlayerStatusListener);
    }

    @Override
    public void startApp() {
        getKWAPI().startAPP(true);
    }

    @Override
    public void exitApp() {
        getKWAPI().exitAPP();
    }

    @Override
    public void playNext() {
        getKWAPI().setPlayState(PlayState.STATE_NEXT);
    }

    @Override
    public void playPre() {
        getKWAPI().setPlayState(PlayState.STATE_PRE);
    }

    @Override
    public void pause() {
        getKWAPI().setPlayState(PlayState.STATE_PAUSE);
    }

    @Override
    public void play() {
        getKWAPI().setPlayState(PlayState.STATE_PLAY);
    }

    @Override
    public void startWithKeyword(String keyword) {
        //...
    }

    @Override
    public void release() {
        removeListener(null);
        getKWAPI().unRegisterPlayerStatusListener();
    }

    @Override
    public void setPlayMode(MusicPlayMode musicPlayMode) {
        switch (musicPlayMode) {
            case MEDIA_CIRCLE :
                getKWAPI().setPlayMode(PlayMode.MODE_ALL_CIRCLE);
                break;
            case MEDIA_ONE :
                getKWAPI().setPlayMode(PlayMode.MODE_SINGLE_CIRCLE);
                break;
            case MEDIA_ORDER :
                getKWAPI().setPlayMode(PlayMode.MODE_ALL_ORDER);
                break;
            case MEDIA_RANDOM :
                getKWAPI().setPlayMode(PlayMode.MODE_ALL_RANDOM);
                break;
        }
    }

    @Override
    public void setPlayModeRandom() {
        int mode = new Random().nextInt(4);
        switch (mode) {
            case 1 :
                setPlayMode(MusicPlayMode.MEDIA_CIRCLE);
                break;
            case 2 :
                setPlayMode(MusicPlayMode.MEDIA_ONE);
                break;
            case 3 :
                setPlayMode(MusicPlayMode.MEDIA_ORDER);
                break;
            case 4 :
                setPlayMode(MusicPlayMode.MEDIA_RANDOM);
                break;
        }
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
    public void registerListener(IMusicStatusListener<Music> listener) {
        if (listener != null) {
            mMusicIMusicStatusListener = listener;
        }
    }

    @Override
    public void removeListener(IMusicStatusListener<Music> listener) {
        mMusicIMusicStatusListener = null;
    }

    @Override
    public void searchMusicInfo(String singer, String song, String album, final IMusicSearchListener<Music> listener) {
        getKWAPI().searchOnlineMusic(singer, song, album, new OnSearchListener() {
            @Override
            public void searchFinshed(SearchStatus searchStatus, boolean b, List<Music> list, boolean b1) {
                listener.musicSearchFinished(searchStatus.name().equals("SUCCESS"), list);
            }
        });
    }

    private KWAPI getKWAPI() {
        if (kwapi != null) {
            return kwapi;
        }
        kwapi = KWAPI.createKWAPI(context.getApplicationContext(), KUWO_KEY);
        return kwapi;
    }

}
