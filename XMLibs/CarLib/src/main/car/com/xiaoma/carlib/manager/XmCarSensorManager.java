package com.xiaoma.carlib.manager;

import android.car.Car;
import android.car.CarNotConnectedException;
import android.car.hardware.CarSensorEvent;
import android.car.hardware.CarSensorManager;
import android.os.IBinder;
import android.util.Log;

import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.model.CarEvent;
import com.xiaoma.carlib.model.GearData;
import com.xiaoma.utils.log.KLog;

/**
 * @author: iSun
 * @date: 2018/12/24 0024
 */
public class XmCarSensorManager extends BaseCarManager<CarSensorManager> implements ICarSensor {
    private static final String TAG = XmCarSensorManager.class.getSimpleName();
    private static XmCarSensorManager instance;
    private static final String SERVICE_NAME = Car.SENSOR_SERVICE;
    private CarSensorManager.OnSensorChangedListener listener;

    public static XmCarSensorManager getInstance() {
        if (instance == null) {
            synchronized (XmCarSensorManager.class) {
                if (instance == null) {
                    instance = new XmCarSensorManager();
                }
            }
        }
        return instance;
    }

    private XmCarSensorManager() {
        super(SERVICE_NAME);
        listener = new CarSensorManager.OnSensorChangedListener() {
            @Override
            public void onSensorChanged(CarSensorEvent carSensorEvent) {
                if (carSensorEvent != null) {
                    if (carSensorEvent.sensorType == CarSensorManager.ID_GEAR_MODE) {
                        changeEvent(carSensorEvent);
                    } else if (carSensorEvent.sensorType == CarSensorManager.SENSOR_TYPE_STEERING_WHEEL_ANGLE) {
                        sendWheelAngelEvent();
                    }
                }
            }
        };
    }

    @Override
    public GearData getCurrentGearData() {
        GearData data = new GearData();
        if (getManager() != null) {
            try {
                CarSensorEvent latestSensorEvent = getManager().getLatestSensorEvent(CarSensorManager.ID_GEAR_MODE);
                if (latestSensorEvent != null) {
                    data.gear = latestSensorEvent.intValues[0];
                    data.timestamp = latestSensorEvent.timestamp;

                }
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Throwable t) {
                t.printStackTrace();
            }

        }
        return data;
    }


    @Override
    public void onCarServiceConnected(IBinder binder) {
        super.onCarServiceConnected();
        //服务连接 注册callBack
        // TODO: 2019/4/3 0003
        if (getManager() != null) {
            try {
                getManager().registerListener(listener, CarSensorManager.ID_GEAR_MODE, CarSensorManager.SENSOR_RATE_FAST);
                getManager().registerListener(listener, CarSensorManager.SENSOR_TYPE_STEERING_WHEEL_ANGLE, CarSensorManager.SENSOR_RATE_FAST);
            } catch (CarNotConnectedException e) {
            }
        }
    }

    private void changeEvent(CarSensorEvent carSensorEvent) {
        XmCarEventDispatcher.getInstance().dispatcherEvent(carPropertyToXmEvent(carSensorEvent));
    }

    private void sendWheelAngelEvent() {
        XmCarEventDispatcher.getInstance().dispatcherEvent(carPropertyToXmEvent());
    }

    private CarEvent carPropertyToXmEvent(CarSensorEvent carSensorEvent) {
        // TODO: 2019/4/3 0003 业务所需数据
        GearData gearData = new GearData();
        gearData.gear = carSensorEvent.intValues[0];
        gearData.timestamp = carSensorEvent.timestamp;
        KLog.e(TAG, "carPropertyToXmEvent() gear=" + gearData.gear);
        return new CarEvent(SDKConstants.CarSenSor.GEAR_ID, -1, gearData);
    }

    private CarEvent carPropertyToXmEvent() {

        int[] value = getWheelAngel();
        return new CarEvent(SDKConstants.CarSenSor.WHEEL_ANGEL_ID, -1, value);
    }

    @Override
    public void onCarServiceDisconnected() {
        super.onCarServiceDisconnected();
        //服务断开
    }

    @Override
    public int[] getWheelAngel() {
        int[] value = null;
        try {
            if (getManager() != null) {
                value = getManager().getIntArrayProperty(CarSensorManager.SENSOR_TYPE_STEERING_WHEEL_ANGLE, 0);
            }
        } catch (CarNotConnectedException e) {
            e.printStackTrace();
        }
        return value;
    }

    public boolean isConditionMeet() {
        // 换肤达成条件: 处于P档,电子驻车拉起
        GearData gearData = XmCarSensorManager.getInstance().getCurrentGearData();
        boolean isEPBLocked = XmCarVendorExtensionManager.getInstance().isEPBLocked();
        Log.e(TAG, String.format("isConditionMeet: gear = %s, isEPBLocked = %s",
                gearData != null ? gearData.gear : "null", isEPBLocked));
        return (gearData == null || SDKConstants.CarGearMode.GEAR_P == gearData.gear)
                && isEPBLocked;
    }

}
