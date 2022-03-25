package com.xiaoma.model.pratice;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.vrpractice.model
 *  @file_name:      CarWindowSettingBean
 *  @author:         Rookie
 *  @create_time:    2019/6/11 20:16
 *  @description：   TODO             */

import android.os.Parcel;
import android.os.Parcelable;

public class CarWindowSettingBean implements Parcelable {

    private boolean mainSeatWindow;
    private boolean secondSeatWindow;
    private int mainSeatWindowLevel;
    private int secondSeatWindowLevel;
    private boolean backLeftWindow;
    private boolean backRightWindow;

    public boolean isMainSeatWindow() {
        return mainSeatWindow;
    }

    public void setMainSeatWindow(boolean mainSeatWindow) {
        this.mainSeatWindow = mainSeatWindow;
    }

    public boolean isSecondSeatWindow() {
        return secondSeatWindow;
    }

    public void setSecondSeatWindow(boolean secondSeatWindow) {
        this.secondSeatWindow = secondSeatWindow;
    }

    public int getMainSeatWindowLevel() {
        return mainSeatWindowLevel;
    }

    public void setMainSeatWindowLevel(int mainSeatWindowLevel) {
        this.mainSeatWindowLevel = mainSeatWindowLevel;
    }

    public int getSecondSeatWindowLevel() {
        return secondSeatWindowLevel;
    }

    public void setSecondSeatWindowLevel(int secondSeatWindowLevel) {
        this.secondSeatWindowLevel = secondSeatWindowLevel;
    }

    public boolean isBackLeftWindow() {
        return backLeftWindow;
    }

    public void setBackLeftWindow(boolean backLeftWindow) {
        this.backLeftWindow = backLeftWindow;
    }

    public boolean isBackRightWindow() {
        return backRightWindow;
    }

    public void setBackRightWindow(boolean backRightWindow) {
        this.backRightWindow = backRightWindow;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.mainSeatWindow ? (byte) 1 : (byte) 0);
        dest.writeByte(this.secondSeatWindow ? (byte) 1 : (byte) 0);
        dest.writeInt(this.mainSeatWindowLevel);
        dest.writeInt(this.secondSeatWindowLevel);
        dest.writeByte(this.backLeftWindow ? (byte) 1 : (byte) 0);
        dest.writeByte(this.backRightWindow ? (byte) 1 : (byte) 0);
    }

    public CarWindowSettingBean() {
    }

    protected CarWindowSettingBean(Parcel in) {
        this.mainSeatWindow = in.readByte() != 0;
        this.secondSeatWindow = in.readByte() != 0;
        this.mainSeatWindowLevel = in.readInt();
        this.secondSeatWindowLevel = in.readInt();
        this.backLeftWindow = in.readByte() != 0;
        this.backRightWindow = in.readByte() != 0;
    }

    public static final Parcelable.Creator<CarWindowSettingBean> CREATOR = new Parcelable.Creator<CarWindowSettingBean>() {
        @Override
        public CarWindowSettingBean createFromParcel(Parcel source) {
            return new CarWindowSettingBean(source);
        }

        @Override
        public CarWindowSettingBean[] newArray(int size) {
            return new CarWindowSettingBean[size];
        }
    };
}
