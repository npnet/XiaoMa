package com.xiaoma.trip.parking.response;

public class ParkingSpotFeeStandardBean {

    /**
     * startTime : 00:00
     * endTime : 00:00
     * preTime : 0
     * price : 0.0
     * range : 1
     * unit : t
     */

    private String startTime;
    private String endTime;
    private String preTime;
    private String price;
    private String range;
    private String unit;

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

    public String getPreTime() {
        return preTime;
    }

    public void setPreTime(String preTime) {
        this.preTime = preTime;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
