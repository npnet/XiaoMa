package com.xiaoma.launcher.travel.hotel.ui;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.travel.hotel
 *  @file_name:      RecomHotelActivity
 *  @author:         Rookie
 *  @create_time:    2019/1/2 14:36
 *  @description：   酒店推荐             */

import android.annotation.SuppressLint;
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
import android.util.Log;
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
import com.xiaoma.launcher.travel.hotel.adapter.RecomHotelAdapter;
import com.xiaoma.launcher.travel.hotel.calendar.DateBean;
import com.xiaoma.launcher.travel.hotel.constants.HotelConstants;
import com.xiaoma.launcher.travel.hotel.vm.RecomHotelVM;
import com.xiaoma.launcher.travel.itemevent.CollectTrackerBean;
import com.xiaoma.launcher.travel.itemevent.HotelBookTrackerBean;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.mapadapter.model.SearchAddressInfo;
import com.xiaoma.mapadapter.ui.MapActivity;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.trip.hotel.response.HotelBean;
import com.xiaoma.trip.hotel.response.HotelPageDataBean;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.TimeUtils;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.Calendar;
@PageDescComponent(EventConstants.PageDescribe.RecomHotelActivityPagePathDesc)
public class RecomHotelActivity extends BaseActivity implements DiscreteScrollView.OnItemChangedListener<RecyclerView.ViewHolder> {
    private static String TAG="[RecomHotelActivity]";
    private DiscreteScrollView mScrollView;
    private RecomHotelVM mRecomHotelVM;
    private RecomHotelAdapter mRecomHotelAdapter;
    private LinearLayout llDate;
    private TextView tvCheckIn;
    private TextView tvCheckOut;
    private Button mBtnGuide;
    private Button mBtnCall;
    private Button mBtnBook;
    private TextView mTvHotelName;
    private TextView mTvHotelMsg;
    private TextView tvNearByHotel;
    private RelativeLayout mNotdataView;
    private ArrayList<HotelBean> mHotelList = new ArrayList<>();
    private HotelBean hotelBean;
    private static final float MIN_SCALE = 0.8f;

    private static final int REQUEST_CODE = 200;
    private static final int REQUEST_MAP_CODE = 210;
    public static final String START_TIME = "starttime";
    public static final String END_TIME = "endtime";
    private DateBean mStartTime;
    private DateBean mEndTime;
    private String start;
    private String end;
    private int pageNo = 1;
    private int maxPageNum = -1;
    private HotelBean mCollectBean;
    private int collectPosition;
    private int mCollectState;

    //true 代表为推荐酒店 false 走正常的接口请求
    private boolean isRecommend = false;
    private LocationInfo mLocationInfo;
    private SearchAddressInfo mMAddressInfo;
    private static final String EXTRA_LOCATION_DATA = "location";
    private String type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recom_hotel);
        initView();
        initData();
    }

    private void initView() {
        mLocationInfo = LocationManager.getInstance().getCurrentLocation();
        getNaviBar().getMiddleView().setImageDrawable(getResources().getDrawable(R.drawable.icon_location));
        getNaviBar().getMiddleView().setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick({EventConstants.NormalClick.HOTEL_RENEW_POSITION})
            public void onClick(View v) {
                startActivityForResult(new Intent(RecomHotelActivity.this, MapActivity.class), REQUEST_MAP_CODE);
            }
        });
        mNotdataView = findViewById(R.id.notdata_view);
        mScrollView = findViewById(R.id.scrollview_recom);
        mBtnGuide = findViewById(R.id.btn_guide);
        mBtnCall = findViewById(R.id.btn_call);
        mBtnBook = findViewById(R.id.btn_book);
        llDate = findViewById(R.id.ll_date);
        tvCheckIn = findViewById(R.id.tv_check_in);
        tvCheckOut = findViewById(R.id.tv_check_out);
        mTvHotelName = findViewById(R.id.tv_hotel_name);
        mTvHotelMsg = findViewById(R.id.tv_hotel_msg);
        tvNearByHotel = findViewById(R.id.tv_nearby_hotel);

        setDate(TimeUtils.getCurrDate(), TimeUtils.getNextDate());
        mRecomHotelAdapter = new RecomHotelAdapter();

        mScrollView.setOrientation(DSVOrientation.HORIZONTAL);
        mScrollView.addOnItemChangedListener(this);
        //加载更多
        mScrollView.addScrollStateChangeListener(new DiscreteScrollView.ScrollStateChangeListener<RecyclerView.ViewHolder>() {
            @Override
            public void onScrollStart(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {
            }

            @Override
            public void onScrollEnd(@NonNull RecyclerView.ViewHolder currentItemHolder, int adapterPosition) {
                if (adapterPosition == mHotelList.size() && pageNo >= maxPageNum) {
                    showToast(R.string.no_more_data);
                    mScrollView.smoothScrollToPosition(adapterPosition - 1);
                } else if (adapterPosition == mHotelList.size()) {
//                    showToast(R.string.no_more_data);
                    mScrollView.scrollToPosition(adapterPosition - 1);
                }
            }

            @Override
            public void onScroll(float scrollPosition, int currentPosition, int newPosition, @Nullable RecyclerView.ViewHolder currentHolder, @Nullable RecyclerView.ViewHolder newCurrent) {
            }
        });
        mScrollView.setSlideOnFling(true);
        mScrollView.setItemTransformer(new ScaleTransformer.Builder()
                .setMinScale(MIN_SCALE)
                .build());

        mRecomHotelAdapter.setEnableLoadMore(true);
//        mRecomHotelAdapter.setLoadMoreView(new HotelLoadMoreView());
        mRecomHotelAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadMore();
            }
        }, mScrollView);
        mRecomHotelAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mScrollView.smoothScrollToPosition(position);
            }
        });
        mRecomHotelAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                //收藏
                if (view.getId() == R.id.tv_collect) {
                    if (!NetworkUtils.isConnected(RecomHotelActivity.this)) {
                        showToastException(R.string.net_work_error);
                        return;
                    }
                    showProgressDialogNoMsg();
                    mCollectBean = mRecomHotelAdapter.getData().get(position);
                    collectPosition = position;
                    mCollectState = mCollectBean.getStatus() == BaseCollectVM.HAVE_COLLECT_STATE ? BaseCollectVM.CANCLE_COLLECT_STATE : BaseCollectVM.HAVE_COLLECT_STATE;
                    mRecomHotelVM.collectItem(mCollectState, mCollectBean.getHotelId(), LauncherConstants.COLLECT_HOTEL, GsonHelper.toJson(mCollectBean));

                    XmAutoTracker.getInstance().onEvent(mCollectBean.getStatus() == BaseCollectVM.HAVE_COLLECT_STATE ? EventConstants.NormalClick.CANCELCOLLECT: EventConstants.NormalClick.COLLECT,       //按钮名称(如播放,音乐列表)
                            GsonUtil.toJson(setHotelTracker(mCollectBean)),          //对应id值(如音乐列表对应item id值)
                            EventConstants.PagePath.recomHotelActivityPath,    //页面路径
                            EventConstants.PageDescribe.RecomHotelActivityPagePathDesc);//页面路径中午意思

                }
            }
        });
        mScrollView.setAdapter(mRecomHotelAdapter);

        llDate.setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick({EventConstants.NormalClick.HOTEL_CHECKIN_TIME})
            public void onClick(View v) {
                //选择入住日期
                Intent intent = new Intent(RecomHotelActivity.this, SelectDateActivity.class);

                if (mStartTime != null && mEndTime != null) {
                    intent.putExtra(START_TIME, mStartTime);
                    intent.putExtra(END_TIME, mEndTime);
                }else{
                    setNormal();
                    intent.putExtra(START_TIME, mStartTime);
                    intent.putExtra(END_TIME, mEndTime);
                }
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        mBtnGuide.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(EventConstants.NormalClick.HOTEL_NAVI,GsonUtil.toJson(setHotelTracker(hotelBean)));
            }

            @Override
            @BusinessOnClick
            public void onClick(View v) {
                showNaviDialog();

            }
        });
        mBtnCall.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(EventConstants.NormalClick.HOTEL_PHONE,GsonUtil.toJson(setHotelTracker(hotelBean)));
            }

            @Override
            @BusinessOnClick
            public void onClick(View v) {
                showPhoneDialog();
            }
        });
        mBtnBook.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(EventConstants.NormalClick.HOTEL_BOOK,GsonUtil.toJson(setHotelBookTracker(hotelBean)));
            }

            @Override
            @BusinessOnClick
            public void onClick(View v) {
                if (hotelBean == null) {
                    return;
                }

                if (!NetworkUtils.isConnected(RecomHotelActivity.this)) {
                    XMToast.toastException(RecomHotelActivity.this, getString(R.string.net_work_error), false);
                    return;
                }
                if (hotelBean != null) {
                    mRecomHotelVM.fetchHotelRoomStatus(hotelBean.getHotelId(), start, end);
                }
            }
        });
    }

    private void initData() {
        isRecommend = getIntent().getBooleanExtra(HotelConstants.HOTEL_DATA_RECOMMEND_TYPE, false);
        type=getIntent().getStringExtra("type");
        if (isRecommend) {
            tvNearByHotel.setText(getString(R.string.recommend_hotel));
        }

        mRecomHotelVM = ViewModelProviders.of(this).get(RecomHotelVM.class);
        mRecomHotelVM.getHotelData().observe(this, new Observer<XmResource<HotelPageDataBean>>() {

            @Override
            public void onChanged(@Nullable XmResource<HotelPageDataBean> hotelPageDataBeanXmResource) {
                if (hotelPageDataBeanXmResource == null) {
                    return;
                }
                hotelPageDataBeanXmResource.handle(new OnCallback<HotelPageDataBean>() {
                    @Override
                    public void onSuccess(HotelPageDataBean data) {
                        if (!ListUtils.isEmpty(data.getHotelList()) && data.getHotelList().size() > 0) {
                            if (data.getPageInfo() != null) {
                                maxPageNum = data.getPageInfo().getTotalPage();
                            }
                            mHotelList.addAll(data.getHotelList());
                            mRecomHotelAdapter.addData(data.getHotelList());
                            if (pageNo == 1) {
                                if (mHotelList.size() >= 3) {
                                    mScrollView.scrollToPosition(2);
                                } else {
                                    mScrollView.scrollToPosition(mHotelList.size() - 1);
                                }

                                if (!ListUtils.isEmpty(mHotelList)) {
                                    setViewVisible(View.VISIBLE);
                                }
                            }
                        } else {
                            if (pageNo == 1) {
                                mNotdataView.setVisibility(View.VISIBLE);
                            }
                        }
                        notifyLoadState(LauncherConstants.COMPLETE);
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onFailure(msg);
                        notifyLoadState(LauncherConstants.FAILED);
                        if (code == LauncherConstants.LOCATION_ERROR) {
                            showToast(msg);
                        }

                    }
                });
            }
        });

        mRecomHotelVM.getRoomStatus().observe(this, new Observer<XmResource<XMResult<String>>>() {
            @Override
            public void onChanged(@Nullable XmResource<XMResult<String>> xmResultXmResource) {
                xmResultXmResource.handle(new OnCallback<XMResult<String>>() {
                    @Override
                    public void onSuccess(XMResult<String> data) {
                        //预订酒店
                        BookHotelOneActivity.startBookHotel(RecomHotelActivity.this, hotelBean, start, end);
                    }

                    @Override
                    public void onError(int code, String message) {
                        XMToast.showToast(RecomHotelActivity.this, getString(R.string.hotel_no_room_message), getDrawable(R.drawable.toast_error));
                    }
                });
            }
        });

        mRecomHotelVM.getCollectItem().observe(this, new Observer<XmResource<String>>() {
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
                            mRecomHotelAdapter.getData().set(collectPosition, mCollectBean);
                            mRecomHotelAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(String msg) {
                            showToast(String.format(getString(R.string.collcet_operate_fail), collectTip));
                        }
                    });
                }
            }
        });

        String[] date = TimeUtils.getCurrAndNextDate().split(" ");
        start = date[0];
        end = date[1];

        setViewVisible(View.INVISIBLE);
        if (isRecommend) {
            mRecomHotelVM.fetchRecommendByHotel();
        } else {

            if (!NetworkUtils.isConnected(this)) {
                showNoNetView();
                return;
            }

            if (mLocationInfo == null) {
                XMToast.showToast(getApplication(), R.string.not_nvi_info);
                return;
            }
            mRecomHotelVM.fetchNearByHotel(type,mLocationInfo.getCity(), String.valueOf(mLocationInfo.getLatitude()), String.valueOf(mLocationInfo.getLongitude()), start, end, pageNo);
        }
    }

    private void loadMore() {
        KLog.e(TAG,"loadMore() ");
        if (mRecomHotelVM == null) {
            return;
        }
        KLog.e(TAG,"loadMore() pageNo= "+pageNo+" , maxPageNum= "+maxPageNum);
        if (pageNo >= maxPageNum) {
            notifyLoadState(LauncherConstants.END);
        } else {
            pageNo++;
            KLog.e(TAG,"loadMore() mMAddressInfo= "+mMAddressInfo);
            if (mMAddressInfo != null && mMAddressInfo.latLonPoint != null) {
                if (mMAddressInfo != null && mMAddressInfo.latLonPoint != null) {
                    mRecomHotelVM.fetchNearByHotel(type,mMAddressInfo.city, String.valueOf(mMAddressInfo.latLonPoint.getLatitude()), String.valueOf(mMAddressInfo.latLonPoint.getLongitude()), start, end, pageNo);
                }
            } else {
                KLog.e(TAG,"loadMore() mLocationInfo= "+mLocationInfo);
                if (mLocationInfo == null) {
                    XMToast.showToast(getApplication(), R.string.not_nvi_info);
                    return;
                }
                if (!NetworkUtils.isConnected(this)) {
                    showToastException(R.string.net_work_error);
                    notifyLoadState(LauncherConstants.FAILED);
                    return;
                }
                mRecomHotelVM.fetchNearByHotel(type,mLocationInfo.getCity(), String.valueOf(mLocationInfo.getLatitude()), String.valueOf(mLocationInfo.getLongitude()), start, end, pageNo);
            }
        }
    }

    private void notifyLoadState(int state) {
        if (LauncherConstants.COMPLETE == state) {
            mRecomHotelAdapter.loadMoreComplete();
        } else if (LauncherConstants.END == state) {
            mRecomHotelAdapter.loadMoreEnd();
        } else if (LauncherConstants.FAILED == state) {
            mRecomHotelAdapter.loadMoreFail();
        }
        KLog.d(String.format("LoadState is %s", state));
    }




    //设置默认日期
    private void setNormal() {
        Calendar calendar = Calendar.getInstance();
        //获取系统的日期
        //年
        int year = calendar.get(Calendar.YEAR);
        //月
        int currMonth = calendar.get(Calendar.MONTH) + 1;
        //日
        int currDay = calendar.get(Calendar.DAY_OF_MONTH);
        mStartTime = new DateBean();
        mStartTime.setSolar(year,currMonth,currDay);
        mEndTime = new DateBean();
        mEndTime.setSolar(year,currMonth,currDay+1);
    }

    @Override
    public void onCurrentItemChanged(@Nullable RecyclerView.ViewHolder viewHolder, int adapterPosition) {
//        int positionInDataSet = mInfiniteAdapter.getRealPosition(adapterPosition);
//        hotelBean = mHotelList.get(positionInDataSet);
        if (ListUtils.isEmpty(mHotelList)) {
            return;
        }

        String landMark = "";
        if (adapterPosition < mHotelList.size()) {

            XmAutoTracker.getInstance().onEvent( EventConstants.NormalClick.LISTITEM,       //按钮名称(如播放,音乐列表)
                    GsonUtil.toJson(setHotelTracker(mHotelList.get(adapterPosition))),          //对应id值(如音乐列表对应item id值)
                    EventConstants.PagePath.recomHotelActivityPath,    //页面路径
                    EventConstants.PageDescribe.RecomHotelActivityPagePathDesc);//页面路径中午意思

            hotelBean = mHotelList.get(adapterPosition);
            mTvHotelName.setText(hotelBean.getHotelName());
            if (!ListUtils.isEmpty(hotelBean.getLandmark())) {
                HotelBean.LandmarkBean landmarkBean = hotelBean.getLandmark().get(0);
                landMark = landmarkBean.getLandName();
            }
            mTvHotelMsg.setText(String.format(getString(R.string.hotel_landmark), StringUtil.isEmpty(landMark) ? getString(R.string.hotel_no_land) : landMark, "酒店", String.valueOf(hotelBean.getDistance()), hotelBean.getStartPrice()));
            Log.d("onCurrentItemChanged", "adapterPosition : " + adapterPosition);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            setViewVisible(View.INVISIBLE);
            pageNo = 1;
            mHotelList.clear();
            mRecomHotelAdapter.getData().clear();
            if (requestCode == REQUEST_CODE) {
                mStartTime = data.getParcelableExtra(START_TIME);
                mEndTime = data.getParcelableExtra(END_TIME);
                int[] startSolar = mStartTime.getSolar();
                int[] endSolar = mEndTime.getSolar();


                setDate(TimeUtils.getDate(startSolar[0], startSolar[1], startSolar[2]), TimeUtils.getDate(endSolar[0], endSolar[1], endSolar[2]));
                start = String.format(getString(R.string.date_str_format), startSolar[0], startSolar[1], startSolar[2]);
                end = String.format(getString(R.string.date_str_format), endSolar[0], endSolar[1], endSolar[2]);

                //如果是推荐酒店，不需要重新更新酒店数据
                if (isRecommend) {
                    return;
                }
                if (mMAddressInfo != null && mMAddressInfo.latLonPoint != null) {
                    mRecomHotelVM.fetchNearByHotel(type,mMAddressInfo.city, String.valueOf(mMAddressInfo.latLonPoint.getLatitude()), String.valueOf(mMAddressInfo.latLonPoint.getLongitude()), start, end, pageNo);
                } else {

                    if (mLocationInfo == null) {
                        XMToast.showToast(getApplication(), R.string.not_nvi_info);
                        return;
                    }

                    if (!NetworkUtils.isConnected(this)) {
                        showToastException(R.string.net_work_error);
                        notifyLoadState(LauncherConstants.FAILED);
                        return;
                    }

                    mRecomHotelVM.fetchNearByHotel(type,mLocationInfo.getCity(), String.valueOf(mLocationInfo.getLatitude()), String.valueOf(mLocationInfo.getLongitude()), start, end, pageNo);
                }

            } else if (requestCode == REQUEST_MAP_CODE) {

                mMAddressInfo = data.getParcelableExtra(EXTRA_LOCATION_DATA);
                if (mMAddressInfo != null && mMAddressInfo.latLonPoint != null) {
                    mRecomHotelVM.fetchNearByHotel(type,mMAddressInfo.city, String.valueOf(mMAddressInfo.latLonPoint.getLatitude()), String.valueOf(mMAddressInfo.latLonPoint.getLongitude()), start, end, pageNo);
                }

            }
        }
    }

    public void setViewVisible(int viewVisible) {
        mScrollView.setVisibility(viewVisible);
        mTvHotelName.setVisibility(viewVisible);
        mTvHotelMsg.setVisibility(viewVisible);
        mBtnBook.setVisibility(viewVisible);
        mBtnCall.setVisibility(viewVisible);
        mBtnGuide.setVisibility(viewVisible);
        llDate.setVisibility(viewVisible);


    }

    private void showNaviDialog() {
        ConfirmDialog dialog = new ConfirmDialog(this);
        dialog.setContent(getString(R.string.travel_message_navi_hotel))
                .setPositiveButton(getString(R.string.travel_left_btn_navi),new XMAutoTrackerEventOnClickListener() {
                    @Override
                    public ItemEvent returnPositionEventMsg(View view) {
                        return new ItemEvent(EventConstants.NormalClick.HOTEL_NAVI_SURE,GsonUtil.toJson(setHotelTracker(hotelBean)));
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
                        return new ItemEvent(EventConstants.NormalClick.HOTEL_NAVI_CANCEL,GsonUtil.toJson(setHotelTracker(hotelBean)));
                    }

                    @Override
                    @BusinessOnClick
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                })
                .show();


//
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
//                return new ItemEvent(EventConstants.NormalClick.HOTEL_NAVI_SURE,GsonUtil.toJson(setHotelTracker(hotelBean)));
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
//                return new ItemEvent(EventConstants.NormalClick.HOTEL_NAVI_CANCEL,GsonUtil.toJson(setHotelTracker(hotelBean)));
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
    private void showPhoneDialog() {
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
                        return new ItemEvent(EventConstants.NormalClick.HOTEL_PHONE_SURE,GsonUtil.toJson(setHotelTracker(hotelBean)));
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
                        return new ItemEvent(EventConstants.NormalClick.HOTEL_PHONE_CANCE,GsonUtil.toJson(setHotelTracker(hotelBean)));
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
//                .setHeight(this.getResources().getDimensionPixelOffset(R.dimen.dialog_travel_height_350))
//                .create();
//        sureBtn.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
//            @Override
//            public ItemEvent returnPositionEventMsg(View view) {
//                return new ItemEvent(EventConstants.NormalClick.HOTEL_PHONE_SURE,GsonUtil.toJson(setHotelTracker(hotelBean)));
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
//                return new ItemEvent(EventConstants.NormalClick.HOTEL_PHONE_CANCE,GsonUtil.toJson(setHotelTracker(hotelBean)));
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
            mRecomHotelVM.fetchNearByHotel(type,mLocationInfo.getCity(), String.valueOf(mLocationInfo.getLatitude()), String.valueOf(mLocationInfo.getLongitude()), start, end, pageNo);
        }
    }
    private void setDate(String checkIn, String checkOut) {
        tvCheckIn.setText(checkIn);
        tvCheckOut.setText(checkOut);
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

    private HotelBookTrackerBean setHotelBookTracker(HotelBean hotelBean) {
        HotelBookTrackerBean hotelBookTrackerBean = new HotelBookTrackerBean();
        hotelBookTrackerBean.id = hotelBean.getHotelId();
        hotelBookTrackerBean.value = hotelBean.getHotelName();
        hotelBookTrackerBean.h = hotelBean.getStarName();  //酒店星级
        hotelBookTrackerBean.i = String.valueOf(hotelBean.getDistance());
        hotelBookTrackerBean.j = hotelBean.getMinPrice();
        hotelBookTrackerBean.k = start;
        hotelBookTrackerBean.l = end;
        return hotelBookTrackerBean;
    }
}
