package com.xiaoma.music.search.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by ZYao.
 * Date ：2018/10/12 0012
 */
public class SongListBean implements Serializable, Parcelable {
    private String songListIconUrl;
    private String songListName;

    public SongListBean(String songListName, String songListIconUrl) {
        this.songListIconUrl = songListIconUrl;
        this.songListName = songListName;
    }

    public String getSongListIconUrl() {
        return songListIconUrl;
    }

    public void setSongListIconUrl(String songListIconUrl) {
        this.songListIconUrl = songListIconUrl;
    }

    public String getSongListName() {
        return songListName;
    }

    public void setSongListName(String songListName) {
        this.songListName = songListName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.songListIconUrl);
        dest.writeString(this.songListName);
    }

    public SongListBean() {
    }

    protected SongListBean(Parcel in) {
        this.songListIconUrl = in.readString();
        this.songListName = in.readString();
    }

    public static final Parcelable.Creator<SongListBean> CREATOR = new Parcelable.Creator<SongListBean>() {
        public SongListBean createFromParcel(Parcel source) {
            return new SongListBean(source);
        }

        public SongListBean[] newArray(int size) {
            return new SongListBean[size];
        }
    };
}
