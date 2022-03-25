package com.xiaoma.trip.hotel.request;

/**
 * @author taojin
 * @date 2019/3/26
 * 用于酒店下单
 */
public class HotelInfo {

    private String id;
    private String address;
    private String lat;
    private String lon;
    private String roomType;
    private String roomMsg;
    private boolean canCancel;
    private String lastCancelDate;
    private String mobile;
    private String hotelName;
    private String iconUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getRoomMsg() {
        return roomMsg;
    }

    public void setRoomMsg(String roomMsg) {
        this.roomMsg = roomMsg;
    }

    public boolean isCanCancel() {
        return canCancel;
    }

    public void setCanCancel(boolean canCancel) {
        this.canCancel = canCancel;
    }

    public String getLastCancelDate() {
        return lastCancelDate;
    }

    public void setLastCancelDate(String lastCancelDate) {
        this.lastCancelDate = lastCancelDate;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public HotelInfo(String id, String address, String lat, String lon, String roomType, String roomMsg, boolean canCancel, String lastCancelDate, String mobile, String hotelName, String iconUrl) {
        this.id = id;
        this.address = address;
        this.lat = lat;
        this.lon = lon;
        this.roomType = roomType;
        this.roomMsg = roomMsg;
        this.canCancel = canCancel;
        this.lastCancelDate = lastCancelDate;
        this.mobile = mobile;
        this.hotelName = hotelName;
        this.iconUrl = iconUrl;
    }
}
