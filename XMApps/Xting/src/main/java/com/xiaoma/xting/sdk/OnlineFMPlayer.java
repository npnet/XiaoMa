package com.xiaoma.xting.sdk;

import android.content.Context;

import com.xiaoma.xting.online.model.PlayMode;
import com.xiaoma.xting.online.model.PlayerState;
import com.xiaoma.xting.sdk.bean.XMCommonTrackList;
import com.xiaoma.xting.sdk.bean.XMPlayableModel;
import com.xiaoma.xting.sdk.bean.XMRadio;
import com.xiaoma.xting.sdk.bean.XMSchedule;
import com.xiaoma.xting.sdk.bean.XMTrack;
import com.ximalaya.ting.android.opensdk.model.track.CommonTrackList;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

/**
 * @author youthyJ
 * @date 2018/10/18
 */
public interface OnlineFMPlayer {
    void init(Context context);

    boolean isConnected();

    // 播放数据获取接口 ↓
    boolean isPlaying();

    boolean hasPreSound();

    boolean hasNextSound();

    boolean canMoveSeekBar();

    boolean isOnlineSource();

    boolean isAdsActive();

    int getCurrentIndex();

    int getPlayCurrPosition();

    long getDuration();

    long getPlayCacheSize();

    String getCurPlayUrl();

    PlayerState getPlayerState();

    XMPlayableModel getCurrSound();
    // 播放数据获取接口 ↑

    // 播放控制接口 ↓
    void play();

    void play(int index);

    void playPre();

    void playNext();

    void pause();

    void stop();

    void seekToByPercent(float percent);

    void seekTo(int position);
    // 播放控制接口 ↑

    // 播放设置接口 ↓
    void setPlayMode(PlayMode playMode);

    void setVolume(float leftChannel, float rightChannel);

    void clearPlayCache();
    // 播放设置接口 ↑

    // 播放内容操作接口 ↓
    void updateTrackInPlayList(XMTrack xmTrack);

    void resetPlayList();

    void removeListByIndex(int index);

    void playList(List<XMTrack> xmTracks, int startIndex);

    void setPlayList(List<XMTrack> xmTracks, int startIndex);

    <T extends CommonTrackList<Track>> void playList(XMCommonTrackList<T> xmCommonTrackList, int startIndex);

    <T extends CommonTrackList<Track>> void setPlayList(XMCommonTrackList<T> xmCommonTrackList, int startIndex);

    void playRadio(XMRadio xmRadio);

    void playLiveRadioForSDK(XMRadio xmRadio, int weekday, int playIndex);

    void playSchedule(List<XMSchedule> xmSchedules, int startIndex);
    // 播放内容操作接口↑

    boolean addPlayerStatusListener(PlayerStatusListener listener);

    boolean removePlayerStatusListener(PlayerStatusListener listener);

    List<? extends XMPlayableModel> getListInPlayer();

    void setRadio(XMRadio xmRadio);

    void addTracksToPlayList(List<XMTrack> xmTracks, boolean addAfter);

    int getPlayIndex();

    int getPlayListSize();

    void resetPlayer();

    void clearMemory(boolean hasMemory);
}
