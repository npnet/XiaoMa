package com.xiaoma.music.common.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ZYao.
 * Date ：2018/12/29 0029
 */
public class UploadMusicModel implements Parcelable {
    private long id;
    private String musicName;
    private String artist;
    private long duration;
    private String type;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public UploadMusicModel() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.musicName);
        dest.writeString(this.artist);
        dest.writeLong(this.duration);
        dest.writeString(this.type);
        dest.writeLong(this.id);
    }

    protected UploadMusicModel(Parcel in) {
        this.musicName = in.readString();
        this.artist = in.readString();
        this.duration = in.readLong();
        this.type = in.readString();
        this.id = in.readLong();
    }

    public static final Creator<UploadMusicModel> CREATOR = new Creator<UploadMusicModel>() {
        public UploadMusicModel createFromParcel(Parcel source) {
            return new UploadMusicModel(source);
        }

        public UploadMusicModel[] newArray(int size) {
            return new UploadMusicModel[size];
        }
    };
}
