package com.xiaoma.mapadapter.listener;

import com.xiaoma.mapadapter.model.LatLng;

/**
 * 点击地图的回调
 * Created by minxiwen on 2017/12/12 0012.
 */

public interface OnMapClickListener {
    void onMapClick(LatLng latLng);
}
