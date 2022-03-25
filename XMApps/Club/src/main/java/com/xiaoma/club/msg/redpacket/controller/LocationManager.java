package com.xiaoma.club.msg.redpacket.controller;

import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.xiaoma.utils.log.KLog;

/**
 * Created by ZYao.
 * Date ：2019/1/30 0030
 */
public class LocationManager implements AMapLocationListener {
    public AMapLocationClient mlocationClient;
    public AMapLocationClientOption mLocationOption = null;
    private OnLocationChangedListener mListener;

    public static LocationManager getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static final LocationManager instance = new LocationManager();
    }

    public void initLocation(Context context, OnLocationChangedListener listener) {
        mlocationClient = new AMapLocationClient(context);
        mLocationOption = new AMapLocationClientOption();
        mlocationClient.setLocationListener(this);
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
        mlocationClient.setLocationOption(mLocationOption);
        mListener = listener;
    }


    public void startLocation() {
        if (mlocationClient != null) {
            mlocationClient.startLocation();
        }
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        KLog.d("MrMine","onLocationChanged: "+amapLocation.getAddress());
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                if (mListener != null) {
                    mListener.onLocationChanged(new LatLng( amapLocation.getLatitude(),amapLocation.getLongitude()));
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }

    public interface OnLocationChangedListener {
        void onLocationChanged(LatLng latLng);
    }
}
