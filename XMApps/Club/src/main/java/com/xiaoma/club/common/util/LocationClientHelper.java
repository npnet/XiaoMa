package com.xiaoma.club.common.util;

import android.content.Context;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

/**
 * Created by LKF on 2019-1-7 0007.
 */
public class LocationClientHelper {
    private static final int LOCATING_INTERVAL = 5000;
    private AMapLocationClient mLocationClient;

    public void create(Context context, boolean onceLocation, AMapLocationListener listener) {
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        //初始化定位客户端
        AMapLocationClient locationClient = new AMapLocationClient(context);
        // 定位客户端设置
        AMapLocationClientOption option = makeLocationOption();
        option.setOnceLocation(onceLocation);
        locationClient.setLocationOption(option);
        // 监听定位数据
        locationClient.setLocationListener(listener);
        mLocationClient = locationClient;
    }

    public void startLocation() {
        if (mLocationClient != null)
            mLocationClient.startLocation();
    }

    public void stopLocation() {
        if (mLocationClient != null)
            mLocationClient.stopLocation();
    }

    public void onDestroy() {
        if (mLocationClient != null) {
            mLocationClient.onDestroy();
            mLocationClient = null;
        }
    }

    public static AMapLocationClientOption makeLocationOption() {
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        option.setLocationCacheEnable(true);
        option.setInterval(LOCATING_INTERVAL);
        return option;
    }
}
