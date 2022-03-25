package com.xiaoma.utils;

import android.content.Context;
import android.content.Intent;

/**
 * Created by ZYao.
 * Date ：2018/9/12 0012
 */
public class ServiceUtils {

    /**
     * 考虑到Android 8.0在后台调用startService时会抛出IllegalStateException
     *
     * @param context
     * @param intent
     */
    public static boolean startServiceSafely(Context context, Intent intent) {
        if (null == context) {
            return false;
        }
        try {
            context.startService(intent);
            return true;
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * 考虑到Android 8.0在后台调用stopService时会抛出IllegalStateException
     *
     * @param context
     * @param intent
     */
    public static boolean stopServiceSafely(Context context, Intent intent) {
        if (null == context) {
            return false;
        }
        try {
            context.stopService(intent);
            return true;
        } catch (IllegalStateException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
