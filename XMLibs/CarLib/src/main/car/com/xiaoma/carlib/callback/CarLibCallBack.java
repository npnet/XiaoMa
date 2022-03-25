package com.xiaoma.carlib.callback;

import android.car.hardware.CarPropertyValue;

/**
 * @author: iSun
 * @date: 2018/12/25 0025
 */
public interface CarLibCallBack {
    void onChangeEvent(CarPropertyValue var1);

    void onErrorEvent(int var1, int var2);
}
