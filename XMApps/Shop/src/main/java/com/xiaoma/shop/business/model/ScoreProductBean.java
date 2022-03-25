package com.xiaoma.shop.business.model;

import android.text.TextUtils;

import com.xiaoma.shop.common.constant.ShopContract;
import com.xiaoma.shop.common.track.model.FlowTrackInfo;

import java.util.List;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/04/10
 * @Describe:
 */

public class ScoreProductBean {

    /**
     * userScore : 49
     * productInfo : [{"id":40,"name":"兑换流量","pid":0,"channelId":"AA1090",
     * "createDate":1554796521000,"iconPath":"http://www.carbuyin
     * .net/by2/qunHeader/53697abf-8317-4994-a9dc-bcf85d4df844.png","desciption":"兑换流量",
     * "enableStatus":1,"orderLevel":1,"exchangeTimes":1,"holeType":1,
     * "childProduct":[{"id":15241,"name":"6G/360天","pid":40,"needScore":200,
     * "iconPath":"http://mall.cu-sc
     * .com/online-store/images/commodity/1212EDD9E106E61E9D24CC60DF9CCD5B.jpg","holeType":1,
     * "userExchangeFlag":0,"primitNum":1,"schemeId":"ff808081665e426e016660b271ee03d4"},
     * {"id":12574,"name":"1G/30天","pid":40,"needScore":300,"iconPath":"http://mall.cu-sc
     * .com/online-store/images/commodity/B39F7E667264E2A43B3CF9162F70BF88.jpg","holeType":1,
     * "userExchangeFlag":0,"primitNum":0,"schemeId":"ff808081665e426e016660b271ee03d5"},
     * {"id":14974,"name":"2G/180天","pid":40,"needScore":200,"iconPath":"http://mall.cu-sc
     * .com/online-store/images/commodity/21D2F233122A94CA09CD0AA07202A410.jpg","holeType":1,
     * "userExchangeFlag":0,"primitNum":0,"schemeId":"ff808081665e426e016660b271ee03d6"}],
     * "userExchangeFlag":0}]
     */

    private int userScore;
    private List<ProductInfoBean> productInfo;

    public int getUserScore() {
        return userScore;
    }

    public void setUserScore(int userScore) {
        this.userScore = userScore;
    }

    public List<ProductInfoBean> getProductInfo() {
        return productInfo;
    }

    public void setProductInfo(List<ProductInfoBean> productInfo) {
        this.productInfo = productInfo;
    }

    public static class ProductInfoBean {
        /**
         * id : 40
         * name : 兑换流量
         * pid : 0
         * channelId : AA1090
         * createDate : 1554796521000
         * iconPath : http://www.carbuyin.net/by2/qunHeader/53697abf-8317-4994-a9dc-bcf85d4df844.png
         * desciption : 兑换流量
         * enableStatus : 1
         * orderLevel : 1
         * exchangeTimes : 1
         * holeType : 1
         * childProduct : [{"id":15241,"name":"6G/360天","pid":40,"needScore":200,
         * "iconPath":"http://mall.cu-sc
         * .com/online-store/images/commodity/1212EDD9E106E61E9D24CC60DF9CCD5B.jpg","holeType":1,
         * "userExchangeFlag":0,"primitNum":1,"schemeId":"ff808081665e426e016660b271ee03d4"},
         * {"id":12574,"name":"1G/30天","pid":40,"needScore":300,"iconPath":"http://mall.cu-sc
         * .com/online-store/images/commodity/B39F7E667264E2A43B3CF9162F70BF88.jpg","holeType":1,
         * "userExchangeFlag":0,"primitNum":0,"schemeId":"ff808081665e426e016660b271ee03d5"},
         * {"id":14974,"name":"2G/180天","pid":40,"needScore":200,"iconPath":"http://mall.cu-sc
         * .com/online-store/images/commodity/21D2F233122A94CA09CD0AA07202A410.jpg","holeType":1,
         * "userExchangeFlag":0,"primitNum":0,"schemeId":"ff808081665e426e016660b271ee03d6"}]
         * userExchangeFlag : 0
         */

        private int id;
        private String name;
        private int pid;
        private String channelId;
        private long createDate;
        private String iconPath;
        private String desciption;
        private int enableStatus;
        private int orderLevel;
        private int exchangeTimes;
        private int holeType;
        private int userExchangeFlag;
        private List<ChildProductBean> childProduct;

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

        public int getPid() {
            return pid;
        }

        public void setPid(int pid) {
            this.pid = pid;
        }

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public long getCreateDate() {
            return createDate;
        }

        public void setCreateDate(long createDate) {
            this.createDate = createDate;
        }

        public String getIconPath() {
            return iconPath;
        }

        public void setIconPath(String iconPath) {
            this.iconPath = iconPath;
        }

        public String getDesciption() {
            return desciption;
        }

        public void setDesciption(String desciption) {
            this.desciption = desciption;
        }

        public int getEnableStatus() {
            return enableStatus;
        }

        public void setEnableStatus(int enableStatus) {
            this.enableStatus = enableStatus;
        }

        public int getOrderLevel() {
            return orderLevel;
        }

        public void setOrderLevel(int orderLevel) {
            this.orderLevel = orderLevel;
        }

        public int getExchangeTimes() {
            return exchangeTimes;
        }

        public void setExchangeTimes(int exchangeTimes) {
            this.exchangeTimes = exchangeTimes;
        }

        public int getHoleType() {
            return holeType;
        }

        public void setHoleType(int holeType) {
            this.holeType = holeType;
        }

        public int getUserExchangeFlag() {
            return userExchangeFlag;
        }

        public void setUserExchangeFlag(int userExchangeFlag) {
            this.userExchangeFlag = userExchangeFlag;
        }

        public List<ChildProductBean> getChildProduct() {
            return childProduct;
        }

        public void setChildProduct(List<ChildProductBean> childProduct) {
            this.childProduct = childProduct;
        }

        public static class ChildProductBean {
            /**
             * id : 15241
             * name : 6G/360天
             * pid : 40
             * needScore : 200
             * iconPath : http://mall.cu-sc
             * .com/online-store/images/commodity/1212EDD9E106E61E9D24CC60DF9CCD5B.jpg
             * holeType : 1
             * userExchangeFlag : 0
             * primitNum : 1
             * schemeId : ff808081665e426e016660b271ee03d4
             */

            private int id;
            private String name;
            private int pid;
            private int needScore;
            private String iconPath;
            private int holeType;
            private int userExchangeFlag;
            private int primitNum;
            private String schemeId;

            private String tagPath;
            private String price;
            private String discountPrice;
            private String discountSocrePrice;
            @ShopContract.Pay
            private int mPayType = ShopContract.Pay.DEFAULT;

            public String getTagPath() {
                return tagPath;
            }

            public void setTagPath(String tagPath) {
                this.tagPath = tagPath;
            }

            public String getPrice() {
                return price;
            }

            public void setPrice(String price) {
                this.price = price;
            }

            public String getDiscountPrice() {
                return discountPrice;
            }

            public void setDiscountPrice(String discountPrice) {
                this.discountPrice = discountPrice;
            }

            public String getDiscountScorePrice() {
                return discountSocrePrice;
            }

            public void setDiscountScorePrice(String discountScorePrice) {
                this.discountSocrePrice = discountScorePrice;
            }

            public int getPayType() {
                return mPayType;
            }

            public void setPayType(int payType) {
                mPayType = payType;
            }

            private boolean isSelected;

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

            public int getPid() {
                return pid;
            }

            public void setPid(int pid) {
                this.pid = pid;
            }

            public int getNeedScore() {
                return needScore;
            }

            public void setNeedScore(int needScore) {
                this.needScore = needScore;
            }

            public String getIconPath() {
                return iconPath;
            }

            public void setIconPath(String iconPath) {
                this.iconPath = iconPath;
            }

            public int getHoleType() {
                return holeType;
            }

            public void setHoleType(int holeType) {
                this.holeType = holeType;
            }

            public int getUserExchangeFlag() {
                return userExchangeFlag;
            }

            public void setUserExchangeFlag(int userExchangeFlag) {
                this.userExchangeFlag = userExchangeFlag;
            }

            public int getPrimitNum() {
                return primitNum;
            }

            public void setPrimitNum(int primitNum) {
                this.primitNum = primitNum;
            }

            public String getSchemeId() {
                return schemeId;
            }

            public void setSchemeId(String schemeId) {
                this.schemeId = schemeId;
            }

            public boolean isSelected() {
                return isSelected;
            }

            public void setSelected(boolean selected) {
                isSelected = selected;
            }


            public FlowTrackInfo toTrackInfo() {
                FlowTrackInfo flowTrackInfo = new FlowTrackInfo();
                flowTrackInfo.setId(id);
                flowTrackInfo.setName(name);
                flowTrackInfo.setRmbPrice((price == null ? "0" : price));
                flowTrackInfo.setRmbPriceDiscount(discountPrice == null ? "0" : discountPrice);
                flowTrackInfo.setCoinPrice(String.valueOf(needScore));
                flowTrackInfo.setCoinPriceDiscount(discountSocrePrice == null ? "0" : discountSocrePrice);

                return flowTrackInfo;
            }
        }

    }
}
