package com.xiaoma.shop.business.skin;

import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoma.config.ConfigManager;
import com.xiaoma.shop.common.util.ZipUtil;
import com.xiaoma.utils.FileUtils;

import java.io.File;

/**
 * Created by LKF on 2019-6-20 0020.
 * 皮肤解压
 */
public class SkinUnzip {
    private static final String TAG = "SkinUnzip";

    /*public static File unzipToUsingDir(Context context, Uri zipFileUri) {
        try {
            FileDescriptor fd = context.getContentResolver().openFileDescriptor(zipFileUri, "r").getFileDescriptor();
            FileInputStream fin = new FileInputStream(fd);
            File tempFile = File.createTempFile("SkinZip", ".skin");
            if (FileUtils.write(fin, tempFile, false)) {
                return unzipToUsingDir(tempFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }*/

    public static File unzipToUsingDir(String zipFilePath) {
        if (TextUtils.isEmpty(zipFilePath))
            return null;
        return unzipToUsingDir(new File(zipFilePath));
    }

    /**
     * 将皮肤解压至正在使用的目录
     *
     * @return 解压后的皮肤目录, 解压失败返回null
     */
    @WorkerThread
    @Nullable
    public static File unzipToUsingDir(File zipFile) {
        Log.i(TAG, String.format("unzipToUsingDir: { zipFile: %s }", zipFile));
        File unzipParentDir = ConfigManager.FileConfig.getShopSkinUnzipFolder();
        // 删除整个解压目录,保证解压的皮肤中,只有一套皮肤,避免解压了太多主题,浪费空间
        if (unzipParentDir.exists()) {
            FileUtils.delete(unzipParentDir);
        }
        return ZipUtil.unzip(zipFile, unzipParentDir);
    }
}