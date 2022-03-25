package com.xiaoma.bluetooth.phone.common.manager;

import android.os.RemoteException;

import com.xiaoma.carlib.wheelcontrol.OnWheelKeyListener;
import com.xiaoma.carlib.wheelcontrol.WheelKeyEvent;
import com.xiaoma.carlib.wheelcontrol.XmWheelManager;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author ZiXu Huang
 * @Data 2019/5/17
 * @DES 方控接听挂断电话
 */
public class WheelOperatePhoneManager {
    private static WheelOperatePhoneManager manager;
    private Map<String, OnWheelKeyListener> listeners = new HashMap<>();
    private List<OnWheelOperatePhoneListener> onWheelOperatePhoneListeners = new ArrayList<>();

    public static WheelOperatePhoneManager getInstance() {
        if (manager == null) {
            manager = new WheelOperatePhoneManager();
        }
        return manager;
    }

    /**
     * @param phoneNum 注册监听的电话号码
     */
    public void registerCarLibListener(final String phoneNum) {
        KLog.d("hzx", "register Wheel");
        if (listeners.containsKey(phoneNum)) {
            return;
        }
        OnWheelKeyListener carLibListener = null;
        XmWheelManager.getInstance().register(carLibListener = new OnWheelKeyListener.Stub() {
            @Override
            public boolean onKeyEvent(final int keyAction, final int keyCode) throws RemoteException {
                KLog.d("hzx","keyCode:"+ keyCode + ", keyAction: " + keyAction);
                ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                    @Override
                    public void run() {
                        if (WheelKeyEvent.KEYCODE_WHEEL_VOICE == keyCode) {
                            if (keyAction == WheelKeyEvent.ACTION_CLICK) {
                                for (OnWheelOperatePhoneListener listener: onWheelOperatePhoneListeners) {
                                    listener.answerPhone(phoneNum);
                                }
                            }
                        } else if (WheelKeyEvent.KEYCODE_WHEEL_MUTE == keyCode) {
                            if (keyAction == WheelKeyEvent.ACTION_CLICK) {
                                for (OnWheelOperatePhoneListener listener: onWheelOperatePhoneListeners)
                                    listener.hangupPhone(phoneNum);

                            }
                        }
                    }
                });

                return true;
            }

            @Override
            public String getPackageName() throws RemoteException {
                return "com.xiaoma.bluetooth.phone";
            }
        }, new int[]{WheelKeyEvent.KEYCODE_WHEEL_VOICE, WheelKeyEvent.KEYCODE_WHEEL_MUTE,
                WheelKeyEvent.KEYCODE_WHEEL_SEEK_SUB, WheelKeyEvent.KEYCODE_WHEEL_SEEK_ADD});

        listeners.put(phoneNum, carLibListener);
    }

    /**
     * @param phoneNum 反注册的电话号码
     */
    public void unregisterCarLibListener(String phoneNum) {
        KLog.d("hzx", "unregister Wheel, phoneNum is " + phoneNum);
        if (listeners.size() != 0) {
            if (listeners.containsKey(phoneNum)) {
                OnWheelKeyListener onWheelKeyListener = listeners.get(phoneNum);
                listeners.remove(phoneNum);
                XmWheelManager.getInstance().unregister(onWheelKeyListener);
            }
        }
    }

    public void unregisterAllCarLibListener(){
        if (listeners.size() != 0) {
            Set<String> strings = listeners.keySet();
            for (String key : strings) {
                OnWheelKeyListener onWheelKeyListener = listeners.get(key);
                XmWheelManager.getInstance().unregister(onWheelKeyListener);
            }
        }
        listeners.clear();
    }

    public void setOnWheelOperatePhoneListener(OnWheelOperatePhoneListener listener) {
        if (!onWheelOperatePhoneListeners.contains(listener)) {
            onWheelOperatePhoneListeners.add(listener);
        }
    }

    public void unregisterPhoneListener(OnWheelOperatePhoneListener listener){
        if (onWheelOperatePhoneListeners.size() != 0) {
            if (onWheelOperatePhoneListeners.contains(listener)) {
                onWheelOperatePhoneListeners.remove(listener);
            }
        }
    }

    public interface OnWheelOperatePhoneListener {
        void answerPhone(String phoneNum);

        void hangupPhone(String phoneNum);
    }

}
