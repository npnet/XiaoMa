package com.xiaoma.push.handler;


import com.xiaoma.push.model.PushMessage;

/**
 * Created by Administrator on 2017/10/19.
 */

public class ModifyConfigHandler implements IPushHandler {

    @Override
    public void handle(PushMessage pm) {
    }

    @Override
    public int getAction() {
        return PUSH_ACTION_MODIFY_CONFIG;
    }
}
