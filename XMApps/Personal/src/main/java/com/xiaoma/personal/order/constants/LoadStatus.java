package com.xiaoma.personal.order.constants;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <pre>
 *       @author Created by Gillben
 *       date: 2019/3/14 0014 14:50
 *       desc：加载状态
 *
 * </pre>
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({LoadStatus.Complete, LoadStatus.End, LoadStatus.Error})
public @interface LoadStatus {

    //加载完成
    int Complete = 0;
    //加载结束
    int End = 1;
    //加载失败
    int Error = 2;
}
