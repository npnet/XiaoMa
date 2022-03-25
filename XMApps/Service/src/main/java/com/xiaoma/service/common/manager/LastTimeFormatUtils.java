package com.xiaoma.service.common.manager;

import android.text.TextUtils;

/**
 * @Author ZiXu Huang
 * @Data 2018/12/4
 */
public class LastTimeFormatUtils {

    public static String countTime(long mss) {
        String time = null;
        String daysString = null;
        String hoursString = null;
        String minutesString = null;
        String secondsString = null;
        long days = mss / (1000 * 60 * 60 * 24);
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        if (days > 0 && days < 10) {
            daysString = "0" + days;
        } else if (days >= 10) {
            daysString = days + "";
        }

        if (hours >= 0 && hours < 10) {
            hoursString = "0" + hours;
        } else if (hours >= 10) {
            hoursString = hours + "";
        }

        if (minutes >= 0 && minutes < 10) {
            minutesString = "0" + minutes;
        } else if (minutes >= 10) {
            minutesString = minutes + "";
        }

        if (seconds >= 0 && seconds < 10) {
            secondsString = "0" + seconds;
        } else if (seconds >= 10) {
            secondsString = seconds + "";
        }

        if (!TextUtils.isEmpty(daysString)) {
            time = daysString + "d";
        }

        if (TextUtils.isEmpty(time)) {
            if (!TextUtils.equals(hoursString, "00")) {
                time = hoursString + ":";
            }
        } else {
            time += ":" + hoursString + ":";
        }
        if (TextUtils.isEmpty(time)) {
            time = minutesString + ":" + secondsString;
        } else {
            time += minutesString + ":" + secondsString;
        }

        return time;
    }


}
