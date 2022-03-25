package com.xiaoma.club.contact.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;

/**
 * Created by LKF on 2019-2-27 0027.
 */
@Entity(primaryKeys = {"uid", "groupId"},
        foreignKeys = @ForeignKey(entity = GroupCardInfo.class,
                parentColumns = "id", childColumns = "groupId",
                onUpdate = ForeignKey.CASCADE,
                deferred = true))
public class ContactGroup {
    private long uid;
    private long groupId;

    public ContactGroup(long uid, long groupId) {
        this.uid = uid;
        this.groupId = groupId;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }
}
