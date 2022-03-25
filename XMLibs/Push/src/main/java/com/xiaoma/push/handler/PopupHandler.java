package com.xiaoma.push.handler;

import android.content.Intent;
import android.text.TextUtils;

import com.tencent.android.tpush.XGPushManager;
import com.xiaoma.push.PushConstants;
import com.xiaoma.push.model.PushMessage;

/**
 * Created by vincenthu on 2017/10/19.
 */

public class PopupHandler implements IPushHandler {

    @Override
    public void handle(PushMessage pm) {
        if (pm == null) {
            return;
        }
        if (TextUtils.isEmpty(pm.getData())) {
            return;
        }

        // TODO: 弹窗的Activity分离到 UI Module中去,这里用 Intent Action方式间接启动
        Intent intent = new Intent(PushConstants.POPUP_ACTION);
        intent.putExtra(PushConstants.EXTRA_POPUP_DATA, pm.getData());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        XGPushManager.getContext().startActivity(intent);
    }

    @Override
    public int getAction() {
        return PUSH_ACTION_POPUP;
    }
}
