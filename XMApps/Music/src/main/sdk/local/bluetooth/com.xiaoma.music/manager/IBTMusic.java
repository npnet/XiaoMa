package com.xiaoma.music.manager;

import android.content.Context;

import com.xiaoma.music.callback.OnBTConnectStateChangeListener;
import com.xiaoma.music.callback.OnBTMusicChangeListener;
import com.xiaoma.music.common.model.PlayStatus;
import com.xiaoma.music.model.BTMusic;
import com.xiaoma.music.model.XMBluetoothDevice;

import java.util.List;

/**
 * Created by ZYao.
 * Date ：2018/10/13 0013
 */
public interface IBTMusic {
    void init(Context context);

    void initMediaSession(Context context);

    void requestBluetoothAudioFocus();

    void startMediaSession();

    void stopMediaSession();

    void registerBluetoothMusicReceiver(Context context);

    void addBtStateChangeListener(OnBTConnectStateChangeListener listener);

    void removeBtStateChangeListener(OnBTConnectStateChangeListener listener);

    void addBtMusicInfoChangeListener(OnBTMusicChangeListener listener);

    void removeBtMusicInfoChangeListener(OnBTMusicChangeListener listener);

    List<XMBluetoothDevice> getBTConnectDevice();

    void switchPlay(boolean play);

    void switchPlay();

    void nextMusic();

    void preMusic();

    void seek(long seek);

    boolean isPlaying(XMBluetoothDevice device);

    boolean isPlaying();

    BTMusic getCurrBTMusic();

    @PlayStatus
    int getBtPlayStatus();
}
