package com.qiming.fawcard.synthesize.base.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConvertUtil {
    /**
     * Drawable转换成一个Bitmap
     *
     * @param drawable drawable对象
     * @return Bitmap Bitmap对象
     */
    public static final Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static String longToString(long currentTime, String formatType)
            throws ParseException {
        Date date = longToDate(currentTime, formatType); // long类型转成Date类型
        String strTime = dateToString(date, formatType); // date类型转成String
        return strTime;
    }

    public static String dateToString(Date data, String formatType) {
        return new SimpleDateFormat(formatType).format(data);
    }

    public static Date longToDate(long currentTime, String formatType)
            throws ParseException {
        Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
        String sDateTime = dateToString(dateOld, formatType); // 把date类型的时间转换为string
        Date date = stringToDate(sDateTime, formatType); // 把String类型转换为Date类型
        return date;
    }

    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }

    public static long millisecondToMinute(long millisecond) {
        long minute = millisecond / (60 * 1000);

        return minute;
    }

    public static long minuteToMillisecond(long minute) {
        long millisecond = minute * 60 * 1000;

        return millisecond;
    }

    public static long secondToMinute(long second) {
        long minute = second / 60;

        return minute;
    }

    public static long minuteToSecond(long minute) {
        long second = minute * 60;

        return second;
    }

    /**
     * 将秒数转换为 日时分，
     *
     * @param second
     * @return
     */
    public static String secondToTime(long second) {
        long days = second / 86400;            //转换天数
        second = second % 86400;            //剩余秒数
        long hours = second / 3600;            //转换小时
        second = second % 3600;                //剩余秒数
        long minutes = second / 60;            //转换分钟
        second = second % 60;                //剩余秒数
        if (days > 0) {
            return days + "天" + hours + "小时" + minutes + "分钟" /*+ second + "秒"*/;
        } else if (hours > 0) {
            return hours + "小时" + minutes + "分钟" /*+ second + "秒"*/;
        } else /*if ()*/ {
            return minutes + "分钟" /*+ second + "秒"*/;
        }
    }

    /**
     * 将日期转换为日时分秒
     *
     * @param date
     * @return
     */
    public static String dateToTime(String date, String dateStyle) {
        SimpleDateFormat format = new SimpleDateFormat(dateStyle);
        try {
            Date oldDate = format.parse(date);
            long time = oldDate.getTime();                    //输入日期转换为毫秒数
            long nowTime = System.currentTimeMillis();        //当前时间毫秒数
            long second = nowTime - time;                    //二者相差多少毫秒
            second = second / 1000;                            //毫秒转换为妙
            long days = second / 86400;
            second = second % 86400;
            long hours = second / 3600;
            second = second % 3600;
            long minutes = second / 60;
            second = second % 60;
            if (days > 0) {
                return days + "天" + hours + "小时" + minutes + "分" + second + "秒";
            } else {
                return hours + "小时" + minutes + "分" + second + "秒";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
