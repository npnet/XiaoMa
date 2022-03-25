package com.xiaoma.smarthome.common.model;

/**
 * 执行条件
 */
public class ExecuteCondition {
    private String startTime;
    private String endTime;
    private int distance;

    public ExecuteCondition(String startTime, String endTime, int distance) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.distance = distance;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
