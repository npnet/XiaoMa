package com.xiaoma.launcher.schedule.manager;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.schedule.manager
 *  @file_name:      ScheduleDataManager
 *  @author:         Rookie
 *  @create_time:    2019/3/21 16:35
 *  @description：   TODO             */

import android.text.TextUtils;

import com.xiaoma.aidl.model.ScheduleInfo;
import com.xiaoma.db.IDatabase;
import com.xiaoma.launcher.common.LauncherUtils;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.common.manager.RequestManager;
import com.xiaoma.launcher.schedule.model.UploadScheduleResult;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.utils.log.KLog;

import org.simple.eventbus.EventBus;

import java.util.List;

public class ScheduleDataManager {

    private static final String FIELD_DATE = "date";

    public static IDatabase getDBManager() {
        return LauncherUtils.getDBManager();
    }

    /**
     * 获取所有日程
     */
    public static List<ScheduleInfo> getLocalScheduleInfos() {
        return getDBManager().queryAll(ScheduleInfo.class);
    }

    /**
     * 根据字段获取日程
     */
    public static List<ScheduleInfo> getLocalScheduleInfosForValue(String field, String value) {
        return getDBManager().queryByWhere(ScheduleInfo.class, field, value);
    }

    /**
     * 根据日期获取日程, 根据时间排序
     */
    public static List<ScheduleInfo> getLocalScheduleInfosForDate(String value) {
        return getDBManager().queryByWhere(ScheduleInfo.class, FIELD_DATE, value);
    }

    /**
     * 更新日程delay
     */
    public static void addScheduleByDelay(ScheduleInfo scheduleInfo) {
        getDBManager().save(scheduleInfo);
    }

    /**
     * 添加日程
     */
    static void addSchedule(ScheduleInfo scheduleInfo) {
        if (scheduleInfo.getType() != ScheduleInfo.RemindType.NORMAL) {
            getDBManager().save(scheduleInfo);
            EventBus.getDefault().post(scheduleInfo, LauncherConstants.UPDATE_STATUS);
            return;
        }
        long createDate = scheduleInfo.getTimestamp();  //创建时间
        String remindDate = scheduleInfo.getDate();     //日程提醒日期
        String remindBeginTime = TextUtils.isEmpty(scheduleInfo.getStartTime()) ? "" : scheduleInfo.getStartTime();   //日程提醒时间
        String remindEndTime = TextUtils.isEmpty(scheduleInfo.getEndTime()) ? "" : scheduleInfo.getEndTime();   //日程提醒时间
        String content = scheduleInfo.getMessage();
        RequestManager.getInstance().postUploadSch(createDate, remindDate, remindBeginTime, remindEndTime,
                content, new ResultCallback<XMResult<UploadScheduleResult>>() {
                    @Override
                    public void onSuccess(XMResult<UploadScheduleResult> result) {
                        UploadScheduleResult uploadScheduleResult = result.getData();
                        if (uploadScheduleResult != null) {
                            scheduleInfo.setCreateId(uploadScheduleResult.getId());
                            scheduleInfo.setIsUpload(true);
                            KLog.d("uploadsch success ");
                        } else {
                            KLog.d("uploadsch fail ");
                        }
                        getDBManager().save(scheduleInfo);
                        EventBus.getDefault().post(scheduleInfo, LauncherConstants.UPDATE_STATUS);
                    }

                    @Override
                    public void onFailure(int errorCode, String msg) {
                        KLog.d("uploadsch fail errorCode: " + errorCode + " msg: " + msg);
                        getDBManager().save(scheduleInfo);
                        EventBus.getDefault().post(scheduleInfo, LauncherConstants.UPDATE_STATUS);
                    }
                });

    }

//    /**
//     * 更新日程
//     */
//    public static void updateSchedule(ScheduleInfo scheduleInfo) {
//        DBManager.getInstance().getDBManager().update(scheduleInfo);
//    }

    /**
     * 删除日程
     */
    static void deleteSchedule(ScheduleInfo scheduleInfo) {
        if (scheduleInfo.getIsUpload()) {
            RequestManager.getInstance().postDelSch(scheduleInfo.getCreateId(), new ResultCallback<XMResult<String>>() {
                @Override
                public void onSuccess(XMResult<String> result) {
                    KLog.d("delsch success");
                }

                @Override
                public void onFailure(int code, String msg) {
                    KLog.d("delsch fail code: " + code + " msg: " + msg);
                }
            });
        }
        getDBManager().delete(scheduleInfo);
    }
}
