package com.xiaoma.utils;

import android.app.AlarmManager;
import android.content.Context;

import java.net.URL;
import java.net.URLConnection;

/**
 * Created by ZYao.
 * Date ：2019/5/27 0027
 */
public class GetNetTimeUtils {

    public static String webUrl1 = "http://www.bjtime.cn";//bjTime
    public static String webUrl2 = "http://www.baidu.com";//百度
    public static String webUrl3 = "http://www.taobao.com";//淘宝
    public static String webUrl4 = "http://www.ntsc.ac.cn";//中国科学院国家授时中心
    public static String webUrl5 = "http://www.360.cn";//360
    public static String webUrl6 = "http://www.beijing-time.org";//beijing-time

    public static boolean setWebsiteDatetime(final Context context) {
        try {
            long start = System.currentTimeMillis();
            URL url = new URL(webUrl2);// 取得资源对象
            URLConnection uc = url.openConnection();// 生成连接对象
            uc.connect();// 发出连接
            long ld = uc.getDate();// 读取网站日期时间
            if (ld > 0) {
                //先设置时区
                long end = System.currentTimeMillis();
                AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                mAlarmManager.setTimeZone("Asia/Shanghai");
                mAlarmManager.setTime(ld + (end - start));
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
