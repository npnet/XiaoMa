package com.xiaoma.personal.taskcenter.vm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.xiaoma.model.PageWrapper;
import com.xiaoma.model.ResultCallback;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.personal.common.RequestManager;
import com.xiaoma.personal.taskcenter.constract.TaskType;
import com.xiaoma.personal.taskcenter.model.OperateTask;

import java.util.List;

/**
 * <des>
 *
 * @author YangGang
 * @date 2018/12/4
 */
public class TaskOperateVM extends AndroidViewModel {

    private MutableLiveData<XmResource<List<OperateTask>>> mTaskDailyLiveData;
    private MutableLiveData<XmResource<List<OperateTask>>> mTaskGrowLiveData;
    private MutableLiveData<XmResource<List<OperateTask>>> mTaskActivityLiveData;
    private MutableLiveData<Object> mClearLiveData;

    public TaskOperateVM(@NonNull Application application) {
        super(application);
    }

    ///0每日任务 1新手任务 2活动任务
    public void fetchOperateTaskList(@TaskType final int catalog, int page) {
        if (catalog == TaskType.DAILY) {
            getTaskDailyLiveData().setValue(XmResource.<List<OperateTask>>loading());
        } else if (catalog == TaskType.GROW_UP) {
            getTaskGrowLiveData().setValue(XmResource.<List<OperateTask>>loading());
        } else if (catalog == TaskType.ACTIVITY) {
            getTaskActivityLiveData().setValue(XmResource.<List<OperateTask>>loading());
        }
        RequestManager.fetchTaskByType(catalog, page, new ResultCallback<XMResult<PageWrapper<OperateTask>>>() {
            @Override
            public void onSuccess(XMResult<PageWrapper<OperateTask>> result) {
                if (result.isSuccess()) {
                    PageWrapper<OperateTask> data = result.getData();
                    if (page == 0) getClearLiveData().setValue(null);
                    if (catalog == TaskType.DAILY) {
                        getTaskDailyLiveData().setValue(XmResource.success(data.getList()));
                    } else if (catalog == TaskType.GROW_UP) {
                        getTaskGrowLiveData().setValue(XmResource.success(data.getList()));
                    } else if (catalog == TaskType.ACTIVITY) {
                        getTaskActivityLiveData().setValue(XmResource.success(data.getList()));
                    }
                }

            }

            @Override
            public void onFailure(int code, String msg) {
                if (catalog == TaskType.DAILY) {
                    getTaskDailyLiveData().setValue(XmResource.<List<OperateTask>>error(code, msg));
                } else if (catalog == TaskType.GROW_UP) {
                    getTaskGrowLiveData().setValue(XmResource.<List<OperateTask>>error(code, msg));
                } else if (catalog == TaskType.ACTIVITY) {
                    getTaskActivityLiveData().setValue(XmResource.<List<OperateTask>>error(code, msg));
                }
            }
        });
    }

    public MutableLiveData<XmResource<List<OperateTask>>> getTaskDailyLiveData() {
        if (mTaskDailyLiveData == null) {
            mTaskDailyLiveData = new MutableLiveData<>();
        }
        return mTaskDailyLiveData;
    }

    public MutableLiveData<XmResource<List<OperateTask>>> getTaskGrowLiveData() {
        if (mTaskGrowLiveData == null) {
            mTaskGrowLiveData = new MutableLiveData<>();
        }
        return mTaskGrowLiveData;
    }

    public MutableLiveData<XmResource<List<OperateTask>>> getTaskActivityLiveData() {
        if (mTaskActivityLiveData == null) {
            mTaskActivityLiveData = new MutableLiveData<>();
        }
        return mTaskActivityLiveData;
    }

    public MutableLiveData<Object> getClearLiveData() {
        if (mClearLiveData == null) {
            mClearLiveData = new MutableLiveData<>();
        }
        return mClearLiveData;
    }

}
