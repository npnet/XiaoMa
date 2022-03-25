package com.xiaoma.launcher.travel.order.ui;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.travel.order.ui
 *  @file_name:      MovieOrdersActivity
 *  @author:         Rookie
 *  @create_time:    2019/1/28 14:38
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
import com.xiaoma.launcher.travel.order.adapter.MovieOrdersAdapter;
import com.xiaoma.launcher.travel.order.view.OrderLoadMoreView;
import com.xiaoma.launcher.travel.order.vm.MovieOrdersVM;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.trip.movie.response.CompleteOrderBean;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmDividerDecoration;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.ListUtils;

import java.util.List;

@PageDescComponent(EventConstants.PageDescribe.MovieOrdersActivityPagePathDesc)
public class MovieOrdersActivity extends BaseActivity {

    private RecyclerView rvMovieOrders;
    private XmScrollBar scrollBar;
    private MovieOrdersVM mMovieOrdersVM;
    private MovieOrdersAdapter mAdapter;
    private int pageNum = 1;
    private static final int PAGE_SIZE = 20;
    private boolean isend = false;
    //手指按下的点为(x1, y1)手指离开屏幕的点为(x2, y2)
    float x1 = 0;
    float x2 = 0;
    private String type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_orders);
        initView();
        initData();
    }


    private void initView() {
        rvMovieOrders = findViewById(R.id.rv_movie_orders);
        scrollBar = findViewById(R.id.scroll_bar);
        rvMovieOrders.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        XmDividerDecoration decor = new XmDividerDecoration(this, DividerItemDecoration.HORIZONTAL);
        int right = 60;
        int extra = 50;
        decor.setRect(0, 0, right, 0);
        decor.setExtraMargin(extra);
        rvMovieOrders.addItemDecoration(decor);
        mAdapter = new MovieOrdersAdapter(R.layout.item_movie_order);
        View emptyView = View.inflate(this, R.layout.empty_view, null);
        TextView tvTip = emptyView.findViewById(R.id.tv_tips);
        tvTip.setText(String.format(getString(R.string.no_order), getString(R.string.move)));
        mAdapter.setEmptyView(emptyView);
        mAdapter.setEnableLoadMore(true);
        mAdapter.setLoadMoreView(new OrderLoadMoreView());
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadMore();
            }
        }, rvMovieOrders);
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.btn_guide) {
                    XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.MOVE_ORDER_ITEM_NAVI,       //按钮名称(如播放,音乐列表)
                            String.valueOf(mAdapter.getData().get(position).getId()),          //对应id值(如音乐列表对应item id值)
                            EventConstants.PagePath.MovieOrdersActivity,    //页面路径
                            EventConstants.PageDescribe.MovieOrdersActivityPagePathDesc);//页面路径中午意思
                    showNaviDialog(mAdapter.getData().get(position));
                }
            }
        });
        rvMovieOrders.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //继承了Activity的onTouchEvent方法，直接监听点击事件
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //当手指按下的时候
                    x1 = event.getX();
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //当手指离开的时候
                    x2 = event.getX();
                    if (x1 - x2 > 50) {
                        if (isend) {
                            if (!rvMovieOrders.canScrollHorizontally(1)) {
                                XMToast.showToast(MovieOrdersActivity.this, getString(R.string.no_more_data));
                            }
                        }
                    }
                }
                return false;
            }
        });
        rvMovieOrders.setAdapter(mAdapter);
        scrollBar.setRecyclerView(rvMovieOrders);
    }

    private void initData() {
        type = getIntent().getStringExtra("type");
        mMovieOrdersVM = ViewModelProviders.of(this).get(MovieOrdersVM.class);
        mMovieOrdersVM.getMovieData().observe(this, new Observer<XmResource<List<CompleteOrderBean.ListBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<CompleteOrderBean.ListBean>> listXmResource) {
                if (listXmResource == null) {
                    return;
                }
                listXmResource.handle(new OnCallback<List<CompleteOrderBean.ListBean>>() {
                    @Override
                    public void onSuccess(List<CompleteOrderBean.ListBean> data) {
                        if (ListUtils.isEmpty(data)) {
                            notifyLoadState(LauncherConstants.COMPLETE);
                        } else {
                            mAdapter.addData(data);

                        }
                        rvMovieOrders.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(String msg) {
                        super.onFailure(msg);
                        notifyLoadState(LauncherConstants.FAILED);
                        rvMovieOrders.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        mMovieOrdersVM.fetchMovieOrders(type, pageNum, PAGE_SIZE);
    }

    private void loadMore() {
        if (mMovieOrdersVM == null) {
            return;
        }
        if (mMovieOrdersVM.isCineMaLoadEnd()) {
            notifyLoadState(LauncherConstants.END);
        } else {
            pageNum++;
            mMovieOrdersVM.fetchMovieOrders(type, pageNum, PAGE_SIZE);
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
    }

    private void showNaviDialog(CompleteOrderBean.ListBean filmBean) {
        ConfirmDialog dialog = new ConfirmDialog(this);
        dialog.setContent(getString(R.string.travel_message_navi_cinema))
                .setPositiveButton(getString(R.string.travel_left_btn_navi), new View.OnClickListener() {
                    @Override
                    @NormalOnClick({EventConstants.NormalClick.MOVE_ORDER_ITEM_NAVI_SURE})
                    public void onClick(View v) {
                        dialog.dismiss();
                        int ret = MapManager.getInstance().startNaviToPoi(filmBean.getOrderName(), filmBean.getAddress(), Double.parseDouble(filmBean.getLon()), Double.parseDouble(filmBean.getLat()));
//                        if (ret == -1) {
//                            showToast(R.string.im_guide);
//                        }
                    }
                })
                .setNegativeButton(getString(R.string.travel_right_btn), new View.OnClickListener() {
                    @Override
                    @NormalOnClick({EventConstants.NormalClick.MOVE_ORDER_ITEM_NAVI_CANCE})
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
//        sureBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            @NormalOnClick({EventConstants.NormalClick.MOVE_ORDER_ITEM_NAVI_SURE})
//            public void onClick(View v) {
//                builder.dismiss();
//                int ret=MapManager.getInstance().startNaviToPoi(filmBean.getOrderName(),filmBean.getAddress(),Double.parseDouble(filmBean.getLon()),Double.parseDouble(filmBean.getLat()));
//                if(ret==-1){
//                    showToast(R.string.im_guide);
//                }
//            }
//        });
//        cancelBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            @NormalOnClick({EventConstants.NormalClick.MOVE_ORDER_ITEM_NAVI_CANCE})
//            public void onClick(View v) {
//                builder.dismiss();
//            }
//        });
//        builder.show();
    }

}
