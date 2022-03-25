package com.xiaoma.personal.common.util;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Gillben
 * date: 2018-11-22
 */
public final class Utils {

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 隐藏手机号中间四位数
     *
     * @param phoneNumber
     * @return
     */
    public static String convertSimplePhoneNumber(String phoneNumber) {

        //跟路阳确定，这里如果手机号不对的话就直接显示，因为已经都不对了
        //只有主账户才有手机号，子账户是没有手机号的。而主账户手机号是4s店确认的，手机号不对的情况几乎不会发生。
        if(TextUtils.isEmpty(phoneNumber)
                || phoneNumber.length() != 11){
            return phoneNumber;
        }

        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < phoneNumber.length(); i++) {
            char child = phoneNumber.charAt(i);
            if (i > 2 && i < 7) {
                child = '*';
            }
            builder.append(child);
        }
        return builder.toString();
    }

    public static String getDateFromStamp(Long birthDayLong) {
        Date date = new Date(birthDayLong);
        return simpleDateFormat.format(date);
    }
}
