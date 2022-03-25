package com.xiaoma.assistant.utils;

import android.text.TextUtils;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/16
 * Desc：特殊字符转义
 */
public class HandleSpecialWordUtils {

    private final static String[] sPunctuation = {",", ".", "?", ";", "!", "，", "。", "？", "；", "！"};

    public static String replaceFilter(String voiceContent){
        if (TextUtils.isEmpty(voiceContent)) {
            return "";
        }
        if (isLastStringContainsChar(voiceContent)) {
            voiceContent = voiceContent.substring(0, voiceContent.length() - 1);
        }
        return voiceContent;
    }

    private static boolean isLastStringContainsChar(String content) {
        content = content.substring(content.length() - 1, content.length());
        for (int i = 0; i < sPunctuation.length; i++) {
            if (sPunctuation[i].equals(content)) {
                return true;
            }
        }
        return false;
    }

}
