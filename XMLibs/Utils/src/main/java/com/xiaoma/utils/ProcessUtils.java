package com.xiaoma.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import com.xiaoma.utils.reflect.Reflect;

import java.util.List;
import java.util.Objects;

/**
 * @author zs
 * @date 2018/3/13 0013.
 * 进程相关工具类
 */

public class ProcessUtils {
    /**
     * 是否为主进程
     */
    public static boolean isMainProcess(Context context) {
        return context != null
                && Objects.equals(getProcessName(), context.getPackageName());
    }

    /**
     * 获取当前进程名称
     */
    public static String getProcessName() {
        return (String) Reflect.on("android.ddm.DdmHandleAppName")
                .method("getAppName")
                .invoke(null);
    }

    public static boolean isProcessExists(Context context, String processName) {
        if (context == null) {
            return false;
        }
        if (TextUtils.isEmpty(processName)) {
            return false;
        }
        Context appContext = context.getApplicationContext();
        ActivityManager as = (ActivityManager) appContext.getSystemService(Context.ACTIVITY_SERVICE);
        if (as == null) {
            return false;
        }
        List<ActivityManager.RunningAppProcessInfo> allProcess = as.getRunningAppProcesses();
        if (allProcess == null || allProcess.size() == 0) {
            return false;
        }
        for (ActivityManager.RunningAppProcessInfo process : allProcess) {
            if (processName.equals(process.processName)) {
                return true;
            }
        }
        return false;
    }
}
