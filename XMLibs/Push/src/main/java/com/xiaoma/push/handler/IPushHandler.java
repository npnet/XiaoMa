package com.xiaoma.push.handler;


import android.support.annotation.IntRange;

import com.xiaoma.push.model.PushMessage;

/**
 * Created by Administrator on 2017/5/24 0024.
 */
public interface IPushHandler {

    int PUSH_ACTION_UPLOAD_SCREENSHOT = 1;
    int PUSH_ACTION_UPLOAD_FILE = 2;
    int PUSH_ACTION_DELETE_FILE = 3;
    int PUSH_ACTION_PHONE_TO_CAR = 4;
    int PUSH_ACTION_MODIFY_CONFIG = 5;
    int PUSH_ACTION_NOTICE = 6;
    int PUSH_ACTION_POPUP = 7;
    int PUSH_ACTION_FLY_SCREEN = 9;
    int PUSH_ACTION_RECOMMEND = 10;

    /**
     * 具体的处理逻辑
     *
     * @param pm PushMessage
     */
    void handle(PushMessage pm);

    /**
     * 返回具体的handler所能处理的action
     *
     * @return 对应的action
     */
    @IntRange(from = 1)
    int getAction();
}
