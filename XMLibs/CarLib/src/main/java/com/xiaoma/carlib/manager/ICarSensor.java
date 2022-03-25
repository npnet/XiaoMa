package com.xiaoma.carlib.manager;

import com.xiaoma.carlib.model.GearData;

/**
 * @author: iSun
 * @date: 2018/12/24 0024
 */
public interface ICarSensor {

    /**
     * 获取当前档位
     *
     * @return
     */
    GearData getCurrentGearData();

    /**
     * 获取方向转角
     * int[2]
     * int[1]:角度,使用(D)*0.04375°转换,D为取到的值
     * int[2]:0: LEFT, 1:RIGHT
     *
     * @return
     */
    int[] getWheelAngel();

    boolean isConditionMeet();
}
