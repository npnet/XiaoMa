package com.xiaoma.setting.service;

import android.content.Context;
import android.os.RemoteException;

import com.xiaoma.aidl.setting.ISettingCarAidlInterface;
import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.ICarCabin;
import com.xiaoma.carlib.manager.IVendorExtension;
import com.xiaoma.utils.log.KLog;

/**
 * Created by LaiLai on 2018/12/13 0013.
 */

public class SettingCarBinder extends ISettingCarAidlInterface.Stub{

    private Context mContext;
    private IVendorExtension mSdkManager;
    private final ICarCabin carCabinManager;

    public SettingCarBinder(Context context){
        mContext = context;
        mSdkManager = XmCarFactory.getCarVendorExtensionManager();
        carCabinManager = XmCarFactory.getCarCabinManager();
    }

    //FCW前防撞预警/AEB主动制动 开启/关闭
    @Override
    public boolean setFCwAebSwitch(boolean state) throws RemoteException {
        KLog.d("ljb", "setFCwAebSwitch");
        mSdkManager.setFcwAebSwitch(state ? SDKConstants.VALUE.FCW_ON_REQ : SDKConstants.VALUE.FCW_OFF_REQ);
        return true;
    }

    //ISA交通标志识别 开启/关闭
    @Override
    public boolean setISA(boolean state) throws RemoteException {
        KLog.d("ljb", "setISA");
        mSdkManager.setISA(state ? SDKConstants.VALUE.IFC_ACTIVE : SDKConstants.VALUE.IFC_OFF);
        return true;
    }

    //驾驶员注意力提醒
    @Override
    public boolean setDAW(boolean state) throws RemoteException {
        KLog.d("ljb", "setDAW");
        mSdkManager.setDAW(state ? SDKConstants.VALUE.DAW_ON_REQ : SDKConstants.VALUE.DAW_OFF_REQ);
        return true;
    }

    //电子刹车自动夹紧EPB
    @Override
    public boolean setElectronicBrake(boolean state) throws RemoteException {
        KLog.d("ljb", "setElectronicBrake");
        mSdkManager.setEPB(state ? SDKConstants.VALUE.EPB_ON : SDKConstants.VALUE.EPB_OFF_REQ);
        return true;
    }

    //后排安全带提醒
    @Override
    public boolean setSeatBelt(boolean state) throws RemoteException {
        KLog.d("ljb", "setSeatBelt");
        carCabinManager.setRearBeltWorningSwitch(state ? SDKConstants.VALUE.REAR_BELT_ON_REQ : SDKConstants.VALUE.REAR_BELT_OFF_REQ);
        return true;
    }

    //随速闭锁
    @Override
    public boolean setSpeedLockControl(boolean state) throws RemoteException {
        KLog.d("ljb", "setSpeedLockControl");
        mSdkManager.setSpeedAutoLock(state);
        return false;
    }

    //离车自动落锁
    @Override
    public void setLeaveAutomaticLock(boolean state) throws RemoteException {
        KLog.d("ljb", "setLeaveAutomaticLock");
        mSdkManager.setLeaveAutoLock(state);
    }

    //锁车自动关窗
    @Override
    public boolean setSelfClosingWindow(boolean state) throws RemoteException {
        KLog.d("ljb", "setSelfClosingWindow");
        mSdkManager.setSelfClosingWindow(state);
        return true;
    }

    //智能行李箱
    @Override
    public void setTrunk(boolean state) throws RemoteException {
        KLog.d("ljb", "setTrunk");
        mSdkManager.setAutomaticTrunk(state ? SDKConstants.VALUE.TRUNK_ON : SDKConstants.VALUE.TRUNK_OFF);
    }

    //锁车后视镜自动折叠
    @Override
    public boolean setRearviewMirror(boolean state) throws RemoteException {
        KLog.d("ljb", "setRearviewMirror");
        carCabinManager.setRearviewMirror(state);
        return true;
    }

    //座椅迎宾退让
    @Override
    public boolean setWelcomeSeat(boolean state) throws RemoteException {
        KLog.d("ljb", "setWelcomeSeat");
        mSdkManager.setWelcomeSeat(state);
        return true;
    }

    //离车灯光
    @Override
    public boolean setLeaveLight(boolean state) throws RemoteException {
        KLog.d("ljb", "setLeaveLight");
        mSdkManager.setWelcomeLightByRhythm(state ? SDKConstants.VALUE.EELCOME_LIGHT_MOTION_ON : SDKConstants.VALUE.EELCOME_LIGHT_MOTION_OFF);
        return true;
    }

    //迎宾灯
    @Override
    public boolean setWelcomeLight(boolean state) throws RemoteException {
        KLog.d("ljb", "setWelcomeLight");
        int value = SDKConstants.VALUE.WELCOM_LIGHT_OFF_REQ;
        if (state == true) {
            value = SDKConstants.VALUE.WELCOM_LIGHT_ON;
        }
        mSdkManager.setWelcomeLampTime(value);
        return true;
    }

    //IHC智能远光
    @Override
    public boolean setIHC(boolean state) throws RemoteException {
        KLog.d("ljb", "setIHC");
        mSdkManager.setIHC(state ? SDKConstants.VALUE.IHC_ON : SDKConstants.VALUE.IHC_OFF);
        return true;
    }

    //氛围灯开关
    @Override
    public boolean setAmbientLightSwitch(boolean state) throws RemoteException {
        KLog.d("ljb", "setAmbientLightSwitch");
        mSdkManager.setAmbientLightSwitch(state ? SDKConstants.VALUE.ATMOSPHERE_LIGHT_ON : SDKConstants.VALUE.ATMOSPHERE_LIGHT_OFF);
        return true;
    }

    //音乐情景随动开关
    @Override
    public boolean setSceneLightSwitch(boolean state) throws RemoteException {
        KLog.d("ljb", "setSceneLightSwitch");
        mSdkManager.setMusicSceneFollow(state ? SDKConstants.VALUE.MUSIC_FOLLOW_ON : SDKConstants.VALUE.MUSIC_FOLLOW_OFF);
        return true;
    }

}
