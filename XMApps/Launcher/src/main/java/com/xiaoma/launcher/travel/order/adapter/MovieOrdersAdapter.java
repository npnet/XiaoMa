package com.xiaoma.launcher.travel.order.adapter;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.travel.order.adapter
 *  @file_name:      MovieOrdersAdapter
 *  @author:         Rookie
 *  @create_time:    2019/1/28 14:43
 *  @description：   电影订单adapter             */


import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.launcher.R;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.trip.movie.request.CinemaJsonBean;
import com.xiaoma.trip.movie.response.CompleteOrderBean;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.StringUtil;


public class MovieOrdersAdapter extends XMBaseAbstractBQAdapter<CompleteOrderBean.ListBean, BaseViewHolder> {
    private CinemaJsonBean mCinemaJsonBean;

    public MovieOrdersAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(BaseViewHolder helper, CompleteOrderBean.ListBean item) {
        if (StringUtil.isNotEmpty(item.getCinemaJson())) {
            mCinemaJsonBean = GsonHelper.fromJson(item.getCinemaJson(), CinemaJsonBean.class);
        }
        helper.setText(R.id.tv_hotel_name, item.getOrderName());
        helper.setText(R.id.tv_address, mCinemaJsonBean.getCinemaName());
        if (!StringUtil.isEmpty(item.getOrderDate())) {
            helper.setText(R.id.tv_time, convertDate(item.getOrderDate()));
        }

        if (!StringUtil.isEmpty(item.getCinemaName())) {
            helper.setText(R.id.tv_address, item.getCinemaName());
        }
        helper.setText(R.id.tv_count, item.getTicketNum() + mContext.getString(R.string.leaf));

        helper.addOnClickListener(R.id.btn_guide);
    }

     private String convertDate(String date) {
        StringBuilder builder = new StringBuilder();
        builder.append(date.substring(0,10));
        builder.append(" ");

        String time = date.substring(date.length() - 4);
        String hour = time.substring(0, 2);
        builder.append(hour);
        builder.append(":");
        String min = time.substring(2, 4);
        builder.append(min);
        return builder.toString();
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent();
    }
}
