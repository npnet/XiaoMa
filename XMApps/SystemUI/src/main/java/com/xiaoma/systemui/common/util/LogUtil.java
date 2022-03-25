package com.xiaoma.systemui.common.util;

import android.app.Notification;
import android.service.notification.StatusBarNotification;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Arrays;
import java.util.Collection;

public class LogUtil {
    private static boolean sShowLog = true;
    private static final String TAG = "XMSystemUI";

    public static void setShowLog(boolean showLog) {
        sShowLog = showLog;
    }

    public static void logI(String format, Object... args) {
        logI(TAG, format, args);
    }

    public static void logI(String tag, String format, Object... args) {
        if (!sShowLog)
            return;
        Log.i(tag, String.format(format, args));
    }

    public static void logW(String format, Object... args) {
        logW(TAG, format, args);
    }

    public static void logW(String tag, String format, Object... args) {
        if (!sShowLog)
            return;
        Log.w(tag, String.format(format, args));
    }

    public static void logE(String format, Object... args) {
        logE(TAG, format, args);
    }

    public static void logE(String tag, String format, Object... args) {
        if (!sShowLog)
            return;
        Log.e(tag, String.format(format, args));
    }

    public static void dump(String tag, Collection<StatusBarNotification> notifications) {
        if (!sShowLog)
            return;
        final StringBuilder log = new StringBuilder();
        log.append("\n=================================== begin ===================================\n");
        if (notifications != null && notifications.size() > 0) {
            for (final StatusBarNotification notification : notifications) {
                log.append(String.format("%s\n\n", getNotificationDump(notification)));
            }

        } else {
            log.append(notifications);
        }
        log.append("=================================== end ===================================\n");
        LogUtil.logE(tag, "%s", log.toString());
    }

    public static void dump(String tag, StatusBarNotification[] notifications) {
        if (!sShowLog)
            return;
        final StringBuilder log = new StringBuilder();
        log.append("\n=================================== begin ===================================\n");
        if (notifications != null && notifications.length > 0) {
            for (final StatusBarNotification notification : notifications) {
                log.append(String.format("%s\n\n", getNotificationDump(notification)));
            }

        } else {
            log.append(Arrays.toString(notifications));
        }
        log.append("=================================== end ===================================\n");
        LogUtil.logE(tag, "%s", log.toString());
    }

    public static String getNotificationDump(@Nullable StatusBarNotification sbn) {
        if (sbn == null) {
            return "null";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("{ ");
        sb.append(String.format("isAppGroup: %s, ", sbn.isAppGroup()));
        sb.append(String.format("key: %s, ", sbn.getKey()));
        sb.append(String.format("postTime: %s", sbn.getPostTime()));
        final Notification n = sbn.getNotification();
        if (n != null) {
            sb.append(", ");
            sb.append(String.format("extras: %s, ", n.extras));
            sb.append(String.format("contentView: %s, ", n.contentView));
            sb.append(String.format("bigContentView: %s, ", n.bigContentView));
            sb.append(String.format("headsUpContentView: %s, ", n.headsUpContentView));
            sb.append(String.format("smallIcon: %s, ", n.getSmallIcon()));
            sb.append(String.format("largeIcon: %s, ", n.getLargeIcon()));
            sb.append(String.format("when: %s", n.when));
        } else {
            sb.append(", notification: null");
        }
        sb.append(" }");
        return sb.toString();
    }
}
