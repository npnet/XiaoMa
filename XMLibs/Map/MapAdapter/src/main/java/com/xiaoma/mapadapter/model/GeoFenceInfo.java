package com.xiaoma.mapadapter.model;

/**
 * Created by minxiwen on 2017/12/13 0013.
 */

public class GeoFenceInfo {
    //中心点
    private LatLng latLng;
    //围栏半径
    private float radius;
    //自定义围栏ID（传入的业务唯一标识）
    private String customId;

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getCustomId() {
        return customId;
    }

    public void setCustomId(String customId) {
        this.customId = customId;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
