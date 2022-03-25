package com.xiaoma.shop.common.util;

import android.support.annotation.Nullable;
import android.util.Log;

import com.xiaoma.utils.FileUtils;
import com.xiaoma.utils.ListUtils;
import com.xiaoma.utils.ZipUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by LKF on 2019-6-26 0026.
 */
public class ZipUtil {
    /**
     * @param zipFile      压缩文件
     * @param unzipRootDir 解压目录的上级目录; 注意,此目录不是解压目录,而是会在此目录下创建解压目录
     * @return 如果解压成功, 返回解压目录;否则返回null
     */
    @Nullable
    public static File unzip(final File zipFile, final File unzipRootDir) {
        File unzipDir = null;
        boolean unzipSuccess = false;
        try {
            String zipFileName = zipFile.getName();
            unzipDir = new File(unzipRootDir, zipFileName);
            // 开始解压
            List<File> unzipFiles = ZipUtils.unzipFile(zipFile, unzipDir);
            unzipSuccess = unzipFiles != null && !unzipFiles.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!unzipSuccess && unzipDir != null) {
            FileUtils.delete(unzipDir);
            unzipDir = null;
        }
        Log.i("ZipUtil", String.format("unzip: { zipFile: %s, unzipRootDir: %s, unzipDir: %s  }",
                zipFile, unzipRootDir, unzipDir));
        return unzipDir;
    }

    public static boolean unzipFile(final File zipFile,
                                    final File destDir) throws IOException {
        if (!ListUtils.isEmpty(ZipUtils.unzipFile(zipFile, destDir))) {
            return renameFile(destDir);
        }
        return false;
    }

    private static boolean renameFile(File dstFolder) {
        if (dstFolder == null || !dstFolder.isDirectory()) return false;
        // 判断文件后缀，将非wav后缀的文件修改成wav
        File[] files = dstFolder.listFiles();
        for (File f : files) {
            int lastPointIndex = f.getAbsolutePath().lastIndexOf(".");
            String prefix = f.getAbsolutePath().substring(0, lastPointIndex);
            String suffix = f.getAbsolutePath().substring(lastPointIndex);
            if (!".wav".equals(suffix)) {
                //修改文件名
                String newName = prefix + ".wav";
                if (!f.renameTo(new File(newName))) return false;
            }
        }
        return true;
    }
}
