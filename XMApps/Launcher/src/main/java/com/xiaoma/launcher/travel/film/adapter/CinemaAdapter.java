package com.xiaoma.launcher.travel.film.adapter;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.ad.utils.GsonUtil;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.travel.film.ui.SelectSessionActivity;
import com.xiaoma.launcher.travel.itemevent.CinemaTrackerBean;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.trip.movie.response.CinemasBean;
import com.xiaoma.trip.movie.response.CinemasShowBean;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.ConvertUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;

import java.text.DecimalFormat;

public class CinemaAdapter extends XMBaseAbstractBQAdapter<CinemasBean, BaseViewHolder> {

    private ImageView mCinemaImg;
    private TextView mCinemaDistance;
    private TextView mCinemaPosition;
    private TextView mCinemaName;
    private TextView mNameAndProice;

    public CinemaAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, final CinemasBean item) {

        initView(helper);
        initData(item);
        helper.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!NetworkUtils.isConnected(mContext)) {
                    XMToast.toastException(mContext, R.string.net_work_error);
                    return;
                }

                CinemasShowBean cinemasShowBean = new CinemasShowBean();
                cinemasShowBean.setAddress(item.getAddress());
                cinemasShowBean.setCinemaId(item.getCinemaId());
                cinemasShowBean.setCinemaName(item.getCinemaName());
                cinemasShowBean.setFilmId(item.getFilms().get(0).getFilmId());
                cinemasShowBean.setFilmName(item.getFilms().get(0).getFilmName());
                cinemasShowBean.setShows(item.getFilms().get(0).getShows());
                cinemasShowBean.setCinemaLinkId(item.getCinemaLinkId());
                cinemasShowBean.setDuration(item.getFilms().get(0).getDuration());
                cinemasShowBean.setMobile(item.getMobile());
                cinemasShowBean.setLat(item.getLatitude());
                cinemasShowBean.setLon(item.getLongitude());
                cinemasShowBean.setIconUrl(item.getImgUrl());

                Intent intent = new Intent(mContext, SelectSessionActivity.class);
                intent.putExtra(LauncherConstants.ActionExtras.CINEMAS_SHOW_BEAN, cinemasShowBean);
                mContext.startActivity(intent);
            }
        });
    }

    private void initData(CinemasBean item) {
        ImageLoader.with(mContext)
                .load(item.getImgUrl())
                .placeholder(R.drawable.not_film_img)
                .error(R.drawable.not_film_img)
                .into(mCinemaImg);
        double distance = ConvertUtils.stringToDouble(item.getDistance());
        if (distance >= 1000) {
            double dis = distance / 1000;
            mCinemaDistance.setText(mContext.getString(R.string.thousand_address_distance, new DecimalFormat("0.00").format(dis)));
        } else {
            mCinemaDistance.setText(mContext.getString(R.string.address_distance, new DecimalFormat("0").format(distance)));
        }

        mCinemaPosition.setText(item.getAddress());
        mCinemaName.setText(item.getCinemaName());
        mNameAndProice.setText(String.format(mContext.getString(R.string.film_min_price), StringUtil.keep2Decimal(Float.parseFloat(item.getMinPrice()))));
    }

    private void initView(BaseViewHolder helper) {
        mCinemaImg = helper.getView(R.id.cinema_img);
        mCinemaDistance = helper.getView(R.id.cinema_distance);
        mCinemaPosition = helper.getView(R.id.cinema_position);
        mCinemaName = helper.getView(R.id.cinema_name);
        mNameAndProice = helper.getView(R.id.name_and_price);

    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(EventConstants.NormalClick.SELECT_CINEMA, GsonUtil.toJson(setCinemaTracker(getData().get(position))));
    }

    private CinemaTrackerBean setCinemaTracker(CinemasBean cinemasBean) {
        CinemaTrackerBean deliciousTrackerBean = new CinemaTrackerBean();
        deliciousTrackerBean.id = cinemasBean.getCinemaId();
        deliciousTrackerBean.value = cinemasBean.getCinemaName();
        deliciousTrackerBean.h = cinemasBean.getDistance();
        return deliciousTrackerBean;
    }
}
