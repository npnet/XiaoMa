package com.xiaoma.player;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zhushi.
 * Date: 2019/2/14
 */
public class AudioCategoryBean implements Parcelable {

    public static final Creator<AudioCategoryBean> CREATOR = new Creator<AudioCategoryBean>() {
        @Override
        public AudioCategoryBean createFromParcel(Parcel source) {
            return new AudioCategoryBean(source);
        }

        @Override
        public AudioCategoryBean[] newArray(int size) {
            return new AudioCategoryBean[size];
        }
    };
    private int categoryId; // 节目id ,用于校验
    private int action;
    private int index; //播放的index
    private String usbPath;//usb音乐路径

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public AudioCategoryBean() {
        action = AudioConstants.PlayAction.DEFAULT;
        index = -1;
        categoryId = 0;
    }

    protected AudioCategoryBean(Parcel in) {
        this.categoryId = in.readInt();
        this.action = in.readInt();
        this.index = in.readInt();
        this.usbPath = in.readString();
    }

    @AudioConstants.PlayAction
    public int getAction() {
        return action;
    }

    public void setAction(@AudioConstants.PlayAction int action) {
        this.action = action;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.categoryId);
        dest.writeInt(this.action);
        dest.writeInt(this.index);
        dest.writeString(this.usbPath);
    }

    public String getUsbPath() {
        return usbPath;
    }

    public void setUsbPath(String usbPath) {
        this.usbPath = usbPath;
    }
}
