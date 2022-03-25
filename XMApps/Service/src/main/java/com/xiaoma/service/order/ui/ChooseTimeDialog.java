package com.xiaoma.service.order.ui;

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.service.R;
import com.xiaoma.service.common.constant.EventConstants;
import com.xiaoma.service.order.adapter.OrderTimeAdapter;
import com.xiaoma.service.order.model.OrderTime;
import com.xiaoma.service.order.vm.OrderVM;
import com.xiaoma.skin.manager.XmSkinManager;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.TimeUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 选择时间dialog
 * Created by zhushi.
 * Date: 2018/11/13
 */
@PageDescComponent(EventConstants.PageDescribe.selectTimeActivityPagePathDesc)
public class ChooseTimeDialog extends BaseActivity implements View.OnClickListener {
    public static final int GRID_TIME_DURING_COUNT = 4;
    private RecyclerView mRecyclerView;
    private OrderTimeAdapter mAdapter;
    private TextView mDateTv;
    private TimePickerView mPvDate;
    private Calendar mSelectedDate;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private OrderVM mOrderVM;
    private int mPosition = -1;
    private OrderTime mOrderTime;
    private Button btnSure;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_choose_time);
        getNaviBar().showBackNavi();
        getWindow().setLayout(getResources().getDimensionPixelOffset(R.dimen.dialog_choose_time_width), ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().setGravity(Gravity.START);
        initView();
    }

    private void initView() {
        String intentDateStr = getIntent().getStringExtra(OrderActivity.EXTRA_ARRIVE_DATE);
        btnSure = findViewById(R.id.time_submit);
        mPosition = getIntent().getIntExtra(OrderActivity.EXTRA_POSITION, -1);
        mOrderVM = ViewModelProviders.of(this).get(OrderVM.class);
        mOrderVM.getOrderDates().observe(this, new Observer<XmResource<List<OrderTime>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<OrderTime>> listXmResource) {
                if (listXmResource == null) {
                    return;
                }
                listXmResource.handle(new OnCallback<List<OrderTime>>() {
                    @Override
                    public void onSuccess(List<OrderTime> data) {
                        if (data == null || data.size() == 0) {
                            mAdapter.setEmptyView(R.layout.state_empty_view, (ViewGroup) mRecyclerView.getParent());
                            mAdapter.notifyDataSetChanged();
                            btnSure.setVisibility(View.GONE);
                            return;
                        }

                        btnSure.setVisibility(View.VISIBLE);
                        if (mPosition != -1 && mPosition < data.size()) {
                            mOrderTime = data.get(mPosition);
                            data.get(mPosition).setSelected(true);
                            btnSure.setEnabled(true);

                        } else {
                            btnSure.setEnabled(false);
                        }
                        mAdapter.setNewData(data);
                    }
                });
            }
        });
        mOrderVM.fetchAppointmentTime();

        mSelectedDate = Calendar.getInstance();
        Calendar mTomorrowDate = Calendar.getInstance();
        mTomorrowDate.add(Calendar.DATE, 1);//把日期往后增加一天

        if (StringUtil.isEmpty(intentDateStr)) {
            mSelectedDate.setTime(Calendar.getInstance().getTime());
            mSelectedDate.add(Calendar.DATE, 1);//把日期往后增加一天

        } else {
            try {
                mSelectedDate.setTime(dateFormat.parse(intentDateStr));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        mDateTv = findViewById(R.id.date);
        mDateTv.setText(dateFormat.format(mSelectedDate.getTime()) + TimeUtils.getWeek(getBaseContext(), mSelectedDate));

        mPvDate = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                //如果选择的时间一样，直接return;
                if (dateFormat.format(mSelectedDate.getTime()).equals(dateFormat.format(date))) {
                    return;
                }
                //清除之前的选择项
                mPosition = -1;
                mOrderTime = null;
                mSelectedDate.setTime(date);
                mDateTv.setText(dateFormat.format(date) + TimeUtils.getWeek(getBaseContext(), mSelectedDate));
                //重新请求可预约时间
                mOrderVM.fetchAppointmentTime();
            }
        }).setLayoutRes(R.layout.pickerview_custom_date, new CustomListener() {
            @Override
            public void customLayout(View v) {
                final TextView tvSubmit = v.findViewById(R.id.tv_finish);
                final TextView tvCancel = v.findViewById(R.id.tv_cancel);
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
                .isCenterLabel(true)
                .setType(new boolean[]{true, true, true, false, false, false})
                .setRangDate(mTomorrowDate, null)//只能选择明天的日期
                .setNeedYearListener(false)
                .isDialog(true).build();

        Dialog mDialog = mPvDate.getDialog();
        if (mDialog != null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(this.getResources().getDimensionPixelOffset(R.dimen.pv_custom_date_width), this.getResources().getDimensionPixelOffset(R.dimen.pv_custom_date_height), Gravity.CENTER);
            mPvDate.getDialogContainerLayout().setLayoutParams(params);
        }

        mRecyclerView = findViewById(R.id.choose_time_recycler);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, GRID_TIME_DURING_COUNT);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mAdapter = new OrderTimeAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setSelectOrderTimeListener(new OrderTimeAdapter.SelectOrderTimeListener() {
            @Override
            public void setSelectOrderTime(int position, OrderTime orderTime) {
                mPosition = position;
                mOrderTime = orderTime;
                btnSure.setEnabled(true);
            }
        });
        btnSure.setOnClickListener(this);
        mDateTv.setOnClickListener(this);
    }

    /**
     * 确定
     */
    public void submitTime() {
        if (mOrderTime == null) {
            XMToast.toastException(this, getString(R.string.order_time_tips), false);
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(OrderActivity.POSITION, mPosition);
        intent.putExtra(OrderActivity.RESULT_DATE, dateFormat.format(mSelectedDate.getTime()));
        intent.putExtra(OrderActivity.RESULT_TIME, mOrderTime.getTimePhase());
        intent.putExtra(OrderActivity.RESULT_WEEK, TimeUtils.getWeek(getBaseContext(), mSelectedDate));

        this.setResult(RESULT_OK, intent);
        this.finish();
    }

    /**
     * 选择日期
     */
    public void chooseDate() {

        if (mSelectedDate != null) {
            mPvDate.setDate(mSelectedDate);
        }
        mPvDate.show();
    }

    @Override
    protected void noNetworkOnRetry() {
        super.noNetworkOnRetry();
        mOrderVM.fetchAppointmentTime();
    }

    @Override
    public void finish() {
        super.finish();
        //注释掉activity本身的过渡动画
        overridePendingTransition(0, 0);
    }

    @Override
    @NormalOnClick({EventConstants.NormalClick.selectDate, EventConstants.NormalClick.affirmTime})
    @ResId({R.id.date, R.id.time_submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.date:
                chooseDate();
                break;
            case R.id.time_submit:
                submitTime();
                break;
        }
    }
}
