package com.xiaoma.pet.common.manager;

import android.text.TextUtils;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.network.db.DownloadManager;
import com.xiaoma.network.engine.OkGo;
import com.xiaoma.network.model.Progress;
import com.xiaoma.network.okserver.OkDownload;
import com.xiaoma.network.okserver.download.DownloadTask;
import com.xiaoma.pet.common.callback.OnDownLoadCallback;
import com.xiaoma.utils.log.KLog;

import java.io.File;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/5/27 0027 17:04
 *   desc:
 * </pre>
 */
public final class DownloadPetResource {

    private static final String TAG = DownloadPetResource.class.getSimpleName();

    private DownloadPetResource() {
    }

    public static DownloadPetResource getInstance() {
        return Holder.DOWNLOAD_PET_RESOURCE;
    }

    public void startDownLoad(String url, OnDownLoadCallback callback) {
        Progress progress = DownloadManager.getInstance().get(url);
        if (progress == null || progress.status == Progress.NONE || progress.status == Progress.WAITING
                || progress.status == Progress.PAUSE || progress.status == Progress.ERROR) {
            String folderPath = ConfigManager.FileConfig.getPetFolder().getPath();
            DownloadTask downloadTask = OkDownload.request(url, OkGo.<File>get(url))
                    .folder(folderPath)
                    .fileName(createFileName(url))
                    .register(callback);
            downloadTask.save();
            downloadTask.start();
        } else if (progress.status == 6) {
            DownloadTask downloadTask = OkDownload.restore(progress)
                    .register(callback);
            downloadTask.save();
            downloadTask.restart();
        } else {
            KLog.d(TAG, progress.status);
        }
    }

    public void release() {
        OkDownload.getInstance().pauseAll();
    }

    public String createFileName(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        return url.substring(url.lastIndexOf("/") + 1);
    }

    private static final class Holder {
        private final static DownloadPetResource DOWNLOAD_PET_RESOURCE = new DownloadPetResource();
    }
}
