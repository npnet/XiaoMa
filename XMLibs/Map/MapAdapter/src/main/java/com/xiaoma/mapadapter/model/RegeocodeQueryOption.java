package com.xiaoma.mapadapter.model;

/**
 * 反地理编码查询配置
 * Created by minxiwen on 2017/12/12 0012.
 */

public class RegeocodeQueryOption {
    //中心点
    private LatLonPoint centerPoint;
    //半径
    private float radius;
    //坐标类型
    private CoordinateType type;

    public RegeocodeQueryOption(LatLonPoint centerPoint, float radius, CoordinateType type) {
        this.centerPoint = centerPoint;
        this.radius = radius;
        this.type = type;
    }

    public LatLonPoint getCenterPoint() {
        return centerPoint;
    }

    public void setCenterPoint(LatLonPoint centerPoint) {
        this.centerPoint = centerPoint;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public CoordinateType getType() {
        return type;
    }

    public void setType(CoordinateType type) {
        this.type = type;
    }
}
