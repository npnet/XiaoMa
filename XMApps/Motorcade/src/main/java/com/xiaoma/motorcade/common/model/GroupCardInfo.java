package com.xiaoma.motorcade.common.model;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Author: loren
 * Date: 2018/10/10 0010
 */

@Entity
public class GroupCardInfo implements Serializable, Parcelable {

    /**
     * "id": 1052496984289718300,
     * "createDate": 1539769805000,
     * "modifyDate": 1539769805000,
     * "nick": "hh",
     * "lat": 43.8267,
     * "lon": 125.238,
     * "address": "",
     * "state": 1,
     * "needPassword": 0,
     * "password": "0",
     * "adminId": 1052471624852582400,
     * "resultCode": "1",
     * "count": 0,
     * "roomId": "63304045232129",
     * "isIn": 0,
     * "chatId": "63304050475010",
     * "picPath": "http://111.230.137.157:8181/111",
     * "enableStatus": "1",
     * "tags": "",
     * "firstCarId": "cd00980f262588d63b38971a882dab76",
     * "firstUserId": "",
     * "distance": "0.00",
     * "qunNo": 1005658,
     * "headerPathUrl": "http://111.230.137.157:8181/111",
     * "type": "0",
     * "privateStatus": "0",
     * "qunTypeId": 0,
     * "channelId": "AA1090",
     * "qunDesc": "",
     * "hotRealScore": 0,
     * "hotShowScore": 0,
     * "qunInform": "",
     * "brief": "",
     * "showQunPics": ["http://www.carbuyin.net/by3/userHeader/default_01.png"],
     * "showHotStars": 2
     */

    @PrimaryKey
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

    // 注意!!! : 后台返回的roomId字段是环信的【群组Id】，千万不要搞反
    //注意！！！ 后台返回的qunKey字段是车队口令，以下的hxGroupId只作为车队口令使用
    @SerializedName("qunKey")
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

    public int getOnlineUserCount() {
        return onlineUserCount;
    }

    public void setOnlineUserCount(int onlineUserCount) {
        this.onlineUserCount = onlineUserCount;
    }

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

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getHxGroupId() {
        return TextUtils.isEmpty(hxGroupId) ? "" : hxGroupId;
    }

    public void setHxGroupId(String hxGroupId) {
        this.hxGroupId = hxGroupId;
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

    public void setIsIn(int isIn) {
        this.isIn = isIn;
    }

    public boolean isIn() {
        return isIn > 0;
    }

    public String getAdminHxId() {
        return adminHxId;
    }

    public void setAdminHxId(String adminHxId) {
        this.adminHxId = adminHxId;
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

    public int getIsIn() {
        return isIn;
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
    }

    public GroupCardInfo() {
    }

    protected GroupCardInfo(Parcel in) {
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
    }

    public static final Parcelable.Creator<GroupCardInfo> CREATOR = new Parcelable.Creator<GroupCardInfo>() {
        public GroupCardInfo createFromParcel(Parcel source) {
            return new GroupCardInfo(source);
        }

        public GroupCardInfo[] newArray(int size) {
            return new GroupCardInfo[size];
        }
    };
}
