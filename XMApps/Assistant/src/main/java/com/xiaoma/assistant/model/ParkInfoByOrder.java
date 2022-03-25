package com.xiaoma.assistant.model;

import java.io.Serializable;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/11/14
 * Desc:
 */

public class ParkInfoByOrder implements Serializable {
    public int order;
    private String parkName;//停车场名称
    private String parkAddress;//停车场地址
    private String parkLon;//经度
    private String parkLat;//纬度
    private String lastParkNum;//空车位
    private String totalParkNum;//总车位

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getParkName() {
        return parkName;
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }

    public String getParkAddress() {
        return parkAddress;
    }

    public void setParkAddress(String parkAddress) {
        this.parkAddress = parkAddress;
    }

    public String getParkLon() {
        return parkLon;
    }

    public void setParkLon(String parkLon) {
        this.parkLon = parkLon;
    }

    public String getParkLat() {
        return parkLat;
    }

    public void setParkLat(String parkLat) {
        this.parkLat = parkLat;
    }

    public String getLastParkNum() {
        return lastParkNum;
    }

    public void setLastParkNum(String lastParkNum) {
        this.lastParkNum = lastParkNum;
    }

    public String getTotalParkNum() {
        return totalParkNum;
    }

    public void setTotalParkNum(String totalParkNum) {
        this.totalParkNum = totalParkNum;
    }
}
