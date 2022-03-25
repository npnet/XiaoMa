package com.xiaoma.assistant.model.parser;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/11/30
 * Desc:
 */
public class DateTimeBean {

    /**
     * date : 2018-07-26
     * time : 15:00:00
     * timeOrig : 下午3点
     * type : DT_BASIC
     */

    private String date;
    private String time;
    private String timeOrig;
    private String type;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimeOrig() {
        return timeOrig;
    }

    public void setTimeOrig(String timeOrig) {
        this.timeOrig = timeOrig;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
