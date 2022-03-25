package com.xiaoma.mapadapter.interfaces;


import com.xiaoma.mapadapter.listener.OnCameraChangeListener;
import com.xiaoma.mapadapter.listener.OnMapClickListener;
import com.xiaoma.mapadapter.listener.OnMapLoadedListener;
import com.xiaoma.mapadapter.listener.OnMarkerClickListener;
import com.xiaoma.mapadapter.model.CameraUpdateInfo;
import com.xiaoma.mapadapter.model.CircleOption;
import com.xiaoma.mapadapter.model.MarkerOption;
import com.xiaoma.mapadapter.view.Marker;
import com.xiaoma.mapadapter.view.UiSetting;

import java.util.List;

/**
 * 地图相关的操作抽象
 * Created by minxiwen on 2017/12/12 0012.
 */

public interface IMap {
    Marker addMarker(MarkerOption option);

    void deleteMarker(Marker marker);

    void setOnMapClickListener(OnMapClickListener listener);

    void setOnCameraChangeListener(OnCameraChangeListener listener);

    void setOnMarkerClickListener(OnMarkerClickListener listener);

    void setOnMapLoadedListener(OnMapLoadedListener listener);

    UiSetting getUiSettings();

    void animateCamera(CameraUpdateInfo cameraUpdateInfo);

    void zoomIn();

    void zoomOut();

    void addCircle(CircleOption circleOption);

    List<Marker> getAllMarkers();

    void invalidate();

    float getMaxZoomLevel();

    void setTrafficEnabled(boolean enabled);

    float getScalePerPixel();
    Object getProjection();
}
