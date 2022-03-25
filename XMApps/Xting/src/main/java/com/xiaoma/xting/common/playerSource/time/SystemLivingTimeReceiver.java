package com.xiaoma.xting.common.playerSource.time;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/6/20
 */
public class SystemLivingTimeReceiver extends BroadcastReceiver {

    private static final String TAG = SystemLivingTimeReceiver.class.getSimpleName();
    private boolean hasRegisterF;

    private CopyOnWriteArrayList<ISystemTimeChangeListener> mTimeChangeListeners;

    private SystemLivingTimeReceiver() {
        hasRegisterF = false;
        mTimeChangeListeners = new CopyOnWriteArrayList<>();
    }

    public static SystemLivingTimeReceiver newSingleton() {
        return Holder.sINSTANCE;
    }

    public void register(Context context, ISystemTimeChangeListener listener) {
        Log.d(TAG, "{register}-[mReceiver / contains] : " + hasRegisterF + " / " + mTimeChangeListeners.contains(listener));
        if (!hasRegisterF) {
            hasRegisterF = true;
            context.registerReceiver(this, getIntentFilter());
        }

        if (!mTimeChangeListeners.contains(listener)) {
            listener.onSystemTimeChanged();
            mTimeChangeListeners.add(listener);
        }
    }

    public void unRegisterReceiver(Context context, ISystemTimeChangeListener listener) {
        Log.d(TAG, "{unRegisterReceiver}-[] : " + hasRegisterF);
        if (hasRegisterF) {
            hasRegisterF = false;
            unRegisterReceiver(listener);
            try {
                context.unregisterReceiver(this);
            } catch (Exception e) {
                Log.e(TAG, "{unRegisterReceiver}-[] : ");
            }
        }
    }

    public void unRegisterReceiver(ISystemTimeChangeListener listener) {
        mTimeChangeListeners.remove(listener);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_TIME_TICK.equals(action)) {
            Log.d(TAG, "{onReceive}-[ACTION_TIME_TICK] : " + mTimeChangeListeners.size());
            if (!mTimeChangeListeners.isEmpty()) {
                for (ISystemTimeChangeListener timeChangeListener : mTimeChangeListeners) {
                    if (timeChangeListener == null) {
                        continue;
                    }
                    try {
                        timeChangeListener.onMinuteChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (Intent.ACTION_TIME_CHANGED.equals(action)) {
            Log.d(TAG, "{onReceive}-[ACTION_TIME_CHANGED] : " + mTimeChangeListeners.size());
            if (!mTimeChangeListeners.isEmpty()) {
                for (ISystemTimeChangeListener timeChangeListener : mTimeChangeListeners) {
                    if (timeChangeListener == null) {
                        continue;
                    }
                    try {
                        timeChangeListener.onSystemTimeChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (Intent.ACTION_DATE_CHANGED.equals(action)) {
            Log.d(TAG, "{onReceive}-[ACTION_DATE_CHANGED] : " + mTimeChangeListeners.size());
            if (!mTimeChangeListeners.isEmpty()) {
                for (ISystemTimeChangeListener timeChangeListener : mTimeChangeListeners) {
                    if (timeChangeListener == null) {
                        continue;
                    }
                    try {
                        timeChangeListener.onSystemDateChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
        return intentFilter;
    }

    interface Holder {
        SystemLivingTimeReceiver sINSTANCE = new SystemLivingTimeReceiver();
    }
}
