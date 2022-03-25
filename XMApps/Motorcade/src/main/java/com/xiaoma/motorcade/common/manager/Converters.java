package com.xiaoma.motorcade.common.manager;

import android.arch.persistence.room.TypeConverter;
import android.text.TextUtils;

import com.xiaoma.utils.GsonHelper;

import java.util.List;

/**
 * Created by LKF on 2019-2-22 0022.
 * 数据转换器
 */
public class Converters {
    @TypeConverter
    public static String strList2Str(List<String> strList) {
        if (strList == null || strList.isEmpty())
            return "";
        return GsonHelper.toJson(strList);
    }

    @TypeConverter
    public static List<String> str2StrList(String str) {
        if (TextUtils.isEmpty(str))
            return null;
        return GsonHelper.fromJsonToList(str, String[].class);
    }
}
