package com.xiaoma.personal.newguide.model;

import java.util.List;

public class AppInfo {
    /**
     * data : [{"id":1,"name":"桌面","packageName":"com.xiaoma.launcher","icon":"http://www.carbuyin.net/by2/appImg/6cb45d73-659d-49d1-ac59-00a45bdf7c7f.png","createDate":1564641715000,"channelId":"AA1090","enableStatus":1,"orderList":0},{"id":2,"name":"奔腾商城","packageName":"com.xiaoma.shop","icon":"http://www.carbuyin.net/by2/appImg/793ce4c9-0c02-4da8-93b4-55396a4cb7d8.png","createDate":1564614996000,"channelId":"AA1090","enableStatus":1,"orderList":0}]
     * resultCode : 1
     * resultMessage : 操作成功
     */

    private String resultCode;
    private String resultMessage;
    private List<DataBean> data;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 1
         * name : 桌面
         * packageName : com.xiaoma.launcher
         * icon : http://www.carbuyin.net/by2/appImg/6cb45d73-659d-49d1-ac59-00a45bdf7c7f.png
         * createDate : 1564641715000
         * channelId : AA1090
         * enableStatus : 1
         * orderList : 0
         */

        private int id;
        private String name;
        private String packageName;
        private String icon;
        private long createDate;
        private String channelId;
        private int enableStatus;
        private int orderList;
        private String guideStatusFlag;

        public String getGuideStatusFlag() {
            return guideStatusFlag;
        }

        public void setGuideStatusFlag(String guideStatusFlag) {
            this.guideStatusFlag = guideStatusFlag;
        }


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
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

        public int getEnableStatus() {
            return enableStatus;
        }

        public void setEnableStatus(int enableStatus) {
            this.enableStatus = enableStatus;
        }

        public int getOrderList() {
            return orderList;
        }

        public void setOrderList(int orderList) {
            this.orderList = orderList;
        }
    }


//    private int appIconResId;
//    private String appName;
//    private String appPackName;
//    private String guideStatusFlag;
//
//    public AppInfo(int appIconResId, String appName, String appPackName, String guideStatusFlag) {
//        this.appIconResId = appIconResId;
//        this.appName = appName;
//        this.appPackName = appPackName;
//        this.guideStatusFlag = guideStatusFlag;
//    }
//
//    public int getAppIconResId() {
//        return appIconResId;
//    }
//
//    public String getGuideStatusFlag() {
//        return guideStatusFlag;
//    }
//
//    public String getAppName() {
//        return appName;
//    }
//
//    public String getAppPackName() {
//        return appPackName;
//    }

}
