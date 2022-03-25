package com.xiaoma.setting.other.ui;

import android.app.AlarmManager;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.setting.R;
import com.xiaoma.setting.common.constant.EventConstants;
import com.xiaoma.setting.common.views.MagicTextView;
import com.xiaoma.setting.common.views.SettingItemView;
import com.xiaoma.setting.common.views.SwitchAnimation;
import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.utils.log.KLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Administrator on 2018/10/10 0010.
 */

@PageDescComponent(EventConstants.PageDescribe.languageSettingFragmentPagePathDesc)
public class LanguageFragment extends BaseFragment implements View.OnClickListener, SettingItemView.StateListener, XmCarVendorExtensionManager.ValueChangeListener {

    public static final String ACTION_UPDATE_TIME = "ACTION_UPDATE_TIME";
    public static final long MIN_DATE = 1194220800000L;
    private static final String TAG = LanguageFragment.class.getSimpleName();
    private static final int MILLIS_IN_SECOND = 1000;
    private static final String CAR_CONTROL_REQ = "car_control_req";
    private TimePickerView mPvTime;
    private TimePickerView mPvFullTime;
    private TimePickerView mPvDate;
    private OptionsPickerView mPvZone;
    private MagicTextView mTvShowZone, mTvShowDate, mTvShowTime;
    private String mTrueTimeText;
    private List<ZoneData> mZoneList = new ArrayList<>();
    //存放时区信息的HashMap
    private HashMap<String, String> mZoneMap = new HashMap<String, String>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH时mm分ss秒");
    private SimpleDateFormat halfTimeFormat = new SimpleDateFormat("hh时mm分ss秒");
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
    private SimpleDateFormat halfSimpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 hh时mm分ss秒");
    private int mSelectedZone;
    private int mTimeType, mLanguageType;
    private SettingItemView mSivLanguageType;
    private RelativeLayout mShowDateLayout, mShowTimeLayout, mShowZoneLayout;
    //    private SettingSwitch mSwitchSyncZone;
    private SwitchAnimation mSwitchSyncTime;
    private ImageView mDateTimeLine, mZoneLine;
    private Integer[] showTime = null;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == ACTION_UPDATE_TIME) {
                initCurrentTime();
            }
        }
    };

    //获取系统默认时区
    public static String getDefaultTimeZone() {
        KLog.d("ljb", "getDefaultTimeZone:" + TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT));
        return TimeZone.getDefault().getDisplayName();
    }

    //获取系统当前时间
    public void getCurrentTime() {
        Integer[] showTime = XmCarFactory.getCarVendorExtensionManager().getShowTime();
        String timeLog = "";
        try {
            for (int i = 0; i < showTime.length; i++) {
                timeLog += "[" + i + ", " + showTime[i] + "]" + ",";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("hzx", "curTime: " + timeLog);
        this.showTime = showTime;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_language, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bindView(view);
        XmCarVendorExtensionManager.getInstance().addValueChangeListener(this);
        initData();
        IntentFilter intentFilter = new IntentFilter(ACTION_UPDATE_TIME);
        mContext.registerReceiver(mBroadcastReceiver, intentFilter);
    }

    private void bindView(View view) {
        mSwitchSyncTime = view.findViewById(R.id.switch_sync_time);
        /*mSwitchSyncTime.setListener(new SettingSwitch.StateListener() {
            @Override
            public void onCheck(int viewId, boolean isChecked) {
                syncTimeViewVisible(isChecked);
                String status = isChecked ? "打开" : "关闭";
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.syncTimeSetting, status,
                        TAG, EventConstants.PageDescribe.languageSettingFragmentPagePathDesc);
            }
        });*/
        mSwitchSyncTime.setListener(new SwitchAnimation.SwitchListener() {
            @Override
            public void onSwitch(int viewId, boolean state) {
//                syncTimeViewVisible(state);
                setAutoDateTime(state ? 1 : 0);
                String status = state ? "打开" : "关闭";
                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.syncTimeSetting, status,
                        TAG, EventConstants.PageDescribe.languageSettingFragmentPagePathDesc);
            }
        });
//        mSwitchSyncZone = view.findViewById(R.id.switch_sync_zone);
//        mSwitchSyncZone.setChecked(true);
//        mSwitchSyncZone.setListener(new SettingSwitch.StateListener() {
//            @Override
//            public void onCheck(int viewId, boolean isChecked) {
//                /*if (isChecked) {
//                    mShowZoneLayout.setVisibility(View.GONE);
//                    mZoneLine.setVisibility(View.GONE);
//                    setAutoTimeZone(1);
//                } else {
//                    mShowZoneLayout.setVisibility(View.VISIBLE);
//                    mZoneLine.setVisibility(View.VISIBLE);
//                    setAutoTimeZone(0);
//                }*/
//                String status = isChecked ? "打开" : "关闭";
//                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.autoTimeZoneSetting, status,
//                        TAG, EventConstants.PageDescribe.languageSettingFragmentPagePathDesc);
//            }
//        });

//        mSivTimeType = view.findViewById(R.id.siv_time_type);
//        mSivTimeType.setListener(this);
        mSivLanguageType = view.findViewById(R.id.siv_language_type);


        mShowDateLayout = view.findViewById(R.id.rl_show_date);
        mShowDateLayout.setOnClickListener(this);
        mShowTimeLayout = view.findViewById(R.id.rl_show_time);
        mShowTimeLayout.setOnClickListener(this);

        mShowZoneLayout = view.findViewById(R.id.rl_show_zone);
        mShowZoneLayout.setOnClickListener(this);

        mTvShowZone = view.findViewById(R.id.tv_show_zone);
        mTvShowDate = view.findViewById(R.id.tv_show_date);
        mTvShowTime = view.findViewById(R.id.tv_show_time);
        mTvShowZone.setShine(true);
        mTvShowDate.setShine(true);
        mTvShowTime.setShine(true);

        mDateTimeLine = view.findViewById(R.id.iv_date_time_line);
        mZoneLine = view.findViewById(R.id.iv_zone_line);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                485,
                350,
                Gravity.CENTER);
        FrameLayout.LayoutParams newParams = new FrameLayout.LayoutParams(
                650,
                475,
                Gravity.CENTER);
        mPvDate = new TimePickerBuilder(getActivity(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                mTvShowDate.setText(dateFormat.format(date));
                setDateAndTime();
            }
        }).setLayoutRes(R.layout.pickerview_custom_date, new CustomListener() {
            @Override
            public void customLayout(View v) {
                KLog.d("ljb", "customLayout");
                final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                final TextView tvCancel = (TextView) v.findViewById(R.id.tv_cancel);
                tvSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPvDate.returnData();
                        mPvDate.dismiss();
                    }
                });
                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPvDate.dismiss();
                    }
                });
            }
        }).setBgColor(android.R.color.transparent)
                .setTextColorCenter(XmSkinManager.getInstance().getColor(R.color.sould_field_text_selected_color))
                .setTextColorOut(XmSkinManager.getInstance().getColor(R.color.timepicker_textcolor_out))
                .setContentTextSize(32)
                .setItemHeight(76)
                .isCenterLabel(true)
                .setStartYear(2011)
                .setEndYear(2099)
                .setType(new boolean[]{true, true, true, false, false, false}).isDialog(true).build();
        Dialog mDialog = mPvDate.getDialog();
        if (mDialog != null) {
            mPvDate.getDialogContainerLayout().setLayoutParams(newParams);
        }

        mPvTime = new TimePickerBuilder(getActivity(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                mTrueTimeText = timeFormat.format(date);
                mTvShowTime.setText(getHalfTimeString(date));
                Log.d("ljb", mTrueTimeText);
                setDateAndTime();
            }
        }).setLayoutRes(R.layout.pickerview_custom_time, new CustomListener() {
            @Override
            public void customLayout(View v) {
                KLog.d("ljb", "customLayout");
                final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                final TextView tvCancel = (TextView) v.findViewById(R.id.tv_cancel);
                tvSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPvTime.returnData();
                        mPvTime.dismiss();
                    }
                });
                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPvTime.dismiss();
                    }
                });
            }
        }).setBgColor(android.R.color.transparent)
                .setTextColorCenter(XmSkinManager.getInstance().getColor(R.color.sould_field_text_selected_color))
                .setTextColorOut(XmSkinManager.getInstance().getColor(R.color.timepicker_textcolor_out))
                .setContentTextSize(26)
                .isCenterLabel(true)
                .setType(new boolean[]{false, false, false, true, true, true}).isDialog(true).is24Hour(false).build();
        Dialog mDialogDate = mPvTime.getDialog();
        if (mDialogDate != null) {
            mPvTime.getDialogContainerLayout().setLayoutParams(params);
        }

        mPvFullTime = new TimePickerBuilder(getActivity(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                KLog.d("lai", "onTimeSelect:" + timeFormat.format(date));
                mTrueTimeText = timeFormat.format(date);
                mTvShowTime.setText(timeFormat.format(date));
                setDateAndTime();
            }
        }).setLayoutRes(R.layout.pickerview_custom_time, new CustomListener() {
            @Override
            public void customLayout(View v) {
                KLog.d("ljb", "customLayout");
                final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                final TextView tvCancel = (TextView) v.findViewById(R.id.tv_cancel);
                tvSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPvFullTime.returnData();
                        mPvFullTime.dismiss();
                    }
                });
                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPvFullTime.dismiss();
                    }
                });
            }
        }).setBgColor(android.R.color.transparent)
                .setTextColorCenter(XmSkinManager.getInstance().getColor(R.color.sould_field_text_selected_color))
                .setTextColorOut(XmSkinManager.getInstance().getColor(R.color.timepicker_textcolor_out))
                .setContentTextSize(32)
                .setItemHeight(76)
                .isCenterLabel(true)
                .setType(new boolean[]{false, false, false, true, true, true}).isDialog(true).is24Hour(true).build();
        Dialog mDialogFullTime = mPvFullTime.getDialog();
        if (mDialogFullTime != null) {
            mPvFullTime.getDialogContainerLayout().setLayoutParams(newParams);
        }

        mPvZone = new OptionsPickerBuilder(getActivity(), new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                String tx = mZoneList.get(options1).getPickerViewText();
                mTvShowZone.setText(tx);
                mSelectedZone = options1;
                convertTimeZone(tx);
            }
        }).setLayoutRes(R.layout.pickerview_custom_options, new CustomListener() {
            @Override
            public void customLayout(View v) {
                KLog.d("ljb", "customLayout");
                final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                final TextView tvCancel = (TextView) v.findViewById(R.id.tv_cancel);
                tvSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPvZone.returnData();
                        mPvZone.dismiss();
                    }
                });
                tvCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPvZone.dismiss();
                    }
                });
            }
        }).setBgColor(android.R.color.transparent)
                .setTextColorCenter(XmSkinManager.getInstance().getColor(R.color.sould_field_text_selected_color))
                .setTextColorOut(XmSkinManager.getInstance().getColor(R.color.timepicker_textcolor_out))
                .setContentTextSize(26)
                .isDialog(true).build();
        Dialog mDialogZone = mPvZone.getDialog();
        if (mDialogZone != null) {
            mPvZone.getDialogContainerLayout().setLayoutParams(params);
        }

    }

    private void syncTimeViewVisible(boolean isChecked) {
        if (isChecked) {
            mShowDateLayout.setVisibility(View.GONE);
            mShowTimeLayout.setVisibility(View.GONE);
            mDateTimeLine.setVisibility(View.GONE);
//            setAutoDateTime(1);
        } else {
            mShowDateLayout.setVisibility(View.VISIBLE);
            mShowTimeLayout.setVisibility(View.VISIBLE);
            mDateTimeLine.setVisibility(View.VISIBLE);
//            setAutoDateTime(0);
        }
    }

    public void initData() {
        KLog.d("ljb", "initTime");
        //初始化语言
        if (getCurrentLanguage() == SDKConstants.LANGUAGE_EN) {
            mSivLanguageType.setCheck(1);
        } else {
            mSivLanguageType.setCheck(0);
        }
        mSivLanguageType.setListener(this);

        //初始化是否自动设置时间
        //boolean isDateTimeAuto = isDateTimeAuto();
        boolean isDateTimeAuto = XmCarVendorExtensionManager.getInstance().getSynchronizeTimeSwitch();
//        mSwitchSyncTime.setChecked(isDateTimeAuto);
        mSwitchSyncTime.check(isDateTimeAuto);

        syncTimeViewVisible(isDateTimeAuto);

        //初始化是否自动时区
//        mSwitchSyncZone.setChecked(isTimeZoneAuto());

        //初始化时区
        mTvShowZone.setText(getDefaultTimeZone());

//        //初始化时间格式
//        if (is24Hour()) {
//            mSivTimeType.setCheck(0);
//        } else {
//            mSivTimeType.setCheck(1);
//        }

        //初始化日期和时间
        initCurrentTime();

        //初始化时区列表
        getZoneData();
        mPvZone.setPicker(mZoneList);
    }

    private void initDateFormat() {
        KLog.d("lai", "initDateFormat");
        dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        timeFormat = new SimpleDateFormat("HH时mm分ss秒");
        halfTimeFormat = new SimpleDateFormat("hh时mm分ss秒");
        simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
        halfSimpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 hh时mm分ss秒");
    }

    private void initCurrentTime() {
        KLog.d("ljb", "initCurrentTime");

        initDateFormat();
//        Date date = new Date(getCurrentTime());
        getCurrentTime();
        String dataString = formatData2DataString(showTime);
        String timeString = formatData2TimeString(showTime);
        if (!TextUtils.isEmpty(dataString)) {
            mTvShowDate.setText(dataString);
        }
        if (!TextUtils.isEmpty(timeString)) {
            mTvShowTime.setText(timeString);
        }
//        if (is24Hour()) {
//        } else {
//            mTvShowTime.setText(timeString);
//        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", mLanguageType);
    }

    @Override
    public void onResume() {
        super.onResume();
        getCurrentTime();
    }

    @Override
    @NormalOnClick({EventConstants.NormalClick.showDate, EventConstants.NormalClick.showTime, EventConstants.NormalClick.showZone})
    @ResId({R.id.rl_show_date, R.id.rl_show_time, R.id.rl_show_zone})
    public void onClick(View v) {
        getCurrentTime();
        switch (v.getId()) {
            case R.id.rl_show_date:
                Calendar date = formatDate2Date(showTime);
                if (date != null) {
                    mPvDate.setDate(date);
                    mPvDate.show();
                }
                break;
            case R.id.rl_show_time:
                Date time = formatData2Time(showTime);
                if (mTimeType == 0) {  //24小时的时候
                    Calendar calendarTime = Calendar.getInstance();
                    if (time != null) {
                        calendarTime.setTime(time);
                        mPvFullTime.setDate(calendarTime);
                        mPvFullTime.show();
                    }
                } else {    //12小时的时候
                    Calendar calendarTime = Calendar.getInstance();
                    /*
                    int hourOfDay = calendarTime.get(Calendar.HOUR_OF_DAY);
                    if (hourOfDay > 12) {
                        hourOfDay -= 12;
                        calendarTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    }*/
                    if (time != null) {
                        calendarTime.setTime(time);
                        mPvTime.setDate(calendarTime);
                        mPvTime.show();
                    }
                }
                break;
            case R.id.rl_show_zone:
                mPvZone.setSelectOptions(mSelectedZone);
                mPvZone.show();
                break;
        }
    }

    @Override
    public void onSelect(int viewId, int index) {
//        if (viewId == R.id.siv_time_type) {
//            mTimeType = index;
//            changeTimeFormat(mTimeType);
//        } else
        if (viewId == R.id.siv_language_type) {
            mLanguageType = index;
            if (mLanguageType == 0 && getCurrentLanguage() == SDKConstants.LANGUAGE_EN) {
                setLanguage(SDKConstants.LANGUAGE_CH);
            } else if (mLanguageType == 1 && getCurrentLanguage() == SDKConstants.LANGUAGE_CH) {
                setLanguage(SDKConstants.LANGUAGE_EN);
            }
        }
    }

    public int getCurrentLanguage() {
        return XmCarFactory.getCarVendorExtensionManager().getLanguage();
    }

    public synchronized void setLanguage(int languageType) {
        XmCarFactory.getCarVendorExtensionManager().setLanguage(languageType);
    }

    public String formatData2TimeString(Integer[] dateArray) {
        String time = "";
        if (dateArray != null && dateArray.length >= 6) {
            time = dateArray[0] + "时" + dateArray[1] + "分" + dateArray[2] + "秒";
        }
        return time;
    }

    public Date formatData2Time(Integer[] dateArray) {
        if (dateArray != null && dateArray.length >= 6) {
            Date date = new Date();
            date.setHours(dateArray[0]);
            date.setMinutes(dateArray[1]);
            date.setSeconds(dateArray[2]);
            return date;
        }
        return null;
    }

    public Calendar formatDate2Date(Integer[] dateArray) {
        if (dateArray != null && dateArray.length >= 6) {
            Calendar instance = Calendar.getInstance();
            instance.set(Calendar.YEAR, dateArray[3] + 2011);
            instance.set(Calendar.MONTH, dateArray[4]);
            instance.set(Calendar.DAY_OF_MONTH, dateArray[5] + 1);
            return instance;
        }
        return null;
    }

    public String formatData2DataString(Integer[] dateArray) {
        String data = "";
        if (dateArray != null && dateArray.length >= 6) {
            int year = dateArray[3] + 2011;
            int month = dateArray[4] + 1;
            int day = dateArray[5] + 1;
            data = year + "年" + month + "月" + day + "日";
        }
        return data;
    }

    //设置日期和时间
    public void setDateAndTime() {
        if (TextUtils.isEmpty(mTvShowDate.getText().toString()) || TextUtils.isEmpty(mTvShowTime.getText().toString()))
            return;
        String str = mTvShowDate.getText().toString() + " " + mTvShowTime.getText().toString();
        try {
            long time = simpleDateFormat.parse(str).getTime();

            /*
            if (!is24Hour()) {
                time = halfSimpleDateFormat.parse(str).getTime();
            }*/
            //SystemClock.setCurrentTimeMillis(time);
//            ((AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE)).setTime(time);
//            getContext().sendBroadcast(new Intent(Intent.ACTION_TIME_CHANGED));
            KLog.d(CAR_CONTROL_REQ, "设置时间和日期: " + str);
            XmCarVendorExtensionManager.getInstance().setTime(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //设置时间格式
    public void changeTimeFormat(int type) {
        try {
            if (type == 0) { //设置24小时
                android.provider.Settings.System.putString(mContext.getContentResolver(),
                        android.provider.Settings.System.TIME_12_24, "24");
                Intent timeChanged = new Intent(Intent.ACTION_TIME_CHANGED);
                mContext.sendBroadcast(timeChanged);
            } else {  //设置12小时
                android.provider.Settings.System.putString(mContext.getContentResolver(),
                        android.provider.Settings.System.TIME_12_24, "12");
                Intent timeChanged = new Intent(Intent.ACTION_TIME_CHANGED);
                mContext.sendBroadcast(timeChanged);
            }
            initCurrentTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //判断系统使用的是24小时制还是12小时制
    public boolean is24Hour() {
        return DateFormat.is24HourFormat(mContext);
    }

    //判断系统的时区是否是自动获取的
    public boolean isTimeZoneAuto() {
        try {
            return android.provider.Settings.Global.getInt(mContext.getContentResolver(),
                    android.provider.Settings.Global.AUTO_TIME_ZONE) > 0;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    //设置系统的时区是否自动获取
    public void setAutoTimeZone(int checked) {
        /*try {
            android.provider.Settings.Global.putInt(mContext.getContentResolver(),
                    android.provider.Settings.Global.AUTO_TIME_ZONE, checked);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        KLog.d(TAG, "目前设置时区接口不可用");
    }

    //判断系统的时间是否自动获取的
    public boolean isDateTimeAuto() {
        try {
            return android.provider.Settings.Global.getInt(mContext.getContentResolver(),
                    android.provider.Settings.Global.AUTO_TIME) > 0;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    //设置系统的时间是否需要自动获取
    public void setAutoDateTime(int checked) {
        /*
        try {
            android.provider.Settings.Global.putInt(mContext.getContentResolver(),
                    android.provider.Settings.Global.AUTO_TIME, checked);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        XmCarVendorExtensionManager.getInstance().setSynchronizeTimeSwitch(checked == 1 ? true : false);
    }

    //设置系统时区
    public void setTimeZone(String id) {
        try {
            AlarmManager mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            mAlarmManager.setTimeZone(id);
            getContext().sendBroadcast(new Intent(Intent.ACTION_TIME_CHANGED));
            initCurrentTime();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable t) {

        }
    }

    //从xml文件获取信息
    public void getZoneData() {
        try {
            mZoneMap.clear();
            mZoneList.clear();
            //获取信息的方法
            Resources res = getResources();
            XmlResourceParser xrp = res.getXml(R.xml.timezones);
            //判断是否已经到了文件的末尾
            while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
                if (xrp.getEventType() == XmlResourceParser.START_TAG) {
                    String name = xrp.getName();
                    if (name.equals("timezone")) {
                        //0，标识id，1标识名称
                        mZoneMap.put(xrp.getAttributeValue(1),
                                xrp.getAttributeValue(0));
                        mZoneList.add(new ZoneData(xrp.getAttributeValue(1)));
                    }
                }
                xrp.next();
            }//while
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//getZoneData

    private void convertTimeZone(String name) {
        String id = mZoneMap.get(name);
        TimeZone tz = TimeZone.getTimeZone(id);
        KLog.d("ljb", "convertTimeZone" + tz.getDisplayName());
        setTimeZone(id);
    }

    @Override
    public void onDestroy() {
        XmCarVendorExtensionManager.getInstance().removeValueChangeListener(this);
        super.onDestroy();
        mContext.unregisterReceiver(mBroadcastReceiver);
    }

    private String getHalfTimeString(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        String str;
        String pre = "上午";
        if (hour > 12) {
            pre = "下午";
        } else if (hour == 0) {
            pre = "凌晨";
        }
        str = pre + halfTimeFormat.format(date);
        if (hour == 0) {
            str = str.replace("12", "00");
        }
        return str;
    }

    @Override
    public void onChange(int id, Object value) {
        if (id == SDKConstants.ID_SYNC_TIME_ON) {
            boolean isSync = (boolean) value;
//            mSwitchSyncTime.setChecked(isSync);
            KLog.d("car_control", "同步网络时间开关: " + isSync);
            mSwitchSyncTime.check(isSync);
            if (isSync) {
                mShowDateLayout.setVisibility(View.GONE);
                mShowTimeLayout.setVisibility(View.GONE);
                mDateTimeLine.setVisibility(View.GONE);
            } else {
                mShowDateLayout.setVisibility(View.VISIBLE);
                mShowTimeLayout.setVisibility(View.VISIBLE);
                mDateTimeLine.setVisibility(View.VISIBLE);
                initCurrentTime();
            }
        }
    }
}
