package com.xiaoma.music.manager;

import android.content.Context;

import com.xiaoma.music.callback.OnUsbMusicChangedListener;
import com.xiaoma.music.common.model.PlayStatus;
import com.xiaoma.music.model.UsbMusic;

/**
 * Created by ZYao.
 * Date ：2018/10/13 0013
 */
public interface IUsbMusic {

    void init(Context context);

    boolean play(UsbMusic usbMusic);

    void switchPlay(boolean isPlay);

    void continuePlayOrPlayFirst();

    void switchPlayPause();

    boolean isPlaying();

    void stop();

    void destroy();

    void seekToPos(long position);

    UsbMusic getCurrUsbMusic();

    long getCurPosition();

    long getDuration();

    @PlayStatus
    int getPlayStatus();

    void addMusicChangeListener(OnUsbMusicChangedListener listener);

    void removeMusicChangeListener(OnUsbMusicChangedListener listener);

    boolean isAudioFocusLossTransient();

    void updateFastPlayProgress(int cur, int total);
}
