package com.xiaoma.process.listener;

public interface IRemoteServiceStatusListener {

    void onConnected();

    void onDisConnected();

    void onConnectedDeath();

    //主动通知车门状态，doorStatus大小为6的数组，依次为发动机盖、前左、前右、后左、后右、行李箱盖状态值，值分为0和-1,0为开启状态,-1为关闭状态
    void notifyDoorStatus(int[] doorStatus);

    //主动通知车窗状态，windowStatus大小为4的数组，依次为前左车窗、前右车窗、后左车窗、后右车窗状态值，值分为0和-1,0为开启状态,-1为关闭状态
    void notifyWindowStatus(int[] windowStatus);

    //主动通知车锁状态，lockStatus大小为4的数组，依次为前左车锁、前右车锁、后左车锁、后右车锁状态值，值分为0和-1,0为开启状态,-1为关闭状态
    void notifyLockStatus(int[] lockStatus);

    //主动通知车灯状态，lightStatus大小为2的数组，依次为远光灯和位置灯状态值，值分为0和-1,0为开启状态,-1为关闭状态
    void notifyLightStatus(int[] lightStatus);

    //主动通知蓄电池状态，power为蓄电池值的百分比值，若蓄电池为50%，则power值为50
    void notifyPowerChange(int power);

    //主动通知剩余油量状态，oil为剩余油量的百分比值，若剩余油量为50%，则oil值为50
    void notifyOilChange(int oil);

    //主动通知总车程状态，mileage为总车程值，若总车程为300km，则mileage值为300
    void notifyMileageChange(int mileage);

    //AC开关是否打开
    void notifyAcStatus(boolean on);

    //空调温度
    void notifyAcTemp(float leftTemp, float rightTemp);

    //Auto开关状态
    void notifyAutoStatus(boolean on);

    //Dual的状态
    void notifyDualStatus(boolean on);

    //主动通知座椅加热状态，warmStatus为大小为2的数组，依次是左侧座椅加热状态值和右侧座椅加热状态值，值分为0和-1,0为开启状态,-1为关闭状态
    void notifySeatWarmStatus(int[] warmStatus);

    //主动通知风窗加热状态，warmStatus为大小为2的数组，依次是前挡风玻璃加热状态值和后挡风玻璃加热状态值，值分为0和-1,0为开启状态,-1为关闭状态
    void notifyWindowWarmStatus(int[] warmStatus);

    //风速大小风速值参考Constants.WindSpeed
    void notifyWindSpeed(int speed);

    //主动通知出风模式值参考Constants.WindModel
    void notifyWindModel(int windowModel);

    //主动通知天窗开启状态，skyLightStatus为天窗开启状态值，值分为0和-1,0为开启状态,-1为关闭状态
    void notifySkyLightStatus(int skyLightStatus);

    //主动通知循环状态，looperStatus为循环状态值，具体值参考Constants.LooperModel
    void notifyLooperStatus(int looperStatus);

}
