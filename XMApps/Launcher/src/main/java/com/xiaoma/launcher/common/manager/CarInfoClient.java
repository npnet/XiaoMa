package com.xiaoma.launcher.common.manager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.xiaoma.center.logic.local.Center;
import com.xiaoma.center.logic.local.StateManager;
import com.xiaoma.center.logic.model.SourceInfo;
import com.xiaoma.center.logic.remote.Client;
import com.xiaoma.center.logic.remote.ClientCallback;
import com.xiaoma.component.AppHolder;

/**
 * @author youthyJ
 * @date 2019/6/6
 */
public class CarInfoClient extends Client {
    private static final String TAG = CarInfoClient.class.getSimpleName() + "_LOG";
    private static final int CLIENT_PORT = 1038;
    private static final int CAR_SPEED_ACTION = 1;
    private static CarInfoClient instance;
    private static Handler handler;
    private static boolean isLooping = false;
    private boolean isInited = false;
    private ClientCallback carSpeedCallback;

    private CarInfoClient() {
        super(AppHolder.getInstance().getAppContext(), CLIENT_PORT);
    }

    public static CarInfoClient getInstance() {
        if (instance == null) {
            synchronized (CarInfoClient.class) {
                if (instance == null) {
                    instance = new CarInfoClient();
                }
            }
        }
        return instance;
    }

    public static void startLoop() {
        if (isLooping) {
            return;
        }
        isLooping = true;
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                CarInfoClient.getInstance().notifyCarSpeed(12.34f);
                handler.postDelayed(this, 5 * 1000);
            }
        }, 5 * 1000);
    }

    public void init() {
        if (isInited) {
            return;
        }
        isInited = false;
        Center.getInstance().runAfterConnected(new Runnable() {
            @Override
            public void run() {
                Center.getInstance().register(CarInfoClient.getInstance());
                StateManager.getInstance().addStateCallback(new StateManager.StateListener() {
                    @Override
                    public void onClientOut(SourceInfo source) {
                        if (source.equals(getSource())) {
                            isInited = false;
                            StateManager.getInstance().removeCallback(this);
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onReceive(int action, Bundle data) {
    }

    @Override
    protected void onRequest(int action, Bundle data, ClientCallback callback) {
    }

    @Override
    protected void onConnect(int action, Bundle data, ClientCallback callback) {
        Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
                + "\n * onConnect: " + action);
        if (CAR_SPEED_ACTION == action) {
            carSpeedCallback = callback;
        }
    }

    public void notifyCarSpeed(float carSpeed) {
        if (!isConnectActionValid(CAR_SPEED_ACTION)) {
            carSpeedCallback = null;
        }
        if (carSpeedCallback == null) {
            Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
                    + "\n * notifyCarSpeed: no callback");
            return;
        } else {
            Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
                    + "\n * notifyCarSpeed: " + carSpeed);
        }
        Bundle data = new Bundle();
        data.putFloat("carSpeed", carSpeed);
        carSpeedCallback.setData(data);
        carSpeedCallback.callback();
    }
}
