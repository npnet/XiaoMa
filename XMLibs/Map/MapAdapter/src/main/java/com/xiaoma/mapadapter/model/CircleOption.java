package com.xiaoma.mapadapter.model;

/**
 * 圆形覆盖物配置参数
 * Created by minxiwen on 2017/12/13 0013.
 */

public class CircleOption {
    //中心点
    private LatLng latLng;
    //半径
    private int radius;
    //填充颜色
    private int fillColor;
    //描边颜色
    private int strokeColor;
    //描边宽度
    private float strokeWidth;

    public CircleOption center(LatLng latLng) {
        this.latLng = latLng;
        return this;
    }

    public CircleOption radius(int radius) {
        this.radius = radius;
        return this;
    }

    public CircleOption strokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        return this;
    }

    public CircleOption strokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        return this;
    }

    public CircleOption fillColor(int fillColor) {
        this.fillColor = fillColor;
        return this;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getFillColor() {
        return fillColor;
    }

    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
    }

    public int getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }
}
