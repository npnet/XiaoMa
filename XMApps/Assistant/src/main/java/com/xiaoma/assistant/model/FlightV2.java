package com.xiaoma.assistant.model;

import java.util.List;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/30
 * Desc:后台查询结果
 */
public class FlightV2 {

    public DataBean data;
    public String resultCode;
    public String resultMessage;

    public static class DataBean {

        public int count;
        public List<ListBean> list;

        public static class ListBean {

            public String flightNum;
            public String aexpected;
            public String airPortName;
            public String depTime;
            public String dexpected;
            public String ontimerate;
            public String depTerminal;
            public String arrTerminal;
            public String airline;
            public String dePortName;
            public String arrTime;
            public String totalTime;
            public String date;
            public String url;
            public String depCity;
            public String arrCity;
        }

    }

}
