package com.xiaoma.vr.tts;

/**
 * Created by ZYao.
 * Date ：2019/7/2 0002
 */
public enum TtsPriority {

    HIGHEST(0),

    HIGHER(HIGHEST.value() + 10),

    HIGH(HIGHER.value() + 10),

    NORMAL(HIGH.value() + 10),

    LOW(NORMAL.value() + 10),

    LOWER(LOW.value() + 10),

    LOWEST(LOWER.value() + 10),

    DEFAULT(LOWEST.value() + 10);

    private int priority;


    TtsPriority(int priority) {
        this.priority = priority;
    }

    public int value() {
        return priority;
    }
}
