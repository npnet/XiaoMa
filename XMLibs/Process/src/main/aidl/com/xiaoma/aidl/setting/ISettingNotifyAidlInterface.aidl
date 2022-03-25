// ISettingNotifyAidlInterface.aidl
package com.xiaoma.aidl.setting;

interface ISettingNotifyAidlInterface {

    //回调wify连接状态
    void onConferConnectWifi(String wifyName, boolean status);

    //回调蓝牙连接状态
    void onConferConnectBlueTooch(String blueTouchName, boolean status);

    //回调热点连接状态
    void onConferConnectHotspot(String hotspotName, boolean status);
}
