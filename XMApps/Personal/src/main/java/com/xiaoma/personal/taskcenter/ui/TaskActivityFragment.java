package com.xiaoma.personal.taskcenter.ui;


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
@PageDescComponent(EventConstants.PageDescribe.ActivityTask)
public class TaskActivityFragment extends AbsTaskFragment {
    public static AbsTaskFragment newInstance() {
        return new TaskActivityFragment();
    }

    @Override
    @TaskType
    protected int getTaskType() {
        return TaskType.ACTIVITY;
    }
}
