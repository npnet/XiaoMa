package com.xiaoma.setting.practice.ui;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.vrpractice.ui
 *  @file_name:      CarWindowSettingActivity
 *  @author:         Rookie
 *  @create_time:    2019/6/11 17:37
 *  @description：   TODO             */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.model.pratice.CarWindowSettingBean;
import com.xiaoma.setting.R;
import com.xiaoma.setting.common.constant.SettingConstants;

public class CarWindowSettingActivity extends BaseActivity implements View.OnClickListener {
    public static final String TAG="[CarWindowSettingActivity]";
    private TextView tvMainSeatWindow;
    private ToggleButton tbMainSeatWindow;
    private TextView tvSecondSeatWindow;
    private ToggleButton tbSecondSeatWondow;
    private RadioGroup rgMainMode;
    private RadioButton rbAll;
    private RadioButton rbHalf;
    private RadioButton rbOneThird;
    private RadioButton rbTwoThird;
    private RadioGroup rgSecondMode;
    private RadioButton rbAll2;
    private RadioButton rbHalf2;
    private RadioButton rbOneThird2;
    private RadioButton rbTwoThird2;
    private TextView tvLeftSeatWindow;
    private ToggleButton tbLeftSeatWindow;
    private TextView tvRightSeatWindow;
    private ToggleButton tbRightWindowHeat;
    private int openMainLevel;
    private int openSecondLevel;
    private CarWindowSettingBean mCarWindowSettingBean;
    private BroadcastReceiver mBroadcastReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_window);
        initView();
        initData();
        registerExit();
    }

    @Override
    protected void onDestroy() {
        unRegisterExit();
        super.onDestroy();
    }

    private void registerExit() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("close_app_VR_PRACTICE");
        registerReceiver(mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                exit();
            }
        }, intentFilter);
    }
    private void unRegisterExit() {
        if (mBroadcastReceiver == null) return;
        unregisterReceiver(mBroadcastReceiver);
    }
    private void exit() {
        finish();
    }
    private void initView() {

        tvMainSeatWindow = findViewById(R.id.tv_main_seat_window);
        tbMainSeatWindow = findViewById(R.id.tb_main_seat_window);
        tvSecondSeatWindow = findViewById(R.id.tv_second_seat_window);
        tbSecondSeatWondow = findViewById(R.id.tb_second_seat_window);
        rgMainMode = findViewById(R.id.rg_main_mode);
        rbAll = findViewById(R.id.rb_all);
        rbHalf = findViewById(R.id.rb_half);
        rbOneThird = findViewById(R.id.rb_one_third);
        rbTwoThird = findViewById(R.id.rb_two_third);
        rgSecondMode = findViewById(R.id.rg_second_mode);
        rbAll2 = findViewById(R.id.rb_all2);
        rbHalf2 = findViewById(R.id.rb_half2);
        rbOneThird2 = findViewById(R.id.rb_one_third2);
        rbTwoThird2 = findViewById(R.id.rb_two_third2);
        tvLeftSeatWindow = findViewById(R.id.tv_left_seat_window);
        tbLeftSeatWindow = findViewById(R.id.tb_left_seat_window);
        tvRightSeatWindow = findViewById(R.id.tv_right_seat_window);
        tbRightWindowHeat = findViewById(R.id.tb_right_window_heat);

        tvMainSeatWindow.setOnClickListener(this);
        tvSecondSeatWindow.setOnClickListener(this);
        tvLeftSeatWindow.setOnClickListener(this);
        tvRightSeatWindow.setOnClickListener(this);
        tbMainSeatWindow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rgMainMode.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
        tbSecondSeatWondow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rgSecondMode.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
        initRb();
    }

    private void initData() {
        mCarWindowSettingBean = getIntent().getParcelableExtra(SettingConstants.WINDOW_BEAN);
        tbMainSeatWindow.setChecked(mCarWindowSettingBean.isMainSeatWindow());
        tbSecondSeatWondow.setChecked(mCarWindowSettingBean.isSecondSeatWindow());
        tbLeftSeatWindow.setChecked(mCarWindowSettingBean.isBackLeftWindow());
        tbRightWindowHeat.setChecked(mCarWindowSettingBean.isBackRightWindow());

        openMainLevel = mCarWindowSettingBean.getMainSeatWindowLevel();
        openSecondLevel = mCarWindowSettingBean.getSecondSeatWindowLevel();
        rgMainMode.check(openMainLevel==0?R.id.rb_all:(openMainLevel==1?R.id.rb_half:(openMainLevel==2?R.id.rb_one_third:R.id.rb_two_third)));
        rgSecondMode.check(openSecondLevel==0?R.id.rb_all2:(openSecondLevel==1?R.id.rb_half2:(openSecondLevel==2?R.id.rb_one_third2:R.id.rb_two_third2)));
    }

    private void initRb() {
        rgMainMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_all:
                        openMainLevel = 0;
                        rbAll.setBackgroundResource(R.drawable.bg_radio_button_left);
                        rbHalf.setBackgroundResource(R.drawable.bg_radio_button_middle);
                        rbOneThird.setBackgroundResource(R.drawable.bg_radio_button_middle);
                        rbTwoThird.setBackgroundResource(R.drawable.bg_radio_button_right);

                        rbAll.setTextColor(getResources().getColor(R.color.tv_reply_y));
                        rbHalf.setTextColor(getResources().getColor(R.color.tv_reply_g));
                        rbOneThird.setTextColor(getResources().getColor(R.color.tv_reply_g));
                        rbTwoThird.setTextColor(getResources().getColor(R.color.tv_reply_g));
                        break;
                    case R.id.rb_half:
                        openMainLevel = 1;
                        rbAll.setBackgroundResource(R.drawable.bg_radio_button_left);
                        rbHalf.setBackgroundResource(R.drawable.bg_radio_button_middle);
                        rbOneThird.setBackgroundResource(R.drawable.bg_radio_button_middle);
                        rbTwoThird.setBackgroundResource(R.drawable.bg_radio_button_right);
                        rbAll.setTextColor(getResources().getColor(R.color.tv_reply_g));
                        rbHalf.setTextColor(getResources().getColor(R.color.tv_reply_y));
                        rbOneThird.setTextColor(getResources().getColor(R.color.tv_reply_g));
                        rbTwoThird.setTextColor(getResources().getColor(R.color.tv_reply_g));
                        break;
                    case R.id.rb_one_third:
                        openMainLevel = 2;
                        rbAll.setBackgroundResource(R.drawable.bg_radio_button_left);
                        rbHalf.setBackgroundResource(R.drawable.bg_radio_button_middle);
                        rbOneThird.setBackgroundResource(R.drawable.bg_radio_button_middle);
                        rbTwoThird.setBackgroundResource(R.drawable.bg_radio_button_right);
                        rbAll.setTextColor(getResources().getColor(R.color.tv_reply_g));
                        rbHalf.setTextColor(getResources().getColor(R.color.tv_reply_g));
                        rbOneThird.setTextColor(getResources().getColor(R.color.tv_reply_y));
                        rbTwoThird.setTextColor(getResources().getColor(R.color.tv_reply_g));
                        break;
                    case R.id.rb_two_third:
                        openMainLevel = 3;
                        rbAll.setBackgroundResource(R.drawable.bg_radio_button_left);
                        rbHalf.setBackgroundResource(R.drawable.bg_radio_button_middle);
                        rbOneThird.setBackgroundResource(R.drawable.bg_radio_button_middle);
                        rbTwoThird.setBackgroundResource(R.drawable.bg_radio_button_right);
                        rbAll.setTextColor(getResources().getColor(R.color.tv_reply_g));
                        rbHalf.setTextColor(getResources().getColor(R.color.tv_reply_g));
                        rbOneThird.setTextColor(getResources().getColor(R.color.tv_reply_g));
                        rbTwoThird.setTextColor(getResources().getColor(R.color.tv_reply_y));
                        break;
                    default:
                        break;
                }
            }
        });

        rgSecondMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_all2:
                        openSecondLevel = 0;
                        rbAll2.setBackgroundResource(R.drawable.bg_radio_button_left);
                        rbHalf2.setBackgroundResource(R.drawable.bg_radio_button_middle);
                        rbOneThird2.setBackgroundResource(R.drawable.bg_radio_button_middle);
                        rbTwoThird2.setBackgroundResource(R.drawable.bg_radio_button_right);

                        rbAll2.setTextColor(getResources().getColor(R.color.tv_reply_y));
                        rbHalf2.setTextColor(getResources().getColor(R.color.tv_reply_g));
                        rbOneThird2.setTextColor(getResources().getColor(R.color.tv_reply_g));
                        rbTwoThird2.setTextColor(getResources().getColor(R.color.tv_reply_g));
                        break;
                    case R.id.rb_half2:
                        openSecondLevel = 1;
                        rbAll2.setBackgroundResource(R.drawable.bg_radio_button_left);
                        rbHalf2.setBackgroundResource(R.drawable.bg_radio_button_middle);
                        rbOneThird2.setBackgroundResource(R.drawable.bg_radio_button_middle);
                        rbTwoThird2.setBackgroundResource(R.drawable.bg_radio_button_right);
                        rbAll2.setTextColor(getResources().getColor(R.color.tv_reply_g));
                        rbHalf2.setTextColor(getResources().getColor(R.color.tv_reply_y));
                        rbOneThird2.setTextColor(getResources().getColor(R.color.tv_reply_g));
                        rbTwoThird2.setTextColor(getResources().getColor(R.color.tv_reply_g));
                        break;
                    case R.id.rb_one_third2:
                        openSecondLevel = 2;
                        rbAll2.setBackgroundResource(R.drawable.bg_radio_button_left);
                        rbHalf2.setBackgroundResource(R.drawable.bg_radio_button_middle);
                        rbOneThird2.setBackgroundResource(R.drawable.bg_radio_button_middle);
                        rbTwoThird2.setBackgroundResource(R.drawable.bg_radio_button_right);
                        rbAll2.setTextColor(getResources().getColor(R.color.tv_reply_g));
                        rbHalf2.setTextColor(getResources().getColor(R.color.tv_reply_g));
                        rbOneThird2.setTextColor(getResources().getColor(R.color.tv_reply_y));
                        rbTwoThird2.setTextColor(getResources().getColor(R.color.tv_reply_g));
                        break;
                    case R.id.rb_two_third2:
                        openSecondLevel = 3;
                        rbAll2.setBackgroundResource(R.drawable.bg_radio_button_left);
                        rbHalf2.setBackgroundResource(R.drawable.bg_radio_button_middle);
                        rbOneThird2.setBackgroundResource(R.drawable.bg_radio_button_middle);
                        rbTwoThird2.setBackgroundResource(R.drawable.bg_radio_button_right);
                        rbAll2.setTextColor(getResources().getColor(R.color.tv_reply_g));
                        rbHalf2.setTextColor(getResources().getColor(R.color.tv_reply_g));
                        rbOneThird2.setTextColor(getResources().getColor(R.color.tv_reply_g));
                        rbTwoThird2.setTextColor(getResources().getColor(R.color.tv_reply_y));
                        break;
                    default:
                        break;
                }
            }
        });
        rgMainMode.check(0);
        rgSecondMode.check(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_main_seat_window:
                setCheckState(tbMainSeatWindow);
                break;
            case R.id.tv_second_seat_window:
                setCheckState(tbSecondSeatWondow);
                break;
            case R.id.tv_left_seat_window:
                setCheckState(tbLeftSeatWindow);
                break;
            case R.id.tv_right_seat_window:
                setCheckState(tbRightWindowHeat);
                break;
            default:
                break;
        }
    }

    private void setCheckState(ToggleButton tb) {
        tb.setChecked(!tb.isChecked());
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, CarWindowSettingActivity.class);
        mCarWindowSettingBean.setMainSeatWindow(tbMainSeatWindow.isChecked());
        mCarWindowSettingBean.setSecondSeatWindow(tbSecondSeatWondow.isChecked());
        mCarWindowSettingBean.setMainSeatWindowLevel(openMainLevel);
        mCarWindowSettingBean.setSecondSeatWindowLevel(openSecondLevel);
        mCarWindowSettingBean.setBackLeftWindow(tbLeftSeatWindow.isChecked());
        mCarWindowSettingBean.setBackRightWindow(tbRightWindowHeat.isChecked());
        intent.putExtra(SettingConstants.WINDOW_BEAN, mCarWindowSettingBean);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }
}
