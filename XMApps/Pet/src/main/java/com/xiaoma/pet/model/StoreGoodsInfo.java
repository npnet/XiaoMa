package com.xiaoma.pet.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Gillben on 2018/12/25 0025
 * <p>
 * desc: 商店商品
 */
public class StoreGoodsInfo implements Parcelable {

    public static final Creator<StoreGoodsInfo> CREATOR = new Creator<StoreGoodsInfo>() {
        @Override
        public StoreGoodsInfo createFromParcel(Parcel in) {
            return new StoreGoodsInfo(in);
        }

        @Override
        public StoreGoodsInfo[] newArray(int size) {
            return new StoreGoodsInfo[size];
        }
    };
    private String createDate;
    private String modifyDate;
    private int enableStatus;
    private String goodsType;
    private String goodsIcon;
    private String goodsName;
    private String goodsDesc;
    private int goodsPrice;
    private String energy;
    private String feedTime;
    private String channelId;
    private String disabledFlag;
    private String goodsFilePath;
    /**
     * id : 1
     * createDate : 2019-04-10 14:31:56
     * modifyDate : null
     * enableStatus : 1
     * goodsType : 1
     * goodsIcon : /smallFoodImgUrl
     * goodsName : 小份食物
     * goodsDesc : 小份食物，进食时间4小时，能量60g
     * goodsPrice : 150
     * energy : 60
     * feedTime : 4
     * channelId : AA1090
     * disabledFlag : 1
     * goodsFilePath : "对应宠物资源url"
     * buy ： boolean  是否已购买
     */

    private long id;
    private boolean buy;

    protected StoreGoodsInfo(Parcel in) {
        id = in.readLong();
        createDate = in.readString();
        modifyDate = in.readString();
        enableStatus = in.readInt();
        goodsType = in.readString();
        goodsIcon = in.readString();
        goodsName = in.readString();
        goodsDesc = in.readString();
        goodsPrice = in.readInt();
        energy = in.readString();
        feedTime = in.readString();
        channelId = in.readString();
        disabledFlag = in.readString();
        goodsFilePath = in.readString();
        buy = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(createDate);
        dest.writeString(modifyDate);
        dest.writeInt(enableStatus);
        dest.writeString(goodsType);
        dest.writeString(goodsIcon);
        dest.writeString(goodsName);
        dest.writeString(goodsDesc);
        dest.writeInt(goodsPrice);
        dest.writeString(energy);
        dest.writeString(feedTime);
        dest.writeString(channelId);
        dest.writeString(disabledFlag);
        dest.writeString(goodsFilePath);
        dest.writeByte((byte) (buy ? 1 : 0));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getModifyDate() {
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

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    public String getGoodsIcon() {
        return goodsIcon;
    }

    public void setGoodsIcon(String goodsIcon) {
        this.goodsIcon = goodsIcon;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(String goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public int getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(int goodsPrice) {
        this.goodsPrice = goodsPrice;
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

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getDisabledFlag() {
        return disabledFlag;
    }

    public void setDisabledFlag(String disabledFlag) {
        this.disabledFlag = disabledFlag;
    }

    public String getGoodsFilePath() {
        return goodsFilePath;
    }

    public void setGoodsFilePath(String goodsFilePath) {
        this.goodsFilePath = goodsFilePath;
    }

    public boolean isBuy() {
        return buy;
    }

    public void setBuy(boolean buy) {
        this.buy = buy;
    }
}
