package com.xiaoma.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.xiaoma.utils.apptool.AndroidAppProcess;
import com.xiaoma.utils.apptool.ProcessManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.content.Context.ACTIVITY_SERVICE;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static java.lang.String.format;

/**
 * Created by LKF on 2017/6/21 0021.
 * 获取App信息工具
 */
public class AppUtils {
    public static boolean isAppForeground() {
        ActivityManager.RunningAppProcessInfo pi = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(pi);
        int importance = pi.importance;
        Log.i("AppUtils", format(Locale.CHINESE, "importance = %d", importance));
        return IMPORTANCE_FOREGROUND == importance;
    }

    public static boolean moveTaskToFront(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> rts = am.getRunningTasks(10);
        if (rts != null && rts.size() > 0) {
            for (ActivityManager.RunningTaskInfo rt : rts) {
                if (packageName.equals(rt.topActivity.getPackageName())) {
                    am.moveTaskToFront(rt.id, ActivityManager.MOVE_TASK_NO_USER_ACTION);
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            return context != null
                    && !TextUtils.isEmpty(packageName)
                    && context.getPackageManager().getApplicationInfo(packageName, 0) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public static void installApk(@NonNull Context context, @NonNull File file) {
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse("file://" + file.toString());
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public static boolean isExistActivity(Class<?> cls, Context context) {
        Intent intent = new Intent(context, cls);
        ComponentName cmpName = intent.resolveActivity(context.getPackageManager());
        boolean flag = false;
        if (cmpName != null) { // 说明系统中存在这个activity
            ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfoList = am.getRunningTasks(10);
            for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
                if (taskInfo.baseActivity.equals(cmpName)) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    public static boolean isExistActivity(Context context, String pkg, String fullClassName) {
        Intent intent = new Intent();
        intent.setClassName(pkg, fullClassName);
        ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo != null;
    }

    public static boolean isExistActivity(Context context, Uri uri) {
        Intent intent = new Intent();
        intent.setData(uri);
        ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo != null;
    }

    public static List<String> getAppInForeground(Context context) {
        List<String> runningAppList;
        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            runningAppList = getRunningAppNew(context);
        } else {
            runningAppList = getRunningAppOld(context, 1);
        }
        return runningAppList;
    }

    private static List<String> getRunningAppNew(Context context) {
        List<String> packNameList = new ArrayList<>();
        try {
            List<AndroidAppProcess> processes = ProcessManager.getRunningForegroundApps(context);
            for (AndroidAppProcess appProcess : processes) {
                String packageName = appProcess.getPackageName();
                packNameList.add(packageName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return packNameList;
    }

    private static List<String> getRunningAppOld(Context context, int num) {
        List<String> packNameList = new ArrayList<>();
        if (context == null || num <= 0)
            return packNameList;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(num);
        if (!runningTasks.isEmpty()) {
            for (ActivityManager.RunningTaskInfo runningTask : runningTasks) {
                if (runningTask.topActivity != null && runningTask.topActivity.getPackageName() != null) {
                    packNameList.add(runningTask.topActivity.getPackageName());
                }
            }
        }
        return packNameList;
    }

    @Nullable
    public static String getAppName(Context context) {
        return getAppName(context, null);
    }

    /**
     * 获取APP的名称
     *
     * @param packageName App包名,如果传null或者空串,则返回当前Context对应的App名
     * @return 返回App的名称
     */
    @Nullable
    public static String getAppName(Context context, String packageName) {
        if (context == null)
            return null;
        if (TextUtils.isEmpty(packageName)) {
            packageName = context.getPackageName();
        }
        String appName = null;
        try {
            final PackageManager pm = context.getPackageManager();
            final ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
            appName = String.valueOf(pm.getApplicationLabel(appInfo));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appName;
    }

    /**
     * 判断服务是否开启
     *
     * @return
     */
    public static boolean isServiceRunning(Context context, String ServiceName) {
        if (("").equals(ServiceName) || ServiceName == null)
            return false;
        ActivityManager myManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
                .getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString()
                    .equals(ServiceName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断某个界面是否在前台
     *
     * @param context
     * @param className 某个界面名称(带包名)
     */
    public static boolean isActivityForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName())) {
                return true;
            }
        }

        return false;
    }

}
