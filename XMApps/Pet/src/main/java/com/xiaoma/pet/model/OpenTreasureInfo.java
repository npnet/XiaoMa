package com.xiaoma.pet.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/24 0024 17:08
 *   desc:   开启宝箱
 * </pre>
 */
public class OpenTreasureInfo implements Parcelable {

    public static final Creator<OpenTreasureInfo> CREATOR = new Creator<OpenTreasureInfo>() {
        @Override
        public OpenTreasureInfo createFromParcel(Parcel in) {
            return new OpenTreasureInfo(in);
        }

        @Override
        public OpenTreasureInfo[] newArray(int size) {
            return new OpenTreasureInfo[size];
        }
    };
    private List<RewardDetails> rewardDetails;

    protected OpenTreasureInfo(Parcel in) {
        rewardDetails = in.createTypedArrayList(RewardDetails.CREATOR);
    }

    public List<RewardDetails> getRewardDetails() {
        return rewardDetails;
    }

    public void setRewardDetails(List<RewardDetails> rewardDetails) {
        this.rewardDetails = rewardDetails;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(rewardDetails);
    }
}
