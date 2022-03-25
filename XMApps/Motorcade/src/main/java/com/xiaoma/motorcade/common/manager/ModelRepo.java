package com.xiaoma.motorcade.common.manager;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.OnConflictStrategy;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by LKF on 2019-2-18 0018.
 * 数据仓库基础类,只定义了插入/移除功能,查询接口由子类根据业务需要自行实现,这里不统一规范.
 */
public abstract class ModelRepo<MODEL> {
    // 如果主键存在,直接替换
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long put(MODEL model);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long[] putAll(Collection<MODEL> models);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long[] putAll(MODEL[] models);

    @Delete
    public abstract int remove(MODEL model);

    @Delete
    public abstract int removeAll(Collection<MODEL> models);

    @Delete
    public abstract int removeAll(MODEL[] models);

    abstract protected String getTableName();

    private final Map<RepoObserver, InvalidationTracker.Observer> mObservers = new ArrayMap<>();

    public void addObserver(final RepoObserver repoObserver) {
        final String table = getTableName();
        if (TextUtils.isEmpty(table)) {
            throw new IllegalStateException("Only repository with table name, can be observe, you should override #getTableName()");
        }
        if (repoObserver == null)
            return;
        InvalidationTracker.Observer realObserver = mObservers.get(repoObserver);
        if (realObserver != null)
            return;
        realObserver = new InvalidationTracker.Observer(table) {
            @Override
            public void onInvalidated(@NonNull Set<String> tables) {
                repoObserver.onChanged(table);
            }
        };
        MotorcadeRepo.getInstance().getInvalidationTracker().addObserver(realObserver);
        mObservers.put(repoObserver, realObserver);
    }

    public void removeObserver(RepoObserver repoObserver) {
        if (repoObserver == null)
            return;
        mObservers.remove(repoObserver);
    }
}
