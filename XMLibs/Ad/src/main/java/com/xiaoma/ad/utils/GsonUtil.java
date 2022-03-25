package com.xiaoma.ad.utils;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xiaoma.ad.models.LinkType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author KY
 * @date 2018/9/13
 */
public class GsonUtil {
    private static final Gson GSON;

    // 注册TypeAdapter以实现枚举的json直接解析
    static {
        GSON = new GsonBuilder()
                .registerTypeAdapter(LinkType.class, new LinkType.GsonAdapter())
                .create();
    }

    /**
     * 解析集合json
     *
     * @param json  json string
     * @param clazz object class
     * @param <T>   object type
     * @return generic List object
     */
    @NonNull
    public static <T> List<T> fromJsonToList(String json, Class<T[]> clazz) {
        if (TextUtils.isEmpty(json)) {
            return new ArrayList<>();
        }
        try {
            T[] arr = GSON.fromJson(json, clazz);
            if (arr == null || arr.length <= 0) {
                return new ArrayList<>();
            }
            return Arrays.asList(arr);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * 序列化json
     *
     * @param object object
     * @return json string
     */
    public static String toJson(Object object) {
        return GSON.toJson(object);
    }
}
