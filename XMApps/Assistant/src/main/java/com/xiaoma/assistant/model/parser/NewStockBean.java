package com.xiaoma.assistant.model.parser;

import java.util.List;

/**
 * @Author ZiXu Huang
 * @Data 2019/4/15
 */
public class NewStockBean {

    /**
     * type : stock
     * intent : hongkong_stock
     * tts : 腾讯控股现在每股396.4港元，涨幅0.66%，总市值:37719.93亿港元
     * data : {"change":"+2.600 (+0.66%)","code":"00700","current_price":"396.400","name":"腾讯控股","time":"2019/04/15 11:08:12","trade_condition":"开盘中","kurl":"http://image.sinajs.cn/newchart/hk_stock/min/00700.gif?rand=155529779514537","info":[{"color":"#f54545","name":"今开","value":"395.600"},{"color":"","name":"昨收","value":"393.800"},{"color":"#f54545","name":"最高","value":"398.400"},{"color":"#f54545","name":"最低","value":"394.200"},{"color":"","name":"换手率","value":"0.06%"},{"color":"","name":"成交量","value":"617.38万"},{"color":"","name":"市盈率","value":"41.73"},{"color":"","name":"总市值","value":"37719.93亿"},{"color":"","name":"流通市值","value":"37719.93亿"},{"color":"","name":"成交额","value":"24.51亿"}]}
     */

    private String type;
    private String intent;
    private String tts;
    private DataBean data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getTts() {
        return tts;
    }

    public void setTts(String tts) {
        this.tts = tts;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * change : +2.600 (+0.66%)
         * code : 00700
         * current_price : 396.400
         * name : 腾讯控股
         * time : 2019/04/15 11:08:12
         * trade_condition : 开盘中
         * kurl : http://image.sinajs.cn/newchart/hk_stock/min/00700.gif?rand=155529779514537
         * info : [{"color":"#f54545","name":"今开","value":"395.600"},{"color":"","name":"昨收","value":"393.800"},{"color":"#f54545","name":"最高","value":"398.400"},{"color":"#f54545","name":"最低","value":"394.200"},{"color":"","name":"换手率","value":"0.06%"},{"color":"","name":"成交量","value":"617.38万"},{"color":"","name":"市盈率","value":"41.73"},{"color":"","name":"总市值","value":"37719.93亿"},{"color":"","name":"流通市值","value":"37719.93亿"},{"color":"","name":"成交额","value":"24.51亿"}]
         */

        private String change;
        private String code;
        private String current_price;
        private String name;
        private String time;
        private String trade_condition;
        private String kurl;
        private List<InfoBean> info;

        public String getChange() {
            return change;
        }

        public void setChange(String change) {
            this.change = change;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getCurrent_price() {
            return current_price;
        }

        public void setCurrent_price(String current_price) {
            this.current_price = current_price;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getTrade_condition() {
            return trade_condition;
        }

        public void setTrade_condition(String trade_condition) {
            this.trade_condition = trade_condition;
        }

        public String getKurl() {
            return kurl;
        }

        public void setKurl(String kurl) {
            this.kurl = kurl;
        }

        public List<InfoBean> getInfo() {
            return info;
        }

        public void setInfo(List<InfoBean> info) {
            this.info = info;
        }

        public static class InfoBean {
            /**
             * color : #f54545
             * name : 今开
             * value : 395.600
             */

            private String color;
            private String name;
            private String value;

            public String getColor() {
                return color;
            }

            public void setColor(String color) {
                this.color = color;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }
    }
}
