package com.xiaoma.club.msg.chat.repo;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.hyphenate.chat.EMClient;
import com.xiaoma.club.common.repo.ModelRepo;
import com.xiaoma.club.msg.chat.model.GroupMuteUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by LKF on 2019-1-30 0030.
 * 群组禁言缓存池,key:群组环信ID, value:禁言用户环信ID集合
 * TODO 实现拉取数据的业务
 */
@Dao
public abstract class GroupMuteUserRepo extends ModelRepo<GroupMuteUser> {
    private static final String QUERY_BY_PRIMARY_KEYS = "SELECT * FROM GroupMuteUser WHERE hxGroupId=:hxGroupId AND  userHxAccount=:userHxAccount";

    @Query(QUERY_BY_PRIMARY_KEYS)
    abstract protected GroupMuteUser get(String hxGroupId, String userHxAccount);

    @Query(QUERY_BY_PRIMARY_KEYS)
    abstract protected LiveData<GroupMuteUser> getLiveData(String hxGroupId, String userHxAccount);

    @Query("SELECT * FROM GroupMuteUser where hxGroupId=:hxGroupId ")
    abstract protected GroupMuteUser[] getMuteUsers(String hxGroupId);

    @Query("DELETE  FROM GroupMuteUser WHERE hxGroupId=:hxGroupId")
    abstract protected void clearMuteUsers(String hxGroupId);

    /**
     * 添加群组禁言用户列表,自动移除原有的禁用列表
     */
    public void putMuteUsers(String hxGroupId, Collection<String> muteUserHxAccounts) {
        clearMuteUsers(hxGroupId);
        if (muteUserHxAccounts != null && !muteUserHxAccounts.isEmpty()) {
            List<GroupMuteUser> muteUserList = new ArrayList<>();
            for (final String account : muteUserHxAccounts) {
                muteUserList.add(new GroupMuteUser(hxGroupId, account, true));
            }
            insertAll(muteUserList);
        }
    }

    /**
     * 自己是否被禁言
     */
    public boolean isMute(String hxGroupId) {
        return isUserMute(hxGroupId, EMClient.getInstance().getCurrentUser());
    }

    /**
     * 返回是否被禁言的LiveData类型
     */
    public LiveData<GroupMuteUser> isMuteLiveData(String hxGroupId) {
        return getLiveData(hxGroupId, EMClient.getInstance().getCurrentUser());
    }

    /**
     * 查看指定用户是否被禁言
     */
    public boolean isUserMute(String hxGroupId, String userHxAccount) {
        final GroupMuteUser muteUser = get(hxGroupId, userHxAccount);
        return muteUser != null && muteUser.isMute();
    }

    public Set<String> getMuteUsersByHxGroupId(String hxGroupId) {
        final GroupMuteUser[] groupMuteUsers = getMuteUsers(hxGroupId);
        if (groupMuteUsers == null || groupMuteUsers.length <= 0)
            return null;
        final Set<String> userSet = new HashSet<>();
        for (final GroupMuteUser user : groupMuteUsers) {
            if (user.isMute()) {
                userSet.add(user.getUserHxAccount());
            }
        }
        return userSet;
    }

    @Override
    protected String getTableName() {
        return GroupMuteUser.class.getSimpleName();
    }
}
