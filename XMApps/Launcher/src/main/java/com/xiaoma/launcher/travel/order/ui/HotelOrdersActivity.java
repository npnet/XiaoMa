package com.xiaoma.launcher.travel.order.ui;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.travel.order.ui
 *  @file_name:      HotelOrdersActivity
 *  @author:         Rookie
 *  @create_time:    2019/1/28 14:29
 *  @description：   TODO             */

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.map.manager.MapManager;
import com.xiaoma.launcher.travel.order.adapter.HotelOrdersAdapter;
import com.xiaoma.launcher.travel.order.view.OrderLoadMoreView;
import com.xiaoma.launcher.travel.order.vm.HotelOrdersVM;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.trip.movie.response.CompleteOrderBean;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmDividerDecoration;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.List;
@PageDescComponent(EventConstants.PageDescribe.HotelOrdersActivityPagePathDesc)
public class HotelOrdersActivity extends BaseActivity {

    private RecyclerView rvHotelOrders;
    private XmScrollBar scrollBar;
    private HotelOrdersVM mHotelOrdersVM;
    private List<CompleteOrderBean.ListBean> mList = new ArrayList<>();
    private HotelOrdersAdapter mAdapter;
    private int pageNum = 1;
    private int maxPageNum;
    private boolean isend = false;
    //手指按下的点为(x1, y1)手指离开屏幕的点为(x2, y2)
    float x1 = 0;
    float x2 = 0;
    private String type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_orders);
        initView();
        initData();
    }


    private void initView() {
        rvHotelOrders = findViewById(R.id.rv_hotel_orders);
        scrollBar = findViewById(R.id.scroll_bar);
        rvHotelOrders.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        XmDividerDecoration decor = new XmDividerDecoration(this, DividerItemDecoration.HORIZONTAL);
        int right = 60;
        int extra = 50;
        decor.setRect(0, 0, right, 0);
        decor.setExtraMargin(extra);
        rvHotelOrders.addItemDecoration(decor);
        mList = new ArrayList<>();
        mAdapter = new HotelOrdersAdapter();
        //设置空布局
        View emptyView = View.inflate(this, R.layout.empty_view, null);
        TextView tvTip = emptyView.findViewById(R.id.tv_tips);
        tvTip.setText(String.format(getString(R.string.no_order), getString(R.string.hotel)));
        mAdapter.setEmptyView(emptyView);
        mAdapter.setEnableLoadMore(true);
        mAdapter.setLoadMoreView(new OrderLoadMoreView());
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadMore();
            }
        }, rvHotelOrders);

        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId()==R.id.btn_guide){
                    XmAutoTracker.getInstance().onEvent( EventConstants.NormalClick.HOTEL_ORDER_ITEM_NAVI,       //按钮名称(如播放,音乐列表)
                            String.valueOf(mList.get(position).getId()),          //对应id值(如音乐列表对应item id值)
                            EventConstants.PagePath.HotelOrdersActivity,    //页面路径
                            EventConstants.PageDescribe.HotelOrdersActivityPagePathDesc);//页面路径中午意思
                    showNaviDialog(mList.get(position));
                }
            }
        });
        rvHotelOrders.setOnTouchListener(new View.OnTouchListener() {
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
                            if (!rvHotelOrders.canScrollHorizontally(1)) {
                                XMToast.showToast(HotelOrdersActivity.this,getString(R.string.no_more_data));
                            }
                        }
                    }
                }
                return false;
            }
        });
        rvHotelOrders.setAdapter(mAdapter);
        scrollBar.setRecyclerView(rvHotelOrders);
    }

    private void initData() {
        type=getIntent().getStringExtra("type");
        mHotelOrdersVM = ViewModelProviders.of(this).get(HotelOrdersVM.class);
        mHotelOrdersVM.getCompleteHotelOrders().observe(this, new Observer<XmResource<CompleteOrderBean>>() {
            @Override
            public void onChanged(@Nullable XmResource<CompleteOrderBean> listXmResource) {
                listXmResource.handle(new OnCallback<CompleteOrderBean>() {
                    @Override
                    public void onSuccess(CompleteOrderBean data) {
                        if (!ListUtils.isEmpty(data.getAppOrders())) {
                            maxPageNum = data.getPageInfo().getTotalPage();
                            mList.addAll(data.getAppOrders());
                            mAdapter.addData(data.getAppOrders());
                            notifyLoadState(LauncherConstants.COMPLETE);
                        }
                        rvHotelOrders.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(int code, String msg) {
                        super.onFailure(msg);
                        notifyLoadState(LauncherConstants.FAILED);
                        rvHotelOrders.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        mHotelOrdersVM.queryCompleteHotelOrders(type,pageNum);
    }

    private void loadMore() {
        if (mHotelOrdersVM == null) {
            return;
        }
        if (pageNum >= maxPageNum) {
            notifyLoadState(LauncherConstants.END);
        } else {
            pageNum++;
            mHotelOrdersVM.queryCompleteHotelOrders(type,pageNum);
        }
    }

    private void notifyLoadState(int state) {
        if (LauncherConstants.COMPLETE == state) {
            mAdapter.loadMoreComplete();
        } else if (LauncherConstants.END == state) {
            isend = true;
            mAdapter.loadMoreEnd();
        } else if (LauncherConstants.FAILED == state) {
            mAdapter.loadMoreFail();
        }
        KLog.d(String.format("LoadState is %s", state));
    }

    private void showNaviDialog(CompleteOrderBean.ListBean listBean) {
        ConfirmDialog dialog = new ConfirmDialog(this);
        dialog.setContent(getString(R.string.travel_message_navi_hotel))
                .setPositiveButton(getString(R.string.travel_left_btn_navi),new View.OnClickListener() {
                    @Override
                    @NormalOnClick({EventConstants.NormalClick.HOTEL_ORDER_ITEM_NAVI_SURE})
                    public void onClick(View v) {
                        dialog.dismiss();
                        int ret=MapManager.getInstance().startNaviToPoi(listBean.getOrderName(),listBean.getAddress(),Double.parseDouble(listBean.getLon()),Double.parseDouble(listBean.getLat()));
//                        if(ret==-1){
//                            showToast(R.string.im_guide);
//                        }
                    }
                })
                .setNegativeButton(getString(R.string.travel_right_btn),new View.OnClickListener() {
                    @Override
                    @NormalOnClick({EventConstants.NormalClick.HOTEL_ORDER_ITEM_NAVI_CANCE})
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
//        sureBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            @NormalOnClick({EventConstants.NormalClick.HOTEL_ORDER_ITEM_NAVI_SURE})
//            public void onClick(View v) {
//                builder.dismiss();
//                int ret=MapManager.getInstance().startNaviToPoi(listBean.getOrderName(),listBean.getAddress(),Double.parseDouble(listBean.getLon()),Double.parseDouble(listBean.getLat()));
//                if(ret==-1){
//                    showToast(R.string.im_guide);
//                }
//            }
//        });
//        cancelBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            @NormalOnClick({EventConstants.NormalClick.HOTEL_ORDER_ITEM_NAVI_CANCE})
//            public void onClick(View v) {
//                builder.dismiss();
//            }
//        });
//        builder.show();
    }
}
