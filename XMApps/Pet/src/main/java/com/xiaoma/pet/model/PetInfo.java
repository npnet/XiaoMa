package com.xiaoma.pet.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Gillben on 2019/1/4 0004
 * <p>
 * desc: 宠物信息
 */
public class PetInfo implements Parcelable {


    public static final Creator<PetInfo> CREATOR = new Creator<PetInfo>() {
        @Override
        public PetInfo createFromParcel(Parcel in) {
            return new PetInfo(in);
        }

        @Override
        public PetInfo[] newArray(int size) {
            return new PetInfo[size];
        }
    };
    /**
     * petName : 野猪佩奇一号
     * experienceValue : 0
     * grade : 1
     * petDesc : 我的第一个宠物野猪佩奇
     * carCoin : 0
     * uid : 854533803434188800
     * channelId : AA1090
     * eatingTime : 2019-04-11 10:35:16
     * foodName : 小份食物
     * energy : 60
     * feedTime : 4
     * eating : true
     * surplusTime : 13226900
     * timeAccumulation : long
     */

    private String petName;
    private long experienceValue;
    private int grade;
    private String petDesc;
    private long carCoin;
    private long uid;
    private String channelId;
    private String eatingTime;
    private String foodName;
    private String energy;
    private int feedTime;
    private boolean eating;
    private long surplusTime;
    private int boxRecordCount;
    private long timeAccumulation;
    private long chapterId;

    protected PetInfo(Parcel in) {
        petName = in.readString();
        experienceValue = in.readLong();
        grade = in.readInt();
        petDesc = in.readString();
        carCoin = in.readLong();
        uid = in.readLong();
        channelId = in.readString();
        eatingTime = in.readString();
        foodName = in.readString();
        energy = in.readString();
        feedTime = in.readInt();
        eating = in.readByte() != 0;
        surplusTime = in.readLong();
        boxRecordCount = in.readInt();
        timeAccumulation = in.readLong();
        chapterId = in.readLong();
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public long getExperienceValue() {
        return experienceValue;
    }

    public void setExperienceValue(long experienceValue) {
        this.experienceValue = experienceValue;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getPetDesc() {
        return petDesc;
    }

    public void setPetDesc(String petDesc) {
        this.petDesc = petDesc;
    }

    public long getCarCoin() {
        return carCoin;
    }

    public void setCarCoin(long carCoin) {
        this.carCoin = carCoin;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getEatingTime() {
        return eatingTime;
    }

    public void setEatingTime(String eatingTime) {
        this.eatingTime = eatingTime;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getEnergy() {
        return energy;
    }

    public void setEnergy(String energy) {
        this.energy = energy;
    }

    public int getFeedTime() {
        return feedTime;
    }

    public void setFeedTime(int feedTime) {
        this.feedTime = feedTime;
    }

    public boolean isEating() {
        return eating;
    }

    public void setEating(boolean eating) {
        this.eating = eating;
    }

    public long getSurplusTime() {
        return surplusTime;
    }

    public void setSurplusTime(long surplusTime) {
        this.surplusTime = surplusTime;
    }

    public int getBoxRecordCount() {
        return boxRecordCount;
    }

    public void setBoxRecordCount(int boxRecordCount) {
        this.boxRecordCount = boxRecordCount;
    }

    public long getPercentComplete() {
        return timeAccumulation;
    }

    public void setPercentComplete(long timeAccumulation) {
        this.timeAccumulation = timeAccumulation;
    }

    public long getTimeAccumulation() {
        return timeAccumulation;
    }

    public void setTimeAccumulation(long timeAccumulation) {
        this.timeAccumulation = timeAccumulation;
    }

    public long getChapterId() {
        return chapterId;
    }

    public void setChapterId(long chapterId) {
        this.chapterId = chapterId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(petName);
        dest.writeLong(experienceValue);
        dest.writeInt(grade);
        dest.writeString(petDesc);
        dest.writeLong(carCoin);
        dest.writeLong(uid);
        dest.writeString(channelId);
        dest.writeString(eatingTime);
        dest.writeString(foodName);
        dest.writeString(energy);
        dest.writeInt(feedTime);
        dest.writeByte((byte) (eating ? 1 : 0));
        dest.writeLong(surplusTime);
        dest.writeInt(boxRecordCount);
        dest.writeLong(timeAccumulation);
        dest.writeLong(chapterId);
    }
}
