package com.xiaoma.service.main.model;

/**
 * Created by ZSH on 2018/12/10 0010.
 */
public class MaintenancePeriodBean {

    /**
     * KMUnit : KM
     * remainingKM : 9400
     * upkeepSize : 11
     * remainingTime : 6
     * timeUnit : 个月
     */

    private String KMUnit;
    private int remainingKM;
    private int upkeepSize;
    private int remainingTime;
    private String timeUnit;

    public String getKMUnit() {
        return KMUnit;
    }

    public void setKMUnit(String KMUnit) {
        this.KMUnit = KMUnit;
    }

    public int getRemainingKM() {
        return remainingKM;
    }

    public void setRemainingKM(int remainingKM) {
        this.remainingKM = remainingKM;
    }

    public int getUpkeepSize() {
        return upkeepSize;
    }

    public void setUpkeepSize(int upkeepSize) {
        this.upkeepSize = upkeepSize;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }
}
