package com.xiaoma.assistant.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoma.assistant.Assistant;
import com.xiaoma.assistant.R;
import com.xiaoma.assistant.model.WeatherInfoV2;
import com.xiaoma.assistant.utils.NumTransUtils;

import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/11/26
 * Desc：
 */
public class WeatherView extends RelativeLayout {
    private TextView tvCity;
    private TextView tvDate;
    private TextView tvTemp;
    private TextView tvInfo;
    private TextView tvAir;
    private TextView tvCurrentDay;
    private ImageView ivIcon;
    private TextView date1;
    private TextView date2;
    private TextView date3;
    private TextView date4;
    private TextView tem1;
    private TextView tem2;
    private TextView tem3;
    private TextView tem4;
    private TextView weather1;
    private TextView weather2;
    private TextView weather3;
    private TextView weather4;


    public WeatherView(Context context) {
        super(context);
        initView();
    }

    public WeatherView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public WeatherView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        View view = inflate(getContext(), R.layout.view_assistant_weather, this);
        tvCity = view.findViewById(R.id.tv_weather_city);
        tvDate = view.findViewById(R.id.tv_weather_date);
        tvTemp = view.findViewById(R.id.tv_weather_degree);
        tvInfo = view.findViewById(R.id.tv_weather_info);
        tvAir = view.findViewById(R.id.tv_weather_air);
        tvCurrentDay = view.findViewById(R.id.tv_weather_day);
        date1 = view.findViewById(R.id.tv_date1);
        date2 = view.findViewById(R.id.tv_date2);
        date3 = view.findViewById(R.id.tv_date3);
        date4 = view.findViewById(R.id.tv_date4);
        tem1 = view.findViewById(R.id.tv_temperature1);
        tem2 = view.findViewById(R.id.tv_temperature2);
        tem3 = view.findViewById(R.id.tv_temperature3);
        tem4 = view.findViewById(R.id.tv_temperature4);
        weather1 = view.findViewById(R.id.tv_weather1);
        weather2 = view.findViewById(R.id.tv_weather2);
        weather3 = view.findViewById(R.id.tv_weather3);
        weather4 = view.findViewById(R.id.tv_weather4);
        ivIcon = view.findViewById(R.id.iv_weather_icon);
    }


    public void setData(WeatherInfoV2 weatherInfoV2) {
        if (weatherInfoV2 == null) return;
        List<WeatherInfoV2.ResultBean> results = weatherInfoV2.result;
        if (results == null) return;
        refreshData(results,weatherInfoV2);
    }

    private void refreshData(List<WeatherInfoV2.ResultBean> results,WeatherInfoV2 weatherInfoV2) {
        WeatherInfoV2.ResultBean resultBean = results.get(0);
        tvCity.setText(resultBean.area);
        tvDate.setText(resultBean.date);

//        tvTemp.setText(resultBean.day_air_temperature + "~" + resultBean.night_air_temperature + "°C");
        tvTemp.setText(NumTransUtils.getMinValue(resultBean.day_air_temperature,resultBean.night_air_temperature)
                + "~" + NumTransUtils.getMaxValue(resultBean.day_air_temperature,resultBean.night_air_temperature)  + "°C");
        tvInfo.setText(resultBean.day_weather);
        tvAir.setText(getContext().getString(R.string.air_quarity) + resultBean.quality);
        tvCurrentDay.setText(weatherInfoV2.changeWeeks(Assistant.getInstance().getApplication().getApplicationContext(), resultBean.weekday));

        try {
            WeatherInfoV2.ResultBean resultBean2 = results.get(1);
            date1.setText(resultBean2.date);
//            tem1.setText(resultBean2.day_air_temperature + "~" + resultBean2.night_air_temperature + "°C");
            tem1.setText(NumTransUtils.getMinValue(resultBean2.day_air_temperature,resultBean2.night_air_temperature)
                    + "~" +NumTransUtils.getMaxValue(resultBean2.day_air_temperature,resultBean2.night_air_temperature) + "°C");
            weather1.setText(resultBean2.day_weather);
            WeatherInfoV2.ResultBean resultBean3 = results.get(2);
            date2.setText(resultBean3.date);
//            tem2.setText(resultBean3.day_air_temperature + "~" + resultBean2.night_air_temperature + "°C");
            tem2.setText(NumTransUtils.getMinValue(resultBean3.day_air_temperature,resultBean3.night_air_temperature)
                    + "~" +NumTransUtils.getMaxValue(resultBean3.day_air_temperature,resultBean3.night_air_temperature) + "°C");
            weather2.setText(resultBean3.day_weather);
            WeatherInfoV2.ResultBean resultBean4 = results.get(3);
            date3.setText(resultBean4.date);
//            tem3.setText(resultBean4.day_air_temperature + "~" + resultBean2.night_air_temperature + "°C");
            tem3.setText(NumTransUtils.getMinValue(resultBean4.day_air_temperature,resultBean4.night_air_temperature)
                    + "~" +NumTransUtils.getMaxValue(resultBean4.day_air_temperature,resultBean4.night_air_temperature) + "°C");
            weather3.setText(resultBean4.day_weather);
            WeatherInfoV2.ResultBean resultBean5 = results.get(4);
            date4.setText(resultBean5.date);
//            tem4.setText(resultBean5.day_air_temperature + "~" + resultBean2.night_air_temperature + "°C");
            tem4.setText(NumTransUtils.getMinValue(resultBean5.day_air_temperature,resultBean5.night_air_temperature)
                    + "~" +NumTransUtils.getMaxValue(resultBean5.day_air_temperature,resultBean5.night_air_temperature) + "°C");
            weather4.setText(resultBean5.day_weather);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
