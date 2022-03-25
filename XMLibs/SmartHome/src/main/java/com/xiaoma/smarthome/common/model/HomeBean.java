package com.xiaoma.smarthome.common.model;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.smarthome.common.model
 *  @file_name:      HomeBean
 *  @author:         Rookie
 *  @create_time:    2019/4/24 19:54
 *  @description：   TODO             */

public class HomeBean {

    private double latitude;
    private double longitude;
    private String address;

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
