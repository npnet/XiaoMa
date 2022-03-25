package com.xiaoma.club.msg.redpacket.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZYao.
 * Date ：2019/4/16 0016
 */
public class LocationRp implements Parcelable {
    private int redEnvelopeListNum;
    private List<LocationRpInfo> redEnvelopeList;

    public int getRedEnvelopeListNum() {
        return redEnvelopeListNum;
    }

    public void setRedEnvelopeListNum(int redEnvelopeListNum) {
        this.redEnvelopeListNum = redEnvelopeListNum;
    }

    public List<LocationRpInfo> getRedEnvelopeList() {
        return redEnvelopeList;
    }

    public void setRedEnvelopeList(List<LocationRpInfo> redEnvelopeList) {
        this.redEnvelopeList = redEnvelopeList;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.redEnvelopeListNum);
        dest.writeList(this.redEnvelopeList);
    }

    public LocationRp() {
    }

    protected LocationRp(Parcel in) {
        this.redEnvelopeListNum = in.readInt();
        this.redEnvelopeList = new ArrayList<LocationRpInfo>();
        in.readList(this.redEnvelopeList, List.class.getClassLoader());
    }

    public static final Parcelable.Creator<LocationRp> CREATOR = new Parcelable.Creator<LocationRp>() {
        public LocationRp createFromParcel(Parcel source) {
            return new LocationRp(source);
        }

        public LocationRp[] newArray(int size) {
            return new LocationRp[size];
        }
    };
}
