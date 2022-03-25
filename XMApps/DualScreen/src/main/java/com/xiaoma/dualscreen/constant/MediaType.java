package com.xiaoma.dualscreen.constant;

/**
 * @author: iSun
 * @date: 2019/1/7 0007
 */
public enum MediaType {
    USB_MUSIC(1),//USB音乐
    BLUE_MUSIC(2),//蓝牙音乐
    LINE_MUSIC(3),//在线音乐
    FM(4),//FM电台
    AM(5),//AM电台
    LINE_RADIO(6);//在线电台

    private int type;

    MediaType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
