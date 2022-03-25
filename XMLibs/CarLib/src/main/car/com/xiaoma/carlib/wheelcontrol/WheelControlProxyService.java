package com.xiaoma.carlib.wheelcontrol;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;

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

    // 仪表按键 begin
    private static final Set<Integer> LCD_KEY_CODES;

    static {
        LCD_KEY_CODES = new HashSet<>(Arrays.asList(
                WheelKeyEvent.KEYCODE_WHEEL_UP,
                WheelKeyEvent.KEYCODE_WHEEL_DOWN,
                WheelKeyEvent.KEYCODE_WHEEL_LEFT,
                WheelKeyEvent.KEYCODE_WHEEL_RIGHT,
                WheelKeyEvent.KEYCODE_WHEEL_OK,
                WheelKeyEvent.KEYCODE_WHEEL_BACK
        ));
    }

    // 仪表按键 end

    public static final int LONG_PRESS_TIME_MS = 1000;// 定义:长按按下时长
    private final Handler mHandler = new Handler();
    private final SparseArray<KeyState> mKeyStateMap = new SparseArray<>();

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
        registerReceiver(mReceiver = new WheelKeyEventReceiver(),
                new IntentFilter(WheelConstant.ACTION_WHEEL_KEY_EVENT));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, String.format("onBind( intent: %s )", intent));
        return mWheelKeyListeners;
    }

    private void dispatchKeyEvent(int keyAction, int keyCode) {
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
                Log.i(TAG, String.format("dispatchKeyEvent{ keyAction: %s, keyCode: %s } < pgk: %s >: < keyCodeSet: %s >  consumed: %s",
                        keyAction, keyCode, pkgName, keyCodeSet, consumed));
                if (consumed)
                    break;
            }
        }
        mCallbackList.finishBroadcast();
    }

    private class WheelKeyEventReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 根据需求,收到方控按键则自动亮屏
            sendBroadcast(new Intent(VrConstants.ActionScreen.TURN_ON_SCREEN_ACTION)
                    .setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES));

            final int keyAction = intent.getIntExtra(WheelConstant.EXTRA_KEY_ACTION, -1);
            final int keyCode = intent.getIntExtra(WheelConstant.EXTRA_KEY_CODE, -1);
            Log.i(TAG, String.format("WheelKeyEventReceiver # onReceive(){ keyAction: %s, keyCode: %s }", keyAction, keyCode));

            // 仪表按键只会给抬起事件,所以直接把仪表的抬起当做CLICK传递出去
            if (LCD_KEY_CODES.contains(keyCode)) {
                dispatchKeyEvent(WheelKeyEvent.ACTION_CLICK, keyCode);
                return;
            }

            KeyState keyState = mKeyStateMap.get(keyCode);
            switch (keyAction) {
                case WheelKeyEvent.ACTION_PRESS: {
                    if (keyState != null) {
                        Log.e(TAG, "Illegal state, [ACTION_PRESS] happened, but last [ACTION_RELEASE] didn't called !!!");
                    } else {
                        mKeyStateMap.put(keyCode, keyState = new KeyState(keyAction, keyCode));
                        mHandler.postDelayed(keyState.longPressAction, LONG_PRESS_TIME_MS);
                    }
                    break;
                }
                case WheelKeyEvent.ACTION_RELEASE: {
                    if (keyState != null) {
                        mHandler.removeCallbacks(keyState.longPressAction);
                        mKeyStateMap.remove(keyCode);
                        if (!keyState.isLongPressCalled) {
                            dispatchKeyEvent(WheelKeyEvent.ACTION_CLICK, keyCode);
                        }
                    } else {
                        Log.e(TAG, "Illegal state, [ACTION_RELEASE] happened without a [ACTION_PRESS] before !!!");
                    }
                    break;
                }
                default: {
                    Log.e(TAG, String.format("Unknown keyAction: %s", keyAction));
                    if (keyState != null) {
                        mHandler.removeCallbacks(keyState.longPressAction);
                        mKeyStateMap.remove(keyCode);
                    }
                    break;
                }
            }
            dispatchKeyEvent(keyAction, keyCode);
        }
    }

    private class KeyState {
        int keyAction;
        int keyCode;
        boolean isLongPressCalled = false;
        final Runnable longPressAction = new Runnable() {
            @Override
            public void run() {
                isLongPressCalled = true;
                dispatchKeyEvent(WheelKeyEvent.ACTION_LONG_PRESS, keyCode);
            }
        };

        KeyState(int keyAction, int keyCode) {
            this.keyAction = keyAction;
            this.keyCode = keyCode;
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
