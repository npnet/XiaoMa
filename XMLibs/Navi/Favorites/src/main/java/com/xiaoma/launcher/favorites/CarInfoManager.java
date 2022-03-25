package com.xiaoma.launcher.favorites;

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
    private List<CarInfoListener> carInfoListeners;

    private CarInfoManager() {
    }

    public static CarInfoManager getInstance() {
        return null;
    }

    public void addCarInfoListener(CarInfoListener carInfoListener) {
    }

    public void removeCarInfoListener(CarInfoListener carInfoListener) {
    }

    public void cleanCarInfoListener() {
    }

    /**
     * 是否支持AR导航
     */
    public boolean hasVrNav() {
        return false;
    }

    /**
     * 是否高配仪表
     */
    public boolean isHighEndLcd() {
        return false;
    }

    /**
     * 获取主题
     *
     * @return 0:默认主题:智慧
     * 1:轻奢主题
     * 2:盗梦主题
     */
    public int getCarTheme() {
        return 0;
    }

    /**
     * 获取当前数据开关状态
     */
    public void getDataSwitch() {

    }

    /**
     * 获取wifi工作模式
     */
    public void getWorkPattern() {

    }

    /**
     * 拨打电话
     */
    public void call(String phoneNumber){

    }

    /**
     * 车机信息监听
     */
    public interface CarInfoListener {
        /**
         * 车机速度回调接口
         *
         * @param speed 单位是km/h
         */
        void Speed(int speed);

        /**
         * 方向盘转角回调接口
         *
         * @param angle int[1]:角度，使用(D)*0.04375°转换，D为取到的值
         *              int[2]:0:LEFT,1:RIGHT
         */
        void Angle(int[] angle);

        /**
         * 车机状态回调接口
         *
         * @param state off：0；running：2
         */
        void AccState(int state);

        /**
         * 档位
         *
         * @param
         */
        void onGearData(int gear);

        /**
         * getdataswitcher()
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
