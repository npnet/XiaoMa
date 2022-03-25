package com.xiaoma.app.vm;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.content.pm.PackageInfo;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;

import com.google.gson.reflect.TypeToken;
import com.xiaoma.app.api.AppStoreAPI;
import com.xiaoma.app.common.constant.AppStoreConstants;
import com.xiaoma.app.model.AppInfo;
import com.xiaoma.app.model.DownLoadAppInfo;
import com.xiaoma.app.util.ApkUtils;
import com.xiaoma.component.base.BaseViewModel;
import com.xiaoma.model.XMResult;
import com.xiaoma.model.XmResource;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.network.okserver.OkDownload;
import com.xiaoma.network.okserver.download.DownloadTask;
import com.xiaoma.utils.GsonHelper;

import java.util.Map;

/**
 * Created by zhushi.
 * Date: 2018/10/17
 */
public class AppInfoVM extends BaseViewModel {

    private MutableLiveData<XmResource<DownLoadAppInfo>> appInfo;

    public AppInfoVM(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<XmResource<DownLoadAppInfo>> getAppInfo() {
        if (appInfo == null) {
            appInfo = new MutableLiveData<>();
        }
        return appInfo;
    }

    public void fetchAppInfo(String packageName) {
        getAppInfo().setValue(XmResource.<DownLoadAppInfo>loading());
        Map<String, Object> params = new ArrayMap<>();
        params.put("packageName", packageName);

        XmHttp.getDefault().getString(AppStoreAPI.GET_APKINFO_BY_PACKGENAME, params, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                XMResult<AppInfo> appInfo = GsonHelper.fromJson(response.body(), new TypeToken<XMResult<AppInfo>>() {
                }.getType());
                if (appInfo == null || appInfo.getData() == null) {
                    getAppInfo().setValue(XmResource.<DownLoadAppInfo>error(response.code(), response.message()));
                    return;
                }
                getAppInfo().setValue(XmResource.response(wrapAppInfo(appInfo.getData())));
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                getAppInfo().setValue(XmResource.<DownLoadAppInfo>error(response.code(), response.message()));
            }
        });
    }

    private DownLoadAppInfo wrapAppInfo(AppInfo appInfo) {
        DownLoadAppInfo loadAppInfo = new DownLoadAppInfo();
        loadAppInfo.setAppInfo(appInfo);
        PackageInfo packageInfo = ApkUtils.getPackageInfo(appInfo.getPackageName());
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

        return loadAppInfo;
    }
}
