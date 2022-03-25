package com.xiaoma.launcher.travel.scenic.ui;

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
import com.xiaoma.launcher.travel.itemevent.CollectTrackerBean;
import com.xiaoma.launcher.travel.scenic.adapter.AttractionsCollectAdapter;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.trip.category.response.SearchStoreBean;
import com.xiaoma.trip.common.CollectItems;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmDividerDecoration;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author taojin
 * @date 2019/2/3
 */
@PageDescComponent(EventConstants.PageDescribe.attractionsActivityCollectPagePathDesc)
public class AttractionsCollectActivity extends BaseActivity {


    private AttractionsCollectAdapter mCollectAdapter;
    private List<SearchStoreBean> searchStoreBeans;


    public RecyclerView mRvCollect;
    private XmScrollBar mXmScrollBar;
    protected CollectVM mCollectVM;
    public static final String COLLECT_ATTR = "Attractions";
    public int mCollectState;
    public SearchStoreBean mCollectBean;
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
        initVM();
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
        tvTip.setText(getString(R.string.no_collection_attraction));
        searchStoreBeans = new ArrayList<>();

        mCollectAdapter = new AttractionsCollectAdapter(R.layout.adapter_collect_v1, searchStoreBeans, getString(R.string.btn_navi), getString(R.string.btn_phone));
        mCollectAdapter.setEmptyView(emptyView);
        mCollectAdapter.setEnableLoadMore(true);
        mCollectAdapter.setLoadMoreView(new TravelLoadMoreView());
        mCollectAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (!NetworkUtils.isConnected(AttractionsCollectActivity.this)) {
                    showToastException(R.string.net_work_error);
                    mCollectAdapter.loadMoreFail();
                    return;
                }

                loadMore();
            }
        }, mRvCollect);
        mCollectAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.collection_linear) {
                    if (!NetworkUtils.isConnected(AttractionsCollectActivity.this)) {
                        showToastException(R.string.net_work_error);
                        return;
                    }
                    mCollectBean = searchStoreBeans.get(position);
                    mCollectState = mCollectBean.getStatus() == BaseCollectVM.HAVE_COLLECT_STATE ? BaseCollectVM.CANCLE_COLLECT_STATE : BaseCollectVM.HAVE_COLLECT_STATE;
                    mCollectVM.collectItem(mCollectState, mCollectBean.getId(), COLLECT_ATTR, GsonHelper.toJson(mCollectBean));
                    XmAutoTracker.getInstance().onEvent(mCollectBean.getStatus() == BaseCollectVM.HAVE_COLLECT_STATE ? EventConstants.NormalClick.CANCELCOLLECT: EventConstants.NormalClick.COLLECT,       //按钮名称(如播放,音乐列表)
                            GsonUtil.toJson(setDeliciousTracker(searchStoreBeans.get(position))) ,          //对应id值(如音乐列表对应item id值)
                            EventConstants.PagePath.attractionsCollectActivityPath,    //页面路径
                            EventConstants.PageDescribe.attractionsActivityCollectPagePathDesc);//页面路径中午意思
                }else if (view.getId() == R.id.tv_action1){
                    XmAutoTracker.getInstance().onEvent( EventConstants.NormalClick.ATTRACTION_COLLECT_NAVIGATION,       //按钮名称(如播放,音乐列表)
                            GsonUtil.toJson(setDeliciousTracker(searchStoreBeans.get(position))),          //对应id值(如音乐列表对应item id值)
                            EventConstants.PagePath.attractionsCollectActivityPath,    //页面路径
                            EventConstants.PageDescribe.attractionsActivityCollectPagePathDesc);//页面路径中午意思
                    showNaviDialog(searchStoreBeans.get(position));
                }else if (view.getId() == R.id.tv_action2){
                    XmAutoTracker.getInstance().onEvent( EventConstants.NormalClick.ATTRACTION_COLLECT_PHONE,       //按钮名称(如播放,音乐列表)
                            GsonUtil.toJson(setDeliciousTracker(searchStoreBeans.get(position))),          //对应id值(如音乐列表对应item id值)
                            EventConstants.PagePath.attractionsCollectActivityPath,    //页面路径
                            EventConstants.PageDescribe.attractionsActivityCollectPagePathDesc);//页面路径中午意思
                    showPhoneDialog(searchStoreBeans.get(position));
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
                                XMToast.showToast(AttractionsCollectActivity.this,getString(R.string.no_more_data));
                            }
                        }
                    }
                }
                return false;
            }
        });
        mRvCollect.setAdapter(mCollectAdapter);
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
            mCollectVM.fetchCollectFilms(type,COLLECT_ATTR, pageNo);
        }
    }

    private void notifyLoadState(int state) {
        if (LauncherConstants.COMPLETE == state) {
            mCollectAdapter.loadMoreComplete();
        } else if (LauncherConstants.END == state) {
            isend = true;
            mCollectAdapter.loadMoreEnd();
        } else if (LauncherConstants.FAILED == state) {
            mCollectAdapter.loadMoreFail();
        }
    }

    protected void initVM() {
        type=getIntent().getStringExtra("type");
        mCollectVM = ViewModelProviders.of(this).get(CollectVM.class);
        mCollectVM.getCollectItem().observe(this, new Observer<XmResource<String>>() {
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
                            mCollectBean.setStatus(mCollectState);
                            mCollectAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(String msg) {
                            showToast(String.format(getString(R.string.collcet_operate_fail), collectTip));
                        }
                    });
                }
            }
        });
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
                        showToast("请求失败");
                        notifyLoadState(LauncherConstants.FAILED);
                        mRvCollect.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        if (!NetworkUtils.isConnected(this)) {
            showNoNetView();
            return;
        }

        mCollectVM.fetchCollectFilms(type,COLLECT_ATTR, pageNo);
    }

    public void initVMData(CollectItems.CollectionsBean data) {
        SearchStoreBean searchStoreBean = GsonHelper.fromJson(data.getCollectionJson(), SearchStoreBean.class);
        searchStoreBean.setStatus(BaseCollectVM.HAVE_COLLECT_STATE);
        searchStoreBeans.add(searchStoreBean);
    }

    @Override
    protected void noNetworkOnRetry() {
        super.noNetworkOnRetry();
        if (NetworkUtils.isConnected(this)) {
            mCollectVM.fetchCollectFilms(type,COLLECT_ATTR, pageNo);
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
                .setPositiveButton(getString(R.string.travel_left_btn_phone),new XMAutoTrackerEventOnClickListener() {
                    @Override
                    public ItemEvent returnPositionEventMsg(View view) {
                        return new ItemEvent(EventConstants.NormalClick.ATTRACTION_COLLECT_PHONE_SURE,  GsonUtil.toJson(setDeliciousTracker(searchStoreBean)));
                    }

                    @Override
                    @BusinessOnClick
                    public void onClick(View v) {
                        dialog.dismiss();
                        LauncherBlueToothPhoneManager.getInstance().callPhone(strings[0]);
                    }
                })
                .setNegativeButton(getString(R.string.travel_right_btn),new XMAutoTrackerEventOnClickListener() {
                    @Override
                    public ItemEvent returnPositionEventMsg(View view) {
                        return new ItemEvent(EventConstants.NormalClick.ATTRACTION_COLLECT_PHONE_CANCE,  GsonUtil.toJson(setDeliciousTracker(searchStoreBean)));
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
//        String[] strings = searchStoreBean.getPhone().split("\\/");
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
//                .setHeight(this.getResources().getDimensionPixelOffset(R.dimen.dialog_travel_height))
//                .create();
//        sureBtn.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
//            @Override
//            public ItemEvent returnPositionEventMsg(View view) {
//                return new ItemEvent(EventConstants.NormalClick.ATTRACTION_COLLECT_PHONE_SURE,  GsonUtil.toJson(setDeliciousTracker(searchStoreBean)));
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
//                return new ItemEvent(EventConstants.NormalClick.ATTRACTION_COLLECT_PHONE_CANCE,  GsonUtil.toJson(setDeliciousTracker(searchStoreBean)));
//            }
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
                .setPositiveButton(getString(R.string.travel_left_btn_navi),new XMAutoTrackerEventOnClickListener() {
                    @Override
                    public ItemEvent returnPositionEventMsg(View view) {
                        return new ItemEvent(EventConstants.NormalClick.ATTRACTION_COLLECT_NAVI_SURE,  GsonUtil.toJson(setDeliciousTracker(searchStoreBean)));
                    }

                    @Override
                    @BusinessOnClick
                    public void onClick(View v) {
                        dialog.dismiss();
                        int ret=MapManager.getInstance().startNaviToPoi(searchStoreBean.getName(),searchStoreBean.getAddress(),Double.parseDouble(searchStoreBean.getLng()),Double.parseDouble(searchStoreBean.getLat()));
//                        if(ret==-1){
//                            showToast(R.string.im_guide);
//                        }
                    }
                })
                .setNegativeButton(getString(R.string.travel_right_btn),new XMAutoTrackerEventOnClickListener() {
                    @Override
                    public ItemEvent returnPositionEventMsg(View view) {
                        return new ItemEvent(EventConstants.NormalClick.ATTRACTION_COLLECT_NAVI_CANCE,  GsonUtil.toJson(setDeliciousTracker(searchStoreBean)));
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
//                return new ItemEvent(EventConstants.NormalClick.ATTRACTION_COLLECT_NAVI_SURE,  GsonUtil.toJson(setDeliciousTracker(searchStoreBean)));
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
//                return new ItemEvent(EventConstants.NormalClick.ATTRACTION_COLLECT_NAVI_CANCE,  GsonUtil.toJson(setDeliciousTracker(searchStoreBean)));
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


    private CollectTrackerBean setDeliciousTracker(SearchStoreBean searchStoreBean) {
        CollectTrackerBean collectTrackerBean = new CollectTrackerBean();
        collectTrackerBean.id = searchStoreBean.getId();
        collectTrackerBean.value = searchStoreBean.getName();
        collectTrackerBean.h = searchStoreBean.getSubcate();
        collectTrackerBean.i = String.valueOf(searchStoreBean.getDistance());
        collectTrackerBean.j = String.valueOf(searchStoreBean.getAvgprice());
        return collectTrackerBean;
    }
}
