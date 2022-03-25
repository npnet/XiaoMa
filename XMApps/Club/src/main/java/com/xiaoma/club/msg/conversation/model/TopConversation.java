package com.xiaoma.club.msg.conversation.model;

import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

/**
 * Created by LKF on 2019-2-25 0025.
 * 置顶会话的记录Model
 */
@Entity(primaryKeys = {"userHxAccount", "conversationId"})
public class TopConversation {
    @NonNull
    private String userHxAccount;
    @NonNull
    private String conversationId;

    public TopConversation(@NonNull String userHxAccount, @NonNull String conversationId) {
        this.userHxAccount = userHxAccount;
        this.conversationId = conversationId;
    }

    @NonNull
    public String getUserHxAccount() {
        return userHxAccount;
    }

    public void setUserHxAccount(@NonNull String userHxAccount) {
        this.userHxAccount = userHxAccount;
    }

    @NonNull
    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(@NonNull String conversationId) {
        this.conversationId = conversationId;
    }
}
