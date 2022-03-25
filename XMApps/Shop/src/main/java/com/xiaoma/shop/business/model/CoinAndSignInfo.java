package com.xiaoma.shop.business.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by LKF on 2017/6/14 0014.
 */

public class CoinAndSignInfo implements Parcelable {
    /**
     * 签到当前进度
     */
    @SerializedName("signProgress")
    private int signInProgress;
    /**
     * 签到当前最大次数
     */
    @SerializedName("signTotal")
    private int signMaxProgress;

    /**
     * 车币/积分
     */
    @SerializedName("totalScore")
    private int carCoin;

    /**
     * 上次签到时间戳
     */
    @SerializedName("preClockDate")
    private String lastSignDate;

    /**
     * 下次签到获得的积分/车币
     */
    @SerializedName("nextScore")
    private int nextSigncoin;

    /**
     * 今天是否已经签到
     */
    @SerializedName("isClock")
    private int isSigned;

    public int getSignInProgress() {
        return signInProgress;
    }

    public void setSignInProgress(int signInProgress) {
        this.signInProgress = signInProgress;
    }

    public int getSignMaxProgress() {
        return signMaxProgress;
    }

    public void setSignMaxProgress(int signMaxProgress) {
        this.signMaxProgress = signMaxProgress;
    }

    public int getCarCoin() {
        return carCoin;
    }

    public void setCarCoin(int carCoin) {
        this.carCoin = carCoin;
    }

    public String getLastSignDate() {
        return lastSignDate;
    }

    public void setLastSignDate(String lastSignDate) {
        this.lastSignDate = lastSignDate;
    }

    public int getNextSigncoin() {
        return nextSigncoin;
    }

    public void setNextSigncoin(int nextSigncoin) {
        this.nextSigncoin = nextSigncoin;
    }

    public int getIsSigned() {
        return isSigned;
    }

    public void setIsSigned(int isSigned) {
        this.isSigned = isSigned;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.signInProgress);
        dest.writeInt(this.signMaxProgress);
        dest.writeInt(this.carCoin);
        dest.writeString(this.lastSignDate);
        dest.writeInt(this.nextSigncoin);
        dest.writeInt(this.isSigned);
    }

    public CoinAndSignInfo() {
    }

    protected CoinAndSignInfo(Parcel in) {
        this.signInProgress = in.readInt();
        this.signMaxProgress = in.readInt();
        this.carCoin = in.readInt();
        this.lastSignDate = in.readString();
        this.nextSigncoin = in.readInt();
        this.isSigned = in.readInt();
    }

    public static final Creator<CoinAndSignInfo> CREATOR = new Creator<CoinAndSignInfo>() {
        @Override
        public CoinAndSignInfo createFromParcel(Parcel source) {
            return new CoinAndSignInfo(source);
        }

        @Override
        public CoinAndSignInfo[] newArray(int size) {
            return new CoinAndSignInfo[size];
        }
    };
}
