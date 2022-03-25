package com.xiaoma.utils;

import android.os.SystemClock;

/**
 * Created by kaka
 * on 19-5-30 下午12:04
 * <p>
 * desc: #a
 * </p>
 */
public class DoubleClickUtils {
    private static long lastClickTime;

    public static boolean isFastDoubleClick(long ms) {
        long time = SystemClock.elapsedRealtime();
        if (time - lastClickTime < ms) {
            return true;
        }
        lastClickTime = time;
        return false;
    }
}
