package com.xiaoma.launcher.map.ui;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.launcher.R;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.utils.log.KLog;

/**
 * Created by Thomas on 2019/1/9 0009
 * 地图 UI
 */

public class MapFragment extends BaseFragment {

    public static final String TAG = "MapFragment";
    private MapView mapView;
    private AMap aMap;

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateWrapView(inflater.inflate(R.layout.fragment_map, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
        mapView.onCreate(savedInstanceState);
        initMap();
    }

    private void bindView(View view) {
        mapView = view.findViewById(R.id.map_view);
    }

    /**
     * 初始化AMap对象
     */
    private void initMap() {
        addMarket(LocationManager.getInstance().getCurrentLocation());
        LocationManager.getInstance().addLocationListener(new LocationManager.ILocationChangedListener() {
            @Override
            public void onLocationChange(LocationInfo locationInfo) {
                KLog.d("MapFragment onLocationChange locationInfo is " + locationInfo);
                addMarket(locationInfo);
            }
        });
    }

    private void addMarket(LocationInfo locationInfo) {
        if (locationInfo == null) {
            return;
        }
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.setMapType(AMap.MAP_TYPE_NIGHT);
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationInfo.getLatitude(), locationInfo.getLongitude()), aMap.getMaxZoomLevel()));
            aMap.addMarker(createLocationMap(locationInfo));
        }
    }


    private MarkerOptions createLocationMap(LocationInfo locationInfo) {
        MarkerOptions markerOptions = new MarkerOptions();
        LatLng latLng = new LatLng(locationInfo.getLatitude(), locationInfo.getLongitude());
        markerOptions.anchor(0.5f, 0.1f)
                .position(latLng)
                .title(locationInfo.getProvince() + locationInfo.getCity())
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.drawable.check)));
        return markerOptions;
    }


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

}
