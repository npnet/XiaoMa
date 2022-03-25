package com.xiaoma.service.common.manager;

import android.media.AudioManager;
import android.text.TextUtils;

import com.xiaoma.carlib.XmCarFactory;
import com.xiaoma.carlib.constant.SDKConstants;
import com.xiaoma.carlib.manager.XmSystemManager;


/**
 * Created by Thomas on 2019/2/27 0027
 * about car interface
 */

public class CarDataManager {

    private static final String VIN_INFO = "LFPH4ABC848A05090";
    private static final int AUDIO_FOCUS_GAIN_FLAG = AudioManager.AUDIOFOCUS_GAIN_TRANSIENT;
    private boolean isBluetoothCall;//蓝牙电话是否在通话中


    private CarDataManager() {
    }

    private static class InstanceHolder {
        static final CarDataManager sInstance = new CarDataManager();
    }

    public static CarDataManager getInstance() {
        return CarDataManager.InstanceHolder.sInstance;
    }

    public boolean getIsBluetoothCall() {
        return isBluetoothCall;
    }

    public void setBluetoothCall(boolean bluetoothCall) {
        isBluetoothCall = bluetoothCall;
    }

    public String getVinInfo() {
        String vinInfo = XmCarFactory.getCarVendorExtensionManager().getVinInfo();
        if (TextUtils.isEmpty(vinInfo)) {
            vinInfo = VIN_INFO;
        }
        return vinInfo;
    }

    public Double getDriveDistance() {
        int currentOdometerData = XmCarFactory.getCarVendorExtensionManager().getOdometer();

        return currentOdometerData * 0.1;
    }

    public int operateICall() {
        return XmSystemManager.getInstance().operateTelePhoneICall(SDKConstants.CallOperation.DIAL);
    }

    public int operateBcall() {
        return XmSystemManager.getInstance().operateTelePhoneBCall(SDKConstants.CallOperation.DIAL);


    }

    public int hangUpBCall() {
        return XmSystemManager.getInstance().hangUpBCall();
    }

    public int hangUpICall() {
        return XmSystemManager.getInstance().hangUpICall();
    }


}
