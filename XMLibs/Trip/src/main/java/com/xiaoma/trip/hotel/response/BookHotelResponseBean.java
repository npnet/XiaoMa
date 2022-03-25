package com.xiaoma.trip.hotel.response;

/**
 * 预定酒店返回bean
 * Created by zhushi.
 * Date: 2018/12/5
 */
public class BookHotelResponseBean {

    /**
     * orderStatus : 已确认
     * orderId : CN18120300009A
     * customerPayStatusId : 3
     * customerPayStatus : 已支付
     * lastCancelTime : 2018/12/3 21:00:00
     * mainOrderId : CN18120300009A
     * orderStatusId : 10
     */

    private String orderStatus;
    private String orderId;
    private String customerPayStatusId;
    private String customerPayStatus;
    private String lastCancelTime;
    private String mainOrderId;
    private String orderStatusId;

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerPayStatusId() {
        return customerPayStatusId;
    }

    public void setCustomerPayStatusId(String customerPayStatusId) {
        this.customerPayStatusId = customerPayStatusId;
    }

    public String getCustomerPayStatus() {
        return customerPayStatus;
    }

    public void setCustomerPayStatus(String customerPayStatus) {
        this.customerPayStatus = customerPayStatus;
    }

    public String getLastCancelTime() {
        return lastCancelTime;
    }

    public void setLastCancelTime(String lastCancelTime) {
        this.lastCancelTime = lastCancelTime;
    }

    public String getMainOrderId() {
        return mainOrderId;
    }

    public void setMainOrderId(String mainOrderId) {
        this.mainOrderId = mainOrderId;
    }

    public String getOrderStatusId() {
        return orderStatusId;
    }

    public void setOrderStatusId(String orderStatusId) {
        this.orderStatusId = orderStatusId;
    }
}
