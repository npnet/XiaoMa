package com.xiaoma.music.search.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by ZYao.
 * Date ：2018/10/12 0012
 */
public class SingerBean implements Serializable, Parcelable {
    private String singerIconUrl;
    private String singerName;

    public SingerBean(String singerName, String singerIconUrl) {
        this.singerIconUrl = singerIconUrl;
        this.singerName = singerName;
    }

    public String getSingerIconUrl() {
        return singerIconUrl;
    }

    public void setSingerIconUrl(String singerIconUrl) {
        this.singerIconUrl = singerIconUrl;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.singerIconUrl);
        dest.writeString(this.singerName);
    }

    public SingerBean() {
    }

    protected SingerBean(Parcel in) {
        this.singerIconUrl = in.readString();
        this.singerName = in.readString();
    }

    public static final Parcelable.Creator<SingerBean> CREATOR = new Parcelable.Creator<SingerBean>() {
        public SingerBean createFromParcel(Parcel source) {
            return new SingerBean(source);
        }

        public SingerBean[] newArray(int size) {
            return new SingerBean[size];
        }
    };
}
