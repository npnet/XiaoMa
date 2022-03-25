package com.xiaoma.motorcade.common.manager;

/**
 * Created by LKF on 2019-2-22 0022.
 * 数据仓库观察者
 */
public interface RepoObserver {
    void onChanged(String table);
}
