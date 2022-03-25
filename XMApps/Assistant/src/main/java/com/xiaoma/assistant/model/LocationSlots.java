package com.xiaoma.assistant.model;

/**
 * Created by qiuboxiang on 2019/3/1 15:02
 * Desc:
 */
public class LocationSlots {

    /**
     * endLoc : {"ori_loc":"美食","topic":"restaurant"}
     * landmark : {"ori_loc":"深南大道"}
     * startLoc : {"ori_loc":"CURRENT_ORI_LOC"}
     * viaLoc : {"ori_loc":"桃园地铁站","topic":"others"}
     */

    private EndLoc endLoc;
    private Landmark landmark;
    private StartLoc startLoc;
    private String insType;
    private EndLoc viaLoc;
    private String distanceDescr;
    private String naviInfo;
    private PosRank posRank;
    private String routeCondition;

    public String getRouteCondition() {
        return routeCondition;
    }

    public void setRouteCondition(String routeCondition) {
        this.routeCondition = routeCondition;
    }

    public PosRank getPosRank() {
        return posRank;
    }

    public void setPosRank(PosRank posRank) {
        this.posRank = posRank;
    }

    public String getNaviInfo() {
        return naviInfo;
    }

    public void setNaviInfo(String naviInfo) {
        this.naviInfo = naviInfo;
    }

    public EndLoc getEndLoc() {
        return endLoc;
    }

    public void setEndLoc(EndLoc endLoc) {
        this.endLoc = endLoc;
    }

    public Landmark getLandmark() {
        return landmark;
    }

    public void setLandmark(Landmark landmark) {
        this.landmark = landmark;
    }

    public StartLoc getStartLoc() {
        return startLoc;
    }

    public void setStartLoc(StartLoc startLoc) {
        this.startLoc = startLoc;
    }

    public String getInsType() {
        return insType;
    }

    public void setInsType(String insType) {
        this.insType = insType;
    }

    public EndLoc getViaLoc() {
        return viaLoc;
    }

    public void setViaLoc(EndLoc viaLoc) {
        this.viaLoc = viaLoc;
    }

    public String getDistanceDescr() {
        return distanceDescr;
    }

    public void setDistanceDescr(String distanceDescr) {
        this.distanceDescr = distanceDescr;
    }

    public static class PosRank {
        /**
         * "posRank":
         *"direct": "+",
         *"offset": "5",
         *"ref": "ZERO",
         *"type": "SPOT"
         */
        private String direct;
        private String offset;
        private String ref;
        private String type;

        public String getDirect() {
            return direct;
        }

        public void setDirect(String direct) {
            this.direct = direct;
        }

        public String getOffset() {
            return offset;
        }

        public void setOffset(String offset) {
            this.offset = offset;
        }

        public String getRef() {
            return ref;
        }

        public void setRef(String ref) {
            this.ref = ref;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }


    public static class EndLoc {
        /**
         * ori_loc : 美食
         * topic : restaurant
         * city : CURRENT_CITY
         * type : LOC_POI
         * poi : 超市
         * xm_cat : NEARBY
         */

        private String ori_loc;
        private String topic;
        private String city;
        private String type;
        private String poi;
        private String xm_cat;

        public String getOri_loc() {
            return ori_loc;
        }

        public void setOri_loc(String ori_loc) {
            this.ori_loc = ori_loc;
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
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

        public String getXm_cat() {
            return xm_cat;
        }

        public void setXm_cat(String xm_cat) {
            this.xm_cat = xm_cat;
        }
    }

    public static class Landmark {
        /**
         * ori_loc : 深南大道
         */

        private String ori_loc;

        public String getOri_loc() {
            return ori_loc;
        }

        public void setOri_loc(String ori_loc) {
            this.ori_loc = ori_loc;
        }
    }

    public static class StartLoc {
        /**
         * ori_loc : CURRENT_ORI_LOC
         */

        private String ori_loc;

        public String getOri_loc() {
            return ori_loc;
        }

        public void setOri_loc(String ori_loc) {
            this.ori_loc = ori_loc;
        }
    }
}
