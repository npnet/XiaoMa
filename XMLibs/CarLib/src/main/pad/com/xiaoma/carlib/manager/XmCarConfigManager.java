package com.xiaoma.carlib.manager;

import com.xiaoma.utils.DeviceUtils;

/**
 * Created by LKF on 2019-6-10 0010.
 * 车的配置信息管理类
 */
public class XmCarConfigManager {
    private static final int INDEX_BLUETOOTH_KEY = 12;
    private static final int INDEX_FACE_RECOGNITION = 10;
    private static final int INDEX_AR_NAV = 7;
    private static final int INDEX_MUSIC_MOOD_LIGHTING = 13;
    private static final int INDEX_LCD_SIZE = 8;
    private static final int INDEX_HOLOGRAM = 6;
    private static final int INDEX_360_LOOK_AROUND = 1;

    /**
     * 是否支持蓝牙钥匙
     */
    public static boolean hasBluetoothKey() {
        return hasConfig(INDEX_BLUETOOTH_KEY);
    }

    /**
     * 是否支持人脸识别
     */
    public static boolean hasFaceRecognition() {
        return DeviceUtils.isPro();
    }

    /**
     * 是否支持AR导航
     */
    public static boolean hasVrNav() {
        return hasConfig(INDEX_AR_NAV);
    }

    /**
     * 是否支持行程记录(Mark),有VR导航才支持行程记录
     */
    public static boolean hasJourneyRecord() {
        return hasVrNav();
    }

    /**
     * 是否支持音乐氛围灯律动
     */
    public static boolean hasMusicMoodLighting() {
        return hasConfig(INDEX_MUSIC_MOOD_LIGHTING);
    }

    /**
     * 是否高配仪表
     */
    public static boolean isHighEndLcd() {
        return false;
    }

    /**
     * 是否支持全息
     */
    public static boolean hasHologram() {
        return hasConfig(INDEX_HOLOGRAM);
    }

    /**
     * 是否360环视
     */
    public static boolean has360LookAround() {
        return hasConfig(INDEX_360_LOOK_AROUND);
    }

    private static boolean hasConfig(int index) {
        return true;
    }

    private static boolean hasConfig(int index, int hasFlag) {
        // PAD不支持车配置
        return false;
    }

    public static boolean hasCourtesyLamp() {
        return true;
    }

    public static boolean isShowWifiAboutItem() {
        return true;
    }

    public static boolean hasFcwAndAeb() {
        return true;
    }

    public static boolean hasBackMirrorAutoFold() {
        return false;
    }

    public static boolean hasAutoSeat() {
        return false;
    }

    public static boolean hasElectricTrunk() {
        return false;
    }

    public static boolean hasSmartHeadlight() {
        return false;
    }

    public static int getSalesArea() { return 0;}
}
