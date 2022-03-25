package com.xiaoma.shop.business.model;

import android.text.TextUtils;

import com.xiaoma.shop.common.constant.ShopContract;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/05/28
 * @Describe:
 */

public class BoughtVehicleSoundBean {
    private String name;
    private String url;
    private int size;

    private boolean isBuy;
    private double discountPrice;
    private double price;
    private int discountScorePrice;
    private int scorePrice;
    private String iconPathUrl;
    private String tagPath;
    private String appName;
    private int usedNum;
    private int defaultShowNum;
    @ShopContract.Pay
    private transient int pay;

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isBuy() {
        return isBuy;
    }

    public void setBuy(boolean buy) {
        isBuy = buy;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getUrl() {
        return TextUtils.isEmpty(url)?"":url;
    }

    public void setUrl(String url) {
        this.url = url;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getDiscountScorePrice() {
        return discountScorePrice;
    }

    public void setDiscountScorePrice(int discountScorePrice) {
        this.discountScorePrice = discountScorePrice;
    }

    public int getScorePrice() {
        return scorePrice;
    }

    public void setScorePrice(int scorePrice) {
        this.scorePrice = scorePrice;
    }

    public int getPay() {
        return pay;
    }

    public void setPay(int pay) {
        this.pay = pay;
    }

    public String getIconPathUrl() {
        return iconPathUrl;
    }

    public void setIconPathUrl(String iconPathUrl) {
        this.iconPathUrl = iconPathUrl;
    }

    public String getTagPath() {
        return tagPath;
    }

    public void setTagPath(String tagPath) {
        this.tagPath = tagPath;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getUsedNum() {
        return usedNum;
    }

    public void setUsedNum(int usedNum) {
        this.usedNum = usedNum;
    }

    public int getDefaultShowNum() {
        return defaultShowNum;
    }

    public void setDefaultShowNum(int defaultShowNum) {
        this.defaultShowNum = defaultShowNum;
    }
}
