package com.xiaoma.setting.car.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.support.annotation.NonNull;

import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.ICarCabin;
import com.xiaoma.carlib.manager.ICarEvent;
import com.xiaoma.carlib.manager.IVendorExtension;
import com.xiaoma.carlib.manager.XmCarEventDispatcher;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.carlib.model.CarEvent;
import com.xiaoma.setting.R;
import com.xiaoma.setting.common.utils.DebugUtils;
import com.xiaoma.utils.log.KLog;

public class SafetyVM extends AndroidViewModel implements XmCarVendorExtensionManager.ValueChangeListener, ICarEvent {
    private static final String TAG = SafetyVM.class.getSimpleName();
    private static final String CAR_CONTROL_REQ = "car_control_req";
    private Context context;
    private OnValueChange onValueChange;

    public SafetyVM(@NonNull Application application) {
        super(application);
        this.context = application;
        getManager().addValueChangeListener(this);
        XmCarEventDispatcher.getInstance().registerEvent(this);
    }

    public IVendorExtension getManager() {
        return XmCarFactory.getCarVendorExtensionManager();
    }

    public ICarCabin getCarCabinManager() {
        return XmCarFactory.getCarCabinManager();
    }


    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public SafetyVM initData() {
        return this;
    }

    /**
     * 主动制动 低中高
     *
     * @param state
     * @return
     */
    public boolean setBrake(int state) {
        DebugUtils.e(TAG, context.getString(R.string.log_brake) + state);
        getManager().setFcwAebSwitch(state);
        return true;
    }

    /**
     * 道路保持 LDW,LKA1 LKA2
     *
     * @param index
     * @return
     */
    public boolean setLaneKeep(int index) {
        DebugUtils.e(TAG, context.getString(R.string.log_lane_keep) + index);
        getManager().setLKA(index);
        KLog.d(CAR_CONTROL_REQ, "车道保持: " + index);
        return true;
    }

    public int getLaneKeep() {
        int result = getManager().getLKA();
        return caseKeepToIndex(result);
    }

    public int caseKeepToIndex(int result) {
        int index = 1;
        switch (result) {
            case SDKConstants.VALUE.LKA_MODE_LDW:
                index = 0;
                break;
            case SDKConstants.VALUE.LKA_MODE_LKA:
                index = 1;
                break;
            case SDKConstants.VALUE.LKA_MODE_LC:
                index = 2;
                break;
        }
        return index;
    }


    //交通标志识别
    public boolean getISA() {
        int result = getManager().getISA();
        return result == SDKConstants.VALUE.IFC_ON;
    }

    public boolean setISA(boolean state) {
        getManager().setISA(caseIsaToValue(state));
        KLog.d(CAR_CONTROL_REQ, "交通标志识别: " + caseIsaToValue(state));
        return true;
    }

    private int caseIsaToValue(boolean state) {
        return state ? SDKConstants.VALUE.IFC_ACTIVE : SDKConstants.VALUE.IFC_OFF;
    }

    /**
     * 后排安全带提醒
     *
     * @param state
     * @return
     */
    public boolean setSeatBelt(boolean state) {
        getCarCabinManager().setRearBeltWorningSwitch(caseSeatBeltToValue(state));
        /*-------测试代码---------*/
//        int i = XmCarFactory.getSystemManager().operateTelePhoneICall(state ? SDKConstants.CallOperation.DIAL : SDKConstants.CallOperation.ANSWER);
//        KLog.d("hzx", "ICall: " + i);
        KLog.d(CAR_CONTROL_REQ, "后排安全带未系提醒: " + caseSeatBeltToValue(state));
        return true;
    }

    private int caseSeatBeltToValue(boolean state) {
        return state ? SDKConstants.VALUE.REAR_BELT_ON_REQ : SDKConstants.VALUE.REAR_BELT_OFF_REQ;
    }

    public boolean getSeatBelt() {
        int result = getCarCabinManager().getRearBeltWorningSwitch();
        return caseSeatBelt(result);
    }

    public boolean caseSeatBelt(int result) {
        return result == SDKConstants.VALUE.REAR_BELT_ON;
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

    private int caseDawToValue(boolean state) {
        return state ? SDKConstants.VALUE.DAW_ON_REQ : SDKConstants.VALUE.DAW_OFF_REQ;
    }

    public boolean getDaw() {
        int result = getManager().getDAW();
        return caseDawToBoolean(result);
    }

    public boolean caseDawToBoolean(int result) {
        return result == SDKConstants.VALUE.DAW_ON || result == SDKConstants.VALUE.DAW_STANDBY;
    }


    public void setResetTiretPressure(int value) {
        getManager().setResetTiretPressure(value);
    }

    //自动夹紧EPB
    public boolean setElectronicBrake(boolean state) {
        getManager().setEPB(caseElectronicBrakeToValue(state));
        KLog.d(CAR_CONTROL_REQ, "电子驻车自动夹紧: " + caseElectronicBrakeToValue(state));
        return true;
    }

    private int caseElectronicBrakeToValue(boolean state) {
        return state ? SDKConstants.VALUE.EPB_ON_REQ : SDKConstants.VALUE.EPB_OFF_REQ;
    }

    public boolean getElectronicBrake() {
        int result = getManager().getEPB();
        return result == SDKConstants.VALUE.EPB_ON;
    }


    public int getFcwSensitivity() {
        int value = caseFcwToIndex(getManager().getFcwSensitivity());
        return value;
    }

    // 获取前防撞预警开关状态
    public boolean getFcwAebSwitch() {
//        return getManager().getFcwAebSwitch() < 0 ? false : true;
        int fcwAebSwitch = getManager().getFcwAebSwitch();
        return fcwAebSwitch == SDKConstants.VALUE.FCW_ON || fcwAebSwitch == SDKConstants.VALUE.FCW_STANDBY;
    }

    public boolean setFcwSensitivity(int index) {
        getManager().setFcwSensitivity(caseFcwToValue(index));
        KLog.d(CAR_CONTROL_REQ, "前防撞预警: " + caseFcwToValue(index));
        return true;
    }

    private int caseFcwToValue(int index) {
        int value = -1;
        switch (index) {
            case 0:
                value = SDKConstants.VALUE.FCW_LOW_REQ;
                break;
            case 1:
                value = SDKConstants.VALUE.FCW_NORMAL_REQ;
                break;
            case 2:
                value = SDKConstants.VALUE.FCW_HIGH_REQ;
                break;
        }
        return value;
    }


    //前防障预警/主动制动灵敏度
    public boolean setFCwAebSwitch(boolean state) {
        KLog.d("SafetyVM", caseFcwAebSwitch(state));
        getManager().setFcwAebSwitch(caseFcwAebSwitch(state));
        KLog.d(CAR_CONTROL_REQ, "前防撞预警: " + caseFcwAebSwitch(state));
        return false;
    }

    private int caseFcwAebSwitch(boolean state) {
        int value = state ? SDKConstants.VALUE.FCW_ON_REQ : SDKConstants.VALUE.FCW_OFF_REQ;
        return value;
    }

    public int caseFcwToIndex(int result) {
        int index = -1;
        switch (result) {
            case SDKConstants.VALUE.FCW_LOW:
                index = 0;
                break;
            case SDKConstants.VALUE.FCW_NORMAL:
                index = 1;
                break;
            case SDKConstants.VALUE.FCW_HIGH:
                index = 2;
                break;
        }
        return index;
    }


    //车道偏离
    public int getLdwSensitivity() {
        return caseLdwToIndex(getManager().getLdwSensitivity());
    }

    private int caseLdwToIndex(int result) {
        int index = -1;
        switch (result) {
            case SDKConstants.VALUE.LDW_LOW:
                index = 0;
                break;
            case SDKConstants.VALUE.LDW_NORMAL:
                index = 1;
                break;
            case SDKConstants.VALUE.LDW_HIGH:
                index = 2;
                break;
        }
        return index;
    }

    public boolean setLdwSensitivity(int index) {
        getManager().setLdwSensitivity(caseLdwToValue(index));
        KLog.d(CAR_CONTROL_REQ, "车道偏离警示: " + caseLdwToValue(index));
        return true;
    }

    public void setRearviewMirror(boolean enable){
        getManager().setRearviewMirror(enable? SDKConstants.VALUE.REAR_VEIW_ENABLE_REQ: SDKConstants.VALUE.REAR_VEIW_DISABLE_REQ );
        KLog.d(CAR_CONTROL_REQ, "智能安全外后视镜开关: " + enable);
    }

    public void setMarkMirrorLeft(int id){
        getManager().setMarkMirrorLeft(id);
        KLog.d(CAR_CONTROL_REQ, "左后视镜随动位置记录: ");
    }

    public void setMarkMirrorRight(int value){
        getManager().setMarkMirrorRight(value);
        KLog.d(CAR_CONTROL_REQ, "右后视镜随动位置记录: ");
    }

    public boolean getRearviewMirrorEnable(){
        int rearViewMirrorEnable = getManager().getRearViewMirrorEnable();
        boolean result = rearViewMirrorEnable ==  SDKConstants.VALUE.REAR_VEIW_ENABLE;
        KLog.d("car_control_get", "是否支持外后视镜随动: " + result);
        return result;
    }

    private int caseLdwToValue(int index) {
        int value = -1;
        switch (index) {
            case 0:
                value = SDKConstants.VALUE.LDW_LOW_REQ;
                break;
            case 1:
                value = SDKConstants.VALUE.LDW_NORMAL_REQ;
                break;
            case 2:
                value = SDKConstants.VALUE.LDW_HIGH_REQ;
                break;
        }
        return value;
    }

    @Override
    public void onChange(int id, Object value) {
        if (onValueChange != null) {
            onValueChange.onChange(id, value);
        }
    }

    @Override
    public void onCarEvent(CarEvent event) {
        if (onValueChange != null) {
            onValueChange.onChange(event.id, event.value);
        }
    }

    public void setOnValueChange(OnValueChange onValueChange) {
        this.onValueChange = onValueChange;
    }

    public interface OnValueChange {
        void onChange(int id, Object value);
    }
}
