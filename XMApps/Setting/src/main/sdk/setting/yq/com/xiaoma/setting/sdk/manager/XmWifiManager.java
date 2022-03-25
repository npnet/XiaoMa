package com.xiaoma.setting.sdk.manager;

import com.fsl.android.tbox.bean.TBoxEndPointInfo;
import com.fsl.android.tbox.bean.TBoxHotSpot;
import com.fsl.android.tbox.bean.TBoxWiFiConnStatus;
import com.fsl.android.tbox.bean.TBoxWifiInfo;
import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.ICarEvent;
import com.xiaoma.carlib.manager.XmCarEventDispatcher;
import com.xiaoma.carlib.manager.XmSystemManager;
import com.xiaoma.carlib.model.CarEvent;
import com.xiaoma.setting.wifi.model.WifiScanResultBean;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: ZiXu Huang
 * @Data: 2019/6/11
 * @Desc:
 */
public class XmWifiManager implements WifiOperation, ICarEvent {
    private static XmWifiManager instance;
    private List<WifiStateCallback> callbacks = new ArrayList<>();

    private XmWifiManager() {
        XmCarEventDispatcher.getInstance().registerEvent(this);
    }

    public static XmWifiManager getInstance() {
        if (instance == null) {
            synchronized (XmWifiManager.class) {
                if (instance == null) {
                    instance = new XmWifiManager();
                }
            }
        }
        return instance;
    }

    public void getWifiCOnnectedState() {
        XmSystemManager.getInstance().getWifiConnectStatus();
    }


    @Override
    public void setWifiEnable(boolean isEnable) {
        XmSystemManager.getInstance().setWorkPattern(isEnable ? SDKConstants.WifiMode.STA : SDKConstants.WifiMode.OFF);
    }

    @Override
    public void setWifiApEnable(boolean isEnable) {
        XmSystemManager.getInstance().setWorkPattern(isEnable ? SDKConstants.WifiMode.AP : SDKConstants.WifiMode.OFF);
    }

    @Override
    public void startScan() {
        XmCarFactory.getSystemManager().scanWifiList();
    }

    @Override
    public void connect(Object tBoxWifiInfo) {
        if (tBoxWifiInfo instanceof TBoxHotSpot) {
            TBoxWifiInfo info = (TBoxWifiInfo) tBoxWifiInfo;
            XmSystemManager.getInstance().connectWifi(SDKConstants.WifiOperation.CONNECT, SDKConstants.WifiSavedState.SAVED, info);
        }
    }

    @Override
    public void deleteSaveInfo() {

    }

    @Override
    public void registerWifiStateCallBack(WifiStateCallback callback) {
        if (callback != null) {
            callbacks.add(callback);
        }
    }

    @Override
    public void unRegisterWifiStateCallBack(WifiStateCallback callback) {
        if (callbacks.contains(callback)) {
            callbacks.remove(callback);
        }
    }

    @Override
    public void requestWifiStatus() {
        XmSystemManager.getInstance().getWorkPattern();
    }

    @Override
    public int apSetting(String apName, String apPassword) {
        TBoxHotSpot tBoxHotSpot = new TBoxHotSpot();
        tBoxHotSpot.setSsid(apName);
        tBoxHotSpot.setPassword(apPassword);
        tBoxHotSpot.setEncryption(SDKConstants.WifiEncryption.WPA2);
        return XmSystemManager.getInstance().setHotSpot(tBoxHotSpot);
    }

    @Override
    public void requestApInfo() {
        XmSystemManager.getInstance().getHotSpot();
    }


    @Override
    public void onCarEvent(final CarEvent event) {
        ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
            @Override
            public void run() {
                if (event.id == SDKConstants.WifiMode.WIFI_MODE_ID) { //wifi开关
                    int workPattern = (int) event.value;
                    if (workPattern == SDKConstants.WifiMode.OFF) {
                        notifyWifiEnable(false);
                        notifyWifiApEnable(false);
                    } else if (workPattern == SDKConstants.WifiMode.AP) {
                        notifyWifiEnable(false);
                        notifyWifiApEnable(true);
                    } else if (workPattern == SDKConstants.WifiMode.STA) {
                        notifyWifiEnable(true);
                        notifyWifiApEnable(false);
                    }
                } else if (event.id == SDKConstants.WifiAboutEventId.ID_WIFI_CONNECT_STATUS_EVENT) { //wifi连接状态
                    TBoxWiFiConnStatus tBoxWiFiConnStatus = (TBoxWiFiConnStatus) event.value;  //具体见SDKConsant.WifiConnectStatus
                    int status = tBoxWiFiConnStatus.getStatus();
                    String statusString = null;
                    if (status == SDKConstants.WifiConnectStatus.CONNECTED) {
                        statusString = "wifi已连接";
                    } else if (status == SDKConstants.WifiConnectStatus.DISCONNECTED) {
                        statusString = "wifi已断开";
                    } else if (status == SDKConstants.WifiConnectStatus.AUTHENTICATION_FAILED) {
                        statusString = "wifi未授权";
                    } else if (status == SDKConstants.WifiConnectStatus.IP_ACQUISITION_FAILED) {
                        statusString = "wifi获取IP地址失败";
                    }
                    KLog.d("hzx", statusString);
                    notifyWifiConnectStatus(tBoxWiFiConnStatus);
                } else if (event.id == SDKConstants.WifiAboutEventId.ID_WIFI_LIST_EVENT) { //wifi列表变化
                    TBoxWifiInfo[] infos = null;
                    try {
                        infos = (TBoxWifiInfo[]) event.value;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (infos != null) {
                        notifyWifiListChange(infos);
                    }
                } else if (event.id == SDKConstants.WifiAboutEventId.ID_WIFI_AP_ACCOUNT_INFO) { //wifi热点账号信息
                    TBoxHotSpot hotSpot = (TBoxHotSpot) event.value;
                    notifyHotSpotInfoReceiver(hotSpot);
                } else if (event.id == SDKConstants.WifiAboutEventId.ID_WIFI_AP_INFO) { //wifi热点连接信息
                    TBoxEndPointInfo[] infos = (TBoxEndPointInfo[]) event.value;
                    notifyHotSpotConnectedStatusChange(infos);
                }
            }
        });

    }

    private void notifyHotSpotConnectedStatusChange(TBoxEndPointInfo[] infos) {
        if (callbacks != null && !callbacks.isEmpty()) {
            for (WifiStateCallback callback : callbacks) {
                callback.onHotSpotConnectedStatusChange(infos);
            }
        }
    }

    private void notifyHotSpotInfoReceiver(TBoxHotSpot hotSpot) {
        if (callbacks != null && !callbacks.isEmpty()) {
            for (WifiStateCallback callback : callbacks) {
                callback.onHotSpotChange(hotSpot);
            }
        }
    }

    private void notifyWifiListChange(TBoxWifiInfo[] infos) {
        List<WifiScanResultBean> list = new ArrayList<>();
        for (TBoxWifiInfo info : infos) {
            WifiScanResultBean wifiScanResultBean = new WifiScanResultBean();
            wifiScanResultBean.setInfo(info);
            list.add(wifiScanResultBean);
        }
        if (callbacks != null && !callbacks.isEmpty()) {
            for (WifiStateCallback callback : callbacks) {
                callback.onWifiListChanged(list);
            }
        }
    }

    private void notifyWifiConnectStatus(TBoxWiFiConnStatus status) {
        if (callbacks != null && !callbacks.isEmpty()) {
            for (WifiStateCallback callback : callbacks) {
                // TODO 接口暂不支持返回当前wifi对象,待接口更新
                callback.onWifiConnectStatusChange(status);
            }
        }
    }

    private void notifyWifiEnable(boolean enable) {
        if (callbacks != null && !callbacks.isEmpty()) {
            for (WifiStateCallback callback : callbacks) {
                callback.onWifiEnable(enable);
            }
        }
    }

    private void notifyWifiApEnable(boolean enable) {
        if (callbacks != null && !callbacks.isEmpty()) {
            for (WifiStateCallback callback : callbacks) {
                callback.onWifiApEnable(enable);
            }
        }
    }

}
