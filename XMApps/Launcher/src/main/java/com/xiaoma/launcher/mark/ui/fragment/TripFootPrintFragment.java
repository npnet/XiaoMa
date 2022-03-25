package com.xiaoma.launcher.mark.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.xiaoma.image.ImageLoader;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.views.VisibilityFragment;
import com.xiaoma.launcher.mark.cluster.ClusterClickListener;
import com.xiaoma.launcher.mark.cluster.ClusterItem;
import com.xiaoma.launcher.mark.cluster.ClusterOverlay;
import com.xiaoma.launcher.mark.cluster.ClusterRender;
import com.xiaoma.launcher.mark.cluster.bean.RegionItem;
import com.xiaoma.launcher.mark.model.MarkPhotoBean;
import com.xiaoma.launcher.mark.ui.activity.MarkMainActivity;
import com.xiaoma.launcher.mark.vm.TripAlblumVM;
import com.xiaoma.mapadapter.listener.OnLocationChangeListener;
import com.xiaoma.mapadapter.manager.LocationClient;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.CameraUpdateInfo;
import com.xiaoma.mapadapter.model.LatLng;
import com.xiaoma.mapadapter.model.LocationClientOption;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.mapadapter.model.LocationMode;
import com.xiaoma.mapadapter.view.Map;
import com.xiaoma.mapadapter.view.Marker;
import com.xiaoma.mapadapter.view.TextrueMapView;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@PageDescComponent(EventConstants.PageDescribe.TripFootPrintActivityPagePathDesc)
public class TripFootPrintFragment extends VisibilityFragment implements View.OnClickListener, OnLocationChangeListener, ClusterRender, ClusterClickListener {
    private static final String TAG = "TripFootPrintFragment";
    private static final int AMAPLOCATION_ERROR_CODE = 0;
    private static final int FIRST_AMPLIFICATION_FACTOR = 18;
    private TextrueMapView mapView;
    private Map aMap;
    private CheckBox mBtnScaleUp;
    private CheckBox mBtnScaleDown;
    private Button mBtnMyLocation;
    TripAlblumVM mTripAlbumVM;
    private LocationInfo locationInfo;
    private LocationClient mlocationClient;
    private double mLatitude;
    private double mLongitude;
    public LocationClientOption mLocationOption = null;
    private List<MarkPhotoBean> mMarkList = new ArrayList<>();


    private float zoomInTrip = FIRST_AMPLIFICATION_FACTOR;
    private static MarkMainActivity mMarkMainActivity;

    private ClusterOverlay mClusterOverlay;
    private int clusterRadius = 150;

    public static TripFootPrintFragment newInstance(MarkMainActivity markMainActivity) {
        mMarkMainActivity = markMainActivity;
        return new TripFootPrintFragment();
    }

    public String getTAG() {
        return TAG;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_footprint, container, false);
        bindview(view);
        mapView.onCreate(savedInstanceState);
        KLog.d("sms","onCreateView");
        return super.onCreateWrapView(view);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        KLog.d("sms","onViewCreated");
    }

    @Override
    public void onResume() {
        super.onResume();
        KLog.d("sms","onResume");
        initView();
        initData();
        initLocation();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    private void bindview(View view) {
        mapView = view.findViewById(R.id.trip_map_view);
        mBtnScaleUp = view.findViewById(R.id.scale_up);
        mBtnScaleDown = view.findViewById(R.id.scale_down);
        mBtnMyLocation = view.findViewById(R.id.my_location);

        mBtnScaleUp.setChecked(true);
        mBtnScaleDown.setChecked(false);
        mBtnScaleUp.setOnClickListener(this);
        mBtnScaleDown.setOnClickListener(this);
        mBtnMyLocation.setOnClickListener(this);


    }

    private void initView() {
        //因为glide会添加一个空白的fragment，导致页面直接退出
        ImageLoader.with(this);
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        LocationInfo locationInfo = LocationManager.getInstance().getCurrentLocation();

        if (locationInfo == null) {
            return;
        }

        aMap.animateCamera(new CameraUpdateInfo(new LatLng(locationInfo.getLatitude(), locationInfo.getLongitude()), 5));

    }


    private void initData() {
        if (mTripAlbumVM==null){
            mTripAlbumVM = ViewModelProviders.of(this).get(TripAlblumVM.class);
            mTripAlbumVM.getTripAlbumList().observe(this, new Observer<XmResource<List<MarkPhotoBean>>>() {
                @Override
                public void onChanged(@Nullable XmResource<List<MarkPhotoBean>> listXmResource) {
                    if (listXmResource == null) {
                        return;
                    }
                    listXmResource.handle(new OnCallback<List<MarkPhotoBean>>() {
                        @Override
                        public void onSuccess(List<MarkPhotoBean> data) {
                            setLoading(data);
                        }
                    });
                }
            });
        }
        mTripAlbumVM.getAllTrip();
    }

    private void setLoading(List<MarkPhotoBean> markList) {
        //添加测试数据
        List<ClusterItem> items = new ArrayList<ClusterItem>();
        if (!ListUtils.isEmpty(markList)) {
            for (int i = 0; i < markList.size(); i++) {
                MarkPhotoBean markPhotoBean = markList.get(i);
                LatLng latLng = new LatLng(markPhotoBean.getLatitude(), markPhotoBean.getLongitude());
                RegionItem regionItem = new RegionItem(latLng,
                        markPhotoBean);
                items.add(regionItem);

            }
        }
        Collections.reverse(items);
        mClusterOverlay = new ClusterOverlay(aMap, items,
                dp2px(mContext, clusterRadius),
                mContext);
        mClusterOverlay.setClusterRenderer(TripFootPrintFragment.this);
        mClusterOverlay.setOnClusterClickListener(TripFootPrintFragment.this);

    }

    private void initLocation() {
        if (locationInfo == null) {
            locationInfo = LocationManager.getInstance().getCurrentLocation();
        }
        startLocation();
    }

    private void startLocation() {
        mlocationClient = new LocationClient(mContext);
        mLocationOption = new LocationClientOption();
        mlocationClient.setLocationListener(this);
        mLocationOption.setLocationMode(LocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
        mLocationOption.setNeedAddress(true);
        mlocationClient.setLocOption(mLocationOption);
        mlocationClient.start();
    }


    @Override
    public void onPause() {
        super.onPause();
        KLog.d("sms","onPause");
        if (aMap != null) {
            aMap = null;
        }
        if (mClusterOverlay!=null){
            mClusterOverlay.onDestroy();
            mClusterOverlay = null;
        }
        if (mTripAlbumVM!=null){
            mTripAlbumVM = null;
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        KLog.d("sms","onDestroy");
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scale_up:
                mapScale(true);
                break;
            case R.id.scale_down:
                mapScale(false);
                break;
            case R.id.my_location:
                aMap.animateCamera(new CameraUpdateInfo(new LatLng(mLatitude, mLongitude), zoomInTrip));
                break;
        }
    }

    private void mapScale(boolean isScaleUp) {
        mBtnScaleUp.setChecked(isScaleUp);
        mBtnScaleDown.setChecked(!isScaleUp);
        if (aMap == null) {
            Log.e(TAG, "mapScale -> AMap is null");
            return;
        }
        if (isScaleUp) {
            Log.i(TAG, "mapScale -> Scale up map");
            aMap.zoomIn();
        } else {
            Log.i(TAG, "mapScale -> Scale down map");
            aMap.zoomOut();
        }
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public void onLocationChange(LocationInfo locationInfo) {
        if (locationInfo != null) {
            if (locationInfo.getErrorCode() == AMAPLOCATION_ERROR_CODE) {
                this.locationInfo = locationInfo;
                mLatitude = locationInfo.getLatitude();
                mLongitude = locationInfo.getLongitude();
                mlocationClient.stop();
            } else {
                KLog.e("location Error, ErrorCode:" + locationInfo.getErrorCode() + ", ErrorInfo:" + locationInfo.getErrorInfo());
            }
        }
    }

    @Override
    public Drawable getDrawAble(int clusterNum) {

        return null;
    }


    @Override
    public void onMarkClick(Marker marker, List<ClusterItem> clusterItems) {
        mMarkMainActivity.setClusterItem(clusterItems);
        TripMarkListFragment tripMarkListFragment = TripMarkListFragment.newInstance(mMarkMainActivity);
        mMarkMainActivity.addBackStackFragment(tripMarkListFragment, tripMarkListFragment.getTAG());
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public boolean onBackPressed() {
        mapView.setBackground(getDrawAble(R.drawable.bg_common));
        return super.onBackPressed();
    }
}
