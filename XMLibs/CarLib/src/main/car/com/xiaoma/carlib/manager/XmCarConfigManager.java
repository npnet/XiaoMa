package com.xiaoma.carlib.manager;

import android.car.hardware.vendor.CarConfigType;
import android.car.hardware.vendor.DLifeType;
import android.car.hardware.vendor.Destination;
import android.car.hardware.vendor.LcdSize;
import android.car.hardware.vendor.RvcAvsType;

import java.util.Objects;

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
    private static final int WIFI_FUNCTION = 2;
    private static final int FCW_AEB = 14;
    private static final int ELECTRIC_TRUNK = 0;
    private static final int COURTESY_LAMP = 15;
    private static final int SMART_HEADLIGHT = 17;
    private static final int AUTO_SEAT = 11;
    private static final int BACK_MIRROR = 22;
    private static final int SQURE_LIGHT = 18;
    private static final int SALES_AREA = 20;

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
        return hasConfig(INDEX_FACE_RECOGNITION);
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
     * 是否支持音乐氛围灯律动(氛围灯)
     */
    public static boolean hasMusicMoodLighting() {
        return hasConfig(INDEX_MUSIC_MOOD_LIGHTING);
    }

    /**
     * 是否高配仪表
     */
    public static boolean isHighEndLcd() {
        return hasConfig(INDEX_LCD_SIZE, LcdSize.INCH_12_3);
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
        return hasConfig(INDEX_360_LOOK_AROUND, RvcAvsType.FULL_SCENE);
    }

    private static boolean hasConfig(int index) {
        return hasConfig(index, CarConfigType.HAVE);
    }

    /**
     * 是否显示wifi和热点功能
     *
     * @return
     */
    public static boolean isShowWifiAboutItem() {
        return hasConfig(WIFI_FUNCTION, DLifeType.WIFI_NO_REMOTE) || hasConfig(WIFI_FUNCTION, DLifeType.WIFI_REMOTE);
//        return true;
    }

    /**
     * 是否有前防撞预警系统及主动紧急制动功能
     *
     * @return
     */
    public static boolean hasFcwAndAeb() {
        return hasConfig(FCW_AEB, CarConfigType.HAVE);
    }

    /**
     * 是否有行李箱智能开启功能(电尾门)
     *
     * @return
     */
    public static boolean hasElectricTrunk() {
        return hasConfig(ELECTRIC_TRUNK, CarConfigType.HAVE);
    }

    /**
     * 是否有迎宾灯
     *
     * @return
     */
    public static boolean hasCourtesyLamp() {
        return hasConfig(COURTESY_LAMP, CarConfigType.HAVE);
    }

    /**
     * 是否有智能大灯(IHC)
     *
     * @return
     */
    public static boolean hasSmartHeadlight() {
        return hasConfig(SMART_HEADLIGHT, CarConfigType.HAVE);
    }

    /**
     * 矩阵式前大灯
     *
     * @return
     */
    public static boolean hasSqureLight() {
        return hasConfig(SQURE_LIGHT, CarConfigType.HAVE);
    }

    /**
     * 是否支持座椅迎宾退让
     *
     * @return
     */
    public static boolean hasAutoSeat() {
        return hasConfig(AUTO_SEAT);
    }

    public static boolean hasBackMirrorAutoFold() {
        return hasConfig(BACK_MIRROR);
    }

    /**
     * 参见{@link android.car.hardware.vendor.Destination}
     *
     * @return 销售区域
     */
    public static int getSalesArea() {
        int config = getConfig(SALES_AREA);
        if (config == -1) {
            return Destination.ASIA;
        }
        return config;
    }

    private static int getConfig(int index) {
        Integer[] configInfo = XmCarVendorExtensionManager.getInstance().getConfigInfo();
        if (configInfo != null && index < configInfo.length) {
            return configInfo[index];
        }
        return -1;
    }

    private static boolean hasConfig(int index, int hasFlag) {
        if (index < 0)
            return false;
        Integer[] configInfo = XmCarVendorExtensionManager.getInstance().getConfigInfo();
        return configInfo != null
                && index < configInfo.length
                && Objects.equals(configInfo[index], hasFlag);
    }
}
