package com.xiaoma.gdmap;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.xiaoma.mapadapter.interfaces.IMapView;
import com.xiaoma.mapadapter.view.Map;

/**
 * Created by minxiwen on 2017/12/14 0014.
 */

public class GDMapView implements IMapView {
    private com.amap.api.maps.MapView mapView;

    public GDMapView(Context context) {
        mapView = new MapView(context);
    }

    @Override
    public Map getMap() {
        mapView.getMap().setMapType(AMap.MAP_TYPE_NIGHT);
        mapView.getMap().getUiSettings().setZoomControlsEnabled(false);
        mapView.getMap().getUiSettings().setScaleControlsEnabled(true);
        return new GDMap(mapView.getMap());
    }

    @Override
    public void onCreate(Bundle bundle) {
        mapView.onCreate(bundle);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        mapView.onResume();
    }

    @Override
    public void onPause() {
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
    }

    @Override
    public View getMapView() {
        return mapView;
    }
}
