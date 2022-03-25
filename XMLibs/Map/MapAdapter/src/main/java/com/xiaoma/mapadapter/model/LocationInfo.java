package com.xiaoma.mapadapter.model;

import java.io.Serializable;

/**
 * Created by minxiwen on 2017/12/11 0011.
 */

public class LocationInfo implements Serializable {
    private double latitude;
    private double longitude;
    private String province;
    private String city;
    private String address;
    private String district;
    private int errorCode;
    private String errorInfo;
    private float speed;
    private float accuracy;
    private float bearing;
    private int satellites;
    private int locationType;
    private double altitude;
    private int gpsAccuracyStatus;

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

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public int getSatellites() {
        return satellites;
    }

    public void setSatellites(int satellites) {
        this.satellites = satellites;
    }

    public int getLocationType() {
        return locationType;
    }

    public void setLocationType(int locationType) {
        this.locationType = locationType;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public int getGpsAccuracyStatus() {
        return gpsAccuracyStatus;
    }

    public void setGpsAccuracyStatus(int gpsAccuracyStatus) {
        this.gpsAccuracyStatus = gpsAccuracyStatus;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    @Override
    public String toString() {
        return "latitude:" + latitude + ", longitude:" + longitude + ", address:" + address + ", locType:" + locationType;
    }

    public static LocationInfo getDefault(){
        LocationInfo locationInfo = new LocationInfo();
        locationInfo.setLatitude(43.82123);
        locationInfo.setLongitude(125.23639);
        locationInfo.setProvince("吉林省");
        locationInfo.setCity("长春市");
        locationInfo.setDistrict("朝阳区");
        locationInfo.setAddress("蔚山路4888号");
        return locationInfo;
    }

}
