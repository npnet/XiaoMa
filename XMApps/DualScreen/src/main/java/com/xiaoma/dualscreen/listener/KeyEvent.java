package com.xiaoma.dualscreen.listener;

/**
 * @author: iSun
 * @date: 2019/3/7 0007
 */
public enum KeyEvent {
    UP(1),
    DOWN(2),
    Left(3),
    Right(4),
    OK(5),
    CANCEL(6),
    AR_ON(7),
    AR_OFF(8);

    private int state;

    KeyEvent(int state) {
        this.state = state;
    }

    public int getKeyEvent() {
        return state;
    }

}
