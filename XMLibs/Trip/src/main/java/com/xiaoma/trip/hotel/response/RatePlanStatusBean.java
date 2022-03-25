package com.xiaoma.trip.hotel.response;

/**
 * @author taojin
 * @date 2019/2/21
 */
public class RatePlanStatusBean {

    private String ratePlanId;
    private String hotelId;
    private String roomId;
    private boolean status;
    private int maxRoomCount = -1;

    public String getRatePlanId() {
        return ratePlanId;
    }

    public void setRatePlanId(String ratePlanId) {
        this.ratePlanId = ratePlanId;
    }

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getMaxRoomCount() {
        return maxRoomCount;
    }

    public void setMaxRoomCount(int maxRoomCount) {
        this.maxRoomCount = maxRoomCount;
    }
}
