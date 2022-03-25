package com.xiaoma.personal.order.constants;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/6/10 0010 15:29
 *   desc:   订单状态对应id
 * </pre>
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({OrderStatusId.DEFAULT,
        OrderStatusId.CANCEL,
        OrderStatusId.WAIT_CONFIRM,
        OrderStatusId.WAIT_PAY,
        OrderStatusId.COMPLETE,
        OrderStatusId.EXPIRED,
        OrderStatusId.CLOSED,
        OrderStatusId.WAIT_REFUND})
public @interface OrderStatusId {

    int DEFAULT = -1;           //异常单
    int CANCEL = 0;             //已取消
    int WAIT_CONFIRM = 1;       //待确认
    int WAIT_PAY = 2;           //待支付
    int COMPLETE = 3;           //已完成
    int EXPIRED = 7;            //已过期
    int CLOSED = 6;             //已关闭
    int WAIT_REFUND = 8;        //待退款

}
