package com.xiaoma.carlib.manager;


import android.content.Context;
import android.content.pm.PackageManager;
import android.os.IBinder;

import com.xiaoma.utils.log.KLog;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author: iSun
 * @date: 2019/2/28 0028
 */
public class CarServiceConnManager {
    private static final long DEFAULT_WAIT_TIMEOUT_MS = 2000;
    private static final String TAG = CarServiceConnManager.class.getSimpleName();
    private Context mContext;
    private List<CarServiceListener> listeners = new CopyOnWriteArrayList<>();
    private static CarServiceConnManager instance;
    private boolean isConnection = false;


    public static CarServiceConnManager getInstance(Context context) {
        if (instance == null) {
            synchronized (CarServiceConnManager.class) {
                if (instance == null) {
                    instance = new CarServiceConnManager(context);
                }
            }
        }
        return instance;
    }

    private CarServiceConnManager(Context context) {
        mContext = context.getApplicationContext();
        initCar();
    }

    public void addCallBack(CarServiceListener carListener) {
        listeners.add(carListener);
        carListener.onCarServiceConnected(null);
    }

    public void removeCallBack(CarServiceListener carListener) {
        listeners.remove(carListener);
    }

    private synchronized void initCar() {
        KLog.e(TAG, " initCar");
        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_AUTOMOTIVE)) {
            return;
        }
    }


    public Object getManagerForName(String serviceName) {
        return null;
    }

    private void serviceConnected(IBinder binder) {
        for (CarServiceListener listener : listeners) {
            listener.onCarServiceConnected(binder);
        }
    }

    private void serviceDisconnected() {
        for (CarServiceListener listener : listeners) {
            listener.onCarServiceDisconnected();
        }
    }

    protected void release() {

    }

    /**
     * 此方法会导致销毁连接再次建立，慎用
     */
    protected synchronized void reInit() {

    }

    public boolean isOnceConnected() {
        return false;
    }

    public boolean isConnection() {
        return false;
    }
}
