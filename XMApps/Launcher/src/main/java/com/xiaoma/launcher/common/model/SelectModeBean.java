package com.xiaoma.launcher.common.model;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.common.model
 *  @file_name:      SelectModeBean
 *  @author:         Rookie
 *  @create_time:    2019/8/15 20:00
 *  @description：   TODO             */

import java.util.Calendar;

public class SelectModeBean {

    private Calendar calendar;
    private int model;
    //星期六和星期一自动显示过的标记
    private boolean haveShownToday;

    public boolean isHaveShownToday() {
        return haveShownToday;
    }

    public void setHaveShownToday(boolean haveShownToday) {
        this.haveShownToday = haveShownToday;
    }

    public SelectModeBean(Calendar calendar, int model) {
        this.calendar = calendar;
        this.model = model;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public int getModel() {
        return model;
    }

    public void setModel(int model) {
        this.model = model;
    }
}
