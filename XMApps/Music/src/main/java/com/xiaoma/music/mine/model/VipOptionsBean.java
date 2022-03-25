package com.xiaoma.music.mine.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/6/25 0025 11:49
 *   desc:   vip 价格选项
 *
 * </pre>
 */
public class VipOptionsBean implements Parcelable {

    /**
     * price : 19.9
     * cnt : 1
     * id : 17
     * type : vip_17
     */

    private double price;
    private int cnt;
    private String id;
    private String type;
    public static final Creator<VipOptionsBean> CREATOR = new Creator<VipOptionsBean>() {
        @Override
        public VipOptionsBean createFromParcel(Parcel in) {
            return new VipOptionsBean(in);
        }

        @Override
        public VipOptionsBean[] newArray(int size) {
            return new VipOptionsBean[size];
        }
    };
    private boolean select;

    protected VipOptionsBean(Parcel in) {
        price = in.readDouble();
        cnt = in.readInt();
        id = in.readString();
        type = in.readString();
        select = in.readByte() != 0;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeDouble(price);
        dest.writeInt(cnt);
        dest.writeString(id);
        dest.writeString(type);
        dest.writeByte((byte) (select ? 1 : 0));
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }
}
