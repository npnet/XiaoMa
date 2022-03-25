package com.xiaoma.player;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ZYao.
 * Date ：2018/11/13 0013
 */
public class ProgressInfo implements Parcelable {
    public static final Creator<ProgressInfo> CREATOR = new Creator<ProgressInfo>() {
        public ProgressInfo createFromParcel(Parcel source) {
            return new ProgressInfo(source);
        }

        public ProgressInfo[] newArray(int size) {
            return new ProgressInfo[size];
        }
    };
    private float percent;
    private long current;
    private long total;
    @AudioConstants.AudioType
    private int audioType;

    public ProgressInfo() {
    }

    protected ProgressInfo(Parcel in) {
        readFromParcel(in);
    }

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    @AudioConstants.AudioType
    public int getAudioType() {
        return audioType;
    }

    public void setAudioType(@AudioConstants.AudioType int audioType) {
        this.audioType = audioType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public void readFromParcel(Parcel in) {
        this.percent = in.readFloat();
        this.current = in.readLong();
        this.total = in.readLong();
        this.audioType = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.percent);
        dest.writeLong(this.current);
        dest.writeLong(this.total);
        dest.writeInt(audioType);
    }

    @Override
    public String toString() {
        return "ProgressInfo {" +
                " percent=" + percent +
                ", current=" + current +
                ", total=" + total +
                ", audioType=" + audioType +
                " }";
    }
}
