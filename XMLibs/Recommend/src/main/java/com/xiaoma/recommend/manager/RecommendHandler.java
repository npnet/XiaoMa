package com.xiaoma.recommend.manager;

import android.content.Intent;
import android.text.TextUtils;

import com.xiaoma.push.handler.IPushHandler;
import com.xiaoma.push.model.PushMessage;
import com.xiaoma.utils.log.KLog;

/**
 * @author: iSun
 * @date: 2018/12/10 0010
 */
public class RecommendHandler implements IPushHandler {

    @Override
    public void handle(PushMessage pm) {
        KLog.d("RecommendHandler , data = " + pm.getData());
        if (TextUtils.isEmpty(pm.getTag())) {
            return;
        }
    }

    @Override
    public int getAction() {
        return PUSH_ACTION_RECOMMEND;
    }
}
