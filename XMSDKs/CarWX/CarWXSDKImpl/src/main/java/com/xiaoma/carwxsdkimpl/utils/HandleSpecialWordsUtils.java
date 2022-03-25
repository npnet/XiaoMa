package com.xiaoma.carwxsdkimpl.utils;

import android.text.TextUtils;

public class HandleSpecialWordsUtils {

    private final static String[] sPunctuation = {",", ".", "?", ";", "!", "，", "。", "？", "；", "！"};

    public static String replaceFilter(String voiceContent) {
        if (TextUtils.isEmpty(voiceContent)) {
            return "";
        }
        if (isLastCharSpecial(voiceContent)) {
            voiceContent = voiceContent.substring(0, voiceContent.length() - 1);
        }
        return voiceContent;
    }

    private static boolean isLastCharSpecial(String content) {
        content = content.substring(content.length() - 1);
        for (int i = 0; i < sPunctuation.length; i++) {
            if (sPunctuation[i].equals(content)) {
                return true;
            }
        }
        return false;
    }

}