package com.xiaoma.assistant.model.parser;

/**
 * User:Created by Terence
 * IDE: Android Studio
 * Date:2018/10/30
 * Desc:火车解析
 */
public class Train {

    /**
     * semantic : {"slots":{"startDate":{"date":"2017-05-28","type":"DT_BASIC","dateOrig":"明天","endDateOrig":"5月31日","endDate":"2017-05-31"},"code":"g5555","type":"往返","endLoc":{"type":"LOC_BASIC","cityAddr":"","city":"","areaAddr":"宁海","area":"宁海县"},"startLoc":{"cityAddr":"深圳","city":"深圳市","areaAddr":"","area":"","type":"LOC_BASIC"}}}
     * rc : 0
     * operation : QUERY
     * service : flight
     * text : 查询下深圳到宁海的火车
     */

    public SemanticBean semantic;
    public int rc;
    public String operation;
    public String service;
    public String text;

    public static class SemanticBean {
        /**
         * slots : {"startDate":{"date":"2017-05-28","type":"DT_BASIC","dateOrig":"明天","endDateOrig":"5月31日","endDate":"2017-05-31"},"code":"g5555","type":"往返","endLoc":{"type":"LOC_BASIC","cityAddr":"","city":"","areaAddr":"宁海","area":"宁海县"},"startLoc":{"cityAddr":"深圳","city":"深圳市","areaAddr":"","area":"","type":"LOC_BASIC"}}
         */

        public SlotsBean slots;

        public static class SlotsBean {
            /**
             * startDate : {"date":"2017-05-28","type":"DT_BASIC","dateOrig":"明天","endDateOrig":"5月31日","endDate":"2017-05-31"}
             * code : g5555
             * type : 往返
             * endLoc : {"type":"LOC_BASIC","cityAddr":"","city":"","areaAddr":"宁海","area":"宁海县"}
             * startLoc : {"cityAddr":"深圳","city":"深圳市","areaAddr":"","area":"","type":"LOC_BASIC"}
             */

            public StartDateBean startDate;
            public String code;
            public String type;
            public EndLocBean endLoc;
            public StartLocBean startLoc;
            public String category;

            public static class StartDateBean {
                /**
                 * date : 2017-05-28
                 * type : DT_BASIC
                 * dateOrig : 明天
                 * endDateOrig : 5月31日
                 * endDate : 2017-05-31
                 */

                public String date = "";
                public String type = "";
                public String dateOrig = "";
                public String endDateOrig = "";
                public String endDate = "";
            }

            public static class EndLocBean {
                /**
                 * type : LOC_BASIC
                 * cityAddr :
                 * city :
                 * areaAddr : 宁海
                 * area : 宁海县
                 */

                public String type = "";
                public String cityAddr = "";
                public String city = "";
                public String areaAddr = "";
                public String area = "";
                public String poi = "";
            }

            public static class StartLocBean {
                /**
                 * cityAddr : 深圳
                 * city : 深圳市
                 * areaAddr :
                 * area :
                 * type : LOC_BASIC
                 */

                public String cityAddr = "";
                public String city = "";
                public String areaAddr = "";
                public String area = "";
                public String type = "";
                public String poi = "";
            }
        }
    }

}