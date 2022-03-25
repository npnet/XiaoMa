package com.xiaoma.motorcade.common.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 简介: 字符格式匹配工具类
 *
 * @author lingyan
 */
public class PatternUtils {

    public static boolean isNumberic(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        Pattern p = Pattern.compile("[0-9]*");
        Matcher m = p.matcher(text);
        if (m.matches()) {
            return true;
        }
        return false;
    }

}
