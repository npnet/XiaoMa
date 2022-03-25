package com.xiaoma.personal.order.constants;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <pre>
 *       @author Created by Gillben
 *       date: 2019/3/15 0015 15:17
 *       desc：订单状态    1：待支付   2：待确认、待退款  3：已完成  4：已关闭、已取消、已过期
 * </pre>
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({OrderStatusMeta.CLOSED,
        OrderStatusMeta.WAIT_CONFIRM,
        OrderStatusMeta.WAIT_PAY,
        OrderStatusMeta.COMPLETED})
public @interface OrderStatusMeta {

    int WAIT_PAY = 1;
    int WAIT_CONFIRM = 2;
    int COMPLETED = 3;
    int CLOSED = 4;
}



