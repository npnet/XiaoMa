package com.xiaoma.launcher.travel.film.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.travel.film.adapter.CinemaAdapter;
import com.xiaoma.launcher.travel.film.view.CinemaLoadMoreView;
import com.xiaoma.launcher.travel.film.vm.FilmVM;
import com.xiaoma.mapadapter.manager.LocationManager;
import com.xiaoma.mapadapter.model.LocationInfo;
import com.xiaoma.mapadapter.model.SearchAddressInfo;
import com.xiaoma.mapadapter.ui.MapActivity;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.trip.movie.response.CinemasBean;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmDividerDecoration;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.tputils.TPUtils;

import java.util.List;
@PageDescComponent(EventConstants.PageDescribe.SelectCinemaActivityPagePathDesc)
public class SelectCinemaActivity extends BaseActivity {

    private RecyclerView mCinemaRv;
    private CinemaAdapter mCinemaAdapter;
    private String mFilmsTag;
    private String mFilmsName;
    private FilmVM mFilmVM;
    private int pageNum = 1;
    private XmScrollBar xmScrollBar;
    private TextView tapOneText;
    private int REQUEST_CODE = 200;
    private LocationInfo mLocationInfo;
    private SearchAddressInfo mMAddressInfo;
    private static final String EXTRA_LOCATION_DATA = "location";
    private View mEmptyView;
    private boolean isend = false;
    //手指按下的点为(x1, y1)手指离开屏幕的点为(x2, y2)
    float x1 = 0;
    float x2 = 0;

    private TextView mSelectSession;
    private TextView mBranching;
    private TextView mSelectSeat;
    private TextView mConfirmPay;
    private int disposeColor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cinema_select);
        bindView();
        initView();
        initData();
    }

    private void bindView() {
        mCinemaRv = findViewById(R.id.cinema_rv);
        tapOneText = findViewById(R.id.tap_one_text);
        xmScrollBar = findViewById(R.id.scroll_bar);
        getNaviBar().getMiddleView().setImageDrawable(getResources().getDrawable(R.drawable.icon_location));

        mSelectSession = findViewById(R.id.tap_two_select_session);
        mBranching = findViewById(R.id.tap_two_branching);
        mSelectSeat = findViewById(R.id.tap_two_select_seat);
        mConfirmPay = findViewById(R.id.tap_three_confirm_pay);
    }

    private void initView() {
        disposeColor = Color.parseColor("#8a919d");
        mSelectSession.setTextColor(disposeColor);
        mBranching.setTextColor(disposeColor);
        mSelectSeat.setTextColor(disposeColor);
        mConfirmPay.setTextColor(disposeColor);

        getNaviBar().getMiddleView().setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick({EventConstants.NormalClick.CINEMA_RENEW_POSITION})
            public void onClick(View v) {
                startActivityForResult(new Intent(SelectCinemaActivity.this, MapActivity.class), REQUEST_CODE);
            }
        });

        Bundle bundleExtra = getIntent().getBundleExtra(LauncherConstants.ActionExtras.MOVIE_BUNDLE);
        if (bundleExtra != null) {
            mFilmsTag = bundleExtra.getString(LauncherConstants.ActionExtras.MOVIE_TAG);
            mFilmsName = bundleExtra.getString(LauncherConstants.ActionExtras.MOVIE_NAME);
        }

        mLocationInfo = LocationManager.getInstance().getCurrentLocation();
        mEmptyView = View.inflate(this, R.layout.cinema_show_empty_view, null);
        TextView tvTip = mEmptyView.findViewById(R.id.tv_tips);
        ImageView imTip = mEmptyView.findViewById(R.id.iv_tips);
        Button button = mEmptyView.findViewById(R.id.renew_trailer);
        imTip.setImageResource(R.drawable.emotional_movie_not_data);
        if (mFilmsName!=null){
            tvTip.setText(String.format(getString(R.string.cinema_no_move),mFilmsName));
        }
        button.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent();
            }

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tapOneText.setText(TPUtils.get(this, LauncherConstants.PATH_TYPE, getString(R.string.select_cinema)));
        mCinemaRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mCinemaAdapter = new CinemaAdapter(R.layout.cinema_item);

        //设置activity间距
        XmDividerDecoration decor = new XmDividerDecoration(this, DividerItemDecoration.HORIZONTAL);
        int right = 107;
        int extra = 0;
        decor.setRect(0, 0, right, 0);
        decor.setExtraMargin(extra);
        mCinemaRv.addItemDecoration(decor);

        //设置加载更多
        mCinemaAdapter.setEnableLoadMore(true);
        mCinemaAdapter.setLoadMoreView(new CinemaLoadMoreView());
        mCinemaAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadMore();
            }
        }, mCinemaRv);


        mCinemaRv.setOnTouchListener(new View.OnTouchListener() {
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
                            if (!mCinemaRv.canScrollHorizontally(1)) {
                                XMToast.showToast(SelectCinemaActivity.this,getString(R.string.no_more_data));
                            }
                        }
                    }
                }
                return false;
            }
        });

        mCinemaRv.setAdapter(mCinemaAdapter);
        xmScrollBar.setRecyclerView(mCinemaRv);

    }

    private void loadMore() {
        if (mFilmVM == null) {
            return;
        }
        if (mFilmVM.isCineMaLoadEnd()) {
            notifyLoadState(LauncherConstants.END);
        } else {
            pageNum++;
            if (mMAddressInfo != null && mMAddressInfo.latLonPoint != null) {
                mFilmVM.queryFilmShow(mFilmsTag, mMAddressInfo.city, "",
                        String.valueOf(mMAddressInfo.latLonPoint.getLatitude()), String.valueOf(mMAddressInfo.latLonPoint.getLongitude()), LauncherConstants.PAGE_SIZE, pageNum);
            } else {
                if (mLocationInfo == null) {
                    XMToast.showToast(getApplication(), R.string.not_nvi_info);
                    return;
                }
                mFilmVM.queryFilmShow(mFilmsTag, mLocationInfo.getCity(), "",
                        String.valueOf(mLocationInfo.getLatitude()), String.valueOf(mLocationInfo.getLongitude()), LauncherConstants.PAGE_SIZE, pageNum);
            }
        }
    }

    private void notifyLoadState(int state) {
        if (LauncherConstants.COMPLETE == state) {
            mCinemaAdapter.loadMoreComplete();
        } else if (LauncherConstants.END == state) {
            isend = true;
            mCinemaAdapter.loadMoreEnd();
        } else if (LauncherConstants.FAILED == state) {
            mCinemaAdapter.loadMoreFail();
        }
    }

    private void initData() {
        mFilmVM = ViewModelProviders.of(this).get(FilmVM.class);
        mFilmVM.getCinemasData().observe(this, new Observer<XmResource<List<CinemasBean>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<CinemasBean>> listXmResource) {
                listXmResource.handle(new OnCallback<List<CinemasBean>>() {
                    @Override
                    public void onSuccess(List<CinemasBean> data) {
                        if (ListUtils.isEmpty(data)) {

                        } else {
                            mCinemaAdapter.addData(data);
                        }
                        mCinemaRv.setVisibility(View.VISIBLE);
                        notifyLoadState(LauncherConstants.COMPLETE);

                        mCinemaAdapter.setEmptyView(mEmptyView);
                    }

                    @Override
                    public void onFailure(String msg) {
                        super.onFailure(msg);
                        notifyLoadState(LauncherConstants.FAILED);
                    }
                });
            }
        });
        if (mLocationInfo == null) {
            XMToast.showToast(getApplication(), R.string.not_nvi_info);
            return;
        }
        mFilmVM.queryFilmShow(mFilmsTag, mLocationInfo.getCity(), "",
                String.valueOf(mLocationInfo.getLatitude()), String.valueOf(mLocationInfo.getLongitude()), LauncherConstants.PAGE_SIZE, pageNum);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            mCinemaRv.setVisibility(View.INVISIBLE);
            pageNum = 1;
            mCinemaAdapter.getData().clear();
            mCinemaAdapter.notifyDataSetChanged();
            mMAddressInfo = data.getParcelableExtra(EXTRA_LOCATION_DATA);
            if (mMAddressInfo != null && mMAddressInfo.latLonPoint != null) {
                mFilmVM.queryFilmShow(mFilmsTag, mMAddressInfo.city, "",
                        String.valueOf(mMAddressInfo.latLonPoint.getLatitude()), String.valueOf(mMAddressInfo.latLonPoint.getLongitude()), LauncherConstants.PAGE_SIZE, pageNum);
            }
        }
    }

}
