package com.xiaoma.assistant.model.parser;

/**
 * @Author ZiXu Huang
 * @Data 2019/3/4
 */
public class NewsSlots {

    /**
     * datetime : {"date":"2019-03-04","dateOrig":"今天","type":"DT_BASIC"}
     */
    private String keyWord;

    public String category;

    private DatetimeBean datetime;

    public DatetimeBean getDatetime() {
        return datetime;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDatetime(DatetimeBean datetime) {
        this.datetime = datetime;
    }

    public static class DatetimeBean {
        /**
         * date : 2019-03-04
         * dateOrig : 今天
         * type : DT_BASIC
         */

        private String date;
        private String dateOrig;
        private String type;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getDateOrig() {
            return dateOrig;
        }

        public void setDateOrig(String dateOrig) {
            this.dateOrig = dateOrig;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
