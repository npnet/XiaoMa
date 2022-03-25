package com.xiaoma.autotracker.model;

/**
 * @author taojin
 * @date 2018/12/5
 * @desc 页面切换事件
 */
public enum AppViewScreen {

    APPPAGEFOREGROUND("页面前台事件", 3, TrackerEventType.LINKAROUTER),
    APPPAGEBACKGROUND("页面后台事件", 4, TrackerEventType.LINKAROUTER),
    APPFOREGROUND("APP前台事件", 1, TrackerEventType.LINKAROUTER),
    APPBACKGROUND("APP后台事件", 2, TrackerEventType.LINKAROUTER);

    private String appEventName;
    private int appStatus;
    private TrackerEventType eventType;

    AppViewScreen(String name, int status, TrackerEventType eventType) {
        this.appEventName = name;
        this.appStatus = status;
        this.eventType = eventType;
    }

    public String getAppEventName() {
        return appEventName;
    }

    public void setAppEventName(String appEventName) {
        this.appEventName = appEventName;
    }

    public int getAppStatus() {
        return appStatus;
    }

    public void setAppStatus(int appStatus) {
        this.appStatus = appStatus;
    }

    public TrackerEventType getEventType() {
        return eventType;
    }

    public void setEventType(TrackerEventType eventType) {
        this.eventType = eventType;
    }


}
