package com.xiaoma.xting.sdk.bean;

import android.text.TextUtils;

import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;

/**
 * @author youthyJ
 * @date 2018/10/13
 */
public class XMRadio extends XMPlayableModel<Radio> {
    public XMRadio(Radio radio) {
        super(radio);
    }

    public String getRadioName() {
        return getSDKBean().getRadioName();
    }

    public void setRadioName(String radioName) {
        getSDKBean().setRadioName(radioName);
    }

    public String getRadioDesc() {
        return getSDKBean().getRadioDesc();
    }

    public void setRadioDesc(String radioDesc) {
        getSDKBean().setRadioDesc(radioDesc);
    }

    public String getProgramName() {
        return getSDKBean().getProgramName();
    }

    public long getProgramId() {
        return getSDKBean().getProgramId();
    }

    public void setProgramId(long programId) {
        getSDKBean().setProgramId(programId);
    }

    public void setProgramName(String programName) {
        getSDKBean().setProgramName(programName);
    }

    public long getScheduleID() {
        return getSDKBean().getScheduleID();
    }

    public void setScheduleID(long scheduleID) {
        getSDKBean().setScheduleID(scheduleID);
    }

    public long getStartTime() {
        return getSDKBean().getStartTime();
    }

    public void setStartTime(long startTime) {
        getSDKBean().setStartTime(startTime);
    }

    public long getEndTime() {
        return getSDKBean().getEndTime();
    }

    public void setEndTime(long endTime) {
        getSDKBean().setEndTime(endTime);
    }

    public int[] getSupportBitrates() {
        return getSDKBean().getSupportBitrates();
    }

    public void setSupportBitrates(int[] supportBitrates) {
        getSDKBean().setSupportBitrates(supportBitrates);
    }

    public String getRate24AacUrl() {
        return getSDKBean().getRate24AacUrl();
    }

    public void setRate24AacUrl(String rate24AacUrl) {
        getSDKBean().setRate24AacUrl(rate24AacUrl);
    }

    public String getRate24TsUrl() {
        return getSDKBean().getRate24TsUrl();
    }

    public void setRate24TsUrl(String rate24TsUrl) {
        getSDKBean().setRate24TsUrl(rate24TsUrl);
    }

    public String getRate64AacUrl() {
        return getSDKBean().getRate64AacUrl();
    }

    public void setRate64AacUrl(String rate64AacUrl) {
        getSDKBean().setRate64AacUrl(rate64AacUrl);
    }

    public String getRate64TsUrl() {
        return getSDKBean().getRate64TsUrl();
    }

    public void setRate64TsUrl(String rate64TsUrl) {
        getSDKBean().setRate64TsUrl(rate64TsUrl);
    }

    public int getRadioPlayCount() {
        return getSDKBean().getRadioPlayCount();
    }

    public void setRadioPlayCount(int radioPlayCount) {
        getSDKBean().setRadioPlayCount(radioPlayCount);
    }

    public String getCoverUrlSmall() {
        return getSDKBean().getCoverUrlSmall();
    }

    public void setCoverUrlSmall(String coverUrlSmall) {
        getSDKBean().setCoverUrlSmall(coverUrlSmall);
    }

    public String getCoverUrlLarge() {
        return getSDKBean().getCoverUrlLarge();
    }

    public void setCoverUrlLarge(String coverUrlLarge) {
        getSDKBean().setCoverUrlLarge(coverUrlLarge);
    }

    public long getUpdateAt() {
        return getSDKBean().getUpdateAt();
    }

    public void setUpdateAt(long updateAt) {
        getSDKBean().setUpdateAt(updateAt);
    }

    public String getShareUrl() {
        return getSDKBean().getShareUrl();
    }

    public void setShareUrl(String shareUrl) {
        getSDKBean().setShareUrl(shareUrl);
    }

    public boolean isActivityLive() {
        return getSDKBean().isActivityLive();
    }

    public void setActivityLive(boolean isActivityLive) {
        getSDKBean().setActivityLive(isActivityLive);
    }

    public long getActivityId() {
        return getSDKBean().getActivityId();
    }

    public void setActivityId(long activityId) {
        getSDKBean().setActivityId(activityId);
    }

    public String getValidCover() {
        if (!TextUtils.isEmpty(getSDKBean().getCoverUrlLarge())) {
            return getSDKBean().getCoverUrlLarge();
        } else {
            return !TextUtils.isEmpty(getSDKBean().getCoverUrlSmall())
                    ? getSDKBean().getCoverUrlLarge()
                    : "";
        }
    }

    public int hashCode() {
        return getSDKBean().hashCode();
    }

    public boolean equals(Object obj) {
        return getSDKBean().equals(obj);
    }

    public String toString() {
        return getSDKBean().toString();
    }
}
