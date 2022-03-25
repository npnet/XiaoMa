package com.xiaoma.setting.common.utils;

import android.text.TextUtils;

/**
 * @Author ZiXu Huang
 * @Data 2018/12/25
 */
public class StringUtils {
    public static String subString(int length, String text) {
        if (TextUtils.isEmpty(text)) return null;
        if (length <= 0) return text;
        if (text.length() <= length) return text;
        return text.substring(0, length) + "...";
    }
}
