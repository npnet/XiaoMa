package com.xiaoma.mapadapter.factory;


import com.xiaoma.mapadapter.model.CameraUpdateInfo;
import com.xiaoma.mapadapter.model.LatLng;

/**
 * 可视区域变化的工厂类
 * Created by minxiwen on 2017/12/13 0013.
 */

public class CameraUpdateFactory {

    public static CameraUpdateInfo newLatLngZoom(LatLng latLng, float factor) {
        return new CameraUpdateInfo(latLng, factor);
    }

}
