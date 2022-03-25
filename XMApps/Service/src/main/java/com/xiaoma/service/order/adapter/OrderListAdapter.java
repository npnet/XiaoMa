package com.xiaoma.service.order.adapter;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.service.R;
import com.xiaoma.service.common.constant.EventConstants;
import com.xiaoma.service.common.model.OrderUploadModel;
import com.xiaoma.service.order.model.OrderBean;
import com.xiaoma.service.order.ui.OrderDetailDialog;
import com.xiaoma.ui.adapter.XMBaseAbstractBQAdapter;
import com.xiaoma.utils.GsonHelper;

import java.text.SimpleDateFormat;

/**
 * Created by zhushi.
 * Date: 2018/12/25
 */
public class OrderListAdapter extends XMBaseAbstractBQAdapter<OrderBean, BaseViewHolder> {
    private int mType;
    private TextView mCarShopName;
    private TextView mOrderStatus;
    private TextView mOrderNumbering;
    private TextView mOrderTime;
    private TextView mProjectStatus;
    private TextView mSeeOrderDel;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
    private final int orderTypeNum = 3;
    public static final String ORDER_DATA = "order_data";
    public static final String ORDER_POSITION = "order_position";

    public OrderListAdapter(int type) {
        super(R.layout.order_item);
        this.mType = type;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final OrderBean item) {
        initView(helper);
        initData(item);
        helper.itemView.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                OrderUploadModel model = new OrderUploadModel(item.getOrderType(), item.getSalerId());
                return new ItemEvent(EventConstants.NormalClick.checkOrderDetail, GsonHelper.toJson(model));
            }

            @Override
            @BusinessOnClick
            public void onClick(View v) {
                Intent intent = new Intent(mContext, OrderDetailDialog.class);
                intent.putExtra(ORDER_DATA, item);
                intent.putExtra(ORDER_POSITION, helper.getAdapterPosition());
                mContext.startActivity(intent);
            }
        });
    }

    private void initView(BaseViewHolder helper) {
        mSeeOrderDel = helper.getView(R.id.see_order_detail);
        mCarShopName = helper.getView(R.id.car_shop_name);
        mOrderStatus = helper.getView(R.id.order_status);
        mOrderNumbering = helper.getView(R.id.order_numbering);
        mOrderTime = helper.getView(R.id.order_time);
        mProjectStatus = helper.getView(R.id.project_status);
    }

    private void initData(OrderBean item) {
        mCarShopName.setText(item.getCompanyName());
        mOrderStatus.setText(item.getVStatus());
        mOrderNumbering.setText(mContext.getString(R.string.order_number, item.getVbillno()));
        String time = dateFormat.format(item.getCreateDate());
        mOrderTime.setText(mContext.getString(R.string.order_time, time));
        mProjectStatus.setText(getOrderType(item.getOrderType()));
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(getData().get(position).getVStatus(), getData().get(position).getVbillno());
    }


    private String getOrderType(String orderType) {

        String[] arr = orderType.split(";"); // 用;分割


        StringBuffer stringBuffer = new StringBuffer();

        for (int i = 0; i < arr.length; i++) {
            if (i >= orderTypeNum) {
                stringBuffer.replace(stringBuffer.length() - 2, stringBuffer.length(), "...");
                break;
            }
            stringBuffer.append("• ").append(arr[i]).append("\t\t");
        }


        return stringBuffer.toString();
    }

}
