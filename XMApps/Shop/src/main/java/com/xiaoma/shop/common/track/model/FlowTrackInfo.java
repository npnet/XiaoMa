package com.xiaoma.shop.common.track.model;

import com.google.gson.annotations.SerializedName;
import com.xiaoma.utils.GsonHelper;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/7/4
 */
public class FlowTrackInfo {

    private int id;
    @SerializedName("value")
    private String name;
    @SerializedName("h")
    private String rmbPrice;
    @SerializedName("i")
    private String rmbPriceDiscount;
    @SerializedName("j")
    private String coinPrice;
    @SerializedName("k")
    private String coinPriceDiscount;
    @SerializedName("l")
    private String unUsedFlow;
    @SerializedName("m")
    private String totalFlow;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnUsedFlow() {
        return unUsedFlow;
    }

    public void setUnUsedFlow(String unUsedFlow) {
        this.unUsedFlow = unUsedFlow;
    }

    public String getTotalFlow() {
        return totalFlow;
    }

    public void setTotalFlow(String totalFlow) {
        this.totalFlow = totalFlow;
    }

    public String getCoinPrice() {
        return coinPrice;
    }

    public void setCoinPrice(String coinPrice) {
        this.coinPrice = coinPrice;
    }

    public String getCoinPriceDiscount() {
        return coinPriceDiscount;
    }

    public void setCoinPriceDiscount(String coinPriceDiscount) {
        this.coinPriceDiscount = coinPriceDiscount;
    }

    public String getRmbPrice() {
        return rmbPrice;
    }

    public void setRmbPrice(String rmbPrice) {
        this.rmbPrice = rmbPrice;
    }

    public String getRmbPriceDiscount() {
        return rmbPriceDiscount;
    }

    public void setRmbPriceDiscount(String rmbPriceDiscount) {
        this.rmbPriceDiscount = rmbPriceDiscount;
    }

    @Override
    public String toString() {
        return GsonHelper.toJson(this);
    }
}
