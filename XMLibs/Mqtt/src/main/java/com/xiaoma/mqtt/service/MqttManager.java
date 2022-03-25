package com.xiaoma.mqtt.service;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.xiaoma.mqtt.constant.MqttConstants;
import com.xiaoma.mqtt.model.MqttInfo;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.tputils.TPUtils;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Created by Thomas on 2019/1/21 0021
 * MqttManager -- push manager
 */

public class MqttManager {

    private static final String TAG = "MqttManager";
    private static MqttManager mqttManager;
    private static MqttAndroidClient mqttAndroidClient;
    private MqttConnectOptions mqttConnectOptions;
    private String uid;
    private static final String TOPIC = "topic";
    private static final String MESSAGE = "message";
    private Context mContext;
    private MqttInfo mqttInfo = MqttInfo.newInstance();
    private MqttCallbackExtended mqttCallbackExtended = new MqttCallbackExtended() {

        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            if (reconnect) {
                subscribeTopic();
                KLog.e(TAG, "MqttManager connectComplete reconnect is true");
            } else {
                KLog.e(TAG, "MqttManager connectComplete reconnect is false");
            }
        }

        @Override
        public void connectionLost(Throwable cause) {
            KLog.d(TAG, " MqttManager connectionLost");
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) {
            KLog.e(TAG, " MqttManager messageArrived the topic is " + topic + "  message is " + message);
            if (MqttConstants.WILL_TOPIC.equals(topic)) {
                return;
            }
            //通过广播，数据发送出去
            notifyPushMessageArrived(topic, new String(message.getPayload()));
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            KLog.e(TAG, " MqttManager deliveryComplete");
        }
    };

    private MqttManager() {
    }

    public static MqttManager getInstance() {
        if (mqttManager == null) {
            synchronized (MqttManager.class) {
                if (mqttManager == null) {
                    mqttManager = new MqttManager();
                }
            }
        }
        return mqttManager;
    }

    public void init(String uid, Context mContext) {
        this.uid = uid;
        this.mContext = mContext;
        getMqttInfo();
        connectMqtt(this.uid);
    }

    private void getMqttInfo() {
        String mqttInfoStr = TPUtils.get(this.mContext, MqttConstants.Mqtt_Info, "");
        if (!TextUtils.isEmpty(mqttInfoStr)) {
            KLog.d("MqttManager getMqttInfo is " + mqttInfoStr);
            MqttInfo mqttInfo = GsonHelper.fromJson(mqttInfoStr, MqttInfo.class);
            if (mqttInfo != null) {
                this.mqttInfo = mqttInfo;
            }
        }
    }

    private void connectMqtt(String clientId) {
        try {
            KLog.e(TAG, " MqttManager create connect and client id is " + clientId);
            mqttConnectOptions = new MqttConnectOptions();
            mqttConnectOptions.setCleanSession(false);
            mqttConnectOptions.setUserName(this.mqttInfo.username);
            mqttConnectOptions.setPassword(this.mqttInfo.password.toCharArray());
            mqttConnectOptions.setWill(MqttConstants.WILL_TOPIC, MqttConstants.WILL_TOPIC_CONTENT, MqttConstants.QOS, true);
            mqttConnectOptions.setAutomaticReconnect(true);
            mqttAndroidClient = new MqttAndroidClient(mContext, this.mqttInfo.broker, clientId);
            mqttAndroidClient.setCallback(mqttCallbackExtended);
            doConnect();
            checkMqttConnectState();
        } catch (Exception e) {
            e.printStackTrace();
            KLog.e(TAG, " MqttManager init MqttClient error, log is " + e.getMessage());
        }
    }

    public void doConnect() {
        if (mqttAndroidClient != null) {
            try {
                mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                        disconnectedBufferOptions.setBufferEnabled(true);
                        disconnectedBufferOptions.setBufferSize(100);
                        disconnectedBufferOptions.setPersistBuffer(false);
                        disconnectedBufferOptions.setDeleteOldestMessages(false);
                        mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                        subscribeTopic();
                        XmPushServiceHandle.getInstance().onSuccessConnect();
                        KLog.e(TAG, " MqttManager connect onSuccess");
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        KLog.e(TAG, " MqttManager connect onFailure: " + exception.getMessage());
                        XmPushServiceHandle.getInstance().onFailConnect();
                        reconnect();
                    }
                });
            } catch (MqttException e) {
                e.printStackTrace();
                KLog.e(TAG, " MqttManager connect onFailure Exception, log is " + e.getMessage());
                XmPushServiceHandle.getInstance().onFailConnect();
                reconnect();
            }
        }
    }

    public void publishMessage(String topicName, byte[] content) {
        if (mqttAndroidClient != null && mqttAndroidClient.isConnected()) {
            try {
                MqttMessage message = new MqttMessage(content);
                message.setPayload(content);
                message.setQos(MqttConstants.QOS);
                message.setRetained(MqttConstants.RETAINED);
                mqttAndroidClient.publish(topicName, message);
            } catch (MqttException e) {
                e.printStackTrace();
                KLog.e(TAG, " MqttManager publishTopic error, log is " + e.getMessage());
            }
        }
    }

    public void publishMessage(String topicName, String content) {
        if (mqttAndroidClient != null && mqttAndroidClient.isConnected()) {
            try {
                MqttMessage message = new MqttMessage(content.getBytes());
                message.setPayload(content.getBytes());
                message.setQos(MqttConstants.QOS);
                message.setRetained(MqttConstants.RETAINED);
                mqttAndroidClient.publish(topicName, message);
                KLog.e(TAG, "MqttManager publishTopic is " + topicName + ", content is " + content);
            } catch (MqttException e) {
                e.printStackTrace();
                KLog.e(TAG, " MqttManager publishTopic error, log is " + e.getMessage());
            }
        }
    }

    private void subscribeTopic() {
//        subscribeTopic(MqttConstants.WILL_TOPIC);
        subscribeTopic(MqttConstants.TOPIC_REC + uid);
        subscribeTopic(MqttConstants.TOPIC_MSG + uid);
        subscribeTopic(MqttConstants.TOPIC_XIAOMA + uid);
        subscribeTopic(MqttConstants.TOPIC_ADB + uid);
        subscribeTopic(MqttConstants.TOPIC_LOG + uid);
        subscribeTopic(MqttConstants.TOPIC_OIL_NOTIFY + uid);
        subscribeTopic(MqttConstants.TOPIC_MQTT_INFO + uid);
    }

    public void subscribeTopic(final String topicName) {
        if (mqttAndroidClient != null && mqttAndroidClient.isConnected()) {
            try {
                mqttAndroidClient.subscribe(topicName, MqttConstants.QOS);
            } catch (MqttException e) {
                e.printStackTrace();
                KLog.e(TAG, " MqttManager subscribeTopic error, log is " + e.getMessage());
            }
        }
    }

    public void unSubscribeTopic(String topicName) {
        if (mqttAndroidClient != null && mqttAndroidClient.isConnected()) {
            try {
                mqttAndroidClient.unsubscribe(topicName);
            } catch (MqttException e) {
                e.printStackTrace();
                KLog.e(TAG, " MqttManager unSubscribeTopic error, log is " + e.getMessage());
            }
        }
    }

    public String getClientId() {
        if (mqttAndroidClient != null) {
            return mqttAndroidClient.getClientId();
        }
        return "";
    }

    public boolean isConnected() {
        return mqttAndroidClient != null && mqttAndroidClient.isConnected();
    }

    private void reconnect() {
        ThreadDispatcher.getDispatcher().postOnMainDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isConnected()) {
                    doConnect();
                }
            }
        }, MqttConstants.CHECK_CONNECTION_TIME);
    }

    private void disConnect() {
        try {
            if (mqttAndroidClient != null && mqttAndroidClient.isConnected()) {
                mqttAndroidClient.disconnect();
            }
        } catch (MqttException e) {
            e.printStackTrace();
            KLog.e(TAG, " MqttManager disconnect error, log is " + e.getMessage());
        }
    }

    public void release() {
        try {
            if (mqttManager != null) {
                disConnect();
                mqttManager = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            KLog.e(TAG, " MqttManager release error, log is " + e.getMessage());
        }
    }

    private void checkMqttConnectState() {
        ThreadDispatcher.getDispatcher().postDelayed(mqttConnectStateRunnable, MqttConstants.CHECK_CONNECTION_TIME);
    }

    private Runnable mqttConnectStateRunnable = new Runnable() {
        @Override
        public void run() {
            KLog.d(TAG, " MqttManager checkMqttConnectState isConnected state is " + isConnected());
            checkMqttConnectState();
        }
    };

    private void notifyPushMessageArrived(String topic, String message) {
        Intent intent = new Intent();
        intent.setAction(MqttConstants.MQTT_ACTION);
        intent.putExtra(TOPIC, topic);
        intent.putExtra(MESSAGE, message);
        mContext.sendBroadcast(intent, MqttConstants.MQTT_PERMISSON);
    }

}
