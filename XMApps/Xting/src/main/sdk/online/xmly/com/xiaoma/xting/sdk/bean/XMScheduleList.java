package com.xiaoma.xting.sdk.bean;

import com.xiaoma.adapter.base.XMBean;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
import com.ximalaya.ting.android.opensdk.model.live.schedule.ScheduleList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author youthyJ
 * @date 2018/10/13
 */
public class XMScheduleList extends XMBean<ScheduleList> {
    public XMScheduleList(ScheduleList scheduleList) {
        super(scheduleList);
    }

    public List<XMSchedule> getmScheduleList() {
        List<Schedule> schedules = getSDKBean().getmScheduleList();
        ArrayList<XMSchedule> xmSchedules = new ArrayList<>();
        if (schedules != null && !schedules.isEmpty()) {
            for (Schedule schedule : schedules) {
                if (schedule == null) {
                    continue;
                }
                xmSchedules.add(new XMSchedule(schedule));
            }
        }
        return xmSchedules;
    }

    public void setmScheduleList(List<XMSchedule> xmScheduleList) {
        if (xmScheduleList == null) {
            getSDKBean().setmScheduleList(null);
            return;
        }
        List<Schedule> schedules = new ArrayList<>();
        for (XMSchedule xmSchedule : xmScheduleList) {
            if (xmSchedule == null) {
                continue;
            }
            schedules.add(xmSchedule.getSDKBean());
        }
        getSDKBean().setmScheduleList(schedules);
    }

}
