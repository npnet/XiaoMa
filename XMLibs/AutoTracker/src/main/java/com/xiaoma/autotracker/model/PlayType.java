package com.xiaoma.autotracker.model;

/**
 * @author taojin
 * @date 2018/12/24
 */
public enum PlayType {
    ONLINEMUSINC("在线音乐"),
    BLUETOOTHMUSIC("蓝牙音乐"),
    USBMUSIC("USB音乐"),
    ONLINERADIO("在线电台"),
    LOCALRADIO("本地电台"),;

    private String playType;

    PlayType(String playType) {
        this.playType = playType;
    }

    public String getPlayType() {
        return playType;
    }
}
