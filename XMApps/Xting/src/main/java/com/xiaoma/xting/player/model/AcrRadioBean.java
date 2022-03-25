package com.xiaoma.xting.player.model;

import java.util.List;

public class AcrRadioBean {

    /**
     * status : {"code":0,"msg":"OK"}
     * data : {"list":[{"name":"CNR中国之声","region":"广东省-深圳市-宝安区","short_name":["CNR中国之声"],"type":"AM","freq":"981","logo":"http://console.acrcloud.cn/car_radio/s1/386.jpg","playlist":[{"name":"千里共良宵","anchors":[],"start_time_utc":"2018-12-03 16:00:00","end_time_utc":"2018-12-03 18:00:00","is_playing":false},{"name":"结束曲","anchors":[],"start_time_utc":"2018-12-03 18:00:00","end_time_utc":"2018-12-03 18:05:00","is_playing":false},{"name":"停机检修","anchors":[],"start_time_utc":"2018-12-03 18:05:00","end_time_utc":"2018-12-03 20:25:00","is_playing":false},{"name":"开始曲","anchors":[],"start_time_utc":"2018-12-03 20:25:00","end_time_utc":"2018-12-03 20:30:00","is_playing":false},{"name":"中央农业广播学校","anchors":["中国之声主持人"],"start_time_utc":"2018-12-03 20:30:00","end_time_utc":"2018-12-03 21:00:00","is_playing":false},{"name":"悦动清晨","anchors":[],"start_time_utc":"2018-12-03 21:00:00","end_time_utc":"2018-12-03 22:00:00","is_playing":false},{"name":"国防时空","anchors":["中国之声主持人"],"start_time_utc":"2018-12-03 22:00:00","end_time_utc":"2018-12-03 22:30:00","is_playing":false},{"name":"新闻和报纸摘要","anchors":["中国之声主持人","于芳"],"start_time_utc":"2018-12-03 22:30:00","end_time_utc":"2018-12-03 23:00:00","is_playing":false},{"name":"新闻纵横","anchors":["林溪","子文","中国之声主持人","张蕾"],"start_time_utc":"2018-12-03 23:00:00","end_time_utc":"2018-12-04 01:00:00","is_playing":false},{"name":"央广新闻","anchors":["胡凡","中国之声主持人","长悦","刘鹤佳"],"start_time_utc":"2018-12-04 01:00:00","end_time_utc":"2018-12-04 04:00:00","is_playing":false},{"name":"全球华语广播网","anchors":["何方","中国之声主持人","郭燕","杨扬"],"start_time_utc":"2018-12-04 04:00:00","end_time_utc":"2018-12-04 05:00:00","is_playing":false},{"name":"央广新闻","anchors":["胡凡","中国之声主持人","长悦","刘鹤佳"],"start_time_utc":"2018-12-04 05:00:00","end_time_utc":"2018-12-04 08:30:00","is_playing":true},{"name":"央广新闻晚高峰","anchors":["雨亭","中国之声主持人","苏扬","J姗姗来迟"],"start_time_utc":"2018-12-04 08:30:00","end_time_utc":"2018-12-04 10:30:00","is_playing":false},{"name":"央广新闻晚高峰","anchors":[],"start_time_utc":"2018-12-04 11:00:00","end_time_utc":"2018-12-04 12:00:00","is_playing":false},{"name":"小喇叭","anchors":["中国之声主持人","小喇叭春天姐姐"],"start_time_utc":"2018-12-04 12:00:00","end_time_utc":"2018-12-04 12:30:00","is_playing":false},{"name":"直播中国","anchors":[],"start_time_utc":"2018-12-04 12:30:00","end_time_utc":"2018-12-04 13:00:00","is_playing":false},{"name":"央广夜新闻","anchors":[],"start_time_utc":"2018-12-04 13:00:00","end_time_utc":"2018-12-04 15:59:00","is_playing":false}],"external_ids":{"qingting":"386","ximalaya":"1065"},"genres":[]}],"size":1,"params_gps":"22.53704,113.948509","timestamp_utc":"2018-12-04 07:53:59"}
     */

    public StatusBean status;
    public DataBean data;

    public static class StatusBean {
        /**
         * code : 0
         * msg : OK
         */

        public int code;
        public String msg;
    }

    public static class DataBean {
        /**
         * list : [{"name":"CNR中国之声","region":"广东省-深圳市-宝安区","short_name":["CNR中国之声"],"type":"AM","freq":"981","logo":"http://console.acrcloud.cn/car_radio/s1/386.jpg","playlist":[{"name":"千里共良宵","anchors":[],"start_time_utc":"2018-12-03 16:00:00","end_time_utc":"2018-12-03 18:00:00","is_playing":false},{"name":"结束曲","anchors":[],"start_time_utc":"2018-12-03 18:00:00","end_time_utc":"2018-12-03 18:05:00","is_playing":false},{"name":"停机检修","anchors":[],"start_time_utc":"2018-12-03 18:05:00","end_time_utc":"2018-12-03 20:25:00","is_playing":false},{"name":"开始曲","anchors":[],"start_time_utc":"2018-12-03 20:25:00","end_time_utc":"2018-12-03 20:30:00","is_playing":false},{"name":"中央农业广播学校","anchors":["中国之声主持人"],"start_time_utc":"2018-12-03 20:30:00","end_time_utc":"2018-12-03 21:00:00","is_playing":false},{"name":"悦动清晨","anchors":[],"start_time_utc":"2018-12-03 21:00:00","end_time_utc":"2018-12-03 22:00:00","is_playing":false},{"name":"国防时空","anchors":["中国之声主持人"],"start_time_utc":"2018-12-03 22:00:00","end_time_utc":"2018-12-03 22:30:00","is_playing":false},{"name":"新闻和报纸摘要","anchors":["中国之声主持人","于芳"],"start_time_utc":"2018-12-03 22:30:00","end_time_utc":"2018-12-03 23:00:00","is_playing":false},{"name":"新闻纵横","anchors":["林溪","子文","中国之声主持人","张蕾"],"start_time_utc":"2018-12-03 23:00:00","end_time_utc":"2018-12-04 01:00:00","is_playing":false},{"name":"央广新闻","anchors":["胡凡","中国之声主持人","长悦","刘鹤佳"],"start_time_utc":"2018-12-04 01:00:00","end_time_utc":"2018-12-04 04:00:00","is_playing":false},{"name":"全球华语广播网","anchors":["何方","中国之声主持人","郭燕","杨扬"],"start_time_utc":"2018-12-04 04:00:00","end_time_utc":"2018-12-04 05:00:00","is_playing":false},{"name":"央广新闻","anchors":["胡凡","中国之声主持人","长悦","刘鹤佳"],"start_time_utc":"2018-12-04 05:00:00","end_time_utc":"2018-12-04 08:30:00","is_playing":true},{"name":"央广新闻晚高峰","anchors":["雨亭","中国之声主持人","苏扬","J姗姗来迟"],"start_time_utc":"2018-12-04 08:30:00","end_time_utc":"2018-12-04 10:30:00","is_playing":false},{"name":"央广新闻晚高峰","anchors":[],"start_time_utc":"2018-12-04 11:00:00","end_time_utc":"2018-12-04 12:00:00","is_playing":false},{"name":"小喇叭","anchors":["中国之声主持人","小喇叭春天姐姐"],"start_time_utc":"2018-12-04 12:00:00","end_time_utc":"2018-12-04 12:30:00","is_playing":false},{"name":"直播中国","anchors":[],"start_time_utc":"2018-12-04 12:30:00","end_time_utc":"2018-12-04 13:00:00","is_playing":false},{"name":"央广夜新闻","anchors":[],"start_time_utc":"2018-12-04 13:00:00","end_time_utc":"2018-12-04 15:59:00","is_playing":false}],"external_ids":{"qingting":"386","ximalaya":"1065"},"genres":[]}]
         * size : 1
         * params_gps : 22.53704,113.948509
         * timestamp_utc : 2018-12-04 07:53:59
         */

        public int size;
        public String params_gps;
        public String timestamp_utc;
        public List<ListBean> list;

        public static class ListBean {
            /**
             * name : CNR中国之声
             * region : 广东省-深圳市-宝安区
             * short_name : ["CNR中国之声"]
             * type : AM
             * freq : 981
             * logo : http://console.acrcloud.cn/car_radio/s1/386.jpg
             * playlist : [{"name":"千里共良宵","anchors":[],"start_time_utc":"2018-12-03 16:00:00","end_time_utc":"2018-12-03 18:00:00","is_playing":false},{"name":"结束曲","anchors":[],"start_time_utc":"2018-12-03 18:00:00","end_time_utc":"2018-12-03 18:05:00","is_playing":false},{"name":"停机检修","anchors":[],"start_time_utc":"2018-12-03 18:05:00","end_time_utc":"2018-12-03 20:25:00","is_playing":false},{"name":"开始曲","anchors":[],"start_time_utc":"2018-12-03 20:25:00","end_time_utc":"2018-12-03 20:30:00","is_playing":false},{"name":"中央农业广播学校","anchors":["中国之声主持人"],"start_time_utc":"2018-12-03 20:30:00","end_time_utc":"2018-12-03 21:00:00","is_playing":false},{"name":"悦动清晨","anchors":[],"start_time_utc":"2018-12-03 21:00:00","end_time_utc":"2018-12-03 22:00:00","is_playing":false},{"name":"国防时空","anchors":["中国之声主持人"],"start_time_utc":"2018-12-03 22:00:00","end_time_utc":"2018-12-03 22:30:00","is_playing":false},{"name":"新闻和报纸摘要","anchors":["中国之声主持人","于芳"],"start_time_utc":"2018-12-03 22:30:00","end_time_utc":"2018-12-03 23:00:00","is_playing":false},{"name":"新闻纵横","anchors":["林溪","子文","中国之声主持人","张蕾"],"start_time_utc":"2018-12-03 23:00:00","end_time_utc":"2018-12-04 01:00:00","is_playing":false},{"name":"央广新闻","anchors":["胡凡","中国之声主持人","长悦","刘鹤佳"],"start_time_utc":"2018-12-04 01:00:00","end_time_utc":"2018-12-04 04:00:00","is_playing":false},{"name":"全球华语广播网","anchors":["何方","中国之声主持人","郭燕","杨扬"],"start_time_utc":"2018-12-04 04:00:00","end_time_utc":"2018-12-04 05:00:00","is_playing":false},{"name":"央广新闻","anchors":["胡凡","中国之声主持人","长悦","刘鹤佳"],"start_time_utc":"2018-12-04 05:00:00","end_time_utc":"2018-12-04 08:30:00","is_playing":true},{"name":"央广新闻晚高峰","anchors":["雨亭","中国之声主持人","苏扬","J姗姗来迟"],"start_time_utc":"2018-12-04 08:30:00","end_time_utc":"2018-12-04 10:30:00","is_playing":false},{"name":"央广新闻晚高峰","anchors":[],"start_time_utc":"2018-12-04 11:00:00","end_time_utc":"2018-12-04 12:00:00","is_playing":false},{"name":"小喇叭","anchors":["中国之声主持人","小喇叭春天姐姐"],"start_time_utc":"2018-12-04 12:00:00","end_time_utc":"2018-12-04 12:30:00","is_playing":false},{"name":"直播中国","anchors":[],"start_time_utc":"2018-12-04 12:30:00","end_time_utc":"2018-12-04 13:00:00","is_playing":false},{"name":"央广夜新闻","anchors":[],"start_time_utc":"2018-12-04 13:00:00","end_time_utc":"2018-12-04 15:59:00","is_playing":false}]
             * external_ids : {"qingting":"386","ximalaya":"1065"}
             * genres : []
             */

            public String name;
            public String region;
            public String type;
            public String freq;
            public String logo;
            public ExternalIdsBean external_ids;
            public List<String> short_name;
            public List<PlaylistBean> playlist;
            public List<?> genres;

            @Override
            public String toString() {
                return "ListBean{" +
                        "name='" + name + '\'' +
                        ", region='" + region + '\'' +
                        ", type='" + type + '\'' +
                        ", freq='" + freq + '\'' +
                        ", logo='" + logo + '\'' +
                        ", external_ids=" + external_ids +
                        ", short_name=" + short_name +
                        ", playlist=" + playlist +
                        ", genres=" + genres +
                        '}';
            }

            public static class ExternalIdsBean {
                /**
                 * qingting : 386
                 * ximalaya : 1065
                 */

                public String qingting;
                public String ximalaya;
            }

            public static class PlaylistBean {
                /**
                 * name : 千里共良宵
                 * anchors : []
                 * start_time_utc : 2018-12-03 16:00:00
                 * end_time_utc : 2018-12-03 18:00:00
                 * is_playing : false
                 */

                public String name;
                public String start_time_utc;
                public String end_time_utc;
                public boolean is_playing;
                public List<?> anchors;
            }
        }
    }
}
