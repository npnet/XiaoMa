package com.xiaoma.mqtt.constant;

/**
 * Created by Thomas on 2019/1/17 0017
 */

public class MqttConstants {

    public final static String SERVER_URI = "tcp://193.112.87.252:1883";
    public final static String USER_NAME = "AA1090:mqtt";
    public final static String PASS_WORD = "xmlx_mq_123!";
    public final static String WILL_TOPIC = "xiaomalixing_push";
    public final static byte[] WILL_TOPIC_CONTENT = "xiaomalixing_push_connect_close".getBytes();
    public final static int QOS = 1;
    public final static boolean RETAINED = false;
    public final static int CHECK_CONNECTION_TIME = 60 * 1000;
    public final static String TOPIC_SENSOR = "/sensor/upload";
    public final static String TOPIC_SENSOR_WILL = "/sensor/offline";
    public final static String TOPIC_SENSOR_ONLINE = "/sensor/online";
    public final static String TOPIC_REC = "/ai/rec/";
    public final static String TOPIC_MSG = "/msg/accountList/";
    public final static String TOPIC_XIAOMA = "/xiaoma/push/";
    public final static String TOPIC_ADB = "/xiaoma/adb/";
    public final static String TOPIC_LOG = "/xiaoma/log/";
    public final static String TOPIC_OIL_NOTIFY = "/msg/accountSingle/oilNotify/";
    public final static String TOPIC_MQTT_INFO = "/msg/mqttInfo/";
    public final static String MQTT_ACTION = "com.xiaoma.mqtt";
    public final static String MQTT_PERMISSON = "com.xiaoma.permission.RECEIVE_MQTT";
    public final static String Mqtt_Info = "Mqtt_Info";

}
