package com.xiaoma.club.msg.redpacket.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.xiaoma.club.common.model.ClubBaseResult;

/**
 * Created by ZYao.
 * Date ：2019/4/16 0016
 */
public class LocationRpResult extends ClubBaseResult implements Parcelable {

    private LocationRp data;

    public LocationRp getData() {
        return data;
    }

    public void setData(LocationRp data) {
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

    public LocationRpResult() {
    }

    protected LocationRpResult(Parcel in) {
        this.data = in.readParcelable(LocationRp.class.getClassLoader());
    }

    public static final Parcelable.Creator<LocationRpResult> CREATOR = new Parcelable.Creator<LocationRpResult>() {
        public LocationRpResult createFromParcel(Parcel source) {
            return new LocationRpResult(source);
        }

        public LocationRpResult[] newArray(int size) {
            return new LocationRpResult[size];
        }
    };
}
