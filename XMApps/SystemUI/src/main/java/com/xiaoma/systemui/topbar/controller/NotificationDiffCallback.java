package com.xiaoma.systemui.topbar.controller;

import android.service.notification.StatusBarNotification;
import android.support.v7.util.DiffUtil;

import com.xiaoma.systemui.common.util.LogUtil;

import java.util.List;
import java.util.Objects;

/**
 * Created by LKF on 2018/11/21 0021.
 */
class NotificationDiffCallback extends DiffUtil.Callback {
    private List<StatusBarNotification> oldNotifications;
    private List<StatusBarNotification> newNotifications;

    public NotificationDiffCallback(List<StatusBarNotification> oldNotifications,
                                    List<StatusBarNotification> newNotifications) {
        this.oldNotifications = oldNotifications;
        this.newNotifications = newNotifications;
    }

    @Override
    public int getOldListSize() {
        final int size = oldNotifications != null ? oldNotifications.size() : 0;
        logI("getOldListSize() size: %s", size);
        return size;
    }

    @Override
    public int getNewListSize() {
        final int size = newNotifications != null ? newNotifications.size() : 0;
        logI("getNewListSize() size: %s", size);
        return size;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        logI("areItemsTheSame( oldItemPosition: %s, newItemPosition: %s )", oldItemPosition, newItemPosition);
        final StatusBarNotification oldSbn = oldNotifications.get(oldItemPosition);
        final StatusBarNotification newSbn = newNotifications.get(newItemPosition);
        return Objects.equals(oldSbn.getId(), newSbn.getId())
                && Objects.equals(oldSbn.getKey(), newSbn.getKey())
                && Objects.equals(oldSbn.isGroup(), newSbn.isGroup())
                && Objects.equals(oldSbn.isAppGroup(), newSbn.isAppGroup())
                ;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        logI("areContentsTheSame( oldItemPosition: %s, newItemPosition: %s )", oldItemPosition, newItemPosition);
        return false;

        /*final StatusBarNotification oldSbn = oldNotifications.get(oldItemPosition);
        final StatusBarNotification newSbn = newNotifications.get(newItemPosition);
        if (!Objects.equals(oldCollapseApp.contains(oldSbn.getPackageName()), newCollapseApp.contains(newSbn.getPackageName())))
            return false;
        if (Objects.equals(oldSbn, newSbn))
            return true;
        final Notification oldN = oldSbn.getNotification();
        final Notification newN = newSbn.getNotification();
        return Objects.equals(oldN, newN);*/
    }

    private void logI(String format, Object... args) {
        LogUtil.logI(getClass().getSimpleName(), format, args);
    }
}