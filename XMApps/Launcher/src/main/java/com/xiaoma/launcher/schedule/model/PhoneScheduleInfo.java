package com.xiaoma.launcher.schedule.model;

/**
 * @Author: XuJian.
 * @Date: 2018/08/01 22:19.
 * @Describe: 从手机端同步的日程.
 */

public class PhoneScheduleInfo {

    private String id;
    private long createDate;
    private String remindDate;
    private String remindBeginTime;
    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public String getRemindDate() {
        return remindDate;
    }

    public void setRemindDate(String remindDate) {
        this.remindDate = remindDate;
    }

    public String getRemindBeginTime() {
        return remindBeginTime;
    }

    public void setRemindBeginTime(String remindBeginTime) {
        this.remindBeginTime = remindBeginTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
