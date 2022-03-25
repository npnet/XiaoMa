package com.xiaoma.music.common.pay;

import android.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/6/27 0027 10:36
 *   desc:   酷我订单状态
 * </pre>
 */
@Retention(RetentionPolicy.SOURCE)
@StringDef({KWOrderStatus.CREATE_ORDER,
        KWOrderStatus.SUCCESS,
        KWOrderStatus.FINISHED,
        KWOrderStatus.CANCEL,
        KWOrderStatus.PAY_FAILED})
public @interface KWOrderStatus {

    String CREATE_ORDER = "0";
    String SUCCESS = "1";
    String FINISHED = "4";
    String CANCEL = "5";
    String PAY_FAILED = "6";
}
