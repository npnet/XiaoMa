package com.xiaoma.setting.car.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xiaoma.carlib.manager.XmCarConfigManager;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.setting.R;
import com.xiaoma.setting.common.constant.SettingConstants;
import com.xiaoma.setting.common.views.SettingItemView;
import com.xiaoma.setting.common.views.SwitchAnimation;
import com.xiaoma.setting.sdk.vm.FaceSettingVM;
import com.xiaoma.utils.log.KLog;

public class FaceSettingFragment extends BaseFragment implements SwitchAnimation.SwitchListener, SettingItemView.StateListener, FaceSettingVM.OnValueChangeListener {

    private SwitchAnimation recognizeSystem;
    private SwitchAnimation fatigueNotification;
    private SettingItemView sensitivityAdjust;
    private SwitchAnimation sightNotification;
    private SwitchAnimation badActionNotification;
    private FaceSettingVM faceSettingVM;
    private ImageView mIvSettingPic;
    private int currentSetting;
    private static final String CAR_CONTROL = "car_control";
    private int[] sensitiveDrawables = {R.drawable.face_fatigue_remind_open_low, R.drawable.face_fatigue_remind_open_middle, R.drawable.face_fatigue_remind_open_high};
    private boolean isHiddenChanged = false; // 是否执行了onHiddenChanged
    private SwitchAnimation safetyDaw;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_car_face, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bindView(view);
        faceSettingVM = ViewModelProviders.of(this).get(FaceSettingVM.class);
        faceSettingVM.setOnValueChangeListener(this);
        initData();
        initEvent();
    }

    private void initEvent() {
        recognizeSystem.setListener(this);
        fatigueNotification.setListener(this);
        sensitivityAdjust.setListener(this);
        sightNotification.setListener(this);
        badActionNotification.setListener(this);
        safetyDaw.setListener(this);
    }

    private void bindView(View view) {
        recognizeSystem = view.findViewById(R.id.switch_face_recognize_system);
        fatigueNotification = view.findViewById(R.id.switch_fatigue_notification);
        sensitivityAdjust = view.findViewById(R.id.item_sensitivity_adjust);
        sensitivityAdjust.setDelayItemSelector(true);
        sightNotification = view.findViewById(R.id.switch_sight_notification);
        badActionNotification = view.findViewById(R.id.switch_bad_action_notification);
        mIvSettingPic = view.findViewById(R.id.iv_setting_pic);
        safetyDaw = view.findViewById(R.id.safety_daw);
        if (!XmCarConfigManager.hasFaceRecognition()) {
            recognizeSystem.setVisibility(View.GONE);
            fatigueNotification.setVisibility(View.GONE);
            sensitivityAdjust.setVisibility(View.GONE);
            sightNotification.setVisibility(View.GONE);
            badActionNotification.setVisibility(View.GONE);
        } else {
            safetyDaw.setVisibility(View.GONE);
        }
    }

    private void initData() {
        if (XmCarConfigManager.hasFaceRecognition()) {
            boolean recognizeSystemState = faceSettingVM.getRecognizeSystemState();
            changeSwitchView(recognizeSystem, recognizeSystemState);
            boolean tiredState = faceSettingVM.getTiredState();
            changeSwitchView(fatigueNotification, tiredState);
            int sensitivityLevel = faceSettingVM.getSensitivityLevel();
            changeItemSelected(sensitivityAdjust, sensitivityLevel);
            boolean distractionState = faceSettingVM.getDistractionState();
            changeSwitchView(sightNotification, distractionState);
            boolean badDriveActionState = faceSettingVM.getBadDriveActionState();
            changeSwitchView(badActionNotification, badDriveActionState);
            if (!isHiddenChanged)
                mIvSettingPic.setImageResource(R.drawable.car_setting_default); // 默认图
        } else {
            boolean safeDawState = faceSettingVM.getDaw();
            changeSwitchView(safetyDaw, safeDawState);
        }
    }

    private void changeItemSelected(SettingItemView itemView, int selectedPosition) {
        itemView.setListener(null);
        itemView.setCheck(selectedPosition);
        itemView.setListener(this);
    }

    private void changeSwitchView(SwitchAnimation switchAnimation, boolean check) {
        if (switchAnimation.isCheck() == check) return;
        switchAnimation.setListener(null);
        switchAnimation.check(check);
        switchAnimation.setListener(this);
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
    public void onSwitch(int viewId, boolean state) {
        switch (viewId) {
            case R.id.switch_face_recognize_system:
                faceSettingVM.setRecognizeSystemState(state);
                break;
            case R.id.switch_fatigue_notification:
                faceSettingVM.setTiredState(state);
                break;
            case R.id.switch_sight_notification:
                faceSettingVM.setDistractionState(state);
                break;
            case R.id.switch_bad_action_notification:
                faceSettingVM.setBadDriveActionState(state);
                break;
            case R.id.safety_daw:
                faceSettingVM.setDAW(state);
                break;
        }
    }

    @Override
    public void onSelect(int viewId, int index) {
        faceSettingVM.setSensivityLevel(index);
    }

    @Override
    public void onRecognizeSystemStateListener(boolean enable) {
        if (enable == recognizeSystem.isCheck())
            return;
        KLog.d(CAR_CONTROL, "人脸识别系统状态: " + enable);
        changeSwitchView(recognizeSystem, enable);
        currentSetting = SettingConstants.FACE_RECOGNIGE_SYSTEM;
        switchSettingPic(enable);
    }

    @Override
    public void onTiredStateListener(boolean enable) {
        if (enable == fatigueNotification.isCheck())
            return;
        KLog.d(CAR_CONTROL, "疲劳检测开关: " + enable);
        changeSwitchView(fatigueNotification, enable);
        currentSetting = SettingConstants.FACE_FATIGUE_NOTIFICATION;
        switchSettingPic(enable);
    }

    @Override
    public void onSensitivityListener(int level) {
        if (level == sensitivityAdjust.getIndex())
            return;
        KLog.d(CAR_CONTROL, "疲劳检测灵敏度: " + level);
        changeItemSelected(sensitivityAdjust, level);
        currentSetting = SettingConstants.FACE_SENSITIVITY_ADJUST;
        switchSettingPic(level);
    }

    @Override
    public void onDistractionStateListener(boolean enable) {
        if (enable == sightNotification.isCheck())
            return;
        KLog.d(CAR_CONTROL, "注意力分散状态: " + enable);
        changeSwitchView(sightNotification, enable);
        currentSetting = SettingConstants.FACE_SIGHT_NOTIFICATION;
        switchSettingPic(enable);
    }

    @Override
    public void onBadDriveActionListener(boolean enable) {
        if (enable == badActionNotification.isCheck())
            return;
        KLog.d(CAR_CONTROL, "不良驾驶行为状态: " + enable);
        changeSwitchView(badActionNotification, enable);
        currentSetting = SettingConstants.FACE_BAD_ATION_NOTIFICATION;
        switchSettingPic(enable);
    }

    @Override
    public void onDAWListener(boolean enable) {
//        if (enable == safetyDaw.isCheck())
//            return;
        KLog.d(CAR_CONTROL, "不良驾驶行为状态: " + enable);
        changeSwitchView(safetyDaw, enable);
        currentSetting = SettingConstants.FACE_DAW;
        switchSettingPic(enable);
    }


    private void switchSettingPic(boolean open) {
        switch (currentSetting) {
            case SettingConstants.FACE_RECOGNIGE_SYSTEM:
                mIvSettingPic.setImageResource(open ? R.drawable.face_recognize_open : R.drawable.face_recognize_close);
                break;
            case SettingConstants.FACE_FATIGUE_NOTIFICATION:
                int level = faceSettingVM.getSensitivityLevel(); // 0 1 2
                mIvSettingPic.setImageResource(open ? sensitiveDrawables[level] : R.drawable.face_fatigue_notification_close);
                break;
            case SettingConstants.FACE_SIGHT_NOTIFICATION:
                mIvSettingPic.setImageResource(open ? R.drawable.face_sight_notification_open : R.drawable.face_sight_notification_close);
                break;
            case SettingConstants.FACE_BAD_ATION_NOTIFICATION:
                mIvSettingPic.setImageResource(open ? R.drawable.face_bad_action_notification_open : R.drawable.face_bad_action_notification_close);
                break;
            case SettingConstants.FACE_DAW:
                mIvSettingPic.setImageResource(open ? R.drawable.safe_driver_attention_remind_open : R.drawable.safe_driver_attention_remind_close);
                break;
        }
    }

    private void switchSettingPic(int index) {
        switch (currentSetting) {
            case SettingConstants.FACE_SENSITIVITY_ADJUST:
                if (index < 0 || index >= sensitiveDrawables.length) return;
                mIvSettingPic.setImageResource(sensitiveDrawables[index]);
                break;
        }

    }
}
