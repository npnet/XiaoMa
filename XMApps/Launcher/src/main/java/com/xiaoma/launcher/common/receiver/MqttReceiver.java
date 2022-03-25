package com.xiaoma.launcher.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.xiaoma.launcher.common.LauncherUtils;
import com.xiaoma.launcher.common.constant.LauncherConstants;
import com.xiaoma.launcher.common.manager.UploadMqttDataManager;
import com.xiaoma.launcher.recommend.manager.NotificationDataParseHandler;
import com.xiaoma.launcher.recommend.manager.RecommendDataParserManager;
import com.xiaoma.launcher.recommend.push.handler.MqttHandlerFactory;
import com.xiaoma.launcher.recommend.push.model.MqttMessage;
import com.xiaoma.mqtt.constant.MqttConstants;
import com.xiaoma.mqtt.model.MqttInfo;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.log.KLog;

import org.simple.eventbus.EventBus;

/**
 * @author taojin
 * @date 2019/1/24
 * xm push 消息接收器
 */

public class MqttReceiver extends BroadcastReceiver {

    private static final String TOPIC = "topic";
    private static final String MESSAGE = "message";
    private static final String TAG = "MqttReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (LauncherConstants.MQTT_ACTION.equals(intent.getAction())) {
            String topic = intent.getStringExtra(TOPIC);
            String message = intent.getStringExtra(MESSAGE);
            KLog.e(TAG, "MqttReceiver onReceive topic is " + topic + " message is " + message);
            if (TextUtils.isEmpty(topic) || TextUtils.isEmpty(message)) {
                return;
            }
            if (topic.startsWith(MqttConstants.TOPIC_REC)) {
                if (LauncherUtils.isActivityForeground(context, LauncherConstants.MAIN_ACTIVITY_CLASS_NAME)) {
                    //如果当前页面是主界面,就弹出推荐窗口
                    EventBus.getDefault().post(message, LauncherConstants.RECOMMEND_MESSAGE);
                } else {
                    //否则就保存该数据,时效五分钟
                    RecommendDataParserManager.getInstance().saveRecommendData(message);
                }
            } else if (topic.startsWith(MqttConstants.TOPIC_MSG)) {
                NotificationDataParseHandler.getInstance().parserNotificationData(context, message);
            } else if (topic.startsWith(MqttConstants.TOPIC_XIAOMA)) {
                MqttMessage mqttMessage = GsonHelper.fromJson(message, MqttMessage.class);
                if (mqttMessage == null) return;
                MqttHandlerFactory.getInstance().produceHandler(mqttMessage).handle(mqttMessage);
            } else if (topic.startsWith(MqttConstants.TOPIC_ADB)) {
                //adb命令推送
                KLog.d("adb command is " + message);
                try {
                    Runtime.getRuntime().exec(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (topic.startsWith(MqttConstants.TOPIC_LOG)) {
                //log命令推送
//                {
//                    "action": 5, //代表日志文件压缩上传
//                        "tag": "adb", //代表是否为adb获取日志类型还是直接压缩com.xiaoma/log下对应包名日志，"adb"代表为adb类型，无需data字段,"log"代表压缩com.xiaoma/log下对应包名日志
//                        "data": "com.xiaoma.music" //当tag为"log"时，此为必需字段，为小马应用包名
//                }
                KLog.d("log content is " + message);

                MqttMessage mqttMessage = GsonHelper.fromJson(message, MqttMessage.class);
                if (mqttMessage == null) return;
                MqttHandlerFactory.getInstance().produceHandler(mqttMessage).handle(mqttMessage);
            } else if (topic.startsWith(MqttConstants.TOPIC_OIL_NOTIFY)) {
                EventBus.getDefault().post(message, LauncherConstants.SHOW_FUEL_WARNING);
            } else if (topic.startsWith(MqttConstants.TOPIC_MQTT_INFO)) {
                MqttInfo mqttInfo = GsonHelper.fromJson(message, MqttInfo.class);
                if (mqttInfo != null) {
                    KLog.e(TAG, "Mqtt Info: " + mqttInfo.toString());
                    UploadMqttDataManager.getInstance().setMqttInterval(mqttInfo.interval);
                }
            }
        }
    }
}
