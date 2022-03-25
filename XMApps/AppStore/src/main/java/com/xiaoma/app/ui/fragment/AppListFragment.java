package com.xiaoma.app.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xiaoma.app.R;
import com.xiaoma.app.adapter.AppListAdapter;
import com.xiaoma.app.common.constant.AppStoreConstants;
import com.xiaoma.app.common.constant.EventConstants;
import com.xiaoma.app.model.AppStateEvent;
import com.xiaoma.app.model.CancelItemEvent;
import com.xiaoma.app.model.DownLoadAppInfo;
import com.xiaoma.app.model.NetworkChangedEvent;
import com.xiaoma.app.ui.activity.AppDetailsActivity;
import com.xiaoma.app.vm.AppListVM;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.network.model.Progress;
import com.xiaoma.network.okserver.OkDownload;
import com.xiaoma.network.okserver.download.DownloadTask;
import com.xiaoma.ui.adapter.XMBaseAbstractRyAdapter;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.log.KLog;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 2018/10/12 0012
 * 应用列表
 */
@PageDescComponent(EventConstants.PageDescribe.appListFragmentPagePathDesc)
public class AppListFragment extends BaseFragment {
    private final String TAG = getClass().getSimpleName();
    private RecyclerView mRv;
    private XmScrollBar xmScrollBar;
    private AppListVM mAppListVM;
    private AppListAdapter mAdapter;
    //待更新数目监听
    private AppManagerFragment.WaitUpdateCountListener waitUpdateCountListener;
    //待更新版本APP list
    private List<DownLoadAppInfo> stateOldAppInfoList;

    public static AppListFragment newInstance() {
        return new AppListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fmt_app_list, container, false);
        return super.onCreateWrapView(view);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
        EventBus.getDefault().register(this);
    }

    private void initView(View view) {
        mRv = view.findViewById(R.id.rv_app_list);
        xmScrollBar = view.findViewById(R.id.scroll_bar);
        mRv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mAdapter = new AppListAdapter(getActivity(), new ArrayList<DownLoadAppInfo>(), R.layout.item_app_list);
        mAdapter.setOnItemClickListener(new XMBaseAbstractRyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.Adapter adapter, View view, RecyclerView.ViewHolder holder, int position) {
                AppDetailsActivity.startActivityInApp(mContext, mAdapter.getDatas().get(position), AppStoreConstants.APP_MARKRT);
            }

        });
        mRv.setAdapter(mAdapter);
        xmScrollBar.setRecyclerView(mRv);

//       mRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
//           @Override
//           public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//               if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    mAdapter.setScroll(false);
//               }else {
//                   mAdapter.setScroll(true);
//               }
//           }
//       });
    }

    private void initData() {
        mAppListVM = ViewModelProviders.of(this).get(AppListVM.class);
        mAppListVM.getAppInfoList().observe(this, new Observer<XmResource<List<DownLoadAppInfo>>>() {
            @Override
            public void onChanged(@Nullable XmResource<List<DownLoadAppInfo>> listXmResource) {
                if (listXmResource == null) {
                    return;
                }
                listXmResource.handle(new OnCallback<List<DownLoadAppInfo>>() {
                    @Override
                    public void onSuccess(List<DownLoadAppInfo> data) {
                        if (!ListUtils.isEmpty(data)) {
                            stateOldAppInfoList = getWaitUpdateApp(data);
                            if (waitUpdateCountListener != null) {
                                waitUpdateCountListener.setWaitUpdateCount(stateOldAppInfoList.size());
                            }
                            mAdapter.setDatas(data);
                            showContentView();

                        } else {
                            mStateView.setEmptyView(R.layout.view_no_app);
                            showEmptyView();
                        }
                    }

                    @Override
                    public void onError(int code, String message) {
                        super.onError(code, message);
                        if (code == AppStoreConstants.EMPTY_DATA_RESULT_CODE) {
                            mStateView.setEmptyView(R.layout.view_no_app);
                            showEmptyView();

                        } else {
                            showNoNetView();
                        }
                    }
                });
            }
        });
        mAppListVM.fetchAppInfoList();
    }

    /**
     * 获取待更新应用list
     *
     * @param data
     * @return
     */
    private List<DownLoadAppInfo> getWaitUpdateApp(List<DownLoadAppInfo> data) {
        List<DownLoadAppInfo> waitUpdateList = new ArrayList<>();
        for (DownLoadAppInfo downLoadAppInfo : data) {
            if (downLoadAppInfo.getInstallState() == AppStoreConstants.INSTALL_STATE_OLD) {
                waitUpdateList.add(downLoadAppInfo);
            }
        }

        return waitUpdateList;
    }

    public void setWaitUpdateCountListener(AppManagerFragment.WaitUpdateCountListener listener) {
        this.waitUpdateCountListener = listener;
    }

    @Override
    protected void noNetworkOnRetry() {
        super.noNetworkOnRetry();
        mAppListVM.fetchAppInfoList();
    }

    /**
     * 下载任务取消回调
     *
     * @param cancelItemEvent
     */
    @Subscriber(tag = AppStoreConstants.MSG_DOWNLOAD_LIST_ITEM_CANCEL)
    public void downloadItemCancel(CancelItemEvent cancelItemEvent) {
        Log.d(TAG, "AppStoreConstants.MSG_DOWNLOAD_LIST_ITEM_CANCEL：downloadItemCancel:" + cancelItemEvent);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * App安装卸载回调
     *
     * @param appStateEvent
     */
    @Subscriber(tag = AppStoreConstants.APP_INSTALL_RECEIVER)
    public void appStateChange(AppStateEvent appStateEvent) {
        Log.d(TAG, TAG + ":" + appStateEvent.toString());
        String packageName = appStateEvent.getPackageName();
        int appState = AppStoreConstants.INSTALL_STATE_NEW;
        switch (appStateEvent.getAppState()) {
            //安装
            //替换
            case AppStateEvent.STATE_PACKAGE_ADDED:
            case AppStateEvent.STATE_PACKAGE_REPLACED:
                //安装失败
                if (!appStateEvent.isResult()) {
                    //1.标记任务失败
                    DownloadTask task = OkDownload.getInstance().getTask(packageName);
                    if (task == null) {
                        return;
                    }
                    task.progress.status = Progress.INSTALL_FAILED;
                    task.updateDatabase(task.progress);
                    //2.刷新视图
                    mAdapter.notifyDataSetChanged();
                    //3.弹框
                    showInstallErrorView();

                    return;
                }
                //安装成功
                DownloadTask task = OkDownload.getInstance().getTask(packageName);
                if (task != null) {
                    task.remove(true);
                }
                appState = AppStoreConstants.INSTALL_STATE_NEW;

                //如果是待更新安装成功还需要刷新红点
                for (DownLoadAppInfo waitUpdateApp : stateOldAppInfoList) {
                    if (packageName.equals(waitUpdateApp.getAppInfo().getPackageName())) {
                        stateOldAppInfoList.remove(waitUpdateApp);
                        if (waitUpdateCountListener == null) {
                            return;
                        }
                        waitUpdateCountListener.setWaitUpdateCount(stateOldAppInfoList.size());
                        break;
                    }
                }
                break;

            //卸载
            case AppStateEvent.STATE_PACKAGE_REMOVED:
                //卸载失败
                if (!appStateEvent.isResult()) {
                    return;
                }
                appState = AppStoreConstants.INSTALL_STATE_NOTHING;
                break;
        }
        //刷新列表数据
        List<DownLoadAppInfo> loadAppInfos = mAdapter.getDatas();
        for (DownLoadAppInfo info : loadAppInfos) {
            if (info.getAppInfo().getPackageName().equals(packageName)) {
                // 针对【system ui/人脸识别/ 双屏互动/语音助手/车应用】应用屏蔽
                if (AppStoreConstants.SYSTEM_UI.equals(packageName) ||
                        AppStoreConstants.FACERECOGNIZE.equals(packageName) ||
                        AppStoreConstants.DUAL_SCREEN.equals(packageName) ||
                        AppStoreConstants.ASSISTANT.equals(packageName) ||
                        AppStoreConstants.APPSTORE.equals(packageName)) {
                    loadAppInfos.remove(info);
                }
                info.setInstallState(appState);
                mAdapter.setDatas(loadAppInfos);
                break;
            }
        }
    }

    /**
     * 安装异常弹框
     */
    private void showInstallErrorView() {
        final ConfirmDialog dialog = new ConfirmDialog(getActivity());
        dialog.setContent(getString(R.string.tips_install_error))
                .setPositiveButton(getString(R.string.i_down), new View.OnClickListener() {
                    @Override
                    @NormalOnClick({EventConstants.NormalClick.installError})
                    @ResId({R.id.sure})
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                })
                .setNegativeButtonVisibility(false)
                .show();

//        View view = View.inflate(getContext(), R.layout.dialog_install_error, null);
//        final XmDialog builder = new XmDialog.Builder(getFragmentManager())
//                .setView(view)
//                .create();
//        view.findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
//            @Override
//            @NormalOnClick({EventConstants.NormalClick.installError})
//            @ResId({R.id.sure})
//            public void onClick(View v) {
//                builder.dismiss();
//            }
//        });
//        builder.show();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.d(TAG, "onHiddenChanged:" + hidden);
        if (!hidden) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        EventBus.getDefault().unregister(this);
    }

    public String getTAG() {
        return TAG;
    }

    /**
     * 网络变化回调
     *
     * @param event
     */
    @Subscriber(tag = AppStoreConstants.MSG_NETWORK_CHANGED)
    public void networkChangedReceiver(NetworkChangedEvent event) {
        KLog.d("zs", "AppListFragment isConnect:" + event.isConnect());
        if (!event.isConnect()) {
            return;
        }
        List<DownloadTask> errorTasks = event.getDownloadTaskList();
        if (errorTasks == null) {
            return;
        }
        for (DownloadTask task : errorTasks) {
            task.start();
            mAdapter.notifyDataSetChanged();
        }
    }

    public void onTabClick() {
        if (mAppListVM != null) {
            mAppListVM.fetchAppInfoList();
        }
    }
}