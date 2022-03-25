package com.xiaoma.app.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.app.R;
import com.xiaoma.app.adapter.AppListAdapter;
import com.xiaoma.app.common.constant.AppStoreConstants;
import com.xiaoma.app.common.constant.EventConstants;
import com.xiaoma.app.model.AppInfo;
import com.xiaoma.app.model.AppStateEvent;
import com.xiaoma.app.model.CancelItemEvent;
import com.xiaoma.app.model.DownLoadAppInfo;
import com.xiaoma.app.model.NetworkChangedEvent;
import com.xiaoma.app.ui.activity.AppDetailsActivity;
import com.xiaoma.app.ui.activity.DownloadListActivity;
import com.xiaoma.app.util.ApkUtils;
import com.xiaoma.app.vm.AppListVM;
import com.xiaoma.autotracker.XmAutoTracker;
import com.xiaoma.component.base.BaseFragment;
import com.xiaoma.guide.utils.GuideConstants;
import com.xiaoma.guide.utils.GuideDataHelper;
import com.xiaoma.guide.utils.NewGuide;
import com.xiaoma.model.XmResource;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.network.engine.OkGo;
import com.xiaoma.network.model.Progress;
import com.xiaoma.network.okserver.OkDownload;
import com.xiaoma.network.okserver.download.DownloadTask;
import com.xiaoma.ui.adapter.XMBaseAbstractRyAdapter;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.dialog.XmDialog;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Thomas on 2018/10/12 0012
 * 应用管理
 */
@PageDescComponent(EventConstants.PageDescribe.appManagerFragmentPagePathDesc)
public class AppManagerFragment extends BaseFragment implements View.OnClickListener {
    private final String TAG = getClass().getSimpleName();
    private RecyclerView mRvApps;
    private XmScrollBar xmScrollBar;
    private ImageView ivDownloadList;
    private AppListVM mAppListVM;
    private AppListAdapter appListAdapter;
    private Button updateAllBtn;
    private TextView installTv;
    //    private TextView waitUpdateTv;
    //全部更新size
    private long totalSize;
    //待更新数目监听
    private WaitUpdateCountListener waitUpdateCountListener;
    //最新版本APP list
    private List<DownLoadAppInfo> stateNewAppInfoList;
    //待更新版本APP list
    private List<DownLoadAppInfo> stateOldAppInfoList;
    //待更新版本APP Map
    private Map<String, DownLoadAppInfo> mWaitUpdateMap;

    private List<DownLoadAppInfo> mDownloadList;
    //需要升级的App list
    private List<DownLoadAppInfo> mNeedUpdateList;
    private NewGuide newGuide;
    private Handler handler;
    private Runnable runnable;

    public static AppManagerFragment newInstance() {
        return new AppManagerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fmt_app_manager, container, false);

        return super.onCreateWrapView(view);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bindView(view);
        initView();
        initData();
    }

    @Override
    protected void noNetworkOnRetry() {
        super.noNetworkOnRetry();
        mAppListVM.fetchAppInfoList();
    }

    /**
     * 初始化控件
     *
     * @param view
     */
    private void bindView(View view) {
//        waitUpdateTv = view.findViewById(R.id.tv_wait_update_count);
        mRvApps = view.findViewById(R.id.rv_app_manager);
        ivDownloadList = view.findViewById(R.id.download_list);
        xmScrollBar = view.findViewById(R.id.scroll_bar);
        updateAllBtn = view.findViewById(R.id.btn_all_update);
        installTv = view.findViewById(R.id.text_install);
        ivDownloadList.setOnClickListener(this);
        updateAllBtn.setOnClickListener(this);

        installTv.setVisibility(View.GONE);
        ivDownloadList.setVisibility(View.GONE);
        updateAllBtn.setVisibility(View.GONE);
        EventBus.getDefault().register(this);
    }

    /**
     * 初始化RecyclerView
     */
    private void initView() {
        mRvApps.setItemAnimator(null);
        //LinearLayoutManager中定制了可扩展的布局排列接口，子类按照接口中的规范来实现就可以定制出不同排雷方式的布局了
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        //配置布局，默认为vertical（垂直布局），下边这句将布局改为水平布局
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvApps.setLayoutManager(layoutManager);
        appListAdapter = new AppListAdapter(mContext, new ArrayList<DownLoadAppInfo>(), R.layout.item_app_list);
        appListAdapter.setOnItemClickListener(new XMBaseAbstractRyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.Adapter adapter, View view, RecyclerView.ViewHolder holder, int position) {
                AppDetailsActivity.startActivityInApp(mContext, appListAdapter.getDatas().get(position), AppStoreConstants.APP_MANAGER);
                dismissGuideWindow();
            }
        });
        mRvApps.setAdapter(appListAdapter);
        xmScrollBar.setRecyclerView(mRvApps);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        stateNewAppInfoList = new ArrayList<>();
        stateOldAppInfoList = new ArrayList<>();
        mWaitUpdateMap = new HashMap<>();
        mNeedUpdateList = new ArrayList<>();

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
                            installTv.setVisibility(View.VISIBLE);
                            ivDownloadList.setVisibility(View.VISIBLE);
                            updateAllBtn.setVisibility(View.VISIBLE);

                            mDownloadList = data;
                            appListAdapter.setDatas(sortAppInfoByState(mDownloadList));
                            showContentView();
                            showGuideWindow();
                        } else {
                            mStateView.setEmptyView(R.layout.view_no_app);
                            showEmptyView();
                        }
                    }

                    @Override
                    public void onError(int code, String message) {
                        super.onError(code, message);
                        installTv.setVisibility(View.GONE);
                        ivDownloadList.setVisibility(View.GONE);
                        updateAllBtn.setVisibility(View.GONE);

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
     * 获取本地已经安装的应用，并把需要更新的app置前排序
     *
     * @param model
     * @return
     */
    private List<DownLoadAppInfo> sortAppInfoByState(List<DownLoadAppInfo> model) {
        List<DownLoadAppInfo> installAppList = new ArrayList<>();
        stateNewAppInfoList.clear();
        stateOldAppInfoList.clear();
        mWaitUpdateMap.clear();
        mNeedUpdateList.clear();
        totalSize = 0;

        for (int i = 0; i < model.size(); i++) {
            DownLoadAppInfo loadAppInfo = model.get(i);
            //最新版本
            if (loadAppInfo.getInstallState() == AppStoreConstants.INSTALL_STATE_NEW) {
                loadAppInfo.setInstallTime(ApkUtils.getPackageInfo(loadAppInfo.getAppInfo().getPackageName()).lastUpdateTime);
                stateNewAppInfoList.add(loadAppInfo);

                //待更新版本
            } else if (loadAppInfo.getInstallState() == AppStoreConstants.INSTALL_STATE_OLD) {
                stateOldAppInfoList.add(loadAppInfo);
                AppInfo info = loadAppInfo.getAppInfo();
                mWaitUpdateMap.put(info.getPackageName(), loadAppInfo);
            }
        }
        //按照安装时间排序
        Collections.sort(stateNewAppInfoList, new Comparator<DownLoadAppInfo>() {
            @Override
            public int compare(DownLoadAppInfo d1, DownLoadAppInfo d2) {
                return (int) (d2.getInstallTime() - d1.getInstallTime());
            }
        });
        installAppList.addAll(stateOldAppInfoList);
        installAppList.addAll(stateNewAppInfoList);

        //将无下载任务的App添加到升级list
        for (DownLoadAppInfo updateAppInfo : stateOldAppInfoList) {
            if (!OkDownload.getInstance().hasTask(updateAppInfo.getAppInfo().getPackageName())) {
                mNeedUpdateList.add(updateAppInfo);
                totalSize = totalSize + updateAppInfo.getAppInfo().getSize();
            }
        }

        //设置全部更新是否点击
        setUpdateAllEnabled(mWaitUpdateMap);

        //设置待更新数
        setWaitUpdateCount(mWaitUpdateMap);

        return installAppList;
    }

    /**
     * 设置全部更新点击
     *
     * @param map
     */
    private void setUpdateAllEnabled(Map<String, DownLoadAppInfo> map) {
        //1.map中没有待更新应用
        if (map == null || map.size() == 0) {
            updateAllBtn.setEnabled(false);
            updateAllBtn.setTextColor(getResources().getColor(R.color.color_gray));
            return;
        }

        for (String key : map.keySet()) {
            //2.map中没有更新的任务
            if (!OkDownload.getInstance().hasTask(key)) {
                updateAllBtn.setEnabled(true);
                updateAllBtn.setTextColor(getResources().getColor(R.color.white));
                break;

            } else {
                //3.map中所有任务更新任务都存在
                updateAllBtn.setEnabled(false);
                updateAllBtn.setTextColor(getResources().getColor(R.color.color_gray));
            }
        }
    }

    /**
     * 设置待更新数
     *
     * @param mWaitUpdateMap
     */
    private void setWaitUpdateCount(Map<String, DownLoadAppInfo> mWaitUpdateMap) {
//        if (mWaitUpdateMap.size() > 0) {
//            waitUpdateTv.setVisibility(View.VISIBLE);
//            waitUpdateTv.setText(mWaitUpdateMap.size() + "");
//
//        } else {
//            waitUpdateTv.setVisibility(View.GONE);
//        }

        if (waitUpdateCountListener != null) {
            waitUpdateCountListener.setWaitUpdateCount(mWaitUpdateMap.size());
        }
    }

    @Override
    @NormalOnClick({EventConstants.NormalClick.downloadList, EventConstants.NormalClick.allAppUpdate})
    @ResId({R.id.download_list, R.id.btn_all_update})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.download_list:
                startActivity(new Intent(getContext(), DownloadListActivity.class));
                break;

            case R.id.btn_all_update:
                showUpdateAllDialog(ApkUtils.byteToM(totalSize));

                XmAutoTracker.getInstance().onEvent(EventConstants.NormalClick.allAppUpdate, ApkUtils.byteToM(totalSize), TAG, EventConstants.PageDescribe.appManagerFragmentPagePathDesc);
                break;
        }
    }

    /**
     * 全部升级弹框
     */
    private void showUpdateAllDialog(String size) {
        final ConfirmDialog updateAllDialog = new ConfirmDialog(getActivity());
        updateAllDialog.setContent(String.format(getString(R.string.tips_content), size))
                .setPositiveButton(getString(R.string.sure), new View.OnClickListener() {
                    @Override
                    @NormalOnClick(EventConstants.NormalClick.updateAllSure)
                    @ResId(R.id.tv_sure)
                    public void onClick(View v) {
                        updateAll();
                        updateAllDialog.dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                    @Override
                    @NormalOnClick(EventConstants.NormalClick.updateAllCancel)
                    @ResId(R.id.tv_cancel)
                    public void onClick(View v) {
                        updateAllDialog.dismiss();
                    }
                });
        updateAllDialog.show();
    }

    /**
     * 更新所有
     */
    private void updateAll() {
        if (mNeedUpdateList == null) {
            return;
        }
        if (!NetworkUtils.isConnected(mContext)) {
            XMToast.toastException(mContext, mContext.getString(R.string.net_work_error), false);
            return;
        }
        //全部更新不可点击
        setUpdateAllEnabled(null);

        for (int i = 0; i < mNeedUpdateList.size(); i++) {
            AppInfo appInfo = mNeedUpdateList.get(i).getAppInfo();
            OkDownload.request(appInfo.getPackageName(), OkGo.<File>get(appInfo.getUrl()))
                    .extra1(appInfo)
                    .extra2(true)//表示是更新任务
                    .save()
                    .start();
            appListAdapter.notifyDataSetChanged();
        }
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
                    appListAdapter.notifyDataSetChanged();
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
                if (mWaitUpdateMap.get(packageName) != null) {
                    mWaitUpdateMap.remove(packageName);
                    setWaitUpdateCount(mWaitUpdateMap);
                }
                break;

            //卸载
            case AppStateEvent.STATE_PACKAGE_REMOVED:
                appState = AppStoreConstants.INSTALL_STATE_NOTHING;
                break;
        }

        for (DownLoadAppInfo info : mDownloadList) {
            if (info.getAppInfo().getPackageName().equals(packageName)) {
                // 针对【system ui/人脸识别/ 双屏互动/语音助手/车应用】应用屏蔽
                if (AppStoreConstants.SYSTEM_UI.equals(packageName) ||
                        AppStoreConstants.FACERECOGNIZE.equals(packageName) ||
                        AppStoreConstants.DUAL_SCREEN.equals(packageName) ||
                        AppStoreConstants.ASSISTANT.equals(packageName) ||
                        AppStoreConstants.APPSTORE.equals(packageName)) {
                    mDownloadList.remove(info);
                }
                info.setInstallState(appState);
                appListAdapter.setDatas(sortAppInfoByState(mDownloadList));
                break;
            }
        }
    }

    /**
     * 安装异常弹框
     */
    private void showInstallErrorView() {
        View view = View.inflate(getContext(), R.layout.dialog_install_error, null);
        final XmDialog builder = new XmDialog.Builder(getFragmentManager())
                .setView(view)
                .create();
        view.findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick({EventConstants.NormalClick.installError})
            @ResId({R.id.sure})
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        builder.show();
    }

    /**
     * 下载任务取消
     *
     * @param cancelItemEvent
     */
    @Subscriber(tag = AppStoreConstants.MSG_DOWNLOAD_LIST_ITEM_CANCEL)
    public void downloadItemCancel(CancelItemEvent cancelItemEvent) {
        Log.d(TAG, "AppStoreConstants.MSG_DOWNLOAD_LIST_ITEM_CANCEL：downloadItemCancel:" + cancelItemEvent);
        appListAdapter.notifyDataSetChanged();
        //如果是更新任务取消，重置全部更新的点击
        DownLoadAppInfo cancelAppInfo = mWaitUpdateMap.get(cancelItemEvent.tag);
        if (cancelAppInfo != null) {
            //add需要更新app
            mNeedUpdateList.add(cancelAppInfo);
            getTotalSize(mNeedUpdateList);
            setUpdateAllEnabled(mWaitUpdateMap);
        }
    }

    /**
     * 下载任务数量
     * 用于刷新下载提示红点
     *
     * @param downloadTaskSize
     */
    @Subscriber(tag = AppStoreConstants.MSG_DOWNLOAD_LIST_SIZE)
    public void downloadTaskSize(int downloadTaskSize) {
        Log.d(TAG, "AppStoreConstants.MSG_DOWNLOAD_LIST_SIZE: downloadTaskSize:" + downloadTaskSize);
        setDownloadSize(downloadTaskSize);
    }

    /**
     * 设置下载任务数
     *
     * @param count
     */
    private void setDownloadSize(int count) {
        if (count > 0) {
            ivDownloadList.setImageResource(R.drawable.icon_download_update);

        } else {
            ivDownloadList.setImageResource(R.drawable.icon_download_normal);
        }
    }

    /**
     * 任务开始下载
     *
     * @param tag
     */
    @Subscriber(tag = AppStoreConstants.MSG_DOWNLOAD_TASK_START)
    public void downloadTaskStart(String tag) {
        Log.d(TAG, "AppStoreConstants.MSG_DOWNLOAD_TASK_START: downloadTaskStart" + tag);
        //如果是更新任务开始下载
        DownLoadAppInfo appInfo = mWaitUpdateMap.get(tag);
        if (appInfo != null) {
            //remove需要更新app
            mNeedUpdateList.remove(appInfo);
            getTotalSize(mNeedUpdateList);
            setUpdateAllEnabled(mWaitUpdateMap);
        }
    }

    /**
     * 获取全部更新需要更新的size
     *
     * @param needUpdateList
     * @return
     */
    private long getTotalSize(List<DownLoadAppInfo> needUpdateList) {
        totalSize = 0;
        for (int i = 0; i < needUpdateList.size(); i++) {
            totalSize = totalSize + needUpdateList.get(i).getAppInfo().getSize();
        }

        return totalSize;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        appListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.d(TAG, "onHiddenChanged:" + hidden);
        if (!hidden) {
            appListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        EventBus.getDefault().unregister(this);
    }

    /**
     * 待更新count Listener
     */
    public interface WaitUpdateCountListener {
        void setWaitUpdateCount(int count);
    }

    public void setWaitUpdateCountListener(WaitUpdateCountListener listener) {
        this.waitUpdateCountListener = listener;
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
        KLog.d("zs", "AppManagerFragment isConnect:" + event.isConnect());
        if (!event.isConnect()) {
            return;
        }
        List<DownloadTask> errorTasks = event.getDownloadTaskList();
        if (errorTasks == null) {
            return;
        }
        for (DownloadTask task : errorTasks) {
            task.start();
            appListAdapter.notifyDataSetChanged();
        }
    }

    public void onTabClick() {
        if (mAppListVM != null) {
            mAppListVM.fetchAppInfoList();
        }
    }

    private void showGuideWindow() {
        if (!GuideDataHelper.shouldShowGuide(getContext(), GuideConstants.APPSTORE_SHOWED, GuideConstants.APPSTORE_GUIDE_FIRST, false))
            return;
        initHandlerAndRunnable();
        mRvApps.postDelayed(new Runnable() {
            @Override
            public void run() {
                View view = mRvApps.getLayoutManager().findViewByPosition(0);
                if (view == null) {
                    handler.postDelayed(runnable, 100);
                    return;
                }
                View targetView = view.findViewById(R.id.iv_app_icon);
                showGuide(targetView);
            }
        }, 500);
    }

    private void initHandlerAndRunnable() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                View view = mRvApps.getLayoutManager().findViewByPosition(0);
                if (view == null) {
                    handler.postDelayed(runnable, 100);
                    return;
                }
                showGuide(view.findViewById(R.id.iv_app_icon));
            }
        };
    }

    private void showGuide(View targetView) {
        newGuide = NewGuide.with(getActivity())
                .setLebal(GuideConstants.APPSTORE_SHOWED)
                .setNeedHande(true)
                .setNeedShake(true)
                .setGuideLayoutId(R.layout.guide_view_app_manager)
                .setTargetView(targetView)
                .setTargetRect(NewGuide.getViewRect(targetView))
                .setViewWaveIdOne(R.id.iv_wave_one)
                .setViewWaveIdTwo(R.id.iv_wave_two)
                .setViewWaveIdThree(R.id.iv_wave_three)
                .setViewHandId(R.id.iv_gesture)
                .setViewSkipId(R.id.tv_guide_skip)
                .build();
        newGuide.showGuide();
    }

    public void dismissGuideWindow() {
        if (newGuide != null) {
            newGuide.dismissGuideWindow();
            newGuide = null;
        }
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
            handler = null;
            runnable = null;
        }
    }
}
