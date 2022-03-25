// ISettingAidlInterface.aidl
package com.xiaoma.aidl.setting;
import com.xiaoma.aidl.setting.ISettingNotifyAidlInterface;

// Declare any non-default types here with import statements

interface ISettingConnectionAidlInterface {

     //注册、解注册回调
     void registerStatusNotify(ISettingNotifyAidlInterface notifyAidlInterface);
     void unRegisterStatusNotify(ISettingNotifyAidlInterface notifyAidlInterface);

     //获取wifi连接状态
     boolean getWifiConnection();
     //连接wifi
     void connectWifi();
     //打开wifi
     boolean openWifi();
     //关闭wifi
     boolean closeWifi();

     //获取蓝牙状态
     boolean getBlueToothStatus();
     //连接蓝牙
     void connectBlueTooth();
     //打开蓝牙
     boolean openBlueTooch();
     //关闭蓝牙
     boolean closeBlueTooch();

     //获取热点状态
     boolean getHotspotStatus();
     //连接热点
     void connectHotspot();
     //打开热点
     boolean openHotspot();
     //关闭热点
     boolean closeHotspot();

     //网络设置
     void setInternetType(int type);

}
