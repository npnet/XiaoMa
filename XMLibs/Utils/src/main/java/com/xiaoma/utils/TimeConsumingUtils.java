package com.xiaoma.utils;

import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.LruCache;

import com.xiaoma.config.ConfigManager;

/**
 * @author: iSun
 * 耗时测试工具类
 * @date: 2019/7/9 0009
 */
public class TimeConsumingUtils {
    private static final String TAG = TimeConsumingUtils.class.getSimpleName();
    private int maxCache = 512;
    private LruCache<String, Long> methodTime = new LruCache<>(maxCache);
    private static TimeConsumingUtils instance;
    private String end = ".java:";
    public boolean isTestMode = false;

    private TimeConsumingUtils() {
        if (ConfigManager.ApkConfig.isDebug()) {
            isTestMode = true;
        } else {
            isTestMode = false;
        }
    }

    public static TimeConsumingUtils getInstance() {
        if (instance == null) {
            synchronized (TimeConsumingUtils.class) {
                if (instance == null) {
                    instance = new TimeConsumingUtils();
                }
            }
        }
        return instance;
    }


    public void start() {
        try {
            if (isTestMode) {
                StackTraceElement[] temp = Thread.currentThread().getStackTrace();
                StackTraceElement a = (StackTraceElement) temp[3];
                String key = getKey(a);
                methodTime.put(key, SystemClock.uptimeMillis());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start(String... key) {
        try {
            if (isTestMode) {
                StringBuilder keys = new StringBuilder();
                for (String s : key) {
                    keys.append(":" + s);
                }
                StackTraceElement[] temp = Thread.currentThread().getStackTrace();
                StackTraceElement a = (StackTraceElement) temp[3];
                methodTime.put(keys.toString(), SystemClock.uptimeMillis());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void end(String... key) {
        StringBuilder keys = new StringBuilder();
        try {
            if (isTestMode) {
                for (String s : key) {
                    keys.append(":" + s);
                }
                StackTraceElement[] temp = Thread.currentThread().getStackTrace();
                StackTraceElement a = (StackTraceElement) temp[3];
                long l = methodTime.get(keys.toString()).longValue();
                Log.e(TAG, keys.toString() + " time:" + (SystemClock.uptimeMillis() - l));
            }
        } catch (Exception e) {
            Log.e(TAG, " no key:" + keys);
        }
    }

    @NonNull
    private String getKey(StackTraceElement a) {
        return new StringBuilder().append(a.getClassName()).append(end).append(a.getMethodName()).toString();
    }

    public void end() {
        String key = null;
        try {
            if (isTestMode) {
                StackTraceElement[] temp = Thread.currentThread().getStackTrace();
                StackTraceElement a = (StackTraceElement) temp[3];
                key = getKey(a);
                long l = methodTime.get(key).longValue();
                Log.e(TAG, key + " time:" + (SystemClock.uptimeMillis() - l));
            }
        } catch (Exception e) {
            Log.e(TAG, " no key:" + key);
        }
    }
}
