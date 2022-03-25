package com.xiaoma.music.kuwo.observer;

import android.annotation.IntDef;

import com.xiaoma.autotracker.model.PlayType;
import com.xiaoma.music.OnlineMusicFactory;
import com.xiaoma.music.common.audiosource.AudioSource;
import com.xiaoma.music.common.audiosource.AudioSourceManager;
import com.xiaoma.music.common.manager.AssistantShowManager;
import com.xiaoma.music.common.manager.MusicDbManager;
import com.xiaoma.music.common.manager.UploadPlayManager;
import com.xiaoma.music.kuwo.listener.OnPlayControlListener;
import com.xiaoma.music.kuwo.manager.KuwoMessage;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.kuwo.model.XMMusicList;
import com.xiaoma.music.kuwo.proxy.XMKWPlayerOperateProxy;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.log.KLog;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashSet;
import java.util.Set;

import cn.kuwo.base.bean.Music;
import cn.kuwo.core.messagemgr.MessageID;
import cn.kuwo.core.observers.IPlayControlObserver;
import cn.kuwo.mod.ModMgr;
import cn.kuwo.mod.playcontrol.PlayMode;
import cn.kuwo.service.PlayDelegate;

/**
 * Created by ZYao.
 * Date ：2018/10/9 0009
 */
public class KuwoPlayControlObserver implements IPlayControlObserver, IMessageObserver {


    public static final String TAG = "KuwoPlayControlObserver";
    private static Set<OnPlayControlListener> kwStateListeners = new HashSet<>();
    private final KuwoMessage kuwoMessage;
    private int audioDuration;
    private static final int MAX_PLAY_FAILED_COUNT = 3;
    private static final long AUTO_NEXT_DELAY = 3000;
    private static final long AUTO_NEXT_DELAY_VIP = 10000;
    private int count = 0;

    public static KuwoPlayControlObserver getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static final KuwoPlayControlObserver instance = new KuwoPlayControlObserver();
    }

    public KuwoPlayControlObserver() {
        kuwoMessage = new KuwoMessage();
    }

    @Override
    public void addPlayStateListener(OnPlayControlListener kwStateListener) {
        if (kwStateListener != null) {
            kwStateListeners.add(kwStateListener);
        }
    }

    @Override
    public void removePlayStateListener(OnPlayControlListener kwStateListener) {
        if (kwStateListener != null) {
            kwStateListeners.remove(kwStateListener);
        }
    }

    @Override
    public void registerKuwoStateListener() {
        kuwoMessage.attachMsg(MessageID.OBSERVER_PLAYCONTROL, this);
    }

    @Override
    public void unregisterKuwoStateListener() {
        kuwoMessage.detachMsg(MessageID.OBSERVER_PLAYCONTROL, this);
    }

    @Override
    public void IPlayControlObserver_ReadyPlay(Music music) {
        KLog.d(TAG, "IPlayControlObserver_ReadyPlay: ");
        if (music == null) {
            return;
        }
        final XMMusic xmMusic = new XMMusic(music);
        for (OnPlayControlListener kwStateListener : kwStateListeners) {
            if (kwStateListener != null) {
                kwStateListener.onReadyPlay(xmMusic);
            }
        }
    }

    @Override
    public void IPlayControlObserver_Play(Music music) {
        KLog.d(TAG, "IPlayControlObserver_Play: ");
        if (music == null) {
            return;
        }
        XMMusic xmMusic = new XMMusic(music);
        for (OnPlayControlListener kwStateListener : kwStateListeners) {
            if (kwStateListener != null) {
                kwStateListener.onReadyPlay(xmMusic);
            }
        }
        ThreadDispatcher.getDispatcher().remove(needVipAutoNext);
        ThreadDispatcher.getDispatcher().remove(needBuyAutoNext);
    }

    @Override
    public void IPlayControlObserver_PreSart(Music music, boolean b) {
        KLog.d(TAG, "IPlayControlObserver_PreSart: ");
        if (music == null) {
            return;
        }
        final XMMusic xmMusic = new XMMusic(music);
        saveHistory(xmMusic);
        AudioSourceManager.getInstance().switchAudioSource(AudioSource.ONLINE_MUSIC);
        for (OnPlayControlListener kwStateListener : kwStateListeners) {
            if (kwStateListener != null) {
                kwStateListener.onPreStart(xmMusic);
            }
        }
    }

    private void saveHistory(XMMusic music) {
        try {
            long rid = music.getSDKBean().rid;
            XMMusic xmMusic = MusicDbManager.getInstance().queryHistoryMusicById(rid);
            if (xmMusic == null) {
                MusicDbManager.getInstance().saveHistoryMusic(music);
            } else {
                MusicDbManager.getInstance().deleteHistoryMusic(xmMusic, false);
                MusicDbManager.getInstance().saveHistoryMusic(music);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initStartPlayTime() {
        if (!OnlineMusicFactory.getKWPlayer().isPlaying()) {
            UploadPlayManager.getInstance().initPlayTime();
        }
    }

    @Override
    public void IPlayControlObserver_RealPlay(Music music) {
        KLog.d(TAG, "IPlayControlObserver_RealPlay: ");
        if (music == null) {
            return;
        }
        audioDuration = ModMgr.getPlayControl().getDuration();
        if (music.duration == 0) {
            music.duration = audioDuration / 1000;
        }
        if (!XMKWPlayerOperateProxy.getInstance().hasAudioFocus()) {
            XMKWPlayerOperateProxy.getInstance().doPause(false);
            return;
        }
        XMMusic xmMusic = new XMMusic(music);
        for (OnPlayControlListener kwStateListener : kwStateListeners) {
            if (kwStateListener != null) {
                kwStateListener.onPlay(xmMusic);
            }
        }
        count = 0;
//        //播放成功后全局音质重新设置为用户自定义设置的音质，保证下一首播放优先选择用户定义的音质
//        OnlineMusicFactory.getKWPlayer().setDownloadWhenPlayQuality(MusicQualityManager.getInstance().getCustomQuality());
        UploadPlayManager.getInstance().uploadKwPlayInfo(music);
    }

    @Override
    public void IPlayControlObserver_Pause() {
        KLog.d(TAG, "IPlayControlObserver_Pause: ");
        UploadPlayManager.getInstance().uploadPlayTime(PlayType.ONLINEMUSINC.getPlayType());
        for (OnPlayControlListener kwStateListener : kwStateListeners) {
            if (kwStateListener != null) {
                kwStateListener.onPause();
            }
        }
    }

    @Override
    public void IPlayControlObserver_Continue() {
        KLog.d(TAG, "IPlayControlObserver_Continue: ");
        AudioSourceManager.getInstance().switchAudioSource(AudioSource.ONLINE_MUSIC);
        UploadPlayManager.getInstance().initPlayTime();
        XMMusic nowPlayingMusic = OnlineMusicFactory.getKWPlayer().getNowPlayingMusic();
        for (OnPlayControlListener kwStateListener : kwStateListeners) {
            if (kwStateListener != null) {
                kwStateListener.onPlay(nowPlayingMusic);
            }
        }
    }

    @Override
    public void IPlayControlObserver_Seek(int i) {
        KLog.d(TAG, "IPlayControlObserver_Seek: " + i);
        for (OnPlayControlListener kwStateListener : kwStateListeners) {
            if (kwStateListener != null) {
                kwStateListener.onSeekSuccess(i);
            }
        }
    }

    @Override
    public void IPlayControlObserver_SetVolumn(int i) {

    }

    @Override
    public void IPlayControlObserver_SetMute(boolean b) {

    }

    @Override
    public void IPlayControlObserver_PlayFailed(PlayDelegate.ErrorCode code) {
        KLog.d(TAG, "IPlayControlObserver_PlayFailed: " + code.toString());
        UploadPlayManager.getInstance().uploadPlayTime(PlayType.ONLINEMUSINC.getPlayType());
        int errorCode = -1;
        switch (code.name()) {
            case "NOCOPYRIGHT":
                errorCode = ErrorCode.NO_COPY_RIGHT;
                break;
            case "NEED_VIP":
                errorCode = ErrorCode.NEED_VIP;
                break;
            case "NEED_SING_SONG":
                errorCode = ErrorCode.NEED_SING_SONG;
                break;
            case "NEED_ALBUM":
                errorCode = ErrorCode.NEED_ALBUM;
                break;
            case "NOT_ENOUGH":
                errorCode = ErrorCode.NOT_ENOUGH;
                break;
            default:
                errorCode = ErrorCode.OTHER_PLAY_ERROR;
                break;

        }
        for (OnPlayControlListener kwStateListener : kwStateListeners) {
            if (kwStateListener != null) {
                kwStateListener.onPause();
                kwStateListener.onPlayFailed(errorCode, code.name());
            }
        }
        handlePlayFailed(errorCode);
    }

    @Override
    public void IPlayControlObserver_PlayStop(boolean b) {
        KLog.d(TAG, "IPlayControlObserver_PlayStop: " + b);
        UploadPlayManager.getInstance().uploadPlayTime(PlayType.ONLINEMUSINC.getPlayType());
        for (OnPlayControlListener kwStateListener : kwStateListeners) {
            if (kwStateListener != null) {
                kwStateListener.onPlayStop();
            }
        }
    }

    @Override
    public void IPlayControlObserver_Progress(int i, int i1) {
        for (OnPlayControlListener kwStateListener : kwStateListeners) {
            if (kwStateListener != null) {
                kwStateListener.onProgressChange(i, audioDuration);
            }
        }
    }

    @Override
    public void IPlayControlObserver_ChangeCurList() {
        for (OnPlayControlListener kwStateListener : kwStateListeners) {
            if (kwStateListener != null) {
                kwStateListener.onCurrentPlayListChanged();
            }
        }
    }

    @Override
    public void IPlayControlObserver_ChangePlayMode(int i) {
        for (OnPlayControlListener kwStateListener : kwStateListeners) {
            if (kwStateListener != null) {
                kwStateListener.onPlayModeChanged(i);
            }
        }
    }

    @Override
    public void IPlayControlObserver_WaitForBuffering() {
        for (OnPlayControlListener kwStateListener : kwStateListeners) {
            if (kwStateListener != null) {
                kwStateListener.onBufferStart();
            }
        }
    }

    @Override
    public void IPlayControlObserver_WaitForBufferingFinish() {
        for (OnPlayControlListener kwStateListener : kwStateListeners) {
            if (kwStateListener != null) {
                kwStateListener.onBufferFinish();
            }
        }
    }

    private void handlePlayFailed(int errorCode) {
        if (errorCode == ErrorCode.NEED_ALBUM ||
                errorCode == ErrorCode.NEED_SING_SONG ||
                errorCode == ErrorCode.NO_COPY_RIGHT) {
            //需要付费或者没有版权，播放失败
            if (count > MAX_PLAY_FAILED_COUNT) {
                //连续三首失败后，暂停，不再跳下一首
                count = 0;
            } else {
                //暂停一定时间后自动跳下一首
                if (!AssistantShowManager.getInstance().isShow()) {
                    count++;
                    ThreadDispatcher.getDispatcher().postDelayed(needBuyAutoNext, AUTO_NEXT_DELAY);
                } else {
                    count = 0;
                }
            }
        } else if (errorCode == ErrorCode.NEED_VIP && !OnlineMusicFactory.getKWLogin().isCarVipUser()) {
            if (AssistantShowManager.getInstance().isShow()) {
                return;
            }
            //选择差一点的音质播放, 1，流畅，2.高品，3.超品，4.无损
            int current = OnlineMusicFactory.getKWPlayer().getDownloadWhenPlayQuality();
            if (current > 1) {
                current--;
                KLog.d("playfailed_quality auto down quality : " + current);
                OnlineMusicFactory.getKWPlayer().setDownloadWhenPlayQuality(current);
                OnlineMusicFactory.getKWPlayer().continuePlay();
            } else {
                KLog.d("playfailed_quality lowest quality , auto play next");
                //需要vip且用户不是vip,且最低音质都无法播放，(将音质重新设置回用户选择的音质，)10S后自动下一首
//                OnlineMusicFactory.getKWPlayer().setDownloadWhenPlayQuality(MusicQualityManager.getInstance().getCustomQuality());
                ThreadDispatcher.getDispatcher().postDelayed(needVipAutoNext, AUTO_NEXT_DELAY_VIP);
            }
        }
    }

    private Runnable needBuyAutoNext = new Runnable() {
        @Override
        public void run() {
            int playMode = OnlineMusicFactory.getKWPlayer().getPlayMode();
            int index = OnlineMusicFactory.getKWPlayer().getNowPlayMusicIndex();
            XMMusicList nowPlayingList = OnlineMusicFactory.getKWPlayer().getNowPlayingList();
            if (nowPlayingList == null || AssistantShowManager.getInstance().isShow()) {
                return;
            }
            int size = nowPlayingList.toList().size();
            if (PlayMode.MODE_ALL_ORDER == playMode && index == size - 1) {
                //已经是最后一首了，直接暂停
                count = 0;
                return;
            }
            OnlineMusicFactory.getKWPlayer().playNext();
        }
    };

    private Runnable needVipAutoNext = new Runnable() {
        @Override
        public void run() {
            int playMode = OnlineMusicFactory.getKWPlayer().getPlayMode();
            int index = OnlineMusicFactory.getKWPlayer().getNowPlayMusicIndex();
            XMMusicList nowPlayingList = OnlineMusicFactory.getKWPlayer().getNowPlayingList();
            if (nowPlayingList == null || AssistantShowManager.getInstance().isShow()) {
                return;
            }
            int size = nowPlayingList.toList().size();
            if (PlayMode.MODE_ALL_ORDER == playMode && index == size - 1) {
                //已经是最后一首了，直接暂停
                return;
            }
            OnlineMusicFactory.getKWPlayer().playNext();
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ErrorCode.NO_COPY_RIGHT, ErrorCode.NEED_VIP, ErrorCode.NEED_SING_SONG,
            ErrorCode.NEED_ALBUM, ErrorCode.NOT_ENOUGH, ErrorCode.OTHER_PLAY_ERROR})
    public @interface ErrorCode {
        int NO_COPY_RIGHT = 0;
        int NEED_VIP = -1;
        int NEED_SING_SONG = -2;
        int NEED_ALBUM = -3;
        int NOT_ENOUGH = -4;
        int OTHER_PLAY_ERROR = -5;
    }
}
