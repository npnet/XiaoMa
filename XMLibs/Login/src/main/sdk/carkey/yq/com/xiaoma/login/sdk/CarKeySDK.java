package com.xiaoma.login.sdk;

import android.car.hardware.vendor.RmbLeKey;
import android.content.Context;
import android.util.Log;

import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;

/**
 * Created by kaka
 * on 19-4-23 下午4:17
 * <p>
 * desc:
 * </p>
 *
 * @see RmbLeKey
 */
public class CarKeySDK implements ICarKey {
    private static final String TAG = CarKey.class.getSimpleName();

    private Context mContext;

    private static class InstanceHolder {
        static final CarKeySDK instance = new CarKeySDK();
    }

    public static CarKeySDK getInstance() {
        return InstanceHolder.instance;
    }

    @Override
    public void init(Context context) {
        this.mContext = context;
        XmCarVendorExtensionManager.getInstance().init(context);
    }

    @Override
    public CarKey getCarKey() {
        int carKey = XmCarVendorExtensionManager.getInstance().getCarKey();
        Log.d(TAG, "getCarKey get key: " + carKey);
        CarKey key = CarKey.valueOf(carKey);
        if(key == CarKey.NO_KEY){
            return null;
        }else {
            return key;
        }
    }

    @Override
    public void setCarKey(String key) {
        // do nothing
    }
}
