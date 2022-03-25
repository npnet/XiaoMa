package com.xiaoma.carlib.manager;


import android.os.IBinder;

/**
 * @author: iSun
 * @date: 2018/12/24 0024
 */
public class XmCarHvacManager extends BaseCarManager implements ICarHvac {
    private static final String TAG = XmCarHvacManager.class.getSimpleName();
    private static XmCarHvacManager instance;
    private static final String SERVICE_NAME = "";

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

    @Override
    public void setHvacPowerOn(int state) {

    }

    @Override
    public int getHvacPowerOn() {
        return 0;
    }

    @Override
    public void setFanDirectionAvailable(int mode) {

    }

    @Override
    public int getFanDirectionAvailable() {
        return 0;
    }

    @Override
    public void setLeftTempSetPoint(int area, float degrees) {

    }

    @Override
    public void setRightTempSetPoint(int area, float degrees) {
    }

    @Override
    public float getLeftTempSetPoint(int area) {
        return 0;
    }

    @Override
    public float getRightTempSetPoint(int area) {
        return 0;
    }

    @Override
    public void setFanSpeedSetpoint(int speed) {

    }

    @Override
    public int getFanSpeedSetpoint() {
        return 0;
    }

    @Override
    public void setAcON(int state) {

    }

    /*@Override
    public void setAcON(boolean state) {

    }*/

    @Override
    public void setAirRecirculationOn(int state) {

    }

    @Override
    public void setAutomaticMode(int state) {

    }

    /*@Override
    public void setAutomaticMode(boolean state) {

    }*/

    @Override
    public void setMirrorDefroster(boolean state) {

    }

    @Override
    public boolean getMirrorDefroster() {
        return false;
    }

    @Override
    public void setWindowRearHeat(boolean state) {

    }

    @Override
    public void setSeatTemp(int area, double temp) {

    }

    @Override
    public void setLeftSeatTemp(int state) {

    }

    @Override
    public void setRightSeatTemp(int state) {

    }

    @Override
    public void setAcTemp(int temp) {

    }

    @Override
    public void setLeftAcTemp(int temp) {

    }

    @Override
    public void setRightAcTemp(int temp) {

    }

    @Override
    public int getLeftAcTemp() {
        return 0;
    }

    @Override
    public int getRightAcTemp() {
        return 0;
    }

    @Override
    public void setTestValue(int constant, int param) {

    }

    @Override
    public void onCarServiceConnected(IBinder binder) {

    }
}
