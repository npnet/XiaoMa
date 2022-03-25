package com.xiaoma.personal.coin.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by LKF on 2017/6/14 0014.
 */

public class CoinRecord implements Parcelable, Serializable {
    @SerializedName("score")
    private int creditsChanged;

    @SerializedName("action")
    private String action;

    @SerializedName("createDate")
    private long actionTime;

    public int getCreditsChanged() {
        return creditsChanged;
    }

    public void setCreditsChanged(int creditsChanged) {
        this.creditsChanged = creditsChanged;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public long getActionTime() {
        return actionTime;
    }

    public void setActionTime(long actionTime) {
        this.actionTime = actionTime;
    }

    public CoinRecord() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.creditsChanged);
        dest.writeString(this.action);
        dest.writeLong(this.actionTime);
    }

    protected CoinRecord(Parcel in) {
        this.creditsChanged = in.readInt();
        this.action = in.readString();
        this.actionTime = in.readLong();
    }

    public static final Creator<CoinRecord> CREATOR = new Creator<CoinRecord>() {
        @Override
        public CoinRecord createFromParcel(Parcel source) {
            return new CoinRecord(source);
        }

        @Override
        public CoinRecord[] newArray(int size) {
            return new CoinRecord[size];
        }
    };
}
