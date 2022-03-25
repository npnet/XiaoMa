package com.xiaoma.setting.sdk.manager;

import com.fsl.android.tbox.bean.TBoxEndPointInfo;
import com.fsl.android.tbox.bean.TBoxHotSpot;
import com.fsl.android.tbox.bean.TBoxWiFiConnStatus;
import com.xiaoma.setting.wifi.model.WifiScanResultBean;

import java.util.List;

/**
 * @Author: ZiXu Huang
 * @Data: 2019/6/11
 * @Desc:
 */
public interface WifiStateCallback {
    void onWifiEnable(boolean isEnable);

    void onWifiApEnable(boolean isWifiApEnable);

    void onWifiListChanged(List<WifiScanResultBean> list);

    void onWifiConnectStatusChange(TBoxWiFiConnStatus status);

    void onHotSpotChange(TBoxHotSpot hotSpot);

    void onHotSpotConnectedStatusChange(TBoxEndPointInfo[] infos);
}
