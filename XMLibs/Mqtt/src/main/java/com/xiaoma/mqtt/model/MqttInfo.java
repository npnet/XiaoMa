package com.xiaoma.mqtt.model;

import com.xiaoma.mqtt.constant.MqttConstants;

public class MqttInfo {

    public String broker;
    public String username;
    public String password;
    public long interval;

    public static MqttInfo newInstance() {
        MqttInfo mqttInfo = new MqttInfo();
        mqttInfo.broker = MqttConstants.SERVER_URI;
        mqttInfo.username = MqttConstants.USER_NAME;
        mqttInfo.password = MqttConstants.PASS_WORD;
        return mqttInfo;
    }

    @Override
    public String toString() {
        return "MqttInfo{" +
                "broker='" + broker + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", interval=" + interval +
                '}';
    }
}
