package com.xiaoma.assistant.model.parser;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/30
 * Desc:航班解析
 */
public class Flight {

    /**
     * semantic : {"slots":{"startDate":{"date":"2017-05-28","type":"DT_BASIC","dateOrig":"明天","endDateOrig":"5月31日","endDate":"2017-05-31"},"flightNo":"g5555","type":"往返","endLoc":{"type":"LOC_BASIC","cityAddr":"","city":"","areaAddr":"宁海","area":"宁海县"},"startLoc":{"cityAddr":"深圳","city":"深圳市","areaAddr":"","area":"","type":"LOC_BASIC"}}}
     * rc : 0
     * operation : QUERY
     * service : flight
     * text : 查询下深圳到宁海的航班
     */

    public SemanticBean semantic;
    public String operation;
    public String service;
    public String text;

    public static class SemanticBean {
        /**
         * slots : {"startDate":{"date":"2017-05-28","type":"DT_BASIC","dateOrig":"明天","endDateOrig":"5月31日","endDate":"2017-05-31"},"flightNo":"g5555","type":"往返","endLoc":{"type":"LOC_BASIC","cityAddr":"","city":"","areaAddr":"宁海","area":"宁海县"},"startLoc":{"cityAddr":"深圳","city":"深圳市","areaAddr":"","area":"","type":"LOC_BASIC"}}
         */

        public SlotsBean slots;

        public static class SlotsBean {
            /**
             * startDate : {"date":"2017-05-28","type":"DT_BASIC","dateOrig":"明天","endDateOrig":"5月31日","endDate":"2017-05-31"}
             * flightNo : g5555
             * type : 往返
             * endLoc : {"type":"LOC_BASIC","cityAddr":"","city":"","areaAddr":"宁海","area":"宁海县"}
             * startLoc : {"cityAddr":"深圳","city":"深圳市","areaAddr":"","area":"","type":"LOC_BASIC"}
             */

            public StartDateBean startDate;
            public String flightNo;
            public String type;
            public EndLocBean endLoc;
            public StartLocBean startLoc;

            public static class StartDateBean {
                /**
                 * date : 2017-05-28
                 * type : DT_BASIC
                 * dateOrig : 明天
                 * endDateOrig : 5月31日
                 * endDate : 2017-05-31
                 */

                public String date;
                public String type;
                public String dateOrig;
                public String endDateOrig;
                public String endDate;
            }

            public static class EndLocBean {
                /**
                 * type : LOC_BASIC
                 * cityAddr :
                 * city :
                 * areaAddr : 宁海
                 * area : 宁海县
                 */

                public String type;
                public String cityAddr;
                public String city;
                public String areaAddr;
                public String area;
            }

            public static class StartLocBean {
                /**
                 * cityAddr : 深圳
                 * city : 深圳市
                 * areaAddr :
                 * area :
                 * type : LOC_BASIC
                 */

                public String cityAddr;
                public String city;
                public String areaAddr;
                public String area;
                public String type;
            }
        }
    }
}
