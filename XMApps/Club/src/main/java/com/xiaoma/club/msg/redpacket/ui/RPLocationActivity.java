package com.xiaoma.club.msg.redpacket.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.xiaoma.club.R;
import com.xiaoma.club.msg.chat.ui.LocationDetailActivity;
import com.xiaoma.club.msg.redpacket.controller.LocationManager;
import com.xiaoma.club.msg.redpacket.model.LocationRpInfo;
import com.xiaoma.club.msg.redpacket.vm.RPLocationVM;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.List;

public class RPLocationActivity extends BaseActivity implements View.OnClickListener, AMap.OnCameraChangeListener {
    private static final int DEFAULT_ZOOM = 15;
    public static final String EXTRA_HX_CHAT_ID = "hx_chat_id";
    public static final String EXTRA_IS_GROUP_CHAT = "is_group_chat";
    private ArrayList<Marker> markerList = new ArrayList<>();
    private MapView mMapView;
    private AMap aMap;
    private CheckBox btnZoomLarge;
    private CheckBox btnZoomSmall;
    private float zoom = DEFAULT_ZOOM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rp_location);
        initView();
        initMap(savedInstanceState);
        initData();
    }

    public static void launch(Context context, long chatId, boolean isGroupChat) {
        context.startActivity(new Intent(context, RPLocationActivity.class)
                .putExtra(EXTRA_HX_CHAT_ID, chatId)
                .putExtra(EXTRA_IS_GROUP_CHAT, isGroupChat));
    }

    private void initData() {
        Intent intent = getIntent();
        long chatId = intent.getLongExtra(EXTRA_HX_CHAT_ID, 0);
        boolean isGroupChat = intent.getBooleanExtra(EXTRA_IS_GROUP_CHAT, false);
        RPLocationVM mRpLocationVM = ViewModelProviders.of(this).get(RPLocationVM.class);
        mRpLocationVM.getLocationRpList().observe(this, new Observer<List<LocationRpInfo>>() {
            @Override
            public void onChanged(@Nullable List<LocationRpInfo> locationRpInfos) {
                if (locationRpInfos != null) {
                    for (LocationRpInfo locationRpInfo : locationRpInfos) {
                        createMarker(locationRpInfo);
                    }
                    showAllMarker();
                }
            }
        });
        if (isGroupChat) {
            mRpLocationVM.requestGroupRpList(chatId);
        } else {
            mRpLocationVM.requestSingleRpList(chatId);
        }
    }

    private void showAllMarker() {
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (int i = 0; i < markerList.size(); i++) {
            boundsBuilder.include(markerList.get(i).getPosition());
        }
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), (int) zoom));
    }

    private void initMap(Bundle savedInstanceState) {
        mMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mMapView.getMap();
            aMap.setMapType(AMap.MAP_TYPE_NIGHT);
            aMap.showBuildings(true);
            aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
                @Override
                public void onMapLoaded() {

                }
            });
            aMap.getUiSettings().setZoomControlsEnabled(false);
            aMap.getUiSettings().setScaleControlsEnabled(true);
        }
        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            // marker 对象被点击时回调的接口
            // 返回 true 则表示接口已响应事件，否则返回false
            @Override
            public boolean onMarkerClick(Marker marker) {
                LocationRpInfo rpResult = (LocationRpInfo) marker.getObject();
                KLog.d("MrMine", "onMarkerClick: " + rpResult);
                LocationDetailActivity.start(RPLocationActivity.this, rpResult.getLat(),
                        rpResult.getLon(), rpResult.getPoiName(), rpResult.getLocation());
                return false;
            }
        });
        aMap.setOnCameraChangeListener(this);
        LocationManager.getInstance().initLocation(this, new LocationManager.OnLocationChangedListener() {
            @Override
            public void onLocationChanged(LatLng latLng) {
                KLog.d("MrMine", "onLocationChanged: " + latLng.latitude + "  " + latLng.longitude);
                aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
            }
        });
    }


    private void initView() {
        getNaviBar().showBackNavi();
        mMapView = findViewById(R.id.rp_location_map);
        btnZoomLarge = findViewById(R.id.btn_zoom_large);
        btnZoomSmall = findViewById(R.id.btn_zoom_small);
        btnZoomLarge.setOnClickListener(this);
        btnZoomSmall.setOnClickListener(this);
        findViewById(R.id.btn_location).setOnClickListener(this);
    }


    private void createMarker(LocationRpInfo rpResult) {

        final Drawable markDr = getResources().getDrawable(R.drawable.rp_map_view_location_icon);
        final Bitmap markBmp = Bitmap.createBitmap(markDr.getIntrinsicWidth(), markDr.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(markBmp);
        markDr.setBounds(0, 0, markDr.getIntrinsicWidth(), markDr.getIntrinsicHeight());
        markDr.draw(canvas);
        MarkerOptions markerOptions = new MarkerOptions();
        LatLng latLng = new LatLng(rpResult.getLat(), rpResult.getLon());
        markerOptions.position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(markBmp));
        Marker marker = aMap.addMarker(markerOptions);
        marker.setObject(rpResult);
        markerList.add(marker);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View view) {
        CameraPosition cameraPosition;
        float mapZoom;
        LatLng mapTarget;
        switch (view.getId()) {
            case R.id.btn_zoom_large:
                btnZoomLarge.setChecked(true);
                btnZoomSmall.setChecked(false);
                cameraPosition = aMap.getCameraPosition();
                mapZoom = cameraPosition.zoom;
                mapTarget = cameraPosition.target;
                scaleLargeMap(mapTarget, ++mapZoom);
                zoom = mapZoom;
                break;
            case R.id.btn_zoom_small:
                btnZoomLarge.setChecked(false);
                btnZoomSmall.setChecked(true);
                cameraPosition = aMap.getCameraPosition();
                mapZoom = cameraPosition.zoom;
                mapTarget = cameraPosition.target;
                scaleLargeMap(mapTarget, --mapZoom);
                zoom = mapZoom;
                break;
            case R.id.btn_location:
                LocationManager.getInstance().startLocation();
                break;
        }
    }

    public void scaleLargeMap(LatLng nowLocation, float scaleValue) {
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(nowLocation, scaleValue));
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        zoom = cameraPosition.zoom;
        updateScaleBtn();
    }


    private void updateScaleBtn() {
        ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
            @Override
            public void run() {
                if (isDestroy())
                    return;

                AMap map = mMapView.getMap();
                if (map == null)
                    return;
                CameraPosition camera = map.getCameraPosition();
                if (camera == null)
                    return;
                final float enabled = 1f;
                final float disabled = 0.25f;

                btnZoomLarge.setEnabled(camera.zoom < map.getMaxZoomLevel());
                btnZoomLarge.setAlpha(btnZoomLarge.isEnabled() ? enabled : disabled);

                btnZoomSmall.setEnabled(camera.zoom > map.getMinZoomLevel());
                btnZoomSmall.setAlpha(btnZoomSmall.isEnabled() ? enabled : disabled);
            }
        });
    }
}
