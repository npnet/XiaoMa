package com.xiaoma.assistant.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author: iSun
 * @date: 2019/1/25 0025
 */
public class XfStockBean implements Serializable {


    public List<StockBean> result;

    public static class StockBean {
        /**
         * closingPrice : 28.78
         * currentPrice : 28.20
         * highPrice : 28.85
         * logoUrl : http://autodev.openspeech.cn/static/source/logo/stock/logo_jrj.png
         * lowPrice : 28.05
         * mbmChartUrl : null
         * name : 科大讯飞
         * openingPrice : 28.60
         * riseRate : -2.02%
         * riseValue : -0.58
         * sourceName : 金融界
         * stockCode : 002230
         * updateDateTime : 2019-01-25 16:45:15
         * url : null
         */

        public String closingPrice;
        public String currentPrice;
        public String highPrice;
        public String logoUrl;
        public String lowPrice;
        public Object mbmChartUrl;
        public String name;
        public String openingPrice;
        public String riseRate;
        public String riseValue;
        public String sourceName;
        public String stockCode;
        public String updateDateTime;
        public Object url;
    }
}
