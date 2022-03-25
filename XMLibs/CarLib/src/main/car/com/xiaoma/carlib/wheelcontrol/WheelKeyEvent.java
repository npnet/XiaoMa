package com.xiaoma.carlib.wheelcontrol;

import android.car.hardware.vendor.Hardkey;

/**
 * Created by LKF on 2019-5-6 0006.
 * 方控按键
 */
public final class WheelKeyEvent {
    /**
     * 上
     */
    public static final int KEYCODE_WHEEL_UP = 0x1;
    /**
     * 下
     */
    public static final int KEYCODE_WHEEL_DOWN = 0x2;
    /**
     * 左
     */
    public static final int KEYCODE_WHEEL_LEFT = 0x3;
    /**
     * 右
     */
    public static final int KEYCODE_WHEEL_RIGHT = 0x4;
    /**
     * OK
     */
    public static final int KEYCODE_WHEEL_OK = 0x5;
    /**
     * Back
     */
    public static final int KEYCODE_WHEEL_BACK = 0x6;
    /**
     * 音量+
     */
    public static final int KEYCODE_WHEEL_VOL_ADD = Hardkey.WHEEL_VOL_ADD;
    /**
     * 音量-
     */
    public static final int KEYCODE_WHEEL_VOL_SUB = Hardkey.WHEEL_VOL_SUB;
    /**
     * 挂断/静音
     */
    public static final int KEYCODE_WHEEL_MUTE = Hardkey.WHEEL_MUTE;
    /**
     * 语音唤醒/接听
     */
    public static final int KEYCODE_WHEEL_VOICE = Hardkey.WHEEL_VOICE;

    /**
     * 上一首
     */
    public static final int KEYCODE_WHEEL_SEEK_SUB = Hardkey.WHEEL_KEY_DOWN;

    /**
     * 下一首
     */
    public static final int KEYCODE_WHEEL_SEEK_ADD = Hardkey.WHEEL_KEY_UP;

    /**
     * 释放行为
     */
    public static final int ACTION_RELEASE = Hardkey.RELEASE;
    /**
     * 按下行为
     */
    public static final int ACTION_PRESS = Hardkey.PRESS;
    /**
     * 单击
     */
    public static final int ACTION_CLICK = Hardkey.CLICK;
    /**
     * 长按
     */
    public static final int ACTION_LONG_PRESS = Hardkey.LONG_PRESS;
}
