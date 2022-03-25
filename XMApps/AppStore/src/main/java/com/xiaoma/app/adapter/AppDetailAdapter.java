package com.xiaoma.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoma.app.R;
import com.xiaoma.app.SilentInstallManager;
import com.xiaoma.app.common.constant.AppStoreConstants;
import com.xiaoma.app.common.constant.EventConstants;
import com.xiaoma.app.listener.AppDownloadListener;
import com.xiaoma.app.listener.ISwitchFragmentListener;
import com.xiaoma.app.model.AppInfo;
import com.xiaoma.app.model.BatchInfo;
import com.xiaoma.app.model.DownLoadAppInfo;
import com.xiaoma.app.ui.activity.AppDetailsActivity;
import com.xiaoma.app.util.ApkUtils;
import com.xiaoma.app.util.AppNotificationHelper;
import com.xiaoma.app.util.Transformations;
import com.xiaoma.app.views.DownloadProgressButton;
import com.xiaoma.app.views.PhotosViewDialog;
import com.xiaoma.autotracker.listener.XMAutoTrackerEventOnClickListener;
import com.xiaoma.image.ImageLoader;
import com.xiaoma.login.LoginManager;
import com.xiaoma.login.UserManager;
import com.xiaoma.login.common.LoginConstants;
import com.xiaoma.model.ItemEvent;
import com.xiaoma.model.User;
import com.xiaoma.model.annotation.BusinessOnClick;
import com.xiaoma.model.annotation.NormalOnClick;
import com.xiaoma.model.annotation.ResId;
import com.xiaoma.network.db.DownloadManager;
import com.xiaoma.network.engine.OkGo;
import com.xiaoma.network.model.Progress;
import com.xiaoma.network.okserver.OkDownload;
import com.xiaoma.network.okserver.download.DownloadTask;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.utils.KeyEventUtils;
import com.xiaoma.utils.LaunchUtils;
import com.xiaoma.utils.NetworkUtils;
import com.xiaoma.utils.logintype.callback.OnBlockCallback;
import com.xiaoma.utils.logintype.manager.LoginType;
import com.xiaoma.utils.logintype.manager.LoginTypeManager;

import org.simple.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhushi.
 * Date: 2018/11/30
 */
public class AppDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private LayoutInflater mInflater;
    private DownLoadAppInfo mDownloadAppInfo;
    private AppInfo mAppInfo;
    //卸载点击监听
    private UninstallListener mUninstallListener;
    //fragment切换监听
    private ISwitchFragmentListener mSwitchFragmentListener;
    private PhotosViewDialog photosViewDialog;

    private final int DETAIL_ITEM_COUNT = 1;

    public AppDetailAdapter(Context mContext) {
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View detailView = mInflater.inflate(R.layout.item_app_detail, parent, false);
        return new DetailViewHolder(detailView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        String bigImageUrl = mDownloadAppInfo.getAppInfo().getBigImgUrl1();
        String bigImageUr2 = mDownloadAppInfo.getAppInfo().getBigImgUrl2();
        setAppBaseInfo((DetailViewHolder) holder);
        DetailViewHolder imageViewHolder = (DetailViewHolder) holder;
        ImageLoader.with(mContext)
                .load(bigImageUrl)
                .placeholder(R.drawable.fm_default_big_cover)
                .into(imageViewHolder.detailImage);
        ImageLoader.with(mContext)
                .load(bigImageUr2)
                .placeholder(R.drawable.fm_default_big_cover)
                .into(imageViewHolder.detailImage2);

        imageViewHolder.detailImage.setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick({EventConstants.NormalClick.detailImage})
            @ResId({R.id.iv_app_detail})
            public void onClick(View v) {
                List<String> imageList = new ArrayList<>();
                imageList.add(mAppInfo.getBigImgUrl1());
                imageList.add(mAppInfo.getBigImgUrl2());
                photosViewDialog = new PhotosViewDialog(mContext, imageList);
                photosViewDialog.setCurrentItem(0);
                photosViewDialog.show();
            }
        });
        imageViewHolder.detailImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            @NormalOnClick({EventConstants.NormalClick.detailImage})
            @ResId({R.id.iv_app_detai2})
            public void onClick(View v) {
                List<String> imageList = new ArrayList<>();
                imageList.add(mAppInfo.getBigImgUrl1());
                imageList.add(mAppInfo.getBigImgUrl2());
                photosViewDialog = new PhotosViewDialog(mContext, imageList);
                photosViewDialog.setCurrentItem(1);
                photosViewDialog.show();
            }
        });
    }

    /**
     * 设置应用基础信息
     *
     * @param detailViewHolder
     */
    private void setAppBaseInfo(DetailViewHolder detailViewHolder) {
        //APP icon
        ImageLoader.with(mContext)
                .load(mAppInfo.getIconPathUrl())
                .placeholder(R.drawable.fm_default_cover)
                .transform(Transformations.getRoundCorners())
                .into(detailViewHolder.ivAppIcon);
        //APP name
        detailViewHolder.tvAppName.setText(mAppInfo.getAppName());
        //APP size
        detailViewHolder.tvAppSize.setText(mContext.getString(R.string.app_size, ApkUtils.byteToM(mAppInfo.getSize())));
        //APP 描述
        String introduce = String.format(mContext.getString(R.string.app_describe), mAppInfo.getVersionDesc());
        SpannableString msp = new SpannableString(introduce);
        msp.setSpan(new ForegroundColorSpan(Color.WHITE), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        msp.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.color_gray)), 3, introduce.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        detailViewHolder.tvAppIntroduce.setText(introduce);

        //APP 版本号
        PackageInfo packageInfo = ApkUtils.getPackageInfo(mAppInfo.getPackageName());
        if (mDownloadAppInfo.getInstallState() == AppStoreConstants.INSTALL_STATE_OLD && packageInfo != null) {
            detailViewHolder.tvAppVersion.setText(String.format(mContext.getString(R.string.version_name_update), packageInfo.versionName, mAppInfo.getVersionName()));

        } else if (mDownloadAppInfo.getInstallState() == AppStoreConstants.INSTALL_STATE_NEW && packageInfo != null) {
            detailViewHolder.tvAppVersion.setText(String.format(mContext.getString(R.string.app_version), packageInfo.versionName));

        } else {
            detailViewHolder.tvAppVersion.setText(String.format(mContext.getString(R.string.app_version), mAppInfo.getVersionName()));
        }

        //APP 更新日志入口(为最新版本时不显示更新日志)
        if (mDownloadAppInfo.getInstallState() == AppStoreConstants.INSTALL_STATE_NEW) {
            detailViewHolder.mTvUpDateLog.setVisibility(View.GONE);
        } else {
            detailViewHolder.mTvUpDateLog.setVisibility(View.VISIBLE);
        }

        //APP 卸载按钮
        //1.应用已经安装&&2.后台配置应用允许卸载
        if (mDownloadAppInfo.getInstallState() != AppStoreConstants.INSTALL_STATE_NOTHING &&
                mAppInfo.getUninstall() == AppStoreConstants.APP_ALLOW_INSTALL) {
            detailViewHolder.btnUninstall.setVisibility(View.VISIBLE);
            detailViewHolder.btnUninstall.setOnClickListener(new View.OnClickListener() {
                @Override
                @NormalOnClick({EventConstants.NormalClick.uninstall})
                @ResId({R.id.btn_uninstall})
                public void onClick(View v) {
                    if (SilentInstallManager.getInstance().isBindService()) {
                        if (mUninstallListener != null) {
                            mUninstallListener.onClickUninstallDialog();
                        }

                    } else {
                        Intent uninstall_intent = new Intent();
                        uninstall_intent.setAction(Intent.ACTION_DELETE);
                        uninstall_intent.setData(Uri.parse("package:" + mAppInfo.getPackageName()));
                        mContext.startActivity(uninstall_intent);
                    }
                }
            });

        } else {
            detailViewHolder.btnUninstall.setVisibility(View.INVISIBLE);
        }

        //APP 下载、打开、更新按钮
        OkDownload instance = OkDownload.getInstance();
        //如果这个任务已经存在
        if (instance.hasTask(mAppInfo.getPackageName())) {
            instance.getTask(mAppInfo.getPackageName()).register(new ListDownloadListener(detailViewHolder));
            setProgressStateBtn(detailViewHolder, DownloadManager.getInstance().get(mAppInfo.getPackageName()));

            //任务不存在
        } else {
            setNormalStateBtn(detailViewHolder);
        }

        setDownloadProgressClick(detailViewHolder);
    }

    private void setDownloadProgressClick(final DetailViewHolder detailViewHolder) {
        detailViewHolder.btnDownloadProgress.setOnClickListener(new XMAutoTrackerEventOnClickListener() {
            @Override
            @BusinessOnClick
            public void onClick(View v) {
                boolean isUpdate = false;
                switch (mDownloadAppInfo.getInstallState()) {
                    //打开
                    case AppStoreConstants.INSTALL_STATE_NEW:
                        // 权限限制
                        if (!canUse(mAppInfo.getPackageName())) return;
                        if (!KeyEventUtils.isGoHome(mContext, mAppInfo.getPackageName())) {
                            LaunchUtils.launchApp(mContext, mAppInfo.getPackageName());
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

                        DownloadTask task = OkDownload.request(mAppInfo.getPackageName(), OkGo.<File>get(mAppInfo.getUrl()))
                                .extra1(mAppInfo)
                                .extra2(isUpdate)
                                .save()
                                .register(new ListDownloadListener(detailViewHolder));
                        Progress progress = DownloadManager.getInstance().get(mAppInfo.getPackageName());
                        switch (progress.status) {
                            case Progress.LOADING:
                                task.pause();
                                break;

                            case Progress.INSTALL_FAILED:
                                if (SilentInstallManager.getInstance().isBindService()) {
                                    reSilentInstall(task, detailViewHolder, progress);

                                } else {
                                    ApkUtils.installApkFile(mContext, progress.filePath);
                                }
                                break;

                            case Progress.PAUSE:
                            case Progress.NONE:
                            case Progress.ERROR:
                                task.start();
                                break;
                        }
                        break;
                }
            }

            @Override
            public ItemEvent returnPositionEventMsg(View view) {
                BatchInfo batchInfo = new BatchInfo(String.valueOf(mAppInfo.getId()), mAppInfo.getAppName(), String.valueOf(mAppInfo.getVersionCode()), mAppInfo.getVersionName());
                return new ItemEvent(((DownloadProgressButton) view).getmCurrentText(), batchInfo.toJson());
            }
        });
    }

    // 权限判断是否能使用
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
                        bundle.putParcelable(LoginConstants.KeyBind.BundleKey.USER, (Parcelable) user);
                        LoginTypeManager.getInstance().keyVerificationAndStartApp(mContext, bundle, packageName);
                        return true;
                    }
                });
    }

    /**
     * 重新安装
     *
     * @param task
     * @param progress
     */
    private void reSilentInstall(DownloadTask task, DetailViewHolder detailViewHolder, Progress progress) {
        //1.标记任务安装中
        progress.status = Progress.INSTALLING;
        task.updateDatabase(progress);
        //2.刷新视图
        setProgressStateBtn(detailViewHolder, progress);
        //3.安装
        ApkUtils.silentInstall(progress.filePath);
    }

    /**
     * 设置进度中按钮
     *
     * @param progress
     */
    private void setProgressStateBtn(DetailViewHolder detailViewHolder, Progress progress) {
        detailViewHolder.btnDownloadProgress.setVisibility(View.VISIBLE);
        switch (progress.status) {
            case Progress.NONE:
                detailViewHolder.btnDownloadProgress.setState(DownloadProgressButton.TYPE_NORMAL, mContext.getString(R.string.app_download));
                break;

            case Progress.PAUSE:
            case Progress.ERROR:
                if (progress.extra2 != null && (boolean) progress.extra2) {
                    detailViewHolder.btnDownloadProgress.setState(DownloadProgressButton.TYPE_PROGRESS_PAUSE, mContext.getString(R.string.update_continue));

                } else {
                    detailViewHolder.btnDownloadProgress.setState(DownloadProgressButton.TYPE_PROGRESS_PAUSE, mContext.getString(R.string.download_continue));
                }
                break;

            case Progress.WAITING:
                detailViewHolder.btnDownloadProgress.setState(DownloadProgressButton.TYPE_PROGRESS_WAIT, mContext.getString(R.string.download_status_waiting));
                break;

            case Progress.LOADING:
                detailViewHolder.btnDownloadProgress.setState(DownloadProgressButton.TYPE_PROGRESS, (int) (progress.fraction * 100) + "%");
                break;

            //安装失败
            case Progress.INSTALL_FAILED:
                detailViewHolder.btnDownloadProgress.setState(DownloadProgressButton.TYPE_NORMAL, mContext.getString(R.string.download_status_finish));
                break;

            //安装中
            case Progress.FINISH:
            case Progress.INSTALLING:
                detailViewHolder.btnDownloadProgress.setState(DownloadProgressButton.TYPE_UN_CLICK, mContext.getString(R.string.installing));
                break;
        }
        detailViewHolder.btnDownloadProgress.setProgress((int) (progress.fraction * 100));
    }

    /**
     * 设置下载，打开，更新按钮
     *
     * @param detailViewHolder
     */
    private void setNormalStateBtn(DetailViewHolder detailViewHolder) {
        detailViewHolder.btnDownloadProgress.setVisibility(View.VISIBLE);
        switch (mDownloadAppInfo.getInstallState()) {
            case AppStoreConstants.INSTALL_STATE_NOTHING:
                detailViewHolder.btnDownloadProgress.setState(DownloadProgressButton.TYPE_NORMAL, mContext.getString(R.string.app_download));
                break;

            case AppStoreConstants.INSTALL_STATE_NEW:
                // 针对【system ui/人脸识别/ 双屏互动/语音助手/车应用】应用屏蔽打开按钮
                if (AppStoreConstants.SYSTEM_UI.equals(mAppInfo.getPackageName()) ||
                        AppStoreConstants.FACERECOGNIZE.equals(mAppInfo.getPackageName()) ||
                        AppStoreConstants.DUAL_SCREEN.equals(mAppInfo.getPackageName()) ||
                        AppStoreConstants.ASSISTANT.equals(mAppInfo.getPackageName()) ||
                        AppStoreConstants.APPSTORE.equals(mAppInfo.getPackageName())) {
                    detailViewHolder.btnDownloadProgress.setVisibility(View.GONE);

                } else {
                    detailViewHolder.btnDownloadProgress.setVisibility(View.VISIBLE);
                }
                detailViewHolder.btnDownloadProgress.setState(DownloadProgressButton.TYPE_NORMAL, mContext.getString(R.string.app_open));
                break;

            case AppStoreConstants.INSTALL_STATE_OLD:
                detailViewHolder.btnDownloadProgress.setState(DownloadProgressButton.TYPE_NORMAL, mContext.getString(R.string.app_update_statue));
                break;
        }
    }

    public void setClickUninstallDialog(UninstallListener listener) {
        this.mUninstallListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return DETAIL_ITEM_COUNT;
    }

    public void setDownloadAppInfo(DownLoadAppInfo mDownloadAppInfo) {
        this.mDownloadAppInfo = mDownloadAppInfo;
        this.mAppInfo = mDownloadAppInfo.getAppInfo();
        notifyDataSetChanged();
    }

    class DetailViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAppIcon;
        TextView tvAppName;
        TextView tvAppSize;
        TextView tvAppVersion;
        TextView tvAppIntroduce;
        DownloadProgressButton btnDownloadProgress;
        TextView btnUninstall;
        TextView mTvUpDateLog;
        ImageView detailImage;
        ImageView detailImage2;


        DetailViewHolder(View itemView) {
            super(itemView);
            ivAppIcon = itemView.findViewById(R.id.iv_app_icon);
            tvAppName = itemView.findViewById(R.id.tv_app_name);
            tvAppSize = itemView.findViewById(R.id.tv_app_size);
            tvAppVersion = itemView.findViewById(R.id.tv_app_version);
            tvAppIntroduce = itemView.findViewById(R.id.tv_app_introduce);
            btnDownloadProgress = itemView.findViewById(R.id.btn_download_progress);
            btnUninstall = itemView.findViewById(R.id.btn_uninstall);
            mTvUpDateLog = itemView.findViewById(R.id.tv_update_log);
            detailImage = itemView.findViewById(R.id.iv_app_detail);
            detailImage2 = itemView.findViewById(R.id.iv_app_detai2);
            mTvUpDateLog.setOnClickListener(new View.OnClickListener() {
                @Override
                @NormalOnClick({EventConstants.NormalClick.updateLog})
                @ResId({R.id.tv_update_log})
                public void onClick(View v) {
                    if (mSwitchFragmentListener != null) {
                        mSwitchFragmentListener.switchFragment(AppDetailsActivity.APPUPDATELOGFRAGMENT);
                    }
                }
            });
        }
    }

    public void setSwitchFragmentListener(ISwitchFragmentListener switchFragmentListener) {
        this.mSwitchFragmentListener = switchFragmentListener;
    }

    /**
     * 卸载点击监听
     */
    public interface UninstallListener {
        void onClickUninstallDialog();
    }

    /**
     * 进度条的监听
     */
    private class ListDownloadListener extends AppDownloadListener {
        private DetailViewHolder detailViewHolder;

        public ListDownloadListener(DetailViewHolder tag) {
            super(tag);
            this.detailViewHolder = tag;
        }

        @Override
        public void onStart(Progress progress) {
            EventBus.getDefault().post(progress.tag, AppStoreConstants.MSG_DOWNLOAD_TASK_START);
        }

        @Override
        public void onProgress(Progress progress) {
            setProgressStateBtn(detailViewHolder, progress);
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
}
