package com.xiaoma.smarthome.scene.model;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.smarthome.scene.model
 *  @file_name:
 *  @author:         Rookie
 *  @create_time:    2019/1/24 15:31
 *  @description：   TODO             */

public class AutoExecuteData {
    public double lat;
    public double lng;
    public long distance;
    public String cmdName;
    public int startTimeHour;
    public int endTimeHour;

    @Override
    public String toString() {
        return "AutoExecuteData{" +
                "lat=" + lat +
                ", lng=" + lng +
                ", distance=" + distance +
                ", cmdName='" + cmdName + '\'' +
                '}';
    }
}
