package com.xiaoma.mapadapter.listener;


import com.xiaoma.mapadapter.model.LatLng;

/**
 * 地图可视区域变化的回调接口， 可根据需要进行扩展方法
 * Created by minxiwen on 2017/12/12 0012.
 */

public interface OnCameraChangeListener {
    void onCameraChangeFinish(LatLng latLng);
    void onCameraChangeZoomFinish(float zoom);
}
