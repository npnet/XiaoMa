package com.xiaoma.alive;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.xiaoma.center.logic.local.Center;
import com.xiaoma.center.logic.local.StateManager;
import com.xiaoma.center.logic.remote.Client;
import com.xiaoma.center.logic.remote.ClientCallback;

/**
 * @author youthyJ
 * @date 2019/3/28
 */
public class AliveClient extends Client {
    public static final int CLIENT_PORT = 3757;
    private static final String TAG = AliveClient.class.getSimpleName() + "_LOG";
    private static AliveClient instance;

    public AliveClient(Context context, int port) {
        super(context, port);
    }

    public static void register(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }
        final Context appContext = context.getApplicationContext();
        final AliveClient client = getInstance(appContext);
        Center.getInstance().init(appContext);
        Center.getInstance().runAfterConnected(new Runnable() {
            @Override
            public void run() {
                final boolean register = Center.getInstance().register(client);
                Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
                        + "\n * register client: " + register);
                StateManager.getInstance().addStateCallback(new StateManager.StateListener() {
                    @Override
                    public void onPrepare(String depend) {
                        StateManager.getInstance().removeCallback(this);
                        register(appContext);
                    }
                });
            }
        });
    }

    private static AliveClient getInstance(Context context) {
        if (instance == null) {
            synchronized (AliveClient.class) {
                if (instance == null) {
                    instance = new AliveClient(context, CLIENT_PORT);
                }
            }
        }
        return instance;
    }

    @Override
    protected void onReceive(int action, Bundle data) {
        // empty
    }

    @Override
    protected void onRequest(int action, Bundle data, ClientCallback callback) {
        // empty
    }

    @Override
    protected void onConnect(int action, Bundle data, ClientCallback callback) {
        // empty
    }
}
