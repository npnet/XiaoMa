package com.xiaoma.aidl.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.xiaoma.process.constants.XMApiConstants;

public class StatusModel implements Parcelable {
    //车门状态(大小为6的数组，依次为发动机盖、前左、前右、后左、后右、行李箱盖状态值，值分为0和-1,0为开启状态,-1为关闭状态)
    private int[] doorStatus;
    //、车窗状态(大小为4的数组，依次为前左车窗、前右车窗、后左车窗、后右车窗状态值，值分为0和-1,0为开启状态,-1为关闭状态)
    private int[] windowStatus;
    //、 车锁状态(大小为4的数组，依次为前左车锁、前右车锁、后左车锁、后右车锁状态值，值分为0和-1,0为开启状态,-1为关闭状态)
    private int[] lockStatus;
    //、车灯状态(大小为2的数组，依次为远光灯和位置灯状态值，值分为0和-1,0为开启状态,-1为关闭状态)
    private int[] lightStatus;
    //、蓄电池状态(大小为1的数组,为蓄电池值的百分比值，若蓄电池为50%，则power值为50)
    private int power;
    //、剩余油量(大小为1的数组,为剩余油量的百分比值，若剩余油量为50%，则oil值为50)
    private int oil;
    //、总里程(大小为1的数组,为总车程值，若总车程为300km，则mileage值为300)
    private int mileage;
    //、座椅加热状态(大小为2的数组，依次是左侧座椅加热状态值和右侧座椅加热状态值，值参考XMApiConstants.SeatWarm)
    private int[] seatWarmStatus;
    //ac开关状态
    private boolean acStatusOpen;
    //auto状态开关
    private boolean autoStatusOpen;
    //dual状态开关
    private boolean dualStatusOpen;
    //左边空调温度
    private float acLeftTemp;
    //右边空调温度
    private float acRightTemp;
    //空调风速
    private int acWindSpeed;
    // 空调吹风模式(一般三挡)
    private int acWindModel;
    //、风窗加热状态(大小为2的数组，依次是前挡风玻璃加热状态值和后挡风玻璃加热状态值，值分为0和-1,0为开启状态,-1为关闭状态)
    private int[] windowWarmStatus;
    //、天窗开启状态(大小为1的数组,为天窗开启状态值，值分为0和-1,0为开启状态,-1为关闭状态)
    private int skyLightStatus;
    //、循环状态(大小为1的数组,为循环状态值，具体值参考XMApiConstants.LooperModel)
    private int looperStatus;


    public float getAcLeftTemp() {
        return acLeftTemp;
    }

    public void setAcLeftTemp(float acLeftTemp) {
        this.acLeftTemp = acLeftTemp;
    }

    public float getAcRightTemp() {
        return acRightTemp;
    }

    public void setAcRightTemp(float acRightTemp) {
        this.acRightTemp = acRightTemp;
    }

    public int getAcWindSpeed() {
        return acWindSpeed;
    }

    public void setAcWindSpeed(int acWindSpeed) {
        this.acWindSpeed = acWindSpeed;
    }

    public int getAcWindModel() {
        return acWindModel;
    }

    public void setAcWindModel(int acWindModel) {
        this.acWindModel = acWindModel;
    }

    public int[] getDoorStatus() {
        return doorStatus;
    }

    public void setDoorStatus(int[] doorStatus) {
        this.doorStatus = doorStatus;
    }

    public int[] getWindowStatus() {
        return windowStatus;
    }

    public void setWindowStatus(int[] windowStatus) {
        this.windowStatus = windowStatus;
    }

    public int[] getLockStatus() {
        return lockStatus;
    }

    public void setLockStatus(int[] lockStatus) {
        this.lockStatus = lockStatus;
    }

    public int[] getLightStatus() {
        return lightStatus;
    }

    public void setLightStatus(int[] lightStatus) {
        this.lightStatus = lightStatus;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getOil() {
        return oil;
    }

    public void setOil(int oil) {
        this.oil = oil;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public int[] getSeatWarmStatus() {
        return seatWarmStatus;
    }

    public void setSeatWarmStatus(int[] seatWarmStatus) {
        this.seatWarmStatus = seatWarmStatus;
    }


    public int[] getWindowWarmStatus() {
        return windowWarmStatus;
    }

    public void setWindowWarmStatus(int[] windowWarmStatus) {
        this.windowWarmStatus = windowWarmStatus;
    }

    public int getSkyLightStatus() {
        return skyLightStatus;
    }

    public void setSkyLightStatus(int skyLightStatus) {
        this.skyLightStatus = skyLightStatus;
    }

    public int getLooperStatus() {
        return looperStatus;
    }

    public void setLooperStatus(int looperStatus) {
        this.looperStatus = looperStatus;
    }


    public boolean isAcStatusOpen() {
        return acStatusOpen;
    }

    public void setAcStatusOpen(boolean acStatusOpen) {
        this.acStatusOpen = acStatusOpen;
    }

    public boolean isAutoStatusOpen() {
        return autoStatusOpen;
    }

    public void setAutoStatusOpen(boolean autoStatusOpen) {
        this.autoStatusOpen = autoStatusOpen;
    }

    public boolean isDualStatusOpen() {
        return dualStatusOpen;
    }

    public void setDualStatusOpen(boolean dualStatusOpen) {
        this.dualStatusOpen = dualStatusOpen;
    }

    public StatusModel() {
        this.doorStatus = new int[]{XMApiConstants.NormalStatus.CLOSE, XMApiConstants.NormalStatus.CLOSE, XMApiConstants.NormalStatus.CLOSE, XMApiConstants.NormalStatus.CLOSE, XMApiConstants.NormalStatus.CLOSE, XMApiConstants.NormalStatus.CLOSE};
        this.windowStatus = new int[]{XMApiConstants.NormalStatus.CLOSE, XMApiConstants.NormalStatus.CLOSE, XMApiConstants.NormalStatus.CLOSE, XMApiConstants.NormalStatus.CLOSE};
        this.lockStatus = new int[]{XMApiConstants.NormalStatus.CLOSE, XMApiConstants.NormalStatus.CLOSE, XMApiConstants.NormalStatus.CLOSE, XMApiConstants.NormalStatus.CLOSE};
        this.lightStatus = new int[]{XMApiConstants.NormalStatus.CLOSE, XMApiConstants.NormalStatus.CLOSE};
        this.power = 100;
        this.oil = 100;
        this.mileage = 0;
        this.acStatusOpen = false;
        this.dualStatusOpen = false;
        this.autoStatusOpen = false;
        this.acLeftTemp = 20;
        this.acRightTemp = 20;
        this.seatWarmStatus = new int[]{XMApiConstants.SeatWarm.CLOSE, XMApiConstants.SeatWarm.CLOSE};
        this.windowWarmStatus = new int[]{XMApiConstants.NormalStatus.CLOSE, XMApiConstants.NormalStatus.CLOSE};
        this.skyLightStatus = XMApiConstants.NormalStatus.CLOSE;
        this.looperStatus = XMApiConstants.LooperModel.INNER_LOOPER;
        this.acWindSpeed = XMApiConstants.WindSpeed.CLOSE;
        this.acWindModel = XMApiConstants.WindModel.BODY;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeIntArray(this.doorStatus);
        dest.writeIntArray(this.windowStatus);
        dest.writeIntArray(this.lockStatus);
        dest.writeIntArray(this.lightStatus);
        dest.writeInt(this.power);
        dest.writeInt(this.oil);
        dest.writeInt(this.mileage);
        dest.writeIntArray(this.seatWarmStatus);
        dest.writeByte(this.acStatusOpen ? (byte) 1 : (byte) 0);
        dest.writeByte(this.autoStatusOpen ? (byte) 1 : (byte) 0);
        dest.writeByte(this.dualStatusOpen ? (byte) 1 : (byte) 0);
        dest.writeFloat(this.acLeftTemp);
        dest.writeFloat(this.acRightTemp);
        dest.writeInt(this.acWindSpeed);
        dest.writeInt(this.acWindModel);
        dest.writeIntArray(this.windowWarmStatus);
        dest.writeInt(this.skyLightStatus);
        dest.writeInt(this.looperStatus);
    }

    protected StatusModel(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        this.doorStatus = in.createIntArray();
        this.windowStatus = in.createIntArray();
        this.lockStatus = in.createIntArray();
        this.lightStatus = in.createIntArray();
        this.power = in.readInt();
        this.oil = in.readInt();
        this.mileage = in.readInt();
        this.seatWarmStatus = in.createIntArray();
        this.acStatusOpen = in.readByte() != 0;
        this.autoStatusOpen = in.readByte() != 0;
        this.dualStatusOpen = in.readByte() != 0;
        this.acLeftTemp = in.readFloat();
        this.acRightTemp = in.readFloat();
        this.acWindSpeed = in.readInt();
        this.acWindModel = in.readInt();
        this.windowWarmStatus = in.createIntArray();
        this.skyLightStatus = in.readInt();
        this.looperStatus = in.readInt();
    }

    public static final Creator<StatusModel> CREATOR = new Creator<StatusModel>() {
        @Override
        public StatusModel createFromParcel(Parcel source) {
            return new StatusModel(source);
        }

        @Override
        public StatusModel[] newArray(int size) {
            return new StatusModel[size];
        }
    };
}
