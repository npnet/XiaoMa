package com.xiaoma.update.model;

import java.io.Serializable;

/**
 * <pre>
 *  author : Jir
 *  date : 2018/9/11
 *  description :
 * </pre>
 */
public class DownloadInfo implements Serializable {

    private String url;
    private String localFilePath;
    private String fileName;
    private boolean downloadFinished;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isDownloadFinished() {
        return downloadFinished;
    }

    public void setDownloadFinished(boolean downloadFinished) {
        this.downloadFinished = downloadFinished;
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }

}

