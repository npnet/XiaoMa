package com.xiaoma.carlib.manager;

import android.util.Log;

import com.xiaoma.carlib.model.CarEvent;

import java.util.concurrent.CopyOnWriteArraySet;

public class WifiObserver implements ICarEvent {
    private static final String TAG = WifiObserver.class.getSimpleName();
    private static WifiObserver instance;
    private CopyOnWriteArraySet<WifiListener> wifiListeners = new CopyOnWriteArraySet<>();
    private WifiListener wifiForUserListener;

    public static WifiObserver getInstance() {
        if (instance == null) {
            synchronized (WifiObserver.class) {
                if (instance == null) {
                    instance = new WifiObserver();
                }
            }
        }
        return instance;
    }

    public void init() {
        XmCarEventDispatcher.getInstance().registerEvent(this);
    }

    public void addListener(WifiListener wifiListener) {
        wifiListeners.add(wifiListener);
    }

    public void removeListener(WifiListener wifiListener) {
        wifiListeners.remove(wifiListener);
    }

    // 设置专为拉取用户信息使用的监听
    public void setForUserListener(WifiListener wifiListener) {
        wifiForUserListener = wifiListener;
    }

    // 移除专为拉取用户信息使用的监听
    public void removeForUserListener() {
        wifiForUserListener = null;
        //如果只有用户的监听则全部移除时间分发
        if (wifiListeners.isEmpty()) {
            XmCarEventDispatcher.getInstance().unregisterEvent(this);
        }
    }

    @Override
    public void onCarEvent(CarEvent event) {
        //empty impl
    }

    private void dispatchConnect() {
        for (WifiListener wifiListener : wifiListeners) {
            try {
                wifiListener.onWifiConnect();
            } catch (Exception e) {
                Log.e(TAG, "dispatchDisConnect: ", e);
            }
        }
        if (wifiForUserListener != null) {
            wifiForUserListener.onWifiConnect();
        }
    }

    private void dispatchDisConnect() {
        for (WifiListener wifiListener : wifiListeners) {
            try {
                wifiListener.onWifiDisConnect();
            } catch (Exception e) {
                Log.e(TAG, "dispatchDisConnect: ", e);
            }
        }
        if (wifiForUserListener != null) {
            wifiForUserListener.onWifiDisConnect();
        }
    }

    public interface WifiListener {
        void onWifiConnect();

        void onWifiDisConnect();
    }
}
