package com.xiaoma.personal.order.ui.fragment;

import com.xiaoma.personal.R;
import com.xiaoma.personal.order.constants.OrderStatusMeta;

/**
 * <pre>
 *       @author Created by Gillben
 *       date: 2019/3/12 0012 9:52
 *       desc：待确认、待退款
 * </pre>
 */
public class WaitUseFragment extends BaseOrderFragment {

    public static WaitUseFragment newInstance() {
        return new WaitUseFragment();
    }

    @Override
    protected int getOrderType() {
        return OrderStatusMeta.WAIT_CONFIRM;
    }

    @Override
    protected String getOrderTypeName() {
        return getString(R.string.order_wait_use);
    }


}
