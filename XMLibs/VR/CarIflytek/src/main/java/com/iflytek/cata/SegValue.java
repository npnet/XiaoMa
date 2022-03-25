package com.iflytek.cata;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Vector;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/12/27
 * Desc：
 */
public class SegValue {
    public static final int ONLY_RAW = 0;
    public static final int RAW_AND_PINYIN = 1;
    public static final int TAG = 2;
    public static final int RAW_AND_TJIU = 3;
    public static final int RAW_AND_PINYIN_AND_TJIU = 4;
    public String mField = null;
    public Vector<String> mTexts;
    public int mMode = 0;
    public int mServPattern = 0;
    public static final Parcelable.Creator<SegValue> CREATOR = new Parcelable.Creator<SegValue>() {
        public SegValue createFromParcel(Parcel source) {
            return new SegValue(source);
        }

        public SegValue[] newArray(int size) {
            return new SegValue[size];
        }
    };

    public SegValue(String field, int mode, Vector<String> texts, int servPattern) {
        this.mField = field;
        this.mMode = mode;
        this.mTexts = texts;
        this.mServPattern = servPattern;
    }

    public SegValue(Parcel in) {
        this.mField = in.readString();
        this.mTexts = new Vector();
        in.readList(this.mTexts, (ClassLoader)null);
        this.mMode = in.readInt();
        this.mServPattern = in.readInt();
    }

    public String getmField() {
        return this.mField;
    }

    public Vector<String> getmTexts() {
        return this.mTexts;
    }

    public int getmMode() {
        return this.mMode;
    }

    public int getServPattern() {
        return this.mServPattern;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mField);
        dest.writeList(this.mTexts);
        dest.writeInt(this.mMode);
        dest.writeInt(this.mServPattern);
    }

    public static Parcelable.Creator<SegValue> getCreator() {
        return CREATOR;
    }

}
