package com.xiaoma.mqtt.client;

import android.content.Context;
import android.text.TextUtils;
import com.xiaoma.mqtt.constant.MqttConstants;
import com.xiaoma.mqtt.listener.MqttListener;
import com.xiaoma.mqtt.model.SenseData;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.log.KLog;

/**
 * Created by Thomas on 2019/1/18 0018
 * xm mqtt push manager
 */

public class PushManager {

    private static final String TAG = "PushManager";

    private PushManager() {
    }

    public static PushManager getInstance() {
        return Holder.instance;
    }

    private static class Holder {
        private static PushManager instance = new PushManager();
    }

    public synchronized void init(Context context, String uid, MqttListener listener) {
        if (listener == null || TextUtils.isEmpty(uid) || context == null) {
            KLog.e(TAG, " PushManager MqttListener or context or uid is null, please check init method parameter");
            return;
        }
        XmPushClientHandle.getInstance().init(context, listener, uid);
    }

    public synchronized void release() {
        XmPushClientHandle.getInstance().release();
    }

    /**
     * 上报感知数据，用于AI分析
     *
     * @param senseData {@link SenseData}
     */
    public void publishTopic(final SenseData senseData) {
        if (senseData != null) {
            String senseMessage = GsonHelper.toJson(senseData);
            if (StringUtil.isNotEmpty(senseMessage)) {
                publishTopic(MqttConstants.TOPIC_SENSOR, senseMessage);
            }
        }
    }

    /**
     * 用户上线
     * @param senseData {@link SenseData}
     */
    public void publishOnLineTopic(final SenseData senseData) {
        if (senseData != null) {
            String senseMessage = GsonHelper.toJson(senseData);
            if (StringUtil.isNotEmpty(senseMessage)) {
                publishTopic(MqttConstants.TOPIC_SENSOR_ONLINE, senseMessage);
            }
        }
    }

    public void publishTopic(String topic, String message) {
        XmPushClientHandle.getInstance().publishTopic(topic, message);
    }

}
