package com.xiaoma.aidl.smart;

import com.xiaoma.aidl.smart.ISmartNotifyStatusAidlInterface;
import com.xiaoma.aidl.model.StatusModel;

interface ISmartStatusAidlInterface {

     boolean registerStatusNotify(ISmartNotifyStatusAidlInterface notifyAidlInterface);

     boolean unRegisterStatusNotify(ISmartNotifyStatusAidlInterface notifyAidlInterface);

     //获取所有状态
     StatusModel getAllStatus();

     //控制座椅加热状态，warmStatus为大小为2的数组，依次是左侧座椅加热状态值和右侧座椅加热状态值，值参考Constants.SeatWarm
     void controlSeatWarmStatus(inout int[] warmStatus);

     //控制风窗加热状态，warmStatus为大小为2的数组，依次是前挡风玻璃加热状态值和后挡风玻璃加热状态值，值分为0和-1,0为开启状态,-1为关闭状态
     void controlWindowWarmStatus(inout int[] warmStatus);

     //控制天窗开启状态，skyLightStatus为天窗开启状态值，值分为0和-1,0为开启状态,-1为关闭状态
     void controlSkyLightStatus(int skyLightStatus);

     //控制循环状态，looperStatus为循环状态值，具体值参考Constants.LooperModel
     void controlLooperStatus(int looperStatus);

     //控制空调开关调教
     void controlAcStatus(boolean on);

     //控制Auto开关
     void controlAutoStatus(boolean on);

     //控制Dual开关
     void controlDualStatus(boolean on);

     //控制空调温度,true调高，false调低
     void controlLeftACTemp(boolean add);

     //控制右边空调温度,true调高，false调低
     void controlRightACTemp(boolean add);

     //控制空调风力
     void controlWindLevel(boolean add);

     //控制风量强度
     void controlWindModel(int model);

}
