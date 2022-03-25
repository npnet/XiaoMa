package com.xiaoma.setting.sdk.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.IVendorExtension;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.utils.log.KLog;

/**
 * @Author: ZiXu Huang
 * @Data: 2019/6/13
 * @Desc:
 */
public class FaceSettingVM extends AndroidViewModel implements XmCarVendorExtensionManager.ValueChangeListener {
    private OnValueChangeListener listener;
    private static final String CAR_CONTROL_REQ = "car_control_req";

    public FaceSettingVM(@NonNull Application application) {
        super(application);
        getManager().addValueChangeListener(this);
    }

    public IVendorExtension getManager() {
        return XmCarFactory.getCarVendorExtensionManager();
    }

    //获取人脸识别系统状态
    public boolean getRecognizeSystemState() {
        int recognizeAvailable = getManager().getRecognizeAvailable();
        return recognizeAvailable == SDKConstants.VALUE.CAN_ON_OFF2_ON;
    }

    //设置人脸识别系统开关
    public void setRecognizeSystemState(boolean enable) {
        getManager().setRecognizeAvailable(enable ? SDKConstants.VALUE.CAN_ON_OFF2_ON_REQ : SDKConstants.VALUE.CAN_ON_OFF2_OFF_REQ);
        KLog.d(CAR_CONTROL_REQ, "人脸识别系统开关: " + enable);
    }

    //获取疲劳检测状态
    public boolean getTiredState() {
        int tiredState = getManager().getTiredState();
        return tiredState == SDKConstants.VALUE.CAN_ON_OFF2_ON;
    }

    //设置疲劳检测状态
    public void setTiredState(boolean enable) {
        getManager().setTiredState(enable ? SDKConstants.VALUE.CAN_ON_OFF2_ON_REQ : SDKConstants.VALUE.CAN_ON_OFF2_OFF_REQ);
        KLog.d(CAR_CONTROL_REQ, "疲劳检测开关: " + enable);
    }

    // 获取灵敏度
    public int getSensitivityLevel() {
        int index = sensitivityToIndex(getManager().getSensitivityLevel());
        return index;
    }

    private int sensitivityToIndex(int sensitivityLevel) {
        int index = -1;
        switch (sensitivityLevel) {
            case SDKConstants.VALUE.TIRED_LOW:
                index = 0;
                break;
            case SDKConstants.VALUE.TIRED_NORMAL:
                index = 1;
                break;
            case SDKConstants.VALUE.TIRED_HIGH:
                index = 2;
                break;
        }
        return index;
    }

    //设置灵敏度等级
    public void setSensivityLevel(int level) {
        getManager().setSensivityLevel(getSensivityLevel(level));
        KLog.d(CAR_CONTROL_REQ, "疲劳检测灵敏度: " + level);
    }

    private int getSensivityLevel(int index) {
        int level = -1;
        switch (index) {
            case 0:
                level = SDKConstants.VALUE.TIRED_LOW_REQ;
                break;
            case 1:
                level = SDKConstants.VALUE.TIRED_NORMAL_REQ;
                break;
            case 2:
                level = SDKConstants.VALUE.TIRED_HIGH_REQ;
                break;
        }
        return level;
    }

    // 获取视野分散状态
    public boolean getDistractionState() {
        int distractionState = getManager().getDistractionState();
        return distractionState == SDKConstants.VALUE.CAN_ON_OFF2_ON;
    }

    // 设置视野分散状态
    public void setDistractionState(boolean enable) {
        getManager().setDistractionState(enable ? SDKConstants.VALUE.CAN_ON_OFF2_ON_REQ : SDKConstants.VALUE.CAN_ON_OFF2_OFF_REQ);
        KLog.d(CAR_CONTROL_REQ, "视野分散: " + enable);
    }

    // 获取不良驾驶行为状态
    public boolean getBadDriveActionState() {
        int badDriverState = getManager().getBadDriverState();
        return badDriverState == SDKConstants.VALUE.CAN_ON_OFF2_ON;
    }

    // 设置不良驾驶行为状态
    public void setBadDriveActionState(boolean enable) {
        getManager().setBadDriverState(enable ? SDKConstants.VALUE.CAN_ON_OFF2_ON_REQ : SDKConstants.VALUE.CAN_ON_OFF2_OFF_REQ);
        KLog.d(CAR_CONTROL_REQ, "不良驾驶行为: " + enable);
    }

    /**
     * 驾驶员注意力提醒
     *
     * @param state
     * @return
     */
    public boolean setDAW(boolean state) {
        getManager().setDAW(caseDawToValue(state));
        /*-------测试代码--------*/
//        int i = XmCarFactory.getSystemManager().operateTelePhoneBCall(state ? SDKConstants.CallOperation.DIAL : SDKConstants.CallOperation.ANSWER);
//        KLog.d("hzx", "BCall: " + i);
        KLog.d(CAR_CONTROL_REQ, "驾驶员注意力提醒: " + caseDawToValue(state));
        return true;
    }

    public boolean getDaw() {
        int result = getManager().getDAW();
        return caseDawToBoolean(result);
    }

    public boolean caseDawToBoolean(int result) {
        return result == SDKConstants.VALUE.DAW_ON || result == SDKConstants.VALUE.DAW_STANDBY;
    }

    private int caseDawToValue(boolean state) {
        return state ? SDKConstants.VALUE.DAW_ON_REQ : SDKConstants.VALUE.DAW_OFF_REQ;
    }

    @Override
    public void onChange(int id, Object value) {
        if (listener == null) return;
        switch (id) {
            case SDKConstants.ID_DMS_STATE: //人脸识别系统
                listener.onRecognizeSystemStateListener((int) value == SDKConstants.VALUE.CAN_ON_OFF2_ON);
                break;
            case SDKConstants.ID_TIRED_REMIND_STATE: //疲劳检测
                listener.onTiredStateListener((int) value == SDKConstants.VALUE.CAN_ON_OFF2_ON);
                break;
            case SDKConstants.ID_DISTRACTION_REMIND_STATE: // 视野分散
                listener.onDistractionStateListener((int) value == SDKConstants.VALUE.CAN_ON_OFF2_ON);
                break;
            case SDKConstants.ID_BAD_DRIVING_STATE: // 不良驾驶行为
                listener.onBadDriveActionListener((int) value == SDKConstants.VALUE.CAN_ON_OFF2_ON);
                break;
            case SDKConstants.ID_TIRED_SENSITIVE: //人脸识别灵敏度
                listener.onSensitivityListener(sensitivityToIndex((int) value));
                break;
            case SDKConstants.DAW:  // 驾驶员状态监测
                listener.onDAWListener((int) value == SDKConstants.VALUE.DAW_ON || (int) value == SDKConstants.VALUE.DAW_STANDBY);
                break;

        }

    }

    public void setOnValueChangeListener(OnValueChangeListener listener) {
        this.listener = listener;
    }

    public interface OnValueChangeListener {

        void onRecognizeSystemStateListener(boolean enable);

        void onTiredStateListener(boolean enable);

        void onSensitivityListener(int level);

        void onDistractionStateListener(boolean enable);

        void onBadDriveActionListener(boolean enable);

        void onDAWListener(boolean enable);
    }
}
