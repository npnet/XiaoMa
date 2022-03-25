package com.xiaoma.assistant.model;


import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.pay.AlbumPriceTypeDetail;

/**
 * @author youthyJ
 * @date 2018/10/11
 */
public class XMAlbumPriceTypeDetail extends XMBean<AlbumPriceTypeDetail> {

    public XMAlbumPriceTypeDetail(AlbumPriceTypeDetail albumPriceTypeDetail) {
        super(albumPriceTypeDetail);
    }

    public int getPriceType() {
        return getSDKBean().getPriceType();
    }

    public void setPriceType(int priceType) {
        getSDKBean().setPriceType(priceType);
    }

    public double getPrice() {
        return getSDKBean().getPrice();
    }

    public void setPrice(double price) {
        getSDKBean().setPrice(price);
    }

    public double getDiscountedPrice() {
        return getSDKBean().getDiscountedPrice();
    }

    public void setDiscountedPrice(double discountedPrice) {
        getSDKBean().setDiscountedPrice(discountedPrice);
    }

    public String getPriceUnit() {
        return getSDKBean().getPriceUnit();
    }

    public void setPriceUnit(String priceUnit) {
        getSDKBean().setPriceUnit(priceUnit);
    }
}
