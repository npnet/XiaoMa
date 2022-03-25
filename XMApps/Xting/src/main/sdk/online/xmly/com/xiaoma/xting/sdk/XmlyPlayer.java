package com.xiaoma.xting.sdk;

import android.content.Context;
import android.util.Log;

import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.xting.common.playerSource.control.AudioFocusManager;
import com.xiaoma.xting.online.model.PlayMode;
import com.xiaoma.xting.online.model.PlayerState;
import com.xiaoma.xting.sdk.bean.XMAdvertis;
import com.xiaoma.xting.sdk.bean.XMAdvertisList;
import com.xiaoma.xting.sdk.bean.XMCommonTrackList;
import com.xiaoma.xting.sdk.bean.XMPlayableModel;
import com.xiaoma.xting.sdk.bean.XMRadio;
import com.xiaoma.xting.sdk.bean.XMSchedule;
import com.xiaoma.xting.sdk.bean.XMTrack;
import com.xiaoma.xting.sdk.utils.HimalayanPlayerUtils;
import com.xiaoma.xting.sdk.utils.PlayModeConverter;
import com.xiaoma.xting.sdk.utils.PlayerStatusConverter;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
import com.ximalaya.ting.android.opensdk.model.track.CommonTrackList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerConfig;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author youthyJ
 * @date 2018/10/17
 */
public class XmlyPlayer implements OnlineFMPlayer {
    private static final String TAG = XmlyPlayer.class.getSimpleName() + "test";
    private static OnlineFMPlayer instance;
    private Context appContext;
    private XmPlayerManager playerInstance;
    private CopyOnWriteArrayList<PlayerStatusListener> listeners = new CopyOnWriteArrayList<>();

    public static OnlineFMPlayer getInstance() {
        if (instance == null) {
            synchronized (XmlyPlayer.class) {
                if (instance == null) {
                    instance = new XmlyPlayer();
                }
            }
        }
        return instance;
    }

    private XmlyPlayer() {
    }

    @Override
    public void init(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }
        appContext = context.getApplicationContext();
        if (playerInstance == null) {
            playerInstance = XmPlayerManager.getInstance(appContext);
            playerInstance.init();
            XmPlayerConfig.getInstance(appContext).setSDKHandleAudioFocus(false);
            XmPlayerConfig.getInstance(appContext).setSDKHandlePhoneComeAudioFocus(false);
            initStatusListener();
        }
    }

    private void initStatusListener() {
        playerInstance.addPlayerStatusListener(new IXmPlayerStatusListener() {
            @Override
            public void onPlayStart() {
                if (AudioFocusManager.getInstance().getLossByOther()) {
                    playerInstance.pause();
                    return;
                }
                if (listeners.isEmpty()) {
                    return;
                }
                for (PlayerStatusListener l : listeners) {
                    if (l == null) {
                        continue;
                    }
                    try {
                        l.onPlayStart();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onPlayPause() {
                if (listeners.isEmpty()) {
                    return;
                }
                for (PlayerStatusListener l : listeners) {
                    if (l == null) {
                        continue;
                    }
                    try {
                        l.onPlayPause();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onPlayStop() {
                if (listeners.isEmpty()) {
                    return;
                }
                for (PlayerStatusListener l : listeners) {
                    if (l == null) {
                        continue;
                    }
                    try {
                        l.onPlayStop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onSoundPlayComplete() {
                if (listeners.isEmpty()) {
                    return;
                }
                for (PlayerStatusListener l : listeners) {
                    if (l == null) {
                        continue;
                    }
                    try {
                        l.onSoundPlayComplete();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onSoundPrepared() {
                if (listeners.isEmpty()) {
                    return;
                }
                for (PlayerStatusListener l : listeners) {
                    if (l == null) {
                        continue;
                    }
                    try {
                        l.onSoundPrepared();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onSoundSwitch(PlayableModel lastSound, PlayableModel curSound) {
                if (listeners.isEmpty()) {
                    return;
                }
                for (PlayerStatusListener l : listeners) {
                    if (l == null) {
                        continue;
                    }
                    try {
                        l.onSoundSwitch(HimalayanPlayerUtils.toXMPlayModel(curSound));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onBufferingStart() {
                if (listeners.isEmpty()) {
                    return;
                }
                for (PlayerStatusListener l : listeners) {
                    if (l == null) {
                        continue;
                    }
                    try {
                        l.onBufferingStart();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onBufferingStop() {
                if (listeners.isEmpty()) {
                    return;
                }
                for (PlayerStatusListener l : listeners) {
                    if (l == null) {
                        continue;
                    }
                    try {
                        l.onBufferingStop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onBufferProgress(int progress) {
                if (listeners.isEmpty()) {
                    return;
                }
                for (PlayerStatusListener l : listeners) {
                    if (l == null) {
                        continue;
                    }
                    try {
                        l.onBufferProgress(progress);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onPlayProgress(int curPos, int duration) {
                if (listeners.isEmpty()) {
                    return;
                }
                for (PlayerStatusListener l : listeners) {
                    if (l == null) {
                        continue;
                    }
                    try {
                        l.onPlayProgress(curPos, duration);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public boolean onError(XmPlayerException e) {
                if (listeners.isEmpty()) {
                    return false;
                }
                for (PlayerStatusListener l : listeners) {
                    if (l == null) {
                        continue;
                    }
                    try {
                        l.onError(e);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                return false;
            }
        });
        playerInstance.addAdsStatusListener(new IXmAdsStatusListener() {
            @Override
            public void onStartGetAdsInfo() {
                if (listeners.isEmpty()) {
                    return;
                }
                for (PlayerStatusListener l : listeners) {
                    if (l == null) {
                        continue;
                    }
                    try {
                        l.onStartGetAdsInfo();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onGetAdsInfo(AdvertisList advertisList) {
                if (listeners.isEmpty()) {
                    return;
                }
                for (PlayerStatusListener l : listeners) {
                    if (l == null) {
                        continue;
                    }
                    try {
                        l.onGetAdsInfo(new XMAdvertisList(advertisList));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onAdsStartBuffering() {
                if (listeners.isEmpty()) {
                    return;
                }
                for (PlayerStatusListener l : listeners) {
                    if (l == null) {
                        continue;
                    }
                    try {
                        l.onAdsStartBuffering();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onAdsStopBuffering() {
                if (listeners.isEmpty()) {
                    return;
                }
                for (PlayerStatusListener l : listeners) {
                    if (l == null) {
                        continue;
                    }
                    try {
                        l.onAdsStopBuffering();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onStartPlayAds(Advertis advertis, int position) {
                if (AudioFocusManager.getInstance().getLossByOther()) {
                    playerInstance.pause();
                    return;
                }
                if (listeners.isEmpty()) {
                    return;
                }
                for (PlayerStatusListener l : listeners) {
                    if (l == null) {
                        continue;
                    }
                    try {
                        l.onStartPlayAds(new XMAdvertis(advertis), position);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCompletePlayAds() {
                if (listeners.isEmpty()) {
                    return;
                }
                for (PlayerStatusListener l : listeners) {
                    if (l == null) {
                        continue;
                    }
                    try {
                        l.onCompletePlayAds();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(int code, int msg) {
                if (listeners.isEmpty()) {
                    return;
                }
                for (PlayerStatusListener l : listeners) {
                    if (l == null) {
                        continue;
                    }
                    try {
                        l.onError(code, msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public boolean isConnected() {
        return playerInstance.isConnected();
    }

    @Override
    public boolean isPlaying() {
        // 缓冲+播放中 均返回true
        boolean isPlaying = playerInstance.isPlaying();
        Log.d(TAG, "isPlaying: return[" + isPlaying + "]");
        return isPlaying;
    }

    @Override
    public void play() {
        if (!AudioFocusManager.getInstance().requestXmlyFocus()) return;
        ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "play");
                playerInstance.play();
            }
        });
    }

    @Override
    public void play(final int index) {
        if (!AudioFocusManager.getInstance().requestXmlyFocus()) return;
        ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "play - index[" + index + "]");
                playerInstance.play(index);
            }
        });
    }

    @Override
    public int getCurrentIndex() {
        int currentIndex = playerInstance.getCurrentIndex();
        Log.d(TAG, "getCurrentIndex: return[" + currentIndex + "]");
        return currentIndex;
    }

    @Override
    public long getDuration() {
        int duration = playerInstance.getDuration();
        Log.d(TAG, "getDuration: return[" + duration + "]");
        return duration;
    }

    @Override
    public int getPlayCurrPosition() {
        int playCurrPositon = playerInstance.getPlayCurrPositon();
        Log.d(TAG, "getPlayCurrPosition: return[" + playCurrPositon + "]");
        return playCurrPositon;
    }

    @Override
    public boolean hasPreSound() {
        boolean hasPreSound = playerInstance.hasPreSound();
        Log.d(TAG, "hasPreSound: return[" + hasPreSound + "]");
        return hasPreSound;
    }

    @Override
    public void playPre() {
        if (!AudioFocusManager.getInstance().requestXmlyFocus()) return;
        Log.d(TAG, "playPre");
        playerInstance.playPre();
    }

    @Override
    public boolean hasNextSound() {
        boolean hasNextSound = playerInstance.hasNextSound();
        Log.d(TAG, "hasNextSound: return[" + hasNextSound + "]");
        return hasNextSound;
    }

    @Override
    public void playNext() {
        if (!AudioFocusManager.getInstance().requestXmlyFocus()) return;
        Log.d(TAG, "playNext");
        playerInstance.playNext();
    }

    @Override
    public void pause() {
        Log.d(TAG, "pause");
        playerInstance.pause();
    }

    @Override
    public void stop() {
        Log.d(TAG, "stop");
        playerInstance.stop();
    }

    @Override
    public void setPlayMode(PlayMode playMode) {
        Log.d(TAG, "setPlayMode - playMode[" + playMode + "]");
        if (playMode == null) {
            return;
        }
        XmPlayListControl.PlayMode xmlyPlayMode = PlayModeConverter.conver(playMode);
        if (xmlyPlayMode == null) {
            return;
        }
        playerInstance.setPlayMode(xmlyPlayMode);
    }

    @Override
    public PlayerState getPlayerState() {
        int xmlyPlayerState = playerInstance.getPlayerStatus();
        PlayerState playerState = PlayerStatusConverter.conver(xmlyPlayerState);
        Log.d(TAG, "getPlayerState: return[" + playerState + "]");
        return playerState;
    }

    @Override
    public void seekToByPercent(float percent) {
        // 百分比, 取值范围 [ 0 - 1 ]
        Log.d(TAG, "seekToByPercent - percent[" + percent + "]");
        playerInstance.seekToByPercent(percent);
    }

    @Override
    public void seekTo(int position) {
        // 单位:毫秒
        Log.d(TAG, "seekTo - position[" + position + "]");
        playerInstance.seekTo(position);
    }

    @Override
    public void setVolume(float leftChannel, float rightChannel) {
        Log.d(TAG, "setVolume - leftChannel[" + leftChannel + "] rightChannel[" + rightChannel + "]");
        playerInstance.setVolume(leftChannel, rightChannel);
    }

    @Override
    public XMPlayableModel getCurrSound() {
        if (playerInstance == null || playerInstance.getCurrSound() == null) {
            return null;
        }
        PlayableModel playable = playerInstance.getCurrSound();
        if (playable instanceof Track) {
            XMTrack xmTrack = new XMTrack((Track) playable);
            Log.d(TAG, "getCurrSound: return[" + xmTrack.getDataId() + "]");
            return xmTrack;
        }
        if (playable instanceof Radio) {
            XMRadio xmRadio = new XMRadio((Radio) playable);
            Log.d(TAG, "getCurrSound: return[" + xmRadio.getDataId() + "]");
            return xmRadio;
        }
        if (playable instanceof Schedule) {
            XMSchedule xmSchedule = new XMSchedule((Schedule) playable);
            Log.d(TAG, "getCurrSound: return[" + xmSchedule.getDataId() + "]");
            return xmSchedule;
        }
        Log.d(TAG, "getCurrSound: return[null]");
        return null;
    }

    @Override
    public List<? extends XMPlayableModel> getListInPlayer() {
        List<Track> playList = playerInstance.getPlayList();
        if (playList == null || playList.size() == 0) {
            return null;
        }
        Track trackTemp = playList.get(0);
        String kind = trackTemp.getKind();
        if (XMPlayableModel.KIND_RADIO.equals(kind)) {
            List<XMRadio> xmRadios = new ArrayList<>();
            //根据测试,通过调用playRadio()进行播放的时候,调用该方法,只会返回一个radio对象
            xmRadios.add(HimalayanPlayerUtils.track2Radio(trackTemp));
            return xmRadios;
        } else if (XMPlayableModel.KIND_TRACK.equals(kind)) {
            List<XMTrack> xmTracks = new ArrayList<>();
            for (Track track : playList) {
                if (track == null) {
                    continue;
                }
                xmTracks.add(new XMTrack(track));
            }

            return xmTracks;
        } else if (XMPlayableModel.KIND_SCHEDULE.equals(kind)) {
            List<XMSchedule> xmSchedules = new ArrayList<>();
            for (Track track : playList) {
                if (track == null) {
                    continue;
                }
                xmSchedules.add(HimalayanPlayerUtils.track2Schedule(track));
            }
            return xmSchedules;
        }
        return null;
    }

    @Override
    public boolean canMoveSeekBar() {
        int currPlayType = playerInstance.getCurrPlayType();
        boolean canMove = (XmPlayListControl.PLAY_SOURCE_TRACK == currPlayType);
        Log.d(TAG, "canMoveSeekBar: return[" + canMove + "]");
        return canMove;
    }

    @Override
    public long getPlayCacheSize() {
        long size = XmPlayerManager.getPlayCacheSize();
        Log.d(TAG, "getPlayCacheSize: return[" + size + "]");
        return size;
    }

    @Override
    public void clearPlayCache() {
        Log.d(TAG, "clearPlayCache");
        playerInstance.clearPlayCache();
    }

    @Override
    public boolean isOnlineSource() {
        boolean isOnlineSource = playerInstance.isOnlineSource();
        Log.d(TAG, "isOnlineSource: return[" + isOnlineSource + "]");
        return isOnlineSource;
    }

    @Override
    public boolean isAdsActive() {
        boolean isAdsActive = playerInstance.isAdsActive();
        Log.d(TAG, "isAdsActive: return[" + isAdsActive + "]");
        return isAdsActive;
    }

    @Override
    public void updateTrackInPlayList(XMTrack xmTrack) {
        Log.d(TAG, "updateTrackInPlayList - xmTrack[" + (xmTrack == null ? "null" : xmTrack.getDataId()) + "]");
        if (xmTrack == null) {
            return;
        }
        playerInstance.updateTrackInPlayList(xmTrack.getSDKBean());
    }

    @Override
    public String getCurPlayUrl() {
        String curPlayUrl = playerInstance.getCurPlayUrl();
        Log.d(TAG, "getCurPlayUrl: return[" + curPlayUrl + "]");
        return curPlayUrl;
    }

    @Override
    public void resetPlayList() {
        Log.d(TAG, "resetPlayList");

        playerInstance.resetPlayList();
    }

    @Override
    public void removeListByIndex(int index) {
        Log.d(TAG, "removeListByIndex - index[" + index + "]");
        playerInstance.removeListByIndex(index);
    }

    @Override
    public void setRadio(XMRadio xmRadio) {
        if (!AudioFocusManager.getInstance().requestXmlyFocus()) return;
        Log.d(TAG, "setRadio - xmRadio[" + xmRadio + "]");
        List<XMTrack> tracks = new ArrayList<>();
        XMTrack xmTrack = HimalayanPlayerUtils.radio2Track(xmRadio);
        tracks.add(xmTrack);
        setPlayList(tracks, 0);
    }

    @Override
    public void setPlayList(List<XMTrack> xmTracks, int startIndex) {
        Log.d(TAG, "setPlayList - xmTracks[" + (xmTracks == null ? "null" : xmTracks.size()) + "] startIndex[" + startIndex + "]");
        if (xmTracks == null) {
            return;
        }
        List<Track> tracks = new ArrayList<>();
        for (XMTrack xmTrack : xmTracks) {
            if (xmTrack == null) {
                continue;
            }
            tracks.add(xmTrack.getSDKBean());
        }
        playerInstance.setPlayList(tracks, startIndex);
    }

    @Override
    public void addTracksToPlayList(List<XMTrack> xmTracks, boolean addAfter) {
        if (xmTracks == null) {
            return;
        }
        List<Track> tracks = new ArrayList<>();
        for (XMTrack xmTrack : xmTracks) {
            if (xmTrack == null) {
                continue;
            }
            tracks.add(xmTrack.getSDKBean());
        }
        if (addAfter) {
            playerInstance.addTracksToPlayList(tracks);
        } else {
            playerInstance.insertTracksToPlayListHead(tracks);
        }
    }


    @Override
    public int getPlayIndex() {
        return playerInstance.getCurrentIndex();
    }

    @Override
    public int getPlayListSize() {
        return playerInstance.getPlayListSize();
    }

    @Override
    public <T extends CommonTrackList<Track>> void setPlayList(XMCommonTrackList<T> xmCommonTrackList, int startIndex) {
        Log.d(TAG, "setPlayList - xmCommonTrackList[" + (xmCommonTrackList == null ? "null" : xmCommonTrackList.size()) + "] startIndex[" + startIndex + "]");
        T commonTrackList = xmCommonTrackList.getSDKBean();
        playerInstance.setPlayList(commonTrackList, startIndex);
    }

    @Override
    public void playList(List<XMTrack> xmTracks, final int startIndex) {
        if (!AudioFocusManager.getInstance().requestXmlyFocus()) return;
        Log.d(TAG, "playList - xmTracks[" + (xmTracks == null ? "null" : xmTracks.size()) + "] startIndex[" + startIndex + "]");
        if (xmTracks == null) {
            return;
        }
        final List<Track> tracks = new ArrayList<>();
        for (XMTrack xmTrack : xmTracks) {
            if (xmTrack == null) {
                continue;
            }
            tracks.add(xmTrack.getSDKBean());
        }
        //避免出现混音的情况
        if (isPlaying()
                && getCurrSound() != null
                && tracks.get(startIndex) != null
                && getCurrSound().getDataId() == tracks.get(startIndex).getDataId()) {
            playerInstance.setPlayList(tracks, startIndex);
            return;
        }
        ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "{run}-[PlayList Called!] : ");
                playerInstance.playList(tracks, startIndex);
            }
        });
    }

    @Override
    public <T extends CommonTrackList<Track>> void playList(XMCommonTrackList<T> xmCommonTrackList, final int startIndex) {
        if (!AudioFocusManager.getInstance().requestXmlyFocus()) return;
        Log.d(TAG, "playList - xmCommonTrackList[" + (xmCommonTrackList == null ? "null" : xmCommonTrackList.size()) + "] startIndex[" + startIndex + "]");
        final T commonTrackList = xmCommonTrackList.getSDKBean();
        ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
            @Override
            public void run() {
                playerInstance.playList(commonTrackList, startIndex);
            }
        });
    }

    @Override
    public void playRadio(XMRadio xmRadio) {
        if (!AudioFocusManager.getInstance().requestXmlyFocus()) return;
        Log.d(TAG, "playRadio - xmRadio[" + (xmRadio == null ? "null" : xmRadio.getDataId()) + "]");
        final Radio radio = xmRadio.getSDKBean();
        ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
            @Override
            public void run() {
                playerInstance.playActivityRadio(radio);
            }
        });
    }

    @Override
    public void playLiveRadioForSDK(XMRadio xmRadio, final int weekday, final int playIndex) {
        if (!AudioFocusManager.getInstance().requestXmlyFocus()) return;
        Log.d(TAG, "playLiveRadioForSDK - xmRadio[" + (xmRadio == null ? "null" : xmRadio.getDataId()) + "] weekday[" + weekday + "] playIndex[" + playIndex + "]");
        final Radio radio = xmRadio.getSDKBean();
        ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
            @Override
            public void run() {
                playerInstance.playLiveRadioForSDK(radio, weekday, playIndex);
            }
        });

    }

    @Override
    public void playSchedule(List<XMSchedule> xmSchedules, int startIndex) {
        if (!AudioFocusManager.getInstance().requestXmlyFocus()) return;
        Log.d(TAG, "playSchedule - xmSchedules[" + (xmSchedules == null ? "null" : xmSchedules.size()) + "] startIndex[" + startIndex + "]");
        if (xmSchedules == null) {
            return;
        }
        List<Schedule> schedules = new ArrayList<>();
        for (XMSchedule xmSchedule : xmSchedules) {
            if (xmSchedule == null) {
                continue;
            }
            schedules.add(xmSchedule.getSDKBean());
        }
        ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
            @Override
            public void run() {
                playerInstance.playSchedule(schedules, startIndex);
            }
        });
    }

    @Override
    public boolean addPlayerStatusListener(PlayerStatusListener listener) {
        if (listener == null) {
            return false;
        }
        if (listeners.contains(listener)) {
            return false;
        }
        return listeners.add(listener);
    }

    @Override
    public boolean removePlayerStatusListener(PlayerStatusListener listener) {
        if (listener == null) {
            return false;
        }
        return listeners.remove(listener);
    }

    public void resetPlayer() {
        Log.d(TAG, "{resetPlayer}-[] : ");
        playerInstance.resetPlayer();
    }

    public void setVoice(float leftVolume, float rightVolume) {
        playerInstance.setVolume(leftVolume, rightVolume);
    }

    /**
     * @param hasMemory
     */
    public void clearMemory(boolean hasMemory) {
        playerInstance.setBreakpointResume(hasMemory);
    }
}
