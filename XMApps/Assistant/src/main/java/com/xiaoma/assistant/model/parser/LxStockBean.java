package com.xiaoma.assistant.model.parser;

import java.io.Serializable;
import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/11/14
 * Desc:
 */
public class LxStockBean implements Serializable {
    /**
     * head : 查询到1条信息！
     * logoUrl : http://autodev.openspeech.cn/static/source/logo/stock/logo_sina.png
     * result : {"allList":[{"closingPrice":"52.940","currentPrice":"53.070","highPrice":"53.640","lowPrice":"52.310","mbmChartUrl":"http://image.sinajs.cn/newchart/min/n/sz002230.gif","name":"科大讯飞","openingPrice":"53.000","riseRate":"0.246%","riseValue":"0.13","stockCode":"sz002230","updateDateTime":"2017-09-22 15:05:03","url":"http://stock1.sina.cn/prog/wapsite/stock/v2/daily.php?code=sz002230&amp;img=1"}],"total":1}
     * sourceName : 新浪
     * state : success
     * version : v1.0
     */

    // 数据来源
    private String sourceName;
    // 查询状态
    private String state;
    // 数据结果
    private StockContainer result;
    private String head;
    private String logoUrl;
    private String version;

    public String getSourceName() {
        return sourceName;
    }

    public String getState() {
        return state;
    }

    public Stock getStock() {
        if (result == null || result.getList() == null || result.getList().isEmpty()) {
            return null;
        }
        Stock stock = result.getList().get(0);
        return stock;
    }

    public static class StockContainer {
        /**
         * allList : [{"closingPrice":"52.940","currentPrice":"53.070","highPrice":"53.640","lowPrice":"52.310","mbmChartUrl":"http://image.sinajs.cn/newchart/min/n/sz002230.gif","name":"科大讯飞","openingPrice":"53.000","riseRate":"0.246%","riseValue":"0.13","stockCode":"sz002230","updateDateTime":"2017-09-22 15:05:03","url":"http://stock1.sina.cn/prog/wapsite/stock/v2/daily.php?code=sz002230&amp;img=1"}]
         * total : 1
         */

        private int total;
        private List<Stock> list;

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<Stock> getList() {
            return list;
        }

        public void setList(List<Stock> list) {
            this.list = list;
        }
    }

    public static class Stock {
        /**
         * closingPrice : 52.940
         * currentPrice : 53.070
         * highPrice : 53.640
         * lowPrice : 52.310
         * mbmChartUrl : http://image.sinajs.cn/newchart/min/n/sz002230.gif
         * name : 科大讯飞
         * openingPrice : 53.000
         * riseRate : 0.246%
         * riseValue : 0.13
         * stockCode : sz002230
         * updateDateTime : 2017-09-22 15:05:03
         * url : http://stock1.sina.cn/prog/wapsite/stock/v2/daily.php?code=sz002230&amp;img=1
         */

        private String closingPrice;
        private String currentPrice;
        private String highPrice;
        private String lowPrice;
        private String mbmChartUrl;
        private String name;
        private String openingPrice;
        private String riseRate;
        private String riseValue;
        private String stockCode;
        private String updateDateTime;
        private String url;

        public String getClosingPrice() {
            return closingPrice;
        }

        public void setClosingPrice(String closingPrice) {
            this.closingPrice = closingPrice;
        }

        public String getCurrentPrice() {
            return currentPrice;
        }

        public void setCurrentPrice(String currentPrice) {
            this.currentPrice = currentPrice;
        }

        public String getHighPrice() {
            return highPrice;
        }

        public void setHighPrice(String highPrice) {
            this.highPrice = highPrice;
        }

        public String getLowPrice() {
            return lowPrice;
        }

        public void setLowPrice(String lowPrice) {
            this.lowPrice = lowPrice;
        }

        public String getMbmChartUrl() {
            return mbmChartUrl;
        }

        public void setMbmChartUrl(String mbmChartUrl) {
            this.mbmChartUrl = mbmChartUrl;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getOpeningPrice() {
            return openingPrice;
        }

        public void setOpeningPrice(String openingPrice) {
            this.openingPrice = openingPrice;
        }

        public String getRiseRate() {
            return riseRate;
        }

        public void setRiseRate(String riseRate) {
            this.riseRate = riseRate;
        }

        public String getRiseValue() {
            return riseValue;
        }

        public void setRiseValue(String riseValue) {
            this.riseValue = riseValue;
        }

        public String getStockCode() {
            return stockCode;
        }

        public void setStockCode(String stockCode) {
            this.stockCode = stockCode;
        }

        public String getUpdateDateTime() {
            return updateDateTime;
        }

        public void setUpdateDateTime(String updateDateTime) {
            this.updateDateTime = updateDateTime;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
