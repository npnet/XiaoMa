package com.xiaoma.shop.common.constant;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <des>
 *
 * @author YangGang
 * @date 2019/3/6
 */

@IntDef({LoadMoreState.DEFAULT, LoadMoreState.COMPLETE,
        LoadMoreState.END, LoadMoreState.FAIL})
@Retention(RetentionPolicy.SOURCE)
public @interface LoadMoreState {

    int DEFAULT = 0;  //默认 不做处理
    int COMPLETE = 1; // 每次加载完成,但还没有加载到底部
    int END = 2; // 加载完成 且加载到底部
    int FAIL = 3; // 加载失败
}
