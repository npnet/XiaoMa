package com.xiaoma.player;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author youthyJ
 * @date 2018/11/7
 */
public class AudioInfo implements Parcelable {

    //是否同步的本地播放历史
    private boolean isHistory;

    private long albumId;

    private String cover;
    private String title;
    private String subTitle;

    @AudioConstants.AudioType
    private int audioType;
    private long uniqueId;//节目对应的Id,非专辑Id
    private int launcherCategoryId;
    private int playState;
    private String usbMusicPath;
    private int playCount; //多少人收听
    private boolean isCenterItem;
    public static final Creator<AudioInfo> CREATOR = new Creator<AudioInfo>() {
        @Override
        public AudioInfo createFromParcel(Parcel source) {
            return new AudioInfo(source);
        }

        @Override
        public AudioInfo[] newArray(int size) {
            return new AudioInfo[size];
        }
    };

    public AudioInfo() {
    }

    private int page;

    public boolean isHistory() {
        return isHistory;
    }

    public void setHistory(boolean history) {
        isHistory = history;
    }

    public long getAlbumId() {
        return albumId;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public int getAudioType() {
        return audioType;
    }

    public void setAudioType(int audioType) {
        this.audioType = audioType;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public long getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(long uniqueId) {
        this.uniqueId = uniqueId;
    }

    public int getLauncherCategoryId() {
        return launcherCategoryId;
    }

    public int getPlayState() {
        return playState;
    }

    public void setPlayState(int playState) {
        this.playState = playState;
    }

    public String getUsbMusicPath() {
        return usbMusicPath;
    }

    public void setUsbMusicPath(String usbMusicPath) {
        this.usbMusicPath = usbMusicPath;
    }

    protected AudioInfo(Parcel in) {
        readFromParcel(in);
    }

    protected void readFromParcel(Parcel in) {
        this.isHistory = in.readByte() != 0;
        this.albumId = in.readLong();
        this.cover = in.readString();
        this.title = in.readString();
        this.subTitle = in.readString();
        this.audioType = in.readInt();
        this.uniqueId = in.readLong();
        this.launcherCategoryId = in.readInt();
        this.playState = in.readInt();
        this.usbMusicPath = in.readString();
        this.playCount = in.readInt();
        this.isCenterItem = in.readByte() != 0;
        this.page = in.readInt();
    }

    public void setLauncherCategoryId(int launcherCategoryId) {
        this.launcherCategoryId = launcherCategoryId;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public boolean isCenterItem() {
        return isCenterItem;
    }

    public void setCenterItem(boolean centerItem) {
        isCenterItem = centerItem;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isHistory ? (byte) 1 : (byte) 0);
        dest.writeLong(this.albumId);
        dest.writeString(this.cover);
        dest.writeString(this.title);
        dest.writeString(this.subTitle);
        dest.writeInt(this.audioType);
        dest.writeLong(this.uniqueId);
        dest.writeInt(this.launcherCategoryId);
        dest.writeInt(this.playState);
        dest.writeString(this.usbMusicPath);
        dest.writeInt(this.playCount);
        dest.writeByte(this.isCenterItem ? (byte) 1 : (byte) 0);
        dest.writeInt(this.page);
    }

    @Override
    public String toString() {
        return "AudioInfo{" +
                "isHistory=" + isHistory +
                ", albumId=" + albumId +
                ", cover='" + cover + '\'' +
                ", title='" + title + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", audioType=" + audioType +
                ", uniqueId=" + uniqueId +
                ", launcherCategoryId=" + launcherCategoryId +
                ", playState=" + playState +
                ", usbMusicPath='" + usbMusicPath + '\'' +
                ", playCount=" + playCount +
                ", isCenterItem=" + isCenterItem +
                ", page=" + page +
                '}';
    }
}
