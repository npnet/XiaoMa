package com.xiaoma.club.msg.redpacket.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ZYao.
 * Date ：2019/4/17 0017
 */
public class RPDetailItemInfo implements Parcelable {
    /**
     * id : 1
     * redEnvelopeId : 1
     * redEnvelopeType : 1
     * pointNum : 10
     * receiverUserId : 187
     * receiverTime : 1555317862000
     */

    private int id;
    private long redEnvelopeId;
    private int redEnvelopeType;
    private int pointNum;
    private long receiverUserId;
    private long receiverTime;
    private String receiverUserPicPath;
    private String receiverUserName;

    public String getReceiverUserPicPath() {
        return receiverUserPicPath;
    }

    public void setReceiverUserPicPath(String receiverUserPicPath) {
        this.receiverUserPicPath = receiverUserPicPath;
    }

    public String getReceiverUserName() {
        return receiverUserName;
    }

    public void setReceiverUserName(String receiverUserName) {
        this.receiverUserName = receiverUserName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getRedEnvelopeId() {
        return redEnvelopeId;
    }

    public void setRedEnvelopeId(long redEnvelopeId) {
        this.redEnvelopeId = redEnvelopeId;
    }

    public int getRedEnvelopeType() {
        return redEnvelopeType;
    }

    public void setRedEnvelopeType(int redEnvelopeType) {
        this.redEnvelopeType = redEnvelopeType;
    }

    public int getPointNum() {
        return pointNum;
    }

    public void setPointNum(int pointNum) {
        this.pointNum = pointNum;
    }

    public long getReceiverUserId() {
        return receiverUserId;
    }

    public void setReceiverUserId(long receiverUserId) {
        this.receiverUserId = receiverUserId;
    }

    public long getReceiverTime() {
        return receiverTime;
    }

    public void setReceiverTime(long receiverTime) {
        this.receiverTime = receiverTime;
    }

    public RPDetailItemInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeLong(this.redEnvelopeId);
        dest.writeInt(this.redEnvelopeType);
        dest.writeInt(this.pointNum);
        dest.writeLong(this.receiverUserId);
        dest.writeLong(this.receiverTime);
        dest.writeString(this.receiverUserPicPath);
        dest.writeString(this.receiverUserName);
    }

    protected RPDetailItemInfo(Parcel in) {
        this.id = in.readInt();
        this.redEnvelopeId = in.readLong();
        this.redEnvelopeType = in.readInt();
        this.pointNum = in.readInt();
        this.receiverUserId = in.readLong();
        this.receiverTime = in.readLong();
        this.receiverUserPicPath = in.readString();
        this.receiverUserName = in.readString();
    }

    public static final Creator<RPDetailItemInfo> CREATOR = new Creator<RPDetailItemInfo>() {
        public RPDetailItemInfo createFromParcel(Parcel source) {
            return new RPDetailItemInfo(source);
        }

        public RPDetailItemInfo[] newArray(int size) {
            return new RPDetailItemInfo[size];
        }
    };
}
