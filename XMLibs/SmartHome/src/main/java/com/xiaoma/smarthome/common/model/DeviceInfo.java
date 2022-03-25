package com.xiaoma.smarthome.common.model;

public class DeviceInfo {


    private String deviceName;
    private String deviceType;
    private int deviceId;

    public DeviceInfo() {
    }

    public DeviceInfo(String deviceName, String deviceType, int deviceId) {
        this.deviceName = deviceName;
        this.deviceType = deviceType;
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "deviceName='" + deviceName + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", deviceId=" + deviceId +
                '}';
    }
}
