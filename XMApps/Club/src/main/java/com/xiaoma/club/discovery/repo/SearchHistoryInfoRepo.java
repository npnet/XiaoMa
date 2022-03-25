package com.xiaoma.club.discovery.repo;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.text.TextUtils;

import com.xiaoma.club.common.repo.ModelRepo;
import com.xiaoma.club.discovery.model.SearchHistoryInfo;

import java.util.List;

/**
 * Created by LKF on 2019-4-8 0008.
 */
@Dao
public abstract class SearchHistoryInfoRepo extends ModelRepo<SearchHistoryInfo> {
    @Query("SELECT * FROM SearchHistoryInfo ORDER BY id DESC")
    public abstract LiveData<List<SearchHistoryInfo>> queryAll();

    @Query("DELETE FROM SearchHistoryInfo WHERE searchContent=:keyword")
    protected abstract void deleteByKeyword(String keyword);

    public void insertKeyword(String keyword) {
        if (TextUtils.isEmpty(keyword))
            return;
        deleteByKeyword(keyword);
        insert(new SearchHistoryInfo(keyword));
    }

    @Query("DELETE FROM SearchHistoryInfo")
    public abstract int clearAll();

    @Override
    protected String getTableName() {
        return SearchHistoryInfo.class.getSimpleName();
    }
}
