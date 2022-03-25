package com.xiaoma.service.order.model;

import java.io.Serializable;

/**
 * 维保项目model
 * Created by zhushi.
 * Date: 2018/11/16
 */
public class ProgramBean implements Serializable {
    private boolean isSelected = false;
    /**
     * id : 1
     * createDate : 1543910978000
     * channelId : AA1090
     * enableStatus : 1
     * orderLevel : 1
     * name : 预约保养
     * type : 1
     */

    private long id;
    private long createDate;
    private String channelId;
    private String enableStatus;
    private int orderLevel;
    private String name;
    private int type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
