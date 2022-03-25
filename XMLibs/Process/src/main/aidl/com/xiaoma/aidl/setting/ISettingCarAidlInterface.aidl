// ISettingCarAidlInterface.aidl
package com.xiaoma.aidl.setting;

// Declare any non-default types here with import statements

interface ISettingCarAidlInterface {

    //FCW前防撞预警/AEB主动制动 开启/关闭
    boolean setFCwAebSwitch(boolean state);

    //ISA交通标志识别 开启/关闭
    boolean setISA(boolean state);

    //驾驶员注意力提醒
    boolean setDAW(boolean state);

    //电子刹车自动夹紧EPB
    boolean setElectronicBrake(boolean state);

    //后排安全带提醒
    boolean setSeatBelt(boolean state);

    //随速闭锁
    boolean setSpeedLockControl(boolean state);

    //离车自动落锁
    void setLeaveAutomaticLock(boolean state);

    //锁车自动关窗
    boolean setSelfClosingWindow(boolean state);

    //智能行李箱
    void setTrunk(boolean state);

    //锁车后视镜自动折叠
    boolean setRearviewMirror(boolean state);

    //座椅迎宾退让
    boolean setWelcomeSeat(boolean state);

    //离车灯光
    boolean setLeaveLight(boolean state);

    //迎宾灯
    boolean setWelcomeLight(boolean state);

    //IHC智能远光
    boolean setIHC(boolean state);

    //氛围灯开关
    boolean setAmbientLightSwitch(boolean state);

    //音乐情景随动开关
    boolean setSceneLightSwitch(boolean state);
}
