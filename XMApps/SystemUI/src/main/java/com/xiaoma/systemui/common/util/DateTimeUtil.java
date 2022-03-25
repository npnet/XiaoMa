package com.xiaoma.systemui.common.util;

import java.util.Date;

/**
 * Created by LKF on 2019-3-13 0013.
 */
public class DateTimeUtil {
    public static boolean isSameDay(long t1, long t2) {
        return isSameDay(new Date(t1), new Date(t2));
    }

    public static boolean isSameDay(Date d1, Date d2) {
        if (d1 == null || d2 == null)
            return false;
        return d1.getYear() == d2.getYear()
                && d1.getMonth() == d2.getMonth()
                && d1.getDate() == d2.getDate();
    }
}
