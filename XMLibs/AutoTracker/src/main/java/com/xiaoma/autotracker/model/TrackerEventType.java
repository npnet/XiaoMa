package com.xiaoma.autotracker.model;

/**
 * @author taojin
 * @date 2018/12/5
 */
public enum TrackerEventType {

    LINKAROUTER("链路跳转", 1),

    ONCLICK("点击事件", 2),

    BUSINESS("业务", 3),

    EXPOSE("曝光事件", 4);

    private String eventName;
    private int eventValue;


    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public int getEventValue() {
        return eventValue;
    }

    public void setEventValue(int eventValue) {
        this.eventValue = eventValue;
    }

    TrackerEventType(String name, int value) {
        this.eventName = name;
        this.eventValue = value;
    }


}
