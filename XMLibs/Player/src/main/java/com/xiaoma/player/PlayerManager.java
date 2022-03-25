package com.xiaoma.player;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static com.xiaoma.player.PlayerConstants.ACTION_PLAYER_PREPARED;

/**
 * @author youthyJ
 * @date 2018/11/7
 */
public class PlayerManager {
    private static final String TAG = PlayerManager.class.getSimpleName() + "_TAG";
    private static final String SERVICE_PACKAGE_NAME = "com.xiaoma.launcher";
    private static final String SERVICE_CLASS_NAME = "com.xiaoma.launcher.player.LauncherPlayerService";
    private static PlayerManager instance;
    private Context appContext;
    private IPlayer service;
    private List<OnPlayerPreparedListener> listeners = new ArrayList<>();

    public static PlayerManager getInstance() {
        if (instance == null) {
            synchronized (PlayerManager.class) {
                if (instance == null) {
                    instance = new PlayerManager();
                }
            }
        }
        return instance;
    }

    public void init(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }
        appContext = context.getApplicationContext();
        boolean bindSuccess = bindPlayerService();
        if (!bindSuccess) {
            listenPlayer();
        }
    }

    private PlayerManager() {
    }

    private boolean bindPlayerService() {
        Intent intent = new Intent();
        ComponentName cn = new ComponentName(SERVICE_PACKAGE_NAME, SERVICE_CLASS_NAME);
        intent.setComponent(cn);
        intent.putExtra("packageName", appContext.getPackageName());
        ServiceConnection conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "on player service connected");
                PlayerManager.this.service = IPlayer.Stub.asInterface(service);
                notifyPlayerPrepared();
                handlePlayerDead(PlayerManager.this.service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "on player service disconnected");
                service = null;
                listenPlayer();
            }
        };
        boolean bindState = appContext.bindService(intent, conn, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "bind player service: [" + bindState + "]");
        return bindState;
    }

    private void listenPlayer() {
        Log.d(TAG, "listen player service broadcast");
        IntentFilter filter = new IntentFilter(ACTION_PLAYER_PREPARED);
        appContext.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "receive player service broadcast");
                bindPlayerService();
                appContext.unregisterReceiver(this);
            }
        }, filter);
    }

    private boolean isServicePrepared() {
        return service != null;
    }

    private void handlePlayerDead(IPlayer player) {
        if (player == null) {
            return;
        }
        IBinder binder = player.asBinder();
        if (!binder.isBinderAlive()) {
            this.service = null;
            return;
        }
        try {
            binder.linkToDeath(new IBinder.DeathRecipient() {
                @Override
                public void binderDied() {
                    Log.d(TAG, "on player service dead");
                    PlayerManager.this.service = null;
                    notifyPlayerDeed();
                }
            }, 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void notifyPlayerPrepared() {
        for (OnPlayerPreparedListener l : listeners) {
            if (l == null) {
                continue;
            }
            try {
                l.onPlayerPrepared();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void notifyPlayerDeed() {
        for (OnPlayerPreparedListener l : listeners) {
            if (l == null) {
                continue;
            }
            try {
                l.onPlayerDead();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public boolean setAudio(IAudio audio) {
        if (audio == null) {
            return false;
        }
        if (service == null) {
            return false;
        }
        if (!service.asBinder().isBinderAlive()) {
            return false;
        }
        try {
            service.setAudio(audio);
            return true;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addOnPlayerServiceListener(OnPlayerPreparedListener l) {
        if (l == null) {
            return false;
        }
        if (isServicePrepared()) {
            l.onPlayerPrepared();
            return true;
        }
        if (listeners.contains(l)) {
            return false;
        }
        return listeners.add(l);
    }

    public boolean removeOnPlayerServiceListener(OnPlayerPreparedListener l) {
        return listeners.remove(l);
    }

    public interface OnPlayerPreparedListener {
        void onPlayerPrepared();

        void onPlayerDead();
    }
}
