package com.xiaoma.aidl.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MusicInfo implements Parcelable {

    private String albumName;
    private String musicName;
    private String singer;
    private String iconUrl;
    @MusicPlayStatus.Status
    private int status;
    private boolean hasPreMusic;
    private boolean hasNextMusic;

    public @MusicPlayStatus.Status int parseInt(int status) {
        switch (status) {
            case MusicPlayStatus.INIT:
                return MusicPlayStatus.INIT;
            case MusicPlayStatus.PLAYING:
                return MusicPlayStatus.PLAYING;
            case MusicPlayStatus.BUFFERING:
                return MusicPlayStatus.BUFFERING;
            case MusicPlayStatus.PAUSE:
                return MusicPlayStatus.PAUSE;
            case MusicPlayStatus.STOP:
                return MusicPlayStatus.STOP;
            case MusicPlayStatus.EXIT:
                return MusicPlayStatus.EXIT;
            default:
                return MusicPlayStatus.INIT;
        }
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
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

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public @MusicPlayStatus.Status int getStatus() {
        return status;
    }

    public void setStatus(@MusicPlayStatus.Status int status) {
        this.status = status;
    }

    public boolean isHasPreMusic() {
        return hasPreMusic;
    }

    public void setHasPreMusic(boolean hasPreMusic) {
        this.hasPreMusic = hasPreMusic;
    }

    public boolean isHasNextMusic() {
        return hasNextMusic;
    }

    public void setHasNextMusic(boolean hasNextMusic) {
        this.hasNextMusic = hasNextMusic;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.albumName);
        dest.writeString(this.musicName);
        dest.writeString(this.singer);
        dest.writeString(this.iconUrl);
        dest.writeInt(this.status);
        dest.writeByte(this.hasPreMusic ? (byte) 1 : (byte) 0);
        dest.writeByte(this.hasNextMusic ? (byte) 1 : (byte) 0);
    }

    public MusicInfo() {
    }

    protected MusicInfo(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        this.albumName = in.readString();
        this.musicName = in.readString();
        this.singer = in.readString();
        this.iconUrl = in.readString();
        this.status = in.readInt();
        this.hasPreMusic = in.readByte() != 0;
        this.hasNextMusic = in.readByte() != 0;
    }

    public static final Parcelable.Creator<MusicInfo> CREATOR = new Parcelable.Creator<MusicInfo>() {
        @Override
        public MusicInfo createFromParcel(Parcel source) {
            return new MusicInfo(source);
        }

        @Override
        public MusicInfo[] newArray(int size) {
            return new MusicInfo[size];
        }
    };

}
