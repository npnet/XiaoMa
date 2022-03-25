package com.xiaoma.launcher.schedule.view;

import android.content.Context;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xiaoma.launcher.R;
import com.xiaoma.launcher.schedule.adapter.CalenderAdapter;
import com.xiaoma.launcher.schedule.model.CalenderItem;
import com.xiaoma.launcher.schedule.utils.DateUtil;
import com.xiaoma.launcher.schedule.utils.LunarUtil;
import com.xiaoma.launcher.schedule.utils.SolarUtil;
import com.xiaoma.ui.adapter.XMBaseAbstractRyAdapter;
import com.xiaoma.ui.view.XmDividerDecoration;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.List;

public class ScheduleCalenderView extends FrameLayout {

    private static final String[] MONTH = {"01月", "02月", "03月", "04月", "05月", "06月", "07月", "08月", "09月", "10月", "11月", "12月"};

    public static final int CALENDER_COLUMNS = 7;

    public static final int CALENDER_TOTAL_DAYS = 6 * 7;

    private NumberPickerView mMonthPicker;

    private RecyclerView mRecyclerView;

    private List<CalenderItem> mCalenderItems;

    private CalenderAdapter mCalenderAdapter;

    private int mCurrentYear;

    private int mCurrentMonth;

    private int mCurrentDay;

    private Context mContext;

    private OnDateChangedListener mDateChangedListener;
    private String[] temp;
    private String solarHoliday;
    private TextView mTvYear;

    public ScheduleCalenderView(Context context) {
        this(context, null);
    }

    public ScheduleCalenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(getContext()).inflate(R.layout.view_calender, this);
        initView();
        initData(context);
    }

    private void initView() {
        mMonthPicker = findViewById(R.id.calender_month_picker);
        mTvYear = findViewById(R.id.tv_year);
        mRecyclerView = findViewById(R.id.calender_recycler_view);
    }

    private void initData(Context context) {
        mContext = context;
        mCurrentDay = DateUtil.getCurrentDay();
        mCurrentMonth = DateUtil.getCurrentMonth();
        mCurrentYear = DateUtil.getCurrentYear();
        initMouthPicker();
        initRecyclerView(context);
        mTvYear.setText(String.format(context.getString(R.string.str_year), mCurrentYear));
    }

    public String getYear() {
        return mCurrentYear + "年";
    }


    private void initMouthPicker() {
        mMonthPicker.setDisplayedValues(MONTH);
        mMonthPicker.setMinValue(0);
        mMonthPicker.setMaxValue(MONTH.length - 1);
        //设置当前时间
        mMonthPicker.setValue(mCurrentMonth - 1);

      /*  //禁止输入
        mMonthPicker.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
        //设置颜色
        mMonthPicker.setNumberPickerDividerColor(mMonthPicker);*/

        mMonthPicker.setOnValueChangeListenerInScrolling(mOnDateChangedListener);
        mMonthPicker.setOnValueChangedListener(new NumberPickerView.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPickerView picker, int oldVal, int newVal, int clickDay) {
                if (mDateChangedListener != null) {
                    mDateChangedListener.onChanged(mCurrentYear, mCurrentMonth, clickDay);
                }
                updateView();
            }
        });

    }

    private void initRecyclerView(Context context) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), CALENDER_COLUMNS);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        XmDividerDecoration decor = new XmDividerDecoration(context, DividerItemDecoration.HORIZONTAL);
        decor.setRect(50, 0, 0, 5);
        mRecyclerView.addItemDecoration(decor);
        getCalenderItem(context, DateUtil.getCurrentYear(), DateUtil.getCurrentMonth(), -1);
        mCalenderAdapter = new CalenderAdapter(context, mCalenderItems, R.layout.item_calender);
        mCalenderAdapter.setOnItemClickListener(new XMBaseAbstractRyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.Adapter adapter, View view, RecyclerView.ViewHolder holder, int position) {
                CalenderItem calenderItem = mCalenderItems.get(position);
                if (calenderItem.isLastMouthDay()) {
                    KLog.e("isLastMouthDay");
                    mMonthPicker.smoothScrollToValue(mCurrentMonth - 1, mCurrentMonth - 2, true, calenderItem.getDay());
                } else if (calenderItem.isNextMouthDay()) {
                    KLog.e("isNextMouthDay");
                    mMonthPicker.smoothScrollToValue(mCurrentMonth - 1, mCurrentMonth, true, calenderItem.getDay());
                } else {
                    mCalenderItems.get(mCalenderAdapter.getCurSelPos()).setSelected(false);
                    calenderItem.setSelected(true);
                    mCalenderAdapter.notifyDataSetChanged();
                    if (mDateChangedListener != null) {
                        mDateChangedListener.onItemClick(calenderItem.getDate(), calenderItem.getLunar());
                    }
                }
            }
        });

        mRecyclerView.setAdapter(mCalenderAdapter);
    }

    public List<CalenderItem> getCalenderItem(Context context, int year, int month, int clickDay) {
        KLog.d("getCalenderItem month" + month);

        newCalenderItem();

        addCalenderItem(year, month, clickDay);

        KLog.d("getCalenderItem mCalenderItems" + mCalenderItems);
        return mCalenderItems;
    }

    private void addCalenderItem(int year, int month, int clickDay) {
        //获取当月第一天的星期
        int week = DateUtil.formatWeek(year, month, 1);
        int daysByYearMonth = DateUtil.getDaysByYearMonth(year, month);
        int lastDaysByYearMonth = getLastDaysByYearMonth(year, month);
        for (int i = 1; i <= CALENDER_TOTAL_DAYS; i++) {
            if (week == 1) {
                addCurrentMonthItem(daysByYearMonth, i, clickDay);
            } else {
                //如果不是礼拜日，第一行的日历列表就需要补
                //获取上月的总天数
                if (week > i) {
                    if (year > 2049 || year < 1900) {
                        temp = new String[3];
                    } else {
                        if (month == 1) {
                            temp = LunarUtil.solarToLunar(year - 1, 12, lastDaysByYearMonth - (week - 1 - i));
                        } else {
                            temp = LunarUtil.solarToLunar(year, month - 1, lastDaysByYearMonth - (week - 1 - i));
                        }
                        if (year == 1900 && month == 1 && lastDaysByYearMonth - (week - 1 - i) == 31) {
                            temp = new String[3];
                        }
                        if (year == 1900 && month == 2 && lastDaysByYearMonth - (week - 1 - i) < 31) {
                            temp = new String[3];
                        }
                    }
                    if (month == 1) {
                        solarHoliday = SolarUtil.getSolarHoliday(year - 1, 12, lastDaysByYearMonth - (week - 1 - i));
                    } else {
                        solarHoliday = SolarUtil.getSolarHoliday(year - 1, 12, lastDaysByYearMonth - (week - 1 - i));
                    }
                    mCalenderItems.add(new CalenderItem(null, lastDaysByYearMonth - (week - 1 - i), new String[]{temp[0], temp[1]}, temp[2],
                            solarHoliday, false, false, false, true, false));
                } else {
                    addCurrentMonthItem(daysByYearMonth, i - week + 1, clickDay);
                }
            }
        }
    }

    private int getLastDaysByYearMonth(int year, int month) {
        int lastDaysByYearMonth;
        if (month == 1) {
            lastDaysByYearMonth = DateUtil.getDaysByYearMonth(year - 1, 12);
        } else {
            lastDaysByYearMonth = DateUtil.getDaysByYearMonth(year, month - 1);
        }
        return lastDaysByYearMonth;
    }

    private void addCurrentMonthItem(int daysByYearMonth, int i, int clickDay) {
        if (clickDay > 0) {
            if (clickDay == i) {
                //2018-05-25
                String date = DateUtil.formatDate(mCurrentYear, mCurrentMonth, i);
                if (mCurrentYear > 2049 || mCurrentYear < 1900) {
                    temp = new String[3];
                } else {
                    temp = LunarUtil.solarToLunar(mCurrentYear, mCurrentMonth, i);
                }
                solarHoliday = SolarUtil.getSolarHoliday(mCurrentYear, mCurrentMonth, i);
                mCalenderItems.add(new CalenderItem(date, i, new String[]{temp[0], temp[1]}, temp[2],
                        solarHoliday, true, false, true, false, false));
                return;
            }
        } else {
            if (i == mCurrentDay && mCurrentMonth == DateUtil.getCurrentMonth() && mCurrentYear == DateUtil.getCurrentYear()) {
                //2018-05-25
                String date = DateUtil.formatDate(mCurrentYear, mCurrentMonth, i);
                if (mCurrentYear > 2049 || mCurrentYear < 1900) {
                    temp = new String[3];
                } else {
                    temp = LunarUtil.solarToLunar(mCurrentYear, mCurrentMonth, i);
                }
                solarHoliday = SolarUtil.getSolarHoliday(mCurrentYear, mCurrentMonth, i);
                mCalenderItems.add(new CalenderItem(date, i, new String[]{temp[0], temp[1]}, temp[2],
                        solarHoliday, true, false, true, false, false));
                return;
            }
        }

        if (i > daysByYearMonth) {
            if (mCurrentYear > 2049 || mCurrentYear < 1900) {
                temp = new String[3];
            } else {
                temp = LunarUtil.solarToLunar(mCurrentYear, mCurrentMonth + 1, i - daysByYearMonth);
            }
            solarHoliday = SolarUtil.getSolarHoliday(mCurrentYear, mCurrentMonth + 1, i - daysByYearMonth);
            mCalenderItems.add(new CalenderItem(null, i - daysByYearMonth, new String[]{temp[0], temp[1]}, temp[2],
                    solarHoliday, false, false, false, false, true));
            return;
        }
        String date = DateUtil.formatDate(mCurrentYear, mCurrentMonth, i);
        if (mCurrentYear > 2049 || mCurrentYear < 1900) {
            temp = new String[3];
        } else {
            if (mCurrentYear == 1900 && mCurrentMonth == 1 && i < 31) {
                temp = new String[3];
            } else {
                temp = LunarUtil.solarToLunar(mCurrentYear, mCurrentMonth, i);
            }
        }
        solarHoliday = SolarUtil.getSolarHoliday(mCurrentYear, mCurrentMonth, i);
        mCalenderItems.add(new CalenderItem(date, i, new String[]{temp[0], temp[1]}, temp[2],
                solarHoliday, false, false, true, false, false));
    }


    private void newCalenderItem() {
        if (mCalenderItems == null) {
            mCalenderItems = new ArrayList<>();
        } else {
            mCalenderItems.clear();
        }
    }


    /**
     * Month Picker控件监听器
     */
    private NumberPickerView.OnValueChangeListenerInScrolling mOnDateChangedListener = new NumberPickerView.OnValueChangeListenerInScrolling() {
        @Override
        public void onValueChangeInScrolling(NumberPickerView picker, int oldVal, int newVal, int inScrollingPickedOldValue, int inScrollingPickedNewValue) {
            updateDate(oldVal, newVal, inScrollingPickedOldValue, inScrollingPickedNewValue);
            KLog.d("oldVal:" + oldVal + "---newVal：" + newVal);
            KLog.d("inScrollingPickedOldValue:" + inScrollingPickedOldValue + "---inScrollingPickedNewValue：" + inScrollingPickedNewValue);
            mTvYear.setText(String.format(mContext.getString(R.string.str_year), mCurrentYear));
        }
    };

    private void updateDate(int oldVal, int newVal, int inScrollingPickedOldValue,
                            int inScrollingPickedNewValue) {

        if (oldVal == 11 && newVal == 0) {
            //从12月到1月
            mCurrentYear++;
            mCurrentMonth = newVal + 1;
            return;
        }

        if (oldVal == 0 && newVal == 11) {
            //从1月到12月
            mCurrentYear--;
            mCurrentMonth = newVal + 1;
            return;
        }

        //数值，每次滑动的个数
        int value = inScrollingPickedNewValue - inScrollingPickedOldValue;
        if (value == 0) {
            //滑动一位
        } else if (value < 0) {
            //向上滑动
            //滑动了多少轮
            int count = Math.abs(value) / 12;
            if (count < 0) {
                //一轮都没滑动到
                if (oldVal - Math.abs(value) < 0) {
                    mCurrentYear--;
                }
            } else {
                //滑动了一轮以上
                mCurrentYear = mCurrentYear - count;
                int temp = Math.abs(value) % 12;
                if (oldVal - temp < 0) {
                    mCurrentYear--;
                }
            }
        } else {
            //向下滑动
            int count = value / 12;
            if (count < 0) {
                //一轮都没滑动到
                if (oldVal + count > 11) {
                    mCurrentYear++;
                }
            } else {
                //滑动了一轮以上
                mCurrentYear = mCurrentYear + count;
                int temp = value % 12;
                if (oldVal + temp > 11) {
                    mCurrentYear++;
                }
            }
        }
        mCurrentMonth = newVal + 1;
    }

    public void updateView() {
        mCalenderAdapter.notifyDataSetChanged();
    }

    public void isChecked() {
        if (mDateChangedListener != null) {
            mDateChangedListener.onChanged(mCurrentYear, mCurrentMonth, -1);
        }
    }

    public interface OnDateChangedListener {
        void onChanged(int year, int month, int clickDay);

        void onItemClick(String date, String[] lunar);
    }

    public void registerDateChangedListener(OnDateChangedListener onDateChangedListener) {
        mDateChangedListener = onDateChangedListener;
    }

    public void unregisterDateChangedListener() {
        mDateChangedListener = null;
    }
}
