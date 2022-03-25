package com.xiaoma.launcher.common.manager;

import android.content.Context;

import java.util.Locale;

public class LanguageUtils {
    public static String getCurrentLanguage(Context context){
        Locale locale = context.getResources().getConfiguration().locale;
        return locale.getLanguage();
    }

    public static boolean isChinese(Context context){
        return "zh".equals(getCurrentLanguage(context));
    }
}
