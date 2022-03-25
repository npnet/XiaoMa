package com.xiaoma.hotfix.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.xiaoma.config.ConfigManager;
import com.xiaoma.hotfix.model.PatchConfig;
import com.xiaoma.hotfix.model.PatchDownloadInfo;
import com.xiaoma.hotfix.model.PatchResult;
import com.xiaoma.hotfix.utils.NetworkStatusUtils;
import com.xiaoma.model.XMResult;
import com.xiaoma.network.XmHttp;
import com.xiaoma.network.callback.FileCallback;
import com.xiaoma.network.callback.StringCallback;
import com.xiaoma.network.model.Response;
import com.xiaoma.thread.ThreadDispatcher;
import com.xiaoma.utils.FileUtils;
import com.xiaoma.utils.GsonHelper;
import com.xiaoma.utils.MD5Utils;
import com.xiaoma.utils.StringUtil;
import com.xiaoma.utils.tputils.TPUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TinkerUpgradeService extends Service implements NetworkStatusUtils.Listener {
    private static final String TAG = TinkerUpgradeService.class.getSimpleName() + "_LOG";
    private static final String PATCH_SERVICE_KEY = "PATCH_SERVICE_KEY";
    private static final String CURRENT_PATCH_CONFIG = "CURRENT_PATCH_CONFIG";

    private BroadcastReceiver networkReceiver;
    private PatchConfig curPatchConfig;
    private boolean isUpgrading = false;
    private boolean isUpgradeSuccess = false;

    public static void launch(Context context, final PatchConfig curPatchConfig) {
        if (context == null || curPatchConfig == null) {
            return;
        }
        final Context appContext = context.getApplicationContext();
        ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
            @Override
            public void run() {
                final String key = appContext.getPackageName() + PATCH_SERVICE_KEY;
                final String checkDate = TPUtils.get(appContext, key, "");
                final String today = StringUtil.getDateByYMD();
                if (checkDate.equals(today)) {
                    return;
                }
                Intent intent = new Intent();
                intent.setClassName(appContext, TinkerUpgradeService.class.getName());
                intent.putExtra(CURRENT_PATCH_CONFIG, curPatchConfig);
                appContext.startService(intent);
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        curPatchConfig = getCurrentPatchConfigByIntent(intent);
        networkReceiver = NetworkStatusUtils.listenNetworkStatusChange(this, this);
        startUpgrade();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        if (receiver == null) {
            return;
        }
        try {
            super.unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkReceiver);
    }

    @Override
    public void onNetworkStatusChange(boolean active) {
        if (active && !isUpgrading && !isUpgradeSuccess) {
            startUpgrade();
        }
    }

    private PatchConfig getCurrentPatchConfig() {
        return curPatchConfig;
    }

    private PatchConfig getCurrentPatchConfigByIntent(Intent intent) {
        if (intent == null) {
            return null;
        }
        return intent.getParcelableExtra(CURRENT_PATCH_CONFIG);
    }

    // 开始补丁
    private void startUpgrade() {
        Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
                + "\n * startUpgrade");
        isUpgrading = true;
        fetchDownloadInfo(new ICallback<PatchDownloadInfo>() {
            @Override
            public void onSuccess(final PatchDownloadInfo downloadInfo) {
                Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
                        + "\n * fetchDownloadInfoSuccess");
                ThreadDispatcher.getDispatcher().postLowPriority(new Runnable() {
                    @Override
                    public void run() {
                        downloadPatch(downloadInfo, new ICallback<File>() {
                            @Override
                            public void onSuccess(File file) {
                                Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
                                        + "\n * downloadPatchSuccess: " + file);
                                installPatch(file);
                                isUpgrading = false;
                            }

                            @Override
                            public void onFailed(int code, String msg) {
                                Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
                                        + "\n * downloadPatchFailure: " + msg);
                                isUpgrading = false;
                            }
                        });
                    }
                });
            }

            @Override
            public void onFailed(int errorCode, String errorMessage) {
                Log.w(TAG, "at " + new Throwable().getStackTrace()[0]
                        + "\n * fetchDownloadInfoFailure: " + errorMessage);
                isUpgrading = false;
            }
        });
    }

    // 获取补丁下载信息
    private void fetchDownloadInfo(final ICallback<PatchDownloadInfo> callback) {
        Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
                + "\n * fetchDownloadInfo");
        String packageName = getPackageName();
        String packageVersion = ConfigManager.ApkConfig.getBuildVersionName();

        String host = ConfigManager.EnvConfig.getEnv().getBusiness();
        String url = host + "apk/getLatestAppPatchForCar.action";
        Map params = new HashMap();
        params.put("packageName", packageName);
        params.put("packageVersion", packageVersion);
        XmHttp.getDefault().getString(url, params, new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                if (!response.isSuccessful()) {
                    callback.onFailed(-1, "request failure");
                    return;
                }
                String data = response.body();
                XMResult<PatchResult> result = GsonHelper.fromJson(data, new TypeToken<XMResult<PatchResult>>() {
                }.getType());
                if (result == null || !result.isSuccess()) {
                    callback.onFailed(-1, "parse failure");
                    return;
                }

                PatchResult patchResult = result.getData();
                PatchDownloadInfo downloadInfo = parsePatchResult(patchResult);
                if (downloadInfo == null) {
                    callback.onFailed(-1, "parse result failure");
                    return;
                }
                callback.onSuccess(downloadInfo);
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                callback.onFailed(-1, "network error");
            }
        });
    }

    // 解析补丁信息
    private PatchDownloadInfo parsePatchResult(PatchResult patchResult) {
        if (patchResult == null) {
            // 补丁下载信息异常
            return null;
        }
        String downloadURL = patchResult.getFile();
        if (TextUtils.isEmpty(downloadURL) || downloadURL.trim().isEmpty()) {
            // 下载url异常
            return null;
        }
        String downloadFileName = "";
        try {
            downloadFileName = downloadURL.substring(downloadURL.lastIndexOf("/") + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(downloadFileName)) {
            // 文件名异常
            return null;
        }
        String baseVersion = patchResult.getPackageVersion();
        String patchMD5 = patchResult.getMd5();
        int patchVersion = patchResult.getPatchVersion();

        PatchConfig servicePatchConfig = new PatchConfig();
        servicePatchConfig.setBasePkgVersion(baseVersion);
        servicePatchConfig.setPatchVersion(patchVersion);

        PatchDownloadInfo downloadInfo = new PatchDownloadInfo();
        downloadInfo.setDownloadUrl(downloadURL);
        downloadInfo.setFileName(downloadFileName);
        downloadInfo.setPatchMD5(patchMD5);
        downloadInfo.setServicePatchConfig(servicePatchConfig);

        return downloadInfo;
    }

    // 判断补丁是否可用
    private boolean isServicePatchAvailable(PatchDownloadInfo downloadInfo) {
        if (downloadInfo == null) {
            return false;
        }
        String downloadUrl = downloadInfo.getDownloadUrl();
        if (TextUtils.isEmpty(downloadUrl) || downloadUrl.trim().isEmpty()) {
            return false;
        }
        String patchMD5 = downloadInfo.getPatchMD5();
        if (TextUtils.isEmpty(patchMD5) || patchMD5.trim().isEmpty()) {
            return false;
        }
        PatchConfig curPatchConfig = getCurrentPatchConfig();
        PatchConfig serPatchConfig = downloadInfo.getServicePatchConfig();
        return isHighVersionPatch(curPatchConfig, serPatchConfig);
    }

    // 判断补丁是否是高版本的
    private boolean isHighVersionPatch(PatchConfig current, PatchConfig service) {
        if (current == null || service == null) {
            return false;
        }
        String curBaseVersion = current.getBasePkgVersion();
        String serBaseVersion = service.getBasePkgVersion();
        if (!curBaseVersion.equals(serBaseVersion)) {
            // 基础包版本不匹配
            return false;
        }
        int curPatchVersion = current.getPatchVersion();
        int serPatchVersion = service.getPatchVersion();
        return curPatchVersion < serPatchVersion;
    }

    // 下载补丁文件
    private void downloadPatch(final PatchDownloadInfo downloadInfo, final ICallback<File> callback) {
        Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
                + "\n * downloadPatch");
        if (!isServicePatchAvailable(downloadInfo)) {
            // 服务器补丁不可用
            callback.onFailed(-1, "service patch unavailable");
            return;
        }
        final String downLoadUrl = downloadInfo.getDownloadUrl();
        final String fileName = downloadInfo.getFileName();
        final String serviceMD5 = downloadInfo.getPatchMD5();
        File patchFolder = ConfigManager.FileConfig.getPatchFolder();
        File file = new File(patchFolder, fileName);
        if (file.exists()) {
            file.delete();
        }
        XmHttp.getDefault().getFile(downLoadUrl, new FileCallback() {
            @Override
            public void onSuccess(Response<File> response) {
                File downloadFile = response.body();
                if (downloadFile == null || !downloadFile.exists()) {
                    callback.onFailed(-1, "download file failure");
                    return;
                }
                File patchFolder = ConfigManager.FileConfig.getPatchFolder();
                File patchFile = new File(patchFolder, downloadFile.getName());
                if (patchFile.exists() || patchFile.isDirectory()) {
                    FileUtils.delete(patchFile);
                }
                boolean success = FileUtils.move(downloadFile, patchFile);
                if (!success) {
                    callback.onFailed(-1, "move patch file failure");
                    return;
                }
                boolean matched = checkPatchByMD5(patchFile, serviceMD5);
                if (!matched) {
                    callback.onFailed(-1, "patch file md5 unmatch");
                    return;
                }
                callback.onSuccess(patchFile);
            }

            @Override
            public void onError(Response<File> response) {
                super.onError(response);
                callback.onFailed(-1, "patch download failure");
            }
        });
    }

    // 校验补丁MD5
    private boolean checkPatchByMD5(File patchFile, String md5) {
        if (TextUtils.isEmpty(md5) || md5.trim().isEmpty() || !patchFile.exists()) {
            return false;
        }
        try {
            String patchFileMD5 = MD5Utils.getFileMD5String(patchFile);
            return md5.equals(patchFileMD5);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 安装补丁文件
    private void installPatch(File patchFile) {
        Log.d(TAG, "at " + new Throwable().getStackTrace()[0]
                + "\n * installPatch");
        if (patchFile == null || !patchFile.exists() || patchFile.isDirectory()) {
            return;
        }
        String key = getPackageName() + PATCH_SERVICE_KEY;
        String today = StringUtil.getDateByYMD();
        TPUtils.put(getApplicationContext(), key, today);
        TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(), patchFile.getAbsolutePath());
        isUpgradeSuccess = true;
        stopSelf();
    }

    private interface ICallback<T> {
        void onSuccess(T t);

        void onFailed(int code, String msg);
    }
}
