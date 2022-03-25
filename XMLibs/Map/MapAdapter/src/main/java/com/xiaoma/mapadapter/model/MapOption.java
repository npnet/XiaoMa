package com.xiaoma.mapadapter.model;

/**
 * Created by minxiwen on 2017/12/12 0012.
 */

public class MapOption {
    private double latitude;
    private double longitude;
    private float factor;

    public MapOption(double latitude, double longitude, float factor) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.factor = factor;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getFactor() {
        return factor;
    }

    public void setFactor(float factor) {
        this.factor = factor;
    }
}
