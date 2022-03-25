package com.xiaoma.faultreminder.sdk;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.xiaoma.faultreminder.sdk.model.CarFault;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author KY
 * @date 12/26/2018
 */
public class FaultFactory {
    public static final String TAG = FaultFactory.class.getSimpleName();
    private static Random random = new Random();
    private static List<FaultListener> faultListeners = new ArrayList<>();
    private static Handler mHandler = new Handler(Looper.getMainLooper());

    private FaultFactory() throws Exception {
        throw new Exception();
    }

    private static Runnable randomFaultTask = new Runnable() {
        @Override
        public void run() {
            for (FaultListener faultListener : faultListeners) {
                faultListener.onFaultOccur(generateFaults());
            }
            mHandler.postDelayed(this, 3000 * 10 + random.nextInt(1000 * 10));
        }
    };

    private static IFault mFault = new IFault() {
        @Override
        public void init(Context context) {
            mHandler.postDelayed(randomFaultTask, 1000 * 15);
        }

        @Override
        public List<CarFault> getCurrentFaults() {
            return generateFaults();
        }

        @Override
        public void registerFaultListener(FaultListener faultListener) {
            faultListeners.add(faultListener);
        }

        @Override
        public void removeFaultListener(FaultListener faultListener) {
            faultListeners.remove(faultListener);
        }
    };

    private static List<CarFault> generateFaults() {
        return Arrays.asList(CarFault.values());
    }

    public static IFault getSDK() {
        return mFault;
    }
}
