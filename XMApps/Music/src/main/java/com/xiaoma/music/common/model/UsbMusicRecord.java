package com.xiaoma.music.common.model;

import com.xiaoma.music.model.UsbMusic;

import java.io.Serializable;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/7/15 0015 20:18
 *   desc:
 * </pre>
 */
public class UsbMusicRecord implements Serializable {


    private UsbMusic usbMusic;
    private long curPosition;
    private int playMode;

    public UsbMusicRecord() {
    }

    public UsbMusic getUsbMusic() {
        return usbMusic;
    }

    public void setUsbMusic(UsbMusic usbMusic) {
        this.usbMusic = usbMusic;
    }

    public long getCurPosition() {
        return curPosition;
    }

    public void setCurPosition(long curPosition) {
        this.curPosition = curPosition;
    }

    public int getPlayMode() {
        return playMode;
    }

    public void setPlayMode(int playMode) {
        this.playMode = playMode;
    }
}
