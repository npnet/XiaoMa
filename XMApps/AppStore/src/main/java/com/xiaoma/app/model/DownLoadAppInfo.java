package com.xiaoma.app.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/12/12 0012.
 */

public class DownLoadAppInfo implements Serializable {
    private AppInfo appInfo;
    private int installState; //0代表未安装。1代表已安装最新版本，2代表已安装老版本
    private String localVersionName;
    private long installTime;

    public AppInfo getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(AppInfo appInfo) {
        this.appInfo = appInfo;
    }

    public int getInstallState() {
        return installState;
    }

    public void setInstallState(int installState) {
        this.installState = installState;
    }

    public String getLocalVersionName() {
        return localVersionName;
    }

    public void setLocalVersionName(String localVersionName) {
        this.localVersionName = localVersionName;
    }

    @Override
    public String toString() {
        return appInfo.toString() + "--" + installState;
    }

    public long getInstallTime() {
        return installTime;
    }

    public void setInstallTime(long installTime) {
        this.installTime = installTime;
    }
}
