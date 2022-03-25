package com.xiaoma.carlib.model;

import java.io.Serializable;

/**
 * @author: iSun
 * @date: 2019/4/3 0003
 */
public class CarEvent implements Serializable {
    public CarEvent() {

    }

    public CarEvent(int id, int areaId, Object value) {
        this.id = id;
        this.areaId = areaId;
        this.value = value;
    }

    public int id;
    public int areaId;
    public Object value;
}
