package com.xiaoma.motorcade.common.manager;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.xiaoma.model.User;

/**
 * Created by LKF on 2019-2-22 0022.
 */
@Dao
public abstract class UserRepo extends ModelRepo<User> {
    private static final String QUERY_BY_UID = "SELECT * FROM User WHERE id=:uid";
    private static final String QUERY_BY_HX_ACCOUNT = "SELECT * FROM User WHERE hxAccount=:hxAccount LIMIT 1";
    private static final String DELETE_BY_ID = "DELETE FROM User WHERE id=:uid";
    private static final String DELETE_BY_HX_ACCOUNT = "DELETE FROM User WHERE hxAccount=:hxAccount";

    @Query(QUERY_BY_UID)
    abstract protected User getById(long uid);

    @Query(QUERY_BY_HX_ACCOUNT)
    abstract protected User getByHxAccount(String hxAccount);

    public User getByKey(String key) {
        User u = getByHxAccount(key);
        if (u == null) {
            try {
                u = getById(Long.parseLong(key));
            } catch (NumberFormatException ignored) {
            }
        }
        return u;
    }


    @Query(QUERY_BY_UID)
    abstract protected LiveData<User> getLiveDataByUid(long uid);

    @Query(QUERY_BY_HX_ACCOUNT)
    abstract protected LiveData<User> getLiveDataByHxAccount(String hxAccount);

    public LiveData<User> getLiveDataByKey(String key) {
        LiveData<User> u = getLiveDataByHxAccount(key);
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
        return User.class.getSimpleName();
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
