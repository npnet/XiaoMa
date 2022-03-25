package com.xiaoma.service.plan.model;

import java.util.List;

/**
 * Created by ZouShao on 2018/11/19 0019.
 */

public class MaintenancePlanBean {
    private int time;
    private List<PlansBean> plans;

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public List<PlansBean> getPlans() {
        return plans;
    }

    public void setPlans(List<PlansBean> plans) {
        this.plans = plans;
    }

    public static class PlansBean {
        private int id;
        private long createDate;
        private String channelId;
        private String enableStatus;
        private int orderLevel;
        private int recommendKilometer;
        private int recommendMonthNum;
        private int recommendTimes;
        private List<OptionsBean> options;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public long getCreateDate() {
            return createDate;
        }

        public void setCreateDate(long createDate) {
            this.createDate = createDate;
        }

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public String getEnableStatus() {
            return enableStatus;
        }

        public void setEnableStatus(String enableStatus) {
            this.enableStatus = enableStatus;
        }

        public int getOrderLevel() {
            return orderLevel;
        }

        public void setOrderLevel(int orderLevel) {
            this.orderLevel = orderLevel;
        }

        public int getRecommendKilometer() {
            return recommendKilometer;
        }

        public void setRecommendKilometer(int recommendKilometer) {
            this.recommendKilometer = recommendKilometer;
        }

        public int getRecommendMonthNum() {
            return recommendMonthNum;
        }

        public void setRecommendMonthNum(int recommendMonthNum) {
            this.recommendMonthNum = recommendMonthNum;
        }

        public int getRecommendTimes() {
            return recommendTimes;
        }

        public void setRecommendTimes(int recommendTimes) {
            this.recommendTimes = recommendTimes;
        }

        public List<OptionsBean> getOptions() {
            return options;
        }

        public void setOptions(List<OptionsBean> options) {
            this.options = options;
        }

        public static class OptionsBean {
            /**
             * id : 1
             * createDate : 1544169009000
             * channelId : AA1090
             * enableStatus : 1
             * orderLevel : 1
             * name : 多楔带
             * upkeepMethod :
             * upkeepPeriod :
             * picUrl :
             */

            private int id;
            private long createDate;
            private String channelId;
            private String enableStatus;
            private int orderLevel;
            private String name;
            private String upkeepMethod;
            private String upkeepPeriod;
            private String picUrl;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public long getCreateDate() {
                return createDate;
            }

            public void setCreateDate(long createDate) {
                this.createDate = createDate;
            }

            public String getChannelId() {
                return channelId;
            }

            public void setChannelId(String channelId) {
                this.channelId = channelId;
            }

            public String getEnableStatus() {
                return enableStatus;
            }

            public void setEnableStatus(String enableStatus) {
                this.enableStatus = enableStatus;
            }

            public int getOrderLevel() {
                return orderLevel;
            }

            public void setOrderLevel(int orderLevel) {
                this.orderLevel = orderLevel;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getUpkeepMethod() {
                return upkeepMethod;
            }

            public void setUpkeepMethod(String upkeepMethod) {
                this.upkeepMethod = upkeepMethod;
            }

            public String getUpkeepPeriod() {
                return upkeepPeriod;
            }

            public void setUpkeepPeriod(String upkeepPeriod) {
                this.upkeepPeriod = upkeepPeriod;
            }

            public String getPicUrl() {
                return picUrl;
            }

            public void setPicUrl(String picUrl) {
                this.picUrl = picUrl;
            }
        }
    }
}
