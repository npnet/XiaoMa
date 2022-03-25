package com.xiaoma.motorcade.common.manager;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.xiaoma.motorcade.common.model.GroupMemberInfo;

/**
 * 简介:
 *
 * @author lingyan
 */
@Dao
public abstract class GroupMemberRepo extends ModelRepo<GroupMemberInfo> {
    private static final String QUERY_BY_UID = "SELECT * FROM GroupMemberInfo WHERE id=:uid";
    private static final String QUERY_BY_HX_ACCOUNT = "SELECT * FROM GroupMemberInfo WHERE hxAccount=:hxAccount LIMIT 1";
    private static final String DELETE_BY_ID = "DELETE FROM GroupMemberInfo WHERE id=:uid";
    private static final String DELETE_BY_HX_ACCOUNT = "DELETE FROM GroupMemberInfo WHERE hxAccount=:hxAccount";

    @Query(QUERY_BY_UID)
    abstract protected GroupMemberInfo getById(long uid);

    @Query(QUERY_BY_HX_ACCOUNT)
    abstract protected GroupMemberInfo getByHxAccount(String hxAccount);

    public GroupMemberInfo getByKey(String key) {
        GroupMemberInfo u = getByHxAccount(key);
        if (u == null) {
            try {
                u = getById(Long.parseLong(key));
            } catch (NumberFormatException ignored) {
            }
        }
        return u;
    }


    @Query(QUERY_BY_UID)
    abstract protected LiveData<GroupMemberInfo> getLiveDataByUid(long uid);

    @Query(QUERY_BY_HX_ACCOUNT)
    abstract protected LiveData<GroupMemberInfo> getLiveDataByHxAccount(String hxAccount);

    public LiveData<GroupMemberInfo> getLiveDataByKey(String key) {
        LiveData<GroupMemberInfo> u = getLiveDataByHxAccount(key);
        if (u == null) {
            try {
                u = getLiveDataByUid(Long.parseLong(key));
            } catch (NumberFormatException ignored) {
            }
        }
        return u;
    }

    @Query(DELETE_BY_ID)
    abstract protected int removeByUid(long uid);

    @Query(DELETE_BY_HX_ACCOUNT)
    abstract protected int removeByHxAccount(String hxAccount);

    @Override
    protected String getTableName() {
        return GroupMemberInfo.class.getSimpleName();
    }

    public int removeByKey(String key) {
        int count = 0;
        try {
            long uid = Long.parseLong(key);
            count += removeByUid(uid);
        } catch (NumberFormatException ignore) {
        }
        count += removeByHxAccount(key);
        return count;
    }

}
