package com.xiaoma.autotracker.model;

/**
 * @author taojin
 * @date 2018/12/24
 */
public enum BusinessType {
    APPON("应用启动"),
    APPOFF("应用退出"),
    PLAYTIME("播放时长"),
    SYSTEMTIME("系统在线时长"),
    APPTIME("应用时长"),
    LISTENINFO("收听信息"),
    CLUBGROUPSCORE("车信部落计分"),;

    private String businessValue;

    BusinessType(String businessValue) {
        this.businessValue = businessValue;
    }

    public String getBusinessValue() {
        return businessValue;
    }
}
