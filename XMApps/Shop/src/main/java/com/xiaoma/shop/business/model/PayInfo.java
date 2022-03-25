package com.xiaoma.shop.business.model;

import android.text.TextUtils;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/08/10
 * @Describe:
 */
public class PayInfo {
    private String productType;//产品类型
    private long  productId;//产品id
    private String price;//产品价格
    private String orderNum;//订单号
    private String paySource;//车币还是现金支付

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public long  getProductId() {
        return productId;
    }

    public void setProductId(long  productId) {
        this.productId = productId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getPaySource() {
        return paySource;
    }

    public void setPaySource(String paySource) {
        this.paySource = paySource;
    }


    public boolean isCoinPay() {
        if (TextUtils.isEmpty(paySource) || !"scorepay".equals(paySource)) return false;
        return true;
    }
}
