package com.xiaoma.pet.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/21 0021 10:48
 *   desc:
 * </pre>
 */
public class RepositoryInfo implements Parcelable {


    public static final Creator<RepositoryInfo> CREATOR = new Creator<RepositoryInfo>() {
        @Override
        public RepositoryInfo createFromParcel(Parcel in) {
            return new RepositoryInfo(in);
        }

        @Override
        public RepositoryInfo[] newArray(int size) {
            return new RepositoryInfo[size];
        }
    };
    /**
     * id : 2
     * decoratedClass : java.util.HashMap
     * channelId : AA1090
     * createDate : 2019-04-11 10:31:38
     * uid : 854533803434188800
     * goodsName : 中份食物
     * energy : 100
     * feedTime : 6
     * goodsType : 1
     * goodsDesc : 中份食物，进食时间6小时，能量100g
     * goodsPrice : 200
     * goodsIcon : "url"
     * goodsNumber : 2
     * goodsTypeName : 食品
     * goodsFilePath : 资源路径
     */

    private long id;
    private String channelId;
    private String createDate;
    private long uid;
    private String goodsName;
    private String energy;
    private String feedTime;
    private String goodsType;
    private String goodsDesc;
    private String goodsPrice;
    private String goodsIcon;
    private int goodsNumber;
    private String goodsTypeName;
    private String goodsFilePath;

    protected RepositoryInfo(Parcel in) {
        id = in.readLong();
        channelId = in.readString();
        createDate = in.readString();
        uid = in.readLong();
        goodsName = in.readString();
        energy = in.readString();
        feedTime = in.readString();
        goodsType = in.readString();
        goodsDesc = in.readString();
        goodsPrice = in.readString();
        goodsIcon = in.readString();
        goodsNumber = in.readInt();
        goodsTypeName = in.readString();
        goodsFilePath = in.readString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getEnergy() {
        return energy;
    }

    public void setEnergy(String energy) {
        this.energy = energy;
    }

    public String getFeedTime() {
        return feedTime;
    }

    public void setFeedTime(String feedTime) {
        this.feedTime = feedTime;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    public String getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(String goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public String getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(String goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public String getGoodsIcon() {
        return goodsIcon;
    }

    public void setGoodsIcon(String goodsIcon) {
        this.goodsIcon = goodsIcon;
    }

    public int getGoodsNumber() {
        return goodsNumber;
    }

    public void setGoodsNumber(int goodsNumber) {
        this.goodsNumber = goodsNumber;
    }

    public String getGoodsTypeName() {
        return goodsTypeName;
    }

    public void setGoodsTypeName(String goodsTypeName) {
        this.goodsTypeName = goodsTypeName;
    }

    public String getGoodsFilePath() {
        return goodsFilePath;
    }

    public void setGoodsFilePath(String goodsFilePath) {
        this.goodsFilePath = goodsFilePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(channelId);
        dest.writeString(createDate);
        dest.writeLong(uid);
        dest.writeString(goodsName);
        dest.writeString(energy);
        dest.writeString(feedTime);
        dest.writeString(goodsType);
        dest.writeString(goodsDesc);
        dest.writeString(goodsPrice);
        dest.writeString(goodsIcon);
        dest.writeInt(goodsNumber);
        dest.writeString(goodsTypeName);
        dest.writeString(goodsFilePath);
    }
}
