package com.xiaoma.shop.business.model;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/04/17
 * @Describe:
 */

public class QrCodeBean {


    /**
     * id : 1011
     * channelId : AA1090
     * createDate : 1555934439000
     * type : Traffic
     * orderNo : 2129220419000146
     * amount : 500
     * orderId : ff808081665e426e016660b271ee03d4
     * orderName : 6G/360天
     * orderStatus : 待支付
     * orderStatusId : 2
     * delFlag : 0
     * uid : 187
     * orderDays : 0
     * payStatus : 0
     * payQrcode : http://www.carbuyin.net/by2/wechatQr/83eb1312-ea0f-4174-a283-7dc65f4a4c02.png
     */

    private int id;
    private String channelId;
    private long createDate;
    private String type;
    private String orderNo;
    private String amount;
    private String orderId;
    private String orderName;
    private String orderStatus;
    private int orderStatusId;
    private int delFlag;
    private long uid;
    private int orderDays;
    private int payStatus;
    private String payQrcode;

    private String qrcode;
    private String status;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getQrCode() {
        return qrcode;
    }

    public void setQrCode(String qrCode) {
        this.qrcode = qrCode;
    }

    public int getId() { return id;}

    public void setId(int id) { this.id = id;}

    public String getChannelId() { return channelId;}

    public void setChannelId(String channelId) { this.channelId = channelId;}

    public long getCreateDate() { return createDate;}

    public void setCreateDate(long createDate) { this.createDate = createDate;}

    public String getType() { return type;}

    public void setType(String type) { this.type = type;}

    public String getOrderNo() { return orderNo;}

    public void setOrderNo(String orderNo) { this.orderNo = orderNo;}

    public String getAmount() { return amount;}

    public void setAmount(String amount) { this.amount = amount;}

    public String getOrderId() { return orderId;}

    public void setOrderId(String orderId) { this.orderId = orderId;}

    public String getOrderName() { return orderName;}

    public void setOrderName(String orderName) { this.orderName = orderName;}

    public String getOrderStatus() { return orderStatus;}

    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus;}

    public int getOrderStatusId() { return orderStatusId;}

    public void setOrderStatusId(int orderStatusId) { this.orderStatusId = orderStatusId;}

    public int getDelFlag() { return delFlag;}

    public void setDelFlag(int delFlag) { this.delFlag = delFlag;}

    public long getUid() { return uid;}

    public void setUid(long uid) { this.uid = uid;}

    public int getOrderDays() { return orderDays;}

    public void setOrderDays(int orderDays) { this.orderDays = orderDays;}

    public int getPayStatus() { return payStatus;}

    public void setPayStatus(int payStatus) { this.payStatus = payStatus;}

    public String getPayQrcode() { return payQrcode;}

    public void setPayQrcode(String payQrcode) { this.payQrcode = payQrcode;}
}
