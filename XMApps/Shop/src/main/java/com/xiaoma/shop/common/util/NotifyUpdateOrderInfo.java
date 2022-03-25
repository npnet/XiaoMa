package com.xiaoma.shop.common.util;

import android.content.Intent;

import com.xiaoma.component.AppHolder;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/08/12
 * @Describe:
 */
public class NotifyUpdateOrderInfo {
    private static final String REFRESH_WAIT_PAY_ACTION = "REFRESH_WAIT_PAY_ACTION";

    public static void sendNotifyMsg() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(REFRESH_WAIT_PAY_ACTION);
        AppHolder.getInstance().getAppContext().sendBroadcast(intent);
    }
}
