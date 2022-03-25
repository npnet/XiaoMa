package com.xiaoma.setting.practice.ui;


/*  @project_name：  XMAgateOS
 *  @package_name：  com.xiaoma.vrpractice.ui
 *  @file_name:      CarSettingActivity
 *  @author:         Rookie
 *  @create_time:    2019/6/4 16:45
 *  @description：   TODO             */

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.Group;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.model.pratice.CarSettingBean;
import com.xiaoma.model.pratice.CarWindowSettingBean;
import com.xiaoma.model.pratice.VrPracticeConstants;
import com.xiaoma.setting.R;
import com.xiaoma.setting.common.constant.SettingConstants;
import com.xiaoma.setting.practice.view.NumberPickerView;
import com.xiaoma.setting.practice.view.ObservableScrollView;
import com.xiaoma.setting.practice.view.VerticalScrollBar;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.LaunchUtils;

public class CarSettingActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = "[CarSettingActivity]";
    private TextView tvTemperature;
    private TextView tvWindow;
    private TextView tvBackWindowHeat;
    private ToggleButton tbBackWindowHeat;
    private TextView tvOpenWindow;
    private ToggleButton tbOpenWindow;
    private TextView tvSeatHeat;
    private ToggleButton tbSeatHeat;
    private TextView tvOpenSunshade;
    private ToggleButton tbOpenSunshade;
    private RadioGroup rgMode;
    private RadioButton rbAll;
    private RadioButton rbHalf;
    private RadioButton rbOneThird;
    private RadioButton rbTwoThird;
    private TextView tvMainSeatHeat;
    private ToggleButton tbMainSeatHeat;
    private TextView tvSecondSeatHeat;
    private ToggleButton tbSecondSeatHeat;
    private TextView tvOpenReadLed;
    private ToggleButton tbOpenReadLed;
    private TextView tvOpenSceneLed;
    private ToggleButton tbOpenSceneLed;
    private Group groupSeatHeat;
    private NumberPickerView mWheelTemp;
    private String[] tempStr = {"18℃", "18.5℃", "19℃", "19.5℃", "20℃", "20.5℃",
            "21℃", "21.5℃", "22℃", "22.5℃", "23℃", "23.5℃", "24℃", "24.5℃",
            "25℃", "25.5℃", "26℃", "26.5℃", "27℃", "27.5℃", "28℃", "28.5℃",
            "29℃", "29.5℃", "30℃", "30.5℃", "31℃", "31.5℃", "32℃"};
    private Dialog mTempDialog;
    private float temperature;
    private CarWindowSettingBean mCarWindowSettingBean;
    private static final int REQUEST_CODE = 101;
    private CarSettingBean mCarSettingBean;
    private int openLevel;
    private TextView mTvTempValue;
    private TextView mTvWindowValue;

    private int mActionPosition;
    private int mRequestCode;

    private VerticalScrollBar mScrollBar;
    private ObservableScrollView mScrollView;
    private BroadcastReceiver mBroadcastReceiver;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_setting);
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

        tvTemperature = findViewById(R.id.tv_temperature);
        tvWindow = findViewById(R.id.tv_window);
        mTvTempValue = findViewById(R.id.tv_temperature_value);
        mTvWindowValue = findViewById(R.id.tv_window_value);
        tvBackWindowHeat = findViewById(R.id.tv_back_window_heat);
        tbBackWindowHeat = findViewById(R.id.tb_back_window_heat);
        tvOpenWindow = findViewById(R.id.tv_open_window);
        tbOpenWindow = findViewById(R.id.tb_open_window);
        tvSeatHeat = findViewById(R.id.tv_seat_heat);
        tbSeatHeat = findViewById(R.id.tb_seat_heat);
        tvOpenSunshade = findViewById(R.id.tv_open_sunshade);
        tbOpenSunshade = findViewById(R.id.tb_open_sunshade);
        rgMode = findViewById(R.id.rg_mode);
        rbAll = findViewById(R.id.rb_all);
        rbHalf = findViewById(R.id.rb_half);
        rbOneThird = findViewById(R.id.rb_one_third);
        rbTwoThird = findViewById(R.id.rb_two_third);
        tvMainSeatHeat = findViewById(R.id.tv_main_seat_heat);
        tbMainSeatHeat = findViewById(R.id.tb_main_seat_heat);
        tvSecondSeatHeat = findViewById(R.id.tv_second_seat_heat);
        tbSecondSeatHeat = findViewById(R.id.tb_second_seat_heat);
        tvOpenReadLed = findViewById(R.id.tv_open_read_led);
        tbOpenReadLed = findViewById(R.id.tb_open_read_led);
        tvOpenSceneLed = findViewById(R.id.tv_open_scene_led);
        tbOpenSceneLed = findViewById(R.id.tb_open_scene_led);
        groupSeatHeat = findViewById(R.id.group_seat_heat);

        tvTemperature.setOnClickListener(this);
        tvWindow.setOnClickListener(this);
        tvBackWindowHeat.setOnClickListener(this);
        tvOpenWindow.setOnClickListener(this);
        tvSeatHeat.setOnClickListener(this);
        tvOpenSunshade.setOnClickListener(this);
        tvMainSeatHeat.setOnClickListener(this);
        tvSecondSeatHeat.setOnClickListener(this);
        tvOpenReadLed.setOnClickListener(this);
        tvOpenSceneLed.setOnClickListener(this);
        groupSeatHeat.setOnClickListener(this);

        tbSeatHeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                groupSeatHeat.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
        tbOpenSunshade.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                rgMode.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
        rgMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_all:
                        openLevel = 0;
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
                        openLevel = 1;
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
                        openLevel = 2;
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
                        openLevel = 3;
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
        rgMode.check(0);
        mScrollBar = findViewById(R.id.sisclamer_scrollbar);
        mScrollView = findViewById(R.id.sisclamer_scrollview);
        mScrollBar.setScrollView(mScrollView);
    }

    private void initData() {
        mCarWindowSettingBean = new CarWindowSettingBean();
        mCarSettingBean = new CarSettingBean();
        mCarSettingBean.setCarWindowSettingBean(mCarWindowSettingBean);
        setTemperature(mCarSettingBean);
        setWindowValue(mCarSettingBean);

        Intent intent = getIntent();
        Bundle bundleExtra = intent.getBundleExtra(LaunchUtils.EXTRA_BUNDLE);
        if (bundleExtra == null) {
            return;
        }
        mActionPosition = bundleExtra.getInt(VrPracticeConstants.ACTION_POSITION);
        mRequestCode = bundleExtra.getInt(VrPracticeConstants.SKILL_REQUEST_CODE);
        String text = bundleExtra.getString(VrPracticeConstants.ACTION_JSON);
        if (!TextUtils.isEmpty(text)) {
            mCarSettingBean = GsonHelper.fromJson(text, CarSettingBean.class);
            if (mCarSettingBean != null) {
                mCarWindowSettingBean = mCarSettingBean.getCarWindowSettingBean();
                parseIntent(mCarSettingBean);
            }
        }

    }

    private void setTemperature(CarSettingBean carSettingBean) {
        if (carSettingBean == null) {
            return;
        }
        if (carSettingBean.getTemperature() != 0) {
            temperature = carSettingBean.getTemperature();
            mTvTempValue.setText(temperature + "℃");
        } else {
            mTvTempValue.setText("");
        }
    }

    private void setWindowValue(CarSettingBean carSettingBean) {
        if (carSettingBean == null) {
            return;
        }
        if (carSettingBean.getCarWindowSettingBean().isMainSeatWindow() && carSettingBean.getCarWindowSettingBean().isSecondSeatWindow()) {
            mTvWindowValue.setText(R.string.main_and_second_seat);
        } else if (carSettingBean.getCarWindowSettingBean().isMainSeatWindow() && !carSettingBean.getCarWindowSettingBean().isSecondSeatWindow()) {
            mTvWindowValue.setText(R.string.main_seat);
        } else if (!carSettingBean.getCarWindowSettingBean().isMainSeatWindow() && carSettingBean.getCarWindowSettingBean().isSecondSeatWindow()) {
            mTvWindowValue.setText(R.string.second_seat);
        } else {
            mTvWindowValue.setText("");
        }
    }

    private void parseIntent(CarSettingBean carSettingBean) {
        if (carSettingBean == null) {
            return;
        }
        setTemperature(carSettingBean);
        setWindowValue(carSettingBean);
        tbBackWindowHeat.setChecked(carSettingBean.isBackWindowHeat());
        tbOpenWindow.setChecked(carSettingBean.isOpenWindow());
        tbSeatHeat.setChecked(carSettingBean.isSeatHeat());
        tbOpenSunshade.setChecked(carSettingBean.isOpenSunShade());
        tbOpenReadLed.setChecked(carSettingBean.isOpenReadLed());
        tbOpenSceneLed.setChecked(carSettingBean.isOpenSceneLed());
        tbMainSeatHeat.setChecked(carSettingBean.isMainSeatHeat());
        tbSecondSeatHeat.setChecked(carSettingBean.isSecondSeatHeat());
        openLevel = carSettingBean.getOpenWindowLevel();
        rgMode.check(openLevel == 0 ? R.id.rb_all : (openLevel == 1 ? R.id.rb_half : (openLevel == 2 ? R.id.rb_one_third : R.id.rb_two_third)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_temperature:
                showTemperatureDialog();
                break;
            case R.id.tv_window:
                Intent intent = new Intent(this, CarWindowSettingActivity.class);
                intent.putExtra(SettingConstants.WINDOW_BEAN, mCarWindowSettingBean);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.tv_back_window_heat:
                setCheckState(tbBackWindowHeat);
                break;
            case R.id.tv_open_window:
                setCheckState(tbOpenWindow);
                break;
            case R.id.tv_seat_heat:
                setCheckState(tbSeatHeat);
                break;
            case R.id.tv_open_sunshade:
                setCheckState(tbOpenSunshade);
                break;
            case R.id.tv_open_read_led:
                setCheckState(tbOpenReadLed);
                break;
            case R.id.tv_open_scene_led:
                setCheckState(tbOpenSceneLed);
                break;
            case R.id.tv_main_seat_heat:
                setCheckState(tbMainSeatHeat);
                break;
            case R.id.tv_second_seat_heat:
                setCheckState(tbSecondSeatHeat);
                break;
            default:
                break;
        }
    }

    private void showTemperatureDialog() {
        if (mTempDialog == null) {
            initTempDialog();
        }
        mTempDialog.show();
    }

    private void initTempDialog() {
        mTempDialog = new Dialog(this);
        View view = View.inflate(this, R.layout.view_temperature, null);
        mWheelTemp = view.findViewById(R.id.wheel_temp);
        mWheelTemp.setDisplayedValues(tempStr);
        mWheelTemp.setMinValue(0);
        mWheelTemp.setMaxValue(tempStr.length - 1);
        for (int i = 0; i < tempStr.length; i++) {
            String str = tempStr[i];
            String[] strs = str.split("℃");
            if (temperature == Float.parseFloat(strs[0])) {
                mWheelTemp.setValue(i);
            }
        }
        view.findViewById(R.id.tv_sure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tempValueStr = tempStr[mWheelTemp.getValue()];
                mTvTempValue.setText(tempValueStr);

                String[] split = tempValueStr.split("℃");
                temperature = Float.parseFloat(split[0]);
                mCarSettingBean.setTemperature(temperature);
                if (mTempDialog != null && mTempDialog.isShowing()) {
                    mTempDialog.dismiss();
                }
            }
        });
        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTempDialog != null && mTempDialog.isShowing()) {
                    mTempDialog.dismiss();
                }
            }
        });
        mTempDialog.setContentView(view);
        WindowManager.LayoutParams lp = mTempDialog.getWindow().getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.width = 600;
        lp.height = 400;
        mTempDialog.getWindow().setAttributes(lp);
    }

    private void setCheckState(ToggleButton tb) {
        tb.setChecked(!tb.isChecked());
    }

    @Override
    public void onBackPressed() {
        Bundle bundle = new Bundle();
        bundle.putString(VrPracticeConstants.ACTION_JSON, getCarSettingJson());
        bundle.putInt(VrPracticeConstants.ACTION_POSITION, mActionPosition);
        bundle.putInt(VrPracticeConstants.SKILL_REQUEST_CODE, mRequestCode);
        LaunchUtils.launchAppWithData(CarSettingActivity.this, VrPracticeConstants.PACKAGE_NAME, VrPracticeConstants.SKILL_CLASS_NAME, bundle);
        finish();
        super.onBackPressed();
    }

    public String getCarSettingJson() {
        mCarSettingBean.setTemperature(temperature);
        mCarSettingBean.setBackWindowHeat(tbBackWindowHeat.isChecked());
        mCarSettingBean.setOpenWindow(tbOpenWindow.isChecked());
        mCarSettingBean.setSeatHeat(tbSeatHeat.isChecked());
        mCarSettingBean.setOpenSunShade(tbOpenSunshade.isChecked());
        mCarSettingBean.setMainSeatHeat(tbMainSeatHeat.isChecked());
        mCarSettingBean.setSecondSeatHeat(tbSecondSeatHeat.isChecked());
        mCarSettingBean.setOpenReadLed(tbOpenReadLed.isChecked());
        mCarSettingBean.setOpenSceneLed(tbOpenSceneLed.isChecked());
        mCarSettingBean.setOpenWindowLevel(openLevel);
        mCarSettingBean.setCarWindowSettingBean(mCarWindowSettingBean);
        return GsonHelper.toJson(mCarSettingBean);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            mCarWindowSettingBean = data.getParcelableExtra(SettingConstants.WINDOW_BEAN);
            mCarSettingBean.setCarWindowSettingBean(mCarWindowSettingBean);
            setWindowValue(mCarSettingBean);
        }
    }
}
