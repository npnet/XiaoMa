package com.xiaoma.assistant.model.hologram;

import com.google.gson.annotations.SerializedName;
import com.xiaoma.utils.GsonHelper;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/7/1
 */
public class GoodsTrackInfo {

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
    private String count;
    @SerializedName("m")
    private String tag;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return GsonHelper.toJson(this);
    }
}
