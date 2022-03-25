package com.xiaoma.music.welcome.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <des>
 *
 * @author Jir
 * @date 2018/10/8
 */
public class PreferenceBean implements Parcelable {

    private String tagName;
    private String thridId;
    private boolean selected;

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getThridId() {
        return thridId;
    }

    public void setThridId(String thridId) {
        this.thridId = thridId;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.tagName);
        dest.writeString(this.thridId);
        dest.writeByte(this.selected ? (byte) 1 : (byte) 0);
    }

    public PreferenceBean() {
    }

    protected PreferenceBean(Parcel in) {
        this.tagName = in.readString();
        this.thridId = in.readString();
        this.selected = in.readByte() != 0;
    }

    public static final Creator<PreferenceBean> CREATOR = new Creator<PreferenceBean>() {
        @Override
        public PreferenceBean createFromParcel(Parcel source) {
            return new PreferenceBean(source);
        }

        @Override
        public PreferenceBean[] newArray(int size) {
            return new PreferenceBean[size];
        }
    };
}
