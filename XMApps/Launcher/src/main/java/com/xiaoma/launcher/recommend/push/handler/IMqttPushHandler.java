package com.xiaoma.launcher.recommend.push.handler;

import android.support.annotation.IntRange;

import com.xiaoma.launcher.recommend.push.model.MqttMessage;

/**
 * @author taojin
 * @date 2019/5/17
 */
public interface IMqttPushHandler {

    int PUSH_ACTION_UPLOAD_SCREENSHOT = 1;
    int PUSH_ACTION_UPLOAD_FILE = 2;
    int PUSH_ACTION_DELETE_FILE = 3;
    int PUSH_ACTION_INSTALL_FILE = 4;
    int PUSH_ACTION_UPLOAD_LOG_FILE = 5;

    /**
     * 具体的处理逻辑
     *
     * @param pm PushMessage
     */
    void handle(MqttMessage pm);

    /**
     * 返回具体的handler所能处理的action
     *
     * @return 对应的action
     */
    @IntRange(from = 1)
    int getAction();
}
