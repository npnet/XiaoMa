package com.xiaoma.shop.business.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by LKF on 2019-6-28 0028.
 */
public class SkinTrialResult {
    @SerializedName("probation")
    private boolean canTrial;

    @SerializedName("desc")
    private String trialDesc;

    public boolean isCanTrial() {
        return canTrial;
    }

    public void setCanTrial(boolean canTrial) {
        this.canTrial = canTrial;
    }

    public String getTrialDesc() {
        return trialDesc;
    }

    public void setTrialDesc(String trialDesc) {
        this.trialDesc = trialDesc;
    }
}
