package com.xiaoma.launcher.travel.film.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.travel.film.adapter.SelectSessionAdapter;
import com.xiaoma.launcher.travel.film.vm.FilmVM;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.trip.movie.request.RequestHallSeatsInfoParm;
import com.xiaoma.trip.movie.response.CinemasShowBean;
import com.xiaoma.trip.movie.response.HallSeatsInfoBean;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.tputils.TPUtils;
@PageDescComponent(EventConstants.PageDescribe.SelectSessionActivityPagePathDesc)
public class SelectSessionActivity extends BaseActivity implements View.OnClickListener {
    private RecyclerView mSessionRv;
    private SelectSessionAdapter mSessionAdapter;
    private CinemasShowBean mCimasShowBean;
    private TextView mTwoImg;
    private TextView mSelectSeat;
    private TextView mConfirmPay;
    private XmScrollBar xmScrollBar;
    private FilmVM mFilmVM;
    private int mPosition;
    private TextView tapOneText;
    private int disposeColor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_session);
        bindView();
        initView();
        initData();
    }

    private void bindView() {
        mSessionRv = findViewById(R.id.session_rv);
        tapOneText = findViewById(R.id.tap_one_text);
        //头部条目设置
        mTwoImg = findViewById(R.id.tap_two_img);
        mSelectSeat = findViewById(R.id.tap_two_select_seat);
        mConfirmPay = findViewById(R.id.tap_three_confirm_pay);
        xmScrollBar = findViewById(R.id.scroll_bar);
    }

    private void initView() {
        tapOneText.setText(TPUtils.get(this, LauncherConstants.PATH_TYPE, getString(R.string.select_cinema)));
        mTwoImg.setBackgroundResource(R.drawable.round_back_yellow);
        disposeColor = Color.parseColor("#8a919d");
        mConfirmPay.setTextColor(disposeColor);
        mSessionRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mSessionAdapter = new SelectSessionAdapter();
        mSessionRv.setAdapter(mSessionAdapter);
        mSessionRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.set(0, 0, 98, 0);
            }
        });

        xmScrollBar.setRecyclerView(mSessionRv);
        mSessionAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (!NetworkUtils.isConnected(SelectSessionActivity.this)) {
                    showToastException(R.string.net_work_error);
                    return;
                }
                checkSeatType(position);
            }
        });
    }

    private void checkSeatType(final int position) {
        mPosition = position;
        if (mCimasShowBean != null || !ListUtils.isEmpty(mCimasShowBean.getShows())) {
            RequestHallSeatsInfoParm requestHallSeatsInfoParm = new RequestHallSeatsInfoParm();
            requestHallSeatsInfoParm.cinemaId = mCimasShowBean.getCinemaId();
            requestHallSeatsInfoParm.cinemaLinkId = mCimasShowBean.getCinemaLinkId();
            requestHallSeatsInfoParm.hallCode = mCimasShowBean.getShows().get(position).getHallCode();
            requestHallSeatsInfoParm.sectionId = "";
            requestHallSeatsInfoParm.showCode = mCimasShowBean.getShows().get(position).getShowCode();
            requestHallSeatsInfoParm.showDate = mCimasShowBean.getShows().get(position).getShowDate();
            requestHallSeatsInfoParm.showTime = mCimasShowBean.getShows().get(position).getShowTime();
            mFilmVM.queryFilmSeat(requestHallSeatsInfoParm);
        }
    }

    private void initData() {
        mCimasShowBean = (CinemasShowBean) getIntent().getSerializableExtra(LauncherConstants.ActionExtras.CINEMAS_SHOW_BEAN);
        if (mCimasShowBean != null) {
            mSessionAdapter.setCimas(mCimasShowBean);
            mSessionAdapter.setNewData(mCimasShowBean.getShows());
        }
        mFilmVM = ViewModelProviders.of(this).get(FilmVM.class);
        mFilmVM.getFilmsSeatsInfo().observe(this, new Observer<XmResource<HallSeatsInfoBean>>() {
            @Override
            public void onChanged(@Nullable XmResource<HallSeatsInfoBean> hallSeatsInfoBeanXmResource) {
                if (hallSeatsInfoBeanXmResource == null) {
                    return;
                }
                hallSeatsInfoBeanXmResource.handle(new OnCallback<HallSeatsInfoBean>() {
                    @Override
                    public void onSuccess(HallSeatsInfoBean data) {
                        if (data != null) {
                            if (data.getMax_Row_Num() == 0 && data.getMax_Col_Num() == 0) {
                                XMToast.showToast(SelectSessionActivity.this, R.string.stop_buy_tick);
                            } else {
                                if (mCimasShowBean == null || !ListUtils.isEmpty(mCimasShowBean.getShows())) {
                                    Intent intent = new Intent(SelectSessionActivity.this, SeatTableActivity.class);
                                    intent.putExtra(LauncherConstants.ActionExtras.SHOW_BEAN, mCimasShowBean.getShows().get(mPosition));
                                    intent.putExtra(LauncherConstants.ActionExtras.CINEMAS_SHOW_BEAN, mCimasShowBean);
                                    intent.putExtra(LauncherConstants.ActionExtras.HALL_SEATS_INFO_BEAN, data);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }
                    }
                });
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }
}
