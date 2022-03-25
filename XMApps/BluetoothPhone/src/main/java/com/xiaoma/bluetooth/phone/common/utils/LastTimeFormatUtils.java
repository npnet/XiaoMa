package com.xiaoma.bluetooth.phone.common.utils;

import android.util.Log;

import com.xiaoma.aidl.model.ContactBean;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

/**
 * @Author ZiXu Huang
 * @Data 2018/12/4
 */
public class LastTimeFormatUtils {

    public static String countTime(long mss) {
        DecimalFormat df = new DecimalFormat("00");
        long seconds = TimeUnit.MILLISECONDS.toSeconds(mss);
        if (seconds < 60) {
            return String.format("00:%s", df.format(seconds));
        }
        long minutes = TimeUnit.MILLISECONDS.toMinutes(mss);
        if (minutes < 60) {
            return String.format("%s:%s", df.format(minutes), df.format(seconds % 60));
        }
        long hours = TimeUnit.MILLISECONDS.toHours(mss);
        return String.format("%s:%s:%s",
                hours,
                df.format(minutes % 60),
                df.format((seconds % (60 * 60)) % 60));
    }

    public static String getCallDuration(ContactBean bean) {
        Log.i("LKF", String.format("getCallDuration -> start: %s, elapsed: %s", bean.getCallStartTime(), bean.getElapsedTime()));
        if (bean.getCallStartTime() <= 0) {
            bean.setCallStartTime(bean.getElapsedTime());
        }
        long l = bean.getElapsedTime() - bean.getCallStartTime();
        // 临时解决问题:断开蓝牙重连后,不更新通话时间,此处如果ElapsedTime正好是1000的整数,则表示是重连之后的时间
        if (bean.getElapsedTime() % 1000 == 0) {
            l = bean.getElapsedTime();
        }
        return countTime(l >= 0 ? l : 0);
    }
}

