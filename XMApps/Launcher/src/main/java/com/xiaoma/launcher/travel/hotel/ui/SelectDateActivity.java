package com.xiaoma.launcher.travel.hotel.ui;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.launcher.travel.hotel.ui
 *  @file_name:      SelectDateActivity
 *  @author:         Rookie
 *  @create_time:    2019/1/4 15:32
 *  @description：   TODO             */

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.launcher.R;
import com.xiaoma.launcher.common.constant.EventConstants;
import com.xiaoma.launcher.travel.hotel.calendar.CalendarAdapter;
import com.xiaoma.launcher.travel.hotel.calendar.CalendarUtil;
import com.xiaoma.launcher.travel.hotel.calendar.DateBean;
import com.xiaoma.launcher.travel.hotel.calendar.DateControler;
import com.xiaoma.launcher.travel.hotel.constants.HotelConstants;
import com.xiaoma.launcher.travel.hotel.vm.RecomHotelVM;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.trip.hotel.response.HotelBean;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.TimeUtils;

import java.util.Calendar;
import java.util.List;
@PageDescComponent(EventConstants.PageDescribe.SelectDateActivityPagePathDesc)
public class SelectDateActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvStartTime;
    private TextView tvEndTime;

    private GridView calendarStart;
    private GridView calendarEnd;
    private TextView btnClear;
    private Button btnSure;
    private List<DateBean> mStartList;
    private List<DateBean> mEndList;

    private DateControler mControler;
    private DateBean mStartDate;
    private DateBean mEndDate;
    private CalendarAdapter mStartAdapter;
    private CalendarAdapter mEndAdapter;
    private final String EXTRA_BUNDLE = "bundle";
    //重新预定
    private String hotelId = "";
    private String checkInDate, checkOutDate;
    private RecomHotelVM recomHotelVM;
    private ImageView mIvPre;
    private ImageView mIvNext;
    private Calendar mCalendar;
    private int mCurrYear;
    private int mCurrMonth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_date);
        initView();
        initData();
    }


    private void initView() {

        tvStartTime = findViewById(R.id.tv_start_time);
        tvEndTime = findViewById(R.id.tv_end_time);
        calendarStart = findViewById(R.id.calendar_start);
        calendarEnd = findViewById(R.id.calendar_end);
        btnClear = findViewById(R.id.btn_clear);
        btnSure = findViewById(R.id.btn_sure);
        mIvPre = findViewById(R.id.iv_pre);
        mIvNext = findViewById(R.id.iv_next);
        btnClear.setOnClickListener(this);
        btnSure.setOnClickListener(this);
        mIvPre.setOnClickListener(this);
        mIvNext.setOnClickListener(this);
        mIvPre.setVisibility(View.GONE);
        tvStartTime.setText(TimeUtils.getCurrYearAndMonthV2("-"));
        tvEndTime.setText(TimeUtils.getCurrYearAndNextMonthV2("-"));

        //左边日历init
        mCalendar = Calendar.getInstance();
        mCurrYear = mCalendar.get(Calendar.YEAR);
        mCurrMonth = mCalendar.get(Calendar.MONTH) + 1;
        //右边下一月
        Calendar calendar2 = (Calendar) mCalendar.clone();
        calendar2.add(Calendar.MONTH, 1);

        mStartList = CalendarUtil.getMonthDate(mCurrYear, mCurrMonth);
        mEndList = CalendarUtil.getMonthDate(calendar2.get(Calendar.YEAR), calendar2.get(Calendar.MONTH) + 1);

        mStartAdapter = new CalendarAdapter(this, mStartList, R.layout.item_month_layout);
        mEndAdapter = new CalendarAdapter(this, mEndList, R.layout.item_month_layout);
        calendarStart.setAdapter(mStartAdapter);
        calendarEnd.setAdapter(mEndAdapter);
        mControler = new DateControler(mStartAdapter, mEndAdapter);

        calendarStart.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DateBean dateBean = mStartList.get(position);
                if (dateBean.getType() == 0 || dateBean.getType() == 2 || dateBean.isExpire()) {
                    return;
                }
                mControler.viewClicked(dateBean);
            }
        });

        calendarEnd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DateBean dateBean = mEndList.get(position);
                //如果是下月的或者过去的日子则不可点击
                if (dateBean.getType() == 0 || dateBean.getType() == 2 || dateBean.isExpire()) {
                    return;
                }
                mControler.viewClicked(dateBean);
            }
        });

        mControler.setOnEndClick(new DateControler.OnEndDayClickListener() {
            @Override
            public void endClick(DateBean startBean, DateBean endBean) {
                int[] solarStart = startBean.getSolar();
                int[] solarEnd = endBean.getSolar();
                String start = solarStart[0] + "." + solarStart[1] + "." + solarStart[2];
                String end = solarEnd[0] + "." + solarEnd[1] + "." + solarEnd[2];
                showToast(getString(R.string.date_of_stay) + start + "--" + end);
            }
        });


    }

    private void initData() {
        Intent intent = getIntent();
        mStartDate = intent.getParcelableExtra(RecomHotelActivity.START_TIME);
        mEndDate = intent.getParcelableExtra(RecomHotelActivity.END_TIME);
        if (mStartDate != null && mEndDate != null) {
            mControler.resetData(mStartDate, mEndDate);
        }


        final HotelBean hotelBean = createHotelBean();

        recomHotelVM = ViewModelProviders.of(this).get(RecomHotelVM.class);

        recomHotelVM.getRoomStatus().observe(this, new Observer<XmResource<XMResult<String>>>() {
            @Override
            public void onChanged(@Nullable XmResource<XMResult<String>> xmResultXmResource) {
                xmResultXmResource.handle(new OnCallback<XMResult<String>>() {
                    @Override
                    public void onSuccess(XMResult<String> data) {
                        //预订酒店
                        BookHotelOneActivity.startBookHotel(SelectDateActivity.this, hotelBean, checkInDate, checkOutDate);
                        finish();
                    }

                    @Override
                    public void onError(int code, String message) {
                        XMToast.showToast(SelectDateActivity.this, getString(R.string.hotel_no_room_message), getDrawable(R.drawable.toast_error));
                    }
                });
            }
        });

    }

    private HotelBean createHotelBean() {
        Bundle bundle = getIntent().getBundleExtra(EXTRA_BUNDLE);

        if (bundle == null) {
            return null;
        }

        hotelId = bundle.getString(HotelConstants.HOTEL_ID_TAG);
        HotelBean hotelBean = new HotelBean();
        hotelBean.setHotelId(bundle.getString(HotelConstants.HOTEL_ID_TAG));
        hotelBean.setHotelName(bundle.getString(HotelConstants.HOTEL_NAME_TAG));
        hotelBean.setAddress(bundle.getString(HotelConstants.HOTEL_ADDRESS_TAG));
        hotelBean.setLat(bundle.getString(HotelConstants.HOTEL_LAT_TAG));
        hotelBean.setLon(bundle.getString(HotelConstants.HOTEL_LAT_TAG));
        hotelBean.setLon(bundle.getString(HotelConstants.HOTEL_LON_TAG));
        hotelBean.setTelephone(bundle.getString(HotelConstants.HOTEL_PHONE_TAG));
        hotelBean.setIconUrl(bundle.getString(HotelConstants.HOTEL_ICON_URL_TAG));

        return hotelBean;

    }
    @NormalOnClick({EventConstants.NormalClick.HOTEL_DATA_CANCE, EventConstants.NormalClick.HOTEL_DATA_SURE, EventConstants.NormalClick.HOTEL_DATA_PRE, EventConstants.NormalClick.HOTEL_DATA_NEXT})//按钮对应的名称
    @ResId({R.id.btn_clear, R.id.btn_sure, R.id.iv_pre, R.id.iv_next})//按钮对应的R文件id
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_clear:
                resetDateData();
                break;
            case R.id.btn_sure:
                confirmDate();
                break;
            case R.id.iv_pre:
                skipPreMonth();
                break;
            case R.id.iv_next:
                skipNextMonth();
                break;
            default:
                break;
        }
    }

    private void skipNextMonth() {
        calAndSetData(1);
    }

    private void skipPreMonth() {
        calAndSetData(-1);
    }

    private void calAndSetData(int i) {

        mCalendar.add(Calendar.MONTH, i);
        mCurrYear = mCalendar.get(Calendar.YEAR);
        mCurrMonth = mCalendar.get(Calendar.MONTH) + 1;

        Calendar calendar2 = (Calendar) mCalendar.clone();
        calendar2.add(Calendar.MONTH, 1);
        int year2 = calendar2.get(Calendar.YEAR);
        int month2 = calendar2.get(Calendar.MONTH) + 1;

        mStartList = CalendarUtil.getMonthDate(mCurrYear, mCurrMonth);
        mEndList = CalendarUtil.getMonthDate(year2, month2);
        tvStartTime.setText(String.format(getString(R.string.year_month), mCurrYear, mCurrMonth));
        tvEndTime.setText(String.format(getString(R.string.year_month), year2, month2));

        mStartAdapter.setDatas(mStartList);
        mEndAdapter.setDatas(mEndList);

        Calendar instance = Calendar.getInstance();
        int realYear = instance.get(Calendar.YEAR);
        int realMonth = instance.get(Calendar.MONTH) + 1;
        //如果左边数据是当前时间月份 就不允许看以前的月份
        if (realYear == mCurrYear && realMonth == mCurrMonth) {
            mIvPre.setVisibility(View.GONE);
        } else {
            mIvPre.setVisibility(View.VISIBLE);
        }
    }


    private void resetDateData() {
        if (mControler != null) {
            mControler.resetData();
        }
    }

    private void confirmDate() {
        if (mControler != null) {
            if (mControler.getStartItem() != null && mControler.getEndItem() != null) {
                if (!StringUtil.isEmpty(hotelId)) {
                    int[] startSolar = mControler.getStartItem().getSolar();
                    int[] endSolar = mControler.getEndItem().getSolar();
                    checkInDate = String.format(getString(R.string.date_str_format), startSolar[0], startSolar[1], startSolar[2]);
                    checkOutDate = String.format(getString(R.string.date_str_format), endSolar[0], endSolar[1], endSolar[2]);
                    recomHotelVM.fetchHotelRoomStatus(hotelId, checkInDate, checkOutDate);
                    return;
                }

                Intent intent = new Intent(SelectDateActivity.this, RecomHotelActivity.class);
                intent.putExtra(RecomHotelActivity.START_TIME, mControler.getStartItem());
                intent.putExtra(RecomHotelActivity.END_TIME, mControler.getEndItem());
                setResult(RESULT_OK, intent);
                finish();
            } else {
                showToast("请选择正确日期");
            }
        }
    }
}
