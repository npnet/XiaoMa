package com.xiaoma.aidl.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AudioVTO implements Parcelable {

    private String songName;
    private String singerName;
    private String coverImgUrl;
    private String songId;
    private String singerId;

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getSingerId() {
        return singerId;
    }

    public void setSingerId(String singerId) {
        this.singerId = singerId;
    }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
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
        dest.writeString(this.songName);
        dest.writeString(this.singerName);
        dest.writeString(this.coverImgUrl);
        dest.writeString(this.songId);
        dest.writeString(this.singerId);
    }

    public AudioVTO() {
    }

    protected AudioVTO(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        this.songName = in.readString();
        this.singerName = in.readString();
        this.coverImgUrl = in.readString();
        this.songId = in.readString();
        this.singerId = in.readString();
    }

    public static final Creator<AudioVTO> CREATOR = new Creator<AudioVTO>() {
        @Override
        public AudioVTO createFromParcel(Parcel source) {
            return new AudioVTO(source);
        }

        @Override
        public AudioVTO[] newArray(int size) {
            return new AudioVTO[size];
        }
    };

}