package com.xiaoma.app.ui.activity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoma.app.R;
import com.xiaoma.app.adapter.DownloadAdapter;
import com.xiaoma.app.common.constant.AppStoreConstants;
import com.xiaoma.app.common.constant.EventConstants;
import com.xiaoma.app.model.AppStateEvent;
import com.xiaoma.app.model.CancelItemEvent;
import com.xiaoma.app.model.NetworkChangedEvent;
import com.xiaoma.component.base.BaseActivity;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.PageDescComponent;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.network.db.DownloadManager;
import com.xiaoma.network.model.Progress;
import com.xiaoma.network.okserver.OkDownload;
import com.xiaoma.network.okserver.download.DownloadTask;
import com.xiaoma.ui.dialog.ConfirmDialog;
import com.xiaoma.ui.view.XmScrollBar;
import com.xiaoma.utils.log.KLog;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;

/**
 * 下载列表弹框
 * Created by zhushi.
 * Date: 2018/10/16
 */
@PageDescComponent(EventConstants.PageDescribe.downloadListActivityPagePathDesc)
public class DownloadListActivity extends BaseActivity {
    private final String TAG = getClass().getSimpleName();
    private RecyclerView rvDownload;
    private XmScrollBar mScrollBar;
    private DownloadAdapter adapter;
    private List<DownloadTask> mData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_list);
        getNaviBar().showBackNavi();
        statusBarDividerGone();
        rvDownload = findViewById(R.id.rc_download);
        mScrollBar = findViewById(R.id.xmScrollBar);

        getWindow().setLayout(1305, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().setGravity(Gravity.START);
        mBaseStateView.setEmptyView(R.layout.download_empty_view);
        adapter = new DownloadAdapter(this, mData, R.layout.item_download_manager);
        rvDownload.setLayoutManager(new LinearLayoutManager(this));
        rvDownload.setAdapter(adapter);
        mScrollBar.setRecyclerView(rvDownload);
        MyTask task = new MyTask();
        task.execute();
        EventBus.getDefault().register(this);
    }

    @SuppressLint("StaticFieldLeak")
    private class MyTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            mData = OkDownload.restore(DownloadManager.getInstance().getAll());
            adapter.setData(mData);

            return mData.size();
        }

        @Override
        protected void onPostExecute(Integer result) {
            adapter.notifyDataSetChanged();
            if (result == 0) {
                showEmptyView();
            }
        }
    }

    /**
     * 下载任务取消回调
     *
     * @param cancelItemEvent
     */
    @Subscriber(tag = AppStoreConstants.MSG_DOWNLOAD_LIST_ITEM_CANCEL)
    public void downloadItemCancel(CancelItemEvent cancelItemEvent) {
        Log.d(TAG, "AppStoreConstants.MSG_DOWNLOAD_LIST_ITEM_CANCEL：downloadItemCancel:" + cancelItemEvent);
        if (cancelItemEvent.downloadSize == 0) {
            showEmptyView();
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
        switch (appStateEvent.getAppState()) {
            //安装
            //替换
            case AppStateEvent.STATE_PACKAGE_REPLACED:
            case AppStateEvent.STATE_PACKAGE_ADDED:
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
                    adapter.notifyDataSetChanged();
                    //3.弹框
                    showInstallErrorView();

                    return;
                }
                //安装成功
                DownloadTask task = OkDownload.getInstance().getTask(packageName);
                if (task != null) {
                    task.remove(true);
                }
                int listSize = adapter.updateData(DownloadAdapter.TYPE_ALL);
                if (listSize == 0) {
                    mBaseStateView.setEmptyView(R.layout.download_empty_view);
                    showEmptyView();
                }
                break;

            //卸载
            case AppStateEvent.STATE_PACKAGE_REMOVED:
                break;

            default:
                break;
        }
    }

    /**
     * 安装异常弹框
     */
    private void showInstallErrorView() {
        final ConfirmDialog dialog = new ConfirmDialog(this);
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
    }

    @Override
    public void finish() {
        super.finish();
        adapter.unRegister();
        //注释掉activity本身的过渡动画
        overridePendingTransition(0, 0);
    }

    /**
     * 网络变化回调
     *
     * @param event
     */
    @Subscriber(tag = AppStoreConstants.MSG_NETWORK_CHANGED)
    public void networkChangedReceiver(NetworkChangedEvent event) {
        KLog.d(TAG, "DownloadListActivity isConnect:" + event.isConnect());
        if (!event.isConnect()) {
            return;
        }
        List<DownloadTask> errorTasks = event.getDownloadTaskList();
        if (errorTasks == null) {
            return;
        }
        for (DownloadTask task : errorTasks) {
            task.start();
            adapter.notifyDataSetChanged();
        }
    }
}
