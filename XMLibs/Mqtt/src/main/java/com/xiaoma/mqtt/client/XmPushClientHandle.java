package com.xiaoma.mqtt.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import com.xiaoma.mqtt.IMqtt;
import com.xiaoma.mqtt.IMqttCallback;
import com.xiaoma.mqtt.listener.MqttListener;
import com.xiaoma.mqtt.service.XmPushService;
import com.xiaoma.utils.log.KLog;

/**
 * Created by Thomas on 2019/6/4 0004
 * push client do handle
 */

class XmPushClientHandle {

    private static XmPushClientHandle instance = new XmPushClientHandle();
    private IMqtt mService;
    private Context context;
    private String currentUid = "";
    private MqttListener mqttListener;
    private IMqttCallback iMqttCallback = new IMqttCallback.Stub() {
        @Override
        public void onSuccessConnect() throws RemoteException {
            if (mqttListener != null) {
                mqttListener.onSuccessConnect();
            }
        }

        @Override
        public void onFailConnect() throws RemoteException {
            if (mqttListener != null) {
                mqttListener.onFailConnect();
            }
        }
    };

    public static XmPushClientHandle getInstance() {
        return instance;
    }

    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {

        @Override
        public void binderDied() {
            KLog.e("XmPushService IBinder.DeathRecipient binderDied");
            if (mService == null || context == null) {
                return;
            }
            mService.asBinder().unlinkToDeath(mDeathRecipient, 0);
            //重新绑定远程服务
            Intent intent = new Intent(context, XmPushService.class);
            context.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    };

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IMqtt.Stub.asInterface(service);
            try {
                KLog.e("XmPushService onServiceConnected");
                mService.registerIMqttCallback(iMqttCallback);
                init(currentUid);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                service.linkToDeath(mDeathRecipient, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            try {
                KLog.e("XmPushService onServiceDisconnected");
                mService.unRegisterIMqttCallback(iMqttCallback);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mService = null;
        }
    };

    public void init(Context context, MqttListener mqttListener, String uid) {
        if (context == null || mqttListener == null || TextUtils.isEmpty(uid)) {
            KLog.e("XmPushService XmPushClientHandle init error");
            return;
        }
        if (this.context != null && this.mqttListener != null && !TextUtils.isEmpty(uid) && uid.equals(currentUid)) {
            KLog.e("XmPushService XmPushClientHandle do repeat init");
            return;
        }
        if (!TextUtils.isEmpty(currentUid) && !uid.equals(currentUid)) {
            this.currentUid = uid;
            init(currentUid);
            return;
        }
        this.context = context;
        this.mqttListener = mqttListener;
        this.currentUid = uid;
        Intent intent = new Intent(context, XmPushService.class);
        intent.setAction("com.xiaoma.mqtt.service.XmPushService");
        context.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void init(String uid) {
        try {
            mService.init(uid);
        } catch (Exception e) {
            KLog.e("XmPushService XmPushClientHandle init Exception, uid is " + uid);
        }
    }

    public void release() {
        try {
            mService.release();
        } catch (Exception e) {
            KLog.e("XmPushService XmPushClientHandle release Exception");
        }
    }

    public void publishTopic(String topic, String message) {
        try {
            mService.publishTopic(topic, message);
        } catch (Exception e) {
            KLog.e("XmPushService XmPushClientHandle publishTopic Exception");
        }
    }

}
