package com.xiaoma.launcher.travel.car.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.discretescrollview.DSVOrientation;
import com.discretescrollview.DiscreteScrollView;
import com.discretescrollview.transform.ScaleTransformer;
import com.xiaoma.ad.utils.GsonUtil;
import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.map.manager.MapManager;
import com.xiaoma.launcher.travel.car.adapter.OilParkAdapter;
import com.xiaoma.launcher.travel.car.vm.OilParkVM;
import com.xiaoma.launcher.travel.itemevent.OilParkTrackerBean;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.mapadapter.model.SearchAddressInfo;
import com.xiaoma.mapadapter.ui.MapActivity;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.trip.parking.response.ParkInfoBean;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author taojin
 * @date 2019/5/8
 */
public class NearByOilParkActivity extends BaseActivity implements DiscreteScrollView.OnItemChangedListener, DiscreteScrollView.ScrollStateChangeListener<RecyclerView.ViewHolder> {

    private static final int DISTANCE = 1000;
    private static final String EXTRA_POI_TYPE = "poi_type";
    private static final String EXTRA_LOCATION_DATA = "location";
    private List<ParkInfoBean> mList = new ArrayList();
    private DiscreteScrollView mItemOilParks;
    private OilParkVM mOilParkVM;
    private OilParkAdapter mOilParkAdapter;
    private TextView mOilParkName;
    private TextView mOilParkInfo;
    private Button mNavigationBtn;
    private RelativeLayout mNotdataView;
    private TextView mTvTips;
    private TextView oilParkTitle;
    private LinearLayout oilParkLayout;
    private String mPoiType;
    private Boolean mIsRecommend = false;
    private LocationInfo mLocationInfo;
    private int REQUEST_CODE = 200;
    private String type;
    private final String OIL = "加油站";
    private final String PARK = "停车场";
    private SearchAddressInfo mAddressInfo;
    private int pageNum = 1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oilpark_activity);
        bindView();
        initView();
        initData();
    }

    private void bindView() {
        oilParkTitle = findViewById(R.id.oilpark_title);
        mNotdataView = findViewById(R.id.notdata_view);
        mTvTips = findViewById(R.id.tv_oilpark_tip);
        oilParkLayout = findViewById(R.id.oilpark_layout);
        mItemOilParks = findViewById(R.id.oilpark_rv);
        mOilParkName = findViewById(R.id.oilpark_name);
        mOilParkInfo = findViewById(R.id.oilpark_info);
        mNavigationBtn = findViewById(R.id.oilpark_navigation);
        getNaviBar().getMiddleView().setImageDrawable(getResources().getDrawable(R.drawable.icon_location));
    }


    private void initView() {

        getNaviBar().getMiddleView().setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick({EventConstants.NormalClick.DELICIOUS_RENEW_POSITION})
            public void onClick(View v) {
                startActivityForResult(new Intent(NearByOilParkActivity.this, MapActivity.class), REQUEST_CODE);
            }
        });

        mIsRecommend = getIntent().getBooleanExtra(LauncherConstants.TravelConstants.OIL_PARK_DATA_RECOMMEND_TYPE, false);
        mPoiType = getIntent().getStringExtra(EXTRA_POI_TYPE);
        String str=getString(R.string.gas_stations);
        if (OIL.equals(mPoiType)) {
            str=getString(R.string.gas_stations);
        }else{
            str=getString(R.string.parking_lots);
        }
        if (mIsRecommend) {
            oilParkTitle.setText(String.format(getString(R.string.recommend_oilpark), str));
            mTvTips.setText(String.format(getString(R.string.no_recommend), str));
        } else {

            oilParkTitle.setText(String.format(getString(R.string.nearby_oilpark), str));
            mTvTips.setText(String.format(getString(R.string.no_nearly), str));
        }

        mOilParkAdapter = new OilParkAdapter(mPoiType);
        mItemOilParks.setOrientation(DSVOrientation.HORIZONTAL);

        mItemOilParks.addOnItemChangedListener(this);
        mItemOilParks.addScrollStateChangeListener(this);
        mItemOilParks.setSlideOnFling(true);
        mItemOilParks.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());
        mOilParkAdapter.setEnableLoadMore(true);
//        mOilParkAdapter.setLoadMoreView(new TravelLoadMoreView());
        mOilParkAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
//                mOilParkAdapter.loadMoreEnd();
                loadMore();
            }
        }, mItemOilParks);
        mOilParkAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mItemOilParks.smoothScrollToPosition(position);
            }
        });
        mOilParkAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

            }
        });
        mItemOilParks.setAdapter(mOilParkAdapter);
    }


    private void initData() {
        type = getIntent().getStringExtra("type");
        mLocationInfo = LocationManager.getInstance().getCurrentLocation();
        mOilParkVM = ViewModelProviders.of(this).get(OilParkVM.class);

        mOilParkVM.getOilList().observe(this, new Observer<XmResource<List<ParkInfoBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<ParkInfoBean>> listXmResource) {
                if (listXmResource == null) {
                    return;
                }
                listXmResource.handle(new OnCallback<List<ParkInfoBean>>() {
                    @Override
                    public void onSuccess(List<ParkInfoBean> data) {
                        if (!ListUtils.isEmpty(data)) {
                            oilParkLayout.setVisibility(View.VISIBLE);
                            mItemOilParks.setVisibility(View.VISIBLE);
                            mList.addAll(data);
                            mOilParkAdapter.addData(data);

                            if (data.size() >= 4) {
                                mItemOilParks.scrollToPosition(2);
                            } else if (data.size() == 3) {
                                mItemOilParks.scrollToPosition(1);
                            } else {
                                mItemOilParks.scrollToPosition(data.size() - 1);
                            }
                        } else {
                            mOilParkVM.getOilList().setValue(XmResource.<List<ParkInfoBean>>error(getApplication().getString(R.string.not_data)));
                        }

//                        mOilParkAdapter.loadMoreEnd();
                    }

                    @Override
                    public void onFailure(String msg) {
                        super.onFailure(msg);
                        if (getProgressDialog().isShowing()) {
                            dismissProgress();
                        }
                    }
                });
            }
        });

        mOilParkVM.getParkList().observe(this, new Observer<XmResource<List<ParkInfoBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<ParkInfoBean>> listXmResource) {
                if (listXmResource == null) {
                    return;
                }
                listXmResource.handle(new OnCallback<List<ParkInfoBean>>() {
                    @Override
                    public void onSuccess(List<ParkInfoBean> data) {
                        if (!ListUtils.isEmpty(data)) {
                            oilParkLayout.setVisibility(View.VISIBLE);
                            mItemOilParks.setVisibility(View.VISIBLE);
                            mList.addAll(data);
                            mOilParkAdapter.addData(data);

                            if (data.size() >= 4) {
                                mItemOilParks.scrollToPosition(2);
                            } else if (data.size() == 3) {
                                mItemOilParks.scrollToPosition(1);
                            } else {
                                mItemOilParks.scrollToPosition(data.size() - 1);
                            }
                        } else {
                            mOilParkVM.getParkList().setValue(XmResource.<List<ParkInfoBean>>error(getApplication().getString(R.string.not_data)));
                        }

                    }

                    @Override
                    public void onFailure(String msg) {
                        super.onFailure(msg);
                        if (getProgressDialog().isShowing()) {
                            dismissProgress();
                        }
                    }

                });
            }
        });
        if (mLocationInfo != null) {
            if (mIsRecommend) {
                mOilParkVM.queryNearByOilPark(mLocationInfo.getLongitude() + "," + mLocationInfo.getLatitude(), mLocationInfo.getCity(), mPoiType, type, pageNum);
            } else {

                mOilParkVM.queryNearByOilPark(mLocationInfo.getLongitude() + "," + mLocationInfo.getLatitude(), mLocationInfo.getCity(), mPoiType, type, pageNum);
            }

        } else {
            showToast(R.string.location_error_hotel);
            finish();
            return;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            oilParkLayout.setVisibility(View.INVISIBLE);
            mItemOilParks.setVisibility(View.INVISIBLE);
            pageNum = 1;
            mList.clear();
            mOilParkAdapter.getData().clear();
            mAddressInfo = data.getParcelableExtra(EXTRA_LOCATION_DATA);
            if (mAddressInfo != null && mAddressInfo.latLonPoint != null) {
                mOilParkVM.queryNearByOilPark(mAddressInfo.latLonPoint.getLatitude() + "," + mAddressInfo.latLonPoint.getLongitude(), mAddressInfo.city, mPoiType, type, pageNum);
            }
        }
    }


    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
        if (ListUtils.isEmpty(mList)) {
            return;
        }
        if (adapterPosition < mList.size()) {
            onItemChanged(mList.get(adapterPosition));
        }
    }

    @Override
    public void onScrollStart(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {

    }

    @Override
    public void onScrollEnd(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {
        if (adapterPosition == mList.size()) {
            XMToast.showToast(this, R.string.no_more_data);
            mItemOilParks.smoothScrollToPosition(adapterPosition - 1);
        }
    }

    @Override
    public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable RecyclerView.ViewHolder currentHolder, @Nullable RecyclerView.ViewHolder newCurrent) {

    }


    private void onItemChanged(final ParkInfoBean poiBean) {

        double distance = Double.valueOf(poiBean.getDistance());
        String distanceStr;
        if (distance >= DISTANCE) {
            distance = div(distance, DISTANCE, 3);
            distanceStr = getString(R.string.thousand_address_distance, new DecimalFormat("0.00").format(distance));
        } else {
            distanceStr = getString(R.string.address_distance, new DecimalFormat("0").format(distance));
        }

        mOilParkName.setText(poiBean.getName());
        mOilParkInfo.setText(String.format(getString(R.string.oilpark_landmark), distanceStr, StringUtil.isNotEmpty(poiBean.getAddress()) ? poiBean.getAddress() : getString(R.string.no_addrerss)));
        mNavigationBtn.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return null;
            }

            @Override
            @BusinessOnClick
            public void onClick(View v) {
                showNaviDialog(poiBean);
            }
        });
    }

    private void showNaviDialog(final ParkInfoBean poiBean) {
        ConfirmDialog dialog = new ConfirmDialog(this);

        String content = OIL.equals(mPoiType) ? getString(R.string.travel_message_navi_oil) : getString(R.string.travel_message_navi_park);

        dialog.setContent(content)
                .setPositiveButton(getString(R.string.travel_left_btn_navi), new XMAutoTrackerEventOnClickListener() {
                    @Override
                    public ItemEvent returnPositionEventMsg(View view) {
                        if (OIL.equals(mPoiType)) {
                            return new ItemEvent(EventConstants.NormalClick.OIL_NAVI_CANCE, GsonUtil.toJson(setOilParkTracker(poiBean)));
                        } else {
                            return new ItemEvent(EventConstants.NormalClick.PARK_NAVI_SURE, GsonUtil.toJson(setOilParkTracker(poiBean)));
                        }
                    }

                    @Override
                    @BusinessOnClick
                    public void onClick(View v) {
                        dialog.dismiss();
                        int ret = MapManager.getInstance().startNaviToPoi(poiBean.getName(), poiBean.getAddress(), poiBean.getLongitude(), poiBean.getLatitude());
//                        if (ret == -1) {
//                            showToast(R.string.im_guide);
//                        }
                    }
                })
                .setNegativeButton(getString(R.string.travel_right_btn), new XMAutoTrackerEventOnClickListener() {
                    @Override
                    public ItemEvent returnPositionEventMsg(View view) {
                        if (OIL.equals(mPoiType)) {
                            return new ItemEvent(EventConstants.NormalClick.OIL_NAVI_CANCE, GsonUtil.toJson(setOilParkTracker(poiBean)));
                        } else {
                            return new ItemEvent(EventConstants.NormalClick.PARK_NAVI_CANCE, GsonUtil.toJson(setOilParkTracker(poiBean)));
                        }
                    }

                    @Override
                    @BusinessOnClick
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private OilParkTrackerBean setOilParkTracker(ParkInfoBean poiBean) {
        OilParkTrackerBean oilParkTrackerBean = new OilParkTrackerBean();
        oilParkTrackerBean.name = poiBean.getName();
        oilParkTrackerBean.address = poiBean.getAddress();
        oilParkTrackerBean.longitude = poiBean.getLongitude();
        oilParkTrackerBean.latitude = poiBean.getLatitude();
        oilParkTrackerBean.distance = poiBean.getDistance();
        return oilParkTrackerBean;
    }

    public double div(double d1, double d2, int len) {// 进行除法运算
        BigDecimal b1 = new BigDecimal(d1);
        BigDecimal b2 = new BigDecimal(d2);
        return b1.divide(b2, len, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    private void loadMore() {
        if (mOilParkVM == null) {
            return;
        }
        if (mOilParkVM.isDeliciousLoadEnd(mPoiType)) {
            notifyLoadState(LauncherConstants.END);
        } else {
            pageNum++;
            if (mAddressInfo != null && mAddressInfo.latLonPoint != null) {
                mOilParkVM.queryNearByOilPark(mAddressInfo.latLonPoint.getLongitude() + "," + mAddressInfo.latLonPoint.getLatitude(), mAddressInfo.city, mPoiType, type, pageNum);
            } else {
                if (mLocationInfo == null) {
                    XMToast.showToast(getApplication(), R.string.not_nvi_info);
                    notifyLoadState(LauncherConstants.FAILED);
                    return;
                }

                if (!NetworkUtils.isConnected(this)) {
                    showToastException(R.string.net_work_error);
                    notifyLoadState(LauncherConstants.FAILED);
                    return;
                }
                mOilParkVM.queryNearByOilPark(mLocationInfo.getLongitude() + "," + mLocationInfo.getLatitude(), mLocationInfo.getCity(), mPoiType, type, pageNum);
            }

        }
    }

    private void notifyLoadState(int state) {
        if (LauncherConstants.COMPLETE == state) {
            mOilParkAdapter.loadMoreComplete();
        } else if (LauncherConstants.END == state) {
            mOilParkAdapter.loadMoreEnd();
        } else if (LauncherConstants.FAILED == state) {
            mOilParkAdapter.loadMoreFail();
        }
    }

    @Override
    protected void noNetworkOnRetry() {
        super.noNetworkOnRetry();
        if (NetworkUtils.isConnected(this)) {
            if (mAddressInfo != null && mAddressInfo.latLonPoint != null) {
                mOilParkVM.queryNearByOilPark(mAddressInfo.latLonPoint.getLongitude() + "," + mAddressInfo.latLonPoint.getLatitude(), mAddressInfo.city, mPoiType, type, pageNum);
            } else {
                mOilParkVM.queryNearByOilPark(mLocationInfo.getLongitude() + "," + mLocationInfo.getLatitude(), mLocationInfo.getCity(), mPoiType, type, pageNum);
            }

        }
    }

}
