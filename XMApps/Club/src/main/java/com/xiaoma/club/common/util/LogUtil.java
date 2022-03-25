package com.xiaoma.club.common.util;

import android.util.Log;

import com.xiaoma.utils.StringUtil;

/**
 * Created by LKF on 2018/10/13 0013.
 * 模块内使用的日志工具类,方便后期切换Logger
 */
public class LogUtil {
    public static void logD(String tag, String format, Object... args) {
        Log.d(tag, StringUtil.format(format, args));
    }

    public static void logI(String tag, String format, Object... args) {
        Log.i(tag, StringUtil.format(format, args));
    }

    public static void logW(String tag, String format, Object... args) {
        Log.w(tag, StringUtil.format(format, args));
    }

    public static void logE(String tag, String format, Object... args) {
        Log.e(tag, StringUtil.format(format, args));
    }
}
