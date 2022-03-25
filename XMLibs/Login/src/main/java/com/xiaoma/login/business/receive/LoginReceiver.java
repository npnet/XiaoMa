package com.xiaoma.login.business.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.xiaoma.login.LoginManager;
import com.xiaoma.login.common.LoginConstants;
import com.xiaoma.model.User;

/**
 * Created by youthyj on 2018/9/18.
 */
public class LoginReceiver extends BroadcastReceiver {
    private static final String Tag = LoginReceiver.class.getSimpleName();
    private static final String KEY_STEP = Tag + "_KEY_STEP";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (LoginConstants.ACTION_ON_LOGIN.equals(intent.getAction())) {
            Log.d(KEY_STEP, "ACTION_ON_LOGIN");
            User data = intent.getParcelableExtra("data");
            LoginManager.getInstance().onLogin(data);
        }
        if (LoginConstants.ACTION_ON_LOGOUT.equals(intent.getAction())) {
            Log.d(KEY_STEP, "ACTION_ON_LOGOUT");
            LoginManager.getInstance().onLogout();
        }
    }
}
