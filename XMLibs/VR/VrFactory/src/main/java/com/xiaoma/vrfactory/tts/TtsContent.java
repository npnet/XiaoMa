package com.xiaoma.vrfactory.tts;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ZYao.
 * Date ：2019/6/6 0006
 */
public class TtsContent implements Parcelable {
    public String content;

    public TtsContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.content);
    }

    protected TtsContent(Parcel in) {
        this.content = in.readString();
    }

    public static final Parcelable.Creator<TtsContent> CREATOR = new Parcelable.Creator<TtsContent>() {
        public TtsContent createFromParcel(Parcel source) {
            return new TtsContent(source);
        }

        public TtsContent[] newArray(int size) {
            return new TtsContent[size];
        }
    };
}
