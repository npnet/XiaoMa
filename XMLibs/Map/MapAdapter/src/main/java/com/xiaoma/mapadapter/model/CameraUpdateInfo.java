package com.xiaoma.mapadapter.model;

/**
 * 可视区域变化的model
 * Created by minxiwen on 2017/12/13 0013.
 */

public class CameraUpdateInfo {
    //中心点
    private LatLng latLng;
    //缩放比例
    private float factor;

    public CameraUpdateInfo(LatLng latLng, float factor) {
        this.latLng = latLng;
        this.factor = factor;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public float getFactor() {
        return factor;
    }

    public void setFactor(float factor) {
        this.factor = factor;
    }
}
