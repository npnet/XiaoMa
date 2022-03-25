package com.xiaoma.update.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.xiaoma.config.ConfigConstants;
import com.xiaoma.ui.toast.XMToast;
import com.xiaoma.update.R;
import com.xiaoma.update.download.XMApkDownloadHelper;
import com.xiaoma.update.listener.IXMDownloadStateListener;
import com.xiaoma.update.model.ApkVersionInfo;
import com.xiaoma.update.views.AppUpdateDialog;
import com.xiaoma.utils.AppUtils;

import java.io.File;

/**
 * Created by Thomas on 2018/10/15 0015
 */

public class AppUpdateManager {

    private ApkVersionInfo updateAppInfo;
    private String mSavePath;
    private Context context;
    public static final String CANCEL_DATE_KEY = "cancel_date_key";

    public AppUpdateManager(Context context, ApkVersionInfo updateAppInfo) {
        this.context = context;
        this.updateAppInfo = updateAppInfo;
    }

    public static void killProcess() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public void showSelectUpdateDialog() {
        final AppUpdateDialog appUpdateDialog = new AppUpdateDialog(context);
        appUpdateDialog.setUpdateAppInfo(updateAppInfo);
        appUpdateDialog.setOnUpdateDialogListener(new AppUpdateDialog.OnUpdateDialogListener() {
            @Override
            public void onSelectUpdate(ApkVersionInfo updateAppInfo) {
                if (AppUtils.isAppInstalled(context, ConfigConstants.APP_STORE_PACKAGE_NAME)) {
                    should2AppStore(updateAppInfo.getPackageName());

                } else {
                    XMToast.toastException(context, context.getString(R.string.install_app_store_tips), false);
//                    downloadApp(updateAppInfo);
                }
            }

            @Override
            public void onForceUpdate(ApkVersionInfo updateAppInfo) {

            }
        });
        appUpdateDialog.show();
    }


    public void showForceUpdateDialog() {
        final AppUpdateDialog appUpdateDialog = new AppUpdateDialog(context);
        appUpdateDialog.setUpdateAppInfo(updateAppInfo);
        appUpdateDialog.setOnUpdateDialogListener(new AppUpdateDialog.OnUpdateDialogListener() {
            @Override
            public void onSelectUpdate(ApkVersionInfo updateAppInfo) {
            }

            @Override
            public void onForceUpdate(ApkVersionInfo updateAppInfo) {
                if (AppUtils.isAppInstalled(context, ConfigConstants.APP_STORE_PACKAGE_NAME)) {
                    should2AppStore(updateAppInfo.getPackageName());

                } else {
                    XMToast.toastException(context, context.getString(R.string.install_app_store_tips), false);
//                    downloadApp(updateAppInfo);
                }
            }
        });
        appUpdateDialog.show();
    }

    //跳转到应用中心去更新APP
    private void should2AppStore(String pckName) {
        Intent intent = new Intent();
        ComponentName cn = new ComponentName(ConfigConstants.APP_STORE_PACKAGE_NAME, ConfigConstants.APP_STORE_DETAIL_ACTIVITY);
        intent.setComponent(cn);
        intent.putExtra(ConfigConstants.APP_STORE_TYPE_KEY, ConfigConstants.APP_STORE_TYPE_XIAOMA_APP_WITHOUT_DATA);
        intent.putExtra(ConfigConstants.APP_STORE_PACKAGENAME_KEY, pckName);
        context.startActivity(intent);
    }

    private void downloadApp(ApkVersionInfo apkversion) {
        if (apkversion == null) {
            return;
        }
        String downLoadUrl = apkversion.getUrl();
        XMApkDownloadHelper xmApkDownloadHelper = XMApkDownloadHelper.newSingleton();
        xmApkDownloadHelper.startDownload(downLoadUrl, new IXMDownloadStateListener() {
            @Override
            public void onDownloadStart() {
                Toast.makeText(context, context.getString(R.string.in_updating), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDownloading(long progress, long total) {

            }

            @Override
            public void onDownloadError(String errorMsg) {
                Toast.makeText(context, context.getString(R.string.update_error), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDownloadFinish(File apkFile) {
                if (apkFile.exists()) {
                    AppUtils.installApk(context, apkFile);
                } else {
                    Toast.makeText(context, context.getString(R.string.download_file_error), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
