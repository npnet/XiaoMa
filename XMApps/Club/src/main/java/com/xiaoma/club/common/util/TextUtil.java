package com.xiaoma.club.common.util;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by LKF on 2019-1-24 0024.
 */
public class TextUtil {
    /**
     * 判断是否包含中文
     */
    public static boolean hasChinese(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        final Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        final Matcher m = p.matcher(text);
        return m.find();
    }
}
