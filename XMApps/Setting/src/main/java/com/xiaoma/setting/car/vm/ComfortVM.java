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

public class ComfortVM extends AndroidViewModel implements ICarEvent, XmCarVendorExtensionManager.ValueChangeListener {
    private static final String TAG = "ConvenientVM";
    private static final String CAR_CONTROL_REQ = "car_control_req";
    private Context context;
    private OnValueChange onValueChange;

    public ComfortVM(@NonNull Application application) {
        super(application);
        this.context = application;
        getManager().addValueChangeListener(this);
        XmCarEventDispatcher.getInstance().registerEvent(this);
    }

    public IVendorExtension getManager() {
        return XmCarFactory.getCarVendorExtensionManager();
    }

    public ICarCabin getCarBinManager() {
        return XmCarFactory.getCarCabinManager();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public ComfortVM initData() {
        return this;
    }

    //随速闭锁
    public boolean setSpeedLockControl(boolean state) {
        getManager().setSpeedAutoLock(state);
        KLog.d(CAR_CONTROL_REQ, "随速闭锁: " + state);
        return true;
    }


    public boolean getSpeedLockControl() {
        boolean result = getManager().getSpeedAutoLock();
        return result;
    }


    //锁车后视镜自动折叠
    public boolean setRearviewMirror(boolean state) {
        DebugUtils.e(TAG, context.getString(R.string.log_rearview_mirror) + state);
        getCarBinManager().setRearviewMirror(state);
        KLog.d(CAR_CONTROL_REQ, "锁车外后视镜自动折叠: " + state);
        return true;
    }

    public boolean getRearviewMirror() {
        boolean result = getCarBinManager().getRearviewMirror();
        return result;
    }

    //座椅迎宾退让
    public boolean setWelcomeSeat(boolean state) {
        DebugUtils.e(TAG, context.getString(R.string.log_seat) + state);
        getManager().setWelcomeSeat(state);
        KLog.d(CAR_CONTROL_REQ, "座椅迎宾退让: " + state);
        return true;
    }

    private int caseWelcomeSeatToValue(boolean state) {
        return state ? SDKConstants.VALUE.WELCOME_SEAT_ON : SDKConstants.VALUE.WELCOME_SEAT_OFF;
    }

    public boolean getWelcomeSeat() {
        boolean result = getManager().getWelcomeSeat();
        return result;
    }

    private boolean caseWelcomeSeatToSwitch(int result) {
        if (result == SDKConstants.VALUE.WELCOME_SEAT_ON) {
            return true;
        }
        return false;
    }

    //锁车自动关窗
    public boolean setSelfClosingWindow(boolean state) {
        DebugUtils.e(TAG, context.getString(R.string.log_auto_close_window) + state);
        getManager().setSelfClosingWindow(state);
        KLog.d(CAR_CONTROL_REQ, "锁车自动关窗: " + state);
        return true;
    }

    public boolean getSelfClosingWindow() {
        boolean result = getManager().getSelfClosingWindow();
        return result;
    }

    public boolean getLeaveAutomaticLock() {
        boolean result = getManager().getLeaveAutoLock();
        return result;
    }

    //离车自动落锁
    public void setLeaveAutomaticLock(boolean state) {
        getManager().setLeaveAutoLock(state);
        KLog.d(CAR_CONTROL_REQ, "离车自动解锁: " + state);
    }

    public boolean getTrunk() {
        boolean result = getManager().getAutomaticTrunk();
        return result;
    }

    //智能行李箱
    public void setTrunk(boolean state) {
        DebugUtils.e(TAG, context.getString(R.string.log_trunk) + state);
        getManager().setAutomaticTrunk(state ? SDKConstants.VALUE.TRUNK_ON : SDKConstants.VALUE.TRUNK_OFF);
        KLog.d(CAR_CONTROL_REQ, "行李箱智能开启: " + state);
    }

    public int getRemoteControlMode() {
        int result = getCarBinManager().getRemoteControlUnlockMode();
        return result == SDKConstants.VALUE.LOCK_ALL_DOOR ? 0 : 1;
    }

    //遥控解锁模式
    public void setRemoteControlMode(int index) {
        getCarBinManager().setRemoteControlUnlockMode(index);
        KLog.d(CAR_CONTROL_REQ, "遥控解锁模式: " + index);
    }

    @Override
    public void onCarEvent(CarEvent event) {
        if (onValueChange != null) {
            onValueChange.onChange(event.id, event.value);
        }
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
