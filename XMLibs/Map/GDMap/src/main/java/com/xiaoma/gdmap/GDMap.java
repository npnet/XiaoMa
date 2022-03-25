package com.xiaoma.gdmap;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.xiaoma.mapadapter.convert.MapConverter;
import com.xiaoma.mapadapter.listener.OnCameraChangeListener;
import com.xiaoma.mapadapter.listener.OnMapClickListener;
import com.xiaoma.mapadapter.listener.OnMapLoadedListener;
import com.xiaoma.mapadapter.listener.OnMarkerClickListener;
import com.xiaoma.mapadapter.model.CameraUpdateInfo;
import com.xiaoma.mapadapter.model.CircleOption;
import com.xiaoma.mapadapter.model.MarkerOption;
import com.xiaoma.mapadapter.view.Map;
import com.xiaoma.mapadapter.view.Marker;
import com.xiaoma.mapadapter.view.UiSetting;
import com.xiaoma.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by minxiwen on 2017/12/12 0012.
 */

public class GDMap extends Map {
    private AMap aMap;
    private List<Marker> markerList;

    public GDMap(AMap aMap) {
        this.aMap = aMap;
    }

    @Override
    public Marker addMarker(MarkerOption option) {
        GDMarker marker = new GDMarker(aMap.addMarker((MarkerOptions) MapConverter.getInstance().convertMarkerOption(option)));
        if (markerList == null) {
            markerList = new ArrayList<>();
        }
        markerList.add(marker);
        return marker;
    }

    @Override
    public void deleteMarker(Marker marker) {
        if (!ListUtils.isEmpty(markerList) && markerList.contains(marker)) {
            markerList.remove(marker);
            marker.remove();
            marker.destroy();
        }
    }

    @Override
    public void zoomIn(){
        aMap.animateCamera(CameraUpdateFactory.zoomIn());
    }

    @Override
    public void zoomOut(){
        aMap.animateCamera(CameraUpdateFactory.zoomOut());
    }

    @Override
    public void setOnMapClickListener(final OnMapClickListener listener) {
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                listener.onMapClick(MapConverter.getInstance().convertLatLng(latLng));
            }
        });
    }

    @Override
    public void setOnCameraChangeListener(final OnCameraChangeListener listener) {
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                listener.onCameraChangeFinish(MapConverter.getInstance().convertLatLng(cameraPosition.target));
                listener.onCameraChangeZoomFinish(cameraPosition.zoom);
            }
        });
    }

    @Override
    public UiSetting getUiSettings() {
        UiSettings settings = aMap.getUiSettings();
        return new GDUiSetting(settings);
    }

    @Override
    public void animateCamera(CameraUpdateInfo cameraUpdateInfo) {
        aMap.animateCamera((CameraUpdate) MapConverter.getInstance().convertCameraUpdateInfo(cameraUpdateInfo));
    }

    @Override
    public void addCircle(CircleOption circleOption) {
        aMap.addCircle((CircleOptions) MapConverter.getInstance().convertCircleOption(circleOption));
    }

    @Override
    public void setOnMarkerClickListener(final OnMarkerClickListener listener) {
        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(com.amap.api.maps.model.Marker marker) {
                listener.onMarkerClick(MapConverter.getInstance().convertMarker(marker));
                return false;
            }
        });
    }

    @Override
    public List<Marker> getAllMarkers() {
//        List<com.amap.api.maps2d.model.Marker> markerList = aMap.getMapScreenMarkers();
//        List<Marker> desMarkerList = new ArrayList<>();
//        for (com.amap.api.maps2d.model.Marker marker : markerList) {
//            desMarkerList.add(MapConverter.getInstance().convertMarker(marker));
//        }
//        return desMarkerList;
        return markerList;
    }

    @Override
    public void setOnMapLoadedListener(final OnMapLoadedListener listener) {
        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                listener.onMapLoaded();
            }
        });
    }

    @Override
    public void invalidate() {
//        aMap.invalidate();
    }

    @Override
    public float getMaxZoomLevel() {
        return aMap.getMaxZoomLevel();
    }

    @Override
    public void setTrafficEnabled(boolean enabled) {
        aMap.setTrafficEnabled(enabled);
    }


    public float getScalePerPixel() {
        try {
            return this.aMap.getScalePerPixel();
        } catch (Throwable var2) {
            var2.printStackTrace();
            return 0.0F;
        }
    }
    public Object getProjection() {
        Object projection = aMap.getProjection();
        return projection;
    }

}
