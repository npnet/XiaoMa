package com.xiaoma.club.discovery.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import com.xiaoma.club.contact.model.GroupCardInfo;

/**
 * Created by LKF on 2019-2-26 0026.
 * 记录发现的群组id
 */
@Entity(foreignKeys = @ForeignKey(entity = GroupCardInfo.class,
        parentColumns = "id", childColumns = "discoverGroupId",
        onUpdate = ForeignKey.CASCADE,
        deferred = true))
public class DiscoverGroup {
    @PrimaryKey(autoGenerate = true)
    private long innerId;

    private long discoverGroupId;

    public long getInnerId() {
        return innerId;
    }

    public void setInnerId(long innerId) {
        this.innerId = innerId;
    }

    public long getDiscoverGroupId() {
        return discoverGroupId;
    }

    public void setDiscoverGroupId(long discoverGroupId) {
        this.discoverGroupId = discoverGroupId;
    }
}