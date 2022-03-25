package com.xiaoma.music.kuwo.impl;

import android.content.Context;

import com.xiaoma.music.kuwo.listener.OnChargeQualityListener;
import com.xiaoma.music.kuwo.listener.OnMusicChargeListener;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.kuwo.model.XMMusicList;

import java.util.List;
import java.util.Map;

/**
 * Created by ZYao.
 * Date ：2018/10/15 0015
 */
public interface IPlayerOperate {

    void init(Context context);

    int getNowPlayMusicIndex();

    XMMusicList getNowPlayingList();

    XMMusic getNowPlayingMusic();

    int getPlayMode();

    void setPlayMode(int var1);

    void play(XMMusic music);

    void play(List<XMMusic> musics, int var2);

    void playBillBroadFirst();

    void playMusicList(XMMusicList musicList, int pos);

    void playLocalMusic(String showName, String filePath);

    void playLocalMusic(Map<String, String> local);

    void playRadio(int cid, String name);

    void playRadio(XMMusicList musicList);

    void pause();

    void stop();

    boolean continuePlay();

    boolean playNext();

    boolean playPre();

    int getStatus();

    int getDuration();

    int getCurrentPos();

    void seek(int var1);

    int getBufferingPos();

    int getVolume();

    void setVolume(int var1);

    void setVolumeScale(float scale);

    int getMaxVolume();

    void setMute(boolean var1);

    boolean isMute();

    int getPreparePercent();

    void saveData(boolean var1);

    void autoPlayNext();

    int getDownloadWhenPlayQuality();

    boolean setDownloadWhenPlayQuality(int quality);

    int getNowPlayMusicBestQuality();

    void chargeNowPlayMusic(int quality, OnChargeQualityListener listener);

    void chargeMusics(List<XMMusic> musics, OnMusicChargeListener listener);
}
