package com.xiaoma.carlib;

import android.content.Context;

import com.xiaoma.carlib.manager.ICarWholeInfo;
import com.xiaoma.carlib.manager.ISystem;
import com.xiaoma.carlib.manager.XmCarAudioManager;
import com.xiaoma.carlib.manager.XmCarCabinManager;
import com.xiaoma.carlib.manager.XmCarHvacManager;
import com.xiaoma.carlib.manager.XmCarInfoManager;
import com.xiaoma.carlib.manager.XmCarSensorManager;
import com.xiaoma.carlib.manager.XmCarUpdateManager;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.carlib.manager.ICarAudio;
import com.xiaoma.carlib.manager.ICarCabin;
import com.xiaoma.carlib.manager.ICarHvac;
import com.xiaoma.carlib.manager.ICarSensor;
import com.xiaoma.carlib.manager.IUpdate;
import com.xiaoma.carlib.manager.IVendorExtension;
import com.xiaoma.carlib.manager.XmMicManager;
import com.xiaoma.carlib.manager.XmSystemManager;
import com.xiaoma.utils.log.KLog;

import java.util.TreeMap;

/**
 * @author: iSun
 * @date: 2018/12/21 0021
 */
public class XmCarFactory {
    private static String TAG = XmCarFactory.class.getSimpleName();
    private static boolean isInit = false;

    //车身
    public static IVendorExtension getCarVendorExtensionManager() {
        return XmCarVendorExtensionManager.getInstance();
    }

    //音量
    public static ICarAudio getCarAudioManager() {
        return XmCarAudioManager.getInstance();
    }

    //升级
    public static IUpdate getUpdateManager() {
        return XmCarUpdateManager.getInstance();
    }

    //室内控制
    public static ICarCabin getCarCabinManager() {
        return XmCarCabinManager.getInstance();
    }

    //传感器
    public static ICarSensor getCarSensorManager() {
        return XmCarSensorManager.getInstance();
    }

    //空调
    public static ICarHvac getCarHvacManager() {
        return XmCarHvacManager.getInstance();
    }

    //wifi bluetooth
    public static ISystem getSystemManager() {
        return XmSystemManager.getInstance();
    }

    //全局信息
    public static ICarWholeInfo getCarInfoManager() {
        return XmCarInfoManager.getInstance();
    }

    public static void init(Context context) {
        KLog.e(TAG, "XmCarFactory init : ");
        if (context != null && !isInit) {
            isInit = true;
            XmSystemManager.getInstance().init(context);
            XmCarVendorExtensionManager.getInstance().init(context);
            XmCarAudioManager.getInstance().init(context);
            XmCarSensorManager.getInstance().init(context);
            XmCarHvacManager.getInstance().init(context);
            XmCarInfoManager.getInstance().init(context);
            XmCarUpdateManager.getInstance().init(context);
            XmCarCabinManager.getInstance().init(context);
            XmMicManager.getInstance().init(context);
        }
    }
}
