package com.xiaoma.club.common.util;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.graphics.drawable.IconCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.reflect.TypeToken;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.xiaoma.club.R;
import com.xiaoma.club.common.model.NotificationModel;
import com.xiaoma.club.common.network.ClubRequestManager;
import com.xiaoma.club.common.repo.ClubRepo;
import com.xiaoma.club.common.repo.impl.PushedNotificationRepo;
import com.xiaoma.club.contact.model.GroupCardInfo;
import com.xiaoma.club.msg.chat.ui.ChatActivity;
import com.xiaoma.club.msg.conversation.util.ConversationUtil;
import com.xiaoma.model.User;
import com.xiaoma.model.XMResult;
import com.xiaoma.network.callback.CallbackWrapper;
import com.xiaoma.systemuilib.NotificationUtil;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.GsonHelper;

import java.io.File;
import java.util.Map;

/**
 * Created by 凌艳 on 2019/3/19.
 */
public class NotificationHelper {
    private static final int MAX_PUSH_MSG_COUNTS = 5;
    private static NotificationHelper instance;
    private int requestId = 0;
    private int maxIdSize = 10000;
    private final PushedNotificationRepo mPushedNotificationRepo = ClubRepo.getInstance().getPushedNotificationRepo();

    /**
     * 获取单例
     *
     * @return 单例对象
     */
    public static NotificationHelper getInstance() {
        if (instance == null) {
            instance = new NotificationHelper();
        }
        return instance;
    }

    /**
     * 处理最新消息
     *
     * @param context 上下文
     * @param message 当前收到的消息
     */
    public void handleMessage(Context context, EMMessage message, boolean showHeadsUp) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(ChatActivity.EXTRA_HX_CHAT_ID, message.conversationId());
        if (message.getChatType() == EMMessage.ChatType.Chat) {
            intent.putExtra(ChatActivity.EXTRA_IS_GROUP_CHAT, false);
            handleUserNotification(context, message, intent, showHeadsUp);
        } else if (message.getChatType() == EMMessage.ChatType.GroupChat) {
            intent.putExtra(ChatActivity.EXTRA_IS_GROUP_CHAT, true);
            handleGroupNotification(context, message, intent, showHeadsUp);
        }
        mPushedNotificationRepo.addPushed(message.getMsgId());// 记录推送过的通知
    }

    private void handleUserNotification(final Context context, final EMMessage message,
                                        final Intent intent, boolean showHeadsUp) {
        final NotificationModel notificationModel = new NotificationModel();
        final String content = ConversationUtil.getMsgContent(context, message);
        notificationModel.setShowHeadsUp(showHeadsUp);
        notificationModel.setTitle(context.getString(R.string.have_new_message));
        notificationModel.setJumpIntent(intent);
        notificationModel.setId(message.conversationId());
        notificationModel.setMessageTime(message.getMsgTime());
        final User user = ClubRepo.getInstance().getUserRepo().getByKey(message.conversationId());
        if (user != null) {
            notificationModel.setContent(user.getName() + "：" + content);
            notificationModel.setPicPath(user.getPicPath());
            show(context, notificationModel, IconCompat.createWithResource(context, R.drawable.app_icon_club).toIcon());
        } else {
            ClubRequestManager.getUserByHxAccount(message.conversationId(), new CallbackWrapper<User>() {
                @Override
                public User parse(String data) {
                    return GsonHelper.fromJson(data, User.class);
                }

                @Override
                public void onSuccess(final User model) {
                    super.onSuccess(model);
                    if (model != null) {
                        notificationModel.setContent(model.getName() + "：" + content);
                        notificationModel.setPicPath(model.getPicPath());
                        show(context, notificationModel, IconCompat.createWithResource(context, R.drawable.app_icon_club).toIcon());
                    }
                }
            });
        }

    }

    private void handleGroupNotification(final Context context, final EMMessage message,
                                         final Intent intent, boolean showHeadsUp) {
        final NotificationModel notificationModel = new NotificationModel();
        final String content = ConversationUtil.getMsgContent(context, message);
        notificationModel.setShowHeadsUp(showHeadsUp);
        notificationModel.setTitle(context.getString(R.string.have_new_message));
        notificationModel.setJumpIntent(intent);
        notificationModel.setId(message.conversationId());
        notificationModel.setMessageTime(message.getMsgTime());
        final User user = ClubRepo.getInstance().getUserRepo().getByKey(message.getFrom());
        if (user != null) {
            notificationModel.setContent(user.getName() + "：" + content);
            setModelOtherValues(context, message, notificationModel);
        } else {
            ClubRequestManager.getUserByHxAccount(message.getFrom(), new CallbackWrapper<User>() {
                @Override
                public User parse(String data) {
                    return GsonHelper.fromJson(data, User.class);
                }

                @Override
                public void onSuccess(final User model) {
                    super.onSuccess(model);
                    if (model != null) {
                        notificationModel.setContent(model.getName() + "：" + content);
                    }
                }
            });
        }
    }

    private void setModelOtherValues(final Context context, final EMMessage message, final NotificationModel notificationModel) {
        final GroupCardInfo groupCardInfo = ClubRepo.getInstance().getGroupRepo().get(message.getFrom());
        if (groupCardInfo != null) {
            notificationModel.setPicPath(groupCardInfo.getPicPath());
            show(context, notificationModel, IconCompat.createWithResource(context, R.drawable.app_icon_club).toIcon());
        } else {
            ClubRequestManager.getGroupByHxId(message.conversationId(), new CallbackWrapper<GroupCardInfo>() {
                @Override
                public GroupCardInfo parse(String data) {
                    XMResult<GroupCardInfo> result = GsonHelper.fromJson(data, new TypeToken<XMResult<GroupCardInfo>>() {
                    }.getType());
                    if (result == null || !result.isSuccess()) {
                        return null;
                    }
                    return result.getData();
                }

                @Override
                public void onSuccess(final GroupCardInfo model) {
                    if (model != null) {
                        notificationModel.setPicPath(model.getPicPath());
                        show(context, notificationModel, IconCompat.createWithResource(context, R.drawable.app_icon_club).toIcon());
                    }
                }

                @Override
                public void onError(int code, String msg) {

                }
            });
        }
    }

//    /**
//     * 请求通知栏图标，显示通知栏
//     *
//     * @param context           上下文
//     * @param notificationModel 封装通知栏内容数据模型
//     */
//    private void requestImgAndNotify(final Context context, final NotificationModel notificationModel) {
//        Glide.with(context)
//                .download(notificationModel.getPicPath())
//                .into(new CustomTarget<File>() {
//                    @Override
//                    public void onResourceReady(@NonNull final File resource, @Nullable Transition<? super File> transition) {
//                        final Uri uri = Uri.fromFile(resource);
//                        show(context, notificationModel, IconCompat.createWithContentUri(uri).toIcon());
//                    }
//
//                    @Override
//                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
//                        show(context, notificationModel, IconCompat.createWithResource(context, R.drawable.app_icon_club).toIcon());
//                    }
//
//                    @Override
//                    public void onLoadCleared(@Nullable Drawable placeholder) {
//
//                    }
//                });
//    }

    /**
     * 显示通知栏
     *
     * @param context 上下文
     * @param model   通知栏数据模型
     */
    private void show(final Context context, final NotificationModel model, final Icon icon) {
        ThreadDispatcher.getDispatcher().runOnMain(new Runnable() {
            @Override
            public void run() {
                //获取PendingIntent
                if (requestId > maxIdSize) {
                    requestId = 0;
                }
                PendingIntent chatPendingIntent = PendingIntent.getActivity(context, requestId++,
                        model.getJumpIntent(), PendingIntent.FLAG_CANCEL_CURRENT);
                Notification notification = NotificationUtil.builder(context, model.getTitle(),
                        model.getContent(), icon, chatPendingIntent,
                        model.getMessageTime(), model.isShowHeadsUp()).setAutoCancel(true).build();
                NotificationManagerCompat.from(context).notify(model.getId(), 0, notification);
            }
        });
    }

    /**
     * 开机后对通知栏未读消息进行推送
     *
     * @param context 上下文
     */
    public void pushMsgAfterPowerOn(final Context context) {
        ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
            @Override
            public void run() {
                int hasPushedCounts = 0;
                Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
                for (EMConversation conversation : conversations.values()) {
                    if (conversation == null || conversation.getUnreadMsgCount() <= 0)
                        continue;
                    EMMessage message = conversation.getLastMessage();
                    if (message == null || mPushedNotificationRepo.isPushed(message.getMsgId()))
                        continue;
                    handleMessage(context, message, false);
                    ++hasPushedCounts;
                    if (hasPushedCounts >= MAX_PUSH_MSG_COUNTS)
                        break;
                }
            }
        });
    }

}
