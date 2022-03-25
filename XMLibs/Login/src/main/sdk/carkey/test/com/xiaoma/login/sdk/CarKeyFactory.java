package com.xiaoma.login.sdk;

import android.content.Context;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.xiaoma.login.business.receive.KeyMocker;
import com.xiaoma.utils.tputils.TPUtils;

public class CarKeyFactory {

    private static final String CAR_KEY = "CarKeyFactory-carKey";

    private CarKeyFactory() throws Exception {
        throw new Exception();
    }

    private static ICarKey iCarKey = new ICarKey() {
        private String carKey;
        private Context mContext;
        private boolean mHasInit;

        @Override
        public synchronized void init(Context context) {
            if (mHasInit)
                return;
            mContext = context.getApplicationContext();
            IntentFilter filter = new IntentFilter("com.xiaoma.KeyMocker");
            context.registerReceiver(new KeyMocker(), filter);
            mHasInit = true;
        }

        @Override
        public CarKey getCarKey() {
            if (TextUtils.isEmpty(carKey)) {
                carKey = TPUtils.get(mContext, CAR_KEY, "1");
            }
            if ("0".equals(carKey)) {
                return null;
            } else {
                return CarKey.strOf(carKey);
            }
        }

        /**
         * 仅用于测试
         *
         * @param carKey
         */
        public void setCarKey(String carKey) {
            this.carKey = carKey;
            TPUtils.put(mContext, CAR_KEY, carKey);
        }
    };

    public static ICarKey getSDK() {
        return iCarKey;
    }
}
