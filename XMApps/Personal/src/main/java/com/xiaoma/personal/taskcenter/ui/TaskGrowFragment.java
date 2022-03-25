package com.xiaoma.personal.taskcenter.ui;


import android.os.Bundle;
import android.support.annotation.Nullable;

import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.personal.common.util.EventConstants;
import com.xiaoma.personal.taskcenter.constract.TaskType;

/**
 * <pre>
 *     author : wutao
 *     time   : 2018/12/03
 *     desc   :
 * </pre>
 */
@PageDescComponent(EventConstants.PageDescribe.GrowTask)
public class TaskGrowFragment extends AbsTaskFragment {
    public static AbsTaskFragment newInstance() {
        return new TaskGrowFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    @TaskType
    protected int getTaskType() {
        return TaskType.GROW_UP;
    }
}
