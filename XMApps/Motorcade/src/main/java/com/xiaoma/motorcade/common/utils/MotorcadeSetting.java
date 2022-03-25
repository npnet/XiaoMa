package com.xiaoma.motorcade.common.utils;

import android.content.Context;

import com.xiaoma.utils.log.KLog;

import net.grandcentrix.tray.TrayPreferences;

/**
 * 简介: sharePreference存储类
 *
 * @author lingyan
 */
public class MotorcadeSetting {
    private static final String TAG = MotorcadeSetting.class.getSimpleName();
    private static TrayPreferences sPreferences;

    public static void init(Context context) {
        sPreferences = new TrayPreferences(context, TAG, 1);
    }

    /**
     * 是否接收其他用户语音
     */
    public static boolean isReceive(String key) {
        return sPreferences.getBoolean(key, true);
    }

    /**
     * 设置是否接收语音
     */
    public static void setReceiveSwitch(String key, boolean isOpen) {
        sPreferences.put(key, isOpen);
        KLog.d("isOpen: " + isOpen);
    }
}
