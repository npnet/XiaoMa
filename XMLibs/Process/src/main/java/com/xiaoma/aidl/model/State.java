package com.xiaoma.aidl.model;

/**
 * @author: iSun
 * @date: 2018/11/19 0019
 */
public enum State {
    INCOMING(1),//正在来电
    ACTIVE(2),//正在通话中
    IDLE(3),//空闲
    KEEP(4),//保留
    CALL(5);//正在拨号

    private int value;

    State(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static State getState(int value) {
        for (State state : State.values()) {
            if (state.value == value) {
                return state;
            }
        }
        return null;
    }

}
