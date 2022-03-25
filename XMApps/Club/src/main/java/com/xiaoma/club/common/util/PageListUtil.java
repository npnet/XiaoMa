package com.xiaoma.club.common.util;

import android.arch.paging.PagedList;

import com.xiaoma.utils.reflect.Reflect;


/**
 * Created by LKF on 2018/10/13 0013.
 */
public class PageListUtil {
    /**
     * 设置当前{@link PagedList}是否可以继续加载更多
     */
    public static <T> void setLoadMoreEnable(PagedList<T> pagedList, boolean enable) {
        if (pagedList == null)
            return;
        Reflect.on(pagedList.getClass())
                .field("mAppendWorkerRunning")
                .set(pagedList, !enable);
    }
}
