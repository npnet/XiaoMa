package com.xiaoma.mqtt.service;

import android.content.Context;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import com.xiaoma.mqtt.IMqtt;
import com.xiaoma.mqtt.IMqttCallback;
import com.xiaoma.thread.ThreadDispatcher;

/**
 * Created by Thomas on 2019/6/4 0004
 * push service do handle
 */

class XmPushServiceHandle {

    private static XmPushServiceHandle instance = new XmPushServiceHandle();
    private RemoteCallbackList<IMqttCallback> mCallBacks = new RemoteCallbackList<>();
    private IMqtt.Stub stub;

    public static XmPushServiceHandle getInstance() {
        return instance;
    }

    public IMqtt.Stub init(final Context context) {
        stub = new IMqtt.Stub() {
            @Override
            public void init(String uid) throws RemoteException {
                MqttManager.getInstance().init(uid, context);
            }

            @Override
            public void release() throws RemoteException {
                MqttManager.getInstance().release();
            }

            @Override
            public void publishTopic(final String topic, final String message) throws RemoteException {
                ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
                    @Override
                    public void run() {
                        MqttManager.getInstance().publishMessage(topic, message);
                    }
                });
            }

            @Override
            public void registerIMqttCallback(IMqttCallback iMqttCallback) throws RemoteException {
                mCallBacks.register(iMqttCallback);
            }

            @Override
            public void unRegisterIMqttCallback(IMqttCallback iMqttCallback) throws RemoteException {
                mCallBacks.unregister(iMqttCallback);
            }
        };
        return stub;
    }

    public synchronized void onSuccessConnect() {
        try {
            final int len = mCallBacks.beginBroadcast();
            for (int i = 0; i < len; i++) {
                try {
                    mCallBacks.getBroadcastItem(i).onSuccessConnect();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mCallBacks.finishBroadcast();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void onFailConnect() {
        try {
            final int len = mCallBacks.beginBroadcast();
            for (int i = 0; i < len; i++) {
                try {
                    mCallBacks.getBroadcastItem(i).onFailConnect();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mCallBacks.finishBroadcast();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
