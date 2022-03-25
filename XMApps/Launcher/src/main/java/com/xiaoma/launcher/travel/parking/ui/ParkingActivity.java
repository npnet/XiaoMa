package com.xiaoma.launcher.travel.parking.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.discretescrollview.DSVOrientation;
import com.discretescrollview.DiscreteScrollView;
import com.discretescrollview.transform.ScaleTransformer;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.common.views.TravelLoadMoreView;
import com.xiaoma.launcher.map.manager.MapManager;
import com.xiaoma.launcher.travel.parking.adapter.ParkingAdapter;
import com.xiaoma.launcher.travel.parking.vm.ParkingVM;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LatLng;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.mapadapter.utils.MapUtil;
import com.xiaoma.model.XmResource;
import com.xiaoma.trip.parking.response.ParkingInfoBean;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.ConvertUtils;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.StringUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class ParkingActivity extends BaseActivity implements DiscreteScrollView.OnItemChangedListener, DiscreteScrollView.ScrollStateChangeListener<RecyclerView.ViewHolder> {

    private List<ParkingInfoBean> mList = new ArrayList();
    private DiscreteScrollView mItemParking;
    private ParkingVM mParkingVM;
    private ParkingAdapter mParkingAdapter;
    private TextView mParkingPosition;
    private TextView mParkingDistance;
    private TextView mParkingOpenType;
    private Button mNavigationBtn;
    private Button mParkingDetails;
    private LocationInfo locationInfo;
    int offsetNum = 1;
    private TextView parkingTitle;
    private LinearLayout parkingLayout;
    private Boolean mIsRecommend = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parking_activity);
        bindView();
        initView();
        initData();
    }

    private void bindView() {
        mItemParking = findViewById(R.id.parking_rv);
        parkingTitle = findViewById(R.id.parking_title);
        mParkingPosition = findViewById(R.id.parking_position);
        mParkingDistance = findViewById(R.id.parking_distance);
        mParkingOpenType = findViewById(R.id.parking_open_type);
        mNavigationBtn = findViewById(R.id.parking_navigation);
        mParkingDetails = findViewById(R.id.parking_details);
        parkingLayout = findViewById(R.id.parking_layout);
    }

    private void initView() {
        mIsRecommend = getIntent().getBooleanExtra(LauncherConstants.TravelConstants.PARKING_DATA_RECOMMEND_TYPE, false);
        if (mIsRecommend) {
            parkingTitle.setText(R.string.parking_recomomend);
        }
        mParkingAdapter = new ParkingAdapter();
        mItemParking.setOrientation(DSVOrientation.HORIZONTAL);
        mItemParking.addOnItemChangedListener(this);
        mItemParking.addScrollStateChangeListener(this);
        mItemParking.setSlideOnFling(true);
        mItemParking.setAdapter(mParkingAdapter);
        mItemParking.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());

        mParkingAdapter.setEnableLoadMore(true);
        mParkingAdapter.setLoadMoreView(new TravelLoadMoreView());
        mParkingAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadMore();
            }
        }, mItemParking);
        mItemParking.setAdapter(mParkingAdapter);
    }

    private void initData() {
        mParkingVM = ViewModelProviders.of(this).get(ParkingVM.class);
        mParkingVM.getParkingList().observe(this, new Observer<XmResource<List<ParkingInfoBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<ParkingInfoBean>> listXmResource) {
                if (listXmResource == null) {
                    return;
                }

                listXmResource.handle(new OnCallback<List<ParkingInfoBean>>() {
                    @Override
                    public void onSuccess(List<ParkingInfoBean> data) {
                        if (!ListUtils.isEmpty(data)) {
                            parkingLayout.setVisibility(View.VISIBLE);
                            parkingTitle.setVisibility(View.VISIBLE);
                            mList.addAll(data);
                            mParkingAdapter.addData(data);
                            if (offsetNum == 1) {
                                if (data.size() >= 4) {
                                    mItemParking.scrollToPosition(2);
                                } else {
                                    mItemParking.scrollToPosition(data.size() - 1);
                                }
                            }
                        } else {
                            if (offsetNum == 1) {
                                mItemParking.removeScrollStateChangeListener(ParkingActivity.this);
                                mItemParking.removeItemChangedListener(ParkingActivity.this);
                                mParkingAdapter.setEmptyView(R.layout.parking_notdata, (ViewGroup) mItemParking.getParent());
                            }
                        }
                        notifyLoadState(LauncherConstants.COMPLETE);
                    }

                    @Override
                    public void onFailure(String msg) {
                        super.onFailure(msg);
                    }
                });
            }
        });
        if (mIsRecommend) {
            mParkingVM.fetchRecommendByParking();
        } else {
            mParkingVM.SearchParkingInfo(offsetNum);
        }
    }


    private void loadMore() {
        if (mParkingVM == null) {
            return;
        }
        if (mParkingVM.isParkingLoadEnd()) {
            notifyLoadState(LauncherConstants.END);
        } else {
            offsetNum = offsetNum + LauncherConstants.PAGE_SIZE;
            mParkingVM.SearchParkingInfo(offsetNum);
        }
    }

    private void notifyLoadState(int state) {
        if (LauncherConstants.COMPLETE == state) {
            mParkingAdapter.loadMoreComplete();
        } else if (LauncherConstants.END == state) {
            mParkingAdapter.loadMoreEnd();
        } else if (LauncherConstants.FAILED == state) {
            mParkingAdapter.loadMoreFail();
        }
    }

    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
        if (adapterPosition < mList.size()) {
            onItemChanged(mList.get(adapterPosition));
        }
    }

    private void onItemChanged(ParkingInfoBean parkingInfoBean) {
        locationInfo = LocationManager.getInstance().getCurrentLocation();
        mParkingPosition.setText(parkingInfoBean.getBuilding());

        if (locationInfo != null) {
            String coordinateAmap = parkingInfoBean.getCoordinateAmap();
            String[] latAndLng = coordinateAmap.split("\\,");
            double distance = MapUtil.calculateLineDistance(new LatLng(locationInfo.getLatitude(), locationInfo.getLongitude()),
                    new LatLng(ConvertUtils.stringToDouble(latAndLng[1]), ConvertUtils.stringToDouble(latAndLng[0])));
            if (distance >= 1000) {
                distance = distance / 1000;
                mParkingDistance.setText(getString(R.string.thousand_address_distance, new DecimalFormat("0.00").format(distance)));
            } else {
                mParkingDistance.setText(getString(R.string.address_distance, new DecimalFormat("0").format(distance)));
            }
        }

//        mParkingOpenType.setText(parkingInfoBean.getOpenTime() + "--" + parkingInfoBean.getCloseTime());
        mNavigationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XMToast.showToast(ParkingActivity.this, getString(R.string.open_nvi));
                String entranceCoordinatesAmap=parkingInfoBean.getEntranceCoordinatesAmap();
                if(StringUtil.isEmpty(entranceCoordinatesAmap)){
                    XMToast.showToast(ParkingActivity.this,getString(R.string.open_nvi_error));
                    return;
                }
                String[] data=entranceCoordinatesAmap.split(",");
                if(data.length<2){
                    XMToast.showToast(ParkingActivity.this,getString(R.string.open_nvi_error));
                    return;
                }
                int ret=MapManager.getInstance().startNaviToPoi(parkingInfoBean.getName(),parkingInfoBean.getAddress(),Double.parseDouble(data[0]),Double.parseDouble(data[1]));
//                if(ret==-1){
//                    showToast(R.string.im_guide);
//                }
            }
        });
        mParkingDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XMToast.showToast(ParkingActivity.this, getString(R.string.look_details));
            }
        });
    }

    @Override
    public void onScrollStart(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {

    }

    @Override
    public void onScrollEnd(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {
        if (adapterPosition == mList.size() && mParkingVM.isParkingLoadEnd()) {
            mItemParking.smoothScrollToPosition(adapterPosition - 1);
        } else if (adapterPosition == mList.size()) {
            mItemParking.scrollToPosition(adapterPosition - 1);
        }
    }

    @Override
    public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable RecyclerView.ViewHolder currentHolder, @Nullable RecyclerView.ViewHolder newCurrent) {

    }
}
