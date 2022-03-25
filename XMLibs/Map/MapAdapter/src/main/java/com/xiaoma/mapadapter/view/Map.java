package com.xiaoma.mapadapter.view;


import com.xiaoma.mapadapter.interfaces.IMap;
import com.xiaoma.mapadapter.listener.OnCameraChangeListener;
import com.xiaoma.mapadapter.listener.OnMapClickListener;
import com.xiaoma.mapadapter.listener.OnMapLoadedListener;
import com.xiaoma.mapadapter.listener.OnMarkerClickListener;
import com.xiaoma.mapadapter.model.CameraUpdateInfo;
import com.xiaoma.mapadapter.model.CircleOption;
import com.xiaoma.mapadapter.model.MarkerOption;

import java.util.List;

/**
 * 地图的包装类型, 具体的操作由具体子类来完成
 * Created by minxiwen on 2017/12/11 0011.
 */

public class Map implements IMap {

    @Override
    public Marker addMarker(MarkerOption option) {
        return null;
    }

    @Override
    public void deleteMarker(Marker marker) {

    }

    @Override
    public void setOnMapClickListener(OnMapClickListener listener) {

    }

    @Override
    public void setOnCameraChangeListener(OnCameraChangeListener listener) {

    }

    @Override
    public void setOnMarkerClickListener(OnMarkerClickListener listener) {

    }

    @Override
    public UiSetting getUiSettings() {
        return null;
    }

    @Override
    public void animateCamera(CameraUpdateInfo cameraUpdateInfo) {

    }

    @Override
    public void zoomIn() {

    }

    @Override
    public void zoomOut() {

    }

    @Override
    public void addCircle(CircleOption circleOption) {

    }

    @Override
    public List<Marker> getAllMarkers() {
        return null;
    }

    @Override
    public void setOnMapLoadedListener(OnMapLoadedListener listener) {

    }

    @Override
    public void invalidate() {

    }

    @Override
    public float getMaxZoomLevel() {
        return 0;
    }

    @Override
    public void setTrafficEnabled(boolean enabled) {

    }

    @Override
    public float getScalePerPixel() {
        return 0;
    }

    public Object getProjection() {

        return new Object();
    }

}
