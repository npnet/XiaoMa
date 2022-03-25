package com.xiaoma.update.download;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.update.listener.IXMApkDownload;
import com.xiaoma.update.listener.IXMDownloadStateListener;
import com.xiaoma.update.model.DownloadInfo;

import java.io.File;

/**
 * <pre>
 *  author : Jir
 *  date : 2018/9/17
 *  description :
 * </pre>
 */
public class XMApkDownloadHelper {

    private IXMApkDownload<DownloadInfo, IXMDownloadStateListener> mXMApkDownload;

    interface Holder {
        XMApkDownloadHelper sINSTANCE = new XMApkDownloadHelper();
    }

    private XMApkDownloadHelper() {
        mXMApkDownload = new XMApkDownloadImpl();
    }

    public static XMApkDownloadHelper newSingleton() {
        return Holder.sINSTANCE;
    }

    public void startDownload(String url, IXMDownloadStateListener listener) {
        mXMApkDownload.startDownload(createDownloadInfo(url), listener);
    }

    public void pauseDownload(String tag) {
        mXMApkDownload.cancelDownload(tag, false);
    }

    public void cancelDownload(String tag) {
        mXMApkDownload.cancelDownload(tag, true);
    }

    public void removeApkFile(String url) {
        mXMApkDownload.deleteApkFile(getDownloadFile(url.substring(url.lastIndexOf("/") + 1, url.length())));
    }

    private DownloadInfo createDownloadInfo(String url) {
        DownloadInfo downloadInfo = new DownloadInfo();
        downloadInfo.setUrl(url);
        String fileName = url.substring(url.lastIndexOf("/") + 1, url.length());
        File apkFile = getDownloadFile(fileName);
        if (apkFile.exists()) {
            downloadInfo.setLocalFilePath(apkFile.getParent());
            downloadInfo.setFileName(apkFile.getName());
            downloadInfo.setDownloadFinished(true);
        } else {
            downloadInfo.setDownloadFinished(false);
            downloadInfo.setUrl(url);
            downloadInfo.setFileName(fileName);
            downloadInfo.setLocalFilePath(ConfigManager.FileConfig.getUpdateFolder().getPath());
        }
        return downloadInfo;
    }

    private File getDownloadFile(String downloadFileName) {
        return new File(ConfigManager.FileConfig.getUpdateFolder().getPath(), downloadFileName);
    }

}
