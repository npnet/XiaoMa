package com.xiaoma.launcher.schedule.utils;

import android.content.Context;

import com.xiaoma.launcher.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    /**
     * 根据年 月 获取对应的月份 天数
     */
    public static int getDaysByYearMonth(int year, int month) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DATE, 1);
        calendar.roll(Calendar.DATE, -1);
        int maxDate = calendar.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * 获取当月的 天数
     */
    public static int getCurrentMonthDay() {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }


    /**
     * 获取年
     */
    public static int getCurrentYear() {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        return year;
    }

    /**
     * 获取月
     */
    public static int getCurrentMonth() {

        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;
        return month;
    }

    /**
     * 获取日
     */
    public static int getCurrentDay() {

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return day;
    }

    /**
     * 根据提供的年月日获取该月份的第一天
     */
    public static long getSupportBeginDayOfMonth(int year, int monthOfYear) {
        Calendar cal = Calendar.getInstance();
        // 不加下面2行，就是取当前时间前一个月的第一天及最后一天
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear - 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date firstDate = cal.getTime();
        return firstDate.getTime();
    }

    /**
     * 根据提供的年月获取该月份的最后一天
     */
    public static long getSupportEndDayOfMonth(int year, int monthOfYear) {
        Calendar cal = Calendar.getInstance();
        // 不加下面2行，就是取当前时间前一个月的第一天及最后一天
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date lastDate = cal.getTime();
        return lastDate.getTime();
    }


    /**
     * 当天的开始时间
     *
     * @return
     */
    public static long startOfDay(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date date = calendar.getTime();
        return date.getTime();
    }

    /**
     * 当天的结束时间
     *
     * @return
     */
    public static long endOfDay(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date date = calendar.getTime();
        return date.getTime();
    }


    public static long endOfToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date date = calendar.getTime();
        return date.getTime();
    }

    public static long startOfTomorrow() {
        return endOfToday() + 1;
    }

    /**
     * eg: 返回 2019/4/11
     *
     * @return
     */
    public static String formatDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        Date date = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        return sdf.format(date);
    }

    /**
     * 当天的结束时间
     *
     * @return
     */
    public static int formatWeek(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        return week;
    }

    /**
     * 当天的时间 eg:2019/04/15
     *
     * @return
     */
    public static String getCurrentFormatDate() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        return sdf.format(date);
    }

    /**
     * 当天的时间 eg:2019-04-15
     *
     * @return
     */
    public static String getLineFormatDate() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }


    public static String getFormatDate(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        try {
            Date date = format.parse(time);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            return sdf.format(date);
        } catch (Exception e) {
            return time;
        }
    }

    /**
     * 2019/02/14 返回 eg:2019年2月14日
     *
     * @param time
     * @return
     */
    public static String getFormatDate2(String time) {
        String[] dateStr = time.split("/");
        StringBuffer sb = new StringBuffer();
        sb.append(dateStr[0] + "年");
        sb.append(Integer.parseInt(dateStr[1]) + "月");
        sb.append(Integer.parseInt(dateStr[2]) + "日");
        return sb.toString();
    }

    public static long getCurrentTimeMinute() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/ddHH:mm");
        String format = sdf.format(date) + ":00";
        return date2Ms2(format);
    }


    public static String getTomorrowFormatDate() {
        Date date = new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        return sdf.format(date);
    }

    /**
     * 日期格式化转换
     *
     * @param date
     * @return
     */
    public static long date2Ms(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/ddHH:mm");
        try {
            return format.parse(date).getTime();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 日期格式化转换
     *
     * @param data
     * @return
     */
    public static long date2Ms2(String data) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/ddHH:mm:ss");
        try {
            Date date = format.parse(data);
            return date.getTime();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 日期格式化转换
     *
     * @param data
     * @return
     */
    public static long date2Ms3(String data) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
        try {
            Date date = format.parse(data);
            return date.getTime();
        } catch (Exception e) {
            return 0;
        }
    }

    public static String getWeek(Calendar selectedDate) {
        String Week = "";
        int wek = selectedDate.get(Calendar.DAY_OF_WEEK);

        if (wek == 1) {
            Week += "周日";
        }
        if (wek == 2) {
            Week += "周一";
        }
        if (wek == 3) {
            Week += "周二";
        }
        if (wek == 4) {
            Week += "周三";
        }
        if (wek == 5) {
            Week += "周四";
        }
        if (wek == 6) {
            Week += "周五";
        }
        if (wek == 7) {
            Week += "周六";
        }
        return Week;
    }

    /**
     * 从时间(毫秒)中提取出时间(时:分)
     * 时间格式: yyyy-MM-dd HH:mm:ss
     *
     * @param millisecond
     * @return
     */
    public static String getTimeFromMillisecond(Long millisecond) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(millisecond);
        return simpleDateFormat.format(date);
    }

    public static String getWelcomeStrByTime(Context context) {
        String welcomeStr;
        Calendar instance = Calendar.getInstance();
        int currHour = instance.get(Calendar.HOUR_OF_DAY);
        //4:00-6:00 清晨好
        //6:00-10:59 早上好
        //11:00-14:59 中午好
        //15:00-17:59 下午好
        //18:00-次日3:59 晚上好
        if (currHour >= 4 && currHour <= 6) {
            welcomeStr = context.getString(R.string.good_morning1);
        } else if (currHour <= 10) {
            welcomeStr = context.getString(R.string.good_morning2);
        } else if (currHour <= 14) {
            welcomeStr = context.getString(R.string.good_afternoon1);
        } else if (currHour <= 17) {
            welcomeStr = context.getString(R.string.good_afternoon2);
        } else {
            welcomeStr = context.getString(R.string.good_evening);
        }
        return welcomeStr;
    }

    /**
     * 获得两个日期间距多少天
     *
     * @param beginDate
     * @param endDate
     * @return
     */
    public static int getTimeDistance(Date beginDate, Date endDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(beginDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long time1 = cal.getTime().getTime();

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(endDate);
        cal2.set(Calendar.HOUR_OF_DAY, 0);
        cal2.set(Calendar.MINUTE, 0);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);
        long time2 = cal2.getTime().getTime();

        long betweenDays = Math.abs(time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(betweenDays));
    }

    //两个日期在没在同一周
    public static boolean isSameWeekWithToday(Calendar todayCal, Calendar dateCal) {
        //比较当前日期在年份中的周数是否相同
        return todayCal.get(Calendar.WEEK_OF_YEAR) == dateCal.get(Calendar.WEEK_OF_YEAR);
    }


}
