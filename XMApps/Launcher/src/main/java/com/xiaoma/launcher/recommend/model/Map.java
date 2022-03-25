package com.xiaoma.launcher.recommend.model;

import com.xiaoma.mqtt.model.DatasBean;

/**
 * @Auther: huojie
 * @Date: 2019/1/2 0002 15:50
 * @Description:导航
 */
public class Map extends DatasBean {
    /**
     * startLoc : {"city":"CURRENT_CITY","type":"LOC_POI","poi":"CURRENT_POI"}
     * endLoc : {"city":"CURRENT_CITY","type":"LOC_POI","poi":"世界之窗地铁站A出口"}
     */

    private StartLocBean startLoc;
    private EndLocBean endLoc;

    public Map(StartLocBean startLoc, EndLocBean endLoc) {
        this.startLoc = startLoc;
        this.endLoc = endLoc;
    }

    public StartLocBean getStartLoc() {
        return startLoc;
    }

    public void setStartLoc(StartLocBean startLoc) {
        this.startLoc = startLoc;
    }

    public EndLocBean getEndLoc() {
        return endLoc;
    }

    public void setEndLoc(EndLocBean endLoc) {
        this.endLoc = endLoc;
    }

    @Override
    public String toString() {
        return "Map{" +
                "startLoc=" + startLoc +
                ", endLoc=" + endLoc +
                '}';
    }

    public static class StartLocBean {
        /**
         * city : CURRENT_CITY
         * type : LOC_POI
         * poi : CURRENT_POI
         */

        private String city;
        private String type;
        private String poi;

        public StartLocBean(String city, String type, String poi) {
            this.city = city;
            this.type = type;
            this.poi = poi;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPoi() {
            return poi;
        }

        public void setPoi(String poi) {
            this.poi = poi;
        }

        @Override
        public String toString() {
            return "StartLocBean{" +
                    "city='" + city + '\'' +
                    ", type='" + type + '\'' +
                    ", poi='" + poi + '\'' +
                    '}';
        }
    }

    public static class EndLocBean {
        /**
         * city : CURRENT_CITY
         * type : LOC_POI
         * poi : 世界之窗地铁站A出口
         */

        private String city;
        private String type;
        private String poi;

        public EndLocBean(String city, String type, String poi) {
            this.city = city;
            this.type = type;
            this.poi = poi;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getPoi() {
            return poi;
        }

        public void setPoi(String poi) {
            this.poi = poi;
        }

        @Override
        public String toString() {
            return "EndLocBean{" +
                    "city='" + city + '\'' +
                    ", type='" + type + '\'' +
                    ", poi='" + poi + '\'' +
                    '}';
        }
    }
}
