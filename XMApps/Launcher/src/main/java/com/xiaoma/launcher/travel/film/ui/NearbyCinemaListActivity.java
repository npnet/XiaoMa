package com.xiaoma.launcher.travel.film.ui;

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
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.common.vm.BaseCollectVM;
import com.xiaoma.launcher.map.manager.MapManager;
import com.xiaoma.launcher.travel.film.adapter.NearbyCinemaAdapter;
import com.xiaoma.launcher.travel.film.vm.FilmVM;
import com.xiaoma.launcher.travel.itemevent.CinemaTrackerBean;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.mapadapter.model.SearchAddressInfo;
import com.xiaoma.mapadapter.ui.MapActivity;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.trip.movie.response.NearbyCinemaBean;
import com.xiaoma.trip.movie.response.NearbyCinemasDetailsBean;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.tputils.TPUtils;

import java.util.ArrayList;
import java.util.List;

@PageDescComponent(EventConstants.PageDescribe.nearbyCinemaListActivityPagePathDesc)
public class NearbyCinemaListActivity extends BaseActivity implements DiscreteScrollView.OnItemChangedListener, DiscreteScrollView.ScrollStateChangeListener<RecyclerView.ViewHolder> {
    private List<NearbyCinemasDetailsBean> mList = new ArrayList();
    private DiscreteScrollView mItemNearbyCinema;
    private FilmVM mFilmVM;
    private NearbyCinemaAdapter mNearbyCinemaAdapter;
    private TextView mNearbyCinemaName;
    private TextView mNearbyCinemaInfo;
    private Button mBuyBtn;
    private Button mNavigationBtn;
    private RelativeLayout mNotdataView;
    private TextView mTvTips;
    private int pageNum = 1;
    private int maxPageNum;
    private LinearLayout linearLayout;
    private TextView nearbyCinema;

    private NearbyCinemasDetailsBean mCollectBean;
    private int collectPosition;
    private int mCollectState;
    private LocationInfo mLocationInfo;
    private int REQUEST_CODE = 200;
    private SearchAddressInfo mAddressInfo;
    private static final String EXTRA_LOCATION_DATA = "location";
    private String type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nearby_cinema_activity);
        bindView();
        initView();
        initData();
    }

    private void bindView() {
        mNotdataView = findViewById(R.id.notdata_view);
        mTvTips = findViewById(R.id.tv_tips);
        linearLayout = findViewById(R.id.nearby_layout);
        nearbyCinema = findViewById(R.id.nearby_cinema);
        mItemNearbyCinema = findViewById(R.id.nearby_cinema_rv);
        mNearbyCinemaName = findViewById(R.id.nearby_cinema_name);
        mNearbyCinemaInfo = findViewById(R.id.nearby_cinema_info);
        mBuyBtn = findViewById(R.id.nearby_cinema_buy);
        mNavigationBtn = findViewById(R.id.nearby_cinema_navigation);
        getNaviBar().getMiddleView().setImageDrawable(getResources().getDrawable(R.drawable.icon_location));
    }

    private void initView() {
        mLocationInfo = LocationManager.getInstance().getCurrentLocation();

        getNaviBar().getMiddleView().setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick({EventConstants.NormalClick.NEARBYCINEMA_RENEW_POSITION})
            public void onClick(View v) {
                startActivityForResult(new Intent(NearbyCinemaListActivity.this, MapActivity.class), REQUEST_CODE);
            }
        });
        mTvTips.setText(String.format(getString(R.string.no_nearly), getString(R.string.cinema)));
        mNearbyCinemaAdapter = new NearbyCinemaAdapter();
        mItemNearbyCinema.setOrientation(DSVOrientation.HORIZONTAL);
        mItemNearbyCinema.addOnItemChangedListener(this);
        mItemNearbyCinema.addScrollStateChangeListener(this);
        mItemNearbyCinema.setSlideOnFling(true);
        mItemNearbyCinema.setAdapter(mNearbyCinemaAdapter);
        mItemNearbyCinema.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build());
        mNearbyCinemaAdapter.setEnableLoadMore(true);
        mNearbyCinemaAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadMore();
            }
        }, mItemNearbyCinema);
        mNearbyCinemaAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mItemNearbyCinema.smoothScrollToPosition(position);
            }
        });
        mNearbyCinemaAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                //收藏
                if (view.getId() == R.id.nearby_cinema_linear) {
                    if (!NetworkUtils.isConnected(NearbyCinemaListActivity.this)) {
                        showToastException(R.string.net_work_error);
                        return;
                    }
                    showProgressDialogNoMsg();
                    mCollectBean = mNearbyCinemaAdapter.getData().get(position);
                    collectPosition = position;
                    mCollectState = mCollectBean.getStatus() == BaseCollectVM.HAVE_COLLECT_STATE ? BaseCollectVM.CANCLE_COLLECT_STATE : BaseCollectVM.HAVE_COLLECT_STATE;
                    mFilmVM.collectItem(mCollectState, mCollectBean.getCinemaId(), LauncherConstants.COLLECT_FILM, GsonHelper.toJson(mCollectBean));

                    XmAutoTracker.getInstance().onEvent(mCollectBean.getStatus() == BaseCollectVM.HAVE_COLLECT_STATE ? EventConstants.NormalClick.CANCELCOLLECT : EventConstants.NormalClick.COLLECT,       //按钮名称(如播放,音乐列表)
                            GsonUtil.toJson(setCinemaTracker(mCollectBean)),          //对应id值(如音乐列表对应item id值)
                            EventConstants.PagePath.nearbyCinemaListActivityPath,    //页面路径
                            EventConstants.PageDescribe.nearbyCinemaListActivityPagePathDesc);//页面路径中午意思
                }
            }
        });

        mItemNearbyCinema.setAdapter(mNearbyCinemaAdapter);
    }

    private void loadMore() {
        if (mFilmVM == null) {
            return;
        }
        if (pageNum >= maxPageNum) {
            notifyLoadState(LauncherConstants.END);
        } else {
            pageNum++;
            if (mAddressInfo != null && mAddressInfo.latLonPoint != null) {
                mFilmVM.queryNearcyCinemas(type, mAddressInfo.city, "", pageNum, LauncherConstants.PAGE_SIZE, mAddressInfo.latLonPoint.getLatitude() + "", mAddressInfo.latLonPoint.getLongitude() + "");
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

                mFilmVM.queryNearcyCinemas(type, mLocationInfo.getCity(), mLocationInfo.getDistrict(), pageNum, LauncherConstants.PAGE_SIZE, mLocationInfo.getLatitude() + "", mLocationInfo.getLongitude() + "");
            }
        }
    }

    private void notifyLoadState(int state) {
        if (LauncherConstants.COMPLETE == state) {
            mNearbyCinemaAdapter.loadMoreComplete();
        } else if (LauncherConstants.END == state) {
            mNearbyCinemaAdapter.loadMoreEnd();
        } else if (LauncherConstants.FAILED == state) {
            mNearbyCinemaAdapter.loadMoreFail();
        }
    }

    private void initData() {
        type = getIntent().getStringExtra("type");
        mFilmVM = ViewModelProviders.of(this).get(FilmVM.class);
        mFilmVM.getNearbyCinemaData().observe(this, new Observer<XmResource<NearbyCinemaBean>>() {
            @Override
            public void onChanged(@Nullable XmResource<NearbyCinemaBean> listXmResource) {
                if (listXmResource == null) {
                    return;
                }
                listXmResource.handle(new OnCallback<NearbyCinemaBean>() {
                    @Override
                    public void onSuccess(NearbyCinemaBean data) {
                        if (data != null && !ListUtils.isEmpty(data.getCinemas())) {
                            linearLayout.setVisibility(View.VISIBLE);
                            mItemNearbyCinema.setVisibility(View.VISIBLE);
                            if (data.getPageInfo() != null) {
                                maxPageNum = data.getPageInfo().getTotalPage();
                            }
                            mList.addAll(data.getCinemas());
                            mNearbyCinemaAdapter.addData(data.getCinemas());
                            if (pageNum == 1) {
                                if (mList.size() >= 4) {
                                    mItemNearbyCinema.scrollToPosition(2);
                                } else {
                                    mItemNearbyCinema.scrollToPosition(mList.size() - 1);
                                }
                            }
                            notifyLoadState(LauncherConstants.COMPLETE);
                        } else {
                            mItemNearbyCinema.removeScrollStateChangeListener(NearbyCinemaListActivity.this);
                            mItemNearbyCinema.removeItemChangedListener(NearbyCinemaListActivity.this);
                            mNotdataView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        super.onFailure(msg);
                        notifyLoadState(LauncherConstants.FAILED);
                    }
                });
            }
        });

        mFilmVM.getCollectItem().observe(this, new Observer<XmResource<String>>() {
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
                            mNearbyCinemaAdapter.getData().set(collectPosition, mCollectBean);
                            mNearbyCinemaAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(String msg) {
                            showToast(String.format(getString(R.string.collcet_operate_fail), collectTip));
                        }
                    });
                }
            }
        });

        if (!NetworkUtils.isConnected(this)) {
            showNoNetView();
            return;
        }
        if (mLocationInfo == null) {
            XMToast.showToast(getApplication(), R.string.not_nvi_info);
            return;
        }

        if (!NetworkUtils.isConnected(this)) {
            showNoNetView();
            return;
        }

        mFilmVM.queryNearcyCinemas(type, mLocationInfo.getCity(), mLocationInfo.getDistrict(), pageNum, LauncherConstants.PAGE_SIZE, mLocationInfo.getLatitude() + "", mLocationInfo.getLongitude() + "");
    }

    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
        if (ListUtils.isEmpty(mList)) {
            return;
        }
        if (adapterPosition < mList.size()) {
            XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.LISTITEM,       //按钮名称(如播放,音乐列表)
                    GsonUtil.toJson(setCinemaTracker(mList.get(adapterPosition))),          //对应id值(如音乐列表对应item id值)
                    EventConstants.PagePath.nearbyCinemaListActivityPath,    //页面路径
                    EventConstants.PageDescribe.nearbyCinemaListActivityPagePathDesc);//页面路径中午意思

            onItemChanged(mList.get(adapterPosition));
        }
    }

    private void onItemChanged(final NearbyCinemasDetailsBean nearbyCinemasDetailsBean) {
        mNearbyCinemaInfo.setText(String.format(getString(R.string.cinema_landmark), nearbyCinemasDetailsBean.getCountyName(), nearbyCinemasDetailsBean.getDistance()) + (StringUtil.isNotEmpty(nearbyCinemasDetailsBean.getMinPrice()) ? String.format(getString(R.string.film_min_price), nearbyCinemasDetailsBean.getMinPrice()) : getString(R.string.film_not_price)));
        mNearbyCinemaName.setText(nearbyCinemasDetailsBean.getCinemaName());
        mBuyBtn.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(EventConstants.NormalClick.NEARBYCINEMA_BUY_TICKETS_NOW, GsonUtil.toJson(setCinemaTracker(nearbyCinemasDetailsBean)));
            }

            @Override
            @BusinessOnClick
            public void onClick(View v) {
                if (!NetworkUtils.isConnected(NearbyCinemaListActivity.this)) {
                    showToastException(R.string.net_work_error);
                    return;
                }

                TPUtils.put(NearbyCinemaListActivity.this, LauncherConstants.PATH_TYPE, getString(R.string.select_film));
                Intent intent = new Intent(NearbyCinemaListActivity.this, CinemaShowActivity.class);
                intent.putExtra(LauncherConstants.ActionExtras.NEARBY_CINEMAS_DETAILS_BEAN, nearbyCinemasDetailsBean);
                startActivity(intent);
            }
        });
        mNavigationBtn.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(EventConstants.NormalClick.NEARBYCINEMA_NAVI, GsonUtil.toJson(setCinemaTracker(nearbyCinemasDetailsBean)));
            }

            @Override
            @BusinessOnClick
            public void onClick(View v) {
                showNaviDialog(nearbyCinemasDetailsBean);
            }
        });
    }

    @Override
    public void onScrollStart(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {

    }

    @Override
    public void onScrollEnd(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {
        if (adapterPosition == mList.size() && pageNum >= maxPageNum) {
            XMToast.showToast(this, R.string.no_more_data);
            mItemNearbyCinema.smoothScrollToPosition(adapterPosition - 1);
        } else if (adapterPosition == mList.size()) {
            mItemNearbyCinema.scrollToPosition(adapterPosition - 1);
        }
    }

    @Override
    public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable RecyclerView.ViewHolder currentHolder, @Nullable RecyclerView.ViewHolder newCurrent) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            pageNum = 1;
            linearLayout.setVisibility(View.INVISIBLE);
            mItemNearbyCinema.setVisibility(View.INVISIBLE);

            mList.clear();
            mNearbyCinemaAdapter.getData().clear();
            mAddressInfo = data.getParcelableExtra(EXTRA_LOCATION_DATA);
            if (mAddressInfo != null && mAddressInfo.latLonPoint != null) {
                mFilmVM.queryNearcyCinemas(type, mAddressInfo.city, "", pageNum, LauncherConstants.PAGE_SIZE, mAddressInfo.latLonPoint.getLatitude() + "", mAddressInfo.latLonPoint.getLongitude() + "");
            }
        }
    }

    private void showNaviDialog(final NearbyCinemasDetailsBean nearbyCinemasDetailsBean) {
        ConfirmDialog dialog = new ConfirmDialog(this);
        dialog.setContent(getString(R.string.travel_message_navi_cinema))
                .setPositiveButton(getString(R.string.travel_left_btn_navi), new XMAutoTrackerEventOnClickListener() {
                    @Override
                    public ItemEvent returnPositionEventMsg(View view) {
                        return new ItemEvent(EventConstants.NormalClick.NEARBYCINEMA_NAVI_SURE, GsonUtil.toJson(setCinemaTracker(nearbyCinemasDetailsBean)));
                    }

                    @Override
                    @BusinessOnClick
                    public void onClick(View v) {
                        dialog.dismiss();
                        int ret = MapManager.getInstance().startNaviToPoi(nearbyCinemasDetailsBean.getCinemaName(), nearbyCinemasDetailsBean.getAddress(), Double.parseDouble(nearbyCinemasDetailsBean.getLongitude()), Double.parseDouble(nearbyCinemasDetailsBean.getLatitude()));
//                        if (ret == -1) {
//                            showToast(R.string.im_guide);
//                        }
                    }
                })
                .setNegativeButton(getString(R.string.travel_right_btn), new XMAutoTrackerEventOnClickListener() {
                    @Override
                    public ItemEvent returnPositionEventMsg(View view) {
                        return new ItemEvent(EventConstants.NormalClick.NEARBYCINEMA_NAVI_CANCE, GsonUtil.toJson(setCinemaTracker(nearbyCinemasDetailsBean)));
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
//        dialogMessage.setText(getString(R.string.travel_message_navi_cinema));
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
//                return new ItemEvent(EventConstants.NormalClick.NEARBYCINEMA_NAVI_SURE, GsonUtil.toJson(setCinemaTracker(nearbyCinemasDetailsBean)));
//            }
//
//            @Override
//            @BusinessOnClick
//            public void onClick(View v) {
//                builder.dismiss();
//                int ret=MapManager.getInstance().startNaviToPoi(nearbyCinemasDetailsBean.getCinemaName(),nearbyCinemasDetailsBean.getAddress(),Double.parseDouble(nearbyCinemasDetailsBean.getLongitude()),Double.parseDouble(nearbyCinemasDetailsBean.getLatitude()));
//                if(ret==-1){
//                    showToast(R.string.im_guide);
//                }
//            }
//        });
//        cancelBtn.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
//            @Override
//            public ItemEvent returnPositionEventMsg(View view) {
//                return new ItemEvent(EventConstants.NormalClick.NEARBYCINEMA_NAVI_CANCE, GsonUtil.toJson(setCinemaTracker(nearbyCinemasDetailsBean)));
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

    private CinemaTrackerBean setCinemaTracker(NearbyCinemasDetailsBean nearbyCinemasDetailsBean) {
        CinemaTrackerBean deliciousTrackerBean = new CinemaTrackerBean();
        deliciousTrackerBean.id = nearbyCinemasDetailsBean.getCinemaId();
        deliciousTrackerBean.value = nearbyCinemasDetailsBean.getCinemaName();
        deliciousTrackerBean.h = nearbyCinemasDetailsBean.getDistance();
        return deliciousTrackerBean;
    }

    @Override
    protected void noNetworkOnRetry() {
        super.noNetworkOnRetry();
        if (NetworkUtils.isConnected(this)) {
            mFilmVM.queryNearcyCinemas(type, mLocationInfo.getCity(), mLocationInfo.getDistrict(), pageNum, LauncherConstants.PAGE_SIZE, mLocationInfo.getLatitude() + "", mLocationInfo.getLongitude() + "");
        }
    }
}
