package com.xiaoma.instructiondistribute.xkan.common.util;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;

import com.bumptech.glide.Glide;

/**
 * Created by LKF on 2019-3-5 0005.
 */
public class ImageUtils {
    public static boolean isGif(String path) {
        if (TextUtils.isEmpty(path))
            return false;
        final int beginIndex = path.lastIndexOf('.');
        if (beginIndex < 0)
            return false;
        final String postfix = path.substring(beginIndex, path.length());
        return !TextUtils.isEmpty(postfix) && postfix.equalsIgnoreCase(".gif");
    }

    // 清除图片磁盘缓存，调用Glide自带方法
    public static void clearCacheDiskSelf(Context context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(context).clearDiskCache();
                    }
                }).start();
            } else {
                Glide.get(context).clearDiskCache();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    // 清除Glide内存缓存
    public static void clearCacheMemory(Context context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) { //只能在主线程执行
                Glide.get(context).clearMemory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
