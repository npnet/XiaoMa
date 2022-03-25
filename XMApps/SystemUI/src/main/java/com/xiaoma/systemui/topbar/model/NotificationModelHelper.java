package com.xiaoma.systemui.topbar.model;

import android.annotation.NonNull;
import android.app.Notification;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.ArraySet;

import com.xiaoma.systemui.common.util.DateTimeUtil;
import com.xiaoma.systemui.common.util.LogUtil;
import com.xiaoma.systemui.common.util.PkgUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by LKF on 2019-3-8 0008.
 * 通知数据源处理类,实现排序分组聚合逻辑,以及控制折叠状态等,并提供一些通知Model常用的工具方法
 */
public class NotificationModelHelper {
    private final String TAG = getClass().getSimpleName();
    private static final int EN_GROUP_MIN_COUNT = 2;// 需要分组聚合的最少通知数,达到此条数则分组
    // 屏蔽通知显示的App
    private static final Set<String> BLACK_LIST_APP = new ArraySet<>();

    static {
        BLACK_LIST_APP.add("com.xiaoma.assistant");
    }

    private final List<StatusBarNotification> mDisplayingNotifications = new ArrayList<>();
    private final Map<GroupKey, Set<StatusBarNotification>> mMapByGroupKey = new TreeMap<>(new Comparator<GroupKey>() {
        @Override
        public int compare(GroupKey o1, GroupKey o2) {
            if (Objects.equals(o1.packageName, o2.packageName)
                    && DateTimeUtil.isSameDay(o1.date, o2.date)) {
                return 0;
            }
            return -Long.compare(o1.date.getTime(), o2.date.getTime());
        }
    });
    private final Set<GroupKey> mCollapseGroup = new HashSet<>();

    public void setDataSource(StatusBarNotification[] notificationArr) {
        mDisplayingNotifications.clear();
        mMapByGroupKey.clear();
        // 如果当前没有任何通知,则清除分组的折叠状态
        if (notificationArr == null || notificationArr.length <= 0) {
            mCollapseGroup.clear();
            return;
        }
        final Comparator<StatusBarNotification> cmp = new Comparator<StatusBarNotification>() {
            @Override
            public int compare(StatusBarNotification o1, StatusBarNotification o2) {
                return -Long.compare(getNotificationTime(o1), getNotificationTime(o2));
            }
        };
        // 按日期+包名 分组
        for (final StatusBarNotification sbn : notificationArr) {
            if (!canNotificationShow(sbn))
                continue;
            final GroupKey groupKey = getGroupKey(sbn);
            Set<StatusBarNotification> set = mMapByGroupKey.get(groupKey);
            if (set == null) {
                set = new TreeSet<>(cmp);
                mMapByGroupKey.put(groupKey, set);
            }
            set.add(sbn);
        }
        // 重建当前通知列表
        rebuildNotifications();
    }

    private void rebuildNotifications() {
        final List<StatusBarNotification> resultNotifications = new ArrayList<>();
        final Set<Map.Entry<GroupKey, Set<StatusBarNotification>>> entries = mMapByGroupKey.entrySet();
        for (final Map.Entry<GroupKey, Set<StatusBarNotification>> entry : entries) {
            final GroupKey groupKey = entry.getKey();
            final Set<StatusBarNotification> set = entry.getValue();
            if (isGroupCollapse(groupKey)) {
                // 如果通知已折叠,只添加第一个通知
                resultNotifications.add(set.iterator().next());
            } else {
                // 未折叠状态添加所有通知
                resultNotifications.addAll(set);
            }
        }
        LogUtil.dump(TAG, resultNotifications);
        mDisplayingNotifications.clear();
        mDisplayingNotifications.addAll(resultNotifications);
    }

    /**
     * 判断当前通知所在的分组是否为折叠状态
     */
    public boolean isGroupCollapse(StatusBarNotification sbn) {
        if (!hasAppGroup(sbn))
            return false;
        return isGroupCollapse(getGroupKey(sbn));
    }

    private boolean isGroupCollapse(GroupKey groupKey) {
        if (!hasAppGroup(groupKey))
            return false;
        return groupKey != null && mCollapseGroup.contains(groupKey);
    }

    /**
     * 是否有分组
     */
    public boolean hasAppGroup(StatusBarNotification sbn) {
        return hasAppGroup(getGroupKey(sbn));
    }

    private boolean hasAppGroup(GroupKey groupKey) {
        Set<StatusBarNotification> set = mMapByGroupKey.get(groupKey);
        return set != null && set.size() >= EN_GROUP_MIN_COUNT;
    }

    public void setGroupCollapse(StatusBarNotification sbn, boolean isCollapse) {
        final GroupKey groupKey = getGroupKey(sbn);
        if (isCollapse) {
            mCollapseGroup.add(groupKey);
        } else {
            mCollapseGroup.remove(groupKey);
        }
        rebuildNotifications();
    }

    @NonNull
    public List<StatusBarNotification> getDisplayingNotifications() {
        return mDisplayingNotifications;
    }

    /**
     * 获取当前通知所在分组的所有通知
     */
    public List<StatusBarNotification> getGroupNotifications(StatusBarNotification sbn) {
        List<StatusBarNotification> list = new ArrayList<>();
        final Set<StatusBarNotification> set = mMapByGroupKey.get(getGroupKey(sbn));
        if (set != null) {
            list.addAll(set);
        }
        return list;
    }

    public boolean isFirstGroupItem(StatusBarNotification sbn) {
        if (!hasAppGroup(sbn))
            return false;
        final List<StatusBarNotification> groupMates = getGroupNotifications(sbn);
        return groupMates != null && !groupMates.isEmpty() && groupMates.get(0) == sbn;
    }

    public static long getNotificationTime(StatusBarNotification sbn) {
        if (sbn == null)
            return -1;
        final Notification n = sbn.getNotification();
        if (n != null && n.when > 0) {
            return n.when;
        }
        return sbn.getPostTime();
    }

    private static GroupKey getGroupKey(StatusBarNotification sbn) {
        if (sbn == null)
            return null;
        return new GroupKey(getNotificationTime(sbn), sbn.getPackageName());
    }

    public static boolean canNotificationShow(StatusBarNotification sbn) {
        return isValidNotification(sbn) && !PkgUtil.isSystemPackage(sbn.getPackageName())
                && !BLACK_LIST_APP.contains(sbn.getPackageName())
                && !isKeepAliveNotification(sbn);
    }

    public static boolean hasCustomContent(Notification n) {
        if (n == null) {
            return false;
        }
        final Bundle extras = n.extras;
        return (extras != null && (!TextUtils.isEmpty(extras.getString(NotificationCompat.EXTRA_TITLE))
                || !TextUtils.isEmpty(extras.getString(NotificationCompat.EXTRA_TEXT))
                || !TextUtils.isEmpty(extras.getString(NotificationCompat.EXTRA_SUB_TEXT))))
                || n.getLargeIcon() != null;
    }

    private static boolean isValidNotification(StatusBarNotification sbn) {
        // 需要过滤Android原生的通知分组
        if (sbn == null || sbn.isAppGroup())
            return false;
        final Notification n = sbn.getNotification();
        if (n == null)
            return false;
        if (n.bigContentView != null || n.contentView != null)
            return true;
        return hasCustomContent(n);
    }

    // 只有小icon,没有其他内容的通知为APP的保活通知,需要隐藏
    private static boolean isKeepAliveNotification(StatusBarNotification sbn) {
        if (sbn == null)
            return false;
        if (!sbn.getPackageName().startsWith("com.xiaoma"))
            return false;
        Notification n = sbn.getNotification();
        return n != null && n.getSmallIcon() != null && !hasCustomContent(n);
    }

    private static class GroupKey {
        Date date;
        String packageName;

        GroupKey(long time, String packageName) {
            this(new Date(time), packageName);
        }

        GroupKey(Date date, String packageName) {
            this.date = date;
            this.packageName = packageName;
        }

        @Override
        public int hashCode() {
            return (packageName + new SimpleDateFormat("_yyyy-MM-dd", Locale.CHINA).format(date))
                    .hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof GroupKey))
                return false;
            final GroupKey other = (GroupKey) obj;
            return Objects.equals(packageName, other.packageName)
                    && DateTimeUtil.isSameDay(date, other.date);
        }
    }
}
