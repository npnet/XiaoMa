package com.xiaoma.launcher.main.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.litesuits.orm.db.assit.QueryBuilder;
import com.xiaoma.aidl.model.ScheduleInfo;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.common.model.XfParserData;
import com.xiaoma.launcher.schedule.manager.ScheduleDataManager;
import com.xiaoma.launcher.schedule.manager.ScheduleRemindManager;
import com.xiaoma.launcher.schedule.model.CalenderItem;
import com.xiaoma.launcher.schedule.ui.CreateMemosDialog;
import com.xiaoma.launcher.schedule.ui.RemindVoiceDialog;
import com.xiaoma.launcher.schedule.ui.ScheduleDetailFragment;
import com.xiaoma.launcher.schedule.utils.DateUtil;
import com.xiaoma.launcher.schedule.view.ScheduleCalenderView;
import com.xiaoma.mapadapter.ui.MapActivity;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.FragmentUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.vr.iat.RemoteIatManager;
import com.xiaoma.vr.tts.EventTtsManager;

import org.json.JSONObject;
import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.HashSet;
import java.util.List;

/**
 * D:\Android\sdk\platform-tools
 * Created by ZSH on 2019/1/3 0003.
 */
@PageDescComponent(EventConstants.PageDescribe.CalendarActivityPagePathDesc)
public class CalendarActivity extends BaseActivity implements ScheduleCalenderView.OnDateChangedListener, View.OnClickListener {

    private static final String FRAGMENT_DETAIL_TAG = "fragment_detail_tag";

    private ScheduleCalenderView scheduleCalenderView;
    private ImageView addSchedule;
    public static final String TYPE_SCHEDULE = "scheduleX";
    private CreateMemosDialog mCreateMemosDialog;
    private RemindVoiceDialog mRemindVoiceDialog;
    private String mAssistantDate;

    private ScheduleInfo mScheduleInfo;
    private static final int REQUEST_MAP_CODE = 200;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        KLog.d("CalendarActivity onCreate");
        EventBus.getDefault().register(this);
        initView();
        initCalendarView();
        initSpeaker();
        registerAssistantReceiver();
    }

    private BroadcastReceiver mAssistantReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (LauncherConstants.SHOW_VOICE_ASSISTANT_DIALOG.equals(action)) {
                dismissRemindDialog();
            }
        }
    };

    private void registerAssistantReceiver() {
        IntentFilter filter = new IntentFilter();
        //语音助手dialog唤起
        filter.addAction(LauncherConstants.SHOW_VOICE_ASSISTANT_DIALOG);
        //语音助手dialog消失
        filter.addAction(LauncherConstants.DISMISS_VOICE_ASSISTANT_DIALOG);
        registerReceiver(mAssistantReceiver, filter);
    }

    private void initSpeaker() {
        EventTtsManager.getInstance().init(this);
    }


    private void initView() {
        addSchedule = findViewById(R.id.add_schedule);
        scheduleCalenderView = findViewById(R.id.schedule_view);
        addSchedule.setOnClickListener(this);
    }

    private void initCalendarView() {
        scheduleCalenderView.registerDateChangedListener(this);
    }

    private void replaceFragment(BaseFragment fragment) {
        FragmentUtils.replace(getSupportFragmentManager(), fragment, com.xiaoma.smarthome.R.id.container_frame, CalendarActivity.FRAGMENT_DETAIL_TAG, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle bundleExtra = getIntent().getExtras();
        if (bundleExtra != null) {
            mAssistantDate = bundleExtra.getString(LauncherConstants.ActionExtras.DATE);
        }
        if (scheduleCalenderView != null) {
            scheduleCalenderView.isChecked();
            scheduleCalenderView.updateView();
        }
        if (StringUtil.isNotEmpty(mAssistantDate)) {
            replaceFragment(ScheduleDetailFragment.newInstance(mAssistantDate.replace("-", "/")));
        }
    }

    private void showRemind(String voiceContent) {
        if (TextUtils.isEmpty(voiceContent)) {
            createSchFailed(getString(R.string.create_sch_fail));
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(voiceContent);
            JSONObject intentData = jsonObject.optJSONObject("intent");
            XfParserData xfParserData = GsonHelper.fromJson(intentData.toString(), XfParserData.class);
            if (xfParserData != null) {
                parserVoiceData(xfParserData);
            } else {
                createSchFailed(getString(R.string.create_sch_fail));
            }
        } catch (Exception e) {
            createSchFailed(getString(R.string.create_sch_fail));
            e.printStackTrace();
        }

    }

    private void createSchFailed(String msg) {
        if (mRemindVoiceDialog != null && mRemindVoiceDialog.isShowing()) {
            mRemindVoiceDialog.dismiss();
        }
        EventTtsManager.getInstance().stopSpeaking();
        EventTtsManager.getInstance().startSpeaking(msg);
    }

    private void parserVoiceData(XfParserData xfParserData) {
        if (!TYPE_SCHEDULE.equals(xfParserData.getService())) {
            createSchFailed(getString(R.string.say_about_sch_remind));
            return;
        }
        XfParserData.SemanticBean.SlotsBeanX slots = xfParserData.getSemantic().getSlots();

        final ScheduleInfo scheduleInfo = new ScheduleInfo();
        if (slots.getDatetime() == null || TextUtils.isEmpty(slots.getDatetime().getDate()) || TextUtils.isEmpty(slots.getDatetime().getTime())) {
            createSchFailed(getString(R.string.no_time_say_again));
            return;
        }
        String date = slots.getDatetime().getDate().replace("-", "/");
        String time = slots.getDatetime().getTime().substring(0, 5);
        //如果日程时间是过去的时间
        if (DateUtil.date2Ms(date + time) < System.currentTimeMillis()) {
            createSchFailed(getString(R.string.wrong_time_say_again));
            return;
        }
        //如果没说具体日程事件
        if (TextUtils.isEmpty(slots.getContent())) {
            createSchFailed(getString(R.string.no_msg_say_again));
            return;
        }
        scheduleInfo.setDate(date);
        scheduleInfo.setTime(time);
        scheduleInfo.setStartTime(time);
        scheduleInfo.setEndTime(time);
        scheduleInfo.setTimestamp(DateUtil.date2Ms(date + time));
        scheduleInfo.setMessage(slots.getContent());

        ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {

            @Override
            public void run() {
                //取消日程弹窗
                dismissRemindDialog();
                //创建日程
                createMemosDialog(scheduleInfo);
            }
        }, 1000);
    }


    @Override
    protected void onPause() {
        super.onPause();
        dismissRemindDialog();
    }

    @Override
    public void onChanged(int year, int month, int clickDay) {
        List<CalenderItem> calenderItems = scheduleCalenderView.getCalenderItem(this, year, month, clickDay);
        changeCalenderItemsForDBData(calenderItems);
    }

    @Override
    public void onItemClick(String date, String[] lunar) {
        replaceFragment(ScheduleDetailFragment.newInstance(date, lunar));
    }


    private void changeCalenderItemsForDBData(List<CalenderItem> calenderItems) {
        QueryBuilder queryBuilder = new QueryBuilder<>(ScheduleInfo.class);
        queryBuilder.orderBy("timestamp");
        //从数据库获取数据
        List<ScheduleInfo> scheduleInfos = ScheduleDataManager.getDBManager().queryData(queryBuilder);
        KLog.d("changeCalenderItemsForDBData  scheduleInfos " + scheduleInfos);
        if (ListUtils.isEmpty(scheduleInfos)) {
            return;
        }
        HashSet<String> dateList = new HashSet<>();
        for (ScheduleInfo scheduleInfo : scheduleInfos) {
            if (!scheduleInfo.isBefore()) {
                dateList.add(scheduleInfo.getDate());
            }
        }
        KLog.d("changeCalenderItemsForDBData   dateList" + dateList);
        if (!ListUtils.isEmpty(dateList)) {
            for (CalenderItem calenderItem : calenderItems) {
                if (dateList.contains(calenderItem.getDate())) {
                    calenderItem.setHasData(true);
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        KLog.d("CalendarActivity onDestroy");
        if (scheduleCalenderView != null) {
            scheduleCalenderView.unregisterDateChangedListener();
        }
        EventBus.getDefault().unregister(this);
        RemoteIatManager.getInstance().release();
        unregisterReceiver(mAssistantReceiver);
        super.onDestroy();
    }

    @Override
    @NormalOnClick({EventConstants.NormalClick.CALENDAR_ADD_SCHEDULE})//按钮对应的名称
    @ResId({R.id.add_schedule})//按钮对应的R文件id
    public void onClick(View view) {
        if (view.getId() == R.id.add_schedule) {
            //添加日程
            addSchdule();
        }
    }

    private void addSchdule() {
        mRemindVoiceDialog = new RemindVoiceDialog(this);
        mRemindVoiceDialog.setOnVoiceContentComplete(new RemindVoiceDialog.OnVoiceContentListener() {
            @Override
            public void onIatComplete(String content) {
                showRemind(content);
            }

            @Override
            public void onIatFail() {
                createSchFailed(getString(R.string.not_clear_say_again));
            }
        });

        mRemindVoiceDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                EventTtsManager.getInstance().stopSpeaking();
                RemoteIatManager.getInstance().stopListening();
                RemoteIatManager.getInstance().release();
                EventTtsManager.getInstance().destroy();
            }
        });

        mRemindVoiceDialog.setCanceledOnTouchOutside(true);
        mRemindVoiceDialog.show();
    }

    private void createMemosDialog(final ScheduleInfo schedule) {
        mCreateMemosDialog = new CreateMemosDialog(this, schedule);
        mCreateMemosDialog.show();
        mCreateMemosDialog.registerCreateMemosListener(new CreateMemosDialog.OnMemosListener() {
            @Override
            public void clickSure(ScheduleInfo scheduleInfo) {
                mCreateMemosDialog.done();
                //添加这个日程
                ScheduleRemindManager.getInstance().addScheduleRemind(scheduleInfo);
            }

            @Override
            public void cancle() {

            }

            @Override
            public void clickLocation(ScheduleInfo scheduleInfo) {
                mScheduleInfo = scheduleInfo;
                startActivityForResult(new Intent(CalendarActivity.this, MapActivity.class), REQUEST_MAP_CODE);
            }
        });
    }

    private void dismissRemindDialog() {
        if (mRemindVoiceDialog != null && mRemindVoiceDialog.isShowing()) {
            mRemindVoiceDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null && !fragments.isEmpty()) {
            for (int i = fragments.size() - 1; i >= 0; i--) {
                Fragment child = fragments.get(i);
                if (child.getChildFragmentManager().popBackStackImmediate()) {
                    return;
                }
            }
        }
        if (getSupportFragmentManager().popBackStackImmediate()) {
            return;
        }
        EventTtsManager.getInstance().stopSpeaking();
        if (mRemindVoiceDialog != null && mRemindVoiceDialog.isShowing()) {
            mRemindVoiceDialog.dismiss();
        }
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        KLog.d("CalendarActivity onNewIntent");
    }

    @Subscriber(tag = LauncherConstants.UPDATE_STATUS)
    private void updateCalendar(ScheduleInfo scheduleInfo) {
        if (scheduleCalenderView != null) {
            scheduleCalenderView.isChecked();
            scheduleCalenderView.updateView();
        }
    }
}
