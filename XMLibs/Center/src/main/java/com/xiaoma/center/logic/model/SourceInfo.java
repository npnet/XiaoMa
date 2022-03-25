package com.xiaoma.center.logic.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.Objects;

/**
 * @author youthyJ
 * @date 2019/1/18
 */
public class SourceInfo implements Parcelable {
    public static final Creator<SourceInfo> CREATOR = new Creator<SourceInfo>() {
        @Override
        public SourceInfo createFromParcel(Parcel in) {
            return new SourceInfo(in);
        }

        @Override
        public SourceInfo[] newArray(int size) {
            return new SourceInfo[size];
        }
    };
    private String location;
    private int port;

    public SourceInfo(String location, int port) {
        this.location = location;
        this.port = port;
    }

    protected SourceInfo(Parcel in) {
        location = in.readString();
        port = in.readInt();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(location);
        dest.writeInt(port);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SourceInfo)) {
            return false;
        }
        SourceInfo other = (SourceInfo) obj;
        if (TextUtils.isEmpty(location)) {
            return false;
        }
        if (port < 0) {
            return false;
        }
        if (!location.equals(other.location)) {
            return false;
        }
        return port == other.port;
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, port);
    }

    @Override
    public String toString() {
        if (TextUtils.isEmpty(location)) {
            return "location error";
        }
        if (port < 0) {
            return "xm://" + location;
        }
        return "xm://" + location + ":" + port;
    }
}
