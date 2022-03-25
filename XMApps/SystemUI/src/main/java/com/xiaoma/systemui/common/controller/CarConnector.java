package com.xiaoma.systemui.common.controller;

import android.car.Car;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.xiaoma.systemui.common.util.LogUtil;

import java.util.HashSet;
import java.util.Set;

public class CarConnector implements ServiceConnection {
    private final static String TAG = "CarConnector";
    private final static CarConnector sInstance = new CarConnector();

    public static CarConnector getInstance() {
        return sInstance;
    }

    private Car mCar;
    private boolean mConnected;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Runnable mConnectCarTask = new Runnable() {
        @Override
        public void run() {
            connectCarService();
        }
    };
    private final Set<OnConnectStateListener<Car>> mOnConnectStateListeners = new HashSet<>();

    private CarConnector() {
    }

    public void init(Context context) {
        context = context.getApplicationContext();
        mCar = Car.createCar(context, this);
        connectCarService();
    }

    public void registerConnectListener(OnConnectStateListener<Car> listener) {
        if (listener != null) {
            if (mConnected) {
                listener.onConnected(mCar);
            }
            mOnConnectStateListeners.add(listener);
        }
    }

    public void unregisterConnectListener(OnConnectStateListener<Car> listener) {
        if (listener != null) {
            mOnConnectStateListeners.remove(listener);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        LogUtil.logE(TAG, "onServiceConnected");
        dispatchConnectState(true);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        LogUtil.logE(TAG, "onServiceDisconnected");
        dispatchConnectState(false);
        delayConnectCarService();
    }

    @Override
    public void onBindingDied(ComponentName name) {
        LogUtil.logE(TAG, "onBindingDied");
        dispatchConnectState(false);
        delayConnectCarService();
    }

    private void connectCarService() {
        LogUtil.logE(TAG, "connectCarService");
        try {
            mCar.connect();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void delayConnectCarService() {
        mHandler.removeCallbacks(mConnectCarTask);
        mHandler.postDelayed(mConnectCarTask, 1000);
    }

    private void dispatchConnectState(boolean connected) {
        if (connected != mConnected) {
            Set<OnConnectStateListener<Car>> listeners = new HashSet<>(mOnConnectStateListeners);
            for (OnConnectStateListener<Car> listener : listeners) {
                if (connected) {
                    listener.onConnected(mCar);
                } else {
                    listener.onDisconnected();
                }
            }
        }
        mConnected = connected;
    }
}
