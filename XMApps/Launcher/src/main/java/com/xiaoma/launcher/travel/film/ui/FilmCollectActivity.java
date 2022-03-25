package com.xiaoma.launcher.travel.film.ui;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.travel.film.ui
 *  @file_name:
 *  @author:         Rookie
 *  @create_time:    2019/2/21 14:25
 *  @description：   TODO             */

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.xiaoma.launcher.common.views.TravelLoadMoreView;
import com.xiaoma.launcher.common.vm.BaseCollectVM;
import com.xiaoma.launcher.map.manager.MapManager;
import com.xiaoma.launcher.travel.film.adapter.FilmCollectAdapter;
import com.xiaoma.launcher.travel.film.vm.CollectVM;
import com.xiaoma.launcher.travel.itemevent.CinemaTrackerBean;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.trip.common.CollectItems;
import com.xiaoma.trip.movie.response.NearbyCinemasDetailsBean;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmDividerDecoration;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.tputils.TPUtils;

import java.util.ArrayList;
import java.util.List;

@PageDescComponent(EventConstants.PageDescribe.FilmCollectActivityPagePathDesc)
public class FilmCollectActivity extends BaseActivity {


    private List<NearbyCinemasDetailsBean> mNearbyCinemasDetailsList;
    private FilmCollectAdapter mFilmCollectAdapter;


    public RecyclerView mRvCollect;
    private XmScrollBar mXmScrollBar;
    protected CollectVM mCollectVM;
    public static final String COLLECT_FILM = "Film";
    public int mCollectState;
    public NearbyCinemasDetailsBean mNearbyCinemasDetailsBean;
    private int pageNo = 0;
    private int maxPageNum;
    private boolean isend = false;
    //手指按下的点为(x1, y1)手指离开屏幕的点为(x2, y2)
    float x1 = 0;
    float x2 = 0;
    private TextView mCollectTitle;
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
        mCollectTitle = findViewById(R.id.collect_title);
        mCollectTitle.setText(R.string.myself_collect);
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
        tvTip.setText(getString(R.string.no_collection_cinema));
        mNearbyCinemasDetailsList = new ArrayList<>();

        mFilmCollectAdapter = new FilmCollectAdapter(R.layout.adapter_collect_v1, mNearbyCinemasDetailsList, getString(R.string.right_now_buy_film_ticket), getString(R.string.btn_navi));
        mFilmCollectAdapter.setEmptyView(emptyView);
        mFilmCollectAdapter.setEnableLoadMore(true);
        mFilmCollectAdapter.setLoadMoreView(new TravelLoadMoreView());
        mFilmCollectAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (!NetworkUtils.isConnected(FilmCollectActivity.this)) {
                    showToastException(R.string.net_work_error);
                    mFilmCollectAdapter.loadMoreFail();
                    return;
                }
                loadMore();
            }
        }, mRvCollect);
        mFilmCollectAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.tv_collection) {
                    if (!NetworkUtils.isConnected(FilmCollectActivity.this)) {
                        showToastException(R.string.net_work_error);
                        return;
                    }
                    mNearbyCinemasDetailsBean = mNearbyCinemasDetailsList.get(position);
                    mCollectState = mNearbyCinemasDetailsBean.getStatus() == BaseCollectVM.HAVE_COLLECT_STATE ? BaseCollectVM.CANCLE_COLLECT_STATE : BaseCollectVM.HAVE_COLLECT_STATE;
                    mCollectVM.collectItem(mCollectState, String.valueOf(mNearbyCinemasDetailsBean.getCinemaId()), COLLECT_FILM, GsonHelper.toJson(mNearbyCinemasDetailsBean));
                    XmAutoTracker.getInstance().onEvent(mNearbyCinemasDetailsBean.getStatus() == BaseCollectVM.HAVE_COLLECT_STATE ? EventConstants.NormalClick.CANCELCOLLECT : EventConstants.NormalClick.COLLECT,       //按钮名称(如播放,音乐列表)
                            GsonUtil.toJson(setCinemaTracker(mNearbyCinemasDetailsBean)),          //对应id值(如音乐列表对应item id值)
                            EventConstants.PagePath.filmCollectActivityPath,    //页面路径
                            EventConstants.PageDescribe.FilmCollectActivityPagePathDesc);//页面路径中午意思

                } else if (view.getId() == R.id.tv_action1) {
                    mNearbyCinemasDetailsBean = mNearbyCinemasDetailsList.get(position);
                    XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.FILM_COLLECT_BUY_TICKETS,       //按钮名称(如播放,音乐列表)
                            GsonUtil.toJson(setCinemaTracker(mNearbyCinemasDetailsBean)),          //对应id值(如音乐列表对应item id值)
                            EventConstants.PagePath.filmCollectActivityPath,    //页面路径
                            EventConstants.PageDescribe.FilmCollectActivityPagePathDesc);//页面路径中午意思
                    rightNowBugTicket(mNearbyCinemasDetailsList.get(position));
                } else if (view.getId() == R.id.tv_action2) {
                    mNearbyCinemasDetailsBean = mNearbyCinemasDetailsList.get(position);
                    XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.FILM_COLLECT_PHONE,       //按钮名称(如播放,音乐列表)
                            GsonUtil.toJson(setCinemaTracker(mNearbyCinemasDetailsBean)),          //对应id值(如音乐列表对应item id值)
                            EventConstants.PagePath.filmCollectActivityPath,    //页面路径
                            EventConstants.PageDescribe.FilmCollectActivityPagePathDesc);//页面路径中午意思
                    showNaviDialog(mNearbyCinemasDetailsList.get(position));
                }
            }
        });
        mRvCollect.setOnTouchListener(new View.OnTouchListener() {
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
                            if (!mRvCollect.canScrollHorizontally(1)) {
                                XMToast.showToast(FilmCollectActivity.this, getString(R.string.no_more_data));
                            }
                        }
                    }
                }
                return false;
            }
        });

        mRvCollect.setAdapter(mFilmCollectAdapter);
        mXmScrollBar.setRecyclerView(mRvCollect);
    }

    private void rightNowBugTicket(NearbyCinemasDetailsBean nearbyCinemasDetailsBean) {
        TPUtils.put(FilmCollectActivity.this, LauncherConstants.PATH_TYPE, getString(R.string.select_film));
        Intent intent = new Intent(FilmCollectActivity.this, CinemaShowActivity.class);
        intent.putExtra(LauncherConstants.ActionExtras.NEARBY_CINEMAS_DETAILS_BEAN, nearbyCinemasDetailsBean);
        startActivity(intent);
    }


    private void loadMore() {
        if (mCollectVM == null) {
            return;
        }
        if (pageNo + 1 >= maxPageNum) {
            notifyLoadState(LauncherConstants.END);
        } else {
            pageNo++;
            mCollectVM.fetchCollectFilms(type, COLLECT_FILM, pageNo);
        }
    }

    private void notifyLoadState(int state) {
        if (LauncherConstants.COMPLETE == state) {
            mFilmCollectAdapter.loadMoreComplete();
        } else if (LauncherConstants.END == state) {
            isend = true;
            mFilmCollectAdapter.loadMoreEnd();
        } else if (LauncherConstants.FAILED == state) {
            mFilmCollectAdapter.loadMoreFail();
        }
    }

    protected void initVM() {
        type = getIntent().getStringExtra("type");
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
                            mNearbyCinemasDetailsBean.setStatus(mCollectState);
                            mFilmCollectAdapter.notifyDataSetChanged();
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

        mCollectVM.fetchCollectFilms(type, COLLECT_FILM, pageNo);
    }

    public void initVMData(CollectItems.CollectionsBean data) {
        NearbyCinemasDetailsBean nearbyCinemasDetailsBean = GsonHelper.fromJson(data.getCollectionJson(), NearbyCinemasDetailsBean.class);
        nearbyCinemasDetailsBean.setStatus(BaseCollectVM.HAVE_COLLECT_STATE);
        mNearbyCinemasDetailsList.add(nearbyCinemasDetailsBean);
    }

    @Override
    protected void noNetworkOnRetry() {
        super.noNetworkOnRetry();
        if (NetworkUtils.isConnected(this)) {
            mCollectVM.fetchCollectFilms(type, COLLECT_FILM, pageNo);
        }
    }

    /**
     * 导航dialog
     *
     * @param nearbyCinemasDetailsBean
     */
    private void showNaviDialog(final NearbyCinemasDetailsBean nearbyCinemasDetailsBean) {
        ConfirmDialog dialog = new ConfirmDialog(this);
        dialog.setContent(getString(R.string.travel_message_navi_delicious))
                .setPositiveButton(getString(R.string.travel_left_btn_navi), new XMAutoTrackerEventOnClickListener() {
                    @Override
                    public ItemEvent returnPositionEventMsg(View view) {
                        return new ItemEvent(EventConstants.NormalClick.FILM_COLLECT_PHONE_SURE, GsonUtil.toJson(setCinemaTracker(nearbyCinemasDetailsBean)));
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
                        return new ItemEvent(EventConstants.NormalClick.DELICIOUS_COLLECT_NAVI_CANCE, GsonUtil.toJson(setCinemaTracker(nearbyCinemasDetailsBean)));
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
//        dialogMessage.setText(getString(R.string.travel_message_navi_delicious));
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
//                return new ItemEvent(EventConstants.NormalClick.FILM_COLLECT_PHONE_SURE,GsonUtil.toJson(setCinemaTracker(nearbyCinemasDetailsBean)));
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
//                return new ItemEvent(EventConstants.NormalClick.DELICIOUS_COLLECT_NAVI_CANCE,GsonUtil.toJson(setCinemaTracker(nearbyCinemasDetailsBean)));
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
}
