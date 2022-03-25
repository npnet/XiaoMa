package com.xiaoma.music.kuwo.proxy;

import android.content.Context;

import com.xiaoma.component.AppHolder;
import com.xiaoma.music.R;
import com.xiaoma.music.common.audiosource.AudioSource;
import com.xiaoma.music.common.audiosource.AudioSourceManager;
import com.xiaoma.music.common.manager.AudioFocusHelper;
import com.xiaoma.music.kuwo.impl.IKuwoConstant;
import com.xiaoma.music.kuwo.impl.IPlayerOperate;
import com.xiaoma.music.kuwo.listener.OnChargeQualityListener;
import com.xiaoma.music.kuwo.listener.OnMusicChargeListener;
import com.xiaoma.music.kuwo.manager.KuwoPlayerOperate;
import com.xiaoma.music.kuwo.model.XMMusic;
import com.xiaoma.music.kuwo.model.XMMusicList;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.log.KLog;

import java.util.List;
import java.util.Map;

import cn.kuwo.service.MainService;

/**
 * Created by ZYao.
 * Date ：2018/10/16 0016
 */
public class XMKWPlayerOperateProxy implements IPlayerOperate {
    public static final String TAG = XMKWPlayerOperateProxy.class.getSimpleName();
    private final AudioSourceManager.PlayerProxy mAudioSourcePlayerProxy = new AudioSourceManager.PlayerProxy() {
        @Override
        public void continuePlay() {
            XMKWPlayerOperateProxy.this.continuePlay();
        }

        @Override
        public void pause() {
            XMKWPlayerOperateProxy.this.pause();
        }
    };

    private AudioFocusHelper mAudioFocusHelper;

    public static XMKWPlayerOperateProxy getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static final XMKWPlayerOperateProxy instance = new XMKWPlayerOperateProxy();
    }

    private IPlayerOperate playerOperate;

    private XMKWPlayerOperateProxy() {
        playerOperate = new KuwoPlayerOperate();
    }

    @Override
    public void init(Context context) {
        mAudioFocusHelper = new AudioFocusHelper(context.getApplicationContext(), new AudioFocusHelper.PlayerProxy() {
            @Override
            public boolean isPlaying() {
                return XMKWPlayerOperateProxy.this.isPlaying();
            }

            @Override
            public void continuePlayWithoutFocus() {
                doContinuePlay(false);
            }

            @Override
            public void pauseWithoutFocus() {
                doPause(false);
            }

            @Override
            public void setVolumeScale(float scale) {
                XMKWPlayerOperateProxy.this.setVolumeScale(scale);
            }
        });
        playerOperate.init(context.getApplicationContext());
    }

    @Override
    public int getNowPlayMusicIndex() {
        return playerOperate.getNowPlayMusicIndex();
    }

    @Override
    public XMMusicList getNowPlayingList() {
        return playerOperate.getNowPlayingList();
    }

    @Override
    public XMMusic getNowPlayingMusic() {
        return playerOperate.getNowPlayingMusic();
    }

    @Override
    public int getPlayMode() {
        return playerOperate.getPlayMode();
    }

    @Override
    public void setPlayMode(int var1) {
        playerOperate.setPlayMode(var1);
    }

    @Override
    public void play(XMMusic music) {
        if (doBeforePlay(true)) {
            playerOperate.play(music);
        }
    }

    @Override
    public void play(List<XMMusic> musics, int var2) {
        if (doBeforePlay(true)) {
            playerOperate.play(musics, var2);
        }
    }

    @Override
    public void playBillBroadFirst() {
        if (doBeforePlay(true)) {
            playerOperate.playBillBroadFirst();
        }
    }

    @Override
    public void playMusicList(XMMusicList musicList, int pos) {
        if (doBeforePlay(true)) {
            playerOperate.playMusicList(musicList, pos);
        }
    }

    @Override
    public void playLocalMusic(String showName, String filePath) {
        if (doBeforePlay(true)) {
            playerOperate.playLocalMusic(showName, filePath);
        }
    }

    @Override
    public void playLocalMusic(Map<String, String> local) {
        if (doBeforePlay(true)) {
            playerOperate.playLocalMusic(local);
        }
    }

    @Override
    public void playRadio(int cid, String name) {
        if (doBeforePlay(true)) {
            playerOperate.playRadio(cid, name);
        }
    }

    @Override
    public void playRadio(XMMusicList musicList) {
        if (doBeforePlay(true)) {
            playerOperate.playRadio(musicList);
        }
    }

    @Override
    public void pause() {
        doPause(true);
    }

    @Override
    public void stop() {
        playerOperate.stop();
        mAudioFocusHelper.abandonAudioFocus();
    }

    @Override
    public boolean continuePlay() {
        return doContinuePlay(true);
    }

    @Override
    public boolean playNext() {
        if (doBeforePlay(true)) {
            return playerOperate.playNext();
        }
        return false;
    }

    @Override
    public boolean playPre() {
        if (doBeforePlay(true)) {
            return playerOperate.playPre();
        }
        return false;
    }

    @Override
    public int getStatus() {
        return playerOperate.getStatus();
    }

    @Override
    public int getDuration() {
        return playerOperate.getDuration();
    }

    @Override
    public int getCurrentPos() {
        return playerOperate.getCurrentPos();
    }

    @Override
    public void seek(int var1) {
        playerOperate.seek(var1);
    }

    @Override
    public int getBufferingPos() {
        return playerOperate.getBufferingPos();
    }

    @Override
    public int getVolume() {
        return playerOperate.getVolume();
    }

    @Override
    public void setVolume(int var1) {
        playerOperate.setVolume(var1);
    }

    @Override
    public void setVolumeScale(float scale) {
        playerOperate.setVolumeScale(scale);
    }

    @Override
    public int getMaxVolume() {
        return playerOperate.getMaxVolume();
    }

    @Override
    public void setMute(boolean var1) {
        playerOperate.setMute(var1);
    }

    @Override
    public boolean isMute() {
        return playerOperate.isMute();
    }

    @Override
    public int getPreparePercent() {
        return playerOperate.getPreparePercent();
    }

    @Override
    public void saveData(boolean var1) {
        playerOperate.saveData(var1);
    }

    @Override
    public void autoPlayNext() {
        playerOperate.autoPlayNext();
    }

    @Override
    public int getDownloadWhenPlayQuality() {
        return playerOperate.getDownloadWhenPlayQuality();
    }

    @Override
    public boolean setDownloadWhenPlayQuality(int quality) {
        return playerOperate.setDownloadWhenPlayQuality(quality);
    }

    @Override
    public int getNowPlayMusicBestQuality() {
        return playerOperate.getNowPlayMusicBestQuality();
    }

    @Override
    public void chargeNowPlayMusic(int quality, OnChargeQualityListener listener) {
        playerOperate.chargeNowPlayMusic(quality, listener);
    }

    @Override
    public void chargeMusics(List<XMMusic> musics, OnMusicChargeListener listener) {
        playerOperate.chargeMusics(musics, listener);
    }

    public boolean doBeforePlay(boolean requestFocus) {
        AudioSourceManager.getInstance().switchAudioSource(AudioSource.ONLINE_MUSIC, mAudioSourcePlayerProxy);
        if (requestFocus) {
            boolean success = mAudioFocusHelper.requestAudioFocus(AudioSource.ONLINE_MUSIC);
            if (!success) {
                XMToast.showToast(AppHolder.getInstance().getAppContext(), R.string.play_failed_for_audio_focus);
                KLog.e(TAG, "request audio focus failed");
            }
            return success;
        }
        return true;
    }

    public boolean hasAudioFocus() {
        return mAudioFocusHelper.hasAudioFocus();
    }

    private boolean doContinuePlay(boolean requestFocus) {
        if (doBeforePlay(requestFocus)) {
            return playerOperate.continuePlay();
        }
        return false;
    }

    public void doPause(boolean abandonFocus) {
        playerOperate.pause();
        if (abandonFocus) {
            mAudioFocusHelper.abandonAudioFocus();
        }
    }

    public void togglePauseAndPlay() {
        int audioState = playerOperate.getStatus();
        if (IKuwoConstant.AudioHelper.isPlayOrBuffering(audioState)) {
            pause();
        } else {
            continuePlay();
        }
    }

    public boolean isPlaying() {
        int audioState = playerOperate.getStatus();
        return IKuwoConstant.AudioHelper.isPlayOrBuffering(audioState);
    }

    public void exitPlayer() {
        pause();
        MainService.disconnect();
        saveData(true);
    }

    public boolean isAudioFocusLossTransient() {
        return mAudioFocusHelper.isAudioFocusLossTransient();
    }

}
