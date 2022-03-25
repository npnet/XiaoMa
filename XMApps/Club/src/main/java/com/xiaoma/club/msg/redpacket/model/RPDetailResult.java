package com.xiaoma.club.msg.redpacket.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.xiaoma.club.common.model.ClubBaseResult;

/**
 * Created by ZYao.
 * Date ：2019/4/17 0017
 */
public class RPDetailResult extends ClubBaseResult implements Parcelable {

    private RpDetailInfo data;

    public RpDetailInfo getData() {
        return data;
    }

    public void setData(RpDetailInfo data) {
        this.data = data;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.data, 0);
    }

    public RPDetailResult() {
    }

    protected RPDetailResult(Parcel in) {
        this.data = in.readParcelable(RpDetailInfo.class.getClassLoader());
    }

    public static final Parcelable.Creator<RPDetailResult> CREATOR = new Parcelable.Creator<RPDetailResult>() {
        public RPDetailResult createFromParcel(Parcel source) {
            return new RPDetailResult(source);
        }

        public RPDetailResult[] newArray(int size) {
            return new RPDetailResult[size];
        }
    };
}
