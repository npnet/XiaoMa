package com.xiaoma.center.logic.local;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;

import com.xiaoma.center.ICenterServer;
import com.xiaoma.center.IServerListener;
import com.xiaoma.center.logic.model.SourceInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 本地的中心服务状态分发器,负责
 * 1、分发中心服务的重要状态
 *
 * @author youthyJ
 * @date 2019/1/15
 */
public class StateManager {
    private static final String TAG = StateManager.class.getSimpleName() + "_LOG";
    private static StateManager instance;
    private List<StateListener> callbackList = new ArrayList<>();
    private IServerListener serverListener;

    private StateManager() {
    }

    public static StateManager getInstance() {
        if (instance == null) {
            synchronized (StateManager.class) {
                if (instance == null) {
                    instance = new StateManager();
                }
            }
        }
        return instance;
    }

    void handleServiceListener() throws RemoteException {
        if (!Center.getInstance().isConnected()) {
            return;
        }
        serverListener = new IServerListener.Stub() {
            @Override
            public void onClientIn(SourceInfo in) {
                Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
                        + "\n * onClientIn: " + in);
                StateManager.getInstance().onClientIn(in);
            }

            @Override
            public void onClientOut(SourceInfo out) {
                Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
                        + "\n * onClientOut: " + out);
                StateManager.getInstance().onClientOut(out);
            }
        };
        ICenterServer centerServer = Center.getInstance().getCenterServer();
        centerServer.addServerListener(serverListener);
    }

    public boolean addStateCallback(StateListener callback) {
        if (callback == null) {
            return false;
        }
        if (callbackList.contains(callback)) {
            return true;
        }
        return callbackList.add(callback);
    }

    public boolean removeCallback(StateListener callback) {
        return callbackList.remove(callback);
    }

    void onPrepare(String depend) {
        for (int i = callbackList.size() - 1; i >= 0; i--) {
            StateListener callback = callbackList.get(i);
            if (callback == null) {
                continue;
            }
            try {
                callback.onPrepare(depend);
            } catch (Exception e) {
                Log.e(TAG, "at " + new Throwable().getStackTrace()[0]
                        + "\n * callback onPrepare exception: "
                        + "\n" + Log.getStackTraceString(e));
            }
        }
    }

    void onBindService(boolean bindState) {
        for (int i = callbackList.size() - 1; i >= 0; i--) {
            StateListener callback = callbackList.get(i);
            if (callback == null) {
                continue;
            }
            try {
                callback.onBindService(bindState);
            } catch (Exception e) {
                Log.e(TAG, "at " + new Throwable().getStackTrace()[0]
                        + "\n * callback onBindService exception: "
                        + "\n" + Log.getStackTraceString(e));
            }
        }
    }

    void onConnected() {
        for (int i = callbackList.size() - 1; i >= 0; i--) {
            StateListener callback = callbackList.get(i);
            if (callback == null) {
                continue;
            }
            try {
                callback.onConnected();
            } catch (Exception e) {
                Log.e(TAG, "at " + new Throwable().getStackTrace()[0]
                        + "\n * callback onConnected exception: "
                        + "\n" + Log.getStackTraceString(e));
            }
        }
    }

    void onClientIn(SourceInfo in) {
        for (int i = callbackList.size() - 1; i >= 0; i--) {
            StateListener callback = callbackList.get(i);
            if (callback == null) {
                continue;
            }
            try {
                callback.onClientIn(in);
            } catch (Exception e) {
                Log.e(TAG, "at " + new Throwable().getStackTrace()[0]
                        + "\n * callback onClientIn exception: "
                        + "\n" + Log.getStackTraceString(e));
            }
        }
    }

    void onClientOut(SourceInfo out) {
        for (int i = callbackList.size() - 1; i >= 0; i--) {
            StateListener callback = callbackList.get(i);
            if (callback == null) {
                continue;
            }
            try {
                callback.onClientOut(out);
            } catch (Exception e) {
                Log.e(TAG, "at " + new Throwable().getStackTrace()[0]
                        + "\n * callback onClientOut exception: "
                        + "\n" + Log.getStackTraceString(e));
            }
        }
    }

    void onDisconnected() {
        serverListener = null;
        for (int i = callbackList.size() - 1; i >= 0; i--) {
            StateListener callback = callbackList.get(i);
            if (callback == null) {
                continue;
            }
            try {
                callback.onDisconnected();
            } catch (Exception e) {
                Log.e(TAG, "at " + new Throwable().getStackTrace()[0]
                        + "\n * callback onDisconnected exception: "
                        + "\n" + Log.getStackTraceString(e));
            }
        }
    }

    public static class StateListener {
        /**
         * 在 Launcher 启动并调起 CenterService 成功后，会被调用。防止先没安装Launcher时，、
         * 启动Xting等应用Center会init不成功，Client也注册不成功。
         * 之后安装好Launcher后，通知其他Client端进行重新init 并重新注册。
         *
         * @param depend 暂时无用的参数
         */
        public void onPrepare(String depend) {
        }

        public void onBindService(boolean bindState) {

        }

        public void onConnected() {
        }

        public void onDisconnected() {
        }

        public void onClientIn(SourceInfo source) {
        }

        public void onClientOut(SourceInfo source) {
        }
    }


    public static class LauncherOnReceive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            getInstance().onPrepare(null);
        }
    }
}
