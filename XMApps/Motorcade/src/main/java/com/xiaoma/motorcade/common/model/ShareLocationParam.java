package com.xiaoma.motorcade.common.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by ZYao.
 * Date ：2019/1/29 0029
 */
public class ShareLocationParam implements Serializable, Parcelable {
    private double lat;
    private double lon;

    private String picPath;
    private String userNick;
    private String hxAccount;

    public ShareLocationParam() {
    }

    public ShareLocationParam(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public String getHxAccount() {
        return hxAccount;
    }

    public void setHxAccount(String hxAccount) {
        this.hxAccount = hxAccount;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public String getPicPath() {
        return picPath;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }

    public String getUserNick() {
        return userNick;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lon);
        dest.writeString(this.picPath);
        dest.writeString(this.userNick);
        dest.writeString(this.hxAccount);
    }

    protected ShareLocationParam(Parcel in) {
        this.lat = in.readDouble();
        this.lon = in.readDouble();
        this.picPath = in.readString();
        this.userNick = in.readString();
        this.hxAccount = in.readString();
    }

    public static final Parcelable.Creator<ShareLocationParam> CREATOR = new Parcelable.Creator<ShareLocationParam>() {
        public ShareLocationParam createFromParcel(Parcel source) {
            return new ShareLocationParam(source);
        }

        public ShareLocationParam[] newArray(int size) {
            return new ShareLocationParam[size];
        }
    };
}
