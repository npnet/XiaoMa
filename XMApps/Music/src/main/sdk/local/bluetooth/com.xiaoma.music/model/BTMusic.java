package com.xiaoma.music.model;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.xiaoma.music.common.model.PlayStatus;

/**
 * Created by ZYao.
 * Date ：2018/10/23 0023
 */
public class BTMusic implements Parcelable {
    private String title;
    private String artist;
    private long duration;
    private long currentProgress;
    private String album;
    @PlayStatus
    private int playState;
    private Bitmap bitmap;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public long getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(long currentProgress) {
        this.currentProgress = currentProgress;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    @PlayStatus
    public int getPlayState() {
        return playState;
    }

    public void setPlayState(@PlayStatus int playState) {
        this.playState = playState;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.artist);
        dest.writeLong(this.duration);
        dest.writeString(this.album);
        dest.writeInt(this.playState);
    }

    public BTMusic() {
    }

    public BTMusic(String title, String artist, long duration, String album) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.album = album;
    }

    public BTMusic(String title, String artist, long duration, String album, int playState) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.album = album;
        this.playState = playState;
    }

    public BTMusic(String title, String artist, long duration, String album, Bitmap bitmap) {
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.album = album;
        this.bitmap = bitmap;
    }

    protected BTMusic(Parcel in) {
        this.title = in.readString();
        this.artist = in.readString();
        this.duration = in.readLong();
        this.album = in.readString();
        this.playState = in.readInt();
    }

    public static final Parcelable.Creator<BTMusic> CREATOR = new Parcelable.Creator<BTMusic>() {
        public BTMusic createFromParcel(Parcel source) {
            return new BTMusic(source);
        }

        public BTMusic[] newArray(int size) {
            return new BTMusic[size];
        }
    };

    @Override
    public String toString() {
        return "BTMusic{" +
                "title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", duration=" + duration +
                ", album='" + album + '\'' +
                '}';
    }
}
