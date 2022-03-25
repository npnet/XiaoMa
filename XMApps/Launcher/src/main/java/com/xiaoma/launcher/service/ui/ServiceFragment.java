package com.xiaoma.launcher.service.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.autotracker.listener.XmTrackerOnTabSelectedListener;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.common.manager.TopManager;
import com.xiaoma.launcher.common.model.SelectModeBean;
import com.xiaoma.launcher.common.views.SelectModelDialog;
import com.xiaoma.launcher.schedule.utils.DateUtil;
import com.xiaoma.launcher.service.adapter.ServiceAdapter;
import com.xiaoma.launcher.service.model.NewServiceBean;
import com.xiaoma.launcher.service.vm.ServiceFragmentVM;
import com.xiaoma.launcher.travel.car.ui.NearByOilParkActivity;
import com.xiaoma.launcher.travel.delicious.ui.DeliciousActivity;
import com.xiaoma.launcher.travel.delicious.ui.DeliciousCollectActivity;
import com.xiaoma.launcher.travel.delicious.ui.DeliciousSortActivity;
import com.xiaoma.launcher.travel.film.ui.FilmActivity;
import com.xiaoma.launcher.travel.film.ui.FilmCollectActivity;
import com.xiaoma.launcher.travel.film.ui.NearbyCinemaListActivity;
import com.xiaoma.launcher.travel.hotel.ui.HotelCollectActivity;
import com.xiaoma.launcher.travel.hotel.ui.RecomHotelActivity;
import com.xiaoma.launcher.travel.order.ui.HotelOrdersActivity;
import com.xiaoma.launcher.travel.order.ui.MovieOrdersActivity;
import com.xiaoma.launcher.travel.scenic.ui.AttractionsActivity;
import com.xiaoma.launcher.travel.scenic.ui.AttractionsCollectActivity;
import com.xiaoma.launcher.travel.scenic.ui.AttractionsSortActivity;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.AppUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.LaunchUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Thomas on 2019/1/10 0010
 */
@PageDescComponent(EventConstants.PageDescribe.ServiceFragmentPagePathDesc)
public class ServiceFragment extends BaseFragment {

    public static final String TAG = "ServiceFragment";
    private static final String PACKAGE_NAME = "com.xiaoma.service";
    private RecyclerView mRvService;
    private TabLayout mTabLayout;
    private String[] titles;
    private ServiceFragment.ViewHolder mHolder;
    private ServiceAdapter mServiceAdapter;
    private ServiceFragmentVM mServiceFragmentVM;
    private LinearLayoutManager mLayoutManager;
    private final int FOOTER_VIEW_WIDTH = 700;
    private boolean isTabClick;
    private final String POI_TYPE = "poi_type";
    private final String OIL = "加油站";
    private final String PARK = "停车场";

    public static final String NEAR_BY_GAS_STATION = "NearbyGasStation";
    public static final String NEAR_BY_GAS_STATION_PAGE = "NearbyGasStationPage";
    public static final String NEAR_BY_PARKING_LOT = "NearbyParkingLot";
    public static final String NEAR_BY_PARKING_LOT_PAGE = "NearbyParkingLotPage";
    public static final String MAINTENANCE_PERIOD = "MaintenancePeriod";
    public static final String MAINTENANCE_PERIOD_PAGE = "MaintenancePeriodPage";

    public static final String NEAR_BY_FOOD = "NearbyFood";
    public static final String NEAR_BY_FOOD_PAGE = "NearbyFoodPage";
    public static final String FOOD_CATEGORY = "FoodCategory";
    public static final String FOOD_CATEGORY_PAGE = "FoodCategoryPage";
    public static final String FOOD_COLLECTION = "FoodCollection";
    public static final String FOOD_COLLECTION_PAGE = "FoodCollectionPage";

    public static final String NEAR_BY_CINEMA = "NearbyCinema";
    public static final String NEAR_BY_CINEMA_PAGE = "NearbyCinemaPage";
    public static final String POPULAR_CINEMA = "PopularCinema";
    public static final String POPULAR_CINEMA_PAGE = "PopularCinemaPage";
    public static final String FILM_ORDER = "FilmOrder";
    public static final String FILM_ORDER_PAGE = "FilmOrderPage";
    public static final String FILM_COLLECTION = "FilmCollection";
    public static final String FILM_COLLECTION_PAGE = "FilmCollectionPage";

    public static final String NEAR_BY_HOTEL = "NearbyHotel";
    public static final String NEAR_BY_HOTEL_PAGE = "NearbyHotelPage";
    public static final String HOTEL_ORDER = "HotelOrder";
    public static final String HOTEL_ORDER_PAGE = "HotelOrderPage";
    public static final String HOTEL_COLLECTION = "HotelCollection";
    public static final String HOTEL_COLLECTION_PAGE = "HotelCollectionPage";

    public static final String NEAR_BY_ATTRACTION = "NearbyAttraction";
    public static final String NEAR_BY_ATTRACTION_PAGE = "NearbyAttractionPage";
    public static final String ATTRACTION_CATEGORY = "AttractionCategory";
    public static final String ATTRACTION_CATEGORY_PAGE = "AttractionCategoryPage";
    public static final String ATTRACTION_COLLECTION = "AttractionCollection";
    public static final String ATTRACTION_COLLECTION_PAGE = "AttractionCollectionPage";

    //选中的tab
    private int selectTab;
    //标题List
    private List<String> MODE_TYPE_TITLE = new ArrayList<>();
    private List<Integer> MODE_TYPE_LENGTH = new ArrayList<>();
    private List<NewServiceBean.ItemsBean> mItemsBeans = new ArrayList<>();
    private int titlePosition = 0;
    private TextView mTvRecom;


    public static ServiceFragment newInstance() {
        return new ServiceFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateWrapView(inflater.inflate(R.layout.fragment_service, container, false));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(view);
        initView();
        initData();
    }


    private void bindView(View view) {
        mRvService = view.findViewById(R.id.service_rv);
        mRvService.setHasFixedSize(true);
        mRvService.setItemAnimator(null);

        mTabLayout = view.findViewById(R.id.service_tab);
        mTvRecom = view.findViewById(R.id.tv_recom);
        mTabLayout.setVisibility(View.GONE);
        mTvRecom.setVisibility(View.GONE);
        mTvRecom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showModelDialog();
            }
        });
    }


    private void showModelDialog() {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                SelectModelDialog selectModelDialog = new SelectModelDialog(mContext);
                selectModelDialog.show();
            }
        });
    }


    private void initView() {
        titles = getResources().getStringArray(R.array.home_service_info);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        setupTabView(mTabLayout);
        mTabLayout.addOnTabSelectedListener(new XmTrackerOnTabSelectedListener() {
            private CharSequence mTabText;

            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(mTabText.toString(), "0");
            }

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (mRvService.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
                    //recycleView正在滑动
                    mRvService.stopScroll();
                }
                mTabText = changeTab(tab);
                isTabClick = true;
                if (tab.getPosition() == 0) {
                    smoothMoveToPosition(mRvService, 0);
                } else if (tab.getPosition() == 1) {
                    smoothMoveToPosition(mRvService, MODE_TYPE_LENGTH.size() >= 1 ? MODE_TYPE_LENGTH.get(1) : 0);
                } else if (tab.getPosition() == 2) {
                    smoothMoveToPosition(mRvService, MODE_TYPE_LENGTH.size() >= 2 ? MODE_TYPE_LENGTH.get(2) : 0);
                } else if (tab.getPosition() == 3) {
                    smoothMoveToPosition(mRvService, MODE_TYPE_LENGTH.size() >= 3 ? MODE_TYPE_LENGTH.get(3) : 0);
                } else {
                    smoothMoveToPosition(mRvService, MODE_TYPE_LENGTH.size() >= 4 ? MODE_TYPE_LENGTH.get(4) : 0);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (mRvService.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
                    //recycleView正在滑动
                    mRvService.stopScroll();
                }
                isTabClick = true;
                if (tab.getPosition() == 0) {
                    smoothMoveToPosition(mRvService, 0);
                } else if (tab.getPosition() == 1) {
                    smoothMoveToPosition(mRvService, MODE_TYPE_LENGTH.size() >= 1 ? MODE_TYPE_LENGTH.get(1) : 0);
                } else if (tab.getPosition() == 2) {
                    smoothMoveToPosition(mRvService, MODE_TYPE_LENGTH.size() >= 2 ? MODE_TYPE_LENGTH.get(2) : 0);
                } else if (tab.getPosition() == 3) {
                    smoothMoveToPosition(mRvService, MODE_TYPE_LENGTH.size() >= 3 ? MODE_TYPE_LENGTH.get(3) : 0);
                } else {
                    smoothMoveToPosition(mRvService, MODE_TYPE_LENGTH.size() >= 4 ? MODE_TYPE_LENGTH.get(4) : 0);
                }
                mTabText = changeTab(tab);
            }
        });

        mServiceAdapter = new ServiceAdapter(mItemsBeans, ImageLoader.with(this));
        View view = new View(getActivity());
        view.setLayoutParams(new ViewGroup.LayoutParams(FOOTER_VIEW_WIDTH, mRvService.getHeight()));
        mServiceAdapter.addFooterView(view, -1, LinearLayout.HORIZONTAL);
        mLayoutManager = new TopManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        mRvService.setLayoutManager(mLayoutManager);
        mRvService.setAdapter(mServiceAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mRvService.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    int firstVisiblePos = mLayoutManager.findFirstVisibleItemPosition();

                    if (firstVisiblePos < (MODE_TYPE_LENGTH.size() >= 1 ? MODE_TYPE_LENGTH.get(1) : 0)) {
                        selectTab = 0;
                    } else if (firstVisiblePos < (MODE_TYPE_LENGTH.size() >= 2 ? MODE_TYPE_LENGTH.get(2) : 0)) {
                        selectTab = 1;
                    } else if (firstVisiblePos < (MODE_TYPE_LENGTH.size() >= 3 ? MODE_TYPE_LENGTH.get(3) : 0)) {
                        selectTab = 2;
                    } else if (firstVisiblePos < (MODE_TYPE_LENGTH.size() >= 4 ? MODE_TYPE_LENGTH.get(4) : 0)) {
                        selectTab = 3;
                    } else {
                        selectTab = 4;
                    }
                    if (!isTabClick) {
                        mTabLayout.setScrollPosition(selectTab, 0, true);
                        changeTab(mTabLayout.getTabAt(selectTab));
                    }
                    if (mShouldScroll && mToPosition - mLayoutManager.findFirstVisibleItemPosition() <= 4) {
                        mShouldScroll = false;
                        smoothMoveToPosition(mRvService, mToPosition);
                    }
                }
            });

            mRvService.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    isTabClick = false;
                    return false;
                }
            });
        }

        mServiceAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mainAudioAdapterOnItemClick(position, false);
            }
        });

        EventBus.getDefault().register(this);
    }

    /**
     * 滑动到指定位置
     *
     * @param mRecyclerView
     * @param position
     */
    private void smoothMoveToPosition(RecyclerView mRecyclerView, final int position) {
        // 第一个可见位置
        int firstItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(0));
        // 最后一个可见位置
        int lastItem = mRecyclerView.getChildLayoutPosition(mRecyclerView.getChildAt(mRecyclerView.getChildCount() - 1));
        if (position < firstItem) {
            // 第一种可能:跳转位置在第一个可见位置之前
            mRecyclerView.smoothScrollToPosition(position);

        } else if (position <= lastItem) {
            // 第二种可能:跳转位置在第一个可见位置之后
            int movePosition = position - firstItem;
            if (movePosition >= 0 && movePosition < mRecyclerView.getChildCount()) {
                int left = mRecyclerView.getChildAt(movePosition).getLeft();
                mRecyclerView.smoothScrollBy(left, 0, new LinearInterpolator());
            }

        } else {
            // 第三种可能:跳转位置在最后可见项之后
            mRecyclerView.smoothScrollToPosition(position);
            mToPosition = position;
            mShouldScroll = true;
        }
    }

    /**
     * 列表项点击
     *
     * @param position
     * @param isRecommand
     */
    private void mainAudioAdapterOnItemClick(int position, boolean isRecommand) {
        NewServiceBean.ItemsBean itemsBean = mServiceAdapter.getData().get(position);
        String type = itemsBean.getType();
        String pageType = itemsBean.getPageType();
        if (pageType == null) {
            return;
        }
        if (MODE_TYPE_TITLE.contains(pageType)) {
            return;
        }
        switch (pageType) {
            case NEAR_BY_GAS_STATION_PAGE:
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.SERVICE_ITEM_GAS,
                        "",
                        EventConstants.PagePath.ServiceFragment,
                        EventConstants.PageDescribe.ServiceFragmentPagePathDesc);
                Intent gasIntent = new Intent(mContext, NearByOilParkActivity.class);
                gasIntent.putExtra("type", type);
                gasIntent.putExtra(POI_TYPE, OIL);
                startActivity(gasIntent);
                break;
            case NEAR_BY_PARKING_LOT_PAGE:
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.SERVICE_ITEM_PARKING,
                        "",
                        EventConstants.PagePath.ServiceFragment,
                        EventConstants.PageDescribe.ServiceFragmentPagePathDesc);
                Intent parkIntent = new Intent(mContext, NearByOilParkActivity.class);
                parkIntent.putExtra("type", type);
                parkIntent.putExtra(POI_TYPE, PARK);
                startActivity(parkIntent);
                break;
            case MAINTENANCE_PERIOD_PAGE:
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.SERVICE_ITEM_CAR_MAINTENANCE,
                        "",
                        EventConstants.PagePath.ServiceFragment,
                        EventConstants.PageDescribe.ServiceFragmentPagePathDesc);
                if (AppUtils.isAppInstalled(getContext(), PACKAGE_NAME)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("type", type);
                    LaunchUtils.launchApp(getContext(), PACKAGE_NAME, bundle);
                } else {
                    XMToast.showToast(getContext(), getContext().getString(R.string.app_no_install_tip));
                }
                break;
            case NEAR_BY_FOOD_PAGE:
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.SERVICE_ITEM_DELICIOUS,
                        "",
                        EventConstants.PagePath.ServiceFragment,
                        EventConstants.PageDescribe.ServiceFragmentPagePathDesc);
                Intent intent1 = new Intent(mContext, DeliciousActivity.class);
                intent1.putExtra("type", type);
                startActivity(intent1);
                break;
            case FOOD_CATEGORY_PAGE:
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.SERVICE_ITEM_DELICIOUS_SORT,
                        "",
                        EventConstants.PagePath.ServiceFragment,
                        EventConstants.PageDescribe.ServiceFragmentPagePathDesc);
                Intent intent2 = new Intent(mContext, DeliciousSortActivity.class);
                intent2.putExtra("type", type);
                startActivity(new Intent(intent2));
                break;
            case FOOD_COLLECTION_PAGE:
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.SERVICE_ITEM_DELICIOUS_COLLECT,
                        "",
                        EventConstants.PagePath.ServiceFragment,
                        EventConstants.PageDescribe.ServiceFragmentPagePathDesc);
                Intent intent3 = new Intent(mContext, DeliciousCollectActivity.class);
                intent3.putExtra("type", type);
                startActivity(intent3);
                break;
            case NEAR_BY_CINEMA_PAGE:
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.SERVICE_ITEM_FILM,
                        "",
                        EventConstants.PagePath.ServiceFragment,
                        EventConstants.PageDescribe.ServiceFragmentPagePathDesc);
                Intent intent4 = new Intent(mContext, NearbyCinemaListActivity.class);
                intent4.putExtra("type", type);
                startActivity(intent4);
                break;
            case POPULAR_CINEMA_PAGE:
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.SERVICE_ITEM_CINEMA,
                        "",
                        EventConstants.PagePath.ServiceFragment,
                        EventConstants.PageDescribe.ServiceFragmentPagePathDesc);
                Intent intent5 = new Intent(mContext, FilmActivity.class);
                intent5.putExtra("type", type);
                startActivity(intent5);
                break;
            case FILM_ORDER_PAGE:
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.SERVICE_ITEM_FILM_ORDER,
                        "",
                        EventConstants.PagePath.ServiceFragment,
                        EventConstants.PageDescribe.ServiceFragmentPagePathDesc);
                Intent intent6 = new Intent(mContext, MovieOrdersActivity.class);
                intent6.putExtra("type", type);
                startActivity(intent6);
                break;
            case FILM_COLLECTION_PAGE:
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.SERVICE_ITEM_FILM_COOLECT,
                        "",
                        EventConstants.PagePath.ServiceFragment,
                        EventConstants.PageDescribe.ServiceFragmentPagePathDesc);
                Intent intent7 = new Intent(mContext, FilmCollectActivity.class);
                intent7.putExtra("type", type);
                startActivity(intent7);
                break;
            case NEAR_BY_HOTEL_PAGE:
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.SERVICE_ITEM_HOTEL,
                        "",
                        EventConstants.PagePath.ServiceFragment,
                        EventConstants.PageDescribe.ServiceFragmentPagePathDesc);
                Intent intent8 = new Intent(mContext, RecomHotelActivity.class);
                intent8.putExtra("type", type);
                startActivity(intent8);
                break;
            case HOTEL_ORDER_PAGE:
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.SERVICE_ITEM_HOTEL_ORDER,
                        "",
                        EventConstants.PagePath.ServiceFragment,
                        EventConstants.PageDescribe.ServiceFragmentPagePathDesc);
                Intent intent9 = new Intent(mContext, HotelOrdersActivity.class);
                intent9.putExtra("type", type);
                startActivity(intent9);
                break;
            case HOTEL_COLLECTION_PAGE:
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.SERVICE_ITEM_HOTEL_COOLECT,
                        "",
                        EventConstants.PagePath.ServiceFragment,
                        EventConstants.PageDescribe.ServiceFragmentPagePathDesc);
                Intent intent10 = new Intent(mContext, HotelCollectActivity.class);
                intent10.putExtra("type", type);
                startActivity(intent10);
                break;
            case NEAR_BY_ATTRACTION_PAGE:
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.SERVICE_ITEM_ATTRACTIONS,
                        "",
                        EventConstants.PagePath.ServiceFragment,
                        EventConstants.PageDescribe.ServiceFragmentPagePathDesc);
                Intent intent11 = new Intent(mContext, AttractionsActivity.class);
                intent11.putExtra("type", type);
                startActivity(intent11);
                break;
            case ATTRACTION_CATEGORY_PAGE:
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.SERVICE_ITEM_ATTRACTIONS_SORT,
                        "",
                        EventConstants.PagePath.ServiceFragment,
                        EventConstants.PageDescribe.ServiceFragmentPagePathDesc);
                Intent intent12 = new Intent(mContext, AttractionsSortActivity.class);
                intent12.putExtra("type", type);
                startActivity(intent12);
                break;
            case ATTRACTION_COLLECTION_PAGE:
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.SERVICE_ITEM_ATTRACTIONS_COLLECT,
                        "",
                        EventConstants.PagePath.ServiceFragment,
                        EventConstants.PageDescribe.ServiceFragmentPagePathDesc);
                Intent intent13 = new Intent(mContext, AttractionsCollectActivity.class);
                intent13.putExtra("type", type);
                startActivity(intent13);
                break;
            default:
                break;
        }
    }

    //目标项是否在最后一个可见项之后
    private boolean mShouldScroll;
    //记录目标项位置
    private int mToPosition;

    private void initData() {
        mServiceFragmentVM = ViewModelProviders.of(this).get(ServiceFragmentVM.class);
        mServiceFragmentVM.getmServicesList().observe(this, new Observer<XmResource<List<NewServiceBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<NewServiceBean>> listXmResource) {
                if (listXmResource == null) {
                    return;
                }
                listXmResource.handle(new OnCallback<List<NewServiceBean>>() {
                    @Override
                    public void onSuccess(List<NewServiceBean> data) {
                        mTabLayout.setVisibility(View.VISIBLE);
                        mTvRecom.setVisibility(View.VISIBLE);
                        ThreadDispatcher.getDispatcher().postOnMain(() -> wrapServiceInfoData(data));
                    }

                    @Override
                    public void onFailure(String msg) {
                        showNoNetView();
                        mTabLayout.setVisibility(View.GONE);
                        mTvRecom.setVisibility(View.GONE);
                    }
                });
            }
        });
        String json = TPUtils.get(getContext(), ServiceFragmentVM.CACHE_SERVICE_LIST_KEY, "");
        KLog.e(TAG, "initData() json= " + json);
        if (!StringUtil.isEmpty(json)) {
            //数据不为空先显示视图
            List<NewServiceBean> data = GsonHelper.fromJsonToList(json, NewServiceBean[].class);
            mServiceFragmentVM.getmServicesList().setValue(XmResource.success(data));
        }
        boolean isNeedPeriod = TPUtils.get(getContext(), LauncherConstants.IS_NEED_PERIOD, false);
        String str = isNeedPeriod ? getString(R.string.fragment_car_service_need_period) : getString(R.string.fragment_car_service_no_need_period);
        mServiceAdapter.setMaintenance(str);
        mServiceFragmentVM.fetchServiceList();
    }

    private void wrapServiceInfoData(List<NewServiceBean> data) {
        try {
            MODE_TYPE_LENGTH.clear();
            MODE_TYPE_TITLE.clear();
            mItemsBeans.clear();
            titlePosition = 0;
            for (int i = 0; i < data.size(); i++) {
                MODE_TYPE_TITLE.add(data.get(i).getName());
                MODE_TYPE_LENGTH.add(i, titlePosition);
                titlePosition += data.get(i).getItems().size() + 1;
                NewServiceBean.ItemsBean titleItemBean = new NewServiceBean.ItemsBean();
                titleItemBean.setId(data.get(i).getId());
                titleItemBean.setName(data.get(i).getName());
                titleItemBean.setCategoryName(data.get(i).getCategoryName());
                titleItemBean.setImgUrl(data.get(i).getImgUrl());
                titleItemBean.setType(data.get(i).getCategoryName());
                titleItemBean.setParentId(data.get(i).getParentId());
                titleItemBean.setCreateDate(data.get(i).getCreateDate());
                titleItemBean.setModifyDate(data.get(i).getModifyDate());
                titleItemBean.setChannelId(data.get(i).getChannelId());
                titleItemBean.setEnableStatus(data.get(i).getEnableStatus());
                titleItemBean.setItemType(i + 1);
                mItemsBeans.add(titleItemBean);
                mItemsBeans.addAll(data.get(i).getItems());
            }
            KLog.i(TAG, "mItemsBeans=" + mItemsBeans);
            mServiceAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setupTabView(TabLayout tabLayout) {
        for (int i = 0; i < titles.length; i++) {
            mTabLayout.addTab(mTabLayout.newTab(), false);
        }
        for (int i = 0; i < titles.length; i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(R.layout.service_tab_layout_item);
                View view = tab.getCustomView();
                if (view != null) {
                    mHolder = new ServiceFragment.ViewHolder(view);
                    mHolder.tabTv.setText(titles[i]);
                }
            }
            if (i == 0) {
                mHolder.tabTv.setSelected(true);
                mHolder.tabTv.setTextAppearance(mContext, R.style.text_view_light_blue_service);
                mHolder.tabTv.setBackgroundResource(R.drawable.title_select);
            }
        }
    }

    private String changeTab(TabLayout.Tab tab) {
        if (tab == null) return "";
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab1 = mTabLayout.getTabAt(i);
            View customView = tab1.getCustomView();
            mHolder = new ServiceFragment.ViewHolder(customView);
            mHolder.tabTv.setBackgroundResource(R.color.transparent);
            mHolder.tabTv.setTextAppearance(getActivity(), R.style.text_view_normal_service);
        }
        View customView = tab.getCustomView();
        if (customView == null) {
            return "";
        }
        mHolder = new ServiceFragment.ViewHolder(customView);
        mHolder.tabTv.setTextAppearance(getActivity(), R.style.text_view_light_blue_service);
        mHolder.tabTv.setBackgroundResource(R.drawable.title_select);
        return mHolder.tabTv.getText().toString();
    }

    @Override
    public void onResume() {
        super.onResume();
        setTvRecomText();
    }

    @Subscriber(tag = LauncherConstants.REFRESH_RECOM)
    public void refreshRecom(int recomType) {
        if (recomType == LauncherConstants.LIVE_MODEL) {
            mTvRecom.setText(R.string.life_mode);
        } else if (recomType == LauncherConstants.WORK_MODEL) {
            mTvRecom.setText(R.string.work_mode);
        } else if (recomType == LauncherConstants.TRAVEL_MODEL) {
            mTvRecom.setText(R.string.travel_mode);
        } else {
            mTvRecom.setText(R.string.need_quiet);
        }
    }

    private void setTvRecomText() {
        int model = TPUtils.get(mContext, LauncherConstants.CAR_MODEL, LauncherConstants.LIVE_MODEL);
        if (model == LauncherConstants.QUIET_MODEL) {
            //如果上次保存的是静静模式，就不需要赋值判断
            mTvRecom.setText(R.string.need_quiet);
        } else {
            int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
            //周一~周五，默认为工作模式；周六~周日，默认为休闲模式
            if (day == Calendar.SUNDAY || day == Calendar.SATURDAY) {
                model = initModeByDay(LauncherConstants.CAR_MODEL_REST, LauncherConstants.LIVE_MODEL);
            } else {
                model = initModeByDay(LauncherConstants.CAR_MODEL_NORMAL, LauncherConstants.WORK_MODEL);
            }
            if (model == LauncherConstants.LIVE_MODEL) {
                mTvRecom.setText(R.string.life_mode);
            } else if (model == LauncherConstants.WORK_MODEL) {
                mTvRecom.setText(R.string.work_mode);
            } else {
                mTvRecom.setText(R.string.travel_mode);
            }
        }
        //保存下当前的模式
        TPUtils.put(mContext, LauncherConstants.CAR_MODEL, model);
    }

    private int initModeByDay(String carModel, int liveModel) {
        int model;
        SelectModeBean selectModeBean = TPUtils.getObject(mContext, carModel, SelectModeBean.class);
        if (selectModeBean == null) {
            model = liveModel;
        } else {
            //因为calendar 一周是从周日到周六，所以不能判断周六周日是否是同一周
            if (LauncherConstants.CAR_MODEL_NORMAL.equals(carModel)) {
                if (DateUtil.isSameWeekWithToday(selectModeBean.getCalendar(), Calendar.getInstance())) {
                    model = selectModeBean.getModel();
                } else {
                    model = liveModel;
                }
            } else {
                if (DateUtil.getTimeDistance(selectModeBean.getCalendar().getTime(), Calendar.getInstance().getTime()) <= 1) {
                    model = selectModeBean.getModel();
                } else {
                    model = liveModel;
                }
            }
        }
        return model;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    class ViewHolder {
        TextView tabTv;

        ViewHolder(View tabView) {
            tabTv = tabView.findViewById(R.id.view_tab_tv);
        }
    }

    private void startNeayOilParkActivity(String poiType, String type) {
        Intent intent = new Intent(mContext, NearByOilParkActivity.class);
        intent.putExtra(POI_TYPE, poiType);
        intent.putExtra("type", type);
        startActivity(intent);
    }

    @Override
    protected void noNetworkOnRetry() {
        super.noNetworkOnRetry();
        KLog.d(TAG, "noNetworkOnRetry()");
        if (NetworkUtils.isConnected(getActivity())) {
            boolean isNeedPeriod = TPUtils.get(getContext(), LauncherConstants.IS_NEED_PERIOD, false);
            String str = isNeedPeriod ? getString(R.string.fragment_car_service_need_period) : getString(R.string.fragment_car_service_no_need_period);
            mServiceAdapter.setMaintenance(str);
            mServiceFragmentVM.fetchServiceList();
        } else {
            mServiceFragmentVM.getmServicesList().setValue(XmResource.failure("-1"));
        }
    }

    @Subscriber(tag = LauncherConstants.IS_FUEL_WARNING)
    public void isFuelWaring(int active) {
        KLog.d("ServiceFragment", "isFuelWaring" + active);
        mServiceAdapter.notifyDataSetChanged();
    }

    @Subscriber(tag = LauncherConstants.PERIOD_STATE)
    public void periodState(boolean active) {
        KLog.d("ServiceFragment", "periodState= " + active);
        String str = active ? getString(R.string.fragment_car_service_need_period) : getString(R.string.fragment_car_service_no_need_period);
        mServiceAdapter.setMaintenance(str);
        mServiceAdapter.notifyDataSetChanged();
    }
}
