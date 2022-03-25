package com.xiaoma.music.common.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import java.io.Serializable;

/**
 * Created by ZYao.
 * Date ：2018/10/11 0011
 */
@Table("history_music")
public class MusicInfo implements Serializable, Parcelable {

    @PrimaryKey(AssignType.BY_MYSELF)
    private int id;
    private String musicName;
    private String singer;
    private String albumName;
    private String iconUrl;

    public MusicInfo(String musicName, String singer, String albumName, String iconUrl) {
        this.musicName = musicName;
        this.singer = singer;
        this.albumName = albumName;
        this.iconUrl = iconUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.musicName);
        dest.writeString(this.singer);
        dest.writeString(this.albumName);
        dest.writeString(this.iconUrl);
    }

    protected MusicInfo(Parcel in) {
        this.musicName = in.readString();
        this.id = in.readInt();
        this.singer = in.readString();
        this.albumName = in.readString();
        this.iconUrl = in.readString();
    }

    public static final Parcelable.Creator<MusicInfo> CREATOR = new Parcelable.Creator<MusicInfo>() {
        public MusicInfo createFromParcel(Parcel source) {
            return new MusicInfo(source);
        }

        public MusicInfo[] newArray(int size) {
            return new MusicInfo[size];
        }
    };
}
