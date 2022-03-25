package com.xiaoma.setting.wifi.model;

import com.fsl.android.tbox.bean.TBoxWifiInfo;

/**
 * @Author ZiXu Huang
 * @Data 2018/10/15
 */
public class WifiScanResultBean {
    private boolean isConnected = false;
    private TBoxWifiInfo info;

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public TBoxWifiInfo getInfo() {
        return info;
    }

    public void setInfo(TBoxWifiInfo info) {
        this.info = info;
    }
}
