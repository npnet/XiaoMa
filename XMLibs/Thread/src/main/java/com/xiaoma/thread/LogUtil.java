package com.xiaoma.thread;

import android.util.Log;

import com.xiaoma.utils.StringUtil;

/**
 * Created by LKF on 2018/9/10 0010.
 */
class LogUtil {
    static void logI(String tag, String log, Object... args) {
        Log.i(tag, StringUtil.format(log, args));
    }

    static void logE(String tag, String log, Object... args) {
        Log.e(tag, StringUtil.format(log, args));
    }
}
