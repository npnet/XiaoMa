package com.xiaoma.dualscreen.constant;

/**
 * @author: iSun
 * @date: 2018/12/29 0029
 */
public enum TabState {
    INFO(1),//信息
    HELP(2),//辅助
    ITINERARY(3),//行程
    MEDIA(4),//媒体
    PHONE(5),//电话
    NAVI(6),//导航
    LEFT(7), //左边区域
    SIMPLE(8),  //概要信息
    LEFT_GONE(9); //左边区域隐藏

    private int state;

    TabState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }
}
