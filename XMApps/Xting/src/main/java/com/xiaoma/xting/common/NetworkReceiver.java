package com.xiaoma.xting.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.xting.R;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : wutao
 *     time   : 2018/11/30
 *     desc   :
 * </pre>
 */
public class NetworkReceiver extends BroadcastReceiver {
    private final static String TAG = NetworkReceiver.class.getName();
    private static List<NetWorkCallback> mWorkCallbackList = new ArrayList<>();

    public static void addNetWorkListener(NetWorkCallback callback) {
        mWorkCallbackList.add(callback);
    }

    public static void removeNetWorkListener(NetWorkCallback callback) {
        mWorkCallbackList.remove(callback);
    }

    public static void removeAllNetWorkListener() {
        mWorkCallbackList.clear();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // 监听网络连接，包括wifi和移动数据的打开和关闭,以及连接上可用的连接都会接到监听
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            //获取联网状态的NetworkInfo对象
            NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (info != null) {
                //如果当前的网络连接成功并且网络连接可用
                if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
                    if (info.getType() == ConnectivityManager.TYPE_WIFI
                            || info.getType() == ConnectivityManager.TYPE_MOBILE) {
                        XMToast.showToast(context, R.string.net_connect);
                        for (NetWorkCallback callback : mWorkCallbackList) {
                            if (callback != null) {
                                callback.onNetWorkConnect();
                            }
                        }

                    }
                } else {
                    XMToast.showToast(context, R.string.net_not_connect);
                    for (NetWorkCallback callback : mWorkCallbackList) {
                        if (callback != null) {
                            callback.onNetworkDisconnect();
                        }
                    }
                }
            }
        }
    }

    public interface NetWorkCallback {
        void onNetworkDisconnect();

        void onNetWorkConnect();
    }
}
