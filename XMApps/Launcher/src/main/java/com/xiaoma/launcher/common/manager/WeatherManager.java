package com.xiaoma.launcher.common.manager;

import android.support.v4.graphics.drawable.IconCompat;
import android.text.TextUtils;

import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.component.AppHolder;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.common.model.Weather;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.systemuilib.StatusBarControl;
import com.xiaoma.systemuilib.StatusBarSlot;
import com.xiaoma.thread.Priority;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;

import java.util.Random;


/**
 * @author taojin
 * @date 2019/4/9
 */

public class WeatherManager {

    public static final String CITY_WEATHER_DETAIL = "city_weather";
    private static WeatherManager instance;
    private final String TAG = WeatherManager.class.getSimpleName();
    private final long CYCLE_GET_WEATHER_TIME = 10 * 60 * 1000;
    private final long CYCLE_GET_WEATHER_FAILED_TIME = 10 * 1000;
    private final long INTERVALS = 10 * 1000;
    private final long ACTION_DEALY_TIME = 5 * 1000;
    private String currentCityName;
    private int[] drawableIds = {R.drawable.icon_weather_sunny,
            R.drawable.icon_weather_cloudy,
            R.drawable.icon_weather_overcast,
            R.drawable.icon_weather_shower,
            R.drawable.icon_weather_thunderstorms,
            R.drawable.icon_weather_light_rain,
            R.drawable.icon_weather_moderate_rain,
            R.drawable.icon_weather_heavy_rain,
            R.drawable.icon_weather_storm_rain,
            R.drawable.icon_weather_light_snow,
            R.drawable.icon_weather_moderate_snow,
            R.drawable.icon_weather_heavy_snow,
            R.drawable.icon_weather_sleet,
            R.drawable.icon_weather_hail,
            R.drawable.icon_weather_sandstorm};

    private long oldTime = 0;
    private int[] actions;

    private WeatherManager() {

    }

    public static WeatherManager getInstance() {
        if (instance == null) {
            synchronized (WeatherManager.class) {
                if (instance == null) {
                    instance = new WeatherManager();
                }
            }
        }
        return instance;
    }

    public void getCurrCityWeather() {
        Weather weather = TPUtils.getObject(AppHolder.getInstance().getAppContext(), CITY_WEATHER_DETAIL, Weather.class);
        if (weather != null) {
            setWeatherIcon(weather.getWeather());
        }
        getLocationWeather(CYCLE_GET_WEATHER_FAILED_TIME);
    }

    public void getCurrRobAction() {
        oldTime = System.currentTimeMillis();
        actions = AppHolder.getInstance().getAppContext().getResources().getIntArray(R.array.actions);
        getLoactionAction(ACTION_DEALY_TIME);
    }

    public void OnLocationChange(LocationInfo locationInfo) {
        if (locationInfo != null && !TextUtils.isEmpty(locationInfo.getCity()) && isCityChanged(locationInfo)) {
            String city = locationInfo.getCity();
            currentCityName = city;
            getWeather();
        }
    }

    private void getLocationWeather(long delayTime) {
        ThreadDispatcher.getDispatcher().postDelayed(new Runnable() {
            @Override
            public void run() {
                getWeather();
            }
        }, delayTime, Priority.NORMAL);

    }

    private void getWeather() {
        if (null == LocationManager.getInstance().getCurrentLocation()) {
            getLocationWeather(CYCLE_GET_WEATHER_FAILED_TIME);
        } else {
            currentCityName = LocationManager.getInstance().getCurrentLocation().getCity();
            if (TextUtils.isEmpty(currentCityName)) {
                getLocationWeather(CYCLE_GET_WEATHER_FAILED_TIME);
            } else {
                getWeatherFromServer(currentCityName);
            }
        }
    }

    private void getWeatherFromServer(String currentCityName) {
        RequestManager.getInstance().getCityWeather(currentCityName, new WeatherResultCallBack<Weather>() {
            @Override
            public void onSuccess(Weather weather) {
                if (weather != null) {
                    //天气详情本地缓存起来
                    TPUtils.putObject(AppHolder.getInstance().getAppContext(), CITY_WEATHER_DETAIL, weather);
                    getLocationWeather(CYCLE_GET_WEATHER_TIME);
                    setWeatherIcon(weather.getWeather());
                } else {
                    getLocationWeather(CYCLE_GET_WEATHER_FAILED_TIME);
                }
            }

            @Override
            public void onFailure(int code, String msg) {

            }
        });

    }

    private void setWeatherIcon(String desc) {
        if (desc == null || desc.isEmpty()) {
            return;
        }
        String[] weatherArray = AppHolder.getInstance().getAppContext().getResources().getStringArray(R.array.weatherInfo);
        for (int i = 0; i < weatherArray.length; i++) {
            if (desc.contains(weatherArray[i])) {
                if (i <= 1) {
                    RobActionManager.getInstance().setRobAction(LauncherConstants.SUN_ACTION);
                } else if (i >= 2 && i < 13) {
                    RobActionManager.getInstance().setRobAction(LauncherConstants.RAIN_ACTION);
                }
                StatusBarControl.setIcon(AppHolder.getInstance().getAppContext(), StatusBarSlot.SLOT_WEATHER, IconCompat.createWithResource(AppHolder.getInstance().getAppContext(), drawableIds[i]).toIcon());
                KLog.d(TAG, "weather" + desc);
            }
        }
    }

    private boolean isCityChanged(LocationInfo locationInfo) {
        return !TextUtils.isEmpty(locationInfo.getCity()) && !TextUtils.isEmpty(currentCityName) && !locationInfo.getCity().equals(currentCityName);
    }

    public void getLoactionAction(long delayTime) {
        ThreadDispatcher.getDispatcher().postDelayed(new Runnable() {
            @Override
            public void run() {
                getAction();
            }
        }, delayTime, Priority.NORMAL);
    }

    private void getAction() {
        long currentTime = System.currentTimeMillis();
        int ret = XmCarVendorExtensionManager.getInstance().getRobAction();//获取3D全息执行待机动作（序号30）
        KLog.e(TAG, "getAction() ret=" + ret);
        if (ret == 30) {
            if ((currentTime - oldTime) >= INTERVALS) {
                Random random = new Random();
                int value = random.nextInt(5);
                KLog.e(TAG, "getAction() value=" + value);
                RobActionManager.getInstance().setRobAction(actions[value]);
                oldTime = currentTime;
            }
        } else {
            oldTime = currentTime;
        }
        getLoactionAction(ACTION_DEALY_TIME);
    }


}
