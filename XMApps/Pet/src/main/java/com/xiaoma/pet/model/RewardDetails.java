package com.xiaoma.pet.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/20 0020 20:04
 *   desc:   宝箱信息
 * </pre>
 */
public class RewardDetails implements Parcelable {

    public static final Creator<RewardDetails> CREATOR = new Creator<RewardDetails>() {
        @Override
        public RewardDetails createFromParcel(Parcel in) {
            return new RewardDetails(in);
        }

        @Override
        public RewardDetails[] newArray(int size) {
            return new RewardDetails[size];
        }
    };
    /**
     * goodsType : 1
     * goodsName : [奖励] 中份食物
     * goodsDesc : [奖励] 中份食物，进食时间6小时，能量100g
     * goodsIcon :
     * smallFoodImgUrl:
     * energy : 100
     * feedTime : 6
     * goodsNumber : 1
     */

    private String goodsType;
    private String goodsName;
    private String goodsDesc;
    private String goodsIcon;
    private String energy;
    private int feedTime;
    private int goodsNumber;

    protected RewardDetails(Parcel in) {
        goodsType = in.readString();
        goodsName = in.readString();
        goodsDesc = in.readString();
        goodsIcon = in.readString();
        energy = in.readString();
        feedTime = in.readInt();
        goodsNumber = in.readInt();
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
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

    public String getGoodsIcon() {
        return goodsIcon;
    }

    public void setGoodsIcon(String goodsIcon) {
        this.goodsIcon = goodsIcon;
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

    public int getGoodsNumber() {
        return goodsNumber;
    }

    public void setGoodsNumber(int goodsNumber) {
        this.goodsNumber = goodsNumber;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(goodsType);
        dest.writeString(goodsName);
        dest.writeString(goodsDesc);
        dest.writeString(goodsIcon);
        dest.writeString(energy);
        dest.writeInt(feedTime);
        dest.writeInt(goodsNumber);
    }
}
