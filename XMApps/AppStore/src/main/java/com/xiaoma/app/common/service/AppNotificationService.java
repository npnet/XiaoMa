package com.xiaoma.app.common.service;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;

import com.xiaoma.app.R;
import com.xiaoma.app.api.AppStoreAPI;
import com.xiaoma.app.common.constant.AppStoreConstants;
import com.xiaoma.app.model.AppInfo;
import com.xiaoma.app.model.AppListResult;
import com.xiaoma.app.model.DownLoadAppInfo;
import com.xiaoma.app.util.ApkUtils;
import com.xiaoma.app.util.AppNotificationHelper;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.network.okserver.OkDownload;
import com.xiaoma.network.okserver.download.DownloadTask;
import com.xiaoma.thread.Priority;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.ListUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author taojin
 * @date 2019/4/3
 */
public class AppNotificationService extends Service {


    @Override
    public void onCreate() {
        super.onCreate();
        checkNeedUpdateApp();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void checkNeedUpdateApp() {
        Map<String, Object> params = new ArrayMap<>();
        XmHttp.getDefault().getString(AppStoreAPI.GET_APK_BY_CATEGORY, params, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                AppListResult listResult = GsonHelper.fromJson(response.body(), AppListResult.class);
                if (listResult == null || listResult.getResultCode() == AppStoreConstants.EMPTY_DATA_RESULT_CODE || listResult.getList() == null) {
                    return;
                }
                List<DownLoadAppInfo> downLoadAppInfos = wrapAppInfos(listResult.getList());

                if (!ListUtils.isEmpty(downLoadAppInfos)) {
                    for (DownLoadAppInfo appInfo : downLoadAppInfos) {
                        AppNotificationHelper.getInstance().putAppInfo(appInfo.getAppInfo().getPackageName(), appInfo.getAppInfo().getIconPathUrl());
                    }
                    getWaitUpdateApp(downLoadAppInfos);

                }

            }

            @Override
            public void onError(Response<String> response) {

            }
        }, Priority.HIGH);
    }


    /**
     * 将接口AppInfo数据和本地已安装应用对比
     *
     * @param appInfos
     * @return
     */
    private List<DownLoadAppInfo> wrapAppInfos(List<AppInfo> appInfos) {
        List<DownLoadAppInfo> downLoadAppInfoList = new ArrayList<>();
        for (int i = 0; i < appInfos.size(); i++) {
            DownLoadAppInfo loadAppInfo = new DownLoadAppInfo();
            loadAppInfo.setAppInfo(appInfos.get(i));
            PackageInfo packageInfo = ApkUtils.getPackageInfo(appInfos.get(i).getPackageName());
            if (packageInfo != null) {
                loadAppInfo.setLocalVersionName(packageInfo.versionName);
                if (packageInfo.versionCode >= loadAppInfo.getAppInfo().getVersionCode()) {
                    //最新版
                    loadAppInfo.setInstallState(AppStoreConstants.INSTALL_STATE_NEW);
                    loadAppInfo.setInstallTime(packageInfo.lastUpdateTime);
                    DownloadTask task = OkDownload.getInstance().getTask(packageInfo.packageName);
                    if (task != null) {
                        task.remove(true);
                    }

                } else {
                    //非最新版
                    loadAppInfo.setInstallState(AppStoreConstants.INSTALL_STATE_OLD);
                }

            } else {
                //未安装
                loadAppInfo.setInstallState(AppStoreConstants.INSTALL_STATE_NOTHING);
            }
            downLoadAppInfoList.add(loadAppInfo);
        }
        return downLoadAppInfoList;
    }

    /**
     * 获取待更新应用list
     *
     * @param data
     * @return
     */
    private void getWaitUpdateApp(List<DownLoadAppInfo> data) {
        List<DownLoadAppInfo> waitUpdateList = new ArrayList<>();
        for (DownLoadAppInfo downLoadAppInfo : data) {
            if (downLoadAppInfo.getInstallState() == AppStoreConstants.INSTALL_STATE_OLD) {
                waitUpdateList.add(downLoadAppInfo);
            }
        }
        if (waitUpdateList.size() > 0) {
            AppInfo appInfo = waitUpdateList.get(0).getAppInfo();
            if (waitUpdateList.size() > 1) {
                int size = waitUpdateList.size();
                AppNotificationHelper.getInstance().handleAppNotification(getApplicationContext(), AppNotificationHelper.APP_UPDATE_TIP, appInfo.getIconPathUrl(), appInfo.getAppName(), String.format(getString(R.string.update_tip_more), appInfo.getAppName(), size), appInfo.getPackageName(), appInfo.getCreateDate());
            } else {
                AppNotificationHelper.getInstance().handleAppNotification(getApplicationContext(), AppNotificationHelper.APP_UPDATE_TIP, appInfo.getIconPathUrl(), appInfo.getAppName(), String.format(getString(R.string.update_tip), appInfo.getAppName()), appInfo.getPackageName(), appInfo.getCreateDate());
            }

        }

    }
}
