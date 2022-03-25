package com.xiaoma.bdmap;

import android.os.Bundle;

import com.xiaoma.mapadapter.convert.MapConverter;
import com.xiaoma.mapadapter.model.LatLng;
import com.xiaoma.mapadapter.view.Marker;


/**
 * Created by minxiwen on 2017/12/14 0014.
 */

public class BDMarker extends Marker {
    private com.baidu.mapapi.map.Marker marker;

    public BDMarker(com.baidu.mapapi.map.Marker marker) {
        this.marker = marker;
    }

    @Override
    public void remove() {
        marker.remove();
    }

    @Override
    public void destroy() {
        marker = null;
    }

    @Override
    public void setObject(Object object) {
        marker.setExtraInfo((Bundle) object);
    }

    @Override
    public Bundle getObject() {
        return marker.getExtraInfo();
    }

    @Override
    public void setPosition(LatLng latLng) {
        com.baidu.mapapi.model.LatLng latLng1 = new com.baidu.mapapi.model.LatLng(latLng.getLatitude(), latLng.getLongitude());
        marker.setPosition(latLng1);
    }

    @Override
    public LatLng getPosition() {
        return MapConverter.getInstance().convertLatLng(marker.getPosition());
    }
}
