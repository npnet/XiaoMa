package com.xiaoma.carlib.manager;

import android.car.Car;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.hvac.CarHvacManager;
import android.car.hardware.vendor.SpeechOnOff2;
import android.content.Context;
import android.os.IBinder;

import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.model.CarEvent;
import com.xiaoma.carlib.utils.LogUtils;

/**
 * @author: iSun
 * @date: 2018/12/24 0024
 */
public class XmCarHvacManager extends BaseCarManager<CarHvacManager> implements ICarHvac {
    private static final String TAG = XmCarHvacManager.class.getSimpleName();
    private static XmCarHvacManager instance;
    private static final String SERVICE_NAME = Car.HVAC_SERVICE;
    private Context context;
    private CarHvacManager.CarHvacEventCallback callback = new CarHvacManager.CarHvacEventCallback() {
        @Override
        public void onChangeEvent(CarPropertyValue carPropertyValue) {
            changeEvent(carPropertyValue);
        }

        @Override
        public void onErrorEvent(int i, int i1) {

        }
    };

    public static XmCarHvacManager getInstance() {
        if (instance == null) {
            synchronized (XmCarHvacManager.class) {
                if (instance == null) {
                    instance = new XmCarHvacManager();
                }
            }
        }
        return instance;
    }

    private XmCarHvacManager() {
        super(SERVICE_NAME);
    }

    private <E> E getValue(Class<E> clazz, int propId, int area) {
        LogUtils.e(TAG, " get value:" + propId);
        E result = getDefaultValue(clazz);
        CarHvacManager manager = getManager();
        if (manager != null) {
            try {
                if (clazz.equals(Integer.class)) {
                    result = clazz.cast(manager.getIntProperty(propId, area));
                } else if (clazz.equals(Boolean.class)) {
                    result = clazz.cast(manager.getBooleanProperty(propId, area));
                } else if (clazz.equals(Float.class)) {
                    result = clazz.cast(manager.getFloatProperty(propId, area));
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
        CarHvacManager manager = getManager();
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

    private void changeEvent(CarPropertyValue carPropertyValue) {
        XmCarEventDispatcher.getInstance().dispatcherEvent(carPropertyToXmEvent(carPropertyValue));
//        LogUtils.e(TAG, "changeEvent key id:" + carPropertyValue.getPropertyId() + ":" + carPropertyValue.getValue());
    }

    private CarEvent carPropertyToXmEvent(CarPropertyValue carPropertyValue) {
        return new CarEvent(carPropertyValue.getPropertyId(), carPropertyValue.getAreaId(), carPropertyValue.getValue());
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
        //服务断开
    }

    private void initCarEvent() {
        if (getManager() != null) {
            try {
                getManager().registerCallback(callback);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //打开/关闭空调
    @Override
    public void setHvacPowerOn(int state) {
        LogUtils.e(TAG, " setHvacPowerOn:" + state);
        setValue(Integer.class, SDKConstants.ID_SPEECH_AC_PWR, SDKConstants.Area.SEAT_ALL, state);
    }

    //空调是否打开
    @Override
    public int getHvacPowerOn() {
        int state = getValue(Integer.class, SDKConstants.ID_AC_MODE, SDKConstants.Area.SEAT_ALL);
        LogUtils.e(TAG, " getHvacPowerOn:" + state);
        return state;
    }

    //吹风模式切换，包括除霜模式，吹脚除霜模式，吹脚模式，吹脸和吹脚模式，吹脸模式
    @Override
    public void setFanDirectionAvailable(int mode) {
        LogUtils.e(TAG, " setFanDirectionAvailable:" + mode);
        setValue(Integer.class, SDKConstants.ID_SPEECH_BLOW_MODE, SDKConstants.Area.SEAT_ALL, mode);
    }

    @Override
    public int getFanDirectionAvailable() {
        int mode = getValue(Integer.class, SDKConstants.ID_BLW_MODE, SDKConstants.Area.SEAT_ALL);
        return mode;
    }

    //设置为具体温度
    @Override
    public void setLeftTempSetPoint(int area, float degrees) {
        LogUtils.e(TAG, " setLeftTempSetPoint:" + degrees);
        setValue(Float.class, SDKConstants.ID_SPEECH_TEMP_LEFT, area, degrees);
    }

    @Override
    public void setRightTempSetPoint(int area, float degrees) {
        LogUtils.e(TAG, " setRightTempSetPoint:" + degrees);
        setValue(Float.class, SDKConstants.ID_SPEECH_TEMP_RIGHT, area, degrees);
    }

    //获取当前温度值
    @Override
    public float getLeftTempSetPoint(int area) {
        LogUtils.e(TAG, " getLeftTempSetPoint:" + area);
        float degrees = getValue(Float.class, SDKConstants.ID_TEMP_L, area);
        return degrees;
    }

    @Override
    public float getRightTempSetPoint(int area) {
        LogUtils.e(TAG, " getRightTempSetPoint:" + area);
        float degrees = getValue(Float.class, SDKConstants.ID_TEMP_R, area);
        return degrees;
    }

    //风量设置
    @Override
    public void setFanSpeedSetpoint(int speed) {
        setRobAction(33);
        LogUtils.e(TAG, " setFanSpeedSetpoint:" + speed);
        setValue(Integer.class, SDKConstants.ID_SPEECH_BLOW_LV, SDKConstants.Area.SEAT_ALL, speed);
    }

    @Override
    public int getFanSpeedSetpoint() {
        int speed = getValue(Integer.class, SDKConstants.ID_BLW_LV, SDKConstants.Area.SEAT_ALL);
        LogUtils.e(TAG, " getFanSpeedSetpoint:" + speed);
        return speed;
    }

    //打开/关闭压缩机
    @Override
    public void setAcON(int state) {
        setRobAction(state == SpeechOnOff2.ON_REQ ? 35 : 36);
        LogUtils.e(TAG, " setAcON:" + state);
        setValue(Integer.class, SDKConstants.ID_SPEECH_COMPRESSOR, SDKConstants.Area.SEAT_ALL, state);
    }

    //切换循环模式，包括内循环和外循环
    @Override
    public void setAirRecirculationOn(int state) {
        setRobAction(32);
        LogUtils.e(TAG, " setAirRecirculationOn:" + state);
        setValue(Integer.class, SDKConstants.ID_SPEECH_FRS_REC, SDKConstants.Area.SEAT_ALL, state);
    }

    //打开/关闭自动模式
    @Override
    public void setAutomaticMode(int state) {
        setRobAction(32);
        LogUtils.e(TAG, " setAutomaticMode:" + state);
        setValue(Integer.class, SDKConstants.ID_SPEECH_AUTO, SDKConstants.Area.SEAT_ALL, state);
    }

    //打开/关闭后视镜加热
    @Override
    public void setMirrorDefroster(boolean state) {
        setRobAction(state ? 35 : 36);
        LogUtils.e(TAG, " setMirrorDefroster:" + state);
        setValue(Boolean.class, SDKConstants.ID_MIRROR_DEFROSTER_ON, SDKConstants.Area.SEAT_ALL, state);
    }

    //打开/关闭后风窗电加热
    @Override
    public void setWindowRearHeat(boolean state) {
        setRobAction(state ? 35 : 36);
        LogUtils.e(TAG, " setWindowRearHeat:" + state);
        setValue(Integer.class, SDKConstants.ID_SPEECH_REAR_WIN_HEAT, SDKConstants.Area.SEAT_ALL, state ? SpeechOnOff2.ON_REQ : SpeechOnOff2.OFF_REQ);
    }

    @Override
    public boolean getMirrorDefroster() {
        boolean state = getValue(Boolean.class, SDKConstants.ID_MIRROR_DEFROSTER_ON, SDKConstants.Area.SEAT_ALL);
        LogUtils.e(TAG, " getMirrorDefroster:" + state);
        return state;
    }

    //打开/关闭驾驶员座椅加热,关闭副驾驶座椅加热
    @Override
    public void setSeatTemp(int area, double temp) {
        //todo
    }

    @Override
    public void setLeftSeatTemp(int state) {
        setRobAction(state == SDKConstants.VALUE.SpeechOnOff2_ON_REQ ? 35 : 36);
        LogUtils.e(TAG, " setLeftSeatTemp:" + state);
        setValue(Integer.class, SDKConstants.ID_SPEECH_FL_SEAT_HEAT, SDKConstants.Area.SEAT_ALL, state);
    }

    @Override
    public void setRightSeatTemp(int state) {
        setRobAction(state == SDKConstants.VALUE.SpeechOnOff2_ON_REQ ? 35 : 36);
        LogUtils.e(TAG, " setRightSeatTemp:" + state);
        setValue(Integer.class, SDKConstants.ID_SPEECH_FR_SEAT_HEAT, SDKConstants.Area.SEAT_ALL, state);
    }

    //设置空调具体温度
    @Override
    public void setAcTemp(int temp) {
//        LogUtils.e(TAG, "设置空调温度: " + temp);
        setLeftAcTemp(temp);
        setRightAcTemp(temp);
    }

    @Override
    public void setLeftAcTemp(int temp) {
        LogUtils.e(TAG, "setLeftAcTemp: " + temp);
        setValue(Integer.class, SDKConstants.ID_SPEECH_TEMP_LEFT, SDKConstants.Area.SEAT_ALL, temp);
    }

    @Override
    public void setRightAcTemp(int temp) {
        LogUtils.e(TAG, "setRightAcTemp: " + temp);
        setValue(Integer.class, SDKConstants.ID_SPEECH_TEMP_RIGHT, SDKConstants.Area.SEAT_ALL, temp);
    }

    @Override
    public int getLeftAcTemp() {
        int value = getValue(Integer.class, SDKConstants.ID_TEMP_L, SDKConstants.Area.SEAT_ALL);
//        LogUtils.e(TAG, "获取空调温度: " + value);
        return value;
    }

    @Override
    public int getRightAcTemp() {
        return getValue(Integer.class, SDKConstants.ID_TEMP_R, SDKConstants.Area.SEAT_ALL);
    }

    @Override
    public void setTestValue(int constant, int param) {
        setValue(Integer.class, constant, 0, param);
    }


}
