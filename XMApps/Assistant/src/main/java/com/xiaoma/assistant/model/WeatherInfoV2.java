package com.xiaoma.assistant.model;

import android.content.Context;
import android.text.TextUtils;

import com.xiaoma.assistant.R;
import com.xiaoma.assistant.constants.AssistantConstants;
import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.utils.GsonHelper;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/17
 * Desc：
 */
public class WeatherInfoV2 {

    public String resultCode;
    public String resultMessage;
    public List<ResultBean> result;
    private String[] weeks;
    private String[] weeks2;

    public static WeatherInfoV2 parseFromJson(String data) {
        return GsonHelper.fromJson(data, WeatherInfoV2.class);
    }

    private String wrapperTemp(Context context, String tempRange) {
        if (TextUtils.isEmpty(tempRange)) {
            return "";
        }
        if (!tempRange.contains("-")) {
            return tempRange;
        }
        String newTemp = tempRange.replaceAll("-", context.getString(R.string.temp_below_zero));
        return newTemp;
    }

    public String changeWeeks(Context context, String weekday) {
        if (weeks == null || weeks2 == null) {
            weeks = context.getResources().getStringArray(R.array.weeks2_start_one);
            weeks2 = context.getResources().getStringArray(R.array.weeks_start_one);
        }
        List<String> list = Arrays.asList(weeks);
        int indexOf = list.indexOf(weekday);
        if (indexOf == -1) {
            return "";
        }
        return weeks2[indexOf];
    }

    private String changeDays(Context context, String day, String weekday) {
        String formatDays;
        if (context.getString(R.string.today).equals(weekday) || context.getString(R.string.tomorrow).equals(weekday)) {
            formatDays = weekday;
        } else {
            try {
                formatDays = new SimpleDateFormat(context.getString(R.string.simple_data_format))
                        .format(new SimpleDateFormat("MM/dd").parse(day));
            } catch (Exception e) {
                formatDays = day;
            }
        }
        return formatDays;
    }

    public String makeSpeakingWord(Context context) {

        if (result == null) return "";
        ResultBean resultBean = result.get(0);
        String weather = "";
        if (resultBean.day_weather != null) {
            if (resultBean.day_weather.equals(resultBean.night_weather)) {
                weather = resultBean.day_weather;
            } else {
                weather = resultBean.day_weather + context.getString(R.string.weather_change) + resultBean.night_weather;
            }
        }

        StringBuilder builder = new StringBuilder();
        builder.append(resultBean.area)
                .append(changeDays(context, resultBean.date, resultBean.weekday))
                .append(context.getString(R.string.comma));

        String week = changeWeeks(context, resultBean.weekday);
        if (!TextUtils.isEmpty(week)) {
            builder.append(week)
                    .append(context.getString(R.string.comma));
        }
        String windPower = resultBean.wind_power;
        if (windPower.contains("级") && windPower.indexOf("级") != windPower.length() - 1) {
            windPower = windPower.substring(0, windPower.indexOf("级") + 1);//resultBean.wind_power示例: 3-4级 5.5~7.9m/s
        }
        builder.append(weather)
                .append(context.getString(R.string.comma))
                .append(String.format(context.getString(R.string.weather_voice_broadcast),
                        wrapperTemp(context, resultBean.night_air_temperature),
                        wrapperTemp(context, resultBean.day_air_temperature)))
                .append(context.getString(R.string.comma))
                .append(resultBean.wind_direction + windPower)
                .append(context.getString(R.string.comma))
                .append(context.getString(R.string.air_quality))
                .append(resultBean.quality);
        String s = builder.toString();
      if ("晴".equals(weather)){
          XmCarFactory.getCarVendorExtensionManager().setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_WEATHER_SUN);
      }else if (weather.contains("云")){
          XmCarFactory.getCarVendorExtensionManager().setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_WEATHER_CLOUD);
      }else if (weather.contains("雨")){
          XmCarFactory.getCarVendorExtensionManager().setRobAction(AssistantConstants.RobActionKey.ROB_ACTION_WEATHER_RAIN);
      }
        return s;
    }

    public static class ResultBean {
        public String night_weather;
        public String day_weather;
        public String area;
        public String weekday;
        public String wind_direction;
        public String wind_power;
        public String quality;
        public String weather_pic;
        public String date;
        public String temperature;
        public String day_air_temperature;
        public String night_air_temperature;
        public String now;
    }

}
