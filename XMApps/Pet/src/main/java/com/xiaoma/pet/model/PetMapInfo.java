package com.xiaoma.pet.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Gillben on 2019/1/3 0003
 * <p>
 * desc: 宠物关卡
 */
public class PetMapInfo implements Parcelable {


    /**
     * id : 3
     * createDate : 2019-04-11 11:45:40
     * modifyDate : null
     * enableStatus : 1
     * chapterPreId : 2
     * chapterName : 第三关
     * chapterVersion : V1.0
     * chapterDesc : 第三关,版本V1.0
     * gameVersion : V1.0
     * gameWall : http://www.carbuyin.net/by2/qunHeader/cf9ddb4d-4b2a-429c-b9eb-82af8d998c38.png
     * mileage : 10000
     * tips : 过关啦
     * channelId : AA1090
     * disabledFlag : 1
     */

    private int id;
    private String createDate;
    private String modifyDate;
    private int enableStatus;
    private long chapterPreId;
    private String chapterName;
    private String chapterVersion;
    private String chapterDesc;
    private String gameVersion;
    private String gameWall;
    private String mileage;
    private String tips;
    private String channelId;
    private int disabledFlag;
    private long chapterNextId;

    protected PetMapInfo(Parcel in) {
        id = in.readInt();
        createDate = in.readString();
        modifyDate = in.readString();
        enableStatus = in.readInt();
        chapterPreId = in.readLong();
        chapterName = in.readString();
        chapterVersion = in.readString();
        chapterDesc = in.readString();
        gameVersion = in.readString();
        gameWall = in.readString();
        mileage = in.readString();
        tips = in.readString();
        channelId = in.readString();
        disabledFlag = in.readInt();
        chapterNextId = in.readLong();
    }

    public static final Creator<PetMapInfo> CREATOR = new Creator<PetMapInfo>() {
        @Override
        public PetMapInfo createFromParcel(Parcel in) {
            return new PetMapInfo(in);
        }

        @Override
        public PetMapInfo[] newArray(int size) {
            return new PetMapInfo[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public Object getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(String modifyDate) {
        this.modifyDate = modifyDate;
    }

    public int getEnableStatus() {
        return enableStatus;
    }

    public void setEnableStatus(int enableStatus) {
        this.enableStatus = enableStatus;
    }

    public long getChapterPreId() {
        return chapterPreId;
    }

    public void setChapterPreId(long chapterPreId) {
        this.chapterPreId = chapterPreId;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getChapterVersion() {
        return chapterVersion;
    }

    public void setChapterVersion(String chapterVersion) {
        this.chapterVersion = chapterVersion;
    }

    public String getChapterDesc() {
        return chapterDesc;
    }

    public void setChapterDesc(String chapterDesc) {
        this.chapterDesc = chapterDesc;
    }

    public String getGameVersion() {
        return gameVersion;
    }

    public void setGameVersion(String gameVersion) {
        this.gameVersion = gameVersion;
    }

    public String getGameWall() {
        return gameWall;
    }

    public void setGameWall(String gameWall) {
        this.gameWall = gameWall;
    }

    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public int getDisabledFlag() {
        return disabledFlag;
    }

    public void setDisabledFlag(int disabledFlag) {
        this.disabledFlag = disabledFlag;
    }

    public long getChapterNextId() {
        return chapterNextId;
    }

    public void setChapterNextId(long chapterNextId) {
        this.chapterNextId = chapterNextId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(createDate);
        dest.writeString(modifyDate);
        dest.writeInt(enableStatus);
        dest.writeLong(chapterPreId);
        dest.writeString(chapterName);
        dest.writeString(chapterVersion);
        dest.writeString(chapterDesc);
        dest.writeString(gameVersion);
        dest.writeString(gameWall);
        dest.writeString(mileage);
        dest.writeString(tips);
        dest.writeString(channelId);
        dest.writeInt(disabledFlag);
        dest.writeLong(chapterNextId);
    }
}
