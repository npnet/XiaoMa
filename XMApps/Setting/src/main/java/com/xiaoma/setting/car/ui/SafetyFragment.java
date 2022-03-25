package com.xiaoma.setting.car.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.XmCarConfigManager;
import com.xiaoma.carlib.manager.XmEngineManager;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.setting.R;
import com.xiaoma.setting.car.vm.SafetyVM;
import com.xiaoma.setting.common.constant.EventConstants;
import com.xiaoma.setting.common.constant.SettingConstants;
import com.xiaoma.setting.common.views.LeftPopupWindow;
import com.xiaoma.setting.common.views.NotificationDialog;
import com.xiaoma.setting.common.views.RadioButtonLayout;
import com.xiaoma.setting.common.views.SettingDialog;
import com.xiaoma.setting.common.views.SettingItemView;
import com.xiaoma.setting.common.views.SettingToast;
import com.xiaoma.setting.common.views.SwitchAnimation;
import com.xiaoma.ui.dialog.XmDialog;
import com.xiaoma.utils.log.KLog;

@PageDescComponent(EventConstants.PageDescribe.safetySettingFragmentPagePathDesc)
public class SafetyFragment extends BaseFragment implements SettingItemView.StateListener, SwitchAnimation.SwitchListener, XmEngineManager.EngineCallBack, SafetyVM.OnValueChange {
    private static final int RESET_TIME = 2000;
    public String TAG = SafetyFragment.class.getSimpleName();
    public static String CAR_CONTROL = "car_control";
    private SafetyVM driverAssistVM;
    private RadioButtonLayout safetyBrakeOpt;
    private SettingItemView sivLane;
    private SettingItemView sivKeep;
    private SwitchAnimation daw;
    private SwitchAnimation seatbelt;
    private SwitchAnimation isa;
    private SwitchAnimation electronicBrake;
    private SwitchAnimation safetyBrake;
    private Button btnReset;
    private LeftPopupWindow mInfoPopupWindow;


    private RadioButtonLayout.CustomOnCheckedChangeListener safeListener = new RadioButtonLayout.CustomOnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            onSelect(group.getId(), checkedId - 1);
        }
    };
    private SwitchAnimation rearveiwMirror;
    private ImageView mIvSettingPic;
    private int currentSetting = 1;
    private int[] strikeImagesArr = {R.drawable.safe_strike_warning_low, R.drawable.safe_strike_warning_middle, R.drawable.safe_strike_warning_high};
    private int[] divergeImagesArr = {R.drawable.safe_diverge_low, R.drawable.safe_diverge_middle, R.drawable.safe_diverge_high};
    private int[] laneKeepingImagesArr = {R.drawable.safe_lane_keeping_diverge_warning, R.drawable.safe_lane_keeping_rectifying_keeping, R.drawable.safe_lane_keeping_center_keeping};
    private boolean isHiddenChanged = false; // 是否执行了onHiddenChanged


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_car_safety, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bindView(view);
        driverAssistVM.setOnValueChange(this);
    }

    private void bindView(View view) {
        safetyBrakeOpt = view.findViewById(R.id.safety_brake_opt);
        safetyBrakeOpt.setDelayUIChange(true);
        sivLane = view.findViewById(R.id.siv_lane);
        sivLane.setDelayItemSelector(true);
        sivKeep = view.findViewById(R.id.siv_keep);
        sivKeep.setDelayItemSelector(true);
        daw = view.findViewById(R.id.safety_daw);
        seatbelt = view.findViewById(R.id.seat_belt);
        isa = view.findViewById(R.id.isa);
        safetyBrake = view.findViewById(R.id.safety_brake);
        if (!XmCarConfigManager.hasFcwAndAeb()) {
            safetyBrake.check(false);
            safetyBrake.setVisibility(View.GONE);
            view.findViewById(R.id.safety_brake_line).setVisibility(View.GONE);
            sivLane.setVisibility(View.GONE);
            view.findViewById(R.id.siv_lane_line).setVisibility(View.GONE);
            sivKeep.setVisibility(view.GONE);
            view.findViewById(R.id.siv_keep_line).setVisibility(View.GONE);
            isa.setVisibility(View.GONE);
            view.findViewById(R.id.isa_line).setVisibility(View.GONE);
            daw.setVisibility(View.GONE);
            view.findViewById(R.id.safety_daw_line).setVisibility(View.GONE);
        }
        electronicBrake = view.findViewById(R.id.electronic_brake);
        btnReset = view.findViewById(R.id.btn_reset);
        mIvSettingPic = view.findViewById(R.id.iv_setting_pic);
        rearveiwMirror = view.findViewById(R.id.safety_rearview_mirror);
        view.findViewById(R.id.mirror_left_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVM().setMarkMirrorLeft(SDKConstants.REAR_MIRROR_LEFT);
            }
        });
        view.findViewById(R.id.mirror_right_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVM().setMarkMirrorRight(SDKConstants.REAR_MIRROR_RIGHT);
            }
        });
        rearveiwMirror.setNoticeVisibity(true);
        rearveiwMirror.setNoticeIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoPop();
            }
        });
       /* if (!XmCarConfigManager.hasBackMirrorAutoFold()) {
            rearveiwMirror.setVisibility(View.GONE);
            view.findViewById(R.id.rearview_mirror_line).setVisibility(View.GONE);
        }*/

        sivLane.setListener(this);
        sivKeep.setListener(this);
        daw.setListener(this);
        seatbelt.setListener(this);
        safetyBrake.setListener(this);
        isa.setListener(this);
        electronicBrake.setListener(this);
        rearveiwMirror.setListener(this);
        XmEngineManager.getInstance(getContext()).registerCallBack(this);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //胎压复位开关
                if (XmEngineManager.getInstance(getContext()).getEngineStatus()) {
                    showResetDialog();
                } else {
                    SettingToast.build(getContext()).setMessage(getContext().getString(R.string.security_tips)).setImage(R.drawable.icon_error).show(2000);
                }
            }
        });


        safetyBrakeOpt.setCustomOnCheckedChangeListener(safeListener);

        initData();
    }

    private void showInfoPop() {
        // TODO: 2019/7/20 0020 具体视图内容等待UI更新
        if (mInfoPopupWindow == null) {
            mInfoPopupWindow = new LeftPopupWindow(getContext(), 1305, WindowManager.LayoutParams.MATCH_PARENT);
        }
        mInfoPopupWindow.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.START | Gravity.TOP,
                0, 0);
    }


    private void showNotificationDialog() {
        NotificationDialog notificationDialog = new NotificationDialog(getContext());
        notificationDialog.show();
        notificationDialog.setTitle(getString(R.string.operation_notification));
        String contentText = getString(R.string.notification_content);
//        SpannableString content = getContent(contentText);
        notificationDialog.setContent(contentText);
    }

    private SpannableString getContent(String content) {
        SpannableString spannableString = new SpannableString(content);
        AbsoluteSizeSpan size28 = new AbsoluteSizeSpan(28);
        AbsoluteSizeSpan size20 = new AbsoluteSizeSpan(20);
        spannableString.setSpan(size28, 0, 17, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(size20, 17, content.length() - 66, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(size28, content.length() - 66, content.length() - 48, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(size20, content.length() - 48, content.length() - 25, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(size28, content.length() - 25, content.length() - 23, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(size20, content.length() - 23, content.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    private void showResetDialog() {
        // TODO: 2018/10/30 0030 复位回调
        driverAssistVM.setResetTiretPressure(SDKConstants.VALUE.RESET_TIRE);
        final XmDialog progressDialog = SettingDialog.createProgressDialog(getFragmentManager(), getContext(), getString(R.string.tips_reset)).show();
        getView().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        }, 3000);
    }


    private void resetResult(boolean result) {
        Drawable image;
        String msg;
        if (result) {
            msg = getString(R.string.tips_reset_success);
            image = getContext().getResources().getDrawable(R.drawable.icon_success);
        } else {
            msg = getString(R.string.tips_reset_error);
            image = getContext().getResources().getDrawable(R.drawable.icon_error);
        }
        SettingToast.build(getContext()).setMessage(msg).setImage(image).show(2000);
    }


    private void initData() {
        driverAssistVM = ViewModelProviders.of(this).get(SafetyVM.class).initData();
        safetyBrake.check(driverAssistVM.getFcwAebSwitch());
        if (driverAssistVM.getFcwSensitivity() != -1) {
            safetyBrakeOpt.checkIndex(driverAssistVM.getFcwSensitivity());
        }

        sivLane.setCheck(driverAssistVM.getLdwSensitivity());
        sivKeep.setCheck(driverAssistVM.getLaneKeep());
        daw.check(driverAssistVM.getDaw());
        seatbelt.check(driverAssistVM.getSeatBelt());
        isa.check(driverAssistVM.getISA());
        electronicBrake.check(driverAssistVM.getElectronicBrake());
        boolean rearviewMirrorEnable = driverAssistVM.getRearviewMirrorEnable();
        rearveiwMirror.setListener(null);
        rearveiwMirror.check(rearviewMirrorEnable);
        rearveiwMirror.setListener(this);
        /*if (rearviewMirrorEnable) {
            rearveiwMirror.check(driverAssistVM.getRearviewMirrir()); //获取外后视镜自动折叠初始值
        } else {
            rearveiwMirror.check(false); //获取外后视镜自动折叠初始值
            rearveiwMirror.setEnabled(rearviewMirrorEnable);
            rearveiwMirror.setListener(null);
        }*/
        if (!isHiddenChanged) // 第一次进入页面 设置默认图 当执行过onHiddenChanged 就不在设置默认图 而是根据onChanged 回调设置选项修改过的
            mIvSettingPic.setImageResource(R.drawable.car_setting_default);//显示默认的车辆渲染图
    }


    private SafetyVM getVM() {
        if (driverAssistVM != null) {
            return driverAssistVM;
        } else {
            return driverAssistVM = ViewModelProviders.of(this).get(SafetyVM.class).initData();
        }
    }


    @Override
    public void engineON() {
        engineStateControl(true);
    }

    @Override
    public void engineOFF() {
        engineStateControl(false);
    }

    private void engineStateControl(boolean state) {
        //        isa,daw,electronicBrake,btnReset,sivKeep,safetyBrake,sivLane
        try {
            isa.setClickable(state);
            daw.setClickable(state);
            electronicBrake.setClickable(state);
            btnReset.setClickable(state);
            sivKeep.setClickable(state);
            safetyBrake.setClickable(state);
            sivLane.setClickable(state);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setSegmentControlIndex(SettingItemView view, boolean isChecked, int open, int close) {
        if (isChecked) {
            view.setCheck(open);
        } else {
            view.setCheck(close - 1);
        }
    }

    @Override
    public void onSelect(int viewId, int index) {
        KLog.e(TAG, "onSelect : " + viewId + "  index:" + index);
        switch (viewId) {
            case R.id.safety_brake_opt://主动制动
                getVM().setFcwSensitivity(index + 1);
                break;
            case R.id.siv_lane://车道偏离
                getVM().setLdwSensitivity(index);
                break;
            case R.id.siv_keep://道路保持
                getVM().setLaneKeep(index);
                break;
        }
    }

    @Override
    public void onDestroy() {
        XmEngineManager.getInstance(getContext()).unregisterCallBack(this);
        super.onDestroy();
    }

    @Override
    public void onSwitch(int viewId, boolean state) {
        KLog.e(TAG, "onSwitch : " + viewId + " state:" + state);
        switch (viewId) {
            case R.id.safety_brake://主动制动
                getVM().setFCwAebSwitch(state);
                if (state) {
                    int fcwSensitivity = driverAssistVM.getFcwSensitivity();
                    if (fcwSensitivity != -1) {
                        safetyBrakeOpt.checkIndex(fcwSensitivity);
                    }
//                    driverAssistVM.setFcwSensitivity(1);
//                    safetyBrakeOpt.checkIndex(1);
                }
                break;
            case R.id.safety_daw://驾驶员注意力提醒
                getVM().setDAW(state);
                break;
            case R.id.seat_belt://后排安全带提醒
                getVM().setSeatBelt(state);
                break;
            case R.id.isa://交通标志识别
                getVM().setISA(state);
                break;
            case R.id.electronic_brake://电子刹车自动夹紧
                driverAssistVM.setElectronicBrake(state);
                break;
            case R.id.safety_rearview_mirror:
                driverAssistVM.setRearviewMirror(state);
                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            isHiddenChanged = true;
            initData();
        }
    }

    @Override
    public void onChange(int id, Object value) {
        if (id == SDKConstants.FCW_AEB_SWITCH) {
            boolean b = (int) value == SDKConstants.VALUE.FCW_ON || (int) value == SDKConstants.VALUE.FCW_STANDBY;
            if (b == safetyBrake.isCheck())
                return;
            safetyBrake.setListener(null);
            safetyBrake.check(b);
            KLog.d(CAR_CONTROL, "前防撞预警开关: " + b);
            safetyBrake.setListener(this);
            currentSetting = SettingConstants.SAFE_STRIKE_WARNING;
            switchSettingPic(b);
        } else if (id == SDKConstants.FCW_SENSITIVITY) {
            int i = driverAssistVM.caseFcwToIndex((Integer) value);
            if (i == safetyBrakeOpt.getCheckedIndex()) {
                return;
            }
            safetyBrakeOpt.setCustomOnCheckedChangeListener(null);
            safetyBrakeOpt.checkIndex(i);
            KLog.d(CAR_CONTROL, "前防撞预警条目: " + i);
            safetyBrakeOpt.setCustomOnCheckedChangeListener(safeListener);
            currentSetting = SettingConstants.SAFE_STRIKE_WARNING;
            switchSettingPic(i);
        } else if (id == SDKConstants.LDW_SENSITIVITY) {
            int i = (Integer) value;
            if (i == sivLane.getIndex())
                return;
            sivLane.setListener(null);
            sivLane.setCheck(i);
            KLog.d(CAR_CONTROL, "车道偏离警示: " + value);
            sivLane.setListener(this);
            currentSetting = SettingConstants.SAFE_DIVERGE_WARNING;
            switchSettingPic(i);
        } else if (id == SDKConstants.LKA_MODE) {
            int index = driverAssistVM.caseKeepToIndex((Integer) value);
            if (index == sivKeep.getIndex())
                return;
            sivKeep.setListener(null);
            sivKeep.setCheck(index);
            KLog.d(CAR_CONTROL, "车道保持: " + value);
            sivKeep.setListener(this);
            currentSetting = SettingConstants.SAFE_LANE_KEEPING_MODE;
            switchSettingPic(index);
        } else if (id == SDKConstants.ISA) {
            boolean check = (int) value == SDKConstants.VALUE.IFC_ON || (int) value == SDKConstants.VALUE.IFC_STANDBY;
            if (check == isa.isCheck())
                return;
            isa.setListener(null);
            isa.check(check);
            KLog.d(CAR_CONTROL, "交通标志识别: " + value);
            isa.setListener(this);
            currentSetting = SettingConstants.SAFE_TRAFFIC_SIGN_RECOGNITION;
            switchSettingPic(check);
        } else if (id == SDKConstants.DAW) {
           /* boolean check = driverAssistVM.caseDawToBoolean((Integer) value);
            if (check == daw.isCheck())
                return;
            daw.setListener(null);
            daw.check(check);
            KLog.d(CAR_CONTROL, "驾驶员注意力提醒: " + value);
            daw.setListener(this);
            currentSetting = SettingConstants.SAFE_DRIVER_ATTENTION_REMINDER;
            switchSettingPic(check);*/
        } else if (id == SDKConstants.EPB) {
            boolean check = (int) value == SDKConstants.VALUE.EPB_ON;
            if (check == electronicBrake.isCheck())
                return;
            electronicBrake.setListener(null);
            electronicBrake.check(check);
            KLog.d(CAR_CONTROL, "电子驻车自动夹紧: " + value);
            electronicBrake.setListener(this);
            currentSetting = SettingConstants.SAFE_ELECTRONIC_BRAKE;
            switchSettingPic(check);
        } else if (id == SDKConstants.REAR_BELT_WORNING_SWITCH) {
            boolean check = driverAssistVM.caseSeatBelt((int) value);
            if (check == seatbelt.isCheck())
                return;
            seatbelt.setListener(null);
            seatbelt.check(check);
            KLog.d(CAR_CONTROL, "后排安全带未系提醒: " + value);
            seatbelt.setListener(this);
            currentSetting = SettingConstants.SAFE_REAR_SEATS_BELT_REMINDER;
            switchSettingPic(check);
        } else if (id == SDKConstants.ID_INTELLIGENT_MIRROR_SWITCH_GET) {  // 外后视镜回调数据
//            boolean isEnable = (int) value == SDKConstants.VALUE.REAR_VEIW_ENABLE;
            Integer[] result = (Integer[])value;
            if (result != null && result.length == 2) {
                boolean  isCheck = result[0] == 1 || result[1] == 1;
                KLog.d(CAR_CONTROL, "外后视镜自动折叠: " + isCheck);
                rearveiwMirror.check(isCheck);
                currentSetting = SettingConstants.REAR_VIEW_MIRROR;
            }
        } else if (id == SDKConstants.VALUE.MIRROR_LEFT_CONFIRM) {
            KLog.d(CAR_CONTROL, "左后视镜设置成功");
        } else if (id == SDKConstants.VALUE.MIRROR_RIGHT_CONFIRM){
            KLog.d(CAR_CONTROL, "右后视镜设置成功");
        }
    }

    private void switchSettingPic(int selectIndex) {
        switch (currentSetting) {
            case SettingConstants.SAFE_STRIKE_WARNING:
                if (selectIndex < 0 || selectIndex >= strikeImagesArr.length) return;
                mIvSettingPic.setImageResource(strikeImagesArr[selectIndex]);
                break;
            case SettingConstants.SAFE_DIVERGE_WARNING:
                if (selectIndex < 0 || selectIndex >= divergeImagesArr.length) return;
                mIvSettingPic.setImageResource(divergeImagesArr[selectIndex]);
                break;
            case SettingConstants.SAFE_LANE_KEEPING_MODE:
                if (selectIndex < 0 || selectIndex >= laneKeepingImagesArr.length) return;
                mIvSettingPic.setImageResource(laneKeepingImagesArr[selectIndex]);
                break;
        }
    }

    private void switchSettingPic(boolean open) {
        switch (currentSetting) {
            case SettingConstants.SAFE_STRIKE_WARNING:
                int fcwSensitivity = driverAssistVM.getFcwSensitivity(); // 主动制动灵敏度
                if (fcwSensitivity == -1) fcwSensitivity = 0;
                mIvSettingPic.setImageResource(open ? strikeImagesArr[fcwSensitivity] : R.drawable.safe_strike_warning_close);
                break;
            case SettingConstants.SAFE_TRAFFIC_SIGN_RECOGNITION:
                mIvSettingPic.setImageResource(open ? R.drawable.safe_traffic_sign_recognition_open : R.drawable.safe_traffic_sign_recognition_close);
                break;
            case SettingConstants.SAFE_DRIVER_ATTENTION_REMINDER:
                mIvSettingPic.setImageResource(open ? R.drawable.safe_driver_attention_remind_open : R.drawable.safe_driver_attention_remind_close);
                break;
            case SettingConstants.SAFE_REAR_SEATS_BELT_REMINDER:
                mIvSettingPic.setImageResource(open ? R.drawable.safe_rear_seat_belt_remind_open : R.drawable.safe_rear_seat_belt_remind_close);
                break;
            case SettingConstants.SAFE_ELECTRONIC_BRAKE: // 自动夹紧
                mIvSettingPic.setImageResource(R.drawable.car_setting_default);
                break;

        }
    }


}
