package com.xiaoma.assistant.model.parser;

import android.text.TextUtils;
import android.widget.TextView;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/26
 * Desc：天气
 */
public class WeatherParserResult {
    private String city = "";
    private String date = "";

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isEmpty() {
        return TextUtils.isEmpty(city) || TextUtils.isEmpty(date);
    }
}
