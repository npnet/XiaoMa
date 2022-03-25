package com.xiaoma.club.common.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArraySet;
import android.text.TextUtils;

import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.xiaoma.carlib.manager.XmCarVendorExtensionManager;
import com.xiaoma.club.R;
import com.xiaoma.club.common.hyphenate.IMUtils;
import com.xiaoma.club.common.network.ClubRequestManager;
import com.xiaoma.club.common.repo.ClubRepo;
import com.xiaoma.club.common.util.LogUtil;
import com.xiaoma.club.msg.chat.constant.ContactEventTag;
import com.xiaoma.club.msg.chat.constant.MessageKey;
import com.xiaoma.club.msg.chat.constant.MessageType;
import com.xiaoma.club.msg.chat.model.GroupMuteUser;
import com.xiaoma.club.msg.hyphenate.SimpleGroupListener;
import com.xiaoma.club.msg.hyphenate.SimpleMessageListener;
import com.xiaoma.thread.ThreadDispatcher;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.xiaoma.club.ClubConstant.Hologram.OBTAIN_RED_PACKET_3D_ACTION_ID;

/**
 * Created by LKF on 2019-1-16 0016.
 */
public class ClubService extends Service {
    private static final String TAG = "ClubService";
    private EMMessageListener mMessageListener;
    private EMGroupChangeListener mGroupChangeListener;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.logI(TAG, "onCreate()");
        // 监听聊天消息
        try {
            EMClient.getInstance().chatManager().addMessageListener(mMessageListener = new HxMessageListener());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 监听群组消息
        try {
            EMClient.getInstance().groupManager().addGroupChangeListener(mGroupChangeListener = new SimpleGroupListener() {
                @Override
                public void onUserRemoved(String groupId, String groupName) {
                    deleteConversation(groupId);
                }

                @Override
                public void onGroupDestroyed(String groupId, String groupName) {
                    deleteConversation(groupId);
                }

                private void deleteConversation(String groupId) {
                    EMClient.getInstance().chatManager().deleteConversation(groupId, true);
                }

                @Override
                public void onMuteListAdded(String groupId, List<String> mutes, long muteExpire) {
                    if (TextUtils.isEmpty(groupId) || mutes == null)
                        return;
                    final List<GroupMuteUser> muteUsers = new ArrayList<>(mutes.size());
                    for (final String mute : mutes) {
                        muteUsers.add(new GroupMuteUser(groupId, mute, true));
                    }
                    ClubRepo.getInstance().getGroupMuteUserRepo().insertAll(muteUsers);
                }

                @Override
                public void onMuteListRemoved(String groupId, List<String> mutes) {
                    if (TextUtils.isEmpty(groupId) || mutes == null)
                        return;
                    final List<GroupMuteUser> muteUsers = new ArrayList<>(mutes.size());
                    for (final String mute : mutes) {
                        muteUsers.add(new GroupMuteUser(groupId, mute, false));
                    }
                    ClubRepo.getInstance().getGroupMuteUserRepo().insertAll(muteUsers);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 初始化好友关系
        ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
            @Override
            public void run() {
                try {
                    ClubRepo.getInstance().getFriendshipRepo().putAllContacts(IMUtils.getHxContacts());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.logI(TAG, "onDestroy()");
        try {
            EMClient.getInstance().chatManager().removeMessageListener(mMessageListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            EMClient.getInstance().groupManager().removeGroupChangeListener(mGroupChangeListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
        EventBus.getDefault().unregister(this);
    }

    @Subscriber(tag = ContactEventTag.ON_CONTACT_ADDED)
    private void onContactAdded(String contactHxAccount) {
        ClubRepo.getInstance().getFriendshipRepo().append(contactHxAccount);
    }

    @Subscriber(tag = ContactEventTag.ON_REQUEST_AGREE)
    private void onContactReqAgree(String contactHxAccount) {
        onContactAdded(contactHxAccount);
    }

    @Subscriber(tag = ContactEventTag.ON_CONTACT_DELETED)
    private void onContactDeleted(String contactHxAccount) {
        ClubRepo.getInstance().getFriendshipRepo().delete(contactHxAccount);
        // 删除对应的会话
        try {
            EMClient.getInstance().chatManager().deleteConversation(contactHxAccount, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 监听环信消息,及时同步用户信息及群组信息
     */
    private class HxMessageListener extends SimpleMessageListener {
        @Override
        public void onMessageReceived(List<EMMessage> list) {
            LogUtil.logI(TAG, "onMessageReceived( msgList: %s )", list);
            fetchSenders(list);
            handleRedPacket(list);
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> list) {
            LogUtil.logI(TAG, "onCmdMessageReceived( msgList: %s )", list);
            fetchSenders(list);
        }

        @Override
        public void onMessageRecalled(List<EMMessage> list) {
            LogUtil.logI(TAG, "onCmdMessageReceived( msgList: %s )", list);
            if (list != null && !list.isEmpty()) {
                for (EMMessage message : list) {
                    // 记录撤回消息
                    final String nickname = message.getStringAttribute(MessageKey.USER_NICKNAME, "");
                    if (!TextUtils.isEmpty(nickname)) {
                        IMUtils.saveRecallMessageTips(message, getResources().getString(R.string.recall_msg_notify_format, nickname));
                    }
                }
            }
        }

        private void handleRedPacket(final List<EMMessage> list) {
            // 处理红包消息,发送3D动作
            if (list != null && !list.isEmpty()) {
                ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
                    @Override
                    public void run() {
                        for (EMMessage message : list) {
                            if (EMMessage.Direct.SEND == message.direct())
                                continue;
                            EMMessage.Type emType = message.getType();
                            if (EMMessage.Type.TXT != emType)
                                continue;
                            String msgType = message.getStringAttribute(MessageKey.MSG_TYPE, "");
                            if (!MessageType.RED_PACKET.equals(msgType))
                                continue;
                            XmCarVendorExtensionManager.getInstance().setRobAction(OBTAIN_RED_PACKET_3D_ACTION_ID);
                            break;
                        }
                    }
                });
            }
        }

        private void fetchSenders(List<EMMessage> list) {
            LogUtil.logI(TAG, "fetchSenders( msgList: %s )", list);
            if (list == null || list.isEmpty())
                return;
            final Set<String> hxUserAccounts = new ArraySet<>();
            final Set<String> hxGroupIds = new ArraySet<>();
            for (final EMMessage message : list) {
                final EMMessage.ChatType chatType = message.getChatType();
                switch (chatType) {
                    case Chat:
                        hxUserAccounts.add(message.getTo());
                        break;
                    case GroupChat:
                        hxGroupIds.add(message.getTo());
                        break;
                }
            }
            fetchUsers(hxUserAccounts);
            fetchGroups(hxGroupIds);
        }

        private void fetchUsers(@NonNull Set<String> hxUserAccounts) {
            final StringBuilder log = new StringBuilder(String.format("fetchUsers( hxUserAccounts: %s )", hxUserAccounts));
            log.append(" [ ");
            for (final String account : hxUserAccounts) {
                if (ClubRepo.getInstance().getUserRepo().getByKey(account) == null) {
                    log.append(account).append(",");
                    ClubRequestManager.getUserByHxAccount(account, null);
                }
            }
            log.deleteCharAt(log.length() - 1);
            log.append(" ]");
            LogUtil.logI(TAG, log.toString());
        }

        private void fetchGroups(@NonNull Set<String> hxGroupIds) {
            final StringBuilder log = new StringBuilder(String.format("fetchGroups( hxGroupIds: %s )", hxGroupIds));
            log.append(" [ ");
            for (final String id : hxGroupIds) {
                // 无群组缓存时,主动拉取群信息
                if (ClubRepo.getInstance().getGroupRepo().get(id) == null) {
                    log.append(id).append(",");
                    ClubRequestManager.getGroupByHxId(id, null);
                }
                // 无群组禁言列表时,主动拉取禁言列表
                final Collection<String> muteUsers = ClubRepo.getInstance().getGroupMuteUserRepo().getMuteUsersByHxGroupId(id);
                if (muteUsers == null || muteUsers.isEmpty()) {
                    ClubRequestManager.getGroupMuteListByHxId(id, null);
                }
            }
            log.deleteCharAt(log.length() - 1);
            log.append(" ]");
            LogUtil.logI(TAG, log.toString());
        }
    }
}
