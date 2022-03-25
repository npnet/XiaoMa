package com.xiaoma.shop.business.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Ljb
 * Time  : 2019/7/20
 * Description:
 */
public class FetchQrCodeBean {


    /**
     * uid : 951642686056837120
     * payChannel : 1
     * channelId : AH2000
     * orderItems : [{"commoNo":"198","count":1,"price":"0.01"}]
     */

    private String uid;
    private String payChannel;
    private String channelId;
    private List<OrderItemsBean> orderItems;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPayChannel() {
        return payChannel;
    }

    public void setPayChannel(String payChannel) {
        this.payChannel = payChannel;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public List<OrderItemsBean> getOrderItems() {
        return orderItems == null ? (orderItems = new ArrayList<>()) : orderItems;
    }

    public void setOrderItems(List<OrderItemsBean> orderItems) {
        this.orderItems = orderItems;
    }

    public static class OrderItemsBean {
        /**
         * commoNo : 198
         * count : 1
         * price : 0.01
         */

        private String commoNo;
        private int count;
        private String price;

        public String getCommoNo() {
            return commoNo;
        }

        public void setCommoNo(String commoNo) {
            this.commoNo = commoNo;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }
    }
}
