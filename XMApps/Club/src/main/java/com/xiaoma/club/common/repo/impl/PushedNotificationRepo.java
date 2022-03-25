package com.xiaoma.club.common.repo.impl;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.text.TextUtils;

import com.hyphenate.chat.EMClient;
import com.xiaoma.club.common.model.PushedNotification;
import com.xiaoma.club.common.repo.ModelRepo;
import com.xiaoma.club.common.util.LogUtil;

import java.util.Calendar;

/**
 * Created by LKF on 2019-4-3 0003.
 */
@Dao
public abstract class PushedNotificationRepo extends ModelRepo<PushedNotification> {
    public static final int KEEP_RECORD_BEFORE_DAYS = 30;// 最多保留多少天的记录,避免记录太多

    @Query("SELECT * FROM PushedNotification WHERE userHxId=:userHxId AND pushId=:pushId")
    protected abstract PushedNotification query(String userHxId, String pushId);

    /**
     * 移除某个时间点之前的记录
     *
     * @param when 时间点
     */
    @Query("DELETE FROM PushedNotification WHERE pushTime<:when")
    protected abstract int deleteWhenBefore(long when);

    public boolean isPushed(String pushId) {
        if (TextUtils.isEmpty(pushId))
            return false;
        String userHxId = EMClient.getInstance().getCurrentUser();
        return query(userHxId, pushId) != null;
    }

    /**
     * 记录已推送的通知
     *
     * @param pushId 推送内容的唯一标识
     */
    public void addPushed(String pushId) {
        if (TextUtils.isEmpty(pushId))
            return;
        // 移除N天之前的记录,避免推送太多,导致表记录累积过多
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        c.add(Calendar.DAY_OF_MONTH, -KEEP_RECORD_BEFORE_DAYS);
        int delCount = deleteWhenBefore(c.getTimeInMillis());

        LogUtil.logI(TAG, "addPushed( pushId: %s ){ delCount: %s }", pushId, delCount);
        String userHxId = EMClient.getInstance().getCurrentUser();
        insert(new PushedNotification(userHxId, pushId));
    }

    /**
     * 推送已推送的通知记录
     *
     * @param pushId 推送内容的唯一标识
     */
    public void removePushed(String pushId) {
        if (TextUtils.isEmpty(pushId))
            return;
        String userHxId = EMClient.getInstance().getCurrentUser();
        delete(new PushedNotification(userHxId, pushId));
        LogUtil.logI(TAG, "removePushed( pushId: %s )", pushId);
    }

    @Override
    protected String getTableName() {
        return PushedNotification.class.getSimpleName();
    }
}
