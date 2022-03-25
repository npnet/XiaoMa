package com.xiaoma.service.order.adapter;

import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.service.R;
import com.xiaoma.service.order.model.OrderTime;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;

/**
 * 预约时间adapter
 * Created by zhushi.
 * Date: 2018/11/14
 */
public class OrderTimeAdapter extends XMBaseAbstractBQAdapter<OrderTime, BaseViewHolder> {

    private SelectOrderTimeListener selectOrderTimeListener;

    public OrderTimeAdapter() {
        super(R.layout.item_order_time);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final OrderTime item) {
        final TextView itemOrderTimeTv = helper.getView(R.id.item_order_time);
        //是否选择
        itemOrderTimeTv.setText(item.getTimePhase());
        itemOrderTimeTv.setSelected(item.isSelected());
        itemOrderTimeTv.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                return new ItemEvent(item.getTimePhase(), item.getId()+"");
            }

            @Override
            @BusinessOnClick
            public void onClick(View v) {
                for (OrderTime orderTime : mData) {
                    orderTime.setSelected(false);
                }
                if (!item.isSelected()) {
                    item.setSelected(true);
                }
                if (selectOrderTimeListener != null) {
                    selectOrderTimeListener.setSelectOrderTime(helper.getAdapterPosition(), item);
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(getData().get(position).getTimePhase(), getData().get(position).getId()+"");
    }

    public interface SelectOrderTimeListener {
        void setSelectOrderTime(int position, OrderTime orderTime);
    }

    public void setSelectOrderTimeListener(SelectOrderTimeListener selectOrderTimeListener) {
        this.selectOrderTimeListener = selectOrderTimeListener;
    }
}
