package com.xiaoma.setting.car.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.support.annotation.NonNull;

import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.IVendorExtension;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.setting.R;
import com.xiaoma.setting.common.utils.DebugUtils;
import com.xiaoma.utils.log.KLog;

public class LamplightVM extends AndroidViewModel implements XmCarVendorExtensionManager.ValueChangeListener {
    private static final String TAG = "LamplightVM";
    private static final String CAR_CONTROL_REQ = "car_control_req";
    private Context context;
    private OnValueChange onValueChange;

    public LamplightVM(@NonNull Application application) {
        super(application);
        this.context = application;
        getManager().addValueChangeListener(this);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public LamplightVM initData() {
        return this;
    }

    public IVendorExtension getManager() {
        return XmCarFactory.getCarVendorExtensionManager();
    }

    public boolean setGoHome(boolean state) {
//        int value = state ? SDKConstants.VALUE.GO_HOME_M1 : SDKConstants.VALUE.GO_HOME_OFF;
        int value = state ? SDKConstants.VALUE.GO_HOME_M1_REQ : SDKConstants.VALUE.GO_HOME_OFF_REQ;
        getManager().setModeGoHome(value);
        KLog.d(CAR_CONTROL_REQ, "回家模式开光: " + value);
        return true;
    }

    public int getGoHome() {
        int result = getManager().getModeGoHome();
        return caseGoHomeToIndex(result);
    }

    public int caseGoHomeToIndex(int result) {
        int index = 0;
        switch (result) {
            case SDKConstants.VALUE.GO_HOME_OFF:
                index = -1;
                break;
            case SDKConstants.VALUE.GO_HOME_M1:
                index = 0;
                break;
            case SDKConstants.VALUE.GO_HOME_M2:
                index = 1;
                break;
            case SDKConstants.VALUE.GO_HOME_M3:
                index = 2;
                break;
        }
        return index;
    }

    /**
     *
     * @param on  总开关打开关闭状态
     * @param checkIndex 改开关底下对应的几个选项
     * @return 最终跟车机系统返回状态对应的index
     */
    public int caseGoHomeFinalStatusToIndex(boolean on,int checkIndex){
        if (!on) return -1;
        return checkIndex;
    }

    public boolean setGoHome(int index) {
        DebugUtils.e(TAG, context.getString(R.string.log_back_home) + index);
        getManager().setModeGoHome(castGoHomeToValue(index));
        KLog.d(CAR_CONTROL_REQ, "回家模式选项: " + castGoHomeToValue(index));
        return true;
    }


    public boolean setLeaveLight(boolean state) {
        getManager().setWelcomeLightByRhythm(caseLeaveLightToValue(state));
        return true;
    }

    public boolean getLeaveLight() {
        boolean result = getManager().getWelcomeLightByRhythm();
        return result;
    }

    private boolean caseLeaveLightToState(boolean result) {
        return result == SDKConstants.VALUE.EELCOME_LIGHT_MOTION_ON ? true : false;
    }

    private boolean caseLeaveLightToValue(boolean state) {
        return state ? SDKConstants.VALUE.EELCOME_LIGHT_MOTION_ON : SDKConstants.VALUE.EELCOME_LIGHT_MOTION_OFF;
    }


    public boolean setIndoorLight(int index) {
        DebugUtils.e(TAG, context.getString(R.string.log_indoor_light) + index);
        getManager().setInteriorLightDelay(index);
        KLog.d(CAR_CONTROL_REQ, "室内灯延时: " + index);
        return true;
    }

    public int getIndoorLight() {
        int result = getManager().getInteriorLightDelay();
        return caseInDoorLightToIndex(result);
    }


    private int caseInDoorLightToIndex(int result) {
        int index = -1;
        switch (result) {
            case SDKConstants.VALUE.INTERIOR_LIGHT_DELAY_10S:
                index = 0;
                break;
            case SDKConstants.VALUE.INTERIOR_LIGHT_DELAY_20S:
                index = 1;
                break;
            case SDKConstants.VALUE.INTERIOR_LIGHT_DELAY_30S:
                index = 2;
                break;
        }
        return index;
    }

    private int caseIndoorLightToValue(int index) {
        int value = SDKConstants.VALUE.INTERIOR_LIGHT_DELAY_20S_REQ;
        switch (index) {
            case 0:
                value = SDKConstants.VALUE.INTERIOR_LIGHT_DELAY_10S_REQ;
                break;
            case 1:
                value = SDKConstants.VALUE.INTERIOR_LIGHT_DELAY_20S_REQ;
                break;
            case 2:
                value = SDKConstants.VALUE.INTERIOR_LIGHT_DELAY_30S_REQ;
                break;
        }
        return value;
    }

    public boolean setWelcomeLight(boolean state) {
        DebugUtils.e(TAG, context.getString(R.string.log_welcome_light) + state);
        getManager().setWelcomeLampSwitch(caseWelcomLightToValue(state));
        KLog.d(CAR_CONTROL_REQ, "迎宾灯开关: " + caseWelcomLightToValue(state));
        return true;
    }

    public boolean getWelcomeLight() {
        int result = getManager().getWelcomeLampSwitch();
        return caseWelcomeLightToSwitch(result);
    }

    public boolean caseWelcomeLightToSwitch(int result) {
        boolean state = false;
        if (result == SDKConstants.VALUE.WELCOM_LIGHT_ON) {
            state = true;
        }
        return state;
    }

    private int caseWelcomLightToValue(boolean state) {
        int value = SDKConstants.VALUE.WELCOM_LIGHT_OFF_REQ;
        if (state) {
            value = SDKConstants.VALUE.WELCOM_LIGHT_ON_REQ;
        }
        return value;
    }


    // IHC智能远光 setIHC

    public boolean setIHC(boolean state) {
        getManager().setIHC(caseIhcToValue(state));
        KLog.d(CAR_CONTROL_REQ, "智能远光: " + caseIhcToValue(state));
        return true;
    }

    public boolean getIHC() {
        int result = getManager().getIHC();
        return caseIhcToState(result);
    }

    public boolean caseIhcToState(int result) {
        boolean state = false;
        switch (result) {
            case SDKConstants.VALUE.IHC_ON:
                state = true;
                break;
            case SDKConstants.VALUE.IHC_OFF:
                state = false;
                break;
        }
        return state;
    }

    private int caseIhcToValue(boolean state) {
//        return state ? SDKConstants.VALUE.IHC_ON : SDKConstants.VALUE.IHC_OFF;
        return state ? SDKConstants.VALUE.IHC_ON_REQ : SDKConstants.VALUE.IHC_OFF_REQ;
    }


    //氛围灯亮度
    public boolean setAmbientLightBrightness(int progress) {
        DebugUtils.e(TAG, context.getString(R.string.log_set_ambient_light) + progress);
        getManager().setAmbientLightBrightness(progress);
        return true;
    }

    public int getAmbientLightBrightness() {
        int result = getManager().getAmbientLightBrightness();
        return result;
    }


    //氛围灯开关
    public boolean setAmbientLightSwitch(boolean state) {
//        getManager().setAmbientLightSwitch(state ? SDKConstants.VALUE.ATMOSPHERE_LIGHT_ON : SDKConstants.VALUE.ATMOSPHERE_LIGHT_OFF);
//        getManager().setAmbientLightSwitch(state ? SDKConstants.VALUE.ATMOSPHERE_LIGHT_ON_REQ :  SDKConstants.VALUE.ATMOSPHERE_LIGHT_OFF_REQ);
        getManager().setAmbientLightSwitch(state ? SDKConstants.VALUE.ATMOSPHERE_LIGHT_ON_REQ : SDKConstants.VALUE.ATMOSPHERE_LIGHT_OFF_REQ );
        KLog.d(CAR_CONTROL_REQ, "氛围灯开关: " + state);
        return true;
    }

    public boolean getAmbientLightSwitch() {
        int result = getManager().getAmbientLightSwitch();
        return caseAmbientLightSwitchToState(result);
    }

    public boolean caseAmbientLightSwitchToState(int result) {
        boolean state = false;
        switch (result) {
            case SDKConstants.VALUE.ATMOSPHERE_LIGHT_ON:
                state = true;
                break;
            case SDKConstants.VALUE.ATMOSPHERE_LIGHT_OFF:
                state = false;
                break;
        }
        return state;
    }

    //音乐情景随动开关
    public boolean setSceneLightSwitch(boolean state) {
        getManager().setMusicSceneFollow(state);
        KLog.d(CAR_CONTROL_REQ, "音乐情景随动开关: " + state);
        return true;
    }

    public boolean getSceneLightSwitch() {
        boolean result = getManager().getMusicSceneFollow();
        return result;
    }

    public boolean setAmbientLightColor(int index) {
        getManager().setAmbientLightColor(caseAmbientLightColorToValue(index));
        KLog.d(CAR_CONTROL_REQ, "音乐情景随动选项: " + caseAmbientLightColorToValue(index));
        return true;
    }

    public int getAmbientLightColor() {
        int result = getManager().getAmbientLightColor();
        return caseAmbientLightColorToIndex(result);
    }

    public int caseAmbientLightColorToIndex(int result) {
        return result;
    }


    public boolean setLaneChangeFlicker(int index) {
        getManager().setLaneChangeFlicker(caseLaneChangeToValue(index));
        return true;
    }

    public int getLaneChangeFlicker() {
        int result = getManager().getLaneChangeFlicker();
        return caseLaneChangeToIndex(result);
    }

    private int caseLaneChangeToIndex(int result) {
        int index = -1;
        switch (result) {
            case SDKConstants.VALUE.LANE_CHANGE_TIME_1:
                index = 0;
                break;
            case SDKConstants.VALUE.LANE_CHANGE_TIME_3:
                index = 1;
                break;
        }
        return index;
    }


    private int caseLaneChangeToValue(int index) {
        int value = -1;
        switch (index) {
            case 0:
                value = SDKConstants.VALUE.LANE_CHANGE_TIME_1;
                break;
            case 1:
                value = SDKConstants.VALUE.LANE_CHANGE_TIME_3;
                break;
        }
        return value;
    }

    private int caseAmbientLightColorToValue(int index) {
        int value = SDKConstants.VALUE.MUSIC_FOLLOW_COLOR_12;
        switch (index) {
            case 0:
                value = SDKConstants.VALUE.MUSIC_FOLLOW_COLOR_1;
                break;
            case 1:
                value = SDKConstants.VALUE.MUSIC_FOLLOW_COLOR_2;
                break;
            case 2:
                value = SDKConstants.VALUE.MUSIC_FOLLOW_COLOR_3;
                break;
            case 3:
                value = SDKConstants.VALUE.MUSIC_FOLLOW_COLOR_4;
                break;
            case 4:
                value = SDKConstants.VALUE.MUSIC_FOLLOW_COLOR_5;
                break;
            case 5:
                value = SDKConstants.VALUE.MUSIC_FOLLOW_COLOR_6;
                break;
            case 6:
                value = SDKConstants.VALUE.MUSIC_FOLLOW_COLOR_7;
                break;
            case 7:
                value = SDKConstants.VALUE.MUSIC_FOLLOW_COLOR_8;
                break;
            case 8:
                value = SDKConstants.VALUE.MUSIC_FOLLOW_COLOR_9;
                break;
            case 9:
                value = SDKConstants.VALUE.MUSIC_FOLLOW_COLOR_10;
                break;
            case 10:
                value = SDKConstants.VALUE.MUSIC_FOLLOW_COLOR_11;
                break;
            case 11:
                value = SDKConstants.VALUE.MUSIC_FOLLOW_COLOR_12;
                break;
        }
        return value;
    }

    private int castGoHomeToValue(int index) {
        int result = SDKConstants.VALUE.GO_HOME_OFF;
        switch (index) {
            case -1:
                result = SDKConstants.VALUE.GO_HOME_M1_REQ;
                break;
            case 0:
                result = SDKConstants.VALUE.GO_HOME_M2_REQ;
                break;
            case 1:
                result = SDKConstants.VALUE.GO_HOME_M3_REQ;
                break;
        }
        return result;
    }

    @Override
    public void onChange(int id, Object value) {
        if (onValueChange != null) {
            onValueChange.onChange(id, value);
        }
    }

    public void setOnValueChange(OnValueChange onValueChange) {
        this.onValueChange = onValueChange;
    }

    public interface OnValueChange {
        void onChange(int id, Object value);
    }

}
