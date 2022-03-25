package com.xiaoma.smarthome.common.model;

public class AirControl {

    //制冷模式
    public static final int AIR_COLD = 1;
    //制热模式
    public static final int AIR_HOT = 2;

    //增加空调风速
    public static final int AIR_WIND_ADD = 11;
    //降低空调风速
    public static final int AIR_WIND_DEC = 12;

    private String deviceName;

    //温度
    private int temperature;

    //空调模式
    private int airMode;

    //风速模式
    private int airWindSpeed;

    public AirControl() {
    }

    public AirControl(String deviceName, int temperature, int airMode, int airWindSpeed) {
        this.deviceName = deviceName;
        this.temperature = temperature;
        this.airMode = airMode;
        this.airWindSpeed = airWindSpeed;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public int getAirMode() {
        return airMode;
    }

    public void setAirMode(int airMode) {
        this.airMode = airMode;
    }

    public int getAirWindSpeed() {
        return airWindSpeed;
    }

    public void setAirWindSpeed(int airWindSpeed) {
        this.airWindSpeed = airWindSpeed;
    }

    @Override
    public String toString() {
        return "AirControl{" +
                "deviceName='" + deviceName + '\'' +
                ", temperature='" + temperature + '\'' +
                ", airMode='" + airMode + '\'' +
                ", airWindSpeed='" + airWindSpeed + '\'' +
                '}';
    }
}
