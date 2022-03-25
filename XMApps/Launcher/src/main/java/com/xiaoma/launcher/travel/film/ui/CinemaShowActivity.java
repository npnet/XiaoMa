package com.xiaoma.launcher.travel.film.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.travel.film.adapter.CinemaShowAdapter;
import com.xiaoma.launcher.travel.film.vm.FilmVM;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.trip.movie.response.CinemaShowDataBean;
import com.xiaoma.trip.movie.response.NearbyCinemasDetailsBean;
import com.xiaoma.ui.view.XmDividerDecoration;
import com.xiaoma.utils.tputils.TPUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@PageDescComponent(EventConstants.PageDescribe.CinemaShowActivityPagePathDesc)
public class CinemaShowActivity extends BaseActivity {
    private RecyclerView mCinemaRv;
    private CinemaShowAdapter mCinemaAdapter;
    private NearbyCinemasDetailsBean mNearByCinemasBean;
    private FilmVM mFilmVM;
    private List<CinemaShowDataBean.CinemaBean.FilmsBean> mCinemasShowList;
    private TextView mTapOneText;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private View mEmptyView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cinema_show_select);
        bindView();
        initView();
        initData();
    }

    private void bindView() {
        mCinemaRv = findViewById(R.id.film_rv);
        mTapOneText = findViewById(R.id.tap_one_text);
    }

    private void initView() {
        mNearByCinemasBean = (NearbyCinemasDetailsBean) getIntent().getSerializableExtra(LauncherConstants.ActionExtras.NEARBY_CINEMAS_DETAILS_BEAN);
        //设置空布局
        mEmptyView = View.inflate(this, R.layout.cinema_empty_view, null);
        TextView tvTip = mEmptyView.findViewById(R.id.tv_tips);
        Button button = mEmptyView.findViewById(R.id.renew_trailer);

        if (mNearByCinemasBean!=null){
            tvTip.setText(String.format(getString(R.string.cinema_notany_move)));
        }else {
            tvTip.setText(String.format(getString(R.string.no_cinema_no_move)));
        }
        button.setText(getString(R.string.renew_select_cinema));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick({EventConstants.NormalClick.CINEMA_SHOW_NETWORK_RECONNECTION})//按钮对应的名称
            public void onClick(View v) {
                finish();
            }
        });
        mTapOneText.setText(TPUtils.get(this, LauncherConstants.PATH_TYPE, getString(R.string.select_cinema)));
        mCinemaRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mCinemaAdapter = new CinemaShowAdapter(R.layout.cinema_show_item);
        XmDividerDecoration decor = new XmDividerDecoration(this, DividerItemDecoration.HORIZONTAL);
        int extra = 0;
        decor.setRect(0, 0, 52, 0);
        decor.setExtraMargin(extra);
        mCinemaRv.addItemDecoration(decor);
        mCinemaRv.setAdapter(mCinemaAdapter);

    }

    private void initData() {

        mFilmVM = ViewModelProviders.of(this).get(FilmVM.class);
        mFilmVM.getCinemasFilmData().observe(this, new Observer<XmResource<CinemaShowDataBean>>() {
            @Override
            public void onChanged(@Nullable XmResource<CinemaShowDataBean> listXmResource) {
                listXmResource.handle(new OnCallback<CinemaShowDataBean>() {
                    @Override
                    public void onSuccess(CinemaShowDataBean data) {
                        if (data == null) {
                            return;
                        }
                        mCinemasShowList = data.getCinema().getFilms();
                        mCinemaAdapter.addData(mCinemasShowList);
                        mCinemaAdapter.setCimasShow(data.getCinema());
                        mCinemaAdapter.setCimasImg(mNearByCinemasBean.getImgUrl());
                        mCinemaAdapter.setEmptyView(mEmptyView);
                    }

                    @Override
                    public void onFailure(String msg) {
                        super.onFailure(msg);
                    }
                });
            }
        });
        if (mNearByCinemasBean != null) {
            mCinemaAdapter.setMobile(mNearByCinemasBean.getMobile());
            mFilmVM.queryCinemaShow(mNearByCinemasBean.getCinemaId(), "", dateFormat.format(new Date(System.currentTimeMillis())));
        }
    }
}
