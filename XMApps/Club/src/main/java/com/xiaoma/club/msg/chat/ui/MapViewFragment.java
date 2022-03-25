package com.xiaoma.club.msg.chat.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

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
import com.amap.api.services.core.PoiItem;
import com.xiaoma.club.R;
import com.xiaoma.club.common.util.LocationClientHelper;
import com.xiaoma.club.common.util.LogUtil;
import com.xiaoma.club.msg.chat.vm.PoiSearchVM;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.thread.ThreadDispatcher;

import java.util.Objects;

import static com.xiaoma.club.common.util.LogUtil.logI;


/**
 * Created by LKF on 2018/10/11 0011.
 */
public class MapViewFragment extends BaseFragment implements View.OnClickListener {
    public static final String TAG = "MapViewFragment";
    private static final int DEFAULT_ZOOM = 18;
    private MapView mMapView;
    private CheckBox mBtnScaleUp;
    private CheckBox mBtnScaleDown;
    private Button mBtnMyLocation;
    private PoiSearchVM mViewModel;
    private Location mMyLocation;
    private final LocationClientHelper mLocationClientHelper = new LocationClientHelper();
    private Marker mMarker;
    private boolean mUpdateLocationOnCameraChangeFinish = false;
    private float zoom = DEFAULT_ZOOM;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fmt_map_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = view.findViewById(R.id.map_view);
        mBtnScaleUp = view.findViewById(R.id.btn_scale_up);
        mBtnScaleDown = view.findViewById(R.id.btn_scale_down);
        mBtnMyLocation = view.findViewById(R.id.btn_my_location);

        mBtnScaleUp.setOnClickListener(this);
        mBtnScaleDown.setOnClickListener(this);
        mBtnScaleUp.setChecked(true);
        mBtnScaleDown.setChecked(false);
        mBtnMyLocation.setOnClickListener(this);
        //地图初始化
        mMapView.onCreate(savedInstanceState);
        final AMap map = mMapView.getMap();
        map.setMapType(AMap.MAP_TYPE_NIGHT);
        map.showBuildings(true);
        // 监听当前位置
        map.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                logI(TAG, "onMyLocationChange -> location: " + location);
                if (isDestroy())
                    return;
                // 首次定位,将定位点设置为当前选中点
                if (mMyLocation == null) {
                    mViewModel.getLocationSearch().setValue(new PoiSearchVM.LocationSearch("", location));
                }
                mMyLocation = location;
            }
        });
        // 监听地图触摸
        map.setOnMapTouchListener(new AMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent event) {
                int action = event.getAction();
                logI(TAG, "onTouch( event: %s )", MotionEvent.actionToString(action));
                switch (action) {
                    case MotionEvent.ACTION_UP:
                        mUpdateLocationOnCameraChangeFinish = true;
                        break;
                    default:
                        mUpdateLocationOnCameraChangeFinish = false;
                        break;
                }
            }
        });
        // 监听地图滑动
        map.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {
                logI(TAG, "onCameraChange( position: %s )", position);
                mapUpdateMarker(position.target);
            }

            @Override
            public void onCameraChangeFinish(CameraPosition position) {
                logI(TAG, "onCameraChangeFinish( position: %s )", position);
                mapUpdateMarker(position.target);
                if (mUpdateLocationOnCameraChangeFinish) {
                    LatLng latLng = position.target;
                    if (latLng != null) {
                        AMapLocation location = new AMapLocation((String) null);
                        location.reset();
                        location.setLatitude(latLng.latitude);
                        location.setLongitude(latLng.longitude);
                        PoiSearchVM.LocationSearch locationSearch = new PoiSearchVM.LocationSearch("", location);
                        mViewModel.getLocationSearch().setValue(locationSearch);
                    }
                }
                mUpdateLocationOnCameraChangeFinish = false;
                zoom = position.zoom;
                updateScaleBtn();
            }
        });

        /*final MyLocationStyle locationStyle = new MyLocationStyle();
        locationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);// 连续定位、蓝点不会移动到地图中心点，并且蓝点会跟随设备移动
        locationStyle.interval(LOCATING_INTERVAL);
        locationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_icon));
        map.setMyLocationStyle(locationStyle);*/
        // 设置地图UI部件
        final UiSettings mapUiSettings = map.getUiSettings();
        mapUiSettings.setCompassEnabled(false);
        mapUiSettings.setMyLocationButtonEnabled(false);
        mapUiSettings.setZoomControlsEnabled(false);
        mapUiSettings.setScaleControlsEnabled(true);
        mViewModel = ViewModelProviders.of((FragmentActivity) view.getContext()).get(PoiSearchVM.class);

        Location location = null;
        PoiSearchVM.LocationSearch locationSearch;
        if ((locationSearch = mViewModel.getLocationSearch().getValue()) != null) {
            location = locationSearch.getLocation();
        }
        if (location != null) {
            updateByLocation(location);
        }

        mViewModel.getSelectPoiIndex().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer index) {
                PoiItem poiItem = null;
                try {
                    final int idx = index != null ? index : 0;
                    PagedList<PoiItem> poiItemList;
                    if ((poiItemList = mViewModel.getPoiItemPageList().getValue()) != null) {
                        poiItem = poiItemList.get(idx);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (poiItem != null) {
                    final AMapLocation location = new AMapLocation((String) null);
                    final LatLonPoint point = poiItem.getLatLonPoint();
                    if (point != null) {
                        location.setLatitude(point.getLatitude());
                        location.setLongitude(point.getLongitude());
                    }
                    location.setCityCode(poiItem.getCityCode());
                    location.setCity(poiItem.getCityName());
                    location.setPoiName(poiItem.getAdName());
                    location.setAddress(poiItem.getSnippet());
                    updateByLocation(location);
                }
            }
        });

        // 监听定位数据
        mLocationClientHelper.create(getContext(), false, new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation location) {
                if (isDestroy()) {
                    LogUtil.logE(TAG, "onLocationChanged -> [ location: %s ] Fragment has destroy", location);
                    return;
                }
                if (location == null) {
                    LogUtil.logE(TAG, "onLocationChanged -> [ location: null ]");
                    return;
                }
                // 第一次定位成功设置为当前选中位置
                if (mMyLocation == null) {
                    logI(TAG, "onLocationChanged -> [ location: %s ] #MyLocation is null,  do #SelectedLocation init", location);
                    mViewModel.getLocationSearch().postValue(new PoiSearchVM.LocationSearch("", location));
                } else {
                    logI(TAG, "onLocationChanged -> [ location: %s ] Update curr location", location);
                }
                mMyLocation = location;
            }
        });
        mLocationClientHelper.startLocation();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMapView.onDestroy();
        mLocationClientHelper.stopLocation();
        mLocationClientHelper.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    private void updateByLocation(Location location) {
        if (location != null && mMapView != null && mMapView.getMap() != null) {
            AMap map = mMapView.getMap();
            LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            if (Objects.equals(newLatLng, map.getCameraPosition().target))
                return;
            mapUpdateMarker(newLatLng);// 更新Mark点
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(newLatLng, zoom));
        }
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
            case R.id.btn_my_location:
                mapMyLocation();
                break;
        }
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
            logI(TAG, "mapScale -> Scale up map");
            map.animateCamera(CameraUpdateFactory.zoomIn());
        } else {
            logI(TAG, "mapScale -> Scale down map");
            map.animateCamera(CameraUpdateFactory.zoomOut());
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

    private void mapMyLocation() {
        final Location myLocation = mMyLocation;
        if (myLocation == null) {
            LogUtil.logE(TAG, "mapMyLocation -> myLocation is null");
            return;
        }
        logI(TAG, "mapMyLocation -> myLocation: %s", myLocation);
        mViewModel.getLocationSearch().setValue(new PoiSearchVM.LocationSearch("", myLocation));
    }

    private void mapUpdateMarker(LatLng latLng) {
        if (latLng == null) {
            LogUtil.logE(TAG, "mapUpdateMarker -> LatLng is null");
            return;
        }
        final AMap map = mMapView.getMap();
        if (map == null) {
            LogUtil.logE(TAG, "mapUpdateMarker -> AMap is null");
            return;
        }
        if (mMarker != null) {
            mMarker.setPosition(latLng);
            return;
        }
        logI(TAG, "mapUpdateMarker -> addMarker");
        final Drawable markDr = getResources().getDrawable(R.drawable.map_view_curr_location_icon);
        final Bitmap markBmp = Bitmap.createBitmap(markDr.getIntrinsicWidth(), markDr.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(markBmp);
        markDr.setBounds(0, 0, markDr.getIntrinsicWidth(), markDr.getIntrinsicHeight());
        markDr.draw(canvas);
        mMarker = map.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(markBmp)));
    }
}
