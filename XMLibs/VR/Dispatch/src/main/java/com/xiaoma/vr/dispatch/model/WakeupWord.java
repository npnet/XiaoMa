package com.xiaoma.vr.dispatch.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author youthyJ
 * @date 2019/3/23
 */
public class WakeupWord implements Parcelable {
    public static final Creator<WakeupWord> CREATOR = new Creator<WakeupWord>() {
        @Override
        public WakeupWord createFromParcel(Parcel in) {
            return new WakeupWord(in);
        }

        @Override
        public WakeupWord[] newArray(int size) {
            return new WakeupWord[size];
        }
    };
    private final String registerApp;    // 处理该指令的App包名
    private final String word;

    public WakeupWord(String registerApp, String word) {
        this.registerApp = registerApp;
        this.word = word;
    }

    protected WakeupWord(Parcel in) {
        registerApp = in.readString();
        word = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(registerApp);
        dest.writeString(word);
    }
}
