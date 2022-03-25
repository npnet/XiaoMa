package com.xiaoma.shop.business.download;

import android.app.DownloadManager;

/**
 * Created by LKF on 2019-6-26 0026.
 */
public class DownloadStatus {
    /**
     * 下载的url
     */
    public String downUrl;
    /**
     * 下载状态参见{@link  DownloadManager} STATUS_*
     */
    public int status = -1;
    /**
     * 当前下载字节数
     */
    public long currentLength = 0;
    /**
     * 文件总字节数,如果没有获取到,则为-1
     */
    public long totalLength = -1;
    /**
     * 下载的文件路径
     */
    public String downFilePath;

    public DownloadStatus() {
    }

    public DownloadStatus(DownloadStatus downloadStatus) {
        if (downloadStatus != null) {
            this.downUrl = downloadStatus.downUrl;
            this.status = downloadStatus.status;
            this.currentLength = downloadStatus.currentLength;
            this.totalLength = downloadStatus.totalLength;
            this.downFilePath = downloadStatus.downFilePath;
        }
    }
}
