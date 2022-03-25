package com.xiaoma.carlib.manager;

/**
 * @author: iSun
 * @date: 2018/12/24 0024
 */
public interface ICarCabin {

    //天窗控制
    void setTopWindowPos(int pos);
    int getTopWindowPos();

    //打开全车窗（透气）
    void setAllWindowPos(int pos);

    //遮阳帘控制
    void setUmbrellaPos(int pos);
    int getUmbrellaPos();

    //打开/关闭后风窗电加热
//    void setWindowRearHeat(boolean state);
    boolean getWindowRearHeat();

    //打开/关闭司机门车锁
    void setDoorLock(int state);
    boolean getDoorLock();

    //打开/关闭全车门锁
    void setAllDoorLock(int state);
    boolean getAllDoorLock();

    //打开后备箱
    void setBackDoorLock(int state);
    boolean getBackDoorLock();

    //打开/关闭司机车窗
    void setLeftWindowLock(int pos);
    int getLeftWindowLock();

    //打开/关闭副司机车窗
    void setRightWindowLock(int pos);
    int getRightWindowLock();

    //打开/关闭左后司机车窗
    void setBackLeftWindowLock(int pos);
    int getBackLeftWindowLock();

    //打开/关闭右后司机车窗
    void setBackRightWindowLock(int pos);
    int getBackRightWindowLock();

    //打开/关闭全车窗
    void setAllWindowLock(int state);
    int getAllWindowLock();

    //后视镜自动折叠
    void setRearviewMirror(boolean value);

    boolean getRearviewMirror();

    void setRearBeltWorningSwitch(int value);

    int getRearBeltWorningSwitch();

    void setEPB(int value);

    int getEPB();

//    void setFcwSensitivity(int value);

//    int getFcwSensitivity();

    void setRemoteControlUnlockMode(int value);

    int getRemoteControlUnlockMode();

    //雨刮器
    void setWipe(int value);

    //雨刮器单次洗涤
    void frontWash(int value);

    void setTestValue(int constant, int param);

    int getTestValue(int constant);
}
