package com.xiaoma.personal.qrcode.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/6/22 0022 14:10
 *   desc:   绑定成功后信息实体
 * </pre>
 */
public class BindStatusBean implements Parcelable {

    public static final Creator<BindStatusBean> CREATOR = new Creator<BindStatusBean>() {
        @Override
        public BindStatusBean createFromParcel(Parcel in) {
            return new BindStatusBean(in);
        }

        @Override
        public BindStatusBean[] newArray(int size) {
            return new BindStatusBean[size];
        }
    };
    private String phone;
    private String icon;

    protected BindStatusBean(Parcel in) {
        phone = in.readString();
        icon = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(phone);
        dest.writeString(icon);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
