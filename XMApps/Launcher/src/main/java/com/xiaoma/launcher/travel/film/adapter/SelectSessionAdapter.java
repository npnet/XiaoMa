package com.xiaoma.launcher.travel.film.adapter;

import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.ad.utils.GsonUtil;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.travel.itemevent.SelectSessionBean;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.trip.movie.response.CinemasShowBean;
import com.xiaoma.trip.movie.response.ShowBean;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.utils.StringUtil;


public class SelectSessionAdapter extends XMBaseAbstractBQAdapter<ShowBean, BaseViewHolder> {


    private ImageView mCinemaImg;
    private TextView mSessionPrice;
    private TextView mFilmLanguage;
    private TextView mGoInTime;
    private TextView mGoOutTime;
    private TextView mSessionName;
    private CinemasShowBean mCinemasBean;


    public SelectSessionAdapter() {
        super(R.layout.session_item);
    }

    public void setCimas(CinemasShowBean cinemasBean) {
        mCinemasBean = cinemasBean;
    }


    @Override
    protected void convert(BaseViewHolder helper, final ShowBean item) {
        initView(helper);
        initData(item);

    }

    private void initData(ShowBean item) {

        mSessionPrice.setText(String.format(mContext.getString(R.string.price),StringUtil.keep2Decimal(Float.parseFloat(item.getPrice()))));
        mFilmLanguage.setText(item.getLanguage() + item.getFilmAttr());
        StringBuffer inTime = new StringBuffer(item.getShowTime());
        mGoInTime.setText(inTime.insert(2, ":").toString());
        if (mCinemasBean != null || StringUtil.isNotEmpty(mCinemasBean.getDuration())) {
            mGoOutTime.setText(StringUtil.getOutTime(item.getShowTime(), mCinemasBean.getDuration().replace("min", "")));
        }
        mSessionName.setText(item.getHallName());

    }


    private void initView(BaseViewHolder helper) {
        mCinemaImg = helper.getView(R.id.session_img);
        mSessionPrice = helper.getView(R.id.session_price);
        mFilmLanguage = helper.getView(R.id.film_language);
        mGoInTime = helper.getView(R.id.go_in_time);
        mGoOutTime = helper.getView(R.id.go_out_time);
        mSessionName = helper.getView(R.id.session_name);

    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(EventConstants.NormalClick.SELECT_SESSION, GsonUtil.toJson(setSelectSessionTracker(getData().get(position))));
    }

    private SelectSessionBean setSelectSessionTracker(ShowBean showBean) {
        StringBuffer inTime = new StringBuffer(showBean.getShowTime());
        SelectSessionBean selectSessionBean = new SelectSessionBean();
        selectSessionBean.id = showBean.getPrice();
        selectSessionBean.value = inTime.insert(2, ":").toString();
        if (mCinemasBean != null || StringUtil.isNotEmpty(mCinemasBean.getDuration())) {
            selectSessionBean.h = StringUtil.getOutTime(showBean.getShowTime(), mCinemasBean.getDuration().replace("min", ""));
        }
        return selectSessionBean;
    }
}
