package com.xiaoma.music.proxy;

import android.content.Context;

import com.xiaoma.music.callback.OnBTConnectStateChangeListener;
import com.xiaoma.music.callback.OnBTMusicChangeListener;
import com.xiaoma.music.common.model.PlayStatus;
import com.xiaoma.music.manager.BTControlManager;
import com.xiaoma.music.manager.IBTMusic;
import com.xiaoma.music.model.BTMusic;
import com.xiaoma.music.model.XMBluetoothDevice;

import java.util.List;

/**
 * Created by ZYao.
 * Date ：2018/10/24 0024
 */
public class XMBTControlProxy implements IBTMusic {

    private IBTMusic btMusicControl;

    public static XMBTControlProxy getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static final XMBTControlProxy instance = new XMBTControlProxy();
    }

    public XMBTControlProxy() {
        btMusicControl = BTControlManager.getInstance();
    }

    @Override
    public void init(Context context) {
        btMusicControl.init(context);
        registerBluetoothMusicReceiver(context);
    }

    @Override
    public void initMediaSession(Context context) {
        btMusicControl.initMediaSession(context);
    }

    @Override
    public void requestBluetoothAudioFocus() {
        btMusicControl.requestBluetoothAudioFocus();
    }

    @Override
    public void startMediaSession() {
        btMusicControl.startMediaSession();
    }

    @Override
    public void stopMediaSession() {
        btMusicControl.stopMediaSession();
    }

    @Override
    public void registerBluetoothMusicReceiver(Context context) {
        btMusicControl.registerBluetoothMusicReceiver(context);
    }

    @Override
    public void addBtStateChangeListener(OnBTConnectStateChangeListener listener) {
        btMusicControl.addBtStateChangeListener(listener);
    }

    @Override
    public void removeBtStateChangeListener(OnBTConnectStateChangeListener listener) {
        btMusicControl.removeBtStateChangeListener(listener);
    }

    @Override
    public void addBtMusicInfoChangeListener(OnBTMusicChangeListener listener) {
        btMusicControl.addBtMusicInfoChangeListener(listener);
    }

    @Override
    public void removeBtMusicInfoChangeListener(OnBTMusicChangeListener listener) {
        btMusicControl.removeBtMusicInfoChangeListener(listener);
    }

    @Override
    public List<XMBluetoothDevice> getBTConnectDevice() {
        return btMusicControl.getBTConnectDevice();
    }

    @Override
    public void switchPlay(boolean play) {
        btMusicControl.switchPlay(play);
    }

    @Override
    public void switchPlay() {
        btMusicControl.switchPlay();
    }

    @Override
    public void nextMusic() {
        btMusicControl.nextMusic();
    }

    @Override
    public void preMusic() {
        btMusicControl.preMusic();
    }

    @Override
    public void seek(long seek) {
        btMusicControl.seek(seek);
    }

    @Override
    public boolean isPlaying(XMBluetoothDevice device) {
        return btMusicControl.isPlaying(device);
    }

    @Override
    public boolean isPlaying() {
        return btMusicControl.isPlaying();
    }

    @Override
    public BTMusic getCurrBTMusic() {
        return btMusicControl.getCurrBTMusic();
    }

    @PlayStatus
    @Override
    public int getBtPlayStatus() {
        return btMusicControl.getBtPlayStatus();
    }
}
