package com.xiaoma.music.online.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.xiaoma.music.kuwo.model.XMBaseQukuItem;

import java.io.Serializable;

/**
 * @author zs
 * @date 2018/10/10 0010.
 */

public class Category implements Serializable, Parcelable {

    private String name;
    private XMBaseQukuItem item;

    public Category(String name, XMBaseQukuItem item) {
        this.name = name;
        this.item = item;
    }

    public XMBaseQukuItem getItem() {
        return item;
    }

    public void setItem(XMBaseQukuItem item) {
        this.item = item;
    }

    public String getName() {
        return name;
    }

    public Category(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
    }

    protected Category(Parcel in) {
        this.name = in.readString();
    }

    public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {
        public Category createFromParcel(Parcel source) {
            return new Category(source);
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };
}
