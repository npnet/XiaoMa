package com.xiaoma.launcher.favorites;

import com.xiaoma.carlib.manager.XmCarConfigManager;
import com.xiaoma.carlib.manager.XmSystemManager;
import com.xiaoma.carwxsdkimpl.service.XMCarManager;
import com.xiaoma.launcher.common.manager.LauncherBlueToothPhoneManager;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : wangkang
 *     time   : 2019/06/17
 *     desc   : 小马对外接口
 * </pre>
 */
public class CarInfoManager {
    private static CarInfoManager mCarInfoManager;
    private List<CarInfoListener> carInfoListeners = new ArrayList<>();

    private CarInfoManager() {
    }

    public static CarInfoManager getInstance() {
        if (mCarInfoManager == null) {
            synchronized (CarInfoManager.class) {
                if (mCarInfoManager == null) {
                    mCarInfoManager = new CarInfoManager();
                }
            }
        }
        return mCarInfoManager;
    }

    public void addCarInfoListener(CarInfoListener carInfoListener) {
        if (carInfoListener != null && !this.carInfoListeners.contains(carInfoListener)) {
            carInfoListeners.add(carInfoListener);
        }
    }

    public void removeCarInfoListener(CarInfoListener carInfoListener) {
        if (carInfoListener != null && this.carInfoListeners.contains(carInfoListener)) {
            carInfoListeners.remove(carInfoListener);
        }
    }

    public void cleanCarInfoListener() {
        if (carInfoListeners != null) {
            carInfoListeners.clear();
        }
    }

    /**
     * 是否支持AR导航
     */
    public boolean hasVrNav() {
        return XmCarConfigManager.hasVrNav();
    }

    /**
     * 是否高配仪表
     */
    public boolean isHighEndLcd() {
        return XmCarConfigManager.isHighEndLcd();
    }

    /**
     * 获取主题
     *
     * @return 0:默认主题:智慧
     * 1:轻奢主题
     * 2:盗梦主题
     */
    public int getCarTheme() {
        return XMCarManager.getInstance().getCurrentTheme();
    }


    /**
     * 获取当前数据开关状态
     */
    public void getDataSwitch() {
        XmSystemManager.getInstance().getCellulatData();
    }

    /**
     * 获取wifi工作模式
     */
    public void getWorkPattern() {
        XmSystemManager.getInstance().getWorkPattern();
    }

    /**
     * 设置车机的车速
     */
    public void setCarSpeed(int speed) {
        for (CarInfoListener carInfoListener : carInfoListeners) {
            carInfoListener.Speed(speed);
        }
    }

    /**
     * 设置方向盘的转角
     */
    public void setWheelAngle(int[] angle) {
        for (CarInfoListener carInfoListener : carInfoListeners) {
            carInfoListener.Angle(angle);
        }
    }

    /**
     * 设置车机状态
     */
    public void setAccState(int state) {
        for (CarInfoListener carInfoListener : carInfoListeners) {
            carInfoListener.AccState(state);
        }
    }


    /**
     * 设置档位
     */
    public void setGearData(int gear) {
        for (CarInfoListener carInfoListener : carInfoListeners) {
            carInfoListener.onGearData(gear);
        }
    }


    /**
     * 设置数据开关
     */
    public void setDataSwitch(boolean isDataSwitch) {
        for (CarInfoListener carInfoListener : carInfoListeners) {
            carInfoListener.onDataSwitch(isDataSwitch);
        }
    }

    /**
     * 设置wifi工作模式
     */
    public void setWorkPattern(int wp) {
        for (CarInfoListener carInfoListener : carInfoListeners) {
            carInfoListener.onWorkPattern(wp);
        }
    }

    /**
     * 拨打电话
     */
    public void call(String phoneNumber){
        LauncherBlueToothPhoneManager.getInstance().callPhone(phoneNumber);
    }

    /**
     * 车机信息监听
     */
    public interface CarInfoListener {

        void Speed(int speed);

        void Angle(int[] angle);

        /**
         * @param state off：0；running：2、
         */
        void AccState(int state);

        /**
         * 档位
         *
         * @param
         */
        void onGearData(int gear);

        /**
         * 数据开关回调
         *
         * @param isDataSwitch
         */
        void onDataSwitch(boolean isDataSwitch);

        /**
         * wifi状态回调
         *
         * @param wp (0:OFF,1:AP,2:STA)
         */
        void onWorkPattern(int wp);
    }
}
