package com.xiaoma.carlib.manager;

import com.xiaoma.carlib.model.CarEvent;

/**
 * @author: iSun
 * @date: 2019/4/3 0003
 */
public interface ICarEvent {
    public void onCarEvent(CarEvent event);
}
