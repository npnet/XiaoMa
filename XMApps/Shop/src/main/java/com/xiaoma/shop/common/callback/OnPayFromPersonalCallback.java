package com.xiaoma.shop.common.callback;

import com.xiaoma.shop.business.model.PayInfo;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/08/10
 * @Describe: 来自个人中心的支付
 */
public interface OnPayFromPersonalCallback {
    void handlePay(PayInfo payInfo,boolean immediateExecute);
}
