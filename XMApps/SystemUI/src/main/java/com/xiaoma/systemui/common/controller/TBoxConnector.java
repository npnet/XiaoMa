package com.xiaoma.systemui.common.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.fsl.android.TboxBinderPool;
import com.xiaoma.systemui.common.util.LogUtil;
import com.xiaoma.systemui.common.util.LogWriter;

import java.util.HashSet;
import java.util.Set;

public class TBoxConnector implements TboxBinderPool.ServiceConnListener {
    public static final String TAG = "TBoxConnector";
    private static TBoxConnector sInstance = new TBoxConnector();

    public static TBoxConnector getInstance() {
        return sInstance;
    }

    private TboxBinderPool mTBoxBinderPool;
    private boolean mTBoxConnected = false;
    private final Set<OnConnectStateListener<TboxBinderPool>> mOnConnectStateListeners = new HashSet<>();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Runnable mTBoxConnectTask = new Runnable() {
        @Override
        public void run() {
            connectTBox();
        }
    };
    private LogWriter mLogWriter;

    private TBoxConnector() {
    }

    public void init(Context context) {
        mLogWriter = new LogWriter(context, "TBoxConnector.log");
        mLogWriter.startup();
        context = context.getApplicationContext();
        mTBoxBinderPool = TboxBinderPool.getInstance(context);
        mTBoxBinderPool.setConnListener(this);
        connectTBox();
    }

    public void registerConnectListener(OnConnectStateListener<TboxBinderPool> listener) {
        if (listener != null) {
            if (mTBoxConnected) {
                listener.onConnected(mTBoxBinderPool);
            }
            mOnConnectStateListeners.add(listener);
        }
    }

    public void unregisterConnectListener(OnConnectStateListener<TboxBinderPool> listener) {
        if (listener != null) {
            mOnConnectStateListeners.remove(listener);
        }
    }

    @Override
    public void onResult(int result) {
        LogUtil.logE(TAG, "TBoxConnector: onResult -> result: %s", result);
        mLogWriter.printLog("TBoxConnector: onResult -> result: %s", result);
        boolean connected = TboxBinderPool.BINDER_SUCCEED == result;
        if (connected != mTBoxConnected) {
            mTBoxConnected = connected;
            Set<OnConnectStateListener<TboxBinderPool>> listeners = new HashSet<>(mOnConnectStateListeners);
            for (OnConnectStateListener<TboxBinderPool> listener : listeners) {
                if (connected) {
                    listener.onConnected(mTBoxBinderPool);
                } else {
                    listener.onDisconnected();
                }
            }
        }
        // TBox断开重连
        if (!connected) {
            mHandler.removeCallbacks(mTBoxConnectTask);
            mHandler.postDelayed(mTBoxConnectTask, 1000);
        }
    }

    private void connectTBox() {
        LogUtil.logE(TAG, "TBoxConnector: connectTBox -> TBoxBinderPool.prepare");
        mLogWriter.printLog("TBoxConnector: connectTBox -> TBoxBinderPool.prepare");
        mTBoxBinderPool.prepare();
    }
}
