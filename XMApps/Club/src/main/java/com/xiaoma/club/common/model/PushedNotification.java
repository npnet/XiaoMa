package com.xiaoma.club.common.model;

import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

/**
 * Created by LKF on 2019-4-3 0003.
 * 已推送过的内容的记录
 */
@Entity(primaryKeys = {"userHxId", "pushId"})
public class PushedNotification {
    @NonNull
    private String userHxId;
    @NonNull
    private String pushId;

    private long pushTime = System.currentTimeMillis();

    public PushedNotification(@NonNull String userHxId, @NonNull String pushId) {
        this.userHxId = userHxId;
        this.pushId = pushId;
    }

    @NonNull
    public String getUserHxId() {
        return userHxId;
    }

    public void setUserHxId(@NonNull String userHxId) {
        this.userHxId = userHxId;
    }

    @NonNull
    public String getPushId() {
        return pushId;
    }

    public void setPushId(@NonNull String pushId) {
        this.pushId = pushId;
    }

    public long getPushTime() {
        return pushTime;
    }

    public void setPushTime(long pushTime) {
        this.pushTime = pushTime;
    }
}
