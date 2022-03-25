package com.xiaoma.login.business.ui.infoview;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.xiaoma.login.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author Gillben
 * date: 2018/12/03
 * <p>
 * 修改年龄
 */
public class PersonalAgeDialog extends BasePersonalInfoDialog implements View.OnClickListener {


    private static final String TAG = "PersonalAgeDialog";
    public static final int WIDTH = 650;
    public static final int HEIGHT = 475;

    private WheelView yearWheelView;
    private WheelView monthWheelView;
    private WheelView dayWheelView;

    private static final int MAX_DAY = 31;
    private static final int MAX_MONTH = 12;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private Calendar startCalendar, endCalendar;
    private int startYear, startMonth, startDay, endYear, endMonth, endDay;

    private List<String> years = new ArrayList<>();
    private List<String> months = new ArrayList<>();
    private List<String> days = new ArrayList<>();

    private boolean isLeapYear;  //是否闰年
    private int tempDayIndex;
    private int tempMonthIndex;
    private String curYear;
    private String curMonth = String.valueOf(Calendar.getInstance(Locale.CHINA).get(Calendar.MONTH) + 1);
    private String curDay = String.valueOf(Calendar.getInstance(Locale.CHINA).get(Calendar.DAY_OF_MONTH));
    private String initDate = sdf.format(new Date());

    private String maxDate;


    public void setInitDate(String maxDate, String initDate) {
        this.maxDate = maxDate;
        if (!TextUtils.isEmpty(initDate)) {
            this.initDate = initDate;
        }
    }

    @Override
    protected int contentLayoutId() {
        return R.layout.dialog_personal_age;
    }

    @Override
    protected String getTitle() {
        return getString(R.string.personal_age);
    }

    @Override
    protected void onBindView(View view) {
        yearWheelView = view.findViewById(R.id.year_wheel_view);
        monthWheelView = view.findViewById(R.id.month_wheel_view);
        dayWheelView = view.findViewById(R.id.day_wheel_view);

        initYearMonthDayList();
        initWheelView();
        initCurrentDate();
    }

    @Override
    protected boolean isCancelableOutside() {
        return false;
    }

    @Override
    protected void onSure() {
        modifyDate();
    }

    @Override
    protected WindowManager.LayoutParams changeWindowParams(WindowManager.LayoutParams lp) {
        lp.width = WIDTH;
        lp.height = HEIGHT;
        return lp;
    }

    /**
     * 初始化日期
     */
    private void initYearMonthDayList() {

        try {
            startCalendar = Calendar.getInstance(Locale.CHINA);
            endCalendar = Calendar.getInstance(Locale.CHINA);

            //防止空指针
            if (maxDate == null) {
                maxDate = sdf.format(new Date());
            }

            //结束时间
            endCalendar.setTime(sdf.parse(maxDate));
            endYear = endCalendar.get(Calendar.YEAR);
            endMonth = endCalendar.get(Calendar.MONTH) + 1;
            endDay = endCalendar.get(Calendar.DAY_OF_MONTH);

            //以100为区间计算最大年龄
            int interval = endYear - 100;
            String startText = String.format(Locale.CHINA, "%d-01-01", interval);
            startCalendar.setTime(sdf.parse(startText));

            startYear = startCalendar.get(Calendar.YEAR);
            startMonth = startCalendar.get(Calendar.MONTH) + 1;
            startDay = startCalendar.get(Calendar.DAY_OF_MONTH);

            //开始添加
            years.clear();
            for (int i = startYear; i <= endYear; i++) {
                years.add(String.valueOf(i));
            }
            months.clear();
            for (int i = startMonth; i <= MAX_MONTH; i++) {
                months.add(String.valueOf(i));
            }
            days.clear();
            for (int i = startDay; i <= MAX_DAY; i++) {
                days.add(String.valueOf(i));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private void initWheelView() {
        //年
        yearWheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                if (TextUtils.isEmpty(item)) {
                    int curYear = Calendar.getInstance(Locale.CHINA).get(Calendar.YEAR);
                    item = String.valueOf(curYear);
                }
                curYear = item;
                int temp = Integer.parseInt(item);
                //判断是否闰年
                isLeapYear = temp % 4 == 0;
                checkConcreteYearToUpdateDays(isLeapYear);
                checkModified();
            }
        });

        //月
        monthWheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                if (TextUtils.isEmpty(item)) {
                    int curMonth = Calendar.getInstance(Locale.CHINA).get(Calendar.MONTH) + 1;
                    item = String.valueOf(curMonth);
                }
                curMonth = item;
                tempMonthIndex = selectedIndex;
                checkConcreteMonthToUpdateDays(Integer.parseInt(item));
                checkModified();
            }
        });

        //天
        dayWheelView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                if (TextUtils.isEmpty(item)) {
                    item = String.valueOf(Calendar.getInstance(Locale.CHINA).get(Calendar.DAY_OF_MONTH));
                }
                curDay = item;
                tempDayIndex = selectedIndex;
                checkModified();
            }
        });


        yearWheelView.setItems(years);
        monthWheelView.setItems(months);
        dayWheelView.setItems(days);
    }

    private void initCurrentDate() {
        Calendar birthDay;
        try {
            birthDay = Calendar.getInstance(Locale.CHINA);
            birthDay.setTime(sdf.parse(initDate));
        } catch (Exception e) {
            e.printStackTrace();
            birthDay = Calendar.getInstance(Locale.CHINA);
        }

        int year = birthDay.get(Calendar.YEAR);
        int month = birthDay.get(Calendar.MONTH) + 1;
        int day = birthDay.get(Calendar.DAY_OF_MONTH);

        curYear = String.valueOf(year);
        curMonth = String.valueOf(month);
        curDay = String.valueOf(day);
        isLeapYear = year % 4 == 0;

        yearWheelView.setSelection(years.indexOf(String.valueOf(year)) + 2);
        monthWheelView.setSelection(months.indexOf(String.valueOf(month)) + 2);
        dayWheelView.setSelection(days.indexOf(String.valueOf(day)) + 2);

        //第一次tempDayIndex,tempMonthIndex 为零，需获取选择后的位置
        tempDayIndex = dayWheelView.getSelectedIndex();
        tempMonthIndex = monthWheelView.getSelectedIndex();
    }

    /**
     * 不同的月份 总天数也不同
     *
     * @param wheelView 天数 view
     * @param list      具体天数的集合
     */
    private void updateWheelViewData(WheelView wheelView, List<String> list) {
        wheelView.setItems(list);
        if (tempDayIndex >= (list.size() + 1)) {
            tempDayIndex = list.size() + 1;
        }
        wheelView.setSelectionAndRefresh(tempDayIndex);
    }

    /**
     * 不同的年份 总月数也不同(如果是今年的话不能大于今天)
     *
     * @param wheelView 月数数 view
     * @param list      具体月数的集合
     */
    private void updateMonthWheelViewData(WheelView wheelView, List<String> list) {
        wheelView.setItems(list);
        if (tempMonthIndex > list.size()) {
            tempMonthIndex = list.size() + 1;
        }
        wheelView.setSelectionAndRefresh(tempMonthIndex);
    }


    /**
     * 监听年份改变每月天数
     *
     * @param leap 年份
     */
    private void checkConcreteYearToUpdateDays(boolean leap) {
        int currentMonth = Integer.parseInt(curMonth);
        int currentYear = Integer.parseInt(curYear);
        int currentDay = Integer.parseInt(curDay);

        int realYear = endCalendar.get(Calendar.YEAR);
        int realMonth = endCalendar.get(Calendar.MONTH) + 1;
        int realDay = endCalendar.get(Calendar.DAY_OF_MONTH);

        if (currentYear == realYear) {
            updateMonthWheelViewData(monthWheelView, months.subList(0, realMonth));
        } else {
            updateMonthWheelViewData(monthWheelView, months);
        }

        if (currentMonth == 2) {
            if (leap) {
                updateWheelViewData(dayWheelView, days.subList(0, days.size() - 2));
            } else {
                updateWheelViewData(dayWheelView, days.subList(0, days.size() - 3));
            }
        }//else   Nothing to do
    }


    /**
     * 监听月份改变每月天数
     *
     * @param month 月份
     */
    private void checkConcreteMonthToUpdateDays(int month) {
        if (month < 1 || month > 12) {
            return;
        }

        int realYear = endCalendar.get(Calendar.YEAR);
        int realMonth = endCalendar.get(Calendar.MONTH) + 1;
        int realDay = endCalendar.get(Calendar.DAY_OF_MONTH);

        if (month == realMonth
                && Integer.parseInt(curYear) == realYear) {
            updateWheelViewData(dayWheelView, days.subList(0, realDay));
        } else {
            if (month == 1 || month == 3 | month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
                updateWheelViewData(dayWheelView, days);
            } else if (month == 4 || month == 6 || month == 9 || month == 11) {
                updateWheelViewData(dayWheelView, days.subList(0, days.size() - 1));
            } else {
                if (isLeapYear) {
                    updateWheelViewData(dayWheelView, days.subList(0, days.size() - 2));
                } else {
                    updateWheelViewData(dayWheelView, days.subList(0, days.size() - 3));
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void modifyDate() {
        //获取当前时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Calendar nowCalendar = Calendar.getInstance();
        try {
            nowCalendar.setTime(sdf.parse(sdf.format(new Date())));
        } catch (ParseException e) {
            Log.e(TAG, "Parse date error");
            return;
        }
        final long birthday = convertDate();

        onSuccessResult(String.valueOf(birthday));
    }


    private long convertDate() {
        String temp = curYear + "-" + fillZero(Integer.parseInt(curMonth)) + "-" + fillZero(Integer.parseInt(curDay));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Date curDate;
        try {
            curDate = simpleDateFormat.parse(temp);
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
        return curDate.getTime();
    }

    private void checkModified() {
        String[] split = initDate.split("-");
        if (curYear.equals(split[0])
                && curMonth.equals(split[1])
                && curDay.equals(split[2])) {
            getSureButton().setEnabled(false);
        } else {
            getSureButton().setEnabled(true);
        }
    }

    @SuppressLint("DefaultLocale")
    private String fillZero(int number) {
        return String.format("%02d", number);
    }
}
