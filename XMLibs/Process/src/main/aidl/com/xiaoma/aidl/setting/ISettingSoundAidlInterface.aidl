// ISettingAidlInterface.aidl
package com.xiaoma.aidl.setting;
import com.xiaoma.aidl.setting.ISettingNotifyAidlInterface;

// Declare any non-default types here with import statements

interface ISettingSoundAidlInterface {

     //获取音量(快捷方式)
     int getSoundValue();
     //设置音量(快捷方式)
     void setSoundValue(int value);

     //设置音效模式
     void setSoundEffectsMode(int mode);
     //获取音效模式
     int getSoundEffectsMode();
     //设置自定义音效
     void setCustomSoundEffects(int zone, int effectLevel);

     //设置Arkamys3D音效（全部乘客、驾驶员、关闭）
     void setArkamys3D(int value);
     //获取Arkamys3D音效（全部乘客、驾驶员、关闭）
     int getArkamys3D();

     //设置声场模式
     void setSoundFieldMode(int soundFieldMode);
     //获取声场模式
     int getSoundFieldMode();
     //获取最佳听音位
     int getSoundEffectPosition();

     //设置开关机音效（开、关）
     void setOnOffMusic(boolean opened);
     //获取开关机音效（开、关）
     boolean getOnOffMusic();

     //设置车辆提示音的级别（一级或是二级）
     void setCarInfoSound(int level);
     //获取车辆提示音的级别（一级或是二级）
     int getCarInfoSound();

     //设置车速音量补偿的级别
     void setCarSpeedVolumeCompensate(int volume);
     //获取车速音量补偿的级别
     int getCarSpeedVolumeCompensate();

     //设置泊车媒体音量的级别（静音、弱化、正常）
     void setParkMediaVolume(int volume);
     //获取泊车媒体音量的级别（静音、弱化、正常）
     int getParkMediaVolume();

}
