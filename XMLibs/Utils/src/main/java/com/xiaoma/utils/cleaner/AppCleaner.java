package com.xiaoma.utils.cleaner;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.IPackageDataObserver;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.utils.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author youthyJ
 * @date 2019/7/10
 */
public class AppCleaner {
    private static final String sp_path = "/shared_prefs";

    public static void deleteCacheFile(Context context, String packageName, DeleteCache callback) {
        if (!ConfigManager.ApkConfig.isCarPlatform()) {
            return;
        }
        if (context == null) {
            throw new NullPointerException("context is null");
        }
        PackageManager pm = context.getPackageManager();
        pm.deleteApplicationCacheFiles(packageName, callback);
    }

    public static void deleteDataFile(Context context, String packageName, DeleteCache callback) {
        if (!ConfigManager.ApkConfig.isCarPlatform()) {
            return;
        }
        if (context == null) {
            throw new NullPointerException("context is null");
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        am.clearApplicationUserData(packageName, callback);
    }

    public static boolean deleteXiaoMaFolder(Context context) {
        if (context == null) {
            throw new NullPointerException("context is null");
        }
        File xiaoMaFolder = ConfigManager.FileConfig.getXiaoMaFolder();
//        return FileUtils.delete(xiaoMaFolder);
        try {
            Process exec = Runtime.getRuntime().exec("rm -rf " + xiaoMaFolder);
            byte[] length = new byte[1024];
            while (true) {
                int read = exec.getInputStream().read(length);
                if (read == -1) {
                    return true;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void deleteSelfSp(Context context) {
        String spPath = context.getFilesDir().getParent() + sp_path;
        FileUtils.delete(spPath);
    }

    public static abstract class DeleteCache extends IPackageDataObserver.Stub {
        public abstract void onRemoveCompleted(String packageName, boolean succeeded);
    }
}
