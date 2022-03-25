package com.xiaoma.launcher.recommend.push.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author taojin
 * @date 2019/5/17
 */
public class MqttMessage implements Parcelable {

    private int action;
    private String tag;
    private String data;
    private String md5File;

    public int getAction() {
        return action;
    }

    public String getTag() {
        return tag;
    }

    public String getData() {
        return data;
    }

    public String getMd5File() {
        return md5File;
    }

    public MqttMessage() {
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(action);
        dest.writeString(tag);
        dest.writeString(data);
        dest.writeString(md5File);
    }

    protected MqttMessage(Parcel in) {
        this.action = in.readInt();
        this.tag = in.readString();
        this.data = in.readString();
        this.md5File = in.readString();
    }

    public static final Creator<MqttMessage> CREATOR = new Creator<MqttMessage>() {
        @Override
        public MqttMessage createFromParcel(Parcel source) {
            return new MqttMessage(source);
        }

        @Override
        public MqttMessage[] newArray(int size) {
            return new MqttMessage[size];
        }
    };
}
