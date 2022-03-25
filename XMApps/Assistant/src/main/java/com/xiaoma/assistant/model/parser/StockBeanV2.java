package com.xiaoma.assistant.model.parser;

import java.io.Serializable;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/11/14
 * Desc:
 */
public class StockBeanV2 implements Serializable {
    public static final int STOCK_TYPE_CN = 0;//A股
    public static final int STOCK_TYPE_HK = 1;//港股
    public static final int STOCK_TYPE_US = 2;//美股

    private String updateTime;//更新时间
    private String code;//股票代码
    private String name;//股票名字
    private String currentPrice;//当前价格
    private String change;//涨/跌幅
    private String jinKai;//今开
    private String zuoShou;//昨收
    private String turnoverRate;//换手率
    private String maxPrice;//最高价
    private String minPrice;//最低价
    private String total;//成交额
    private int stockType;//股票类型
    private boolean isUp;

    public int getStockType() {
        return stockType;
    }

    public void setStockType(int stockType) {
        this.stockType = stockType;
    }

    public boolean isUp() {
        return isUp;
    }

    public void setUp(boolean up) {
        isUp = up;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(String currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getJinKai() {
        return jinKai;
    }

    public void setJinKai(String jinKai) {
        this.jinKai = jinKai;
    }

    public String getZuoShou() {
        return zuoShou;
    }

    public void setZuoShou(String zuoShou) {
        this.zuoShou = zuoShou;
    }

    public String getTurnoverRate() {
        return turnoverRate;
    }

    public void setTurnoverRate(String turnoverRate) {
        this.turnoverRate = turnoverRate;
    }

    public String getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(String maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "StockBeanV2{" +
                "updateTime='" + updateTime + '\'' +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", currentPrice='" + currentPrice + '\'' +
                ", change='" + change + '\'' +
                ", jinKai='" + jinKai + '\'' +
                ", zuoShou='" + zuoShou + '\'' +
                ", turnoverRate='" + turnoverRate + '\'' +
                ", maxPrice='" + maxPrice + '\'' +
                ", minPrice='" + minPrice + '\'' +
                ", total='" + total + '\'' +
                ", stockType=" + stockType +
                ", isUp=" + isUp +
                '}';
    }
}
