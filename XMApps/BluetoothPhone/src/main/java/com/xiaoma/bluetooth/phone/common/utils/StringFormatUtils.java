package com.xiaoma.bluetooth.phone.common.utils;

/**
 * @Author ZiXu Huang
 * @Data 2018/12/12
 */
public class StringFormatUtils {

    //20181212T194407
    public static String getDateFromString(String string) {
        String[] ts = string.split("T");
        if (ts.length >= 1) {
            String t = ts[0];
            if (t.length() == 8) {
//                String substring = t.substring(4);
                String temp;
                temp = t.substring(0, 4) + "-" + t.substring(4, 6) + "-" + t.substring(6) + " " + ts[1].substring(0, 2) + ":" + ts[1].substring(2, 4);
                return temp;
            }
        }
        return null;
    }

    public static String getDate(String t){
        return t.substring(0, 4) + "-" + t.substring(4, 6) + "-" + t.substring(6);
    }

    //20181212T194407
    public static String getTimeFromString(String string) {
        String[] ts = string.split("T");
        if (ts.length >= 2) {
            String t = ts[1];
            if (t.length() == 6) {
                return t.substring(0, 2) + ":" + t.substring(2, 4);
            }
        }
        return null;
    }

    public static String getTimeStampString(String string) {
        return string.replace("T", "");
    }
}
