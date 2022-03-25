package com.xiaoma.xkan.common.util;

import android.content.Context;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.xiaoma.thread.ThreadDispatcher;

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
    public static void clearCacheDiskSelf(final Context context) {
        try {
            ThreadDispatcher.getDispatcher().post(new Runnable() {
                @Override
                public void run() {
                    //只能在子线程执行
                    Glide.get(context.getApplicationContext()).clearDiskCache();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    // 清除Glide内存缓存
    public static void clearCacheMemory(final Context context) {
        try {
            //只能在主线程执行
            ThreadDispatcher.getDispatcher().postOnMain(new Runnable() {
                @Override
                public void run() {
                    Glide.get(context.getApplicationContext()).clearMemory();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
