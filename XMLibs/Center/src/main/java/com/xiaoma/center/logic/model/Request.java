package com.xiaoma.center.logic.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author youthyJ
 * @date 2019/1/17
 */
public class Request implements Parcelable {
    public static final Creator<Request> CREATOR = new Creator<Request>() {
        @Override
        public Request createFromParcel(Parcel in) {
            return new Request(in);
        }

        @Override
        public Request[] newArray(int size) {
            return new Request[size];
        }
    };
    private SourceInfo local;
    private RequestHead intent;
    private Bundle extra;

    protected Request(Parcel in) {
        local = in.readParcelable(SourceInfo.class.getClassLoader());
        intent = in.readParcelable(RequestHead.class.getClassLoader());
        extra = in.readBundle(getClass().getClassLoader());
    }

    public Request(SourceInfo local, RequestHead intent, Bundle extra) {
        this.local = local;
        this.intent = intent;
        this.extra = extra;
    }

    public SourceInfo getLocal() {
        return local;
    }

    public RequestHead getIntent() {
        return intent;
    }

    public Bundle getExtra() {
        return extra;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(local, flags);
        dest.writeParcelable(intent, flags);
        dest.writeBundle(extra);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return local + " >>> " + intent;
    }
}
