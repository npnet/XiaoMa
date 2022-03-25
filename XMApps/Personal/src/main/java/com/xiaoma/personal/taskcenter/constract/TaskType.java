package com.xiaoma.personal.taskcenter.constract;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <des>
 *
 * @author YangGang
 * @date 2018/12/4
 */
@IntDef({TaskType.DAILY, TaskType.GROW_UP, TaskType.ACTIVITY})
@Retention(RetentionPolicy.SOURCE)
public @interface TaskType {
    int DAILY = 0;
    int GROW_UP = 1;
    int ACTIVITY = 2;
}
