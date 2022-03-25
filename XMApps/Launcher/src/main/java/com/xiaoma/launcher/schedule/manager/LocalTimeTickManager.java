package com.xiaoma.launcher.schedule.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.xiaoma.aidl.model.ScheduleInfo;
import com.xiaoma.launcher.schedule.utils.DateUtil;

import java.util.Calendar;


public class LocalTimeTickManager {

    private static final String TAG = LocalTimeTickManager.class.getSimpleName();

    private static LocalTimeTickManager INSTANCE;

    private ITimeTickEventListener mITimeTickEventListener;

    private long mStartOfTomorrow;

    public static final String START_TIME = "startTime";
    public static final String END_TIME = "endTime";
    public static final String PACKAGE_NAME = "packageName";
    public static final String COMPONENT_NAME = "className";

    //电台
    private static final String ACTION_LOCAL_FM = "xting.local.fm";
    public static final String SUB_FM_MSG = "info";

    //车服务
    private static final String ACTION_CAR_SERVICE = "com.xiaoma.service.notification";
    public static final String SUB_CAR_SERVICE_MSG = "content";
    public static final String SUB_CAR_SERVICE_TITLE = "title";
    public static final String SUB_CAR_SERVICE_START_DATE = "startDate";

    private BroadcastReceiver mTimeTickReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (Intent.ACTION_TIME_TICK.equals(action)) {
                long currentTimeMillis = DateUtil.getCurrentTimeMinute();

                if (mITimeTickEventListener != null) {
                    mITimeTickEventListener.updateTime(context, currentTimeMillis);
                }

                if (currentTimeMillis >= mStartOfTomorrow) {
                    mITimeTickEventListener.onDateChanged();
                    mStartOfTomorrow = DateUtil.startOfTomorrow();
                }
            } else if (ACTION_LOCAL_FM.equals(action)) {
                //电台节目订阅通知
                addSubFMMsg(intent);
            } else if (ACTION_CAR_SERVICE.equals(action)) {
                //车服务订阅通知
                addSubCarServiceMsg(intent);
            }
        }
    };

    private void addSubCarServiceMsg(Intent intent) {

        String packageName = intent.getStringExtra(PACKAGE_NAME);
        String component = intent.getStringExtra(COMPONENT_NAME);
        String endTime = intent.getStringExtra(START_TIME);
        String startDate = intent.getStringExtra(SUB_CAR_SERVICE_START_DATE);
        String title = intent.getStringExtra(SUB_CAR_SERVICE_TITLE);
        String message = intent.getStringExtra(SUB_CAR_SERVICE_MSG);

        //提前一天的车服务日程提醒
        Calendar instance = Calendar.getInstance();
        String[] dateStr = startDate.split("/");
        instance.set(Integer.parseInt(dateStr[0]), Integer.parseInt(dateStr[1]), Integer.parseInt(dateStr[2]));

        //原始的车服务
        ScheduleInfo scheduleInfo = new ScheduleInfo();
        scheduleInfo.setPackageName(packageName);
        scheduleInfo.setComponent(component);
        scheduleInfo.setStartTime("00:00");
        scheduleInfo.setEndTime(endTime);
        scheduleInfo.setDate(startDate);
        scheduleInfo.setTime(endTime);
        scheduleInfo.setCarServiceTitle(title);
        scheduleInfo.setMessage(message);
        scheduleInfo.setTimestamp(DateUtil.date2Ms(startDate + endTime));
        scheduleInfo.setType(ScheduleInfo.RemindType.CAR_SERVICE);
        ScheduleRemindManager.getInstance().addScheduleRemind(scheduleInfo);


        //日程当天的前一天
        instance.add(Calendar.DATE, -1);
        int year = instance.get(Calendar.YEAR);
        int month = instance.get(Calendar.MONTH);
        int date = instance.get(Calendar.DATE);
        String endCalTime = DateUtil.formatDate(year, month, date);
        ScheduleInfo scheduleInfoB = new ScheduleInfo();
        scheduleInfoB.setPackageName(packageName);
        scheduleInfoB.setComponent(component);
        scheduleInfoB.setEndTime("23:59");
        scheduleInfoB.setStartTime("00:00");
        scheduleInfoB.setTime(endTime);
        scheduleInfoB.setDate(endCalTime);
        scheduleInfoB.setCarServiceTitle(title);
        scheduleInfoB.setMessage(message);
        scheduleInfoB.setTimestamp(DateUtil.date2Ms(endCalTime + endTime));
        scheduleInfoB.setType(ScheduleInfo.RemindType.CAR_SERVICE);
        scheduleInfoB.setBefore(true);
        scheduleInfoB.setDateBefore(startDate);
        ScheduleRemindManager.getInstance().addScheduleRemind(scheduleInfoB);

    }

    private void addSubFMMsg(Intent intent) {
        String packageName = intent.getStringExtra(PACKAGE_NAME);
        String startTime = intent.getStringExtra(START_TIME);
        String endTime = intent.getStringExtra(END_TIME);
        String component = intent.getStringExtra(COMPONENT_NAME);
        String subMsg = intent.getStringExtra(SUB_FM_MSG);

        ScheduleInfo scheduleInfo = new ScheduleInfo();
        scheduleInfo.setPackageName(packageName);
        scheduleInfo.setComponent(component);
        scheduleInfo.setInfoMsg(subMsg);
        scheduleInfo.setStartTime(startTime);
        scheduleInfo.setEndTime(endTime);
        scheduleInfo.setDate(DateUtil.getCurrentFormatDate());
        scheduleInfo.setTime(startTime);
        scheduleInfo.setMessage("你订阅的节目即将开播");
        scheduleInfo.setTimestamp(DateUtil.date2Ms(DateUtil.getCurrentFormatDate() + startTime));
        scheduleInfo.setType(ScheduleInfo.RemindType.FM);

        ScheduleRemindManager.getInstance().addScheduleRemind(scheduleInfo);
    }

    public static LocalTimeTickManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LocalTimeTickManager();
        }
        return INSTANCE;
    }


    public void open(Context context) {
        mStartOfTomorrow = DateUtil.startOfTomorrow();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        //本地电台订阅
        filter.addAction(ACTION_LOCAL_FM);
        //车服务订阅
        filter.addAction(ACTION_CAR_SERVICE);
        context.registerReceiver(mTimeTickReceiver, filter);
    }


    public void release(Context context) {
        unregisterTimeTickEventListener();
        context.unregisterReceiver(mTimeTickReceiver);
    }


    public interface ITimeTickEventListener {

        void updateTime(Context context, long currentTimeMillis);

        void onDateChanged();
    }


    public void registerTimeTickEventListener(ITimeTickEventListener listener) {
        mITimeTickEventListener = listener;
    }

    public void unregisterTimeTickEventListener() {
        mITimeTickEventListener = null;
    }
}
