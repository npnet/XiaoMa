package com.xiaoma.center.logic.local;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.xiaoma.center.ICenterServer;
import com.xiaoma.center.logic.CenterConstants;
import com.xiaoma.center.logic.model.SourceInfo;
import com.xiaoma.center.logic.remote.Client;
import com.xiaoma.utils.log.KLog;

/**
 * 中心服务在本地的映射,负责:
 * 1、绑定远程服务
 * 2、注册客户端到中心服务
 * 3、查询客户端是否活跃
 *
 * @author youthyJ
 * @date 2019/1/14
 */
public class Center {
    private static final String TAG = Center.class.getSimpleName() + "_LOG";
    private static Center instance;
    private Context appContext;
    private ICenterServer centerServer;
    private boolean inited;

    public static Center getInstance() {
        if (instance == null) {
            synchronized (Center.class) {
                if (instance == null) {
                    instance = new Center();
                }
            }
        }
        return instance;
    }

    public boolean init(Context context) {
        if (context == null) {
            return false;
        }
        if (inited) {
            return true;
        }
        inited = true;
        appContext = context.getApplicationContext();
        return bindService();
    }

    public boolean isConnected() {
        return centerServer != null;
    }

    public void runAfterConnected(final Runnable task) {
        if (isConnected()) {
            task.run();
            return;
        }
        StateManager.getInstance().addStateCallback(new StateManager.StateListener() {
            @Override
            public void onConnected() {
                task.run();
                StateManager.getInstance().removeCallback(this);
            }
        });
    }

    public boolean register(Client client) {
        if (!isConnected()) {
            return false;
        }
        if (client == null) {
            return false;
        }
        if (centerServer == null) {
            return false;
        }
        try {
            centerServer.register(client.getSource(), client);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean isClientAlive(SourceInfo remote) {
        if (centerServer == null) {
            return false;
        }
        try {
            return centerServer.isClientAlive(remote);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    ICenterServer getCenterServer() {
        return centerServer;
    }

    private boolean bindService() {
        if (centerServer != null) {
            return true;
        }
        Intent intent = new Intent();
        ComponentName cn = new ComponentName(CenterConstants.DEPEND_PACKAGE_NAME,
                CenterConstants.SERVICE_CLASS_NAME);
        intent.setComponent(cn);
        ServiceConnection conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                KLog.d(TAG, "at " + new Throwable().getStackTrace()[0]
                        + "\n * onServiceConnected");
                centerServer = ICenterServer.Stub.asInterface(service);
                try {
                    StateManager.getInstance().handleServiceListener();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                StateManager.getInstance().onConnected();

                // 在 Launcher bind service成功后，发送一个绑定成功的广播，通知之前未注册成功的Client，重新注册
                if (appContext.getPackageName().equals(CenterConstants.DEPEND_PACKAGE_NAME)) {
                    Intent intent = new Intent();
                    intent.setAction(CenterConstants.RECEIVER_ACTION);
                    appContext.sendBroadcast(intent);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                KLog.d(TAG, "at " + new Throwable().getStackTrace()[0]
                        + "\n * onServiceDisconnected");
                centerServer = null;
                inited = false;
                StateManager.getInstance().onDisconnected();
            }
        };
        boolean state = appContext.bindService(intent, conn, Context.BIND_AUTO_CREATE);
        inited = state;
        KLog.d(TAG, "at " + new Throwable().getStackTrace()[0]
                + "\n * bindService state: " + state);
        StateManager.getInstance().onBindService(state);
        return state;
    }

}
