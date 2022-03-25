package com.xiaoma.assistant.model;

import android.support.annotation.Nullable;

import com.xiaoma.utils.GsonHelper;

import java.io.Serializable;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/30
 * Desc:
 */
public class UploadLimitInfo implements Serializable {
    private int queryDay;
    private String queryCity = "";
    private String queryDate = "";

    @Nullable
    public static UploadLimitInfo parseFormJson(String json) {
        return GsonHelper.fromJson(json, UploadLimitInfo.class);
    }

    public int getQueryDay() {
        return queryDay;
    }

    public String getQueryCity() {
        return queryCity;
    }

    public String getQueryDate() {
        return queryDate;
    }
}
