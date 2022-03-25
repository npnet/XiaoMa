package com.xiaoma.launcher.travel.order.adapter;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.travel.order.adapter
 *  @file_name:      HotelOrdersAdapter
 *  @author:         Rookie
 *  @create_time:    2019/1/28 14:41
 *  @description：   酒店订单adapter             */

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.launcher.R;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.trip.hotel.request.HotelInfo;
import com.xiaoma.trip.movie.response.CompleteOrderBean;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.TimeUtils;

public class HotelOrdersAdapter extends XMBaseAbstractBQAdapter<CompleteOrderBean.ListBean, BaseViewHolder> {
    private HotelInfo mHotelInfo;
    public HotelOrdersAdapter() {
        super(R.layout.item_hotel_order);
    }

    @Override
    protected void convert(BaseViewHolder helper, CompleteOrderBean.ListBean item) {
        if (StringUtil.isNotEmpty(item.getHotelJson())) {
            mHotelInfo = GsonHelper.fromJson(item.getHotelJson(), HotelInfo.class);
        }
        helper.setText(R.id.tv_hotel_name, mHotelInfo.getHotelName());
        helper.setText(R.id.tv_address, mHotelInfo.getAddress());
        String[] start = item.getCheckIn().split("-");
        String[] end = item.getCheckOut().split("-");

        helper.setText(R.id.tv_time, TimeUtils.getDateStr(start[0], start[1], start[2], end[0], end[1], end[2]) + " 共" + item.getOrderDays() + "天");
        helper.setText(R.id.tv_count, item.getTicketNum() + "间");

        helper.addOnClickListener(R.id.btn_guide);

    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent();
    }
}
