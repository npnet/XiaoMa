package com.xiaoma.service.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xiaoma.service.common.manager.IBCallManager;

/**
 * @author taojin
 * @date 2019/7/26
 */
public class IBCallReceiver extends BroadcastReceiver {

    private final int ICALL = 1, BCALL = 2;

    private final String XIAOMA_ASSISTANT_ICALL_ACTION = "xiaoma_assistant_icall_action";
    private final String XIAOMA_ASSISTANT_BCALL_ACTION = "xiaoma_assistant_bcall_action";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (XIAOMA_ASSISTANT_ICALL_ACTION.equals(action)) {
            IBCallManager.getInstance().handleIBCall(ICALL, context);
        } else if (XIAOMA_ASSISTANT_BCALL_ACTION.equals(action)) {
            IBCallManager.getInstance().handleIBCall(BCALL, context);
        }
    }
}
