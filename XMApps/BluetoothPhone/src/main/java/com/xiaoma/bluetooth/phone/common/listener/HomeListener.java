package com.xiaoma.bluetooth.phone.common.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * @author: iSun
 * @date: 2018/11/19 0019
 */
public class HomeListener {
    private static final String TAG = HomeListener.class.getSimpleName();
    private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
    private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
    private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
    private static final String SYSTEM_DIALOG_REASON_ASSIST = "assist";

    private KeyFun mKeyFun;
    private Context mContext;
    private IntentFilter mHomeBtnIntentFilter = null;
    private HomeReceiver mHomeBtnReceiver = null;

    public HomeListener(Context context) {
        mContext = context;
        mHomeBtnIntentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        mHomeBtnReceiver = new HomeReceiver();
    }

    public HomeListener startListen() {
        if (mContext != null) {
            try {
                mContext.registerReceiver(mHomeBtnReceiver, mHomeBtnIntentFilter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "mContext is null and startListen fail");
        }
        return this;
    }

    public void stopListen() {
        if (mContext != null) {
            try {
                mContext.unregisterReceiver(mHomeBtnReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "mContext is null and stopListen fail");
        }
    }

    public HomeListener setInterface(KeyFun keyFun) {
        mKeyFun = keyFun;
        return this;
    }

    class HomeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null) {
                    if (null != mKeyFun) {
                        if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {
                            //Home键
                            mKeyFun.onHome();
                        } else if (SYSTEM_DIALOG_REASON_RECENT_APPS.equals(reason)) {
                            //最近任务
                            mKeyFun.onRecent();
                        } else if (SYSTEM_DIALOG_REASON_ASSIST.equals(reason)) {
                            //长按home
                            mKeyFun.onLongHome();
                        }
                    }
                }
            }
        }
    }

    public interface KeyFun {
        //短按Home
        public void onHome();

        //最近任务键
        public void onRecent();

        //长按Home
        public void onLongHome();
    }
}


