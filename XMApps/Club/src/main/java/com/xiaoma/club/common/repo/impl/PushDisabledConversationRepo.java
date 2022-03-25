package com.xiaoma.club.common.repo.impl;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.hyphenate.chat.EMClient;
import com.xiaoma.club.common.model.PushDisabledConversation;
import com.xiaoma.club.common.repo.ModelRepo;

/**
 * Created by LKF on 2019-3-29 0029.
 * 禁用通知推送的会话数据仓库
 */
@Dao
public abstract class PushDisabledConversationRepo extends ModelRepo<PushDisabledConversation> {
    @Query("SELECT * FROM PushDisabledConversation WHERE userHxId=:currHxUser AND disabledHxId=:hxAccount")
    protected abstract PushDisabledConversation query(String currHxUser, String hxAccount);

    public void append(String hxAccount) {
        final String myHxAccount = EMClient.getInstance().getCurrentUser();
        insert(new PushDisabledConversation(myHxAccount, hxAccount));
    }

    public void delete(String hxAccount) {
        final String myHxAccount = EMClient.getInstance().getCurrentUser();
        delete(new PushDisabledConversation(myHxAccount, hxAccount));
    }

    public boolean isPushDisabled(String hxAccount) {
        final String myHxAccount = EMClient.getInstance().getCurrentUser();
        return query(myHxAccount, hxAccount) != null;
    }

    @Override
    protected String getTableName() {
        return PushDisabledConversation.class.getSimpleName();
    }
}
