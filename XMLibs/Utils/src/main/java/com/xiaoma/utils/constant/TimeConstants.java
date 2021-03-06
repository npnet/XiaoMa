package com.xiaoma.utils.constant;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class TimeConstants {

    public static final int MSEC = 1;
    public static final int SEC  = 1000;
    public static final int MIN  = 60000;
    public static final int HOUR = 3600000;
    public static final int DAY  = 86400000;

    public static final String SIGN_COLON = ":";
    public static final String PAD_ZERO = "0";
    public static final String PAD_ZERO_ZERO = "00";
    public static final String TIME_POINT = ".";
    public static final String SPLIT_POINT = "\\.";

    @IntDef({MSEC, SEC, MIN, HOUR, DAY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Unit {
    }
}
