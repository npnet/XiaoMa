package com.xiaoma.assistant.model.parser;

import java.io.Serializable;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/11/14
 * Desc:
 */
public class ConstellationXfParser implements Serializable {

    /**
     * date : {"year":"2017","day":"25","month":"5"}
     * time : S
     */

    public WhenBean when;
    /**
     * when : {"date":{"year":"2017","day":"25","month":"5"},"time":"S"}
     * constellation : 射手座
     */

    public String constellation;

    public static class WhenBean {
        /**
         * year : 2017
         * day : 25
         * month : 5
         */

        public DateBean date;
        public String time;

        public static class DateBean {
            public int year;
            public int day;
            public int month;
        }
    }
}
