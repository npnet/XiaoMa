package com.xiaoma.club.msg.conversation.repo;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.hyphenate.chat.EMClient;
import com.xiaoma.club.common.repo.ModelRepo;
import com.xiaoma.club.msg.conversation.model.TopConversation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by LKF on 2019-2-14 0014.
 * 置顶会话ID的数据仓库
 */
@Dao
public abstract class TopConversationRepo extends ModelRepo<TopConversation> {
    @Query("SELECT * FROM TopConversation WHERE userHxAccount=:userHxAccount AND conversationId=:conversationId")
    abstract protected TopConversation get(String userHxAccount, String conversationId);

    @Query("SELECT * FROM TopConversation WHERE userHxAccount=:userHxAccount")
    abstract protected TopConversation[] getTopConversations(String userHxAccount);

    public void append(String conversationId) {
        final String currHxAccount = EMClient.getInstance().getCurrentUser();
        insert(new TopConversation(currHxAccount, conversationId));
    }

    public void delete(String conversationId) {
        final String currHxAccount = EMClient.getInstance().getCurrentUser();
        delete(new TopConversation(currHxAccount, conversationId));
    }

    public boolean isTop(String conversationId) {
        final String currHxAccount = EMClient.getInstance().getCurrentUser();
        return get(currHxAccount, conversationId) != null;
    }

    /**
     * 获取当前用户的置顶回话ID集合
     */
    public Collection<String> getTopConversationIds() {
        final String currHxAccount = EMClient.getInstance().getCurrentUser();
        TopConversation[] conversations = getTopConversations(currHxAccount);
        if (conversations == null || conversations.length <= 0)
            return null;
        final Set<String> conversationIds = new HashSet<>(conversations.length);
        for (final TopConversation conversation : conversations) {
            conversationIds.add(conversation.getConversationId());
        }
        return conversationIds;
    }

    @Override
    protected String getTableName() {
        return TopConversation.class.getSimpleName();
    }
}
