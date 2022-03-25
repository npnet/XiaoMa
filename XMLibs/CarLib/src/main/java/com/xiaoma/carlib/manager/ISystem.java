package com.xiaoma.carlib.manager;

/**
 * @author: iSun
 * @date: 2018/12/26 0026
 */
public interface ISystem {
    public boolean getBlueToothStatus();

    public boolean setBlueToothStatus(boolean status);

    public boolean getWIfiStatus();

    public void setWIfiStatus(boolean status);

    /**
     * 设置数据开关
     *
     * @param status
     * @return 参考 Constants.RequestAck(otaserver aar包)
     */
    public int setDataSwitch(boolean status);

    /**
     * 设置WIFI工作模式
     *
     * @param status 入参参考 Constants.WifiMode
     * @return 参考 Constants.RequestAck(otaserver aar包)
     */
    public int setWorkPattern(int status);

    /**
     * 请求Wifi工作模式
     */
    public void getWorkPattern();

    /**
     * 拨打ICALL
     * @return 参考 Constants.RequestAck(otaserver aar包)
     */
    public int operateTelePhoneICall(int operation);


    /**
     * 拨打BCALL
     * @return 参考 Constants.RequestAck(otaserver aar包)
     */
    public int operateTelePhoneBCall(int operation);


    /**
     * 挂断ICALL
     * @return
     */
    public int hangUpICall();

    /**
     * 挂断BCALL
     * @return
     */
    public int hangUpBCall();
    /**
     * 扫描可连接的wifi
     */
    public void scanWifiList();

    /**
     * 获取wifi列表
     */
    public void getWifiList();

    /**
     * 操作wifi
     * @param op {SDKConstants.WifiOperation}
     * @param ssid wifi网络信息
     */
    public void operateConnectedWifi(int op, String ssid);

    /**
     * 连接wifi
     * @param op {SDKConstants.WifiOperation}
     * @param auto {SDKConstants.WifiSavedState}
     * @param info
     * @return {@link com.xiaoma.carlib.constant.SDKConstants.RequestAck}
     */
    public int connectWifi(int op, int auto, Object tBoxHotSpot);

    /**
     * @param hotspo
     * @return {@link com.xiaoma.carlib.constant.SDKConstants.RequestAck}
     */
    public int setHotSpot(Object tBoxHotSpot);

    /**
     * 设置流量阈值
     * @param thresold 阈值，单位MB
     * @return {@link com.xiaoma.carlib.constant.SDKConstants.RequestAck}
     */
    public int setDataTrafficThreshold(String thresold);

    /**
     * 获取wifi连接状态
     */
    public void getWifiConnectStatus();

    /**
     * 获取流量开关状态
     */
    public void getCellulatData();

    /**
     * 获取热点账户和密码信息
     */
    public void getHotSpot();

    public boolean noUpdate();

    public void setUpdateListener(XmSystemManager.IupDateListener updateListener);

}
