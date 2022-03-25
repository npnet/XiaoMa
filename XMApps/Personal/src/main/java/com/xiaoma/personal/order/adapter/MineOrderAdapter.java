package com.xiaoma.personal.order.adapter;

import android.support.constraint.ConstraintLayout;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.image.transformation.ReflectiveBitmapTransformation;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.personal.R;
import com.xiaoma.personal.order.constants.OrderTypeMeta;
import com.xiaoma.personal.order.constants.timer.OrderTimer;
import com.xiaoma.personal.order.model.OrderInfo;
import com.xiaoma.personal.order.ui.view.SlideView;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.utils.log.KLog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * <pre>
 *       @author Created by Gillben
 *       date: 2019/3/12 0012 10:30
 *       desc：订单列表适配器
 * </pre>
 */
public class MineOrderAdapter extends XMBaseAbstractBQAdapter<OrderInfo.Order, MineOrderAdapter.AopBaseViewHolder> {


    public MineOrderAdapter() {
        super(R.layout.item_mine_order);
    }


    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(getData().get(position).toString(), "");
    }


    @Override
    protected void convert(AopBaseViewHolder helper, OrderInfo.Order order) {
        addListener(helper);
        setContent(helper, order);
    }


    private void addListener(BaseViewHolder helper) {
        helper.addOnClickListener(R.id.item_order_content_layout);
        helper.addOnClickListener(R.id.delete_text);
        helper.addOnClickListener(R.id.bt_pay_order);
        helper.addOnClickListener(R.id.bt_call_phone);
        helper.addOnClickListener(R.id.bt_navigation_go);
    }


    private void operationButton(BaseViewHolder helper, boolean visible) {
        helper.setGone(R.id.bt_call_phone, visible);
        helper.setGone(R.id.bt_navigation_go, visible);
    }


    private void setContent(BaseViewHolder helper, OrderInfo.Order order) {
        String iconUrl = null;
        if (order.getCinemaJsonVo() != null) {
            iconUrl = order.getCinemaJsonVo().getIconUrl();
        } else if (order.getHotelJsonVo() != null) {
            iconUrl = order.getHotelJsonVo().getIconUrl();
        }
        ImageLoader.with(mContext)
                .load(iconUrl)
                .placeholder(R.drawable.default_cover)
                .transform(new ReflectiveBitmapTransformation(mContext, R.drawable.order_delete_reflective))
                .into((ImageView) helper.getView(R.id.iv_order_content_icon));


        String type = order.getType();
        if (OrderTypeMeta.HOTEL.equals(type)) {
            operationButton(helper, true);
            String checkIn = order.getCheckIn();
            String checkOut = order.getCheckOut();
            String range = checkIn + "~" + checkOut;
            int offset = (int) getDaySub(checkIn, checkOut);
            helper.setText(R.id.tv_order_content_desc, mContext.getResources().getString(R.string.item_hotel_date, range, offset, order.getTicketNum()));
            helper.setText(R.id.tv_order_content_category, order.getHotelJsonVo().getRoomType());
            helper.setText(R.id.tv_order_content_title, order.getHotelJsonVo().getHotelName());
        } else if (OrderTypeMeta.FILM.equals(type)) {
            operationButton(helper, true);
            helper.setText(R.id.tv_order_content_title, order.getOrderName());
            helper.setText(R.id.tv_order_content_desc, convertFilmInfo(order));
            helper.setText(R.id.tv_order_content_category, order.getCinemaJsonVo().getCinemaName());
        } else {
            //商城订单
            operationButton(helper, false);
            helper.setText(R.id.tv_order_content_title, order.getOrderName());
            helper.setText(R.id.tv_order_content_category, mContext.getString(R.string.mall_product_category, getShopOrderTypeDesc(order.getType())));
            helper.setText(R.id.tv_order_content_desc, mContext.getString(R.string.order_number_text, order.getOrderNo()));
        }


        String retainOneDecimal = String.format(Locale.CHINA, "%.2f", Float.parseFloat(order.getAmount()));
        int priceFormatId = "scorepay".equals(order.getPaySource()) ? R.string.item_car_coin_pay_amount : R.string.item_pay_amount;
        helper.setText(R.id.tv_pay_amount,
                Html.fromHtml(mContext.getResources().getString(priceFormatId, retainOneDecimal)));
        helper.setText(R.id.tv_create_time, mContext.getResources().getString(R.string.item_order_create_date, convertDate(order.getCreateDate())));

        //TODO 显示订单支付时间
        if (order.getOrderStatusId() == 2) {
            dynamicMargin(helper, R.id.bt_call_phone, mContext.getResources().getDimensionPixelOffset(R.dimen.size_mine_order_item_button));
            helper.setGone(R.id.bt_pay_order, true);
            helper.setGone(R.id.tv_order_time, true);
            int interval = OrderTimer.calculationInterval(order.getCurrentDate(), order.getLastpayDate());
            int min = interval / 60 + 1;
            String timeFormat;
            if (min >= 10) {
                timeFormat = "%02d";
            } else {
                timeFormat = "%d";
            }

            String curMin = String.format(Locale.CHINA, timeFormat, min);
            helper.setText(R.id.tv_order_time, mContext.getResources().getString(R.string.item_pay_remain_time, curMin));
            helper.setText(R.id.tv_pay_status, R.string.order_wait_pay);

            //TODO 禁止删除订单
            SlideView slideView = helper.getView(R.id.slide_view);
            slideView.setSlidingEnable(false);
        } else {
            dynamicMargin(helper, R.id.bt_call_phone, 0);
            helper.setGone(R.id.tv_order_time, false);
            helper.setGone(R.id.bt_pay_order, false);

            if (order.getOrderStatusId() == 3) {
                helper.setText(R.id.tv_pay_status, R.string.order_completed);
            } else {
                helper.setText(R.id.tv_pay_status, R.string.order_closed);
            }
        }
    }


    private String convertDate(long date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        return dateFormat.format(new Date(date));
    }


    private String convertFilmInfo(OrderInfo.Order order) {
        StringBuilder builder = new StringBuilder();
        //日期
        String consumeDate = order.getOrderDate();
        String[] splitDate = splitString(consumeDate, "-");
        String month = splitDate[1];
        String day = splitDate[2].substring(0, 2);
        String date = mContext.getResources().getString(R.string.item_Film_date, month, day);
        builder.append(date);
        builder.append(" ");

        //座位
        String seat = order.getCinemaJsonVo().getSeat();
        if (!TextUtils.isEmpty(seat)) {
            List<String> seatList = new ArrayList<>();
            if (seat.contains("|")) {
                seatList = Arrays.asList(splitString(seat, "\\|"));
            } else {
                seatList.add(seat);
            }

            for (int i = 0; i < seatList.size(); i++) {
                String temp = seatList.get(i);
                int splitIndex = temp.indexOf(":");
                String number1 = temp.substring(0, splitIndex);
                String number2 = temp.substring(splitIndex + 1);
                builder.append(number1);
                builder.append(mContext.getString(R.string.unit_row));
                builder.append(number2);
                builder.append(mContext.getString(R.string.unit_seat));

                if (i != seatList.size() - 1) {
                    builder.append("、");
                }
            }
        }
        return builder.toString();
    }


    private String[] splitString(String content, String regex) {
        return content.split(regex);
    }


    private void dynamicMargin(BaseViewHolder helper, int resId, int start) {
        Button curBt = helper.getView(resId);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) curBt.getLayoutParams();
        layoutParams.setMarginStart(start);
        curBt.setLayoutParams(layoutParams);
    }


    private long getDaySub(String beginDateStr, String endDateStr) {
        long day;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Date beginDate;
        Date endDate;
        try {
            beginDate = format.parse(beginDateStr);
            endDate = format.parse(endDateStr);
            day = (endDate.getTime() - beginDate.getTime()) / (24 * 60 * 60 * 1000);
        } catch (ParseException e) {
            KLog.w(TAG, "Parse failed.");
            return 0;
        }
        return day;
    }


    private String getShopOrderTypeDesc(String type) {
        String tempType = "";
        switch (type) {
            case OrderTypeMeta.SKIN:
                tempType = mContext.getString(R.string.order_type_skin);
                break;
            case OrderTypeMeta.SKU:
                tempType = mContext.getString(R.string.order_type_sku);
                break;
            case OrderTypeMeta.TRAFFIC:
                tempType = mContext.getString(R.string.order_type_traffic);
                break;
            case OrderTypeMeta.HOLOGRAM:
                tempType = mContext.getString(R.string.order_type_hologram);
                break;
            case OrderTypeMeta.VEHICLE:
                tempType = mContext.getString(R.string.order_type_vehicle);
                break;

        }
        return tempType;
    }


    class AopBaseViewHolder extends BaseViewHolder {

        public AopBaseViewHolder(View view) {
            super(view);
        }

        @Override
        public BaseViewHolder addOnClickListener(int viewId) {
            getChildClickViewIds().add(viewId);
            final View localView = getView(viewId);
            if (localView != null) {
                if (!localView.isClickable()) {
                    localView.setClickable(true);
                }
                localView.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
                    @Override
                    public ItemEvent returnPositionEventMsg(View view) {
                        return new ItemEvent("item_" + getClickPosition(), "ViewId_" + localView.getId());
                    }

                    @Override
                    @BusinessOnClick
                    public void onClick(View v) {
                        if (getOnItemChildClickListener() != null) {
                            getOnItemChildClickListener().onItemChildClick(MineOrderAdapter.this, v, getClickPosition());
                        }
                    }
                });
            }
            return this;
        }

        private int getClickPosition() {
            if (getLayoutPosition() >= getHeaderLayoutCount()) {
                return getLayoutPosition() - getHeaderLayoutCount();
            }
            return 0;
        }
    }

}
