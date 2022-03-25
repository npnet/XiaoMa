package com.xiaoma.push.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by LKF on 2018/3/26 0026.
 */

public class PushMessage implements Parcelable {
    private int action;
    private String tag;
    private String data;

    public int getAction() {
        return action;
    }

    public String getTag() {
        return tag;
    }

    public String getData() {
        return data;
    }

    public PushMessage() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.action);
        dest.writeString(this.tag);
        dest.writeString(this.data);
    }

    protected PushMessage(Parcel in) {
        this.action = in.readInt();
        this.tag = in.readString();
        this.data = in.readString();
    }

    public static final Creator<PushMessage> CREATOR = new Creator<PushMessage>() {
        @Override
        public PushMessage createFromParcel(Parcel source) {
            return new PushMessage(source);
        }

        @Override
        public PushMessage[] newArray(int size) {
            return new PushMessage[size];
        }
    };
}
