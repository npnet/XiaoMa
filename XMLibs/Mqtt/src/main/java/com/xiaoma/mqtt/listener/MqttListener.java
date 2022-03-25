package com.xiaoma.mqtt.listener;

import com.xiaoma.mqtt.model.Message;

/**
 * Created by Thomas on 2019/1/16 0016
 * message listener
 */

public interface MqttListener {

    //连接成功
    void onSuccessConnect();

    //连接失败
    void onFailConnect();

}
