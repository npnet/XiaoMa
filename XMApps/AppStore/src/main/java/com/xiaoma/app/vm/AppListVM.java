package com.xiaoma.app.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.content.pm.PackageInfo;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;

import com.xiaoma.app.api.AppStoreAPI;
import com.xiaoma.app.common.constant.AppStoreConstants;
import com.xiaoma.app.model.AppInfo;
import com.xiaoma.app.model.AppListResult;
import com.xiaoma.app.model.DownLoadAppInfo;
import com.xiaoma.app.util.ApkUtils;
import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.XmResource;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.network.okserver.OkDownload;
import com.xiaoma.network.okserver.download.DownloadTask;
import com.xiaoma.thread.Priority;
import com.xiaoma.utils.GsonHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by LKF on 2018/9/25 0025
 */
public class AppListVM extends BaseViewModel {

    private MutableLiveData<XmResource<List<DownLoadAppInfo>>> mAppInfoList;

    public AppListVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<List<DownLoadAppInfo>>> getAppInfoList() {
        if (mAppInfoList == null) {
            mAppInfoList = new MutableLiveData<>();
        }
        return mAppInfoList;
    }

    public void fetchAppInfoList() {
        getAppInfoList().setValue(XmResource.<List<DownLoadAppInfo>>loading());
        Map<String, Object> params = new ArrayMap<>();
        XmHttp.getDefault().getString(AppStoreAPI.GET_APK_BY_CATEGORY, params, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                AppListResult listResult = GsonHelper.fromJson(response.body(), AppListResult.class);
                if (listResult == null) {
                    getAppInfoList().setValue(XmResource.<List<DownLoadAppInfo>>error(response.code(), response.message()));
                    return;
                }
                if (listResult.getResultCode() == AppStoreConstants.EMPTY_DATA_RESULT_CODE || listResult.getList() == null) {
                    getAppInfoList().setValue(XmResource.<List<DownLoadAppInfo>>error(listResult.getResultCode(), listResult.getResultMessage()));
                    return;
                }
                getAppInfoList().setValue(XmResource.response(wrapAppInfos(listResult.getList())));
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                getAppInfoList().setValue(XmResource.<List<DownLoadAppInfo>>error(response.code(), response.message()));
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
                    // 针对【system ui/人脸识别/ 双屏互动/语音助手/车应用】应用屏蔽
                    if (AppStoreConstants.SYSTEM_UI.equals(packageInfo.packageName) ||
                            AppStoreConstants.FACERECOGNIZE.equals(packageInfo.packageName) ||
                            AppStoreConstants.DUAL_SCREEN.equals(packageInfo.packageName) ||
                            AppStoreConstants.ASSISTANT.equals(packageInfo.packageName)||
                            AppStoreConstants.APPSTORE.equals(packageInfo.packageName)) {
                        continue;
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

    @Override
    protected void onCleared() {
        super.onCleared();
        mAppInfoList = null;
    }
}