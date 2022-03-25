package com.xiaoma.service.main.model;

import java.io.Serializable;

/**
 * Created by ZouShao on 2018/11/16 0016.
 */

public class MaintainBean implements Serializable {
    private static final long serialVersionUID = 8455704317375855983L;
    /**
     * id : 16
     * createDate : 1544169009000
     * channelId : AA1090
     * enableStatus : 1
     * orderLevel : 16
     * name : 制动液
     * upkeepMethod :
     * upkeepPeriod :
     * picUrl :
     * optionContent：
     */

    private int id;
    private long createDate;
    private String channelId;
    private String enableStatus;
    private int orderLevel;
    private String name;
    private String upkeepMethod;
    private String upkeepPeriod;
    private String picUrl;
    private String optionContent;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUpkeepMethod() {
        return upkeepMethod;
    }

    public void setUpkeepMethod(String upkeepMethod) {
        this.upkeepMethod = upkeepMethod;
    }

    public String getUpkeepPeriod() {
        return upkeepPeriod;
    }

    public void setUpkeepPeriod(String upkeepPeriod) {
        this.upkeepPeriod = upkeepPeriod;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getOptionContent() {
        return optionContent;
    }

    public void setOptionContent(String optionContent) {
        this.optionContent = optionContent;
    }
}
