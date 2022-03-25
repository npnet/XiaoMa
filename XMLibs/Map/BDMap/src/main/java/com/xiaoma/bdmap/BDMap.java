package com.xiaoma.bdmap;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MarkerOptions;
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
 * Created by minxiwen on 2017/12/14 0014.
 */

public class BDMap extends Map {
    private BaiduMap baiduMap;
    private List<Marker> markerList;

    public BDMap(BaiduMap baiduMap) {
        this.baiduMap = baiduMap;
    }

    @Override
    public Marker addMarker(MarkerOption option) {
        Marker marker = new BDMarker((com.baidu.mapapi.map.Marker) baiduMap.addOverlay((MarkerOptions) MapConverter.getInstance().convertMarkerOption(option)));
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
    public void setOnMapClickListener(final OnMapClickListener listener) {
        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(com.baidu.mapapi.model.LatLng latLng) {
                listener.onMapClick(MapConverter.getInstance().convertLatLng(latLng));
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
    }

    @Override
    public void setOnCameraChangeListener(final OnCameraChangeListener listener) {
        baiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                listener.onCameraChangeFinish(MapConverter.getInstance().convertLatLng(mapStatus.target));
            }
        });
    }

    @Override
    public void setOnMarkerClickListener(final OnMarkerClickListener listener) {
        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(com.baidu.mapapi.map.Marker marker) {
                listener.onMarkerClick(MapConverter.getInstance().convertMarker(marker));
                return false;
            }
        });
    }

    @Override
    public UiSetting getUiSettings() {
        return new BDUiSetting(baiduMap.getUiSettings());
    }

    @Override
    public void animateCamera(CameraUpdateInfo cameraUpdateInfo) {
        baiduMap.animateMapStatus((MapStatusUpdate) MapConverter.getInstance().convertCameraUpdateInfo(cameraUpdateInfo));
    }

    @Override
    public void addCircle(CircleOption circleOption) {
        baiduMap.addOverlay((CircleOptions) MapConverter.getInstance().convertCircleOption(circleOption));
    }

    /**
     * 不要调用此方法,返回结果总是null
     *
     * @return 返回null
     */
    @Deprecated
    @Override
    public List<Marker> getAllMarkers() {
        return markerList;
    }

    @Override
    public void setOnMapLoadedListener(final OnMapLoadedListener listener) {
        baiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                listener.onMapLoaded();
            }
        });
    }

    @Override
    public void invalidate() {

    }

    @Override
    public float getMaxZoomLevel() {
        return baiduMap.getMaxZoomLevel();
    }

    @Override
    public void setTrafficEnabled(boolean enabled) {
        baiduMap.setTrafficEnabled(enabled);
    }
}
