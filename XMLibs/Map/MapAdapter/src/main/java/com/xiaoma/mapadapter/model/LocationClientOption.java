package com.xiaoma.mapadapter.model;

/**
 * 定位参数配置, 可自行增加参数进行配置
 * Created by minxiwen on 2017/12/11 0011.
 */

public class LocationClientOption {

    private boolean isNeedAddress;
    private boolean isMockEnable;
    private long locationInterval;
    private LocationMode locationMode;
    private boolean isGpsFirst;
    private boolean onceLocation;

    public boolean isNeedAddress() {
        return isNeedAddress;
    }

    public LocationMode getLocationMode() {
        return locationMode;
    }

    public void setLocationMode(LocationMode locationMode) {
        this.locationMode = locationMode;
    }

    public void setNeedAddress(boolean needAddress) {
        isNeedAddress = needAddress;
    }

    public boolean isMockEnable() {
        return isMockEnable;
    }

    public void setMockEnable(boolean mockEnable) {
        isMockEnable = mockEnable;
    }

    public long getLocationInterval() {
        return locationInterval;
    }

    public void setLocationInterval(long locationInterval) {
        this.locationInterval = locationInterval;
    }

    public boolean isGpsFirst() {
        return isGpsFirst;
    }

    public void setGpsFirst(boolean gpsFirst) {
        isGpsFirst = gpsFirst;
    }

    public boolean isOnceLocation() {
        return onceLocation;
    }

    public void setOnceLocation(boolean onceLocation) {
        this.onceLocation = onceLocation;
    }
}
