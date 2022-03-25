package com.xiaoma.mqtt;

import com.xiaoma.mqtt.IMqttCallback;

interface IMqtt {

    oneway void init(String uid);

    oneway void release();

    oneway void publishTopic(String topic, String message);

    oneway void registerIMqttCallback(IMqttCallback iMqttCallback);

    oneway void unRegisterIMqttCallback(IMqttCallback iMqttCallback);

}
