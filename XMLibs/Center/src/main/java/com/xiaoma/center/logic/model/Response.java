package com.xiaoma.center.logic.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author youthyJ
 * @date 2019/1/20
 */
public class Response implements Parcelable {
    public static final Creator<Response> CREATOR = new Creator<Response>() {
        @Override
        public Response createFromParcel(Parcel in) {
            return new Response(in);
        }

        @Override
        public Response[] newArray(int size) {
            return new Response[size];
        }
    };
    private SourceInfo source;
    private Bundle extra;

    public Response(SourceInfo source, Bundle extra) {
        this.source = source;
        this.extra = extra;
    }

    protected Response(Parcel in) {
        source = in.readParcelable(SourceInfo.class.getClassLoader());
        extra = in.readBundle(getClass().getClassLoader());
    }

    public SourceInfo getSource() {
        return source;
    }

    public Bundle getExtra() {
        return extra;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(source, flags);
        dest.writeBundle(extra);
    }
}
