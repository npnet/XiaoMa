package com.xiaoma.mapadapter.model;

/**
 * 坐标类型
 * Created by minxiwen on 2017/12/12 0012.
 */

public enum CoordinateType {
    GPS("gps"),
    GD("autonavi");

    String value;

    CoordinateType(String value) {
        this.value = value;
    }
}

