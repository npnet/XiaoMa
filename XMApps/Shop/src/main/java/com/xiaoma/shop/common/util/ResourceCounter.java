package com.xiaoma.shop.common.util;

import com.xiaoma.utils.log.KLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * <pre>
 *   @author Create by on Gillben
 *   date:   2019/4/25 0025 10:52
 *   desc:   资源文件cache计数
 * </pre>
 */
public final class ResourceCounter {


    private List<String> cacheFileName = new ArrayList<>();
    private int selectedCacheSize;


    private ResourceCounter() {
    }

    private static class Holder {
        private static final ResourceCounter RESOURCE_COUNTER = new ResourceCounter();
    }


    public static ResourceCounter getInstance() {
        return Holder.RESOURCE_COUNTER;
    }


    /**
     * 每次清理前先release，防止上一次缓存
     */
    public void release() {
        selectedCacheSize = 0;
        cacheFileName.clear();
    }


    //统计已选择文件size
    public String calculationSelectedFileCache(boolean select, File file) {
        if (select) {
            if (!cacheFileName.contains(file.getName())) {
                selectedCacheSize += getFileSize(file);
                cacheFileName.add(file.getName());
            }
        } else {
            selectedCacheSize -= getFileSize(file);
            cacheFileName.remove(file.getName());
        }
        return convertByteAsUnit(selectedCacheSize);
    }


    //转换cache为文本形式
    public String convertFolderCacheText(File file) {
        int size = getFileSize(file);
        return convertByteAsUnit(size);
    }


    public List<String> getCanCleanFileName() {
        return cacheFileName;
    }


    public int getSelectedNeedCleanLength() {
        return cacheFileName == null ? 0 : cacheFileName.size();
    }


    public int getDirectoryLength(File file) {
        return file == null || !file.isDirectory() ? 0 : file.listFiles().length;
    }


    /**
     * 获取某类资源总cache
     *
     * @param file 资源目录 或者单个资源
     */
    private int getFileSize(File file) {
        int size = 0;
        if (file == null) {
            return size;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File temp : files) {
                if (temp.isDirectory()) {
                    size += getFileSize(temp);
                } else {
                    size += temp.length();
                }
            }
        } else {
            size = (int) file.length();
        }
        KLog.e("size: " + size);
        return size;
    }


    public String convertByteAsUnit(long bit) {
        float K = 1024;
        float M = 1024 * 1024;
        float G = 1024 * 1024 * 1024;

        String tempSize = "M";
        if (bit <= 0) {
            return 0 + tempSize;
        }


        if (bit < M) {
            int size = (int) (bit / K);
            tempSize = String.format(Locale.CHINA, "%d", size) + "K";
        } else if (bit > M && bit < G) {
            float size = bit / M;
            tempSize = String.format(Locale.CHINA, "%.2f", size) + "M";
        } else if (bit > G) {
            float size = bit / G;
            tempSize = String.format(Locale.CHINA, "%.2f", size) + "G";
        }
        return tempSize;
    }


}
