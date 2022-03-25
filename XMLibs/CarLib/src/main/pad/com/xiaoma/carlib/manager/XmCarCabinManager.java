package com.xiaoma.carlib.manager;


import android.os.IBinder;

/**
 * @author: iSun
 * @date: 2018/10/22 0022
 */
public class XmCarCabinManager extends BaseCarManager implements ICarCabin {
    private static final String TAG = XmCarCabinManager.class.getSimpleName();
    private static final String SERVICE_NAME = "";
    private static XmCarCabinManager instance;

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

    @Override
    public void setTopWindowPos(int pos) {

    }

    @Override
    public int getTopWindowPos() {
        return 0;
    }

    @Override
    public void setAllWindowPos(int pos) {

    }

    @Override
    public void setUmbrellaPos(int pos) {

    }

    @Override
    public int getUmbrellaPos() {
        return 0;
    }

    @Override
    public boolean getWindowRearHeat() {
        return false;
    }

    @Override
    public void setDoorLock(int state) {

    }

   /* @Override
    public void setDoorLock(boolean state) {

    }*/

    @Override
    public boolean getDoorLock() {
        return false;
    }

    @Override
    public void setAllDoorLock(int state) {

    }

    /*@Override
    public void setAllDoorLock(boolean state) {

    }*/

    @Override
    public boolean getAllDoorLock() {
        return false;
    }

    @Override
    public void setBackDoorLock(int state) {

    }

    /*@Override
    public void setBackDoorLock(boolean state) {

    }*/

    @Override
    public boolean getBackDoorLock() {
        return false;
    }

    @Override
    public void setLeftWindowLock(int pos) {

    }

    @Override
    public int getLeftWindowLock() {
        return 0;
    }

    @Override
    public void setRightWindowLock(int pos) {

    }

    @Override
    public int getRightWindowLock() {
        return 0;
    }

    @Override
    public void setBackLeftWindowLock(int state) {

    }

    @Override
    public int getBackLeftWindowLock() {
        return 0;
    }

    @Override
    public void setBackRightWindowLock(int pos) {

    }

    @Override
    public int getBackRightWindowLock() {
        return 0;
    }

    @Override
    public void setAllWindowLock(int state) {

    }

    /*@Override
    public void setAllWindowLock(boolean state) {

    }*/

    @Override
    public int getAllWindowLock() {
        return 0;
    }

    @Override
    public void setRearviewMirror(boolean value) {

    }

    @Override
    public boolean getRearviewMirror() {
        return false;
    }

    @Override
    public void setRearBeltWorningSwitch(int value) {

    }

    @Override
    public int getRearBeltWorningSwitch() {
        return 0;
    }

    @Override
    public void setEPB(int value) {

    }

    @Override
    public int getEPB() {
        return 0;
    }

    @Override
    public void setRemoteControlUnlockMode(int value) {

    }

    @Override
    public int getRemoteControlUnlockMode() {
        return 0;
    }

    @Override
    public void setWipe(int value) {

    }

    @Override
    public void frontWash(int value) {

    }

    @Override
    public void setTestValue(int constant, int param) {

    }

    @Override
    public int getTestValue(int constant) {
        return 0;
    }

    @Override
    public void onCarServiceConnected(IBinder binder) {

    }
}
