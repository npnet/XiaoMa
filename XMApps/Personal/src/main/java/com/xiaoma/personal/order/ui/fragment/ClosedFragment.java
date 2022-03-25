package com.xiaoma.personal.order.ui.fragment;

import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.personal.R;
import com.xiaoma.personal.common.util.EventConstants;
import com.xiaoma.personal.order.constants.OrderStatusMeta;

/**
 * <pre>
 *       @author Created by Gillben
 *       date: 2019/3/12 0012 9:53
 *       desc：
 * </pre>
 */
@PageDescComponent(EventConstants.PageDescribe.closedFragment)
public class ClosedFragment extends BaseOrderFragment {


    public static ClosedFragment newInstance() {
        return new ClosedFragment();
    }


    @Override
    protected int getOrderType() {
        return OrderStatusMeta.CLOSED;
    }

    @Override
    protected String getOrderTypeName() {
        return getString(R.string.order_closed);
    }


}
