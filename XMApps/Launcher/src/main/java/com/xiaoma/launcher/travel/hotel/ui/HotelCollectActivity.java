package com.xiaoma.launcher.travel.hotel.ui;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.travel.hotel.ui
 *  @file_name:      HotelCollectActivity
 *  @author:         Rookie
 *  @create_time:    2019/2/21 14:27
 *  @description：   TODO             */

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.ad.utils.GsonUtil;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.common.manager.LauncherBlueToothPhoneManager;
import com.xiaoma.launcher.common.views.TravelLoadMoreView;
import com.xiaoma.launcher.common.vm.BaseCollectVM;
import com.xiaoma.launcher.map.manager.MapManager;
import com.xiaoma.launcher.travel.film.vm.CollectVM;
import com.xiaoma.launcher.travel.hotel.adapter.HotelCollectAdapter;
import com.xiaoma.launcher.travel.hotel.vm.RecomHotelVM;
import com.xiaoma.launcher.travel.itemevent.CollectTrackerBean;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.trip.common.CollectItems;
import com.xiaoma.trip.hotel.response.HotelBean;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmDividerDecoration;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;
@PageDescComponent(EventConstants.PageDescribe.hotelCollectActivityPagePathDesc)
public class HotelCollectActivity extends BaseActivity {

    private List<HotelBean> mHotelBeanList;
    private HotelCollectAdapter mHotelCollectAdapter;
    private RecomHotelVM mRecomHotelVM;
    private HotelBean mHotelBean;
    private String mStart;
    private String mEnd;
    public RecyclerView mRvCollect;
    private XmScrollBar mXmScrollBar;
    public static final String COLLECT_HOTEL = "Hotel";
    public int mCollectState;
    protected CollectVM mCollectVM;
    private int pageNo = 0;
    private int maxPageNum;
    private boolean isend = false;
    //手指按下的点为(x1, y1)手指离开屏幕的点为(x2, y2)
    float x1 = 0;
    float x2 = 0;
    private String type;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_collect);
        initView();
        initCollectVM();
        initRecomHoteVM();
    }

    private void initView() {
        mRvCollect = findViewById(R.id.rv_collect);
        mRvCollect.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mXmScrollBar = findViewById(R.id.scroll_bar);
        XmDividerDecoration decor = new XmDividerDecoration(this, DividerItemDecoration.HORIZONTAL);
        int right = 90;
        int extra = 50;
        decor.setRect(0, 0, right, 0);
        decor.setExtraMargin(extra);
        mRvCollect.addItemDecoration(decor);
        //设置空布局
        View emptyView = View.inflate(this, R.layout.empty_view, null);
        TextView tvTip = emptyView.findViewById(R.id.tv_tips);
        tvTip.setText(getString(R.string.no_collection_hotel));
        mHotelBeanList = new ArrayList<>();
        mHotelCollectAdapter = new HotelCollectAdapter(R.layout.adapter_collect_v2, mHotelBeanList);

        mHotelCollectAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                int id = view.getId();
                if (id == R.id.tv_book) {

                    mHotelBean = mHotelCollectAdapter.getData().get(position);
                    XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.HOTEL_CLLECT_BOOK,       //按钮名称(如播放,音乐列表)
                           "",          //对应id值(如音乐列表对应item id值)
                            EventConstants.PagePath.hotelCollectActivity,    //页面路径
                            EventConstants.PageDescribe.hotelCollectActivityPagePathDesc);//页面路径中午意思
                    String[] date = TimeUtils.getCurrAndNextDate().split(" ");
                    mStart = date[0];
                    mEnd = date[1];
                    mRecomHotelVM.fetchHotelRoomStatus(mHotelBean.getHotelId(), mStart, mEnd);
                } else if (id == R.id.tv_guide) {
                    mHotelBean = mHotelBeanList.get(position);
                    XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.HOTEL_COLLECT_NAVI,       //按钮名称(如播放,音乐列表)
                            GsonUtil.toJson(setHotelTracker(mHotelBean)),          //对应id值(如音乐列表对应item id值)
                            EventConstants.PagePath.hotelCollectActivity,    //页面路径
                            EventConstants.PageDescribe.hotelCollectActivityPagePathDesc);//页面路径中午意思
                    showNaviDialog(mHotelBeanList.get(position));
                } else if (id == R.id.tv_call) {
                    mHotelBean = mHotelBeanList.get(position);
                    XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.HOTEL_COLLECT_PHONE,       //按钮名称(如播放,音乐列表)
                            GsonUtil.toJson(setHotelTracker(mHotelBean)),          //对应id值(如音乐列表对应item id值)
                            EventConstants.PagePath.hotelCollectActivity,    //页面路径
                            EventConstants.PageDescribe.hotelCollectActivityPagePathDesc);//页面路径中午意思
                    showPhoneDialog(mHotelCollectAdapter.getData().get(position));
                } else if (id == R.id.tv_collection) {
                    if (!NetworkUtils.isConnected(HotelCollectActivity.this)) {
                        showToastException(R.string.net_work_error);
                        return;
                    }
                    mHotelBean = mHotelBeanList.get(position);

                    XmAutoTracker.getInstance().onEvent(mHotelBean.getStatus() == BaseCollectVM.HAVE_COLLECT_STATE ? EventConstants.NormalClick.CANCELCOLLECT: EventConstants.NormalClick.COLLECT,       //按钮名称(如播放,音乐列表)
                            GsonUtil.toJson(setHotelTracker(mHotelBean)),          //对应id值(如音乐列表对应item id值)
                            EventConstants.PagePath.hotelCollectActivity,    //页面路径
                            EventConstants.PageDescribe.hotelCollectActivityPagePathDesc);//页面路径中午意思

                    mCollectState = mHotelBean.getStatus() == BaseCollectVM.HAVE_COLLECT_STATE ? BaseCollectVM.CANCLE_COLLECT_STATE : BaseCollectVM.HAVE_COLLECT_STATE;
                    mRecomHotelVM.collectItem(mCollectState, String.valueOf(mHotelBean.getHotelId()), COLLECT_HOTEL, GsonHelper.toJson(mHotelBean));
                }
            }
        });

        mRvCollect.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //继承了Activity的onTouchEvent方法，直接监听点击事件
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    //当手指按下的时候
                    x1 = event.getX();
                }
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    //当手指离开的时候
                    x2 = event.getX();
                    if(x1 - x2 > 50) {
                        if (isend){
                            if (!mRvCollect.canScrollHorizontally(1)) {
                                XMToast.showToast(HotelCollectActivity.this,getString(R.string.no_more_data));
                            }
                        }
                    }
                }
                return false;
            }
        });
        mHotelCollectAdapter.setEmptyView(emptyView);
        mHotelCollectAdapter.setEnableLoadMore(true);
        mHotelCollectAdapter.setLoadMoreView(new TravelLoadMoreView());
        mHotelCollectAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {

                if (!NetworkUtils.isConnected(HotelCollectActivity.this)) {
                    showToastException(R.string.net_work_error);
                    mHotelCollectAdapter.loadMoreFail();
                    return;
                }
                loadMore();
            }
        }, mRvCollect);
        mRvCollect.setAdapter(mHotelCollectAdapter);
        mXmScrollBar.setRecyclerView(mRvCollect);
    }


    private void loadMore() {
        if (mCollectVM == null) {
            return;
        }
        if (pageNo + 1 >= maxPageNum) {
            notifyLoadState(LauncherConstants.END);
        } else {
            pageNo++;
            mCollectVM.fetchCollectFilms(type,COLLECT_HOTEL, pageNo);
        }
    }

    private void notifyLoadState(int state) {
        if (LauncherConstants.COMPLETE == state) {
            mHotelCollectAdapter.loadMoreComplete();
        } else if (LauncherConstants.END == state) {
            isend = true;
            mHotelCollectAdapter.loadMoreEnd();
        } else if (LauncherConstants.FAILED == state) {
            mHotelCollectAdapter.loadMoreFail();
        }
    }

    private void initCollectVM() {
        type=getIntent().getStringExtra("type");
        mCollectVM = ViewModelProviders.of(this).get(CollectVM.class);
        mCollectVM.getCollectFilms().observe(this, new Observer<XmResource<CollectItems>>() {
            @Override
            public void onChanged(@Nullable XmResource<CollectItems> listXmResource) {
                if (listXmResource == null) {
                    return;
                }
                listXmResource.handle(new OnCallback<CollectItems>() {
                    @Override
                    public void onSuccess(CollectItems data) {
                        if (data.getPageInfo() != null) {
                            maxPageNum = data.getPageInfo().getTotalPage();
                        }
//                        mRvAdapter.setEnableLoadMore(hasMore);
                        if (!ListUtils.isEmpty(data.getCollections())) {
                            for (int i = 0; i < data.getCollections().size(); i++) {
                                initVMData(data.getCollections().get(i));
                            }
                        }
                        notifyLoadState(LauncherConstants.COMPLETE);
                        mRvCollect.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(String msg) {
                        super.onFailure(msg);
                        notifyLoadState(LauncherConstants.COMPLETE);
                        mRvCollect.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        if (!NetworkUtils.isConnected(this)) {
            showNoNetView();
            return;
        }

        mCollectVM.fetchCollectFilms(type,COLLECT_HOTEL, pageNo);
    }

    private void initRecomHoteVM() {
        mRecomHotelVM = ViewModelProviders.of(this).get(RecomHotelVM.class);
        mRecomHotelVM.getCollectItem().observe(this, new Observer<XmResource<String>>() {
            @Override
            public void onChanged(@Nullable XmResource<String> stringXmResource) {
                final String collectTip = getString(mCollectState == BaseCollectVM.HAVE_COLLECT_STATE ? R.string.collect : R.string.cancle_collect);
                if (stringXmResource == null) {
                    showToast(String.format(getString(R.string.collcet_operate_fail), collectTip));
                } else {
                    stringXmResource.handle(new OnCallback<String>() {
                        @Override
                        public void onSuccess(String data) {
                            showToast(String.format(getString(R.string.collcet_operate_success), collectTip));
                            mHotelBean.setStatus(mCollectState);
                            mHotelCollectAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(String msg) {
                            showToast(String.format(getString(R.string.collcet_operate_fail), collectTip));
                        }
                    });
                }
            }
        });

        mRecomHotelVM.getRoomStatus().observe(this, new Observer<XmResource<XMResult<String>>>() {
            @Override
            public void onChanged(@Nullable XmResource<XMResult<String>> xmResultXmResource) {
                xmResultXmResource.handle(new OnCallback<XMResult<String>>() {
                    @Override
                    public void onSuccess(XMResult<String> data) {
                        //预订酒店
                        BookHotelOneActivity.startBookHotel(HotelCollectActivity.this, mHotelBean, mStart, mEnd);
                    }

                    @Override
                    public void onError(int code, String message) {
                        XMToast.showToast(HotelCollectActivity.this, getString(R.string.hotel_no_room_message), getDrawable(R.drawable.toast_error));
                    }
                });
            }
        });
    }


    private void initVMData(CollectItems.CollectionsBean data) {
        HotelBean hotelBean = GsonHelper.fromJson(data.getCollectionJson(), HotelBean.class);
        hotelBean.setStatus(BaseCollectVM.HAVE_COLLECT_STATE);
        mHotelBeanList.add(hotelBean);
    }

    private void showNaviDialog(HotelBean hotelBean) {
        ConfirmDialog dialog = new ConfirmDialog(this);
        dialog.setContent(getString(R.string.travel_message_navi_hotel))
                .setPositiveButton(getString(R.string.travel_left_btn_navi),new XMAutoTrackerEventOnClickListener() {
                    @Override
                    public ItemEvent returnPositionEventMsg(View view) {
                        return new ItemEvent(EventConstants.NormalClick.HOTEL_CLLECT_NAVI_SURE,GsonUtil.toJson(setHotelTracker(mHotelBean)));
                    }

                    @Override
                    @BusinessOnClick
                    public void onClick(View v) {
                        dialog.dismiss();
                        int ret=MapManager.getInstance().startNaviToPoi(hotelBean.getHotelName(),hotelBean.getAddress(),Double.parseDouble(hotelBean.getLon()),Double.parseDouble(hotelBean.getLat()));
//                        if(ret==-1){
//                            showToast(R.string.im_guide);
//                        }
                    }
                })
                .setNegativeButton(getString(R.string.travel_right_btn),new XMAutoTrackerEventOnClickListener() {
                    @Override
                    public ItemEvent returnPositionEventMsg(View view) {
                        return new ItemEvent(EventConstants.NormalClick.HOTEL_CLLECT_NAVI_CANCEL,GsonUtil.toJson(setHotelTracker(mHotelBean)));
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
//        dialogMessage.setText(getString(R.string.travel_message_navi_hotel));
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
//                return new ItemEvent(EventConstants.NormalClick.HOTEL_CLLECT_NAVI_SURE,GsonUtil.toJson(setHotelTracker(mHotelBean)));
//            }
//
//            @Override
//            @BusinessOnClick
//            public void onClick(View v) {
//                builder.dismiss();
//                int ret=MapManager.getInstance().startNaviToPoi(hotelBean.getHotelName(),hotelBean.getAddress(),Double.parseDouble(hotelBean.getLon()),Double.parseDouble(hotelBean.getLat()));
//                if(ret==-1){
//                    showToast(R.string.im_guide);
//                }
//            }
//        });
//        cancelBtn.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
//            @Override
//            public ItemEvent returnPositionEventMsg(View view) {
//                return new ItemEvent(EventConstants.NormalClick.HOTEL_CLLECT_NAVI_CANCEL,GsonUtil.toJson(setHotelTracker(mHotelBean)));
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

    @SuppressLint("StringFormatInvalid")
    private void showPhoneDialog(HotelBean hotelBean) {
        if(hotelBean == null || TextUtils.isEmpty(hotelBean.getTelephone())){
            showToastException(R.string.no_found_number);
            return;
        }
        String message = String.format(getString(R.string.travel_message_phone_hotel), hotelBean.getTelephone());
        SpannableString s1 = new SpannableString(message);
        s1.setSpan(new AbsoluteSizeSpan(32, true), 0, s1.length() - hotelBean.getTelephone().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        s1.setSpan(new AbsoluteSizeSpan(28, true), s1.length() - hotelBean.getTelephone().length(), s1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ConfirmDialog dialog = new ConfirmDialog(this);
        dialog.setContent(s1)
                .setPositiveButton(getString(R.string.travel_left_btn_phone),new XMAutoTrackerEventOnClickListener() {
                    @Override
                    public ItemEvent returnPositionEventMsg(View view) {
                        return new ItemEvent(EventConstants.NormalClick.HOTEL_CLLECT_PHONE_SURE,GsonUtil.toJson(setHotelTracker(mHotelBean)));
                    }

                    @Override
                    @BusinessOnClick
                    public void onClick(View v) {
                        dialog.dismiss();
                        LauncherBlueToothPhoneManager.getInstance().callPhone(hotelBean.getTelephone());
                    }
                })
                .setNegativeButton(getString(R.string.travel_right_btn),new XMAutoTrackerEventOnClickListener() {
                    @Override
                    public ItemEvent returnPositionEventMsg(View view) {
                        return new ItemEvent(EventConstants.NormalClick.HOTEL_CLLECT_PHONE_CANCE,GsonUtil.toJson(setHotelTracker(mHotelBean)));
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
//        String message = String.format(getString(R.string.travel_message_phone_hotel), hotelBean.getTelephone());
//        SpannableString s1 = new SpannableString(message);
//        s1.setSpan(new AbsoluteSizeSpan(32, true), 0, s1.length() - hotelBean.getTelephone().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        s1.setSpan(new AbsoluteSizeSpan(28, true), s1.length() - hotelBean.getTelephone().length(), s1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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
//                return new ItemEvent(EventConstants.NormalClick.HOTEL_CLLECT_PHONE_SURE,GsonUtil.toJson(setHotelTracker(mHotelBean)));
//            }
//
//            @Override
//            @BusinessOnClick
//            public void onClick(View v) {
//                builder.dismiss();
//                LauncherBlueToothPhoneManager.getInstance().callPhone(hotelBean.getTelephone());
//            }
//        });
//        cancelBtn.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
//            @Override
//            public ItemEvent returnPositionEventMsg(View view) {
//                return new ItemEvent(EventConstants.NormalClick.HOTEL_CLLECT_PHONE_CANCE,GsonUtil.toJson(setHotelTracker(mHotelBean)));
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

    @Override
    protected void noNetworkOnRetry() {
        super.noNetworkOnRetry();
        if (NetworkUtils.isConnected(this)) {
            mCollectVM.fetchCollectFilms(type,COLLECT_HOTEL, pageNo);
        }
    }


    private CollectTrackerBean setHotelTracker(HotelBean hotelBean) {
        CollectTrackerBean collectTrackerBean = new CollectTrackerBean();
        collectTrackerBean.id = hotelBean.getHotelId();
        collectTrackerBean.value = hotelBean.getHotelName();
        collectTrackerBean.h = hotelBean.getStarName();  //酒店星级
        collectTrackerBean.i = String.valueOf(hotelBean.getDistance());
        collectTrackerBean.j = hotelBean.getMinPrice();
        return collectTrackerBean;
    }


}
