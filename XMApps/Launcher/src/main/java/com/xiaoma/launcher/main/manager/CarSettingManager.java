package com.xiaoma.launcher.main.manager;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.main.manager
 *  @file_name:      CarSettingManager
 *  @author:         Rookie
 *  @create_time:    2019/3/8 10:46
 *  @description：   TODO             */

import android.content.Context;
import android.text.TextUtils;

import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.utils.log.KLog;

public class CarSettingManager {

    private static final String TAG = CarSettingManager.class.getSimpleName();

    private static CarSettingManager instance;

    /*媒体音量：0-40
    通话音量：1-40
    语音音量：0-8
    蓝牙音乐：0-40*/

    //默认媒体音量
    private static final int DEFAULT_MEDIA_VOLUME = 16;
    //媒体音量最大值
    public static final int MAX_MEDIA_VOLUNE = 40;
    //屏幕亮度最大值
    public static final int MAX_DISPLAY_BRIGHTNESS_VALUE = 10;

    private int mDisplayMode;

    //亮度分为三个模式 自动 白天 黑夜
    private static final int BRITHNESS_AUTO = 0;
    private static final int BRITHNESS_DAY = 1;
    private static final int BRITHNESS_NIGHT = 2;
    private static final String VIN_INFO = "LFPH4ABC848A05090";


    public static CarSettingManager getInstance() {
        if (instance == null) {
            synchronized (CarSettingManager.class) {
                if (instance == null) {
                    instance = new CarSettingManager();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        XmCarFactory.init(context);
    }

    public void setMediaVolume(int value) {
        XmCarFactory.getCarAudioManager().setStreamVolume(SDKConstants.MEDIA_VOLUME, value);
    }

    /**
     * 如果获取不到车机音量会返回Integer.MAX_VALUE, 那么返回默认值16
     *
     * @return
     */
    public int getMediaVolume() {
        int streamVolume = XmCarFactory.getCarAudioManager().getStreamVolume(SDKConstants.MEDIA_VOLUME);
        return streamVolume == Integer.MAX_VALUE ? DEFAULT_MEDIA_VOLUME : streamVolume;
    }

    public void setBrightness(int value) {
        mDisplayMode = XmCarFactory.getCarVendorExtensionManager().getDisplayMode();
        KLog.d("CarSettingManager", "displayMode =" + mDisplayMode);
        if (mDisplayMode == BRITHNESS_AUTO) {
            XmCarFactory.getCarVendorExtensionManager().setAutoDisplayLevel(value);
        } else if (mDisplayMode == BRITHNESS_DAY) {
            XmCarFactory.getCarVendorExtensionManager().setDayDisplayLevel(value);
        } else if (mDisplayMode == BRITHNESS_NIGHT) {
            XmCarFactory.getCarVendorExtensionManager().setNightDisplayLevel(value);
        }
    }

    /**
     * 返回屏幕亮度 范围值0~10
     *
     * @return
     */
    public int getBrightness() {
        int brightness = 0;
        mDisplayMode = XmCarFactory.getCarVendorExtensionManager().getDisplayMode();
        if (mDisplayMode == BRITHNESS_AUTO) {
            brightness = XmCarFactory.getCarVendorExtensionManager().getAutoDisplayLevel();
        } else if (mDisplayMode == BRITHNESS_DAY) {
            brightness = XmCarFactory.getCarVendorExtensionManager().getDayDisplayLevel();
        } else if (mDisplayMode == BRITHNESS_NIGHT) {
            brightness = XmCarFactory.getCarVendorExtensionManager().getNightDisplayLevel();
        }
        return brightness;
    }

    /**
     * 开关蓝牙
     */
    public void toggleBt() {
        XmCarFactory.getSystemManager().setBlueToothStatus(!getBtState());
    }

    public boolean getBtState() {
        return XmCarFactory.getSystemManager().getBlueToothStatus();
    }

    /**
     * 开关流量
     */
    public int toggleInternet(boolean isOpenData) {
        return XmCarFactory.getSystemManager().setDataSwitch(isOpenData);
    }

    /**
     * 获取移动数据是否打开
     */
    public void getInternet() {
        XmCarFactory.getSystemManager().getCellulatData();
    }

    public boolean getInternetState() {
        return false;
    }

    public void getWorkPattern() {
        XmCarFactory.getSystemManager().getWorkPattern();
    }

    /**
     * 开关热点
     */
    public int toggleHotspot(boolean isOpenHotSpot) {
        if (isOpenHotSpot) {
            return XmCarFactory.getSystemManager().setWorkPattern(SDKConstants.WifiMode.AP);
        } else {
            return XmCarFactory.getSystemManager().setWorkPattern(SDKConstants.WifiMode.OFF);
        }
    }

    /**
     * 开关wifi
     */
    public int toggleWifi(boolean isOpenWifi) {
        if (isOpenWifi) {
            return XmCarFactory.getSystemManager().setWorkPattern(SDKConstants.WifiMode.STA);
        } else {
            return XmCarFactory.getSystemManager().setWorkPattern(SDKConstants.WifiMode.OFF);
        }
    }

    public boolean getWifiState() {
        return XmCarFactory.getSystemManager().getWIfiStatus();
    }

    /**
     * 关屏幕
     */
    public void turnOff() {
        XmCarFactory.getCarVendorExtensionManager().closeScreen();
    }


    /**
     * 获取Vin码
     *
     * @return
     */
    public String getVinInfo() {
        String vinInfo = XmCarFactory.getCarVendorExtensionManager().getVinInfo();
        if (TextUtils.isEmpty(vinInfo)) {
            vinInfo = VIN_INFO;
        }
        return vinInfo;
    }

    /**
     * 获取引擎状态
     *
     * @return
     */
    public int getEngineStatus() {

        int state = XmCarFactory.getCarVendorExtensionManager().getEngineState();

        if (state == SDKConstants.EngineState.ENGINE_OFF) {
            state = 0;
        } else if (state == SDKConstants.EngineState.ENGINE_RUNNING) {
            state = 1;
        }

        return state;
    }

    /**
     * 续航里程
     *
     * @return
     */
    public int getOdometerResidual() {
        return XmCarFactory.getCarVendorExtensionManager().getOdometerResidual();
    }

    /**
     * 获取平均油耗 (int L/100Km,取得的值*0.01)
     *
     * @return
     */
    public int getFuelConsumption() {
        return XmCarFactory.getCarVendorExtensionManager().getFuelConsumption();
    }

}
