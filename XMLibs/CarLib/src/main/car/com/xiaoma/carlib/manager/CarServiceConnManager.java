package com.xiaoma.carlib.manager;

import android.car.Car;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;

import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.log.KLog;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author: iSun
 * @date: 2019/2/28 0028
 */
public class CarServiceConnManager {
    private static final long DEFAULT_WAIT_TIMEOUT_MS = 2000;
    private static final String TAG = CarServiceConnManager.class.getSimpleName();
    private Context mContext;
    private Car mCar;
    private List<CarServiceListener> listeners = new CopyOnWriteArrayList<>();
    private static CarServiceConnManager instance;
    private boolean isConnection = false;
    private final DefaultServiceConnectionListener mConnectionListener =
            new DefaultServiceConnectionListener();
    private IBinder iBinder;
    private boolean onceConnected = false;//是否有过连接成功记录


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
        mContext = context;
        if (context != null) {
            initCar();
        }
    }

    public void addCallBack(CarServiceListener carListener) {
        if (carListener != null && !listeners.contains(carListener)) {
            listeners.add(carListener);
        }
        if (carListener != null && isConnection && iBinder != null) {
            carListener.onCarServiceConnected(iBinder);
        }
    }

    public void removeCallBack(CarServiceListener carListener) {
        listeners.remove(carListener);
    }

    private synchronized void initCar() {
        KLog.e(TAG, " initCar");
        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_AUTOMOTIVE) || mCar != null) {
            return;
        }
        ThreadDispatcher.getDispatcher().postHighPriority(new Runnable() {
            @Override
            public void run() {
                try {
                    mCar = Car.createCar(mContext, mConnectionListener);
                    mCar.connect();
                    mConnectionListener.waitForConnection(DEFAULT_WAIT_TIMEOUT_MS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Car getCar() {
        return mCar;
    }

    public Object getManagerForName(String serviceName) {
        Object carManager = null;
        if (mCar != null && isConnection) {
            try {
                carManager = mCar.getCarManager(serviceName);
            } catch (Exception e) {
                KLog.e(TAG, "getManagerForName : " + " err:" + e.getMessage());
                e.printStackTrace();
            }
        }
        return carManager;
    }

    protected class DefaultServiceConnectionListener implements ServiceConnection {
        private final Semaphore mConnectionWait = new Semaphore(0);

        public void waitForConnection(long timeoutMs) throws InterruptedException {
            mConnectionWait.tryAcquire(timeoutMs, TimeUnit.MILLISECONDS);
            KLog.e(TAG, " waitForConnection ");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            KLog.e(TAG, " onServiceConnected ");
            mConnectionWait.release();
            iBinder = service;
            isConnection = true;
            onceConnected = true;
            serviceConnected(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            KLog.e(TAG, " onServiceDisconnected ");
            mCar = null;
            isConnection = false;
            serviceDisconnected();
            iBinder = null;
            //重新连接
            reConnect();
        }
    }

    private void reConnect() {
        ThreadDispatcher.getDispatcher().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isConnection) {
                    initCar();
                }
            }
        }, 3000);
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
        try {
            if (mCar != null) {
                mCar.disconnect();
            }
            serviceDisconnected();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mCar = null;
            iBinder = null;
            isConnection = false;
        }
    }

    /**
     * 此方法会导致销毁连接再次建立，慎用
     */
    protected synchronized void reInit() {
        if (onceConnected) {
            release();
            initCar();
            onceConnected = false;
        }
    }

    public boolean isOnceConnected() {
        return onceConnected;
    }

    public boolean isConnection() {
        return isConnection;
    }
}
