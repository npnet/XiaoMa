package com.xiaoma.launcher.common;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;

import com.xiaoma.db.DBManager;
import com.xiaoma.db.IDatabase;
import com.xiaoma.login.LoginManager;

import java.util.List;

public class LauncherUtils {

    private static long mLastClickTime;
    private static final long INVALID_CLICK_DURATION = 1000;

    private LauncherUtils() throws Exception {
        throw new Exception();
    }

    public static IDatabase getDBManager() {
        if (LoginManager.getInstance().isUserLogin()) {
            String loginUserId = LoginManager.getInstance().getLoginUserId();
            if (loginUserId == null || TextUtils.isEmpty(loginUserId.trim())) {
                return DBManager.getInstance().getDBManager();//这里可能处出现null的情况
            }
            return DBManager.getInstance().getUserDBManager(loginUserId);
        } else {
            return DBManager.getInstance().getDBManager();
        }
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
