package com.xiaoma.shop.business.pay;


/**
 * <pre>
 *   @author Create by on Gillben
 *   date：2019/4/12 0012:16:06
 *   desc：支付成功后结果处理回调
 * </pre>
 */
public interface PaySuccessResultCallback {

    /**
     * 购买成功
     */
    void confirm();

    /**
     * 购买失败  扫码支付无需处理
     */
    void cancel();
}
