package com.xiaoma.club.msg.redpacket.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZYao.
 * Date ：2019/4/17 0017
 */
public class RpDetailInfo implements Parcelable {

    private int receivedNum;
    private int totalNum;
    private int redEnvelopeReceivedDetailListNum;
    private int remainderNum;
    private List<RPDetailItemInfo> redEnvelopeReceivedDetailList;

    public int getReceivedNum() {
        return receivedNum;
    }

    public void setReceivedNum(int receivedNum) {
        this.receivedNum = receivedNum;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public int getRedEnvelopeReceivedDetailListNum() {
        return redEnvelopeReceivedDetailListNum;
    }

    public void setRedEnvelopeReceivedDetailListNum(int redEnvelopeReceivedDetailListNum) {
        this.redEnvelopeReceivedDetailListNum = redEnvelopeReceivedDetailListNum;
    }

    public int getRemainderNum() {
        return remainderNum;
    }

    public void setRemainderNum(int remainderNum) {
        this.remainderNum = remainderNum;
    }

    public List<RPDetailItemInfo> getRedEnvelopeReceivedDetailList() {
        return redEnvelopeReceivedDetailList;
    }

    public void setRedEnvelopeReceivedDetailList(List<RPDetailItemInfo> redEnvelopeReceivedDetailList) {
        this.redEnvelopeReceivedDetailList = redEnvelopeReceivedDetailList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.receivedNum);
        dest.writeInt(this.totalNum);
        dest.writeInt(this.redEnvelopeReceivedDetailListNum);
        dest.writeInt(this.remainderNum);
        dest.writeList(this.redEnvelopeReceivedDetailList);
    }

    public RpDetailInfo() {
    }

    protected RpDetailInfo(Parcel in) {
        this.receivedNum = in.readInt();
        this.totalNum = in.readInt();
        this.redEnvelopeReceivedDetailListNum = in.readInt();
        this.remainderNum = in.readInt();
        this.redEnvelopeReceivedDetailList = new ArrayList<RPDetailItemInfo>();
        in.readList(this.redEnvelopeReceivedDetailList, List.class.getClassLoader());
    }

    public static final Parcelable.Creator<RpDetailInfo> CREATOR = new Parcelable.Creator<RpDetailInfo>() {
        public RpDetailInfo createFromParcel(Parcel source) {
            return new RpDetailInfo(source);
        }

        public RpDetailInfo[] newArray(int size) {
            return new RpDetailInfo[size];
        }
    };
}
