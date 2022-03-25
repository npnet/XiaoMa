package com.xiaoma.personal.taskcenter.ui;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
@PageDescComponent(EventConstants.PageDescribe.DailyTask)
public class TaskDailyFragment extends AbsTaskFragment {
    public static AbsTaskFragment newInstance() {
        return new TaskDailyFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    @TaskType
    protected int getTaskType() {
        return TaskType.DAILY;
    }
}
