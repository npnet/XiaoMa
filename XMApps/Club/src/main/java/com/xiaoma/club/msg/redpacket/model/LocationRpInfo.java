package com.xiaoma.club.msg.redpacket.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ZYao.
 * Date ：2019/4/16 0016
 */
public class LocationRpInfo implements Parcelable {
    /**
     * id : 9
     * userId : 188
     * channelId : AA1080
     * redEnvelopeType : 2
     * redEnvelopeStatus : 1
     * sendTime : 1555407257000
     * toUserId : null
     * toGroupId : 847402480479379456
     * message : 恭喜发财
     * pointNum : 5
     * totalNum : 4
     * receivedNum : 0
     * remainderNum : 4
     * createDate : 1555407257000
     * modifyDate : null
     * lon : 111.11111
     * lat : 22.2222
     * poiName : 深圳市南山区
     * location : 粤美特大厦
     * isLocationRedEnvelope : 1
     */

    private long id;
    private long userId;
    private String channelId;
    private int redEnvelopeType;
    private int redEnvelopeStatus;
    private long sendTime;
    private long toUserId;
    private long toGroupId;
    private String message;
    private int pointNum;
    private int totalNum;
    private int receivedNum;
    private int remainderNum;
    private long createDate;
    private long modifyDate;
    private double lon;
    private double lat;
    private String poiName;
    private String location;
    private int isLocationRedEnvelope;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public int getRedEnvelopeType() {
        return redEnvelopeType;
    }

    public void setRedEnvelopeType(int redEnvelopeType) {
        this.redEnvelopeType = redEnvelopeType;
    }

    public int getRedEnvelopeStatus() {
        return redEnvelopeStatus;
    }

    public void setRedEnvelopeStatus(int redEnvelopeStatus) {
        this.redEnvelopeStatus = redEnvelopeStatus;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public long getToUserId() {
        return toUserId;
    }

    public void setToUserId(long toUserId) {
        this.toUserId = toUserId;
    }

    public long getToGroupId() {
        return toGroupId;
    }

    public void setToGroupId(long toGroupId) {
        this.toGroupId = toGroupId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getPointNum() {
        return pointNum;
    }

    public void setPointNum(int pointNum) {
        this.pointNum = pointNum;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public int getReceivedNum() {
        return receivedNum;
    }

    public void setReceivedNum(int receivedNum) {
        this.receivedNum = receivedNum;
    }

    public int getRemainderNum() {
        return remainderNum;
    }

    public void setRemainderNum(int remainderNum) {
        this.remainderNum = remainderNum;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public long getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(long modifyDate) {
        this.modifyDate = modifyDate;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getPoiName() {
        return poiName;
    }

    public void setPoiName(String poiName) {
        this.poiName = poiName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getIsLocationRedEnvelope() {
        return isLocationRedEnvelope;
    }

    public void setIsLocationRedEnvelope(int isLocationRedEnvelope) {
        this.isLocationRedEnvelope = isLocationRedEnvelope;
    }


    public LocationRpInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeLong(this.userId);
        dest.writeString(this.channelId);
        dest.writeInt(this.redEnvelopeType);
        dest.writeInt(this.redEnvelopeStatus);
        dest.writeLong(this.sendTime);
        dest.writeLong(this.toUserId);
        dest.writeLong(this.toGroupId);
        dest.writeString(this.message);
        dest.writeInt(this.pointNum);
        dest.writeInt(this.totalNum);
        dest.writeInt(this.receivedNum);
        dest.writeInt(this.remainderNum);
        dest.writeLong(this.createDate);
        dest.writeLong(this.modifyDate);
        dest.writeDouble(this.lon);
        dest.writeDouble(this.lat);
        dest.writeString(this.poiName);
        dest.writeString(this.location);
        dest.writeInt(this.isLocationRedEnvelope);
    }

    protected LocationRpInfo(Parcel in) {
        this.id = in.readLong();
        this.userId = in.readLong();
        this.channelId = in.readString();
        this.redEnvelopeType = in.readInt();
        this.redEnvelopeStatus = in.readInt();
        this.sendTime = in.readLong();
        this.toUserId = in.readLong();
        this.toGroupId = in.readLong();
        this.message = in.readString();
        this.pointNum = in.readInt();
        this.totalNum = in.readInt();
        this.receivedNum = in.readInt();
        this.remainderNum = in.readInt();
        this.createDate = in.readLong();
        this.modifyDate = in.readLong();
        this.lon = in.readDouble();
        this.lat = in.readDouble();
        this.poiName = in.readString();
        this.location = in.readString();
        this.isLocationRedEnvelope = in.readInt();
    }

    public static final Creator<LocationRpInfo> CREATOR = new Creator<LocationRpInfo>() {
        public LocationRpInfo createFromParcel(Parcel source) {
            return new LocationRpInfo(source);
        }

        public LocationRpInfo[] newArray(int size) {
            return new LocationRpInfo[size];
        }
    };
}
