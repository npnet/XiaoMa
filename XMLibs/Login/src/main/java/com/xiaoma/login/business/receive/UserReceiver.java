package com.xiaoma.login.business.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.xiaoma.login.UserManager;
import com.xiaoma.login.common.LoginConstants;

/**
 * @author youthyJ
 * @date 2018/10/10
 */
public class UserReceiver extends BroadcastReceiver {
    private static final String Tag = UserReceiver.class.getSimpleName();
    private static final String KEY_STEP = Tag + "_KEY_STEP";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (LoginConstants.ACTION_ON_USER_UPDATE.equals(intent.getAction())) {
            Log.d(KEY_STEP, "ACTION_ON_USER_UPDATE");
            String data = intent.getStringExtra("userId");
            UserManager.getInstance().onUserUpdate(data);
        }
    }
}
