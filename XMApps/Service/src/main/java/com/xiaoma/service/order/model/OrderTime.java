package com.xiaoma.service.order.model;

/**
 * 预约时间项
 * Created by zhushi.
 * Date: 2018/11/15
 */
public class OrderTime {
    //是否选中
    private boolean isSelected;
    /**
     * id : 1
     * createDate : 1543907091000
     * channelId : AA1090
     * enableStatus : 1
     * orderLevel : 1
     * timePhase : 8:00-9:00
     */

    private int id;
    private long createDate;
    private String channelId;
    private String enableStatus;
    private int orderLevel;
    //预约时间
    private String timePhase;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getEnableStatus() {
        return enableStatus;
    }

    public void setEnableStatus(String enableStatus) {
        this.enableStatus = enableStatus;
    }

    public int getOrderLevel() {
        return orderLevel;
    }

    public void setOrderLevel(int orderLevel) {
        this.orderLevel = orderLevel;
    }

    public String getTimePhase() {
        return timePhase;
    }

    public void setTimePhase(String timePhase) {
        this.timePhase = timePhase;
    }
}
