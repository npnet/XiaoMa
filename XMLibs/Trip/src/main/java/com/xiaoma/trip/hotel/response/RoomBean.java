package com.xiaoma.trip.hotel.response;

import java.util.List;

/**
 * Created by zhushi.
 * Date: 2018/12/7
 */
public class RoomBean {
    /**
     * images : [{"imageUrl":"http://pic.cnbooking.net:10541/uploadorder/201808/78e876a4-8ae9-40cc-b964-7ae56f42bd75.jpg","imageId":"","imageName":"商务单人间"},{"imageUrl":"http://pic.cnbooking.net:10541/uploadorder/201808/9886b6bf-5317-4f2a-b2e4-164c0ad0cefc.jpg","imageId":"","imageName":"商务单人间"}]
     * intro : 该房可无烟处理
     * guide :
     * roomId : 40118
     * roomName : 商务单人间
     * roomTypeId : 009020
     * roomType : 大床房
     * bedTypeId : 001009
     * bedType : 180*200cm大床
     * acreage : 30
     * floor : 6-11
     * roomAcount : 10
     * maxAdult : 2
     * maxChild : 0
     * hasWindow : 2
     * allowAddBed : 0
     * allowAddBedNum : 0
     * allowSmoke : 1
     * hasNet : 1
     * isNetFee : 0
     * netFee : 0
     */

    private String intro;
    private String guide;
    private String roomId;
    private String roomName;
    private String roomTypeId;
    private String roomType;
    private String bedTypeId;
    private String bedType;
    private String acreage;
    private String floor;
    private String roomAcount;
    private String maxAdult;
    private String maxChild;
    private String hasWindow;
    private String allowAddBed;
    private String allowAddBedNum;
    private String allowSmoke;
    private String hasNet;
    private String isNetFee;
    private String netFee;
    private List<ImageBean> images;

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getGuide() {
        return guide;
    }

    public void setGuide(String guide) {
        this.guide = guide;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(String roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getBedTypeId() {
        return bedTypeId;
    }

    public void setBedTypeId(String bedTypeId) {
        this.bedTypeId = bedTypeId;
    }

    public String getBedType() {
        return bedType;
    }

    public void setBedType(String bedType) {
        this.bedType = bedType;
    }

    public String getAcreage() {
        return acreage;
    }

    public void setAcreage(String acreage) {
        this.acreage = acreage;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getRoomAcount() {
        return roomAcount;
    }

    public void setRoomAcount(String roomAcount) {
        this.roomAcount = roomAcount;
    }

    public String getMaxAdult() {
        return maxAdult;
    }

    public void setMaxAdult(String maxAdult) {
        this.maxAdult = maxAdult;
    }

    public String getMaxChild() {
        return maxChild;
    }

    public void setMaxChild(String maxChild) {
        this.maxChild = maxChild;
    }

    public String getHasWindow() {
        return hasWindow;
    }

    public void setHasWindow(String hasWindow) {
        this.hasWindow = hasWindow;
    }

    public String getAllowAddBed() {
        return allowAddBed;
    }

    public void setAllowAddBed(String allowAddBed) {
        this.allowAddBed = allowAddBed;
    }

    public String getAllowAddBedNum() {
        return allowAddBedNum;
    }

    public void setAllowAddBedNum(String allowAddBedNum) {
        this.allowAddBedNum = allowAddBedNum;
    }

    public String getAllowSmoke() {
        return allowSmoke;
    }

    public void setAllowSmoke(String allowSmoke) {
        this.allowSmoke = allowSmoke;
    }

    public String getHasNet() {
        return hasNet;
    }

    public void setHasNet(String hasNet) {
        this.hasNet = hasNet;
    }

    public String getIsNetFee() {
        return isNetFee;
    }

    public void setIsNetFee(String isNetFee) {
        this.isNetFee = isNetFee;
    }

    public String getNetFee() {
        return netFee;
    }

    public void setNetFee(String netFee) {
        this.netFee = netFee;
    }

    public List<ImageBean> getImages() {
        return images;
    }

    public void setImages(List<ImageBean> images) {
        this.images = images;
    }
}
