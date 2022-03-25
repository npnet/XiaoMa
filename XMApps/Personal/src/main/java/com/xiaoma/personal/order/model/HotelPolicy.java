package com.xiaoma.personal.order.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <pre>
 *       @author Created by Gillben
 *       date: 2019/3/21 0021 10:24
 *       desc：酒店政策(订房须知)
 * </pre>
 */
public class HotelPolicy implements Parcelable {


    /**
     * channelId : AA1000
     * hotelId : 23048
     * time : 入住时间：15:00以后      离店时间：12:00以前
     * cancel : 不同类型的客房附带不同的取消预订和预先付费政策 选择上述客房时，请参阅“客房政策”
     * pet : 不可携带宠物。
     * child : 不接受18岁以下客人单独入住。不接受18岁以下客人在无监护人陪同的情况下入住
     */

    private String channelId;
    private String hotelId;
    private String time;
    private String cancel;
    private String pet;
    private String child;

    protected HotelPolicy(Parcel in) {
        channelId = in.readString();
        hotelId = in.readString();
        time = in.readString();
        cancel = in.readString();
        pet = in.readString();
        child = in.readString();
    }

    public static final Creator<HotelPolicy> CREATOR = new Creator<HotelPolicy>() {
        @Override
        public HotelPolicy createFromParcel(Parcel in) {
            return new HotelPolicy(in);
        }

        @Override
        public HotelPolicy[] newArray(int size) {
            return new HotelPolicy[size];
        }
    };

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCancel() {
        return cancel;
    }

    public void setCancel(String cancel) {
        this.cancel = cancel;
    }

    public String getPet() {
        return pet;
    }

    public void setPet(String pet) {
        this.pet = pet;
    }

    public String getChild() {
        return child;
    }

    public void setChild(String child) {
        this.child = child;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(channelId);
        dest.writeString(hotelId);
        dest.writeString(time);
        dest.writeString(cancel);
        dest.writeString(pet);
        dest.writeString(child);
    }
}
