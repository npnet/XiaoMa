package com.qiming.fawcard.synthesize.base.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarUtils {

    /**
     * 获取本周是本月的第几周
     * @return
     */
    public static int getWeekOfMonth() {
        Calendar calendar = Calendar.getInstance();
        int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
        return weekOfMonth;
    }

    /**
     * 获取上周周一
     * @return
     */
    public static Date getLastWeekMondayDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        calendar.add(Calendar.WEEK_OF_MONTH,-1); // 本周-1 = 上周
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY); // 周一
        return calendar.getTime();
    }

    /**
     * 获取上周周日
     * @return
     */
    public static Date getLastWeekSundayDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        calendar.add(Calendar.WEEK_OF_MONTH,-1); // 本周-1 = 上周
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY); // 周一
        return calendar.getTime();
    }

    /**
     * 获取上周周一
     * @return
     */
    public static String getLastWeekMonday(){
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.WEEK_OF_MONTH,-1); // 本周-1 = 上周
//        calendar.setFirstDayOfWeek(Calendar.MONDAY);
//        calendar.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY); // 周一
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String time = sdf.format(getLastWeekMondayDate().getTime());
        return time;
    }

    /**
     * 获取上周周一
     * @return
     */
    public static String getLastWeekSunday(){
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.WEEK_OF_MONTH,-1); // 本周-1 = 上周
//        calendar.setFirstDayOfWeek(Calendar.MONDAY);
//        calendar.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY); // 周一
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String time = sdf.format(getLastWeekSundayDate().getTime());
        return time;
    }
}
