package com.xiaoma.club.common.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.xiaoma.club.common.repo.ClubRepo;
import com.xiaoma.club.common.util.ClubSettings;
import com.xiaoma.club.common.util.LogUtil;
import com.xiaoma.club.common.util.NotificationHelper;
import com.xiaoma.club.common.util.UserUtil;
import com.xiaoma.club.msg.hyphenate.SimpleMessageListener;
import com.xiaoma.utils.logintype.constant.LoginCfgConstant;
import com.xiaoma.utils.logintype.manager.LoginTypeManager;
import com.xiaoma.model.User;
import com.xiaoma.utils.AppUtils;

import java.util.List;
import java.util.Objects;

/**
 * Author: loren
 * Date: 2019/3/18 0018
 */

public class ClubNotificationService extends Service {
    private final String TAG = ClubNotificationService.class.getSimpleName();
    private EMMessageListener hxListener;
    private static final String CHAT_ACTIVITY_CLASS_NAME = "com.xiaoma.club.msg.chat.ui.ChatActivity";

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            EMClient.getInstance().chatManager().addMessageListener(hxListener = new HxMsgListener());
            NotificationHelper.getInstance().pushMsgAfterPowerOn(ClubNotificationService.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            EMClient.getInstance().chatManager().removeMessageListener(hxListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class HxMsgListener extends SimpleMessageListener {

        // 收到普通消息（群聊、单聊）;
        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            if (!LoginTypeManager.getInstance().judgeUse(LoginCfgConstant.CLUB)) { //子账号 无法接收消息
                return;
            }
            // 用户未登陆 或 当前用户环信ID不一致的时候,不推送
            final User u = UserUtil.getCurrentUser();
            final String hxUid = EMClient.getInstance().getCurrentUser();
            if (u == null || !Objects.equals(u.getHxAccount(), hxUid)) {
                LogUtil.logE(TAG, "onMessageReceived( messages: %s ) { User: %s, hxUid: %s }",
                        u != null ? u.getHxAccount() : "null", hxUid);
                return;
            }
            // 关闭了推送通知 或 App处于前台时不推送
            final boolean isPushOpen = ClubSettings.isChatPushOpen();
            final boolean isAppForeground = AppUtils.isAppForeground();
            LogUtil.logI(TAG, "onMessageReceived( messages: %s ) { isPushOpen: %s, isAppForeground: %s }",
                    messages, isPushOpen, isAppForeground);
            if (!isPushOpen
                    || isAppForeground
                    || messages == null || messages.isEmpty()) {
                return;
            }
            final EMMessage msg = messages.get(0);
            // 禁用了当前会话的消息不推送
            final boolean isPushDisabledConversation = ClubRepo.getInstance().getPushDisabledConversationRepo().isPushDisabled(msg.getTo());
            LogUtil.logI(TAG, "onMessageReceived( messages: %s ) { isPushDisabledConversation: %s }", messages, isPushDisabledConversation);
            if (isPushDisabledConversation)
                return;
            NotificationHelper.getInstance().handleMessage(ClubNotificationService.this, msg, true);
        }
    }

}
