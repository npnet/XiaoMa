package com.xiaoma.assistant.model;

/**
 * Created by qiuboxiang on 2019/3/9 11:11
 * Desc:
 */
public class HotelSlots {

    /**
     * endLoc : {"ori_loc":"酒店","topic":"hotel"}
     * landmark : {"city":"深圳市","ori_loc":"深圳"}
     * property : {"hotelLvl":{"direct":"+","offset":"5","ref":"ZERO","type":"SPOT"}}
     * startLoc : {"ori_loc":"CURRENT_ORI_LOC"}
     */

    private EndLocBean endLoc;
    private LandmarkBean landmark;
    private PropertyBean property;
    private StartLocBean startLoc;
    /**
     * price : {"leftClosure":"MIN","rightClosure":"500","type":"RANGE"}
     */
    private PriceBean price;


    public EndLocBean getEndLoc() {
        return endLoc;
    }

    public void setEndLoc(EndLocBean endLoc) {
        this.endLoc = endLoc;
    }

    public LandmarkBean getLandmark() {
        return landmark;
    }

    public void setLandmark(LandmarkBean landmark) {
        this.landmark = landmark;
    }

    public PropertyBean getProperty() {
        return property;
    }

    public void setProperty(PropertyBean property) {
        this.property = property;
    }

    public StartLocBean getStartLoc() {
        return startLoc;
    }

    public void setStartLoc(StartLocBean startLoc) {
        this.startLoc = startLoc;
    }

    public PriceBean getPrice() {
        return price;
    }

    public void setPrice(PriceBean price) {
        this.price = price;
    }

    public static class EndLocBean {
        /**
         * ori_loc : 酒店
         * topic : hotel
         */

        private String ori_loc;
        private String topic;

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
    }

    public static class LandmarkBean {
        /**
         * city : 深圳市
         * ori_loc : 深圳
         */

        private String city;
        private String ori_loc;

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getOri_loc() {
            return ori_loc;
        }

        public void setOri_loc(String ori_loc) {
            this.ori_loc = ori_loc;
        }
    }

    public static class PropertyBean {
        /**
         * hotelLvl : {"direct":"+","offset":"5","ref":"ZERO","type":"SPOT"}
         */

        private HotelLvlBean hotelLvl;

        public HotelLvlBean getHotelLvl() {
            return hotelLvl;
        }

        public void setHotelLvl(HotelLvlBean hotelLvl) {
            this.hotelLvl = hotelLvl;
        }

        public static class HotelLvlBean {
            /**
             * direct : +
             * offset : 5
             * ref : ZERO
             * type : SPOT
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
    }

    public static class StartLocBean {
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

    public static class PriceBean {
        /**
         * leftClosure : MIN
         * rightClosure : 500
         * type : RANGE
         */

        private String leftClosure;
        private String rightClosure;
        private String type;

        public String getLeftClosure() {
            return leftClosure;
        }

        public void setLeftClosure(String leftClosure) {
            this.leftClosure = leftClosure;
        }

        public String getRightClosure() {
            return rightClosure;
        }

        public void setRightClosure(String rightClosure) {
            this.rightClosure = rightClosure;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
