package com.xiaoma.app.adapter;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xiaoma.app.R;
import com.xiaoma.app.SilentInstallManager;
import com.xiaoma.app.common.constant.AppStoreConstants;
import com.xiaoma.app.listener.AppDownloadListener;
import com.xiaoma.app.model.AppInfo;
import com.xiaoma.app.model.BatchInfo;
import com.xiaoma.app.model.DownLoadAppInfo;
import com.xiaoma.app.util.ApkUtils;
import com.xiaoma.app.util.AppNotificationHelper;
import com.xiaoma.app.util.Transformations;
import com.xiaoma.app.views.DownloadProgressButton;
import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.login.common.LoginConstants;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.User;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.network.db.DownloadManager;
import com.xiaoma.network.engine.OkGo;
import com.xiaoma.network.model.Progress;
import com.xiaoma.network.okserver.OkDownload;
import com.xiaoma.network.okserver.download.DownloadTask;
import com.xiaoma.ui.adapter.XMBaseAbstractRyAdapter;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.ui.vh.XMViewHolder;
import com.xiaoma.utils.KeyEventUtils;
import com.xiaoma.utils.LaunchUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.log.KLog;
import com.xiaoma.utils.logintype.callback.OnBlockCallback;
import com.xiaoma.utils.logintype.manager.LoginType;
import com.xiaoma.utils.logintype.manager.LoginTypeManager;

import org.simple.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by Thomas on 2018/10/12 0012
 */

public class AppListAdapter extends XMBaseAbstractRyAdapter<DownLoadAppInfo> {

    public AppListAdapter(Context context, List<DownLoadAppInfo> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    protected void convert(XMViewHolder holder, final DownLoadAppInfo downLoadAppInfo, final int position) {

        LinearLayout.LayoutParams firstLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        firstLayoutParams.setMargins(80, 0, 58, 0);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 58, 0);

        //列表第一项
        if (position == 0) {
            holder.itemView.setLayoutParams(firstLayoutParams);

        } else {
            holder.itemView.setLayoutParams(layoutParams);
        }

        final AppInfo info = downLoadAppInfo.getAppInfo();
        holder.setText(R.id.tv_app_name, info.getAppName());
        holder.setText(R.id.tv_version, info.getVersionName());
        holder.setText(R.id.tv_app_introduce, info.getVersionDesc());
        final DownloadProgressButton progressButton = holder.getView(R.id.progressBtn);
        progressButton.setEnabled(true);
        progressButton.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                AppInfo mAppInfo = downLoadAppInfo.getAppInfo();
                BatchInfo batchInfo = new BatchInfo(String.valueOf(mAppInfo.getId()), mAppInfo.getAppName(), String.valueOf(mAppInfo.getVersionCode()), mAppInfo.getVersionName());
                return new ItemEvent(((DownloadProgressButton) view).getmCurrentText(), batchInfo.toJson());
            }

            @Override
            @BusinessOnClick
            public void onClick(View v) {
                progressButton.setTag(info.getPackageName());
                refreshDownloadView(info.getPackageName(), downLoadAppInfo, progressButton);
            }
        });

        ImageLoader.with(mContext)
                .load(info.getIconPathUrl())
                .placeholder(R.drawable.fm_default_cover)
                .transform(Transformations.getRoundCorners())
                .into((ImageView) holder.getView(R.id.iv_app_icon));

        String tag = info.getPackageName();
        OkDownload instance = OkDownload.getInstance();
        progressButton.setTag(tag);
        if (instance.hasTask(tag)) {
            refreshDownload(DownloadManager.getInstance().get(tag), progressButton, tag);
            instance.getTask(tag).register(new ListDownloadListener(tag, downLoadAppInfo, progressButton));

        } else {
            refreshNormal(downLoadAppInfo, progressButton);
        }
        ApkUtils.notifyDownloadTaskCount();

    }

    private void refreshDownloadView(String tag, DownLoadAppInfo downLoadAppInfo, DownloadProgressButton progressButton) {
        AppInfo info = downLoadAppInfo.getAppInfo();
        boolean isUpdate = false;
        switch (downLoadAppInfo.getInstallState()) {
            //打开
            case AppStoreConstants.INSTALL_STATE_NEW:
                // 权限限制
                if (!canUse(info.getPackageName())) return;
                if (!KeyEventUtils.isGoHome(mContext, info.getPackageName())) {
                    LaunchUtils.launchApp(mContext, info.getPackageName());
                }
                break;

            //下载、更新
            case AppStoreConstants.INSTALL_STATE_OLD:
                isUpdate = true;

            case AppStoreConstants.INSTALL_STATE_NOTHING:
                if (!NetworkUtils.isConnected(mContext)) {
                    XMToast.toastException(mContext, mContext.getString(R.string.net_work_error), false);
                    return;
                }

                DownloadTask task = OkDownload.request(info.getPackageName(), OkGo.<File>get(info.getUrl()))
                        .extra1(info)
                        .extra2(isUpdate)
                        .save()
                        .register(new ListDownloadListener(tag, downLoadAppInfo, progressButton));

                Progress progress = DownloadManager.getInstance().get(info.getPackageName());
                switch (progress.status) {
                    case Progress.LOADING:
                        task.pause();
                        break;

                    case Progress.PAUSE:
                    case Progress.NONE:
                    case Progress.ERROR:
                        task.start();
                        break;

                    case Progress.INSTALL_FAILED:
                        if (SilentInstallManager.getInstance().isBindService()) {
                            reSilentInstall(task, progress, progressButton, tag);

                        } else {
                            ApkUtils.installApkFile(mContext, progress.filePath);
                        }
                        break;

                    default:
                        break;
                }
                ApkUtils.notifyDownloadTaskCount();
                break;

            default:
                break;
        }
    }

    //权限判断是否能使用
    private boolean canUse(final String packageName) {
        if (TextUtils.isEmpty(packageName)) return true;
        return LoginTypeManager.getInstance().judgeUse(packageName,
                new OnBlockCallback() {
                    @Override
                    public boolean onShowToast(LoginType loginType) {
                        XMToast.showToast(mContext, loginType.getPrompt(mContext));
                        return true;
                    }

                    @Override
                    public boolean onKeyVerification(LoginType loginType) {
                        User user = null;
                        if (LoginManager.getInstance().isUserLogin()) {
                            user = UserManager.getInstance().getUser(LoginManager.getInstance().getLoginUserId());
                        }
                        if (user == null) return true;
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(LoginConstants.KeyBind.BundleKey.USER, user);
                        LoginTypeManager.getInstance().keyVerificationAndStartApp(mContext, bundle, packageName);
                        return true;
                    }
                });
    }


    private void refreshNormal(DownLoadAppInfo downLoadAppInfo, DownloadProgressButton progressButton) {
        switch (downLoadAppInfo.getInstallState()) {
            case AppStoreConstants.INSTALL_STATE_NOTHING:
                progressButton.setState(DownloadProgressButton.TYPE_NORMAL, mContext.getString(R.string.app_download));
                break;

            case AppStoreConstants.INSTALL_STATE_NEW:
                progressButton.setState(DownloadProgressButton.TYPE_NORMAL, mContext.getString(R.string.app_open));
                break;

            case AppStoreConstants.INSTALL_STATE_OLD:
                progressButton.setState(DownloadProgressButton.TYPE_NORMAL, mContext.getString(R.string.app_update_statue));
                break;
            default:
                break;
        }
    }

    private void refreshDownload(Progress progress, DownloadProgressButton progressButton, String tag) {
        if (!tag.equals(progressButton.getTag())) {
            return;
        }
        switch (progress.status) {
            case Progress.NONE:
                progressButton.setState(DownloadProgressButton.TYPE_NORMAL, mContext.getString(R.string.app_download));
                break;

            case Progress.PAUSE:
            case Progress.ERROR:
                if (progress.extra2 != null && (boolean) progress.extra2) {
                    progressButton.setState(DownloadProgressButton.TYPE_PROGRESS_PAUSE, mContext.getString(R.string.update_continue));

                } else {
                    progressButton.setState(DownloadProgressButton.TYPE_PROGRESS_PAUSE, mContext.getString(R.string.download_continue));
                }
                break;

            case Progress.WAITING:
                progressButton.setState(DownloadProgressButton.TYPE_PROGRESS_WAIT, mContext.getString(R.string.download_status_waiting));
                break;

            case Progress.LOADING:
                progressButton.setState(DownloadProgressButton.TYPE_PROGRESS, (int) (progress.fraction * 100) + "%");
                break;

            //安装失败
            case Progress.INSTALL_FAILED:
                progressButton.setState(DownloadProgressButton.TYPE_NORMAL, mContext.getString(R.string.download_status_finish));
                break;

            //正在安装
            case Progress.FINISH:
            case Progress.INSTALLING:
                progressButton.setState(DownloadProgressButton.TYPE_UN_CLICK, mContext.getString(R.string.installing));
                break;
            default:
                break;
        }
        progressButton.setProgress((int) (progress.fraction * 100));
    }

    @Override
    public ItemEvent returnPositionEventMsg(int position) {
        //单个应用点击一次记录一次应用名称
        return new ItemEvent(getDatas().get(position).getAppInfo().getAppName(), "");
    }

    private class ListDownloadListener extends AppDownloadListener {

        private DownLoadAppInfo downLoadAppInfo;
        private DownloadProgressButton progressButton;
        private String tag;

        ListDownloadListener(String tag, DownLoadAppInfo downLoadAppInfo, DownloadProgressButton progressButton) {
            super(tag);
            this.tag = tag;
            this.downLoadAppInfo = downLoadAppInfo;
            this.progressButton = progressButton;
        }

        @Override
        public void onStart(Progress progress) {
            EventBus.getDefault().post(progress.tag, AppStoreConstants.MSG_DOWNLOAD_TASK_START);
        }

        @Override
        public void onProgress(Progress progress) {
            if (tag.equals(progressButton.getTag())) {
                refreshDownload(progress, progressButton, tag);
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
        public void onFinish(File file, Progress progress) {
            KLog.d("installState:tag=" + progress.tag);
            String lable;
            if ((boolean) progress.extra2) {
                //已更新成功
                lable = String.format(mContext.getString(R.string.download_update_complete), downLoadAppInfo.getAppInfo().getAppName());

            } else {
                //已下载成功
                lable = String.format(mContext.getString(R.string.download_complete), downLoadAppInfo.getAppInfo().getAppName());
            }
            AppNotificationHelper.getInstance().handleAppNotification(mContext, AppNotificationHelper.APP_DOWNLOAD_COMPLETE,
                    downLoadAppInfo.getAppInfo().getIconPathUrl(), downLoadAppInfo.getAppInfo().getAppName(), lable,
                    downLoadAppInfo.getAppInfo().getPackageName(), System.currentTimeMillis());

            if (SilentInstallManager.getInstance().isBindService()) {
                ApkUtils.silentInstall(progress.filePath);

            } else {
                ApkUtils.installApkFile(mContext, progress.filePath);
            }

        }

    }


    /**
     * 重新安装
     */
    private void reSilentInstall(DownloadTask task, Progress progress, DownloadProgressButton progressButton, String tag) {
        //1.标记任务安装中
        progress.status = Progress.INSTALLING;
        task.updateDatabase(progress);
        //2.刷新视图
        refreshDownload(progress, progressButton, tag);
        //3.安装
        ApkUtils.silentInstall(progress.filePath);
    }
}