package com.xiaoma.carlib.utils;

import android.util.Log;

import com.xiaoma.utils.log.KLog;

public class LogUtils {
    private static boolean SHOW_DEBUG_LOG = true;

    public static void e(String msg) {
        if (SHOW_DEBUG_LOG) {
            KLog.d(msg);
        }
    }

    public static void e(String tag, String msg) {
        if (SHOW_DEBUG_LOG) {
            Log.e(tag, msg);
        }
    }

}
