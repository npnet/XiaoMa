package com.xiaoma.club.msg.chat.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DistanceItem;
import com.amap.api.services.route.DistanceResult;
import com.amap.api.services.route.DistanceSearch;
import com.mapbar.xiaoma.manager.XmMapNaviManager;
import com.xiaoma.club.R;
import com.xiaoma.club.common.util.LocationClientHelper;
import com.xiaoma.club.common.util.LogUtil;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.thread.Priority;
import com.xiaoma.thread.SeriesAsyncWorker;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.thread.Work;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

/**
 * Created by LKF on 2019-1-7 0007.
 */
public class LocationDetailActivity extends BaseActivity implements View.OnClickListener {
    public static final String EXTRA_LAT = "latitude";
    public static final String EXTRA_LON = "longitude";
    public static final String EXTRA_POI_NAME = "poiName";
    public static final String EXTRA_POI_ADDRESS = "poiAddress";
    private static final int DEFAULT__MAP_SCALE = 18;
    private static final String TAG = "LocationDetailActivity";

    private MapView mMapView;
    private CheckBox mBtnScaleUp;
    private CheckBox mBtnScaleDown;
    private View mBtnExit;
    private Button mBtnMyLocation;

    private PoiInfoWindow mPoiInfoWindow;
    private final LocationClientHelper mLocationClientHelper = new LocationClientHelper();
    private AMapLocation mMyLocation;
    private float zoom = DEFAULT__MAP_SCALE;

    public static void start(Context context, double lat, double lon, String poiName, String poiAddress) {
        final Intent intent = new Intent(context, LocationDetailActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra(EXTRA_LAT, lat)
                .putExtra(EXTRA_LON, lon)
                .putExtra(EXTRA_POI_NAME, poiName)
                .putExtra(EXTRA_POI_ADDRESS, poiAddress);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_detail);

        if (getNaviBar() != null)
            getNaviBar().hideNavi();

        mMapView = findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);

        mBtnScaleUp = findViewById(R.id.btn_scale_up);
        mBtnScaleUp.setOnClickListener(this);

        mBtnScaleDown = findViewById(R.id.btn_scale_down);
        mBtnScaleDown.setOnClickListener(this);

        mBtnExit = findViewById(R.id.btn_exit);
        mBtnExit.setOnClickListener(this);

        mBtnMyLocation = findViewById(R.id.btn_my_location);
        mBtnMyLocation.setOnClickListener(this);

        final Intent intent = getIntent();
        final double lat = intent.getDoubleExtra(EXTRA_LAT, 0f);
        final double lon = intent.getDoubleExtra(EXTRA_LON, 0f);
        final String poiName = intent.getStringExtra(EXTRA_POI_NAME);
        final String poiAddress = intent.getStringExtra(EXTRA_POI_ADDRESS);

        final AMap map = mMapView.getMap();
        map.setMapType(AMap.MAP_TYPE_NIGHT);
        map.showBuildings(true);
        // 隐藏按钮
        final UiSettings mapUiSettings = map.getUiSettings();
        mapUiSettings.setZoomControlsEnabled(false);
        mapUiSettings.setScaleControlsEnabled(true);
        final LatLng latLng = new LatLng(lat, lon);
        map.setInfoWindowAdapter(mPoiInfoWindow = new PoiInfoWindow(latLng, poiName, poiAddress));
        // 添加图钉
        final Drawable markDr = getResources().getDrawable(R.drawable.map_view_curr_location_icon);
        final Bitmap markBmp = Bitmap.createBitmap(markDr.getIntrinsicWidth(), markDr.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(markBmp);
        markDr.setBounds(0, 0, markDr.getIntrinsicWidth(), markDr.getIntrinsicHeight());
        markDr.draw(canvas);
        final MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(markBmp))
                .snippet("snippet")
                .title("title")
                .visible(true);
        final Marker marker = map.addMarker(markerOptions);
        marker.showInfoWindow();
        // 定位到目标位置
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
        // 向下移动一点,令Poi浮窗显示全
        map.moveCamera(CameraUpdateFactory.scrollBy(0, -150));

        // 获取自己的定位
        mLocationClientHelper.create(this, true, new AMapLocationListener() {
            @Override
            public void onLocationChanged(final AMapLocation location) {
                if (isDestroy()) {
                    LogUtil.logI(TAG, String.format("onLocationChanged( location: %s ) Activity isDestroy, return", location));
                    return;
                }
                LogUtil.logI(TAG, String.format("onLocationChanged( location: %s )", location));
                mMyLocation = location;
                mPoiInfoWindow.updateDistance();
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), zoom));
                // 向下移动一点,令Poi浮窗显示全
                map.moveCamera(CameraUpdateFactory.scrollBy(0, -150));
            }
        });
        map.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                zoom = cameraPosition.zoom;
                updateScaleBtn();
            }
        });
        mLocationClientHelper.startLocation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mLocationClientHelper.stopLocation();
        mLocationClientHelper.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scale_up:
                mapScale(true);
                break;
            case R.id.btn_scale_down:
                mapScale(false);
                break;
            case R.id.btn_exit:
                finish();
                break;
            case R.id.btn_my_location:
                locationPosition();
                break;
        }
    }

    public void locationPosition() {
        if (mMyLocation == null) {
            LogUtil.logE(TAG, "locationPosition -> mMyLocation is null");
            return;
        }
        mMapView.getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mMyLocation.getLatitude(),
                mMyLocation.getLongitude()), zoom));
        // 向下移动一点,令Poi浮窗显示全
        mMapView.getMap().moveCamera(CameraUpdateFactory.scrollBy(0, -150));
    }

    private void mapScale(boolean isScaleUp) {
        mBtnScaleUp.setChecked(isScaleUp);
        mBtnScaleDown.setChecked(!isScaleUp);
        final AMap map = mMapView.getMap();
        if (map == null) {
            LogUtil.logE(TAG, "mapScale -> AMap is null");
            return;
        }
        if (isScaleUp) {
            LogUtil.logI(TAG, "mapScale -> Scale up map");
            map.animateCamera(CameraUpdateFactory.zoomIn());
        } else {
            LogUtil.logI(TAG, "mapScale -> Scale down map");
            map.animateCamera(CameraUpdateFactory.zoomOut());
        }
    }

    private class PoiInfoWindow implements AMap.InfoWindowAdapter {
        private LatLng latLng;
        private String poiName;
        private String poiAddress;

        private View mView;

        PoiInfoWindow(final LatLng latLng, final String poiName, final String poiAddress) {
            this.latLng = latLng;
            this.poiName = poiName;
            this.poiAddress = poiAddress;
        }

        @Override
        public View getInfoWindow(final Marker marker) {
            if (mView == null) {
                mView = View.inflate(LocationDetailActivity.this, R.layout.location_detail_overlay, null);
            }
            final View poiView = mView;

            final TextView tvPoiName = poiView.findViewById(R.id.tv_poi_name);
            tvPoiName.setText(poiName);

            final TextView tvPoiAddress = poiView.findViewById(R.id.tv_poi_address);
            tvPoiAddress.setText(poiAddress);

            updateDistance();// 计算驾车导航距离
            poiView.findViewById(R.id.btn_navi).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    XmMapNaviManager.getInstance().startNaviToPoi(poiName, poiAddress, latLng.longitude, latLng.latitude);
                }
            });
            return poiView;
        }

        @Override
        public View getInfoContents(final Marker marker) {
            return null;
        }

        void updateDistance() {
            final TextView tvPoiDistance = mView.findViewById(R.id.tv_poi_distance);
            final AMapLocation myLocation = mMyLocation;
            if (myLocation == null) {
                tvPoiDistance.setText("");
                return;
            }
            SeriesAsyncWorker.create().next(new Work(Priority.HIGH) {
                @Override
                public void doWork(final Object lastResult) {
                    if (isDestroy())
                        return;
                    DistanceResult result = null;
                    try {
                        final LatLonPoint start = new LatLonPoint(myLocation.getLatitude(), myLocation.getLongitude());
                        final LatLonPoint dest = new LatLonPoint(latLng.latitude, latLng.longitude);
                        final DistanceSearch.DistanceQuery distanceQuery = new DistanceSearch.DistanceQuery();
                        distanceQuery.setOrigins(Collections.singletonList(start));
                        distanceQuery.setDestination(dest);
                        distanceQuery.setType(DistanceSearch.TYPE_DISTANCE);
                        final DistanceSearch distanceSearch = new DistanceSearch(LocationDetailActivity.this);
                        result = distanceSearch.calculateRouteDistance(distanceQuery);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    doNext(result);
                }
            }).next(new Work<DistanceResult>() {
                @Override
                public void doWork(final DistanceResult result) {
                    if (isDestroy())
                        return;
                    List<DistanceItem> distanceItems;
                    if (result != null && (distanceItems = result.getDistanceResults()) != null
                            && !distanceItems.isEmpty()) {
                        // 返回的距离单位:米
                        float distance = distanceItems.get(0).getDistance();
                        @StringRes int display = R.string.navi_distance_m_format;
                        if (distance >= 1000f) {
                            // 超过1km的转换为公里
                            distance /= 1000f;
                            display = R.string.navi_distance_km_format;
                        }
                        final DecimalFormat df = new DecimalFormat("0.##");
                        tvPoiDistance.setText(getString(display, df.format(distance)));
                    }
                }
            }).start();
        }
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

                mBtnScaleUp.setEnabled(camera.zoom < map.getMaxZoomLevel());
                mBtnScaleUp.setAlpha(mBtnScaleUp.isEnabled() ? enabled : disabled);

                mBtnScaleDown.setEnabled(camera.zoom > map.getMinZoomLevel());
                mBtnScaleDown.setAlpha(mBtnScaleDown.isEnabled() ? enabled : disabled);
            }
        });
    }
}
