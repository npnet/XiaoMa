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
public class TaskStateConstract {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TaskState.DEFAULT, TaskState.PRE_START,
            TaskState.STARTING, TaskState.FINISHED, TaskState.TIME_OUT})
    public @interface TaskState {
        int DEFAULT = 0;
        int PRE_START = 1;
        int STARTING = 2;
        int FINISHED = 3;
        int TIME_OUT = 4;
    }

    public static boolean checkStateEnable(@TaskState int state) {
        return state == TaskState.STARTING;
    }

    public static class TaskStateException extends RuntimeException {

        public TaskStateException() {
            super("pls set task state!");
        }
    }
}
