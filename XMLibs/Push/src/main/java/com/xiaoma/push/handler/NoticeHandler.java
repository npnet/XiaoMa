package com.xiaoma.push.handler;

import android.content.Intent;
import android.text.TextUtils;

import com.tencent.android.tpush.XGPushManager;
import com.xiaoma.push.PushConstants;
import com.xiaoma.push.model.PushMessage;
import com.xiaoma.utils.log.KLog;


/**
 * Created by Administrator on 2017/11/29 0029.
 */

public class NoticeHandler implements IPushHandler {

    @Override
    public void handle(PushMessage pm) {
        KLog.d("NoticeHandler , data = " + pm.getData());
        if (TextUtils.isEmpty(pm.getTag())) {
            return;
        }

        // 消息提示通过广播分发出去，用特定的 PushConstants.PUSH_NOTICE_ACTION 区分
        Intent intent = new Intent(PushConstants.PUSH_NOTICE_ACTION);
        intent.putExtra(PushConstants.PUSH_NOTICE_EXTRA_MSG_ACTION, pm.getAction());
        intent.putExtra(PushConstants.PUSH_NOTICE_EXTRA_MSG_TAG, pm.getTag());
        intent.putExtra(PushConstants.PUSH_NOTICE_EXTRA_MSG_DATA, pm.getData());
        XGPushManager.getContext().sendBroadcast(intent);
    }

    @Override
    public int getAction() {
        return PUSH_ACTION_NOTICE;
    }
}
