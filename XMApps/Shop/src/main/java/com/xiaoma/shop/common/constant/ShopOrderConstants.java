package com.xiaoma.shop.common.constant;

public final class ShopOrderConstants {


    private ShopOrderConstants() {
        throw new RuntimeException("Not allow create instance.");
    }

    public static final String SHOP_URI = "xiaoma://com.xiaoma.shop/main_shop";
    public static final String SHOP_ACTION = "com.xiaoma.shop.assistan.jump";


    public static final String ORDER_ID = "ORDER_ID";                   //商品id
    public static final String ORDER_NUMBER = "ORDER_NUMBER";           //订单号
    public static final String PAY_TYPE = "PAY_TYPE";                   //支付订单类型
    public static final String PRODUCT_PRICE = "PRODUCT_PRICE";         //订单价格
    public static final String PAY_SOURCE = "PAY_SOURCE";               //支付类型   scorepay 表示车币支付，其余表示扫码
}