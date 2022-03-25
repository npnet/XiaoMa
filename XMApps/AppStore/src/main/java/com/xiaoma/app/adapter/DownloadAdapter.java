package com.xiaoma.app.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xiaoma.app.R;
import com.xiaoma.app.SilentInstallManager;
import com.xiaoma.app.common.constant.AppStoreConstants;
import com.xiaoma.app.listener.AppDownloadListener;
import com.xiaoma.app.model.AppInfo;
import com.xiaoma.app.model.BatchInfo;
import com.xiaoma.app.model.CancelItemEvent;
import com.xiaoma.app.util.ApkUtils;
import com.xiaoma.app.util.AppNotificationHelper;
import com.xiaoma.app.util.Transformations;
import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.network.db.DownloadManager;
import com.xiaoma.network.model.Progress;
import com.xiaoma.network.okserver.OkDownload;
import com.xiaoma.network.okserver.download.DownloadTask;
import com.xiaoma.ui.adapter.XMBaseAbstractRyAdapter;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.vh.XMViewHolder;
import com.xiaoma.utils.NetworkUtils;

import org.simple.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;


/**
 * 下载列表页面adapter
 * Created by zhushi.
 * Date: 2018/10/16
 */
public class DownloadAdapter extends XMBaseAbstractRyAdapter<DownloadTask> {
    public static final String TAG = "DownloadAdapter";
    public static final int TYPE_ALL = 0;
    public static final int TYPE_FINISH = 1;
    public static final int TYPE_ING = 2;
    private static final int MAX_VALUE = 10000;

    private int type;

    public DownloadAdapter(Context context, List<DownloadTask> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    public int updateData(int type) {
        //这里是将数据库的数据恢复
        this.type = type;
        switch (type) {
            case TYPE_ALL:
                mDatas = OkDownload.restore(DownloadManager.getInstance().getAll());
                break;

            case TYPE_FINISH:
                mDatas = OkDownload.restore(DownloadManager.getInstance().getFinished());
                break;

            case TYPE_ING:
                mDatas = OkDownload.restore(DownloadManager.getInstance().getDownloading());
                break;
            default:
                break;
        }
        notifyDataSetChanged();
        return mDatas.size();
    }

    @Override
    protected void convert(XMViewHolder holder, final DownloadTask downloadTask, int position) {
        Progress progress = downloadTask.progress;
        final AppInfo apk = (AppInfo) progress.extra1;
        if (apk != null) {
            ImageLoader.with(mContext)
                    .load(apk.getIconPathUrl())
                    .placeholder(R.drawable.fm_default_cover)
                    .transform(Transformations.getRoundCorners())
                    .into((ImageView) holder.getView(R.id.icon));
            holder.setText(R.id.name, apk.getAppName());
            holder.setText(R.id.app_version, String.format(mContext.getString(R.string.app_version), apk.getVersionName()));
        }

        final ProgressBar appProgress = holder.getView(R.id.download_progress_bar);
        final TextView tvStart = holder.getView(R.id.start);
        final TextView tvSpeed = holder.getView(R.id.tv_speed);
        final String tag = createTag(downloadTask);

        holder.setOnClickListener(R.id.start, new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                BatchInfo batchInfo = new BatchInfo(String.valueOf(apk.getId()), apk.getAppName(), String.valueOf(apk.getVersionCode()), apk.getVersionName());
                return new ItemEvent(((TextView) view).getText() + "", batchInfo.toJson());
            }

            @Override
            @BusinessOnClick
            public void onClick(View v) {
                clickStartEvent(downloadTask, tvStart, tvSpeed, appProgress, apk, tag);
            }
        });

        holder.setOnClickListener(R.id.remove, new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                BatchInfo batchInfo = new BatchInfo(String.valueOf(apk.getId()), apk.getAppName(), String.valueOf(apk.getVersionCode()), apk.getVersionName());
                return new ItemEvent(((TextView) view).getText() + "", batchInfo.toJson());
            }

            @Override
            @BusinessOnClick
            public void onClick(View v) {
                downloadTask.remove(true);
                int size = updateData(type);
                //下载任务取消通知
                EventBus.getDefault().post(new CancelItemEvent(size, downloadTask.progress.tag), AppStoreConstants.MSG_DOWNLOAD_LIST_ITEM_CANCEL);
            }
        });
        downloadTask.register(new ListDownloadListener(tag, tvStart, tvSpeed, appProgress, apk));
        appProgress.setTag(tag);
        refresh(downloadTask.progress, tvStart, tvSpeed, appProgress, apk, tag);
    }

    private void clickStartEvent(DownloadTask downloadTask, TextView tvStart, TextView tvSpeed, ProgressBar appProgress, AppInfo appInfo, String tag) {
        if (!NetworkUtils.isConnected(mContext)) {
            XMToast.toastException(mContext, mContext.getString(R.string.net_work_error), false);
            return;
        }

        Progress progress = downloadTask.progress;
        switch (progress.status) {
            case Progress.PAUSE:
            case Progress.NONE:
            case Progress.ERROR:
                downloadTask.start();
                break;

            case Progress.LOADING:
                downloadTask.pause();
                break;

            case Progress.INSTALL_FAILED:
                if (SilentInstallManager.getInstance().isBindService()) {
                    reSilentInstall(downloadTask, progress, tvStart, tvSpeed, appProgress, appInfo, tag);

                } else {
                    ApkUtils.installApkFile(mContext, progress.filePath);
                }
                break;

            default:
                break;
        }
    }

    public void setData(List<DownloadTask> datas) {
        mDatas = datas;
    }

    private String createTag(DownloadTask task) {
        return type + "_" + task.progress.tag;
    }

    public void unRegister() {
        Map<String, DownloadTask> taskMap = OkDownload.getInstance().getTaskMap();
        for (DownloadTask task : taskMap.values()) {
            task.unRegister(createTag(task));
        }
    }

    public void refresh(Progress progress, TextView tvDownload, TextView tvSpeed, ProgressBar appProgress, AppInfo appInfo, String tag) {
        if (!tag.equals(appProgress.getTag())) {
            return;
        }
        switch (progress.status) {
            case Progress.NONE:
                tvSpeed.setVisibility(View.INVISIBLE);
                tvDownload.setEnabled(true);
                tvDownload.setTextColor(mContext.getResources().getColor(R.color.white));
                tvDownload.setText(mContext.getString(R.string.download_status_none));
                break;

            case Progress.PAUSE:
            case Progress.ERROR:
                tvSpeed.setVisibility(View.VISIBLE);
                tvSpeed.setText((mContext.getResources().getString(R.string.download_speed, "0")));
                tvDownload.setEnabled(true);
                tvDownload.setTextColor(mContext.getResources().getColor(R.color.download_pause_text_color));
                if (progress.extra2 != null && (boolean) progress.extra2) {
                    tvDownload.setText(mContext.getString(R.string.update_continue));

                } else {
                    tvDownload.setText(mContext.getString(R.string.download_continue));
                }
                break;

            case Progress.WAITING:
                tvSpeed.setVisibility(View.VISIBLE);
                tvSpeed.setText((mContext.getResources().getString(R.string.download_speed, "0")));
                tvDownload.setEnabled(false);
                tvDownload.setTextColor(mContext.getResources().getColor(R.color.download_un_click_color));
                tvDownload.setText(mContext.getString(R.string.download_status_waiting));
                break;

            case Progress.LOADING:
                tvSpeed.setVisibility(View.VISIBLE);
                tvDownload.setEnabled(true);
                tvDownload.setTextColor(mContext.getResources().getColor(R.color.white));
                tvDownload.setText(mContext.getString(R.string.download_status_pause));
                String pSpeed = Formatter.formatFileSize(mContext, progress.speed);
                tvSpeed.setText((mContext.getResources().getString(R.string.download_speed, pSpeed)));
                break;

            //正在安装
            case Progress.FINISH:
            case Progress.INSTALLING:
                tvSpeed.setVisibility(View.INVISIBLE);
                tvDownload.setEnabled(false);
                tvDownload.setTextColor(mContext.getResources().getColor(R.color.download_un_click_color));
                tvDownload.setText(mContext.getString(R.string.installing));
                break;

            //安装失败
            case Progress.INSTALL_FAILED:
                tvSpeed.setVisibility(View.INVISIBLE);
                tvDownload.setEnabled(true);
                tvDownload.setTextColor(mContext.getResources().getColor(R.color.download_pause_text_color));
                tvDownload.setText(mContext.getString(R.string.download_status_finish));
                break;
            default:
                break;
        }
        appProgress.setMax(MAX_VALUE);
        appProgress.setProgress((int) (progress.fraction * MAX_VALUE));
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        return new ItemEvent(getDatas().get(position).progress.fileName, getDatas().get(position).progress.fileName);
    }

    private class ListDownloadListener extends AppDownloadListener {
        private TextView mTvDownload;
        private TextView mTvSpeed;
        private ProgressBar mProgressBar;
        private AppInfo mAppInfo;
        private String tag;

        ListDownloadListener(String tag, TextView tvDownload, TextView tvSpeed, ProgressBar progressBar, AppInfo appInfo) {
            super(tag);
            this.tag = tag;
            mTvDownload = tvDownload;
            mTvSpeed = tvSpeed;
            mProgressBar = progressBar;
            mAppInfo = appInfo;
        }

        @Override
        public void onStart(Progress progress) {

        }

        @Override
        public void onProgress(Progress progress) {
            if (tag.equals(mProgressBar.getTag())) {
                refresh(progress, mTvDownload, mTvSpeed, mProgressBar, mAppInfo, tag);
            }
        }

        @Override
        public void onError(Progress progress) {
            super.onError(progress);
            if (progress.exception instanceof FileNotFoundException) {
                XMToast.toastException(mContext, mContext.getString(R.string.storage_error), false);

            } else {
                XMToast.toastException(mContext, mContext.getString(R.string.net_work_error), false);
            }
        }

        @Override
        public void onFinish(File file, final Progress progress) {
            String lable;
            if ((boolean) progress.extra2) {
                //已更新成功
                lable = String.format(mContext.getString(R.string.download_update_complete), mAppInfo.getAppName());

            } else {
                //已下载成功
                lable = String.format(mContext.getString(R.string.download_complete), mAppInfo.getAppName());
            }

            AppNotificationHelper.getInstance().handleAppNotification(mContext, AppNotificationHelper.APP_DOWNLOAD_COMPLETE,
                    mAppInfo.getIconPathUrl(), mAppInfo.getAppName(), lable, mAppInfo.getPackageName(), System.currentTimeMillis());
            if (SilentInstallManager.getInstance().isBindService()) {
                ApkUtils.silentInstall(progress.filePath);
            } else {
                ApkUtils.installApkFile(mContext, progress.filePath);
            }
        }
    }

    /**
     * 重新安装
     *
     * @param task
     * @param progress
     */
    private void reSilentInstall(DownloadTask task, Progress progress, TextView tvStart, TextView tvSpeed, ProgressBar appProgress, AppInfo appInfo, String tag) {
        //1.标记任务安装中
        progress.status = Progress.INSTALLING;
        task.updateDatabase(progress);
        //2.刷新视图
        refresh(progress, tvStart, tvSpeed, appProgress, appInfo, tag);
        //3.安装
        ApkUtils.silentInstall(progress.filePath);
    }
}
