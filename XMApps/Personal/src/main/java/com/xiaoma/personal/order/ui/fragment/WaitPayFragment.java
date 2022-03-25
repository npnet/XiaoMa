package com.xiaoma.personal.order.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.personal.R;
import com.xiaoma.personal.common.util.EventConstants;
import com.xiaoma.personal.order.constants.OrderStatusMeta;

/**
 * <pre>
 *       @author Created by Gillben
 *       date: 2019/3/12 0012 9:52
 *       desc：待支付
 * </pre>
 */
@PageDescComponent(EventConstants.PageDescribe.waitPayFragment)
public class WaitPayFragment extends BaseOrderFragment {

    private static final String REFRESH_WAIT_PAY_ACTION = "REFRESH_WAIT_PAY_ACTION";

    public static WaitPayFragment newInstance() {
        return new WaitPayFragment();
    }


    @Override
    protected int getOrderType() {
        return OrderStatusMeta.WAIT_PAY;
    }

    @Override
    protected String getOrderTypeName() {
        return getString(R.string.order_wait_pay);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        register();
    }

    private void register() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(REFRESH_WAIT_PAY_ACTION);
        mContext.registerReceiver(refreshReceiver, filter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(refreshReceiver);
    }


    private BroadcastReceiver refreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(WaitPayFragment.class.getSimpleName(),
                    "Handle wait pay refresh.");

            if (intent != null && REFRESH_WAIT_PAY_ACTION.equals(intent.getAction())) {
                fetchData();
            }
        }
    };

}
