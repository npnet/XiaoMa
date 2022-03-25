package com.xiaoma.club.common.hyphenate;

import android.content.Context;
import android.os.Looper;
import android.os.NetworkOnMainThreadException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMChatManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.exceptions.HyphenateException;
import com.xiaoma.club.common.hyphenate.callback.EMConnectionListenerImpl;
import com.xiaoma.club.common.hyphenate.callback.EMContactListenerImpl;
import com.xiaoma.club.common.util.UserUtil;
import com.xiaoma.club.msg.chat.constant.ChatMsgEventTag;
import com.xiaoma.club.msg.chat.constant.MessageKey;
import com.xiaoma.club.msg.chat.constant.MessageType;
import com.xiaoma.club.msg.hyphenate.MessageStatusCallback;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.config.bean.EnvType;
import com.xiaoma.model.User;
import com.xiaoma.utils.BuildConfig;
import com.xiaoma.utils.StringUtil;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.xiaoma.club.common.util.LogUtil.logE;

public class IMUtils {
    private final static String TAG = "IMUtils";
    // 测试体验环境
    private static final String HX_APP_KEY_TEST = "xiaomalixing#xiaomaexperience";
    // 正式环境(带消息撤回功能)
    private static final String HX_APP_KEY_OFFICE = "xiaomalixing#xiaomazhuomian";

    public static synchronized void hxImInit(final Context context, boolean isFactoryUser) {
        // 初始化环信
        final EMClient emClient = EMClient.getInstance();
        emClient.init(context, createEMOptions(getHxAppKey(), isFactoryUser));
        emClient.setDebugMode(BuildConfig.DEBUG);
        emClient.addConnectionListener(new EMConnectionListenerImpl(context));
        emClient.contactManager().setContactListener(EMContactListenerImpl.getInstance());
        //emClient.chatManager().addMessageListener(EMMessageListenerImpl.getInstance());
        //emClient.groupManager().addGroupChangeListener(EMGroupChangeListenerImpl.getInstance());
        //emClient.chatroomManager().addChatRoomChangeListener(EMChatRoomChangeListenerImpl.getInstance());
    }

    private static EMOptions createEMOptions(String hxAppKey, boolean isFactoryUser) {
        EMOptions options = new EMOptions();
        // 默认好友请求是自动同意的，如果要手动同意需要在初始化SDK时设置此标记为false
        options.setAcceptInvitationAlways(false);
        // 如果用到已读的回执需要把这个flag设置成true
        options.setRequireAck(false);
        // 如果用到已发送的回执需要把这个flag设置成true
        options.setRequireDeliveryAck(false);
        //因工厂模式测试完并不会执行logout操作，防止正式用户使用不会一开机就登录环信
        options.setAutoLogin(!isFactoryUser);
        options.setAppKey(hxAppKey);
        return options;
    }

    public static synchronized void loginHx(String account, String password, final EMCallBack callBack) {
        if (EMClient.getInstance().isConnected()) {
            if (!TextUtils.isEmpty(account) && account.equals(EMClient.getInstance().getCurrentUser())) {
                logE(TAG, "HxLogin isConnected, no login more ->");
                if (callBack != null) callBack.onSuccess();
                return;
            } else {
                loginOutHx();
            }
        }
        // 当前没有已登录的环信ID 或者 小马用户的环信ID和环信SDK中的ID不一致时,重新登录环信
        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)) {
            logE(TAG, String.format("HxLogin start -> [ acc: %s, psd: %s ]", account, password));
            EMClient.getInstance().login(account, password, new EMCallBack() {
                @Override
                public void onSuccess() {
                    logE(TAG, "HxLogin onSuccess ->");
                    // 根据环信文档:登录成功后需要调用以下两个方法同步群组和会话
                    EMClient.getInstance().groupManager().loadAllGroups();
                    EMClient.getInstance().chatManager().loadAllConversations();
                    try {
                        EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (callBack != null) callBack.onSuccess();
                }

                @Override
                public void onError(int code, String message) {
                    logE(TAG, StringUtil.format("HxLogin onError -> [ code: %d, message: %s ]", code, message));
                    if (callBack != null) callBack.onError(code, message);
                }

                @Override
                public void onProgress(int progress, String status) {
                    logE(TAG, StringUtil.format("HxLogin onProgress -> [ progress: %d, status: %s ]", progress, status));
                    if (callBack != null) callBack.onProgress(progress, status);
                }
            });
        } else if (TextUtils.isEmpty(account)) {
            logE(TAG, StringUtil.format("HxLogin onError -> [ code: %d, message: %s ]", EMError.INVALID_USER_NAME, "account is empty"));
            if (callBack != null) callBack.onError(EMError.INVALID_USER_NAME, "account is empty");
        } else if (TextUtils.isEmpty(password)) {
            logE(TAG, StringUtil.format("HxLogin onError -> [ code: %d, message: %s ]", EMError.INVALID_PASSWORD, "password is empty"));
            if (callBack != null) callBack.onError(EMError.INVALID_PASSWORD, "password is empty");
        }
    }

    public static void loginOutHx(final EMCallBack callBack) {
        EMClient.getInstance().logout(true, new EMCallBack() {

            @Override
            public void onSuccess() {
                logE(TAG, "HxLoginOut onSuccess -> ");
                if (callBack != null) callBack.onSuccess();
            }

            @Override
            public void onProgress(int progress, String status) {
                logE(TAG, StringUtil.format("HxLoginOut onProgress -> [ progress: %d, status: %s ]", progress, status));
                if (callBack != null) callBack.onProgress(progress, status);
            }

            @Override
            public void onError(int code, String message) {
                logE(TAG, StringUtil.format("HxLoginOut onError -> [ code: %d, message: %s ]", code, message));
                if (callBack != null) callBack.onError(code, message);
            }
        });
    }

    public static void loginOutHx() {
        EMClient.getInstance().logout(true);
    }

    /**
     * 由于发送消息环信侧没有回调,这里通过EventBus分发事件
     */
    public static void sendMessage(EMMessage message) {
        final EMChatManager chatManager = EMClient.getInstance().chatManager();
        final User u = UserUtil.getCurrentUser();
        if (u != null && !TextUtils.isEmpty(u.getName())) {
            message.setAttribute(MessageKey.USER_NICKNAME, u.getName());
        }
        message.setMessageStatusCallback(new MessageStatusCallback(message));
        chatManager.sendMessage(message);
        EventBus.getDefault().post(message, ChatMsgEventTag.SEND_MESSAGE);
    }

    /**
     * 消息重发
     */
    public static void reSendMessage(EMMessage message) {
        final EMConversation conversation = EMClient.getInstance().chatManager().getConversation(message.getTo());
        if (conversation != null) {
            conversation.removeMessage(message.getMsgId());
        }
        sendMessage(message);
    }

    /**
     * 由于撤回消息环信侧没有回调,这里通过EventBus分发事件
     * <p>
     * * @param message 被撤回的消息
     * * @param tips    撤回提示,比如:XX撤回了一条消息
     */
    public static void recallMessage(EMMessage message) throws HyphenateException {
        final EMChatManager chatManager = EMClient.getInstance().chatManager();
        chatManager.recallMessage(message);
        EventBus.getDefault().post(message, ChatMsgEventTag.RECALL_MESSAGE);
    }

    /**
     * 保存一条撤回消息的记录
     *
     * @param message 被撤回的消息
     * @param tips    撤回提示,比如:XX撤回了一条消息
     */
    public static void saveRecallMessageTips(EMMessage message, String tips) {
        if (message == null || TextUtils.isEmpty(tips))
            return;
        final EMMessage recallMsg = EMMessage.createTxtSendMessage(tips, getOtherHxId(message));
        recallMsg.setMsgId(message.getMsgId());
        recallMsg.setAttribute(MessageKey.MSG_TYPE, MessageType.SYSTEM_NOTIFY);
        recallMsg.setMsgTime(message.getMsgTime());
        saveMessage(recallMsg);
    }

    /**
     * 保存消息到会话中
     */
    public static void saveMessage(EMMessage message) {
        if (message == null)
            return;
        EMClient.getInstance().chatManager().saveMessage(message);
        EventBus.getDefault().post(message, ChatMsgEventTag.SAVE_MESSAGE);
    }

    /**
     * 禁言群组用户
     * 注意!!! 要在子线程里调!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     *
     * @param hxGroupId 环信群ID
     * @param hxAccount 环信用户ID
     * @param duration  禁言时长
     * @return 环信群数据
     */
    @WorkerThread
    @Nullable
    public static EMGroup muteGroupMember(String hxGroupId, String hxAccount, long duration) throws Exception {
        ensureWorkerThread();
        EMGroup group = null;
        final List<String> mutes = new ArrayList<>();
        mutes.add(hxAccount);
        group = EMClient.getInstance()
                .groupManager()
                .muteGroupMembers(hxGroupId, mutes, duration);
        return group;
    }

    /**
     * 解除禁言
     * 注意!!!要在子线程里调!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
     *
     * @param hxGroupId 环信群ID
     * @param hxAccount 环信用户ID
     * @return 环信群数据
     */
    @WorkerThread
    @Nullable
    public static EMGroup unMuteGroupMembers(String hxGroupId, String hxAccount) throws Exception {
        ensureWorkerThread();
        EMGroup group = null;
        final List<String> unMutes = new ArrayList<>();
        unMutes.add(hxAccount);
        group = EMClient.getInstance()
                .groupManager()
                .unMuteGroupMembers(hxGroupId, unMutes);
        return group;
    }

    /**
     * 获取当前用户的环信好友ID
     *
     * @return 好友环信ID集合
     */
    @WorkerThread
    @Nullable
    public static Set<String> getHxContacts() {
        ensureWorkerThread();
        Set<String> contacts = null;
        try {
            List<String> contactList = EMClient.getInstance().contactManager().getAllContactsFromServer();
            if (contactList == null) {
                contactList = Collections.emptyList();
            }
            contacts = new HashSet<>(contactList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contacts;
    }

    // 确认当前方法调用是在Worker线程,否则直接抛出异常
    private static void ensureWorkerThread() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new NetworkOnMainThreadException();
        }
    }

    private static String getHxAppKey() {
        EnvType type = ConfigManager.EnvConfig.getEnvType();
        return EnvType.OFFICE == type ?
                HX_APP_KEY_OFFICE : HX_APP_KEY_TEST;
    }

    // 获取对方的环信ID
    private static String getOtherHxId(@NonNull EMMessage message) {
        String myHxId = EMClient.getInstance().getCurrentUser();
        if (!Objects.equals(myHxId, message.getTo())) {
            return message.getTo();
        }
        return message.getFrom();
    }
}