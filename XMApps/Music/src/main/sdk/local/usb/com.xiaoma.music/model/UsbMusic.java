package com.xiaoma.music.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.xiaoma.music.common.adapter.IGalleryData;

/**
 * @author zs
 * @date 2018/10/29 0029.
 */
@Table("UsbMusicList")
public class UsbMusic implements Parcelable, IGalleryData {

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;
    private String name;
    private String realName;
    private String album;
    private String path;
    private String displayName;
    private String artist;
    @Ignore
    private long duration;
    @Ignore
    private long size;
    @Ignore
    private String iconUrl;
    private String genre;

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public String getCoverUrl() {
        if (getIconUrl() == null) {
            return "";
        }
        return getIconUrl();
    }

    @Override
    public String getTitleText() {
        return name;
    }

    @Override
    public String getFooterText() {
        return null;
    }

    @Override
    public String getBottomText() {
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.album);
        dest.writeString(this.path);
        dest.writeString(this.displayName);
        dest.writeString(this.artist);
        dest.writeLong(this.duration);
        dest.writeLong(this.size);
        dest.writeString(this.iconUrl);
        dest.writeString(this.genre);
        dest.writeString(this.realName);
    }

    public UsbMusic() {
    }

    protected UsbMusic(Parcel in) {
        this.name = in.readString();
        this.album = in.readString();
        this.path = in.readString();
        this.displayName = in.readString();
        this.artist = in.readString();
        this.duration = in.readLong();
        this.size = in.readLong();
        this.iconUrl = in.readString();
        this.genre = in.readString();
        this.realName = in.readString();
    }

    public static final Creator<UsbMusic> CREATOR = new Creator<UsbMusic>() {
        @Override
        public UsbMusic createFromParcel(Parcel source) {
            return new UsbMusic(source);
        }

        @Override
        public UsbMusic[] newArray(int size) {
            return new UsbMusic[size];
        }
    };

}
