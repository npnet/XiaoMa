package com.xiaoma.smarthome.common.model;

import android.support.annotation.NonNull;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class LocalDeviceInfo implements MultiItemEntity, Comparable<LocalDeviceInfo> {

    public static final int TYPE_OTHERS = 1;
    public static final int TYPE_LEAVE_HOME = 2;
    public static final int TYPE_ARRIVE_HOME = 3;

    private String deviceName;
    private String deviceType;
    private int sceneType;
    private boolean isAuto;

    public LocalDeviceInfo() {

    }

    public LocalDeviceInfo(String deviceName, String deviceType, int sceneType) {
        this.deviceName = deviceName;
        this.deviceType = deviceType;
        this.sceneType = sceneType;
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

    public int getSceneType() {
        return sceneType;
    }

    public void setSceneType(int sceneType) {
        this.sceneType = sceneType;
    }

    @Override
    public String toString() {
        return "LocalDeviceInfo{" +
                "deviceName='" + deviceName + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", sceneType=" + sceneType +
                '}';
    }

    @Override
    public int getItemType() {
        return sceneType;
    }

    public boolean isAuto() {
        return isAuto;
    }

    public void setAuto(boolean auto) {
        isAuto = auto;
    }

    @Override
    public int compareTo(@NonNull LocalDeviceInfo localDeviceInfo) {
        return localDeviceInfo.getSceneType() - getSceneType();
    }
}
