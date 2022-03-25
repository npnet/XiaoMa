package com.xiaoma.setting.common.utils;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: ZiXu Huang
 * @Data: 2019/8/17
 * @Desc:
 */
public class NoChinecFilter implements InputFilter {
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]+");
        Matcher matcher = p.matcher(source);
        if (matcher.matches()) {
            return "";
        } else {
            return null;
        }
    }
}
