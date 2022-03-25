package com.xiaoma.shop.business.model;

import java.util.List;

/**
 * @Author: LiangJingBo.
 * @Date: /2019/4/9
 * @Describe: 流量余量bean
 */

public class FlowMarginBean {

    private List<LeftInfoBean> leftInfo;
    private List<?> notActived;

    public List<LeftInfoBean> getLeftInfo() { return leftInfo;}

    public void setLeftInfo(List<LeftInfoBean> leftInfo) { this.leftInfo = leftInfo;}

    public List<?> getNotActived() { return notActived;}

    public void setNotActived(List<?> notActived) { this.notActived = notActived;}

    public static class LeftInfoBean {
        /**
         * sku : WIFI
         * quota : 12582912
         * quotaUsage : 1528156
         * quotaBalance : 11054756
         * unit : Kb
         * expirationTime : 2019-12-07 00.00.00
         * status : using
         */

        private String sku;
        private String quota;
        private String quotaUsage;
        private String quotaBalance;
        private String unit;
        private String expirationTime;
        private String status;

        public String getSku() { return sku;}

        public void setSku(String sku) { this.sku = sku;}

        public String getQuota() { return quota;}

        public void setQuota(String quota) { this.quota = quota;}

        public String getQuotaUsage() { return quotaUsage;}

        public void setQuotaUsage(String quotaUsage) { this.quotaUsage = quotaUsage;}

        public String getQuotaBalance() { return quotaBalance;}

        public void setQuotaBalance(String quotaBalance) { this.quotaBalance = quotaBalance;}

        public String getUnit() { return unit;}

        public void setUnit(String unit) { this.unit = unit;}

        public String getExpirationTime() { return expirationTime;}

        public void setExpirationTime(String expirationTime) { this.expirationTime = expirationTime;}

        public String getStatus() { return status;}

        public void setStatus(String status) { this.status = status;}

        @Override
        public String toString() {
            return "LeftInfoBean{" + "sku='" + sku + '\'' + ", quota='" + quota + '\'' + ", " +
                    "quotaUsage='" + quotaUsage + '\'' + ", quotaBalance='" + quotaBalance + '\''
                    + ", unit='" + unit + '\'' + ", expirationTime='" + expirationTime + '\'' +
                    ", status='" + status + '\'' + '}';
        }
    }

    @Override
    public String toString() {
        return "FlowMarginBean{" + "leftInfo=" + leftInfo + ", notActived=" + notActived + '}';
    }
}
