package com.xiaoma.app.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import com.xiaoma.app.common.constant.AppStoreConstants;
import com.xiaoma.app.SilentInstallManager;
import com.xiaoma.app.model.DownLoadAppInfo;
import com.xiaoma.component.AppHolder;
import com.xiaoma.network.db.DownloadManager;
import com.xiaoma.network.model.Progress;
import com.xiaoma.utils.LaunchUtils;
import com.xiaoma.utils.log.KLog;

import org.simple.eventbus.EventBus;

import java.io.File;
import java.io.FilenameFilter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/12/12 0012.
 */

public class ApkUtils {
    private static final int ONE_ZERO_TWO_FOUR = 1024;

    /**
     * 获取文件目录下所有apk文件的app信息
     *
     * @param file
     * @return
     */
    public static List<DownLoadAppInfo> findAllApkFilePIByDir(File file) {
        if (file == null || !file.isDirectory()) return null;
        String[] filesName = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".apk");
            }
        });
        ArrayList<DownLoadAppInfo> apkFilePackageInfoArray = new ArrayList<>();
        for (String fileName : filesName) {
            DownLoadAppInfo downLoadAppInfo = new DownLoadAppInfo();
//            downLoadAppInfo.setLocalFilePath(fileName);
            apkFilePackageInfoArray.add(downLoadAppInfo);
        }
        return apkFilePackageInfoArray;
    }

    /**
     * 获取文件目录下所有apk文件的app信息
     *
     * @param dir
     * @return
     */
    public static List<DownLoadAppInfo> findAllApkFilePIByDir(String dir) {
        File file = new File(dir);
        if (file != null) {
            return findAllApkFilePIByDir(file);
        }
        return null;
    }

    /**
     * 获取本地已安装的app信息
     *
     * @param context
     * @return
     */
    public static List<PackageInfo> getAllInstalledPackageInfo(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packageinfos = pm.getInstalledPackages(0);
        return packageinfos;
    }

    public static HashMap<String, PackageInfo> getAllInstalledPiByHash(Context context) {
        if (context == null) return new HashMap<>();
        List<PackageInfo> packageInfosArr = getAllInstalledPackageInfo(context);
        HashMap<String, PackageInfo> packageInfosHash = new HashMap<>();
        if (packageInfosArr != null) {
            for (PackageInfo packageInfo : packageInfosArr) {
                packageInfosHash.put(packageInfo.packageName, packageInfo);
            }
        }
        return packageInfosHash;
    }


    public static HashMap<String, PackageInfo> getAllInstalledApp(Context context) {
        List<PackageInfo> packageInfos = ApkUtils.getAllInstalledPackageInfo(context);
        if (packageInfos == null) return null;
        HashMap<String, PackageInfo> packageInfoHashMap = new HashMap<>();
        for (PackageInfo packageInfo : packageInfos) {
            packageInfoHashMap.put(packageInfo.packageName, packageInfo);
        }
        return packageInfoHashMap;
    }

    /**
     * 安装apk文件
     *
     * @param context
     * @param filePath
     */
    public static void installApkFile(Context context, String filePath) {
        if (filePath == null || filePath.isEmpty() || context == null) return;
        KLog.d("installApkFile : the file path is " + filePath);
        File file = new File(filePath);
        if (file.isFile() && file.exists() && file.getName().endsWith(".apk")) {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= 24) {
                //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
                Uri apkUri = FileProvider.getUriForFile(context, "com.xiaoma.app.fileprovider", file);
                //添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");

            } else {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            }
            context.startActivity(intent);
        }
    }

    /**
     * 打开某个app
     *
     * @param context
     * @param packageName
     */
    public static void openApp(Context context, String packageName) {
        PackageInfo packageinfo = null;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }
        LaunchUtils.launchApp(context, packageName);
    }

    /**
     * 根据版本号和packageName判断本地是否已经安装该程序
     *
     * @param localApps   本地已安装的app
     * @param packageName 需要判断的程序packageName
     * @param versionCode 需要判断的程序versionCode
     * @return
     */
    public static boolean isInstalled(HashMap<String, PackageInfo> localApps, String packageName, int versionCode) {
        PackageInfo localPackageInfo = localApps.get(packageName);
        if (localPackageInfo == null) return false;
        else return localPackageInfo.versionCode >= versionCode;
    }


    /**
     * 将字节转换为MB
     *
     * @param byteSize
     * @return
     */
    public static String byteToM(long byteSize) {
        BigDecimal filesize = new BigDecimal(byteSize);
        BigDecimal megabyte = new BigDecimal(ONE_ZERO_TWO_FOUR * ONE_ZERO_TWO_FOUR);
        float returnValue = filesize.divide(megabyte, 1, BigDecimal.ROUND_UP)
                .floatValue();
        if (returnValue > 1)
            return (returnValue + "MB");
        BigDecimal kilobyte = new BigDecimal(ONE_ZERO_TWO_FOUR);
        returnValue = filesize.divide(kilobyte, 1, BigDecimal.ROUND_UP)
                .floatValue();
        return (returnValue + "KB");
    }

    public static PackageInfo getPackageInfo(String pkgName) {
        return getPackageInfo(pkgName, 0);
    }

    public static PackageInfo getPackageInfo(String pkgName, int flag) {
        PackageManager pm = AppHolder.getInstance().getAppContext().getPackageManager();
        try {
            if (pm != null) {
                return pm.getPackageInfo(pkgName, flag);
            }
        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
        }
        return null;
    }

    /**
     * 静默安装
     *
     * @param filePath
     */
    public static void silentInstall(final String filePath) {
        List<String> path = new ArrayList<>();
        path.add(filePath);
        SilentInstallManager.getInstance().installApkFile(path);
    }

    /**
     * 静默安装
     *
     * @param pathList
     */
    public static void silentInstall(List<String> pathList) {
        SilentInstallManager.getInstance().installApkFile(pathList);
    }

    /**
     * 通知下载任务数目
     */
    public static void notifyDownloadTaskCount() {
        EventBus.getDefault().post(DownloadManager.getInstance().getAll().size(), AppStoreConstants.MSG_DOWNLOAD_LIST_SIZE);
    }


}
