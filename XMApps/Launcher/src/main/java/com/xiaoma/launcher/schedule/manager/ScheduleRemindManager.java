package com.xiaoma.launcher.schedule.manager;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.graphics.drawable.IconCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.xiaoma.aidl.model.ScheduleInfo;
import com.xiaoma.autotracker.XmTracker;
import com.xiaoma.autotracker.model.TrackerCountType;
import com.xiaoma.component.AppHolder;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.main.ui.CalendarActivity;
import com.xiaoma.launcher.schedule.utils.DateUtil;
import com.xiaoma.systemuilib.NotificationUtil;
import com.xiaoma.utils.LaunchUtils;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;

import org.simple.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleRemindManager implements LocalTimeTickManager.ITimeTickEventListener {

    public static final String TAG = "ScheduleRemindManager";

    private static ScheduleRemindManager INSTANCE;
    private static final long SCHEDULE_REMIND_DELAY_TIME = 30 * 60 * 1000L;
    private static final long SCHEDULE_REMIND_LIMIT_TIME = 60 * 60 * 1000L;
    private static final long ONE_MINUTE = 60 * 1000L;
    private static final int REMIND_DIALOG_WIDTH = 660;
    private static final int REMIND_DIALOG_HEIGHT = 425;
    private Map<Long, ScheduleInfo> mScheduleMap = new HashMap<>();
    private LocalTimeTickManager mLocalTimeTickManager;
    private WindowManager mWindowManager;
    private View mRemindView;
    private WindowManager.LayoutParams mParams;

    private boolean isShowDialog;
    private TextView mTvTime;
    private TextView mTvMessage;
    private TextView mTvLocation;
    private Button mBtnSure;
    private Button mBtnCancle;

    private int requestId;
    private TextView mTvTitle;

    private ScheduleRemindManager() {
    }

    public static ScheduleRemindManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScheduleRemindManager();
        }
        return INSTANCE;
    }

    public void open(Context context) {
        startTimeTickManager(context);
        initScheduleInfos();
    }

    private void startTimeTickManager(Context context) {
        mLocalTimeTickManager = LocalTimeTickManager.getInstance();
        mLocalTimeTickManager.registerTimeTickEventListener(this);
        mLocalTimeTickManager.open(context);
    }

    private void initScheduleInfos() {
        String currentFormatDate = DateUtil.getCurrentFormatDate();
        KLog.d(TAG, "currentFormatDate: " + currentFormatDate);
        //获取当天的日程
        List<ScheduleInfo> scheduleInfos = ScheduleDataManager.getLocalScheduleInfosForDate(currentFormatDate);

        if (ListUtils.isEmpty(scheduleInfos)) {
            return;
        }
        for (ScheduleInfo info : scheduleInfos) {
            long scheduleTime = DateUtil.date2Ms(info.getDate() + info.getTime());
            //将今天日程存进来
            mScheduleMap.put(scheduleTime, info);
        }
    }


    /**
     * 删除单个日程
     *
     * @param info
     */
    public void removeScheduleRemind(ScheduleInfo info) {
        KLog.d(TAG, "removeScheduleRemind");
        int type = info.getType();
        String time;
        if ((type == ScheduleInfo.RemindType.CAR_SERVICE && info.isBefore()) || type == ScheduleInfo.RemindType.FM) {
            time = info.getEndTime();
        } else {
            time = info.getTime();
        }
        long scheduleTime = DateUtil.date2Ms(info.getDate() + time);
        if (mScheduleMap.containsKey(scheduleTime)) {
            mScheduleMap.remove(scheduleTime);
        }
        ScheduleDataManager.deleteSchedule(info);
    }

    /**
     * 添加单个日程
     *
     * @param info
     */
    public void addScheduleRemind(ScheduleInfo info) {
        int type = info.getType();
        String time;
        if ((type == ScheduleInfo.RemindType.CAR_SERVICE && info.isBefore()) || type == ScheduleInfo.RemindType.FM) {
            time = info.getEndTime();
        } else {
            time = info.getTime();
        }
        long scheduleTime = DateUtil.date2Ms(info.getDate() + time);
        //如果日程时间是过去的时间
        if (scheduleTime < System.currentTimeMillis()) {
            KLog.d(TAG, "addScheduleRemind fail");
            return;
        }
        //如果是今天的日程，就添加进今天的map里
        if (DateUtil.getCurrentFormatDate().equals(info.getDate())) {
            mScheduleMap.put(scheduleTime, info);
        }
        ScheduleDataManager.addSchedule(info);
        KLog.d(TAG, "addScheduleRemind success");
        XmTracker.getInstance().uploadEvent(-1, TrackerCountType.CREATESCHEDULE.getType());
    }


    /**
     * 日程提醒
     *
     * @param scheduleInfo
     */
    private void scheduleRemind(Context context, final long time, final ScheduleInfo scheduleInfo) {
        KLog.d(TAG, "startScheduleRemind: " + scheduleInfo.getDate() + scheduleInfo.getMessage());
        showRemindWindow(context, time, scheduleInfo);
        //showRemindNotification(context, time, scheduleInfo);
    }


    private void showRemindNotification(Context context, final long time, final ScheduleInfo scheduleInfo) {
        String title = null;
        Intent intent = null;
        //id区分不同通知 避免之前notification被覆盖
        int type = scheduleInfo.getType();
        if (type == ScheduleInfo.RemindType.FM) {
            //跳转到电台xting
            Bundle bundle = new Bundle();
            bundle.putString(LocalTimeTickManager.START_TIME, scheduleInfo.getStartTime());
            bundle.putString(LocalTimeTickManager.END_TIME, scheduleInfo.getEndTime());
            bundle.putString(LocalTimeTickManager.SUB_FM_MSG, scheduleInfo.getInfoMsg());
            intent = new Intent();
            ComponentName componentName = new ComponentName(scheduleInfo.getPackageName(), scheduleInfo.getComponent());
            intent.setComponent(componentName);
            intent.putExtra("bundle", bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            title = context.getString(R.string.xting_sch_remind);

        } else if (type == ScheduleInfo.RemindType.CAR_SERVICE) {
            //跳转到车服务
            intent = new Intent();
            ComponentName componentName = new ComponentName(scheduleInfo.getPackageName(), scheduleInfo.getComponent());
            intent.setComponent(componentName);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            title = scheduleInfo.getCarServiceTitle();

        } else if (type == ScheduleInfo.RemindType.NORMAL) {
            title = context.getString(R.string.sch_remind);
            intent = new Intent(context, CalendarActivity.class);

        }
        PendingIntent chatPendingIntent = PendingIntent.getActivity(context, requestId,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            Notification notification = NotificationUtil.builder(context,
                    title,
                    scheduleInfo.getMessage(),
                    IconCompat.createWithResource(context, R.drawable.icon_app_launcher).toIcon(),
                    chatPendingIntent,
                    System.currentTimeMillis(), true)
                    .setAutoCancel(true).build();

            NotificationManagerCompat.from(context)
                    .notify(null, requestId++, notification);

            //提醒后就删除该日程
            mScheduleMap.remove(time);
            ScheduleDataManager.deleteSchedule(scheduleInfo);
        }

    }

    /**
     * 显示日程弹窗 一次只显示一个弹窗不重叠
     *
     * @param context
     * @param time
     * @param scheduleInfo
     */
    private void showRemindWindow(final Context context, final long time, final ScheduleInfo scheduleInfo) {

        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            mParams = new WindowManager.LayoutParams();
            mParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            mParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            }*/
            mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            mParams.format = PixelFormat.TRANSLUCENT;
            mParams.gravity = Gravity.CENTER;

            mRemindView = LayoutInflater.from(AppHolder.getInstance().getAppContext()).inflate(R.layout.view_dialog_remind, null);
            mTvTitle = mRemindView.findViewById(R.id.tv_title);
            mTvTime = mRemindView.findViewById(R.id.tv_time);
            mTvMessage = mRemindView.findViewById(R.id.tv_message);
            mTvLocation = mRemindView.findViewById(R.id.tv_location);
            mBtnSure = mRemindView.findViewById(R.id.btn_sure);
            mBtnCancle = mRemindView.findViewById(R.id.btn_cancle);
        }
        final int type = scheduleInfo.getType();
        String date;
        String title;
        if (type == ScheduleInfo.RemindType.CAR_SERVICE) {
            if (TextUtils.isEmpty(scheduleInfo.getDateBefore())) {
                date = scheduleInfo.getDate();
            } else {
                date = scheduleInfo.getDateBefore();
            }
            title = scheduleInfo.getCarServiceTitle();
        } else if (type == ScheduleInfo.RemindType.FM) {
            date = scheduleInfo.getDate();
            title = context.getString(R.string.xting_sch_remind);
        } else {
            date = scheduleInfo.getDate();
            title = context.getString(R.string.sch_remind);
        }
        mTvTitle.setText(title);
        mTvTime.setText(String.format(context.getString(R.string.schedule_time), date, scheduleInfo.getTime()));
        mTvMessage.setText(scheduleInfo.getMessage());
        mTvLocation.setText(StringUtil.isEmpty(scheduleInfo.getLocation()) ? context.getString(R.string.none) : scheduleInfo.getLocation());

        mBtnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowDialog) {
                    mWindowManager.removeViewImmediate(mRemindView);
                    isShowDialog = false;
                }
                if (type == ScheduleInfo.RemindType.FM) {
                    Bundle bundle = new Bundle();
                    bundle.putString(LocalTimeTickManager.START_TIME, scheduleInfo.getStartTime());
                    bundle.putString(LocalTimeTickManager.END_TIME, scheduleInfo.getEndTime());
                    bundle.putString(LocalTimeTickManager.SUB_FM_MSG, scheduleInfo.getInfoMsg());
                    LaunchUtils.launchAppWithData(context, scheduleInfo.getPackageName(),
                            scheduleInfo.getComponent(), bundle);
                } else if (type == ScheduleInfo.RemindType.CAR_SERVICE) {
                    LaunchUtils.launchAppWithData(context, scheduleInfo.getPackageName(),
                            scheduleInfo.getComponent(), null);
                }
                //如果用户点了确认 就从提醒名单里删除该日程
                removeScheduleRemind(scheduleInfo);
                EventBus.getDefault().post(scheduleInfo, LauncherConstants.UPDATE_STATUS);
            }
        });

        mBtnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scheduleInfo.getNeedDelayTime() == 0) {
                    //如果用户点了取消 就延迟半小时后再弹出
                    scheduleInfo.setNeedDelayTime(System.currentTimeMillis() + SCHEDULE_REMIND_DELAY_TIME);
                    mScheduleMap.put(time, scheduleInfo);
                    //更新info
                    ScheduleDataManager.addScheduleByDelay(scheduleInfo);
                    KLog.d(TAG, "delaytime:" + DateUtil.getTimeFromMillisecond(System.currentTimeMillis() + SCHEDULE_REMIND_DELAY_TIME));
                } else {
                    //如果之前点了取消,第二次点取消就删除该日程
                    removeScheduleRemind(scheduleInfo);
                    EventBus.getDefault().post(scheduleInfo, LauncherConstants.UPDATE_STATUS);
                    KLog.d(TAG, "delaytime remove:" + DateUtil.getTimeFromMillisecond(time));
                }
                if (isShowDialog) {
                    mWindowManager.removeViewImmediate(mRemindView);
                    isShowDialog = false;
                }
            }
        });
        if (!isShowDialog) {
            mWindowManager.addView(mRemindView, mParams);
            isShowDialog = true;
        }
    }


    public void release(Context context) {
        mLocalTimeTickManager.unregisterTimeTickEventListener();
        mLocalTimeTickManager.release(context);
    }

    @Override
    public void updateTime(Context context, long currentTimeMillis) {
        KLog.d(TAG, "updateTime currentTimeMillis" + currentTimeMillis + " mScheduleMap.size:" + mScheduleMap.size());
        //由于判断逻辑过多，防止耗时，就放在子线程运行
        for (Long time : mScheduleMap.keySet()) {
            KLog.d(TAG, "time：" + time);

            ScheduleInfo scheduleInfo = mScheduleMap.get(time);
            long needDelayTime = scheduleInfo.getNeedDelayTime();
            int type = scheduleInfo.getType();
            if (type == ScheduleInfo.RemindType.NORMAL &&
                    currentTimeMillis > (time - SCHEDULE_REMIND_LIMIT_TIME) && currentTimeMillis < (time + SCHEDULE_REMIND_LIMIT_TIME)) {
                //普通日程提醒
                showRemindWithDelay(context, currentTimeMillis, time, scheduleInfo, needDelayTime);

            } else if (type == ScheduleInfo.RemindType.FM) {
                //如果是电台提醒
                long subStartTime = DateUtil.date2Ms(scheduleInfo.getDate() + scheduleInfo.getStartTime());
                long subEndTime = DateUtil.date2Ms(scheduleInfo.getDate() + scheduleInfo.getEndTime());
                if (currentTimeMillis >= subStartTime && currentTimeMillis <= subEndTime) {
                    showRemindWithDelay(context, currentTimeMillis, time, scheduleInfo, needDelayTime);
                }

            } else if (type == ScheduleInfo.RemindType.CAR_SERVICE) {
                //车服务提醒
                long subStartTime = DateUtil.date2Ms(scheduleInfo.getDate() + scheduleInfo.getStartTime());
                long subEndTime = DateUtil.date2Ms(scheduleInfo.getDate() + scheduleInfo.getEndTime());
                //在时间范围内就提醒
                if (currentTimeMillis >= subStartTime && currentTimeMillis <= subEndTime) {
                    showRemindWithDelay(context, currentTimeMillis, time, scheduleInfo, needDelayTime);
                }
            }
        }

    }

    private void showRemindWithDelay(Context context, long currentTimeMillis, Long time, ScheduleInfo scheduleInfo, long needDelayTime) {
        if (needDelayTime == 0 || (currentTimeMillis > needDelayTime - ONE_MINUTE && needDelayTime + ONE_MINUTE > currentTimeMillis)) {
            //如果没有延迟时间并且在节目时间范围内 直接提醒该日程 或者
            //如果有延迟时间 并且在这个范围内（误差一分钟）
            scheduleRemind(context, time, scheduleInfo);
        }
    }

    @Override
    public void onDateChanged() {
        KLog.d(TAG, "onDateChanged");
        initScheduleInfos();
    }
}
