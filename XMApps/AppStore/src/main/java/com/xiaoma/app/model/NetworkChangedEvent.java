package com.xiaoma.app.model;

import com.xiaoma.network.okserver.download.DownloadTask;

import java.util.List;

/**
 * Created by zhushi.
 * Date: 2018/11/30
 */
public class NetworkChangedEvent {
    private boolean isConnect;
    private List<DownloadTask> downloadTasks;

    public boolean isConnect() {
        return isConnect;
    }

    public void setConnect(boolean isConnect) {
        this.isConnect = isConnect;
    }

    public List<DownloadTask> getDownloadTaskList() {
        return downloadTasks;
    }

    public void setDownloadTaskList(List<DownloadTask> downloadTaskList) {
        this.downloadTasks = downloadTaskList;
    }
}
