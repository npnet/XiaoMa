package com.xiaoma.hotfix.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author youthyJ
 * @date 2018/11/14
 */
public class NetworkStatusUtils {
    public static BroadcastReceiver listenNetworkStatusChange(Context context, final Listener listener) {
        if (context == null || listener == null) {
            return null;
        }
        final Context appContext = context.getApplicationContext();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (!ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                    return;
                }
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isConnected()) {
                    listener.onNetworkStatusChange(false);
                } else {
                    listener.onNetworkStatusChange(true);
                }
            }
        };
        appContext.registerReceiver(receiver, filter);
        return receiver;
    }

    public interface Listener {
        void onNetworkStatusChange(boolean active);
    }
}
