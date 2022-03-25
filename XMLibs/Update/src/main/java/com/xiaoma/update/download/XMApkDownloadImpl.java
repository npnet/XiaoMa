package com.xiaoma.update.download;

import android.util.Log;

import com.xiaoma.network.db.DownloadManager;
import com.xiaoma.network.engine.OkGo;
import com.xiaoma.network.model.Progress;
import com.xiaoma.network.okserver.OkDownload;
import com.xiaoma.network.okserver.download.DownloadListener;
import com.xiaoma.network.okserver.download.DownloadTask;
import com.xiaoma.update.listener.IXMApkDownload;
import com.xiaoma.update.listener.IXMDownloadStateListener;
import com.xiaoma.update.model.DownloadInfo;
import com.xiaoma.utils.FileUtils;
import com.xiaoma.utils.log.KLog;

import java.io.File;
import java.util.Map;

/**
 * <pre>
 *  author : Jir
 *  date : 2018/9/17
 *  description :
 * </pre>
 */
public class XMApkDownloadImpl implements IXMApkDownload<DownloadInfo, IXMDownloadStateListener> {

    public static final String TAG = "XMApkDownloadImpl";
    private DownloadInfo mDownloadInfo;

    @Override
    public void startDownload(DownloadInfo downloadInfo, IXMDownloadStateListener listener) {
        if (checkDownloadState(downloadInfo)) {
            if (listener != null) {
                listener.onDownloadFinish(new File(downloadInfo.getLocalFilePath(), downloadInfo.getFileName()));
            }
        } else {
            requestForDownload(downloadInfo, listener);
        }
    }


    @Override
    public void cancelDownload(String tag, boolean deleteFileTF) {
        if (tag != null) {
            DownloadTask downloadTask = OkDownload.getInstance().getTask(tag);
            if (downloadTask != null) {
                downloadTask.pause();
                if (deleteFileTF) {
                    downloadTask.remove(true);
                }
            }
        }
    }

    @Override
    public void deleteApkFile(File apkFile) {
        FileUtils.delete(apkFile);
    }

    private boolean checkDownloadState(DownloadInfo downloadInfo) {
        return downloadInfo.isDownloadFinished();
    }

    private void requestForDownload(DownloadInfo downloadInfo, IXMDownloadStateListener listener) {
        Log.d(TAG, "requestForDownload url: " + downloadInfo.getUrl());
        this.mDownloadInfo = downloadInfo;
        if (OkDownload.getInstance().hasTask(downloadInfo.getUrl())) {
            DownloadTask task = OkDownload.getInstance().getTask(downloadInfo.getUrl());
            Progress progress = DownloadManager.getInstance().get(downloadInfo.getUrl());
            if (progress.status == Progress.PAUSE || progress.status == Progress.WAITING) {
                task.start();
            } else {
                task.restart();
            }
        } else {
            String requestTag = downloadInfo.getUrl();
            OkDownload.request(requestTag, OkGo.<File>get(requestTag))
                    .folder(downloadInfo.getLocalFilePath())
                    .fileName(downloadInfo.getFileName())
                    .save()
                    .register(new XMDownloadListener(requestTag, listener))
                    .start();
        }
    }

    private boolean isTempFileValid(File tempFile, Progress progress) {
        if (progress.currentSize == progress.totalSize) {
            return true;
        } else {
            FileUtils.delete(tempFile);
            return false;
        }
    }

    private void unregisterAll() {
        Map<String, DownloadTask> taskMap = OkDownload.getInstance().getTaskMap();
        if (taskMap != null) {
            for (DownloadTask task : taskMap.values()) {
                task.unRegister(task.progress.tag);
            }
        }
    }

    private void unregister(String urlTag) {
        DownloadTask task = OkDownload.getInstance().getTask(urlTag);
        if (task != null) {
            task.unRegister(urlTag);
        }
    }

    private class XMDownloadListener extends DownloadListener {

        private IXMDownloadStateListener mXmDownloadStateListener;

        public XMDownloadListener(Object tag, IXMDownloadStateListener xmDownloadStateListener) {
            super(tag);
            this.mXmDownloadStateListener = xmDownloadStateListener;
        }

        @Override
        public void onStart(Progress progress) {
            KLog.d("XMApkDownloadImpl onStart");
            if (mXmDownloadStateListener != null) {
                mXmDownloadStateListener.onDownloadStart();
            }
        }

        @Override
        public void onProgress(Progress progress) {
            KLog.d("XMApkDownloadImpl progress");
            if (mXmDownloadStateListener != null) {
                mXmDownloadStateListener.onDownloading(progress.currentSize, progress.totalSize);
            }
        }

        @Override
        public void onError(Progress progress) {
            KLog.d("XMApkDownloadImpl onError");
            if (mXmDownloadStateListener != null) {
                FileUtils.delete(progress.filePath);
                mXmDownloadStateListener.onDownloadError(progress.exception.toString());
            }
        }

        @Override
        public void onFinish(File file, Progress progress) {
            KLog.d("XMApkDownloadImpl onFinish");
            if (isTempFileValid(file, progress)) {
                if (mXmDownloadStateListener != null) {
                    mXmDownloadStateListener.onDownloadFinish(file);
                }
            } else {
                FileUtils.delete(progress.filePath);
                if (mXmDownloadStateListener != null) {
                    mXmDownloadStateListener.onDownloadError("CheckFailed");
                }
            }
        }

        @Override
        public void onRemove(Progress progress) {
            KLog.d("XMApkDownloadImpl onRemove");
        }
    }

}
