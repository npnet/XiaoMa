package com.qiming.fawcard.synthesize.data.entity;

/**
 * Created by My on 2018/7/2.
 */

public class DrivedRequest {
    public String vin = "";
    public Type type;
    public Long startTime = 0L;
    public Long endTime = 0L;
    public Long size = 0L;

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public enum Type {
        WEEK("WEEK"), YEAR("YEAR"), MONTH("MONTH");
        public String type;

        Type(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }
}
