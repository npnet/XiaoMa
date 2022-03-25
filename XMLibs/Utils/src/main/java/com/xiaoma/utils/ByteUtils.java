package com.xiaoma.utils;

import java.text.DecimalFormat;

/**
 * @author taojin
 * @date 2018/11/14
 */
public class ByteUtils {

    /**
     * 返回byte的数据大小对应的文本
     *
     * @param size
     * @return
     */
    public static String getFileSize(long size) {

        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString;
        if (size <= 0) {
            return "0B";
        }
        if (size < 1024) {
            fileSizeString = df.format((double) size) + "B";
        } else if (size < 1048576) {
            fileSizeString = df.format((double) size / 1024) + "KB";
        } else if (size < 1073741824) {
            fileSizeString = df.format((double) size / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) size / 1073741824) + "GB";
        }
        return fileSizeString;
    }
}
