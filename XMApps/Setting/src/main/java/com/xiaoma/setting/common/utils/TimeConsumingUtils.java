package com.xiaoma.setting.common.utils;

import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;

import com.xiaoma.config.ConfigManager;

import java.util.HashMap;

/**
 * @author: iSun
 * 测试
 * @date: 2019/7/9 0009
 */
public class TimeConsumingUtils {
    private static final String TAG = TimeConsumingUtils.class.getSimpleName();
    private HashMap<String, Long> methodTime = new HashMap<>();
    private int maxCache = 512;
    private static TimeConsumingUtils instance;
    private String end = ".java:";
    public boolean isPrintTime = false;

    private TimeConsumingUtils() {
        if (ConfigManager.ApkConfig.isDebug()) {
            isPrintTime = true;
        } else {
            isPrintTime = false;
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
        if (isPrintTime) {
            StackTraceElement[] temp = Thread.currentThread().getStackTrace();
            StackTraceElement a = (StackTraceElement) temp[3];
            String key = getKey(a);
            methodTime.put(key, SystemClock.uptimeMillis());
        }
    }

    public void start(String key) {
        if (isPrintTime) {
            StackTraceElement[] temp = Thread.currentThread().getStackTrace();
            StackTraceElement a = (StackTraceElement) temp[3];
            methodTime.put(key, SystemClock.uptimeMillis());
        }
    }

    public void end(String key) {
        if (isPrintTime) {
            StackTraceElement[] temp = Thread.currentThread().getStackTrace();
            StackTraceElement a = (StackTraceElement) temp[3];
            long l = methodTime.get(key).longValue();
            Log.e(TAG, key + " time:" + (SystemClock.uptimeMillis() - l));
            if (methodTime.size() > maxCache) {
                methodTime.clear();
            }
        }
    }

    @NonNull
    private String getKey(StackTraceElement a) {
        return new StringBuilder().append(a.getClassName()).append(end).append(a.getMethodName()).toString();
    }

    public void end() {
        if (isPrintTime) {
            StackTraceElement[] temp = Thread.currentThread().getStackTrace();
            StackTraceElement a = (StackTraceElement) temp[3];
            String key = getKey(a);
            long l = methodTime.get(key).longValue();
            Log.e(TAG, key + " time:" + (SystemClock.uptimeMillis() - l));
            if (methodTime.size() > maxCache) {
                methodTime.clear();
            }
        }
    }
}
