package com.xiaoma.setting.service;

import android.content.Context;
import android.os.RemoteException;

import com.xiaoma.aidl.setting.ISettingSoundAidlInterface;
import com.xiaoma.carlib.manager.IVendorExtension;
import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.utils.log.KLog;

/**
 * Created by LaiLai on 2018/12/10 0010.
 */

public class SettingSoundBinder extends ISettingSoundAidlInterface.Stub{

    private Context mContext;
    private IVendorExtension mSdkManager;

    public SettingSoundBinder(Context context){
        mContext = context;
        mSdkManager = XmCarFactory.getCarVendorExtensionManager();
    }

    //获取音量(快捷方式)
    @Override
    public int getSoundValue() throws RemoteException {
        KLog.d("ljb", "getSoundValue");
        return 0;
    }

    //设置音量(快捷方式)
    @Override
    public void setSoundValue(int value) throws RemoteException {
        KLog.d("ljb", "setSoundValue, value=" + value);
    }

    //设置音效模式
    @Override
    public void setSoundEffectsMode(int mode) throws RemoteException {
        KLog.d("ljb", "setSoundEffectsMode, mode=" + mode);
        mSdkManager.setSoundEffectsMode(mode);
    }

    //获取音效模式
    @Override
    public int getSoundEffectsMode() throws RemoteException {
        KLog.d("ljb", "getSoundEffectsMode");
        mSdkManager.getSoundEffectsMode();
        return 0;
    }

    @Override
    public void setCustomSoundEffects(int zone, int effectLevel) throws RemoteException {

    }

    //设置Arkamys3D音效（开、关）
    @Override
    public void setArkamys3D(int value) throws RemoteException {
        KLog.d("ljb", "setArkamys3D, value=" + value);
        mSdkManager.setArkamys3D(value);
    }

    //获取Arkamys3D音效（开、关）
    @Override
    public int getArkamys3D() throws RemoteException {
        KLog.d("ljb", "getArkamys3D");
        return mSdkManager.getArkamys3D();
    }

    //设置声场模式
    @Override
    public void setSoundFieldMode(int soundFieldMode) throws RemoteException {
        KLog.d("ljb", "setSoundFieldMode, soundFieldMode=" + soundFieldMode);
        mSdkManager.setSoundFieldMode(soundFieldMode);
    }

    //获取声场模式
    @Override
    public int getSoundFieldMode() throws RemoteException {
        KLog.d("ljb", "getSoundFieldMode");
        return mSdkManager.getSoundFieldMode();
    }

    //获取最佳听音位
    @Override
    public int getSoundEffectPosition() throws RemoteException {
        KLog.d("ljb", "getSoundEffectPosition");
//        return mSdkManager.getSoundEffectPosition();
        return 0;
    }

    //设置开关机音效（开、关）
    @Override
    public void setOnOffMusic(boolean opened) throws RemoteException {
        KLog.d("ljb", "setOnOffMusic,opened=" + opened);
        mSdkManager.setOnOffMusic(opened);
    }

    //获取开关机音效（开、关）
    @Override
    public boolean getOnOffMusic() throws RemoteException {
        KLog.d("ljb", "getOnOffMusic");
        return mSdkManager.getOnOffMusic();
    }

    //设置车辆提示音的级别（一级或是二级）
    @Override
    public void setCarInfoSound(int level) throws RemoteException {
        KLog.d("ljb", "setCarInfoSound,level=" + level);
        mSdkManager.setCarInfoSound(level);
    }

    //获取车辆提示音的级别（一级或是二级）
    @Override
    public int getCarInfoSound() throws RemoteException {
        KLog.d("ljb", "getCarInfoSound");
        return mSdkManager.getCarInfoSound();
    }

    //设置车速音量补偿的级别
    @Override
    public void setCarSpeedVolumeCompensate(int volume) throws RemoteException {
        KLog.d("ljb", "setCarSpeedVolumeCompensate,volume=" + volume);
        mSdkManager.setCarSpeedVolumeCompensate(volume);
    }

    //获取车速音量补偿的级别
    @Override
    public int getCarSpeedVolumeCompensate() throws RemoteException {
        KLog.d("ljb", "getCarSpeedVolumeCompensate");
        return mSdkManager.getCarSpeedVolumeCompensate();
    }

    //设置泊车媒体音量的级别（静音、弱化、正常）
    @Override
    public void setParkMediaVolume(int volume) throws RemoteException {
        KLog.d("ljb", "setParkMediaVolume, volume=" + volume);
        mSdkManager.setParkMediaVolume(volume);
    }

    //获取泊车媒体音量的级别（静音、弱化、正常）
    @Override
    public int getParkMediaVolume() throws RemoteException {
        KLog.d("ljb", "getParkMediaVolume");
        return mSdkManager.getParkMediaVolume();
    }
}
