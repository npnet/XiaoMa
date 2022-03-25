package com.xiaoma.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.List;

import static android.content.pm.PackageManager.MATCH_DEFAULT_ONLY;

/**
 * Created by youthyj on 2018/9/10.
 * App启动工具
 */
public class LaunchUtils {
    public static final String PATH_LAUNCH = "/index";

    public static final String EXTRA_BUNDLE = "bundle";

    private LaunchUtils() throws Exception {
        throw new Exception();
    }


    // 使用PackageName启动App
    public static boolean launchApp(Context context, String packageName) {
        if (context == null || TextUtils.isEmpty(packageName) || packageName.trim().isEmpty()) {
            return false;
        }
        return launchApp(context, packageName, null, null, false);
    }

    public static boolean launchApp(Context context, String packageName, Bundle bundle) {
        if (context == null || TextUtils.isEmpty(packageName) || packageName.trim().isEmpty()) {
            return false;
        }
        return launchApp(context, packageName, null, bundle, false);
    }

    public static boolean launchApp(Context context, String packageName, String className) {
        if (context == null || TextUtils.isEmpty(packageName) || packageName.trim().isEmpty()) {
            return false;
        }
        return launchApp(context, packageName, className, null, false);
    }

    public static boolean launchApp(Context context, String packageName, String className, Bundle bundle) {
        if (context == null || TextUtils.isEmpty(packageName) || packageName.trim().isEmpty()) {
            return false;
        }
        return launchApp(context, packageName, className, bundle, false);
    }

    public static boolean launchApp(Context context, String packageName, String className, Bundle bundle, boolean newTask) {
        if (context == null || TextUtils.isEmpty(packageName) || packageName.trim().isEmpty()) {
            // 关键参数缺失
            return false;
        }
        if (!isAppInstalled(context, packageName)) {
            // 目标App未安装
            return false;
        }
        Intent intent;
        if (TextUtils.isEmpty(className) || className.trim().isEmpty()) {
            PackageManager pm = context.getPackageManager();
            intent = pm.getLaunchIntentForPackage(packageName);
            if (intent == null) {
                // 无法获取有效Intent
                return false;
            }
        } else {
            intent = new Intent();
            ComponentName componentName = new ComponentName(packageName, className);
            intent.setComponent(componentName);
        }
        if (newTask) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        try {
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            // 跳转时发生异常
            return false;
        }
    }


    public static boolean launchAppOnlyNewTask(Context context, String packageName, String className, Bundle bundle) {
        if (context == null || TextUtils.isEmpty(packageName) || packageName.trim().isEmpty()) {
            // 关键参数缺失
            return false;
        }
        if (!isAppInstalled(context, packageName)) {
            // 目标App未安装
            return false;
        }
        Intent intent;
        if (TextUtils.isEmpty(className) || className.trim().isEmpty()) {
            PackageManager pm = context.getPackageManager();
            intent = pm.getLaunchIntentForPackage(packageName);
            if (intent == null) {
                // 无法获取有效Intent
                return false;
            }
        } else {
            intent = new Intent();
            ComponentName componentName = new ComponentName(packageName, className);
            intent.setComponent(componentName);
        }

        try {
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            // 跳转时发生异常
            return false;
        }
    }


    //携带数据打开第三方app
    public static boolean launchAppWithData(Context context, String packageName, String className, Bundle bundle) {
        if (context == null || TextUtils.isEmpty(packageName) || packageName.trim().isEmpty()) {
            // 关键参数缺失
            return false;
        }
        if (!isAppInstalled(context, packageName)) {
            // 目标App未安装
            return false;
        }
        Intent intent;
        if (TextUtils.isEmpty(className) || className.trim().isEmpty()) {
            PackageManager pm = context.getPackageManager();
            intent = pm.getLaunchIntentForPackage(packageName);
            if (intent == null) {
                // 无法获取有效Intent
                return false;
            }
        } else {
            intent = new Intent();
            ComponentName componentName = new ComponentName(packageName, className);
            intent.setComponent(componentName);

            if (bundle != null) {
                intent.putExtra(EXTRA_BUNDLE, bundle);
            }
        }
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            // 跳转时发生异常
            return false;
        }
    }

    // 使用指定uri启动App
    public static boolean launchAppByUri(Context context, String uri) {
        if (context == null || TextUtils.isEmpty(uri) || uri.trim().isEmpty()) {
            return false;
        }
        return launchAppByUri(context, Uri.parse(uri), true);
    }

    public static boolean launchAppByUri(Context context, String uri, boolean newTask) {
        if (context == null || TextUtils.isEmpty(uri) || uri.trim().isEmpty()) {
            return false;
        }
        return launchAppByUri(context, Uri.parse(uri), newTask);
    }

    public static boolean launchAppByUri(Context context, Uri uri) {
        if (context == null || uri == null) {
            return false;
        }
        return launchAppByUri(context, uri, true);
    }

    public static boolean launchAppByUri(Context context, Uri uri, boolean newTask) {
        if (context == null || uri == null) {
            return false;
        }
        String host = uri.getHost();
        if (TextUtils.isEmpty(host)) {
            return false;
        }
        String path = uri.getPath();
        if (TextUtils.isEmpty(path) || PATH_LAUNCH.equalsIgnoreCase(path) || "/".equals(path.trim())) {
            //path留空也当做是启动App
            return launchApp(context, host);
        }

        Intent intent = new Intent();
        if (newTask) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setData(uri);
        if (!isValidIntent(context, intent)) {
            return false;
        }
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 其他启动App的方式
    public static boolean launchAppByPackageInfo(Context context, String packageName) {
        if (context == null || TextUtils.isEmpty(packageName)) {
            return false;
        }
        return launchAppByPackageInfo(context, packageName, null);
    }

    public static boolean launchAppByPackageInfo(Context context, String packageName, Bundle bundle) {
        if (context == null || TextUtils.isEmpty(packageName) || packageName.trim().isEmpty()) {
            return false;
        }
        PackageInfo packageinfo;
        try {
            packageinfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        if (packageinfo == null) {
            return false;
        }
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setPackage(packageinfo.packageName);
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> infoList = pm.queryIntentActivities(intent, 0);
        ResolveInfo resolveinfo = infoList.iterator().next();
        return launchAppByResolveInfo(context, resolveinfo, bundle);
    }

    public static boolean launchAppByResolveInfo(Context context, ResolveInfo resolveinfo) {
        if (context == null || resolveinfo == null) {
            return false;
        }
        return launchAppByResolveInfo(context, resolveinfo, null);
    }

    public static boolean launchAppByResolveInfo(Context context, ResolveInfo resolveinfo, Bundle bundle) {
        if (context == null || resolveinfo == null) {
            return false;
        }
        Intent intent = new Intent();
        if (bundle == null || bundle.isEmpty()) {
            intent.setAction(Intent.ACTION_MAIN);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        } else {
            intent.setAction(Intent.ACTION_DEFAULT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        String packageName = resolveinfo.activityInfo.packageName;
        String className = resolveinfo.activityInfo.name;
        ComponentName cn = new ComponentName(packageName, className);
        intent.setComponent(cn);
        context.startActivity(intent);
        return true;
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

    private static boolean isValidIntent(Context context, Intent intent) {
        if (context == null || intent == null) {
            return false;
        }
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(intent, MATCH_DEFAULT_ONLY);
        return list != null && !list.isEmpty();
    }

    public static boolean openApp(Context context, String packageName) {
        if (checkPackInfo(context, packageName)) {
            return openPackage(context, packageName);
        } else {
            return false;
        }
    }

    private static boolean checkPackInfo(Context context, String packageName) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo != null;
    }

    private static Intent getAppOpenIntentByPackageName(Context context, String packageName) {
        String mainAct = null;
        PackageManager pkgMag = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);

        List<ResolveInfo> list = pkgMag.queryIntentActivities(intent, PackageManager.GET_ACTIVITIES);
        for (int i = 0; i < list.size(); i++) {
            ResolveInfo info = list.get(i);
            if (info.activityInfo.packageName.equals(packageName)) {
                mainAct = info.activityInfo.name;
//                break;
            }
        }
        if (TextUtils.isEmpty(mainAct)) {
            return null;
        }
        intent.setComponent(new ComponentName(packageName, mainAct));
        return intent;
    }

    private static Context getPackageContext(Context context, String packageName) {
        Context pkgContext = null;
        if (context.getPackageName().equals(packageName)) {
            pkgContext = context;
        } else {
            // 创建第三方应用的上下文环境
            try {
                pkgContext = context.createPackageContext(packageName, Context.CONTEXT_IGNORE_SECURITY | Context.CONTEXT_INCLUDE_CODE);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return pkgContext;
    }

    private static boolean openPackage(Context context, String packageName) {
        Context pkgContext = getPackageContext(context, packageName);
        Intent intent = getAppOpenIntentByPackageName(context, packageName);
        if (pkgContext != null && intent != null) {
            pkgContext.startActivity(intent);
            return true;
        }
        return false;
    }
}

