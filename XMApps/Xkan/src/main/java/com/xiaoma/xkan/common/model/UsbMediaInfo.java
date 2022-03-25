package com.xiaoma.xkan.common.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Thomas on 2018/11/14 0014
 */

public class UsbMediaInfo implements Serializable, Parcelable {

    private String mediaName;
    private String firstLetter;
    private String path;
    private long size;
    private long date;
    private int fileType;

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public void setMediaName(String mediaName) {
        this.mediaName = mediaName;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMediaName() {
        return mediaName;
    }

    public String getPath() {
        return path;
    }

    public String getFirstLetter() {
        return firstLetter;
    }

    public void setFirstLetter(String firstLetter) {
        this.firstLetter = firstLetter;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mediaName);
        dest.writeString(this.firstLetter);
        dest.writeString(this.path);
        dest.writeLong(this.size);
        dest.writeLong(this.date);
        dest.writeInt(this.fileType);
    }

    public UsbMediaInfo() {}

    protected UsbMediaInfo(Parcel in) {
        this.mediaName = in.readString();
        this.firstLetter = in.readString();
        this.path = in.readString();
        this.size = in.readLong();
        this.date = in.readLong();
        this.fileType = in.readInt();
    }

    public static final Parcelable.Creator<UsbMediaInfo> CREATOR = new Parcelable.Creator<UsbMediaInfo>() {
        @Override
        public UsbMediaInfo createFromParcel(Parcel source) {return new UsbMediaInfo(source);}

        @Override
        public UsbMediaInfo[] newArray(int size) {return new UsbMediaInfo[size];}
    };
}
