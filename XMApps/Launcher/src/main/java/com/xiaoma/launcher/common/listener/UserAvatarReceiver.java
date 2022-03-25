package com.xiaoma.launcher.common.listener;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.xiaoma.login.UserIconManager;
import com.xiaoma.login.common.LoginConstants;

@SuppressLint("LogNotTimber")
public class UserAvatarReceiver extends BroadcastReceiver {
    public static final String TAG = "UserAvatarReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e(TAG, "onReceive: " + action);
        if (LoginConstants.ACTION_ON_LOGIN.equals(action)) {
            updateUserAvatar();
        } else if (LoginConstants.ACTION_ON_LOGOUT.equals(action)) {
            UserIconManager.getInstance().removeIcon();
        } else if (LoginConstants.ACTION_ON_USER_UPDATE.equals(action)) {
            updateUserAvatar();
        } else if (Intent.ACTION_LOCALE_CHANGED.equals(action)) {
            updateUserAvatar();
        }
    }

    private static void updateUserAvatar() {
        try {
            UserIconManager.getInstance().updateUserAvatar();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
