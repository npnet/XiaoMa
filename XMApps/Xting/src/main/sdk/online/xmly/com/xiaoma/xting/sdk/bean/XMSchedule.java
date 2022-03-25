package com.xiaoma.xting.sdk.bean;

import com.ximalaya.ting.android.opensdk.model.live.program.Program;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;

/**
 * @author youthyJ
 * @date 2018/10/13
 */
public class XMSchedule extends XMPlayableModel<Schedule> {
    public XMSchedule(Schedule schedule) {
        super(schedule);
    }

    public String getRealBeginTime() {
        return getSDKBean().getRealBeginTime();
    }

    public String getRealOverTime() {
        return getSDKBean().getRealOverTime();
    }

    public String getStartTime() {
        return getSDKBean().getStartTime();
    }

    public void setStartTime(String startTime) {
        getSDKBean().setStartTime(startTime);
    }

    public String getEndTime() {
        return getSDKBean().getEndTime();
    }

    public void setEndTime(String endTime) {
        getSDKBean().setEndTime(endTime);
    }

    public Program getRelatedProgram() {
        return getSDKBean().getRelatedProgram();
    }

    public void setRelatedProgram(Program relatedProgram) {
        getSDKBean().setRelatedProgram(relatedProgram);
    }

    public long getUpdateAt() {
        return getSDKBean().getUpdateAt();
    }

    public void setUpdateAt(long updateAt) {
        getSDKBean().setUpdateAt(updateAt);
    }

    public String getListenBackUrl() {
        return getSDKBean().getListenBackUrl();
    }

    public void setListenBackUrl(String listenBackUrl) {
        getSDKBean().setListenBackUrl(listenBackUrl);
    }

    public long getRadioId() {
        return getSDKBean().getRadioId();
    }

    public void setRadioId(long radioId) {
        getSDKBean().setRadioId(radioId);
    }

    public String getRadioName() {
        return getSDKBean().getRadioName();
    }

    public void setRadioName(String radioName) {
        getSDKBean().setRadioName(radioName);
    }

    public int getRadioPlayCount() {
        return getSDKBean().getRadioPlayCount();
    }

    public void setRadioPlayCount(int radioPlayCount) {
        getSDKBean().setRadioPlayCount(radioPlayCount);
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