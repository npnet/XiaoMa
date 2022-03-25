package com.xiaoma.shop.business.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/4/18 0018 14:49
 *   desc:   购买
 * </pre>
 */
public class BuyBean implements Parcelable {

    /**
     * id : 1291
     * channelId : AA1090
     * createDate : 1557128914098
     * type : Traffic
     * orderNo : 4129060519000004
     * amount : 1
     * orderId : 15241
     * orderName : 6G/360天
     * orderStatus : 待支付
     * orderStatusId : 2
     * delFlag : 0
     * uid : 187
     * orderDays : 0
     * paySource : jifen
     * payStatus : 0
     * lastpayDate : 1557130714098
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
    private String paySource;
    private int payStatus;
    private long lastpayDate;

    protected BuyBean(Parcel in) {
        id = in.readInt();
        channelId = in.readString();
        createDate = in.readLong();
        type = in.readString();
        orderNo = in.readString();
        amount = in.readString();
        orderId = in.readString();
        orderName = in.readString();
        orderStatus = in.readString();
        orderStatusId = in.readInt();
        delFlag = in.readInt();
        uid = in.readLong();
        orderDays = in.readInt();
        paySource = in.readString();
        payStatus = in.readInt();
        lastpayDate = in.readLong();
    }

    public static final Creator<BuyBean> CREATOR = new Creator<BuyBean>() {
        @Override
        public BuyBean createFromParcel(Parcel in) {
            return new BuyBean(in);
        }

        @Override
        public BuyBean[] newArray(int size) {
            return new BuyBean[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getOrderStatusId() {
        return orderStatusId;
    }

    public void setOrderStatusId(int orderStatusId) {
        this.orderStatusId = orderStatusId;
    }

    public int getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(int delFlag) {
        this.delFlag = delFlag;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getOrderDays() {
        return orderDays;
    }

    public void setOrderDays(int orderDays) {
        this.orderDays = orderDays;
    }

    public String getPaySource() {
        return paySource;
    }

    public void setPaySource(String paySource) {
        this.paySource = paySource;
    }

    public int getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(int payStatus) {
        this.payStatus = payStatus;
    }

    public long getLastpayDate() {
        return lastpayDate;
    }

    public void setLastpayDate(long lastpayDate) {
        this.lastpayDate = lastpayDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(channelId);
        dest.writeLong(createDate);
        dest.writeString(type);
        dest.writeString(orderNo);
        dest.writeString(amount);
        dest.writeString(orderId);
        dest.writeString(orderName);
        dest.writeString(orderStatus);
        dest.writeInt(orderStatusId);
        dest.writeInt(delFlag);
        dest.writeLong(uid);
        dest.writeInt(orderDays);
        dest.writeString(paySource);
        dest.writeInt(payStatus);
        dest.writeLong(lastpayDate);
    }
}
