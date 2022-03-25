package com.xiaoma.setting.sdk.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.IVendorExtension;

/**
 * @Author: ZiXu Huang
 * @Data: 2019/6/13
 * @Desc:
 */
public class FaceSettingVM extends AndroidViewModel {
    private OnValueChangeListener listener;

    public FaceSettingVM(@NonNull Application application) {
        super(application);
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
    }

    //获取疲劳检测状态
    public boolean getTiredState() {
        int tiredState = getManager().getTiredState();
        return tiredState == SDKConstants.VALUE.CAN_ON_OFF2_ON;
    }

    //设置疲劳检测状态
    public void setTiredState(boolean enable) {
        getManager().setTiredState(enable ? SDKConstants.VALUE.CAN_ON_OFF2_ON_REQ : SDKConstants.VALUE.CAN_ON_OFF2_OFF_REQ);
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
    }

    // 获取不良驾驶行为状态
    public boolean getBadDriveActionState() {
        int badDriverState = getManager().getBadDriverState();
        return badDriverState == SDKConstants.VALUE.CAN_ON_OFF2_ON;
    }

    // 设置不良驾驶行为状态
    public void setBadDriveActionState(boolean enable) {
        getManager().setBadDriverState(enable ? SDKConstants.VALUE.CAN_ON_OFF2_ON_REQ : SDKConstants.VALUE.CAN_ON_OFF2_OFF_REQ);
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
    }
}
