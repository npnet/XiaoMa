package com.xiaoma.club.msg.chat.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoma.club.msg.chat.ui.ChatActivity;

/**
 * Created by LKF on 2019-5-31 0031.
 * 调起聊天页面的广播
 */
public class ChatReceiver extends BroadcastReceiver {
    private static final String EXTRA_CHAT_ID = "chatId";
    private static final String EXTRA_IS_GROUP = "isGroup";
    private final String TAG = getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String chatId = intent.getStringExtra(EXTRA_CHAT_ID);
        boolean isGroup = intent.getBooleanExtra(EXTRA_IS_GROUP, false);
        Log.i(TAG, String.format("onReceive(){ chatId: %s, isGroup: %s }", chatId, isGroup));
        if (TextUtils.isEmpty(chatId))
            return;
        Intent launcherIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        Intent chatIntent = new Intent(context, ChatActivity.class)
                .putExtra(ChatActivity.EXTRA_HX_CHAT_ID, chatId)
                .putExtra(ChatActivity.EXTRA_IS_GROUP_CHAT, isGroup);
        context.startActivities(new Intent[]{launcherIntent, chatIntent});
    }
}
