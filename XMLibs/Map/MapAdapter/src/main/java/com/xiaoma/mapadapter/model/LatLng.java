package com.xiaoma.mapadapter.model;

/**
 * 定位回传的数据层Model， 供给上层UI展示所用， 需要增加字段可以根据业务拓展
 * Created by minxiwen on 2017/12/11 0011.
 */

public class LatLng {
    public double latitude;
    public double longitude;

    public LatLng() {

    }

    public LatLng(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
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

    public void setLongtitude(double longitude) {
        this.longitude = longitude;
    }
}
