package com.xiaoma.xting.welcome.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <des>
 *
 * @author Jir
 * @date 2018/10/8
 */
public class PreferenceBean implements Parcelable {

    public static final String TAG_UNSELECTED = "1";
    public static final String TAG_SELECTED = "2";
    /**
     * id : 2
     * thridId : 2
     * channelId : AA1090
     * createDate : 1540895808000
     * tagName : test2
     * orderLevel : 2
     * enableStatus : 1
     */

    private int id;
    private int thridId;
    private String channelId;
    private long createDate;
    private String tagName;
    private int orderLevel;
    private String enableStatus;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getThridId() {
        return thridId;
    }

    public void setThridId(int thridId) {
        this.thridId = thridId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public int getOrderLevel() {
        return orderLevel;
    }

    public void setOrderLevel(int orderLevel) {
        this.orderLevel = orderLevel;
    }

    public String getEnableStatus() {
        return enableStatus;
    }

    public void setEnableStatus(String enableStatus) {
        this.enableStatus = enableStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeInt(this.thridId);
        dest.writeString(this.channelId);
        dest.writeLong(this.createDate);
        dest.writeString(this.tagName);
        dest.writeInt(this.orderLevel);
        dest.writeString(this.enableStatus);
    }

    public PreferenceBean() {
    }

    protected PreferenceBean(Parcel in) {
        this.id = in.readInt();
        this.thridId = in.readInt();
        this.channelId = in.readString();
        this.createDate = in.readLong();
        this.tagName = in.readString();
        this.orderLevel = in.readInt();
        this.enableStatus = in.readString();
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

    public boolean isSelected() {
        return TAG_SELECTED.equals(enableStatus);
    }

    public void reverseSelectState() {
        if (isSelected()) {
            this.enableStatus = TAG_UNSELECTED;
        } else {
            this.enableStatus = TAG_SELECTED;
        }
    }
}
