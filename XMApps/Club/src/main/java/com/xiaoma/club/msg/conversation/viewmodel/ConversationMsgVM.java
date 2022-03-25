package com.xiaoma.club.msg.conversation.viewmodel;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.xiaoma.club.common.repo.ClubRepo;
import com.xiaoma.club.msg.conversation.util.ConversationUtil;
import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.XmResource;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.log.KLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.hyphenate.chat.EMConversation.EMConversationType.Chat;
import static com.hyphenate.chat.EMConversation.EMConversationType.GroupChat;

/**
 * Author: loren
 * Date: 2018/12/29 0029
 */

public class ConversationMsgVM extends BaseViewModel {

    private MutableLiveData<XmResource<List<Integer>>> msgCounts;
    private MutableLiveData<XmResource<List<EMConversation>>> groupMsgs;
    private MutableLiveData<XmResource<List<EMConversation>>> friendMsgs;

    public ConversationMsgVM(@NonNull Application application) {
        super(application);
    }

    @Override
    protected void onCleared() {
        super.onCleared();

    }

    public MutableLiveData<XmResource<List<Integer>>> getMsgCounts() {
        if (msgCounts == null) {
            msgCounts = new MutableLiveData<>();
        }
        return msgCounts;
    }

    public MutableLiveData<XmResource<List<EMConversation>>> getGroupMsgs() {
        if (groupMsgs == null) {
            groupMsgs = new MutableLiveData<>();
        }
        return groupMsgs;
    }

    public MutableLiveData<XmResource<List<EMConversation>>> getFriendMsgs() {
        if (friendMsgs == null) {
            friendMsgs = new MutableLiveData<>();
        }
        return friendMsgs;
    }

    public void requestGroupMsgs() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            getGroupMsgs().setValue(XmResource.response(getConversation(GroupChat)));
        } else {
            getGroupMsgs().postValue(XmResource.response(getConversation(GroupChat)));
        }
    }

    public void requestFriendMsgs() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            getFriendMsgs().setValue(XmResource.response(getConversation(Chat)));
        } else {
            getFriendMsgs().postValue(XmResource.response(getConversation(Chat)));
        }
    }

    private List<EMConversation> getConversation(EMConversation.EMConversationType type) {
        final List<EMConversation> conversations = EMClient.getInstance().chatManager().getConversationsByType(type);
        if (conversations != null && !conversations.isEmpty()) {
            final Collection<String> topConversations = ClubRepo.getInstance().getTopConversationRepo().getTopConversationIds();
            // 有未读消息优先,按照最新消息时间降序排序
            Collections.sort(conversations, new Comparator<EMConversation>() {
                @Override
                public int compare(@NonNull EMConversation o1, @NonNull EMConversation o2) {
                    final String conversationId1 = o1.conversationId();
                    final String conversationId2 = o2.conversationId();
                    // 置顶会话优先展示
                    if (topConversations != null) {
                        if (topConversations.contains(conversationId1) && !topConversations.contains(conversationId2)) {
                            return -1;
                        } else if (!topConversations.contains(conversationId1) && topConversations.contains(conversationId2)) {
                            return 1;
                        }
                    }
                    return -Long.compare(getTime(o1), getTime(o2));
                }

                private long getTime(@NonNull EMConversation conversation) {
                    final EMMessage msg = conversation.getLastMessage();
                    return msg != null ? msg.getMsgTime() : ConversationUtil.getConversationCreateTime(conversation);
                }
            });
        }
        KLog.d("getConversation type:" + type + "   size: " + (conversations != null ? conversations.size() : "null"));
        return conversations;
    }

    public void refreshConversation() {
        ThreadDispatcher.getDispatcher().postNormalPriority(new Runnable() {
            @Override
            public void run() {
                Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
                List<Integer> unReadCount = new ArrayList<>();
                int groupUnread = 0;
                int chatUnread = 0;
                for (EMConversation value : conversations.values()) {
                    if (value == null) {
                        continue;
                    }
                    if (value.getType() == GroupChat) {
                        groupUnread += value.getUnreadMsgCount();
                    } else if (value.getType() == Chat) {
                        chatUnread += value.getUnreadMsgCount();
                    }
                }
                unReadCount.add(0, groupUnread);
                unReadCount.add(1, chatUnread);
                getMsgCounts().postValue(XmResource.response(unReadCount));
            }
        });

    }

}
