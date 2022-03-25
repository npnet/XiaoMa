package com.xiaoma.music.mine.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/6/25 0025 20:04
 *   desc:   订单状态
 * </pre>
 */
public class OrderStatus implements Parcelable {


    private long id;
    private String KWOrderStatus;
    private String XMOrderStatus;
    private int XMOrderStatusId;
    private String orderNo;


    public static final Creator<OrderStatus> CREATOR = new Creator<OrderStatus>() {
        @Override
        public OrderStatus createFromParcel(Parcel in) {
            return new OrderStatus(in);
        }

        @Override
        public OrderStatus[] newArray(int size) {
            return new OrderStatus[size];
        }
    };


    protected OrderStatus(Parcel in) {
        id = in.readLong();
        KWOrderStatus = in.readString();
        XMOrderStatus = in.readString();
        XMOrderStatusId = in.readInt();
        orderNo = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(KWOrderStatus);
        dest.writeString(XMOrderStatus);
        dest.writeInt(XMOrderStatusId);
        dest.writeString(orderNo);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKWOrderStatus() {
        return KWOrderStatus;
    }

    public void setKWOrderStatus(String KWOrderStatus) {
        this.KWOrderStatus = KWOrderStatus;
    }

    public String getXMOrderStatus() {
        return XMOrderStatus;
    }

    public void setXMOrderStatus(String XMOrderStatus) {
        this.XMOrderStatus = XMOrderStatus;
    }

    public int getXMOrderStatusId() {
        return XMOrderStatusId;
    }

    public void setXMOrderStatusId(int XMOrderStatusId) {
        this.XMOrderStatusId = XMOrderStatusId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
