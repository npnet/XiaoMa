package com.xiaoma.center.logic.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * @author youthyJ
 * @date 2019/1/17
 */
public class RequestHead implements Parcelable {
    public static final Creator<RequestHead> CREATOR = new Creator<RequestHead>() {
        @Override
        public RequestHead createFromParcel(Parcel in) {
            return new RequestHead(in);
        }

        @Override
        public RequestHead[] newArray(int size) {
            return new RequestHead[size];
        }
    };
    private SourceInfo remote;
    private int action;

    public RequestHead(SourceInfo remote, int action) {
        this.remote = remote;
        this.action = action;
    }

    protected RequestHead(Parcel in) {
        remote = in.readParcelable(SourceInfo.class.getClassLoader());
        action = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(remote, flags);
        dest.writeInt(action);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RequestHead)) {
            return false;
        }
        if (remote == null) {
            return false;
        }
        if (action < 0) {
            return false;
        }
        RequestHead other = (RequestHead) obj;
        if (!remote.equals(other.remote)) {
            return false;
        }
        return action == other.action;

    }

    @Override
    public int hashCode() {
        return Objects.hash(remote, action);
    }

    @Override
    public String toString() {
        if (remote == null) {
            return "remote not found";
        }
        return remote + "/" + action;
    }

    public SourceInfo getRemote() {
        return remote;
    }

    public int getAction() {
        return action;
    }
}
