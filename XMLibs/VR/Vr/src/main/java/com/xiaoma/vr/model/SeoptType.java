package com.xiaoma.vr.model;

/**
 * @author: iSun
 * @date: 2019/3/18 0018
 */
public enum SeoptType {
    CLOSE(1),
    LEFT(2),
    RIGHT(3),
    AUTO(4);
    private int type;

    SeoptType(int type) {
        this.type = type;
    }

    public int getValue() {
        return type;
    }


}
