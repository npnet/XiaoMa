package com.xiaoma.personal.taskcenter.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.component.base.VisibleFragment;
import com.xiaoma.component.nodejump.NodeConst;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.personal.R;
import com.xiaoma.personal.common.util.EventConstants;
import com.xiaoma.personal.common.util.RecyclerViewHelper;
import com.xiaoma.personal.taskcenter.adapter.OperateTaskAdapter;
import com.xiaoma.personal.taskcenter.constract.TaskType;
import com.xiaoma.personal.taskcenter.model.OperateTask;
import com.xiaoma.personal.taskcenter.vm.TaskOperateVM;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.AppUtils;
import com.xiaoma.utils.KeyEventUtils;
import com.xiaoma.utils.LaunchUtils;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;

import java.util.List;
import java.util.Objects;

/**
 * <pre>
 *     author : wutao
 *     time   : 2018/12/03
 *     desc   :
 * </pre>
 */
@PageDescComponent(EventConstants.PageDescribe.taskFragment)
public abstract class AbsTaskFragment extends VisibleFragment implements TaskCenterActivity.OnSignInCallback {

    private static final String TAG = AbsTaskFragment.class.getSimpleName();
    private static final String LINK_SCHEME = "xiaoma";
    private static final String LINK_PARAM = "param";
    protected RecyclerView mRecyclerView;
    private BaseQuickAdapter<OperateTask, BaseViewHolder> mAdapter;
    private TaskOperateVM mTaskOperateVM;
    private int mPage;
    private XmScrollBar mScrollBar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.recycler_view, container, false);
        inflate.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        return super.onCreateWrapView(inflate);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((TaskCenterActivity) getActivity()).setOnSignInCallback(this);
        mStateView.setEmptyView(R.layout.include_no_task);
        bindView();
        initVM();
        checkNet();
    }

    private void initVM() {
        mTaskOperateVM = ViewModelProviders.of(this).get(TaskOperateVM.class);
        mTaskOperateVM.getTaskActivityLiveData().observe(this, new Observer<XmResource<List<OperateTask>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<OperateTask>> listXmResource) {
                setAdapterData(listXmResource);
            }
        });
        mTaskOperateVM.getTaskDailyLiveData().observe(this, new Observer<XmResource<List<OperateTask>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<OperateTask>> listXmResource) {
                setAdapterData(listXmResource);
            }
        });
        mTaskOperateVM.getTaskGrowLiveData().observe(this, new Observer<XmResource<List<OperateTask>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<OperateTask>> listXmResource) {
                setAdapterData(listXmResource);
            }
        });
        mTaskOperateVM.getClearLiveData().observe(this, new Observer<Object>() {
            @Override
            public void onChanged(@Nullable Object o) {
                mAdapter.setNewData(null);
            }
        });
    }

    private void setAdapterData(@Nullable XmResource<List<OperateTask>> listXmResource) {
        listXmResource.handle(new OnCallback<List<OperateTask>>() {

            @Override
            public void onLoading() {
                if (mPage == 0) {
                    super.onLoading();
//                    showLoadingView();
                }
            }

            @Override
            public void onSuccess(List<OperateTask> data) {
                showContentView();
                mAdapter.loadMoreComplete();
                if (data == null || data.size() < 20) {
                    mAdapter.loadMoreEnd(true);
                }
                if (!ListUtils.isEmpty(data)) {
                    if (mPage == 0) {
                        mAdapter.setNewData(data);
                    } else {
                        mAdapter.addData(data);
                    }
                } else {
                    if (mPage != 0) {
                        mAdapter.loadMoreEnd();
                    } else {
                        showEmptyView();
                    }
                }
            }

            @Override
            public void onFailure(String msg) {
                super.onFailure(msg);
                if (mPage > 0) {
                    mPage--;
                } else if (mPage == 0) {
//                    if (!NetworkUtils.isConnected(getContext())) {
                    showNoNetView();
//                    } else {
//                        showErrorView();
//                    }
                }
            }

            @Override
            public void onError(int code, String message) {
                super.onError(code, message);
                if (mPage > 0) {
                    mPage--;
                } else if (mPage == 0) {
//                    if (!NetworkUtils.isConnected(getContext())) {
                    showNoNetView();
//                    } else {
//                        showErrorView();
//                    }
                }
            }
        });
    }

    private void bindView() {
        mRecyclerView = getView().findViewById(R.id.recycler_view);
        mScrollBar = getView().findViewById(R.id.scroll_bar);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new OperateTaskAdapter(mActivity, getTaskType());
        mAdapter.bindToRecyclerView(mRecyclerView);
        mScrollBar.setRecyclerView(mRecyclerView);
        mAdapter.setEnableLoadMore(true);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mPage++;
                mTaskOperateVM.fetchOperateTaskList(getTaskType(), mPage);
            }
        }, mRecyclerView);

        RecyclerViewHelper.addVerticalDivider(mRecyclerView, R.drawable.horizontal_divide_line);
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.btnOperate) {
                    OperateTask task = (OperateTask) adapter.getData().get(position);
                    if (task != null) {
                        handleFinishTask(task);
                        XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.toCompleteTask,
                                task.getDescription(), "AbsTaskFragment", EventConstants.PageDescribe.taskFragment);
                    }
                }
            }
        });
    }


    private void handleFinishTask(OperateTask task) {
        String appUri = task.getJumpUri();
        Uri uri = Uri.parse(appUri);
        String scheme = uri.getScheme();
        if (!LINK_SCHEME.equals(scheme)) {
            KLog.w(TAG, "scheme is error.");
            return;
        }
        String host = uri.getHost();
        String path = uri.getPath();
        String param = uri.getQueryParameter(LINK_PARAM);
        String className = path == null
                ? null
                : path.trim().isEmpty()
                ? null
                : path.substring(1);
        Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
                + "\n * url:" + appUri
                + "\n * host:" + host
                + "\n * path:" + path
                + "\n * param:" + param
                + "\n * className:" + className
        );

        // 应用未安装
        if (!AppUtils.isAppInstalled(mContext, host)) {
            XMToast.toastException(mContext, R.string.app_not_install);
            return;
        }
        // 非小马应用,直接打开app
        if (!host.startsWith("com.xiaoma")) {
            LaunchUtils.launchApp(mContext, host);
            return;
        }
        // 个人中心首页，调用finish
        if (mContext.getPackageName().equals(host)) {
            Objects.requireNonNull(getActivity()).finish();
            return;
        }
        Bundle bundle = null;
        if (NodeConst.LAUNCHER.DESK_SERVICE.equals(param)) {
            bundle = new Bundle();
            bundle.putBoolean(KeyEventUtils.GO_HOME_SERVICE, true);
        }
        LaunchUtils.launchAppOnlyNewTask(mContext, host, className, bundle);
    }

    @Override
    public void onVisibleChange(boolean realVisible) {
        super.onVisibleChange(realVisible);
        if (realVisible) {
            mPage = 0;
            mTaskOperateVM.fetchOperateTaskList(getTaskType(), mPage);
        }
    }

    private void checkNet() {
        if (NetworkUtils.isConnected(getContext())) {
            mPage = 0;
            mTaskOperateVM.fetchOperateTaskList(getTaskType(), mPage);
        } else {
            showNoNetView();
        }
    }

    @Override
    protected void noNetworkOnRetry() {
        checkNet();
    }


    @Override
    public void signIn() {
        checkNet();
    }

    @TaskType
    protected abstract int getTaskType();
}
