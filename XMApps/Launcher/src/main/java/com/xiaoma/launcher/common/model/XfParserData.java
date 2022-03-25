package com.xiaoma.launcher.common.model;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.common.model
 *  @file_name:      XfParserData
 *  @author:         Rookie
 *  @create_time:    2019/4/11 14:59
 *  @description：   TODO             */

public class XfParserData {

    /**
     * bislocalresult : 1
     * data : {"result":null}
     * demand_semantic : null
     * extra : {"data_finded_match":false,"match_gram":"general_gram","multi_service":null,"nlp_match_info":"---------+++++","nlp_match_score":"0.714","nlp_score":"-1.503","org_sent":"提醒我明天下午三点在公司开会","sem_route":"提醒我scheduleX_datetime.datescheduleX_datetime.intervalscheduleX_datetime.timescheduleX_content","trim_sent":"提醒我明天下午三点在公司开会","used_hist_depth":0,"user_data":null,"user_defined_params":null,"usrid":"car"}
     * history : [{"meanings":[null],"state":{"fg::scheduleX::default::default":{"state":"default"}},"used_state":{"state":"default","state_key":"fg::scheduleX::default::default"}}]
     * nlocalconfidencescore : 0
     * operation : CREATE
     * orig_semantic : {"slots":{"content":"在公司开会","datetime.INTERVAL":"下午","datetime.date":"明天","datetime.time":"三点","name":"reminder","operation":"CREATE"}}
     * rc : 0
     * save_history : null
     * score : 0.714
     * search_semantic : null
     * semantic : {"slots":{"content":"在公司开会","datetime":{"date":"2019-04-18","dateOrig":"明天","time":"15:00:00","timeOrig":"下午三点","type":"DT_BASIC"},"name":"reminder"}}
     * service : scheduleX
     * state : {"fg::scheduleX::default::default":{"state":"default"}}
     * text : 提醒我明天下午三点在公司开会
     * used_state : {"state":"default","state_key":"fg::scheduleX::default::default"}
     * version : 1.0.1052
     */


    private SemanticBean semantic;
    private String service;

    public SemanticBean getSemantic() {
        return semantic;
    }

    public void setSemantic(SemanticBean semantic) {
        this.semantic = semantic;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public static class SemanticBean {
        /**
         * slots : {"content":"在公司开会","datetime":{"date":"2019-04-18","dateOrig":"明天","time":"15:00:00","timeOrig":"下午三点","type":"DT_BASIC"},"name":"reminder"}
         */

        private SemanticBean.SlotsBeanX slots;

        public SemanticBean.SlotsBeanX getSlots() {
            return slots;
        }

        public void setSlots(SemanticBean.SlotsBeanX slots) {
            this.slots = slots;
        }

        public static class SlotsBeanX {
            /**
             * content : 在公司开会
             * datetime : {"date":"2019-04-18","dateOrig":"明天","time":"15:00:00","timeOrig":"下午三点","type":"DT_BASIC"}
             * name : reminder
             */

            private String content;
            private SemanticBean.SlotsBeanX.DatetimeBean datetime;
            private String name;

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public SemanticBean.SlotsBeanX.DatetimeBean getDatetime() {
                return datetime;
            }

            public void setDatetime(SemanticBean.SlotsBeanX.DatetimeBean datetime) {
                this.datetime = datetime;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public static class DatetimeBean {
                /**
                 * date : 2019-04-18
                 * dateOrig : 明天
                 * time : 15:00:00
                 * timeOrig : 下午三点
                 * type : DT_BASIC
                 */

                private String date;
                private String dateOrig;
                private String time;
                private String timeOrig;
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

                public String getTime() {
                    return time;
                }

                public void setTime(String time) {
                    this.time = time;
                }

                public String getTimeOrig() {
                    return timeOrig;
                }

                public void setTimeOrig(String timeOrig) {
                    this.timeOrig = timeOrig;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }
            }
        }
    }

}
