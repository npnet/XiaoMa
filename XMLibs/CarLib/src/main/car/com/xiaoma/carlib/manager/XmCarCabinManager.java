package com.xiaoma.carlib.manager;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.VehicleAreaDoor;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.cabin.CarCabinManager;
import android.car.hardware.vendor.CanOnOff2;
import android.car.hardware.vendor.DoorUnlock;
import android.os.IBinder;
import android.util.MutableInt;

import com.xiaoma.carlib.R;
import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.model.CarEvent;
import com.xiaoma.carlib.utils.LogUtils;
import com.xiaoma.utils.log.KLog;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

/**
 * @author: iSun
 * @date: 2018/10/22 0022
 */
public class XmCarCabinManager extends BaseCarManager<CarCabinManager> implements ICarCabin {
    private static final String TAG = XmCarCabinManager.class.getSimpleName();
    private static final String SERVICE_NAME = Car.CABIN_SERVICE;
    private static XmCarCabinManager instance;
    private CountDownLatch errorLatch = new CountDownLatch(1);
    private MutableInt propertyIdReceived = new MutableInt(0);
    private MutableInt areaIdReceived = new MutableInt(0);
    private static final int SKY_WINDOW_OFF = 0;
    private static final int SUNSHADE_OFF = 0;
    private CarCabinManager.CarCabinEventCallback callBack = new CarCabinManager.CarCabinEventCallback() {
        @Override
        public void onChangeEvent(CarPropertyValue carPropertyValue) {
            changeEvent(carPropertyValue);
        }

        @Override
        public void onErrorEvent(int propertyId, int area) {
            propertyIdReceived.value = propertyId;
            areaIdReceived.value = area;
            errorLatch.countDown();
        }
    };
    public static final Integer[] windowBase = new Integer[]{0x7E, 0x7E, 0x7E, 0x7E};

    public static XmCarCabinManager getInstance() {
        if (instance == null) {
            synchronized (XmCarCabinManager.class) {
                if (instance == null) {
                    instance = new XmCarCabinManager();
                }
            }
        }
        return instance;
    }

    private XmCarCabinManager() {
        super(SERVICE_NAME);
    }

    private <E> E getValue(Class<E> clazz, int propId, int area) {
        LogUtils.e(TAG, " get value:" + propId);
        E result = getDefaultValue(clazz);
        CarCabinManager manager = getManager();
        if (manager != null) {
            try {
                if (clazz.equals(Integer.class)) {
                    result = clazz.cast(manager.getIntProperty(propId, area));
                } else if (clazz.equals(Boolean.class)) {
                    result = clazz.cast(manager.getBooleanProperty(propId, area));
                } else if (clazz.equals(Float.class)) {
                    result = clazz.cast(manager.getFloatProperty(propId, area));
                } else if (clazz.equals(Integer[].class)) {
                    //目前只有获取车窗状态的几个接口是走的getProperty并强转为int[]，后续需要获取强转为int[]的请验证是否也是getProperty
                    result = clazz.cast(manager.getProperty(clazz, propId, area));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            LogUtils.e(TAG, " manager is null");
        }
        return result;
    }

    private <E> void setValue(Class<E> clazz, int propId, int area, E value) {
        LogUtils.e(TAG, " set value:" + propId);
        CarCabinManager manager = getManager();
        if (manager != null) {
            try {
                if (clazz.equals(Integer.class)) {
                    manager.setIntProperty(propId, area, (Integer) value);
                } else if (clazz.equals(Boolean.class)) {
                    manager.setBooleanProperty(propId, area, (Boolean) value);
                } else if (clazz.equals(Float.class)) {
                    manager.setFloatProperty(propId, area, (Float) value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            LogUtils.e(TAG, " manager is null");
        }

    }

    private void setIntArrayValue(int propId, int area, Integer[] value) {
        CarCabinManager manager = getManager();
        if (manager != null) {
            try {
                manager.setIntArrayProperty(propId, area, value);
            } catch (CarNotConnectedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCarServiceConnected(IBinder binder) {
        super.onCarServiceConnected();
        //服务连接 注册callBack
        initCarEvent();
    }

    @Override
    public void onCarServiceDisconnected() {
        super.onCarServiceDisconnected();
        //服务断开xc
    }

    private void initCarEvent() {
        if (getManager() != null) {
            try {
                getManager().registerCallback(callBack);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void changeEvent(CarPropertyValue carPropertyValue) {
        XmCarEventDispatcher.getInstance().dispatcherEvent(carPropertyToXmEvent(carPropertyValue));
        LogUtils.e(TAG, "changeEvent key id:" + carPropertyValue.getPropertyId() + ":" + carPropertyValue.getValue());
    }

    private CarEvent carPropertyToXmEvent(CarPropertyValue carPropertyValue) {
        return new CarEvent(carPropertyValue.getPropertyId(), carPropertyValue.getAreaId(), carPropertyValue.getValue());
    }

    //天窗控制
    @Override
    public void setTopWindowPos(int pos) {
        LogUtils.e(TAG, " setTopWindowPos:" + pos);
        setValue(Integer.class, SDKConstants.ID_SPEECH_SMA_SLIDE_POS, SDKConstants.Area.SEAT_ALL, pos);
    }

    @Override
    public int getTopWindowPos() {
        LogUtils.e(TAG, " getTopWindowPos:");
        int pos = getValue(Integer.class, SDKConstants.ID_SMA_POS, SDKConstants.Area.SEAT_ALL);
        return pos;
    }

    //打开全车窗（透气）
    @Override
    public void setAllWindowPos(int pos) {
        LogUtils.e(TAG, " setAllWindowPos:" + pos);
        setValue(Integer.class, SDKConstants.ID_WINDOW_POS, SDKConstants.Area.SEAT_ALL, pos);
    }

    //遮阳帘控制
    @Override
    public void setUmbrellaPos(int pos) {
        LogUtils.e(TAG, " setUmbrellaPos:" + pos);
        setValue(Integer.class, SDKConstants.ID_SPEECH_SSM_SLIDE_POS, SDKConstants.Area.SEAT_ALL, pos);
    }

    @Override
    public int getUmbrellaPos() {
        LogUtils.e(TAG, " getUmbrellaPos:");
        int pos = getValue(Integer.class, SDKConstants.ID_SSM_POS, SDKConstants.Area.SEAT_ALL);
        return pos;
    }

    @Override
    public boolean getWindowRearHeat() {
        boolean state = getValue(Boolean.class, SDKConstants.ID_SPEECH_REAR_WIN_HEAT, SDKConstants.Area.SEAT_ALL);
        return state;
    }

    //打开/关闭司机门车锁
    @Override
    public void setDoorLock(int state) {
        LogUtils.e(TAG, " setDoorLock:state" + state);
        setValue(Integer.class, SDKConstants.ID_SPEECH_DOOR_LOCK, VehicleAreaDoor.DOOR_ROW_1_LEFT, state);
    }

    @Override
    public boolean getDoorLock() {
        boolean state = getValue(Boolean.class, SDKConstants.ID_SPEECH_DOOR_LOCK, VehicleAreaDoor.DOOR_ROW_1_LEFT);
        return state;
    }

    //打开/关闭全车门锁
    @Override
    public void setAllDoorLock(int state) {
        LogUtils.e(TAG, " setDoorLock:state" + state);
        setValue(Integer.class, SDKConstants.ID_SPEECH_DOOR_LOCK, SDKConstants.Area.SEAT_ALL, state);
    }

    @Override
    public boolean getAllDoorLock() {
        boolean state = getValue(Boolean.class, SDKConstants.ID_SPEECH_DOOR_LOCK, SDKConstants.Area.SEAT_ALL);
        return state;
    }

    //打开后备箱
    @Override
    public void setBackDoorLock(int state) {
        LogUtils.e(TAG, " setDoorLock:state" + state);
        setValue(Integer.class, SDKConstants.ID_SPEECH_LUGGAGE_DOOR, SDKConstants.Area.SEAT_ALL, state);
    }

    @Override
    public boolean getBackDoorLock() {
        boolean state = getValue(Boolean.class, SDKConstants.ID_LUGGAGE_DOOR_STATE, SDKConstants.Area.SEAT_ALL);
        LogUtils.e(TAG, " getBackDoorLock:state" + state);
        return state;
    }

    //打开/关闭司机车窗(0-100)
    @Override
    public void setLeftWindowLock(int pos) {
        LogUtils.e(TAG, " setLeftWindowLock pos:" + pos);
        Integer[] realValue = windowBase.clone();
        realValue[0] = pos;
        LogUtils.e(TAG, " setLeftWindowLock realValue:" + Arrays.toString(realValue));
        setIntArrayValue(SDKConstants.ID_SPEECH_FL_WIN, SDKConstants.Area.SEAT_ALL, realValue);
//        setIntArrayValue(SDKConstants.ID_SPEECH_FL_WIN, SDKConstants.Area.SEAT_ALL, realValue);
    }

    //获取司机车窗状态
    @Override
    public int getLeftWindowLock() {
        Integer[] value = getValue(Integer[].class, SDKConstants.ID_FL_WIN_DOOR_STATE, SDKConstants.Area.SEAT_ALL);
        int pos = value.length > 1 ? value[1] : 0;
        LogUtils.e(TAG, " getLeftWindowLock:state" + pos);
        return pos;
    }

    //打开/关闭副司机车窗
    @Override
    public void setRightWindowLock(int pos) {
        LogUtils.e(TAG, " setRightWindowLock pos:" + pos);
        Integer[] realValue = windowBase.clone();
        realValue[1] = pos;
        LogUtils.e(TAG, " setRightWindowLock realValue:" + Arrays.toString(realValue));
        setIntArrayValue(SDKConstants.ID_SPEECH_FR_WIN, SDKConstants.Area.SEAT_ALL, realValue);
    }

    @Override
    public int getRightWindowLock() {
        Integer[] value = getValue(Integer[].class, SDKConstants.ID_FR_WIN_DOOR_STATE, SDKConstants.Area.SEAT_ALL);
        int pos = value.length > 1 ? value[1] : 0;
        LogUtils.e(TAG, " getRightWindowLock:state" + pos);
        return pos;
    }

    //打开/关闭左后窗
    @Override
    public void setBackLeftWindowLock(int pos) {
        LogUtils.e(TAG, " setBackLeftWindowLock pos:" + pos);
        Integer[] realValue = windowBase.clone();
        realValue[2] = pos;
        LogUtils.e(TAG, " setBackLeftWindowLock realValue:" + Arrays.toString(realValue));
        setIntArrayValue(SDKConstants.ID_SPEECH_RL_WIN, SDKConstants.Area.SEAT_ALL, realValue);
    }

    @Override
    public int getBackLeftWindowLock() {
        Integer[] value = getValue(Integer[].class, SDKConstants.ID_RL_WIN_DOOR_STATE, SDKConstants.Area.SEAT_ALL);
        int pos = value.length > 1 ? value[1] : 0;
        LogUtils.e(TAG, " getBackLeftWindowLock:state" + pos);
        return pos;
    }

    //打开/关闭右后窗
    @Override
    public void setBackRightWindowLock(int pos) {
        LogUtils.e(TAG, " setBackRightWindowLock pos:" + pos);
        Integer[] realValue = windowBase.clone();
        realValue[3] = pos;
        LogUtils.e(TAG, " setBackRightWindowLock realValue:" + Arrays.toString(realValue));
        setIntArrayValue(SDKConstants.ID_SPEECH_RR_WIN, SDKConstants.Area.SEAT_ALL, realValue);
    }

    @Override
    public int getBackRightWindowLock() {
        Integer[] value = getValue(Integer[].class, SDKConstants.ID_RR_WIN_DOOR_STATE, SDKConstants.Area.SEAT_ALL);
        int pos = value.length > 1 ? value[1] : 0;
        LogUtils.e(TAG, " getBackRightWindowLock:state" + pos);
        return pos;
    }

    //打开/关闭全车窗
    @Override
    public void setAllWindowLock(int state) {
        LogUtils.e(TAG, " setAllWindowLock:" + state);
        Integer[] realValue = new Integer[]{state, state, state, state};
        setIntArrayValue(SDKConstants.ID_WINDOW_LOCK, SDKConstants.Area.SEAT_ALL, realValue);
    }

    @Override
    public int getAllWindowLock() {
        Integer[] value = getValue(Integer[].class, SDKConstants.ID_WINDOW_LOCK, SDKConstants.Area.SEAT_ALL);
        int pos = value[0];
        LogUtils.e(TAG, " getAllWindowLock:" + pos);
        return pos;
    }

    @Override
    public void setRearviewMirror(boolean value) {
        LogUtils.e(TAG, mContext.getString(R.string.log_car_vendor_extension_set_mirror) + value);
        int realValue = value ? CanOnOff2.ON_REQ : CanOnOff2.OFF_REQ;
        setValue(Integer.class, SDKConstants.MIRROR_AUTOMATIC_FOLDING, 0, realValue);
    }

    @Override
    public void setRearBeltWorningSwitch(int value) {
        LogUtils.e(TAG, mContext.getString(R.string.log_car_vendor_extension_set_worning_switch) + value);
        setValue(Integer.class, SDKConstants.REAR_BELT_WORNING_SWITCH, 0, value);
    }

    @Override
    public int getRearBeltWorningSwitch() {
        int result = getValue(Integer.class, SDKConstants.REAR_BELT_WORNING_SWITCH, 0);
        LogUtils.e(TAG, mContext.getString(R.string.log_car_vendor_extension_get_worning_switch) + result);
        return result;
    }

    @Override
    public void setEPB(int value) {
        LogUtils.e(TAG, mContext.getString(R.string.log_car_vendor_extension_set_epb) + value);
        setValue(Integer.class, SDKConstants.EPB, 0, value);
    }

    @Override
    public int getEPB() {
        int result = getValue(Integer.class, SDKConstants.EPB, 0);
        LogUtils.e(TAG, mContext.getString(R.string.log_car_vendor_extension_get_epb) + result);
        return result;
    }

    @Override
    public void setRemoteControlUnlockMode(int value) {
        LogUtils.e(TAG, mContext.getString(R.string.log_car_vendor_extension_set_remote_control_mode) + value);
        setValue(Integer.class, SDKConstants.REMOTE_CONTROL_UNLOCK_MODE, 0, getRemoteControlUnlockValue(value));
    }

    private int getRemoteControlUnlockValue(int value) {
        int realValue = 0;
        switch (value) {
            case 0:
                realValue = DoorUnlock.ALL_DOOR_REQ;
                break;
            case 1:
                realValue = DoorUnlock.DRIVER_DOOR_REQ;
                break;
        }
        return realValue;
    }

    @Override
    public int getRemoteControlUnlockMode() {
        int result = getValue(Integer.class, SDKConstants.REMOTE_CONTROL_UNLOCK_MODE, 0);
        int realValue = -1;
        switch (result) {
            case DoorUnlock.ALL_DOOR:
                realValue = 0;
                break;
            case DoorUnlock.DRIVER_DOOR:
                realValue = 1;
                break;
        }
        LogUtils.e(TAG, mContext.getString(R.string.log_car_vendor_extension_get_remote_control_mode) + result);
        return realValue;
    }

    @Override
    public void setWipe(int value) {
        LogUtils.e(TAG, "设置雨刮器: " + value);
        setValue(Integer.class, CarCabinManager.ID_SPEECH_FRONT_WIPER, 0, value);
    }

//    @Override
//    public void setFcwSensitivity(int value) {
//        LogUtils.e(TAG, mContext.getString(R.string.log_car_vendor_extension_set_fcw_sensitivity) + value);
//        setValue(Integer.class, SDKConstants.FCW_SENSITIVITY, 0, value);
//    }

//    @Override
//    public int getFcwSensitivity() {
//        int result = getValue(Integer.class, SDKConstants.FCW_SENSITIVITY, 0);
//        LogUtils.e(TAG, mContext.getString(R.string.log_car_vendor_extension_get_fcw_sensitivity) + result);
//        return result;
//    }

    @Override
    public boolean getRearviewMirror() {
        int result = getValue(Integer.class, SDKConstants.MIRROR_AUTOMATIC_FOLDING, 0);
        LogUtils.e(TAG, mContext.getString(R.string.log_car_vendor_extension_get_mirror) + result);
        return result == CanOnOff2.ON;
    }

    @Override
    public void frontWash(int state) {
        LogUtils.e(TAG, "雨刮器单次洗涤: " + state);
        setValue(Integer.class, CarCabinManager.ID_SPEECH_FRONT_WASH, 0, state);
    }

    @Override
    public void setTestValue(int constant, int param) {
        setValue(Integer.class, constant, 0, param);
    }

    @Override
    public int getTestValue(int constant) {
        int value = getValue(Integer.class, constant, 0);
        return value;
    }
}
