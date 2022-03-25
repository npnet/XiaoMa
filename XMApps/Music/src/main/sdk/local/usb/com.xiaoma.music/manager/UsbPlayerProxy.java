package com.xiaoma.music.manager;

import android.content.Context;

import com.xiaoma.music.callback.OnUsbMusicChangedListener;
import com.xiaoma.music.model.UsbMusic;


/**
 * Created by ZYao.
 * Date ：2018/10/13 0013
 */
public class UsbPlayerProxy implements IUsbMusic {

    public static UsbPlayerProxy getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static final UsbPlayerProxy instance = new UsbPlayerProxy();
    }

    private final IUsbMusic player = IJKPlayerManager.getInstance();

    @Override
    public void init(Context context) {
        player.init(context);
    }

    @Override
    public boolean play(UsbMusic usbMusic) {
        return player.play(usbMusic);
    }

    @Override
    public void switchPlay(boolean isPlay) {
        player.switchPlay(isPlay);
    }

    @Override
    public void continuePlayOrPlayFirst() {
        player.continuePlayOrPlayFirst();
    }

    @Override
    public void switchPlayPause() {
        player.switchPlayPause();
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public void stop() {
        player.stop();
    }

    @Override
    public void destroy() {
        player.destroy();
    }

    @Override
    public void seekToPos(long position) {
        player.seekToPos(position);
    }

    @Override
    public UsbMusic getCurrUsbMusic() {
        return player.getCurrUsbMusic();
    }

    @Override
    public long getCurPosition() {
        return player.getCurPosition();
    }

    @Override
    public long getDuration() {
        return player.getDuration();
    }

    @Override
    public int getPlayStatus() {
        return player.getPlayStatus();
    }

    @Override
    public void addMusicChangeListener(OnUsbMusicChangedListener listener) {
        player.addMusicChangeListener(listener);
    }

    @Override
    public void removeMusicChangeListener(OnUsbMusicChangedListener listener) {
        player.removeMusicChangeListener(listener);
    }

    public boolean isAudioFocusLossTransient() {
        return player.isAudioFocusLossTransient();
    }

    @Override
    public void updateFastPlayProgress(int cur, int total) {
        player.updateFastPlayProgress(cur, total);
    }

}
