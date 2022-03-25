package com.xiaoma.shop.common.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: LiangJingBo.
 * @Date: 2019/04/10
 * @Describe:
 */

public class TaskPool {
    private List<TaskMode> taskQueue = new ArrayList<>();


    /**
     * 添加任务
     *
     * @param taskMode
     */
    public void addTask(TaskMode taskMode) {
        taskQueue.add(taskMode);
    }

    /**
     * 结束任务
     *
     * @param listener
     */
    public void finish(TaskFinishListener listener) {
        if (!taskQueue.isEmpty()) {
            taskQueue.remove(0);
            if (taskQueue.isEmpty()) {
                listener.onComplete();
            }
        } else {
            listener.onComplete();
        }
    }

    public static class TaskMode {
    }

    public interface TaskFinishListener {
        void onComplete();
    }


}
