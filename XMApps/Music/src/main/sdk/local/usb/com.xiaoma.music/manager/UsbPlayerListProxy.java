package com.xiaoma.music.manager;

import android.content.Context;

import com.xiaoma.music.model.UsbMusic;

import java.util.List;

/**
 * Created by ZYao.
 * Date ：2018/12/21 0021
 */
public class UsbPlayerListProxy implements IUsbMusicList {

    private IUsbMusicList playerListManager;

    public static UsbPlayerListProxy getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static final UsbPlayerListProxy instance = new UsbPlayerListProxy();
    }

    public UsbPlayerListProxy() {
        playerListManager = new PlayerListManager();
    }


    @Override
    public boolean playPre() {
       return playerListManager.playPre();
    }

    @Override
    public void autoPlayNext() {
        playerListManager.autoPlayNext();
    }

    @Override
    public boolean playNext() {
       return playerListManager.playNext();
    }

    @Override
    public void setPlayMode(int playMode) {
        playerListManager.setPlayMode(playMode);
    }

    @Override
    public int getPlayMode() {
        return playerListManager.getPlayMode();
    }

    @Override
    public void addStateChangeListen(PlayerListManager.IUsbPlayModeChangedListener listen) {
        playerListManager.addStateChangeListen(listen);
    }

    @Override
    public void removeStateChangeListen(PlayerListManager.IUsbPlayModeChangedListener listen) {
        playerListManager.removeStateChangeListen(listen);
    }

    @Override
    public void init(Context context) {
        playerListManager.init(context);
    }

    @Override
    public void addUsbMusicList(List<UsbMusic> musicList) {
        playerListManager.addUsbMusicList(musicList);
    }

    @Override
    public void replaceUsbMusicList(List<UsbMusic> musicList) {
        playerListManager.replaceUsbMusicList(musicList);
    }

    @Override
    public void removeUsbMusicList(List<UsbMusic> musicList) {
        playerListManager.removeUsbMusicList(musicList);
    }

    @Override
    public void removeAllUsbMusicList() {
        playerListManager.removeAllUsbMusicList();
    }

    @Override
    public List<UsbMusic> getUsbMusicList() {
        return playerListManager.getUsbMusicList();
    }

    @Override
    public List<UsbMusic> getDefaultUsbMusicList() {
        return playerListManager.getDefaultUsbMusicList();
    }

    @Override
    public void insertUsbMusic(UsbMusic music) {
        playerListManager.insertUsbMusic(music);
    }

    @Override
    public void insertUsbMusic(UsbMusic music, int position) {
        playerListManager.insertUsbMusic(music, position);
    }

    @Override
    public void deleteUsbMusic(UsbMusic music) {
        playerListManager.deleteUsbMusic(music);
    }

    @Override
    public void addUsbPlayListChangedListener(OnUsbPlayListChangedListener listener) {
        playerListManager.addUsbPlayListChangedListener(listener);
    }

    @Override
    public void removeUsbPlayListChangedListener(OnUsbPlayListChangedListener listener) {
        playerListManager.removeUsbPlayListChangedListener(listener);
    }

    @Override
    public void addUsbPlayTipsListener(IUsbPlayTipsListener listener) {
        playerListManager.addUsbPlayTipsListener(listener);
    }

    @Override
    public void removeUsbPlayTipsListener(IUsbPlayTipsListener listener) {
        playerListManager.removeUsbPlayTipsListener(listener);
    }
}
