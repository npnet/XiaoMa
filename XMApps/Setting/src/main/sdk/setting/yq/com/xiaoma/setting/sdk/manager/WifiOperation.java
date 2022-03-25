package com.xiaoma.setting.sdk.manager;

/**
 * @Author: ZiXu Huang
 * @Data: 2019/6/11
 * @Desc:
 */
public interface WifiOperation {

    void setWifiEnable(boolean isEnable);

    void setWifiApEnable(boolean isEnable);

    void startScan();

    void connect(Object tBoxWifiInfo);

    void deleteSaveInfo();

    void registerWifiStateCallBack(WifiStateCallback callback);

    void unRegisterWifiStateCallBack(WifiStateCallback callback);

    void requestWifiStatus();

    int apSetting(String apName, String apPassword);

    void requestApInfo();

}
