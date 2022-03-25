package com.xiaoma.bdmap;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.baidu.mapapi.map.MapView;
import com.xiaoma.mapadapter.interfaces.IMapView;
import com.xiaoma.mapadapter.view.Map;

/**
 * Created by minxiwen on 2017/12/14 0014.
 */

public class BDMapView implements IMapView {
    private com.baidu.mapapi.map.MapView mapView;
    private Context context;

    public BDMapView(Context context) {
        this.context = context;
        mapView = new MapView(context);
    }

    @Override
    public Map getMap() {
        return new BDMap(mapView.getMap());
    }

    @Override
    public void onCreate(Bundle bundle) {
        mapView.onCreate(context, bundle);
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
