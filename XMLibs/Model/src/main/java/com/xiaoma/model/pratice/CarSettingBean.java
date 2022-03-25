package com.xiaoma.model.pratice;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.vrpractice.model
 *  @file_name:      CarSettingBean
 *  @author:         Rookie
 *  @create_time:    2019/6/11 20:16
 *  @description：   TODO             */

import android.os.Parcel;
import android.os.Parcelable;

public class CarSettingBean implements Parcelable {
    private float temperature;
    private CarWindowSettingBean mCarWindowSettingBean;
    private boolean backMirrorHeat;
    private boolean backWindowHeat;
    private boolean openWindow;
    private boolean seatHeat;
    private boolean openSunShade;
    private int openWindowLevel;
    private boolean mainSeatHeat;
    private boolean secondSeatHeat;
    private boolean openReadLed;
    private boolean openSceneLed;

    public boolean isOpenSunShade() {
        return openSunShade;
    }

    public void setOpenSunShade(boolean openSunShade) {
        this.openSunShade = openSunShade;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public CarWindowSettingBean getCarWindowSettingBean() {
        return mCarWindowSettingBean;
    }

    public void setCarWindowSettingBean(CarWindowSettingBean carWindowSettingBean) {
        mCarWindowSettingBean = carWindowSettingBean;
    }

    public boolean isBackMirrorHeat() {
        return backMirrorHeat;
    }

    public void setBackMirrorHeat(boolean backMirrorHeat) {
        this.backMirrorHeat = backMirrorHeat;
    }

    public boolean isBackWindowHeat() {
        return backWindowHeat;
    }

    public void setBackWindowHeat(boolean backWindowHeat) {
        this.backWindowHeat = backWindowHeat;
    }

    public boolean isOpenWindow() {
        return openWindow;
    }

    public void setOpenWindow(boolean openWindow) {
        this.openWindow = openWindow;
    }

    public boolean isSeatHeat() {
        return seatHeat;
    }

    public void setSeatHeat(boolean seatHeat) {
        this.seatHeat = seatHeat;
    }

    public int getOpenWindowLevel() {
        return openWindowLevel;
    }

    public void setOpenWindowLevel(int openWindowLevel) {
        this.openWindowLevel = openWindowLevel;
    }

    public boolean isMainSeatHeat() {
        return mainSeatHeat;
    }

    public void setMainSeatHeat(boolean mainSeatHeat) {
        this.mainSeatHeat = mainSeatHeat;
    }

    public boolean isSecondSeatHeat() {
        return secondSeatHeat;
    }

    public void setSecondSeatHeat(boolean secondSeatHeat) {
        this.secondSeatHeat = secondSeatHeat;
    }

    public boolean isOpenReadLed() {
        return openReadLed;
    }

    public void setOpenReadLed(boolean openReadLed) {
        this.openReadLed = openReadLed;
    }

    public boolean isOpenSceneLed() {
        return openSceneLed;
    }

    public void setOpenSceneLed(boolean openSceneLed) {
        this.openSceneLed = openSceneLed;
    }

    public CarSettingBean() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.temperature);
        dest.writeParcelable(this.mCarWindowSettingBean, flags);
        dest.writeByte(this.backMirrorHeat ? (byte) 1 : (byte) 0);
        dest.writeByte(this.backWindowHeat ? (byte) 1 : (byte) 0);
        dest.writeByte(this.openWindow ? (byte) 1 : (byte) 0);
        dest.writeByte(this.seatHeat ? (byte) 1 : (byte) 0);
        dest.writeByte(this.openSunShade ? (byte) 1 : (byte) 0);
        dest.writeInt(this.openWindowLevel);
        dest.writeByte(this.mainSeatHeat ? (byte) 1 : (byte) 0);
        dest.writeByte(this.secondSeatHeat ? (byte) 1 : (byte) 0);
        dest.writeByte(this.openReadLed ? (byte) 1 : (byte) 0);
        dest.writeByte(this.openSceneLed ? (byte) 1 : (byte) 0);
    }

    protected CarSettingBean(Parcel in) {
        this.temperature = in.readFloat();
        this.mCarWindowSettingBean = in.readParcelable(CarWindowSettingBean.class.getClassLoader());
        this.backMirrorHeat = in.readByte() != 0;
        this.backWindowHeat = in.readByte() != 0;
        this.openWindow = in.readByte() != 0;
        this.seatHeat = in.readByte() != 0;
        this.openSunShade = in.readByte() != 0;
        this.openWindowLevel = in.readInt();
        this.mainSeatHeat = in.readByte() != 0;
        this.secondSeatHeat = in.readByte() != 0;
        this.openReadLed = in.readByte() != 0;
        this.openSceneLed = in.readByte() != 0;
    }

    public static final Creator<CarSettingBean> CREATOR = new Creator<CarSettingBean>() {
        @Override
        public CarSettingBean createFromParcel(Parcel source) {
            return new CarSettingBean(source);
        }

        @Override
        public CarSettingBean[] newArray(int size) {
            return new CarSettingBean[size];
        }
    };
}
