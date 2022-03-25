package com.xiaoma.systemui.bussiness.wheel;

import android.car.Car;
import android.car.hardware.CarPropertyValue;
import android.car.hardware.CarVendorExtensionManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.xiaoma.carlib.wheelcontrol.WheelConstant;
import com.xiaoma.carlib.wheelcontrol.WheelKeyEvent;
import com.xiaoma.systemui.common.controller.OnConnectStateListener;
import com.xiaoma.systemui.common.util.LogUtil;

public class WheelKeyListener implements OnConnectStateListener<Car> {
    private static final String TAG = "WheelKeyListener";
    private Context mContext;
    private CarVendorExtensionManager mExMgr;
    private final CarVendorExtensionManager.CarVendorExtensionCallback callback = new CarVendorExtensionManager.CarVendorExtensionCallback() {
        @Override
        public void onChangeEvent(CarPropertyValue carPropertyValue) {
            LogUtil.logI(TAG, "onChangeEvent -> carPropertyValue: %s", carPropertyValue);
            if (carPropertyValue == null)
                return;
            try {
                int propId = carPropertyValue.getPropertyId();
                if (CarVendorExtensionManager.ID_MULTIFUNC_SWITCH == propId) {
                    int keyCode = (int) carPropertyValue.getValue();
                    LogUtil.logI(TAG, "onChangeEvent: ID_MULTIFUNC_SWITCH -> keyCode: %s", keyCode);
                    mContext.sendBroadcast(new Intent(WheelConstant.ACTION_WHEEL_KEY_EVENT)
                            .putExtra(WheelConstant.EXTRA_KEY_ACTION, WheelKeyEvent.ACTION_CLICK)
                            .putExtra(WheelConstant.EXTRA_KEY_CODE, keyCode)
                            .setPackage(mContext.getPackageName()));
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onErrorEvent(int propertyId, int area) {

        }
    };

    public WheelKeyListener(Context context) {
        mContext = context;
    }

    @Override
    public void onConnected(@NonNull Car car) {
        release();
        try {
            mExMgr = (CarVendorExtensionManager) car.getCarManager(Car.VENDOR_EXTENSION_SERVICE);
            mExMgr.registerCallback(callback);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisconnected() {
        release();
    }

    private void release() {
        if (mExMgr != null) {
            try {
                mExMgr.unregisterCallback(callback);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            mExMgr = null;
        }
    }
}
