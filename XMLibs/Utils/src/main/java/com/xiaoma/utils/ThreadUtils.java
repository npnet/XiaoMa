package com.xiaoma.utils;

import android.os.Looper;

/**
 * Created by youthyj on 2018/9/7.
 */
public class ThreadUtils {
    private ThreadUtils() throws Exception {
        throw new Exception();
    }

    public static boolean isMainThread() {
        return Looper.getMainLooper().equals(Looper.myLooper());
    }

    public static long getThreadId() {
        return Thread.currentThread().getId();
    }

    public static String getThreadName() {
        return Thread.currentThread().getName();
    }

}
