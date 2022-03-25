package com.xiaoma.motorcade.common.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Author: loren
 * Date: 2019/4/16 0016
 */

public class MeetingInfo implements Serializable, Parcelable {

    private long id;
    private String nick;
    private double lat;
    private double lon;
    private String address;
    private long adminId;
    private int isIn;

    // 注意!!! : 后台返回的chatId字段是环信的【聊天室】Id，千万不要搞反
    @SerializedName("chatId")
    private String chatRoomId;

    private String qunKey;

    // 注意!!! : 后台返回的roomId字段是环信的【群组Id】，千万不要搞反
    @SerializedName("roomId")
    private String hxGroupId;

    @SerializedName("firstCarId")
    private String adminHxId;

    private String picPath;
    private String qunInform;
    private String brief;
    private List<String> showQunPics;
    private int showHotStars;
    private int count;
    private long qunNo;
    private int onlineUserCount;
    private String meetingAccount;
    private String meetingPassword;
    private boolean needCreate;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getAdminId() {
        return adminId;
    }

    public void setAdminId(long adminId) {
        this.adminId = adminId;
    }

    public int getIsIn() {
        return isIn;
    }

    public void setIsIn(int isIn) {
        this.isIn = isIn;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getHxGroupId() {
        return hxGroupId;
    }

    public void setHxGroupId(String hxGroupId) {
        this.hxGroupId = hxGroupId;
    }

    public String getAdminHxId() {
        return adminHxId;
    }

    public void setAdminHxId(String adminHxId) {
        this.adminHxId = adminHxId;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public String getQunInform() {
        return qunInform;
    }

    public void setQunInform(String qunInform) {
        this.qunInform = qunInform;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public List<String> getShowQunPics() {
        return showQunPics;
    }

    public void setShowQunPics(List<String> showQunPics) {
        this.showQunPics = showQunPics;
    }

    public int getShowHotStars() {
        return showHotStars;
    }

    public void setShowHotStars(int showHotStars) {
        this.showHotStars = showHotStars;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getQunNo() {
        return qunNo;
    }

    public void setQunNo(long qunNo) {
        this.qunNo = qunNo;
    }

    public int getOnlineUserCount() {
        return onlineUserCount;
    }

    public void setOnlineUserCount(int onlineUserCount) {
        this.onlineUserCount = onlineUserCount;
    }

    public String getMeetingAccount() {
        return meetingAccount;
    }

    public void setMeetingAccount(String meetingAccount) {
        this.meetingAccount = meetingAccount;
    }

    public String getMeetingPassword() {
        return meetingPassword;
    }

    public void setMeetingPassword(String meetingPassword) {
        this.meetingPassword = meetingPassword;
    }

    public String getQunKey() {
        return qunKey;
    }

    public void setQunKey(String qunKey) {
        this.qunKey = qunKey;
    }

    public boolean isNeedCreate() {
        return needCreate;
    }

    public void setNeedCreate(boolean needCreate) {
        this.needCreate = needCreate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.nick);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lon);
        dest.writeString(this.address);
        dest.writeLong(this.adminId);
        dest.writeInt(this.isIn);
        dest.writeString(this.chatRoomId);
        dest.writeString(this.hxGroupId);
        dest.writeString(this.adminHxId);
        dest.writeString(this.picPath);
        dest.writeString(this.qunInform);
        dest.writeString(this.brief);
        dest.writeStringList(this.showQunPics);
        dest.writeInt(this.showHotStars);
        dest.writeInt(this.count);
        dest.writeLong(this.qunNo);
        dest.writeInt(this.onlineUserCount);
        dest.writeString(this.meetingAccount);
        dest.writeString(this.meetingPassword);
        dest.writeString(this.qunKey);
    }

    public MeetingInfo() {
    }

    protected MeetingInfo(Parcel in) {
        this.id = in.readLong();
        this.nick = in.readString();
        this.lat = in.readDouble();
        this.lon = in.readDouble();
        this.address = in.readString();
        this.adminId = in.readLong();
        this.isIn = in.readInt();
        this.chatRoomId = in.readString();
        this.hxGroupId = in.readString();
        this.adminHxId = in.readString();
        this.picPath = in.readString();
        this.qunInform = in.readString();
        this.brief = in.readString();
        this.showQunPics = in.createStringArrayList();
        this.showHotStars = in.readInt();
        this.count = in.readInt();
        this.qunNo = in.readLong();
        this.onlineUserCount = in.readInt();
        this.meetingAccount = in.readString();
        this.meetingPassword = in.readString();
        this.qunKey = in.readString();
    }

    public static final Parcelable.Creator<MeetingInfo> CREATOR = new Parcelable.Creator<MeetingInfo>() {
        public MeetingInfo createFromParcel(Parcel source) {
            return new MeetingInfo(source);
        }

        public MeetingInfo[] newArray(int size) {
            return new MeetingInfo[size];
        }
    };
}
