桌面初始化使用，其它应用不需要初始化
private void initMqtt() {
        PushManager.getInstance().init(this, LoginManager.getInstance().getLoginUserId(), new MqttListener() {
            @Override
            public void onSuccessConnect() {
                KLog.d(TAG + " onSuccessConnect");
            }

            @Override
            public void onFailConnect() {
                KLog.d(TAG + " onFailConnect");
            }

            @Override
            public void onReceive(Message message) {
                KLog.d(TAG + " onReceive message： " + message);
            }

            @Override
            public void onSendSuccess(Message message) {
                KLog.d(TAG + " onSendSuccess message： " + message);
            }
        });
}

//-------------------发布，订阅和取消订阅topic目前应用不需要自己使用，只需要push manager统一使用即可--------------------------
发布topic
PushManager.getInstance().publishTopic("topic", "content");
订阅topic
PushManager.getInstance().subscribeTopic("topic");
取消订阅topic
PushManager.getInstance().unSubscribeTopic("topic");
//-------------------发布，订阅和取消订阅topic目前应用不需要自己使用，只需要push manager统一使用即可--------------------------


Note:
其它应用只需要监听广播收发消息推送通知即可
//--------------------------------------------------------------
由于数据是通过广播发送
Intent intent = new Intent();
intent.setAction(MqttConstants.MQTT_ACTION);
intent.putExtra("topic", topic);
intent.putExtra("message", message);
mContext.sendBroadcast(intent);
各应用上层通过注册广播，去接收数据
各应用注册广播需要首先在manifest里加入如下权限：
<uses-permission android:name="com.xiaoma.permission.RECEIVE_MQTT"/>
注册广播：
IntentFilter intentFilter = new IntentFilter();
intentFilter.addAction(LauncherConstants.MQTT_ACTION);
AppHolder.getInstance().getAppContext().registerReceiver(new MqttReceiver(), intentFilter);

接收数据：
public class MqttReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (LauncherConstants.MQTT_ACTION.equals(intent.getAction())) {
            String topic = intent.getStringExtra("topic");
            String message = intent.getStringExtra("message");
            KLog.d("Mqttmanager receiver topic" + topic + "message" + message);
            XMToast.showToast(context, message);
        }

    }
}

数据格式大致如下:注意由于type不同，对应的value是不同的，所以在解析时需要根据不同的type生成不同的对象，请注意！！！
 {
    "tid": "213456489896545",
    "cd": 448971636,
    "uid": 1073758947469074432,
    "iccid": "89860918710000022349",
    "imei": "867012039872830",
    "cid": "AA1080",
    "rec": [
        {
            "type": "map",
            "index": 1,
            "datas": [
                {
                    "startLoc": {
                        "city": "CURRENT_CITY",
                        "type": "LOC_POI",
                        "poi": "CURRENT_POI"
                    },
                    "endLoc": {
                        "city": "CURRENT_CITY",
                        "type": "LOC_POI",
                        "poi": "世界之窗地铁站A出口"
                    }
                },
                {
                    "startLoc": {
                        "city": "CURRENT_CITY",
                        "type": "LOC_POI",
                        "poi": "CURRENT_POI"
                    },
                    "endLoc": {
                        "city": "CURRENT_CITY",
                        "type": "LOC_POI",
                        "poi": "世界之窗地铁站B口"
                    }
                }
            ]
        },
        {
            "type": "music",
            "index": 2,
            "datas": [
                {
                    "songid": "123132",
                    "src": "kuwo",
                    "name": "大海-张雨生",
                    "image": "http://www.baidu.com",
                    "mp4url": "http://www.baidu.com/dahai.mp4"
                },
                {
                    "songid": "123132",
                    "src": "kuwo",
                    "name": "大海-伍佰",
                    "image": "http://www.baidu.com",
                    "mp4url": "http://www.baidu.com/dahai.mp4"
                }
            ]
        }
    ]
}
//--------------------------------------------------------------
