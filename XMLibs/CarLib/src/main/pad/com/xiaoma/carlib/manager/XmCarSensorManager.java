package com.xiaoma.carlib.manager;


import android.os.IBinder;

import com.xiaoma.carlib.model.GearData;

/**
 * @author: iSun
 * @date: 2018/12/24 0024
 */
public class XmCarSensorManager extends BaseCarManager implements ICarSensor {
    private static final String TAG = XmCarSensorManager.class.getSimpleName();
    private static XmCarSensorManager instance;
    private static final String SERVICE_NAME = "";

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
    }


    @Override
    public GearData getCurrentGearData() {
        return null;
    }

    @Override
    public int[] getWheelAngel() {
        return new int[0];
    }

    @Override
    public boolean isConditionMeet() {
        return true;
    }

    @Override
    public void onCarServiceConnected(IBinder binder) {

    }
}
