package com.xiaoma.motorcade.common.manager;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.xiaoma.motorcade.common.model.GroupCardInfo;


/**
 * Created by LKF on 2019-1-14 0014.
 */
@Dao
public abstract class GroupRepo extends ModelRepo<GroupCardInfo> {
    private static final String QUERY_BY_GROUP_ID = "SELECT * FROM GroupCardInfo WHERE id=:groupId";
    private static final String QUERY_BY_HX_GROUP_ID = "SELECT * FROM GroupCardInfo WHERE hxGroupId=:hxGroupId LIMIT 1";

    @Query(QUERY_BY_GROUP_ID)
    abstract public GroupCardInfo get(long groupId);

    @Query(QUERY_BY_HX_GROUP_ID)
    abstract public GroupCardInfo get(String hxGroupId);

    @Query(QUERY_BY_GROUP_ID)
    abstract public LiveData<GroupCardInfo> getLiveData(long groupId);

    @Query(QUERY_BY_HX_GROUP_ID)
    abstract public LiveData<GroupCardInfo> getLiveData(String hxGroupId);

    @Override
    protected String getTableName() {
        return GroupCardInfo.class.getSimpleName();
    }
}
