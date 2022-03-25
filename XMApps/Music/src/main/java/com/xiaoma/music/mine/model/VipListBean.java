package com.xiaoma.music.mine.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/6/28 0028 15:37
 *   desc:   vip页列表实体
 * </pre>
 */
public class VipListBean implements Parcelable {

    public static final Creator<VipListBean> CREATOR = new Creator<VipListBean>() {
        @Override
        public VipListBean createFromParcel(Parcel in) {
            return new VipListBean(in);
        }

        @Override
        public VipListBean[] newArray(int size) {
            return new VipListBean[size];
        }
    };
    private List<PrivilegeBean> privileges;
    private List<VipOptionsBean> data;

    protected VipListBean(Parcel in) {
        privileges = in.createTypedArrayList(PrivilegeBean.CREATOR);
        data = in.createTypedArrayList(VipOptionsBean.CREATOR);
    }

    public List<PrivilegeBean> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(List<PrivilegeBean> privileges) {
        this.privileges = privileges;
    }

    public List<VipOptionsBean> getData() {
        return data;
    }

    public void setData(List<VipOptionsBean> data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(privileges);
        dest.writeTypedList(data);
    }
}
