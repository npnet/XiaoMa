package com.xiaoma.mapadapter.model;

import android.graphics.Bitmap;

/**
 * 地图覆盖物配置
 * <p>
 * Created by minxiwen on 2017/12/11 0011.
 */

public class MarkerOption {
    private LatLng latLng;
    private Bitmap bitmap;
    private float anchor1;
    private float anchor2;
    private String title;

    public MarkerOption position(LatLng latLng) {
        this.latLng = latLng;
        return this;
    }

    public MarkerOption anchor(float anchor1, float anchor2) {
        this.anchor1 = anchor1;
        this.anchor2 = anchor2;
        return this;
    }

    public MarkerOption icon(Bitmap bitmap) {
        this.bitmap = bitmap;
        return this;
    }

    public MarkerOption title(String title) {
        this.title = title;
        return this;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public float getAnchor1() {
        return anchor1;
    }

    public void setAnchor1(float anchor1) {
        this.anchor1 = anchor1;
    }

    public float getAnchor2() {
        return anchor2;
    }

    public void setAnchor2(float anchor2) {
        this.anchor2 = anchor2;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
