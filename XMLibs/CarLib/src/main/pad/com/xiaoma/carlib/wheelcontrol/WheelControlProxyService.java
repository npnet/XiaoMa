package com.xiaoma.carlib.wheelcontrol;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.xiaoma.utils.constant.VrConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by LKF on 2019-5-6 0006.
 * <p>方控代理服务,因为原服务需要注册权限,并且耦合了伟世通的业务;
 * <p>为了解耦,这里单独提供一个代理服务给其他APP.
 */
@SuppressLint("LogNotTimber")
public class WheelControlProxyService extends Service {
    private static final String TAG = "WCPService";
    private final WheelKeyListeners.Stub mWheelKeyListeners = new WheelKeyListenersImpl();
    private final RemoteCallbackList<OnWheelKeyListener> mCallbackList = new RemoteCallbackList<OnWheelKeyListener>() {
        @Override
        public void onCallbackDied(OnWheelKeyListener callback) {
            super.onCallbackDied(callback);
            mListeningKeyCodes.remove(callback.asBinder());
        }
    };
    private final Map<IBinder, Set<Integer>> mListeningKeyCodes = new WeakHashMap<>();
    private final Map<IBinder, Long> mListenerCounters = new WeakHashMap<>();
    private final AtomicLong mCounter = new AtomicLong(Long.MAX_VALUE);

    private BroadcastReceiver mReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver = new WheelKeyEventReceiver(),
                new IntentFilter(WheelConstant.ACTION_WHEEL_KEY_EVENT));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, String.format("onBind( intent: %s )", intent));
        return mWheelKeyListeners;
    }

    private class WheelKeyEventReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 根据需求,收到方控按键则自动亮屏
            sendBroadcast(new Intent(VrConstants.ActionScreen.TURN_ON_SCREEN_ACTION)
                    .setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES));

            final int keyAction = intent.getIntExtra(WheelConstant.EXTRA_KEY_ACTION, -1);
            final int keyCode = intent.getIntExtra(WheelConstant.EXTRA_KEY_CODE, -1);
            StringBuilder log = new StringBuilder(
                    String.format("WheelKeyEventReceiver # onReceive(){ keyAction: %s, keyCode: %s }", keyAction, keyCode));

            int num = mCallbackList.beginBroadcast();
            List<OnWheelKeyListener> activeListeners = new ArrayList<>(num);
            while (num > 0) {
                num--;
                activeListeners.add(mCallbackList.getBroadcastItem(num));
            }
            // 按照最新注册的监听排在前头排序
            Collections.sort(activeListeners, new Comparator<OnWheelKeyListener>() {
                @Override
                public int compare(OnWheelKeyListener l1, OnWheelKeyListener l2) {
                    Long counter1 = mListenerCounters.get(l1.asBinder());
                    Long counter2 = mListenerCounters.get(l2.asBinder());
                    return Long.compare(
                            counter1 != null ? counter1 : Long.MAX_VALUE,
                            counter2 != null ? counter2 : Long.MAX_VALUE);
                }
            });
            for (OnWheelKeyListener listener : activeListeners) {
                Set<Integer> keyCodeSet = mListeningKeyCodes.get(listener.asBinder());
                if (keyCodeSet != null && keyCodeSet.contains(keyCode)) {
                    boolean consumed = false;
                    try {
                        consumed = listener.onKeyEvent(keyAction, keyCode);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    String pkgName = null;
                    try {
                        pkgName = listener.getPackageName();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    log.append(String.format("Handled by < %s >: < keyCodeSet: %s >  consumed: %s",
                            pkgName, keyCodeSet, consumed));
                    if (consumed)
                        break;
                }
            }
            mCallbackList.finishBroadcast();

            Log.i(TAG, log.toString());
        }
    }

    private class WheelKeyListenersImpl extends WheelKeyListeners.Stub {
        @Override
        public void register(OnWheelKeyListener listener, int[] keyCodes) {
            if (listener == null || keyCodes == null || keyCodes.length <= 0)
                return;
            String callingPkg = null;
            try {
                callingPkg = listener.getPackageName();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Log.i(TAG, String.format("register( keyCodes: %s ){ callingPkg: %s }",
                    Arrays.toString(keyCodes), callingPkg));

            // 注册监听
            try {
                mCallbackList.register(listener);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Set<Integer> keyCodeSet = new HashSet<>(keyCodes.length);
            for (final int code : keyCodes) {
                keyCodeSet.add(code);
            }
            mListeningKeyCodes.put(listener.asBinder(), keyCodeSet);
            mListenerCounters.put(listener.asBinder(), mCounter.getAndAdd(-1));
        }

        @Override
        public void unregister(OnWheelKeyListener listener) {
            if (listener == null)
                return;
            String callingPkg = null;
            try {
                callingPkg = listener.getPackageName();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Log.i(TAG, String.format("unregister(){ callingPkg: %s }", callingPkg));
            mListenerCounters.remove(listener.asBinder());
            mListeningKeyCodes.remove(listener.asBinder());
            try {
                mCallbackList.unregister(listener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
