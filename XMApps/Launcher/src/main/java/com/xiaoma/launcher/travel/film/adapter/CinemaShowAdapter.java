package com.xiaoma.launcher.travel.film.adapter;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.ad.utils.GsonUtil;
import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.travel.film.ui.SelectSessionActivity;
import com.xiaoma.launcher.travel.itemevent.SelectFilmBean;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.trip.movie.response.CinemaShowDataBean;
import com.xiaoma.trip.movie.response.CinemasShowBean;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.tputils.TPUtils;

public class CinemaShowAdapter extends XMBaseAbstractBQAdapter<CinemaShowDataBean.CinemaBean.FilmsBean, BaseViewHolder> {

    private ImageView mFilmImg;
    private TextView mFilmScore;
    private TextView mfilmType;
    private TextView mFilmName;
    private TextView mTimeAndProice;
    private CinemaShowDataBean.CinemaBean mCinemasShowBean;
    private String mMobile;
    private String mCimasImg;

    public CinemaShowAdapter(int layoutResId) {
        super(layoutResId);
    }

    public void setCimasShow(CinemaShowDataBean.CinemaBean cinemasShowBean) {
        mCinemasShowBean = cinemasShowBean;
    }
    public void setCimasImg(String cimasImg) {
        mCimasImg = cimasImg;
    }

    public void setMobile(String mobile) {
        mMobile = mobile;
    }


    @Override
    protected void convert(BaseViewHolder helper, final CinemaShowDataBean.CinemaBean.FilmsBean item) {

        initView(helper);
        initData(item);
        helper.itemView.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(EventConstants.NormalClick.CINEMA_SELECT_FILM,  GsonUtil.toJson(setFilmTracker(item)));
            }

            @Override
            @BusinessOnClick
            public void onClick(View v) {
                if (!NetworkUtils.isConnected(mContext)) {
                    XMToast.toastException(mContext, R.string.net_work_error);
                    return;
                }

                CinemasShowBean cinemasShowBean = new CinemasShowBean();
                cinemasShowBean.setAddress(mCinemasShowBean.getAddress());
                cinemasShowBean.setCinemaId(mCinemasShowBean.getCinemaId());
                cinemasShowBean.setCinemaName(mCinemasShowBean.getCinemaName());
                cinemasShowBean.setCinemaLinkId(mCinemasShowBean.getCinemaLinkId());
                cinemasShowBean.setFilmId(item.getFilmId());
                cinemasShowBean.setFilmName(item.getFilmName());
                cinemasShowBean.setShows(item.getShows());
                cinemasShowBean.setDuration(item.getDuration());
                cinemasShowBean.setMobile(mMobile);
                cinemasShowBean.setLat(mCinemasShowBean.getLatitude());
                cinemasShowBean.setLon(mCinemasShowBean.getLongitude());
                if (StringUtil.isNotEmpty(mCimasImg))
                cinemasShowBean.setIconUrl(mCimasImg);
                TPUtils.put(mContext, LauncherConstants.FILM_TYPE, item.getFilmType().replaceAll(",", "/"));
                Intent intent = new Intent(mContext, SelectSessionActivity.class);
                intent.putExtra(LauncherConstants.ActionExtras.CINEMAS_SHOW_BEAN, cinemasShowBean);
                mContext.startActivity(intent);
            }
        });
    }

    private void initData(CinemaShowDataBean.CinemaBean.FilmsBean item) {
        ImageLoader.with(mContext)
                .load(item.getImgUrl())
                .placeholder(R.drawable.not_film_img)
                .error(R.drawable.not_film_img)
                .into(mFilmImg);
        mFilmScore.setText(item.getFilmScore());
        mfilmType.setText(item.getFilmType().replaceAll(",", "/"));
        mFilmName.setText(item.getFilmName());
        mTimeAndProice.setText(String.format(mContext.getString(R.string.film_price), item.getDuration().replace("min", "分钟"), StringUtil.keep2Decimal(Float.parseFloat(item.getMinPrice()))));
    }

    private void initView(BaseViewHolder helper) {
        mFilmImg = helper.getView(R.id.film_img);
        mFilmScore = helper.getView(R.id.film_score);
        mfilmType = helper.getView(R.id.film_type);
        mFilmName = helper.getView(R.id.film_name);
        mTimeAndProice = helper.getView(R.id.time_and_price);
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(EventConstants.NormalClick.CINEMA_SHOW_ITEM,getData().get(position).getFilmId());
    }
    private SelectFilmBean setFilmTracker(CinemaShowDataBean.CinemaBean.FilmsBean filmsBean) {
        SelectFilmBean selectFilmBean = new SelectFilmBean();
        selectFilmBean.id = filmsBean.getFilmId();
        selectFilmBean.value = filmsBean.getFilmName();
        selectFilmBean.h = filmsBean.getFilmScore();
        return selectFilmBean;
    }
}
