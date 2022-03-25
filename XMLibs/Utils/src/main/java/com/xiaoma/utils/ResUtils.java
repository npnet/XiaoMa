package com.xiaoma.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

/**
 * Created by youthyj on 2018/9/5.
 */
public class ResUtils {
    private ResUtils() throws Exception {
        throw new Exception();
    }

    public static Resources getResource(Context context) {
        if (context == null) {
            return null;
        }
        context = context.getApplicationContext();
        return context.getResources();
    }

    public static String getString(Context context, @StringRes int resId) {
        if (context == null) {
            return null;
        }
        context = context.getApplicationContext();
        return context.getResources().getString(resId);
    }

    public static int getColor(Context context, @ColorRes int resId) {
        if (context == null) {
            return -1;
        }
        context = context.getApplicationContext();
        return context.getResources().getColor(resId);
    }

    public static float getDimension(Context context, @DimenRes int resId) {
        if (context == null) {
            return Float.MIN_VALUE;
        }
        context = context.getApplicationContext();
        return context.getResources().getDimension(resId);
    }

    public static Drawable getDrawable(Context context, @DrawableRes int resId) {
        if (context == null) {
            return null;
        }
        context = context.getApplicationContext();
        return context.getResources().getDrawable(resId);
    }

}
