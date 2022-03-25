package com.xiaoma.systemui.bussiness;

import android.content.Context;
import android.support.annotation.NonNull;

import com.xiaoma.systemui.bussiness.barstatus.BarStatus;
import com.xiaoma.systemui.bussiness.barstatus.BatteryBarStatus;
import com.xiaoma.systemui.bussiness.barstatus.BluetoothBarStatus;
import com.xiaoma.systemui.bussiness.barstatus.MobileSignalBarStatus;
import com.xiaoma.systemui.bussiness.barstatus.TimeBarStatusIC;
import com.xiaoma.systemui.bussiness.barstatus.UsbBarStatus;
import com.xiaoma.systemui.bussiness.barstatus.VolumeBarStatus;
import com.xiaoma.systemui.bussiness.barstatus.WifiBarStatus;

import java.util.Arrays;
import java.util.List;

/**
 * Created by LKF on 2019-3-6 0006.
 */
public class BarStatusManager {
    private static final List<Class<? extends BarStatus>> BAR_STATUS = Arrays.asList(
            //HotspotBarStatus.class,
            WifiBarStatus.class,
            UsbBarStatus.class,
            BatteryBarStatus.class,
            BluetoothBarStatus.class,
            VolumeBarStatus.class,
            MobileSignalBarStatus.class,
            TimeBarStatusIC.class
    );

    public static void startup(Context context) {
        WifiBarStatus wifiStatus = null;
        MobileSignalBarStatus mobileStatus = null;
        final int size = BAR_STATUS.size();
        for (int i = 0; i < size; i++) {
            final Class<? extends BarStatus> status = BAR_STATUS.get(i);
            try {
                BarStatus barStatus = status.newInstance();
                if (barStatus instanceof WifiBarStatus) {
                    wifiStatus = (WifiBarStatus) barStatus;
                } else if (barStatus instanceof MobileSignalBarStatus) {
                    mobileStatus = (MobileSignalBarStatus) barStatus;
                }
                barStatus.startup(context, i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (mobileStatus != null && wifiStatus != null) {
            wifiStatus.setCallback(new WifiCallback(mobileStatus));
            mobileStatus.setCallback(new MobileStatusCallback(wifiStatus));
        }
    }

    private static class WifiCallback implements WifiBarStatus.Callback {
        MobileSignalBarStatus mobileSignalStatus;

        WifiCallback(MobileSignalBarStatus mMobileSignalStatus) {
            this.mobileSignalStatus = mMobileSignalStatus;
        }

        @Override
        public void onWifiVisibleChanged(boolean isShow) {
            mobileSignalStatus.postUpdate();
        }
    }

    private static class MobileStatusCallback implements MobileSignalBarStatus.Callback {
        private WifiBarStatus mWifiStatus;

        MobileStatusCallback(@NonNull WifiBarStatus wifiStatus) {
            mWifiStatus = wifiStatus;
        }

        @Override
        public boolean isWifiStatusShowing() {
            return mWifiStatus.isShowing();
        }
    }
}
