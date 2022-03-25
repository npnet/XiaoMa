package com.xiaoma.launcher.travel.scenic.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
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
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.common.manager.LauncherBlueToothPhoneManager;
import com.xiaoma.launcher.common.vm.BaseCollectVM;
import com.xiaoma.launcher.map.manager.MapManager;
import com.xiaoma.launcher.service.ui.ServiceFragment;
import com.xiaoma.launcher.travel.itemevent.CollectTrackerBean;
import com.xiaoma.launcher.travel.scenic.adapter.AttractionsAdapter;
import com.xiaoma.launcher.travel.scenic.vm.AttractionsVM;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.mapadapter.model.SearchAddressInfo;
import com.xiaoma.mapadapter.ui.MapActivity;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.trip.category.response.SearchStoreBean;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@PageDescComponent(EventConstants.PageDescribe.attractionsActivityPagePathDesc)
public class AttractionsActivity extends BaseActivity implements DiscreteScrollView.OnItemChangedListener, DiscreteScrollView.ScrollStateChangeListener<RecyclerView.ViewHolder> {

    private static final int DISTANCE = 1;
    private List<SearchStoreBean> mList = new ArrayList();
    private DiscreteScrollView mItemAttraction;
    private AttractionsVM mAttractionsVM;
    private AttractionsAdapter mAttractionsAdapter;
    private TextView mAttractionName;
    private TextView mAttractionInfo;
    private Button mNavigationBtn;
    private Button mPhoneBtn;
    private LocationInfo locationInfo;
    int offsetNum = 1;
    private String searchKey = "";
    private String display = "";
    private String cateId = "";
    public static final String TOUR_AROUND_ID = "195";
    private TextView attractionTitle;
    private LinearLayout attractionLayout;
    private SearchStoreBean mCollectBean;
    private int collectPosition;
    private int mCollectState;
    private Boolean mIsRecommend = false;
    private int REQUEST_CODE = 200;
    private LocationInfo mLocationInfo;
    private SearchAddressInfo mMAddressInfo;
    private static final String EXTRA_LOCATION_DATA = "location";
    private RelativeLayout mNotdataView;
    private TextView mTvTips;
    private String type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attraction_activity);
        bindView();
        initView();
        initData();
    }

    private void bindView() {
        attractionLayout = findViewById(R.id.attraction_layout);
        mNotdataView = findViewById(R.id.notdata_view);
        mTvTips = findViewById(R.id.tv_tips);
        attractionTitle = findViewById(R.id.attraction_title);
        mItemAttraction = findViewById(R.id.attraction_rv);
        mAttractionName = findViewById(R.id.attraction_name);
        mAttractionInfo = findViewById(R.id.attractions_info);
        mNavigationBtn = findViewById(R.id.attraction_navigation);
        mPhoneBtn = findViewById(R.id.attraction_phone);
        getNaviBar().getMiddleView().setImageDrawable(getResources().getDrawable(R.drawable.icon_location));
    }

    private void initView() {
        mLocationInfo = LocationManager.getInstance().getCurrentLocation();

        getNaviBar().getMiddleView().setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick({EventConstants.NormalClick.ATTRACTION_RENEW_POSITION})
            public void onClick(View v) {
                startActivityForResult(new Intent(AttractionsActivity.this, MapActivity.class), REQUEST_CODE);
            }
        });
        mIsRecommend = getIntent().getBooleanExtra(LauncherConstants.TravelConstants.ATTRACTION_DATA_RECOMMEND_TYPE, false);
        searchKey = getIntent().getStringExtra(LauncherConstants.SEARCH_KEY) == null ? getString(R.string.nearby_play) : getIntent().getStringExtra(LauncherConstants.SEARCH_KEY);
        display = getIntent().getStringExtra(LauncherConstants.DISPLAY) == null ? getString(R.string.nearby_play) : getIntent().getStringExtra(LauncherConstants.DISPLAY);
        cateId = getIntent().getStringExtra(LauncherConstants.CATEID) == null ? "" : getIntent().getStringExtra(LauncherConstants.CATEID);
        if (mIsRecommend) {
            attractionTitle.setText(R.string.recommend_attractions);
            mTvTips.setText(String.format(getString(R.string.no_recommend), getString(R.string.attraction)));
        } else {
            if (getString(R.string.nearby_play).equals(display)) {
                attractionTitle.setText(R.string.nearby_attractions);
                mTvTips.setText(String.format(getString(R.string.no_nearly), getString(R.string.attraction)));
            } else {
                attractionTitle.setText(display);
                mTvTips.setText(String.format(getString(R.string.no_find_related), display, getString(R.string.attraction)));
            }
        }

        mAttractionsAdapter = new AttractionsAdapter();
        mItemAttraction.setOrientation(DSVOrientation.HORIZONTAL);
        mItemAttraction.addOnItemChangedListener(this);
        mItemAttraction.addScrollStateChangeListener(this);
        mItemAttraction.setSlideOnFling(true);
        mItemAttraction.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());
        mAttractionsAdapter.setEnableLoadMore(true);
        mAttractionsAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadMore();
            }
        }, mItemAttraction);
        mAttractionsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mItemAttraction.smoothScrollToPosition(position);
            }
        });
        mAttractionsAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.attractions_linear) {
                    if (!NetworkUtils.isConnected(AttractionsActivity.this)) {
                        showToastException(R.string.net_work_error);
                        return;
                    }
                    showProgressDialogNoMsg();
                    mCollectBean = mAttractionsAdapter.getData().get(position);
                    collectPosition = position;
                    mCollectState = mCollectBean.getStatus() == BaseCollectVM.HAVE_COLLECT_STATE ? BaseCollectVM.CANCLE_COLLECT_STATE : BaseCollectVM.HAVE_COLLECT_STATE;
                    mAttractionsVM.collectItem(mCollectState, mCollectBean.getId(), LauncherConstants.COLLECT_ATTR, GsonHelper.toJson(mCollectBean));
                    XmAutoTracker.getInstance().onEvent(mCollectBean.getStatus() == BaseCollectVM.HAVE_COLLECT_STATE ? EventConstants.NormalClick.CANCELCOLLECT : EventConstants.NormalClick.COLLECT,       //按钮名称(如播放,音乐列表)
                            GsonUtil.toJson(setDeliciousTracker(mCollectBean)),          //对应id值(如音乐列表对应item id值)
                            EventConstants.PagePath.attractionsActivityPath,    //页面路径
                            EventConstants.PageDescribe.attractionsActivityPagePathDesc);//页面路径中午意思
                }
            }
        });
        mItemAttraction.setAdapter(mAttractionsAdapter);
    }

    private void loadMore() {
        if (mAttractionsVM == null) {
            return;
        }
        if (mAttractionsVM.isAttractionsLoadEnd()) {
            notifyLoadState(LauncherConstants.END);
        } else {
            offsetNum = offsetNum + LauncherConstants.PAGE_SIZE;
            if (mMAddressInfo != null && mMAddressInfo.latLonPoint != null) {
                mAttractionsVM.searchAttractions(type, searchKey, mMAddressInfo.latLonPoint.getLatitude() + "," + mMAddressInfo.latLonPoint.getLongitude(), "", cateId, offsetNum);
            } else {
                if (!NetworkUtils.isConnected(this)) {
                    showToastException(R.string.net_work_error);
                    notifyLoadState(LauncherConstants.FAILED);
                    return;
                }
                if (mLocationInfo == null) {
                    XMToast.showToast(getApplication(), R.string.not_nvi_info);
                    notifyLoadState(LauncherConstants.FAILED);
                    return;
                }

                mAttractionsVM.searchAttractions(type, searchKey, mLocationInfo.getLatitude() + "," + mLocationInfo.getLongitude(), mLocationInfo.getCity(), cateId, offsetNum);
            }

        }

    }

    private CollectTrackerBean setDeliciousTracker(SearchStoreBean searchStoreBean) {
        CollectTrackerBean collectTrackerBean = new CollectTrackerBean();
        collectTrackerBean.id = searchStoreBean.getId();
        collectTrackerBean.value = searchStoreBean.getName();
        collectTrackerBean.h = searchStoreBean.getSubcate();
        collectTrackerBean.i = String.valueOf(searchStoreBean.getDistance());
        collectTrackerBean.j = String.valueOf(searchStoreBean.getAvgprice());
        return collectTrackerBean;
    }

    private void notifyLoadState(int state) {
        if (LauncherConstants.COMPLETE == state) {
            mAttractionsAdapter.loadMoreComplete();
        } else if (LauncherConstants.END == state) {

            mAttractionsAdapter.loadMoreEnd();
        } else if (LauncherConstants.FAILED == state) {
            mAttractionsAdapter.loadMoreFail();
        }
    }

    private void initData() {
        type = getIntent().getStringExtra("type");
        if (StringUtil.isEmpty(type)) {
            type = ServiceFragment.NEAR_BY_ATTRACTION;
        }
        locationInfo = LocationManager.getInstance().getCurrentLocation();
        mAttractionsVM = ViewModelProviders.of(this).get(AttractionsVM.class);
        mAttractionsVM.getAttractionsList().observe(this, new Observer<XmResource<List<SearchStoreBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<SearchStoreBean>> listXmResource) {
                if (listXmResource == null) {
                    return;
                }
                listXmResource.handle(new OnCallback<List<SearchStoreBean>>() {
                    @Override
                    public void onSuccess(List<SearchStoreBean> data) {
                        if (!ListUtils.isEmpty(data)) {
                            attractionLayout.setVisibility(View.VISIBLE);
                            mItemAttraction.setVisibility(View.VISIBLE);
                            mList.addAll(data);
                            mAttractionsAdapter.addData(data);
                            if (offsetNum == 1) {
                                if (data.size() >= 4) {
                                    mItemAttraction.scrollToPosition(2);
                                } else if (data.size() == 3) {
                                    mItemAttraction.scrollToPosition(1);
                                } else {
                                    mItemAttraction.scrollToPosition(data.size() - 1);
                                }
                            }
                        } else {
                            if (offsetNum == 1) {
                                mItemAttraction.removeScrollStateChangeListener(AttractionsActivity.this);
                                mItemAttraction.removeItemChangedListener(AttractionsActivity.this);
                                attractionTitle.setVisibility(View.VISIBLE);
                                mNotdataView.setVisibility(View.VISIBLE);
                            }
                        }
                        notifyLoadState(LauncherConstants.COMPLETE);

                    }

                    @Override
                    public void onFailure(String msg) {
                        super.onFailure(msg);
                        notifyLoadState(LauncherConstants.FAILED);
                    }

                    @Override
                    public void onError(int code, String message) {
                        super.onError(code, message);
                    }
                });
            }
        });
        mAttractionsVM.getCollectItem().observe(this, new Observer<XmResource<String>>() {

            @Override
            public void onChanged(@Nullable XmResource<String> stringXmResource) {
                dismissProgress();
                final String collectTip = getString(mCollectState == BaseCollectVM.HAVE_COLLECT_STATE ? R.string.collect : R.string.cancle_collect);
                if (stringXmResource == null) {
                    showToast(String.format(getString(R.string.collcet_operate_fail), collectTip));
                } else {
                    stringXmResource.handle(new OnCallback<String>() {
                        @Override
                        public void onSuccess(String data) {
                            showToast(String.format(getString(R.string.collcet_operate_success), collectTip));
                            mCollectBean.setStatus(mCollectState);
                            mAttractionsAdapter.getData().set(collectPosition, mCollectBean);
                            mAttractionsAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(String msg) {
                            showToast(String.format(getString(R.string.collcet_operate_fail), collectTip));
                        }
                    });
                }
            }
        });

        if (mIsRecommend) {
            mAttractionsVM.fetchRecommendByAttraction();
        } else {
            if (mLocationInfo == null) {
                XMToast.showToast(getApplication(), R.string.not_nvi_info);
                return;
            }

            if (!NetworkUtils.isConnected(this)) {
                showNoNetView();
                return;
            }

            mAttractionsVM.searchAttractions(type, searchKey, mLocationInfo.getLatitude() + "," + mLocationInfo.getLongitude(), mLocationInfo.getCity(), cateId, offsetNum);
        }

    }

    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
        if (adapterPosition < mList.size()) {
            XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.LISTITEM,       //按钮名称(如播放,音乐列表)
                    GsonUtil.toJson(setDeliciousTracker(mList.get(adapterPosition))),          //对应id值(如音乐列表对应item id值)
                    EventConstants.PagePath.attractionsActivityPath,    //页面路径
                    EventConstants.PageDescribe.attractionsActivityPagePathDesc);//页面路径中午意思
            onItemChanged(mList.get(adapterPosition));
        }
    }

    private void onItemChanged(final SearchStoreBean searchStoreBean) {
        double distance = searchStoreBean.getDistance();
        String distanceStr = "";
        if (distance >= DISTANCE) {
            distanceStr = getString(R.string.thousand_address_distance, new DecimalFormat("0.00").format(distance));
        } else {
            distance = distance * 1000;
            distanceStr = getString(R.string.address_distance, new DecimalFormat("0").format(distance));
        }

        mAttractionName.setText(searchStoreBean.getName());
        mAttractionInfo.setText(String.format(getString(R.string.delicious_landmark), searchStoreBean.getArea().length() > 0 ? searchStoreBean.getArea() : getString(R.string.hotel_no_land), searchStoreBean.getSubcate(), distanceStr, String.valueOf(searchStoreBean.getAvgprice())));
        mNavigationBtn.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(EventConstants.NormalClick.ATTRACTION_NAVIGATION, GsonUtil.toJson(setDeliciousTracker(searchStoreBean)));
            }

            @Override
            @BusinessOnClick
            public void onClick(View v) {
                showNaviDialog(searchStoreBean);
            }
        });
        mPhoneBtn.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(EventConstants.NormalClick.ATTRACTION_PHONE,       //按钮名称(如播放,音乐列表)
                        GsonUtil.toJson(setDeliciousTracker(searchStoreBean)));
            }

            @Override
            @BusinessOnClick
            public void onClick(View v) {
                showPhoneDialog(searchStoreBean);
            }
        });
    }

    @Override
    public void onScrollStart(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {

    }

    @Override
    public void onScrollEnd(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {
        if (adapterPosition == mList.size() && mAttractionsVM.isAttractionsLoadEnd()) {
            XMToast.showToast(this, R.string.no_more_data);
            mItemAttraction.smoothScrollToPosition(adapterPosition - 1);
        } else if (adapterPosition == mList.size()) {
            mItemAttraction.scrollToPosition(adapterPosition - 1);
        }
    }

    @Override
    public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable RecyclerView.ViewHolder currentHolder, @Nullable RecyclerView.ViewHolder newCurrent) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            offsetNum = 1;
            attractionLayout.setVisibility(View.INVISIBLE);
            mItemAttraction.setVisibility(View.INVISIBLE);
            mList.clear();
            mAttractionsAdapter.getData().clear();
            mMAddressInfo = data.getParcelableExtra(EXTRA_LOCATION_DATA);
            if (mMAddressInfo != null && mMAddressInfo.latLonPoint != null) {
                mAttractionsVM.searchAttractions(type, searchKey, mMAddressInfo.latLonPoint.getLatitude() + "," + mMAddressInfo.latLonPoint.getLongitude(), "", cateId, offsetNum);
            }
        }
    }

    @Override
    protected void noNetworkOnRetry() {
        super.noNetworkOnRetry();
        if (NetworkUtils.isConnected(this)) {
            mAttractionsVM.searchAttractions(type, searchKey, mLocationInfo.getLatitude() + "," + mLocationInfo.getLongitude(), mLocationInfo.getCity(), cateId, offsetNum);
        }
    }

    /**
     * 电话dialog
     *
     * @param searchStoreBean
     */
    private void showPhoneDialog(final SearchStoreBean searchStoreBean) {
        if(searchStoreBean == null || TextUtils.isEmpty(searchStoreBean.getPhone())){
            showToastException(R.string.no_found_number);
            return;
        }
        String[] strings = searchStoreBean.getPhone().split("\\/");
        String message = String.format(getString(R.string.travel_message_phone_attraction), strings[0]);
        SpannableString s1 = new SpannableString(message);
        s1.setSpan(new AbsoluteSizeSpan(32, true), 0, s1.length() - strings[0].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        s1.setSpan(new AbsoluteSizeSpan(28, true), s1.length() - strings[0].length(), s1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ConfirmDialog dialog = new ConfirmDialog(this);
        dialog.setContent(s1)
                .setPositiveButton(getString(R.string.travel_left_btn_phone), new XMAutoTrackerEventOnClickListener() {
                    @Override
                    public ItemEvent returnPositionEventMsg(View view) {
                        return new ItemEvent(EventConstants.NormalClick.ATTRACTION_PHONE_SURE, GsonUtil.toJson(setDeliciousTracker(searchStoreBean)));
                    }

                    @Override
                    @BusinessOnClick
                    public void onClick(View v) {
                        dialog.dismiss();
                        LauncherBlueToothPhoneManager.getInstance().callPhone(strings[0]);
                    }
                })
                .setNegativeButton(getString(R.string.travel_right_btn), new XMAutoTrackerEventOnClickListener() {
                    @Override
                    public ItemEvent returnPositionEventMsg(View view) {
                        return new ItemEvent(EventConstants.NormalClick.ATTRACTION_PHONE_CANCE, GsonUtil.toJson(setDeliciousTracker(searchStoreBean)));
                    }

                    @Override
                    @BusinessOnClick
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                })
                .show();

//        View view = View.inflate(this, R.layout.dialog_travel, null);
//        TextView dialogTitle = view.findViewById(R.id.dialog_title);
//        TextView dialogMessage = view.findViewById(R.id.dialog_message);
//        TextView sureBtn = view.findViewById(R.id.btn_sure);
//        TextView cancelBtn = view.findViewById(R.id.btn_cancel);
//        String[] strings = searchStoreBean.getPhone().split("\\/");
//        dialogTitle.setText(getString(R.string.travel_tip_str));
//        String message = String.format(getString(R.string.travel_message_phone_attraction), strings[0]);
//        SpannableString s1 = new SpannableString(message);
//        s1.setSpan(new AbsoluteSizeSpan(32, true), 0, s1.length() - strings[0].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        s1.setSpan(new AbsoluteSizeSpan(28, true), s1.length() - strings[0].length(), s1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        dialogMessage.setText(s1);
//        sureBtn.setText(getString(R.string.travel_left_btn_phone));
//        cancelBtn.setText(getString(R.string.travel_right_btn));
//        final XmDialog builder = new XmDialog.Builder(this)
//                .setView(view)
//                .setWidth(this.getResources().getDimensionPixelOffset(R.dimen.dialog_travel_width))
//                .setHeight(this.getResources().getDimensionPixelOffset(R.dimen.dialog_travel_height_320))
//                .create();
//        sureBtn.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
//            @Override
//            public ItemEvent returnPositionEventMsg(View view) {
//                return new ItemEvent(EventConstants.NormalClick.ATTRACTION_PHONE_SURE,  GsonUtil.toJson(setDeliciousTracker(searchStoreBean)));
//            }
//
//            @Override
//            @BusinessOnClick
//            public void onClick(View v) {
//                builder.dismiss();
//                LauncherBlueToothPhoneManager.getInstance().callPhone(strings[0]);
//            }
//        });
//        cancelBtn.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
//            @Override
//            public ItemEvent returnPositionEventMsg(View view) {
//                return new ItemEvent(EventConstants.NormalClick.ATTRACTION_PHONE_CANCE,  GsonUtil.toJson(setDeliciousTracker(searchStoreBean)));
//            }
//
//            @Override
//            @BusinessOnClick
//            public void onClick(View v) {
//                builder.dismiss();
//            }
//        });
//        builder.show();
    }

    private void showNaviDialog(final SearchStoreBean searchStoreBean) {
        ConfirmDialog dialog = new ConfirmDialog(this);
        dialog.setContent(getString(R.string.travel_message_navi_attractions))
                .setPositiveButton(getString(R.string.travel_left_btn_navi), new XMAutoTrackerEventOnClickListener() {
                    @Override
                    public ItemEvent returnPositionEventMsg(View view) {
                        return new ItemEvent(EventConstants.NormalClick.ATTRACTION_NAVI_SURE, GsonUtil.toJson(setDeliciousTracker(searchStoreBean)));
                    }

                    @Override
                    @BusinessOnClick
                    public void onClick(View v) {
                        dialog.dismiss();
                        int ret = MapManager.getInstance().startNaviToPoi(searchStoreBean.getName(), searchStoreBean.getAddress(), Double.parseDouble(searchStoreBean.getLng()), Double.parseDouble(searchStoreBean.getLat()));
//                        if (ret == -1) {
//                            showToast(R.string.im_guide);
//                        }
                    }
                })
                .setNegativeButton(getString(R.string.travel_right_btn), new XMAutoTrackerEventOnClickListener() {
                    @Override
                    public ItemEvent returnPositionEventMsg(View view) {
                        return new ItemEvent(EventConstants.NormalClick.ATTRACTION_NAVI_CANCE, GsonUtil.toJson(setDeliciousTracker(searchStoreBean)));
                    }

                    @Override
                    @BusinessOnClick
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                })
                .show();

//        View view = View.inflate(this, R.layout.dialog_travel, null);
//        TextView dialogTitle = view.findViewById(R.id.dialog_title);
//        TextView dialogMessage = view.findViewById(R.id.dialog_message);
//        TextView sureBtn = view.findViewById(R.id.btn_sure);
//        TextView cancelBtn = view.findViewById(R.id.btn_cancel);
//        dialogTitle.setText(getString(R.string.travel_tip_str));
//        dialogMessage.setText(getString(R.string.travel_message_navi_attractions));
//        sureBtn.setText(getString(R.string.travel_left_btn_navi));
//        cancelBtn.setText(getString(R.string.travel_right_btn));
//        final XmDialog builder = new XmDialog.Builder(this)
//                .setView(view)
//                .setWidth(this.getResources().getDimensionPixelOffset(R.dimen.dialog_travel_width))
//                .setHeight(this.getResources().getDimensionPixelOffset(R.dimen.dialog_travel_height))
//                .create();
//        sureBtn.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
//            @Override
//            public ItemEvent returnPositionEventMsg(View view) {
//                return new ItemEvent(EventConstants.NormalClick.ATTRACTION_NAVI_SURE,  GsonUtil.toJson(setDeliciousTracker(searchStoreBean)));
//            }
//
//            @Override
//            @BusinessOnClick
//            public void onClick(View v) {
//                builder.dismiss();
//                int ret=MapManager.getInstance().startNaviToPoi(searchStoreBean.getName(),searchStoreBean.getAddress(),Double.parseDouble(searchStoreBean.getLng()),Double.parseDouble(searchStoreBean.getLat()));
//                if(ret==-1){
//                    showToast(R.string.im_guide);
//                }
//            }
//        });
//        cancelBtn.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
//            @Override
//            public ItemEvent returnPositionEventMsg(View view) {
//                return new ItemEvent(EventConstants.NormalClick.ATTRACTION_NAVI_CANCE,  GsonUtil.toJson(setDeliciousTracker(searchStoreBean)));
//            }
//
//            @Override
//            @BusinessOnClick
//            public void onClick(View v) {
//                builder.dismiss();
//            }
//        });
//        builder.show();
    }
}
